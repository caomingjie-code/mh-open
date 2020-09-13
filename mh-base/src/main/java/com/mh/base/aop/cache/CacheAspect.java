package com.mh.base.aop.cache;

import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.util.*;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.mh.base.annotation.conditional.ConditionalOnPropertyMustExists;
import com.mh.base.utils.model.ModelUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.mh.base.exception.CacheException;
import com.mh.base.annotation.cache.EnableCache;
import com.mh.base.annotation.cache.UnableCache;
import com.mh.base.definition.CacheData;
import com.mh.base.common.definition.ClassDefinition;
import com.mh.base.common.definition.MethodDefinition;
import com.mh.base.definition.UniqueKey;
import com.mh.base.common.clazz.ClassUtils;
import com.mh.base.common.concurrent.ConcurrentHashSet;
import com.mh.base.common.log.LogUtils;
import com.mh.base.utils.redis.JedisUtils;
import jdk.internal.org.objectweb.asm.tree.AnnotationNode;

/**
 * @description 
        缓存切面,缓存策略： uniquekey原理  ： Base64（sessionId + key + params）
        	使用注解@EnableCache 开启缓存  key全局唯一
        	使用注解@UnableCache 清楚缓存  key全局唯一

   *      缓存uniquekey  全局唯一, 在底层逻辑会根据  sessionId + key 进行关联多个 uniquekey 
   *      在Redis中的key 为 uniquekey，当逻辑需要删除时 会根据 sessionId + key 查询关联的所有  uniquekey 
   *      然后全部删除。（数据默认缓存为1小时存活时间）
 *  @NOTE 在初始化时会检验@EnableCache和@UnableCache所属key的唯一性 ,
 *      	并且 只允许在 controller ，service ，repository 中使用。
 * @author mingjie      
 * @return
 */
@Component
@ConditionalOnProperty(prefix = "mh.cache",value = "cache",havingValue = "true")
@Aspect
public class CacheAspect implements InitializingBean{

	// sessionId + key 进行关联多个 uniquekey 用于删除缓存时使用
	private static final SoftHashMap<String, SoftReference<ConcurrentHashSet<String>>> SESSION_KEY_UNIQUE_KEY = new SoftHashMap<String, SoftReference<ConcurrentHashSet<String>>>();
	private static Logger logger = LoggerFactory.getLogger(ModelUtils.class);// log4j记录日志
	private static final String  ENABLE_CACHE = "com.mh.base.cache.annotation.EnableCache";
	private static final String  UNABLE_CACHE = "com.mh.base.cache.annotation.UnableCache";
	    
	/**
	 * 启用缓存切点
	 */
	@Pointcut("@annotation(com.mh.base.annotation.cache.EnableCache)")
	public void pointCutEnableCache() {
	}

	/**
	 * 弃用缓存切点
	 */
	@Pointcut("@annotation(com.mh.base.annotation.cache.UnableCache)")
	public void pointCutUnableCache() {
	}

	/**
	 * 在启用缓存切点 前执行
	 * 
	 * @throws Throwable
	 */
	@Around("pointCutEnableCache()")
	public Object doAroundEnableCache(final ProceedingJoinPoint joinPoint) throws Throwable {
		Signature signature = joinPoint.getSignature();
		Object[] args = joinPoint.getArgs();//方法参数值
		EnableCache declaredAnnotation = baseChecked(signature);//基础信息校验
		String uniqueKeyStr = getUniqueKey(declaredAnnotation,args); //redis key
		String sessionIdKey = getSessionIdKey(declaredAnnotation);//获取sessionIDKey
		/**从redis中查询数据**/
		Object cacheData = queryCacheData(uniqueKeyStr);
		if(cacheData != null) {
			return cacheData;
		}

		Object proceed = joinPoint.proceed();
		//做缓存
		saveCacheData(uniqueKeyStr, sessionIdKey, proceed);
		LogUtils.printLog("CacheAspect_ENABLE_CACHE CACHE_ID:"+uniqueKeyStr+"  SESSIONIDKEY:"+sessionIdKey);
		return proceed;
	}

	/**
	 * 保存缓存数据
	 * @param uniqueKeyStr
	 * @param sessionIdKey
	 * @param proceed
	 */
	private void saveCacheData(String uniqueKeyStr, String sessionIdKey, Object proceed) {
		try {
			if(proceed!=null) {
				CacheData cacheData_ = new CacheData(proceed);
				JedisUtils.putData(uniqueKeyStr, cacheData_, 60*60); //做缓存，时长为1小时
			    //做关联
				if(SESSION_KEY_UNIQUE_KEY.get(sessionIdKey)!=null&&
						SESSION_KEY_UNIQUE_KEY.get(sessionIdKey).get() instanceof Set) {
					SESSION_KEY_UNIQUE_KEY.get(sessionIdKey).get().add(uniqueKeyStr);
				}else {
					ConcurrentHashSet<String> CACHE_IDS = new ConcurrentHashSet<String>();
					CACHE_IDS.add(uniqueKeyStr);
					SoftReference<ConcurrentHashSet<String>> sr = new SoftReference<ConcurrentHashSet<String>>(CACHE_IDS);
					SESSION_KEY_UNIQUE_KEY.put(sessionIdKey, sr);
				}
			}
		}catch (Exception e) {
			LogUtils.printLog("-------------saveCacheData : not redis conf or soft refernce is null ------");
		}
		
	}

	/**
	 * 获取uniqueKey
	 * @return
	 */
	public String getUniqueKey(EnableCache enableCache,Object[] args) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();

		String requestURI = request.getRequestURI();// 表示请求接口
		String sessionId = null;                    //session id
		Map<String, String[]> parameterMap = null;  // paramsKV
		Object[] as = null;                         //方法参数值

		HashSet<Class> excludeClass = new HashSet<>();
		excludeClass.add(ServletRequest.class);
		excludeClass.add(ServletResponse.class);

		if(enableCache.session()){
			sessionId = request.getSession().getId();// sessionId 表示同一个session
		}
		if(enableCache.requestParams()){
			parameterMap = request.getParameterMap();
		}
		if(enableCache.methodArgs()){
			as = args;
		}if(enableCache.excludeArgs()!=null&&enableCache.excludeArgs().length>0){
			for(Class clazz : enableCache.excludeArgs()){
				excludeClass.add(clazz);
			}
		}

		UniqueKey uniqueKey = new UniqueKey(excludeClass,sessionId, requestURI, parameterMap, as);
		String uniqueKeyStr = uniqueKey.getUniqueKeyStr(); //redis key
		return uniqueKeyStr;
	}

	/**
	 *  获取SessionIdKey
	 * @param declaredAnnotation
	 * @return
	 */
	public String getSessionIdKey(EnableCache declaredAnnotation) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		HttpSession session = request.getSession();
		return getSessionIdKey(session, declaredAnnotation);
	}
	
	/**
	 *  获取SessionIdKey
	 * @param declaredAnnotation
	 * @return
	 */
	public List<String> getSessionIdKey(UnableCache declaredAnnotation) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		HttpSession session = request.getSession();
		return getSessionIdKey(session, declaredAnnotation);
	}
	
	/**
	 * 基础校验
	 * @param signature
	 * @return
	 * @throws CacheException
	 */
	private EnableCache baseChecked(Signature signature) throws CacheException {
		EnableCache declaredAnnotation = null;
		if (signature instanceof MethodSignature) {
			MethodSignature ms = (MethodSignature) signature;
			Method method = ms.getMethod();
			Class<?> dc = method.getDeclaringClass();
			Controller controller = dc.getAnnotation(Controller.class);
			Service service = dc.getAnnotation(Service.class);
			Repository repository = dc.getAnnotation(Repository.class);
			if (controller == null && service == null && repository == null) {
				throw new CacheException("缓存只允许在controller ，service ，repository 层中使用");
			}
			declaredAnnotation = method.getDeclaredAnnotation(EnableCache.class);
			String value = declaredAnnotation.value();
			if(!StringUtils.isNoneBlank(value)) {
				throw new CacheException("EnableCache 的值不允许为 空");
			}
		}
		return declaredAnnotation;
	}

	/**
	 * 获取sessionIdKey
	 * @param session
	 * @param declaredAnnotation
	 * @return
	 */
	private String getSessionIdKey(HttpSession session, EnableCache declaredAnnotation) {
		return session.getId()+declaredAnnotation.value();
	}
	
	/**
	 * 获取sessionIdKey
	 * @param session
	 * @param declaredAnnotation
	 * @return
	 */
	private LinkedList getSessionIdKey(HttpSession session, UnableCache declaredAnnotation) {
		String[] value = declaredAnnotation.value();
		LinkedList<String> linkedList = new LinkedList<String>();
		if(value!=null&&value.length!=0) {
			for(String valu : value) {
				linkedList.add(valu);
			}
		}
		return linkedList;
		
		
	}

	/**
	    *  从Redis缓存中查询数据,
	 * @param uniqueKeyStr
	 * @return  查询不到返回null
	 */
	private Object queryCacheData(String uniqueKeyStr) {
		try {
			if(JedisUtils.getClient()!=null) {
				CacheData data = JedisUtils.getData(uniqueKeyStr, CacheData.class);
				if(data!=null) {
					Object cacheData = data.getData();
					if(cacheData!=null) {
						return cacheData;
					}
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 在弃用缓存切点 前执行
	 */
	@Before("pointCutUnableCache()")
	public void doBeforeUnableCache(final JoinPoint joinPoint) {
		Signature signature = joinPoint.getSignature();
		if(signature instanceof MethodSignature) {
			Method method = ((MethodSignature) signature).getMethod();
			UnableCache unableCache = method.getDeclaredAnnotation(UnableCache.class);
			List<String> sessionIdKeys = getSessionIdKey(unableCache);
			if(sessionIdKeys==null||sessionIdKeys.size()==0) return ;
			for(String sessionIdKey : sessionIdKeys) {
				try {
					ConcurrentHashSet<String> concurrentHashSet = SESSION_KEY_UNIQUE_KEY.get(sessionIdKey)!=null?SESSION_KEY_UNIQUE_KEY.get(sessionIdKey).get():new ConcurrentHashSet<String>();
					if(concurrentHashSet!=null&&concurrentHashSet.size()>0) {
						for(String cacheId : concurrentHashSet ) {
							JedisUtils.deleteDataValue(cacheId);
							//logger.info("CacheAspect_UNABLE_CACHE CACHE_ID:"+cacheId+"  SESSIONIDKEY:"+sessionIdKey);
							LogUtils.printLog("CacheAspect_UNABLE_CACHE CACHE_ID:"+cacheId+"  SESSIONIDKEY:"+sessionIdKey);
						}
						concurrentHashSet.clear(); 
					}
				}catch (NullPointerException e) {
					   logger.info(e.getMessage());
				}catch (Exception e) {
						throw e;
				} 
			}
		}
	}


	/**
	 * 检验@EnableCache和@UnableCache所属key的唯一性
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Set<ClassDefinition> classDefinitions = ClassUtils.getClassDefinitions();
		if(classDefinitions != null&&classDefinitions.size()>0) {
			HashMap<String, MethodDefinition> enableUniqueMap = new HashMap<String, MethodDefinition>();
			HashMap<List, MethodDefinition> unableUniqueMap = new HashMap<List, MethodDefinition>();
			for(ClassDefinition cd : classDefinitions) {
				List<MethodDefinition> methodDefinition = cd.getMethodDefinition();
				if(methodDefinition!=null&&methodDefinition.size()>0) {
					for(MethodDefinition md : methodDefinition) {
						List<String> methodAnnotationNames = md.getMethodAnnotationNames();
						if(methodAnnotationNames!=null&&methodAnnotationNames.size()>0) {
							for(String man : methodAnnotationNames) {
								//校验EnableCache 的唯一性
								if(ENABLE_CACHE.equals(man)) {
									checkEnableCacheInitData(enableUniqueMap, md, man);
								}
								//校验UnableCache 的唯一性
								if(UNABLE_CACHE.equals(man)) {
									checkUnableCacheInitData(unableUniqueMap, md, man);
								}
							}
						}
					}
				}
			}
		}
	}
	/**
	 * spring 容器启动时检查基本数据
	 * @param md
	 * @param man
	 * @throws CacheException
	 */
	private void checkUnableCacheInitData(
			HashMap<List, MethodDefinition> uniqueMap,
			MethodDefinition md, 
			String man) throws CacheException {
		
		Map<String, AnnotationNode> methodAnnotations = md.getMethodAnnotations();
		AnnotationNode annotationNode = methodAnnotations.get(man);
		List<Object> values = annotationNode.values;
		String targetClass_MehotdName = md.getClassDefinition().getTargetClazz()+"."+md.getMethodName();
		if(values==null) {
			throw new CacheException(man+" 的值不允许为 空,方法名："+targetClass_MehotdName);
		}
		if(values.size()!=2) {
			throw new CacheException(man+" 的值出现问题,方法名："+targetClass_MehotdName);
		}
		List unableCacheValue = (List)values.get(1);
		if(CollectionUtils.isEmpty(unableCacheValue)) {
			throw new CacheException(man+" 的值不允许为 空,方法名："+targetClass_MehotdName);
		}
	}
	/**
	 * spring 容器启动时检查基本数据
	 * @param md
	 * @param man
	 * @throws CacheException
	 */
	private void checkEnableCacheInitData(
			HashMap<String, MethodDefinition> uniqueMap,
			MethodDefinition md, 
			String man) throws CacheException {
		
		Map<String, AnnotationNode> methodAnnotations = md.getMethodAnnotations();
		AnnotationNode annotationNode = methodAnnotations.get(man);
		List<Object> values = annotationNode.values;
		String targetClass_MehotdName = md.getClassDefinition().getTargetClazz()+"."+md.getMethodName();
		if(values==null) {
			throw new CacheException(man+" 的值不允许为 空,方法名："+targetClass_MehotdName);
		}
		if(values.size()!=2) {
			throw new CacheException(man+" 的值出现问题,方法名："+targetClass_MehotdName);
		}
		String enableCacheValue = values.get(1)+"";
		if(!StringUtils.isNoneBlank(enableCacheValue)) {
			throw new CacheException(man+" 的值不允许为 空,方法名："+targetClass_MehotdName);
		}
		MethodDefinition key = uniqueMap.get(enableCacheValue);
		if(key==null) {
			uniqueMap.put(enableCacheValue, md);
		}else {
			String targetClass = key.getClassDefinition().getTargetClazz()+".";
			throw new CacheException(man+" 的值出现重复 ,方法名为："+targetClass+key.getMethodName()+" , "+targetClass+md.getMethodName());
		}
	}

}
