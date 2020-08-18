package com.mh.base.mq.process;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.mh.base.mq.annotation.CreateQueue;
import com.mh.base.mq.annotation.ListenerQueue;
import org.apache.commons.lang3.StringUtils;
import com.mh.base.mq.exception.MHMQException;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.tree.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.MethodParameterNamesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.springframework.stereotype.Component;

/**
 *  扫描queue 注解
 *  
 * @author cmj
 *
 */
@SuppressWarnings({ "restriction", "unused" })
@Component
public class ScanQueueClazz  {
	private static final Log logger = LogFactory.getLog(ScanQueueClazz.class);

	//存放所有队列名称
	private static final ConcurrentHashMap<String, Class> queues = new ConcurrentHashMap<String, Class>();
	private static final ConcurrentHashMap<String, QueueBeanDefinition> listenerQueueMethods = new ConcurrentHashMap<String, QueueBeanDefinition>();//key: 类名+方法拼接
    private static final String QUEUE_ANNOTATION_NAME = "com.mh.base.mq.annotation.CreateQueue";
    private static final String LISTENER_QUEUE_ANNOTATION_NAME = "com.mh.base.mq.annotation.ListenerQueue";
	static {
    	try {

			Collection<URL> urls = ScanQueueClazz.forResource(resourceName("com.mh"), ScanQueueClazz.class.getClassLoader());
			List<File> list = new LinkedList();
			for(URL url : urls){
				String path = url.getPath();
				List<File> ls = getAllClassFile(new File( path ));//此方法为获取classpath路径下所有的class文件（所有包）
				list.addAll(ls);
			}

        	for(File f: list) {//便利每一个class文件
    			//getClassName(f);
    			 ClassReader reader = new ClassReader(new FileInputStream(f));
    			 ClassNode cn = new ClassNode();//创建ClassNode,读取的信息会封装到这个类里面
    	         reader.accept(cn, 0);//开始读取
    	         List<AnnotationNode> annotations = cn.visibleAnnotations;//获取声明的所有注解
    	         List<MethodNode> methods = cn.methods;
    	         if(methods!=null&&methods.size()>0) {
    	        	 for( MethodNode method: methods) {
    	        		 List<AnnotationNode> visibleAnnotations = method.visibleAnnotations;//获取方法上的注解
    	        		 if(visibleAnnotations != null) {
    	        			 for(AnnotationNode mt:visibleAnnotations) {
    	                 		//获取注解的描述信息
    	                 		 String anno = mt.desc.replaceAll("/", ".");
    	                 		 String annoName = anno.substring(1, anno.length()-1);
    	                 		 if(LISTENER_QUEUE_ANNOTATION_NAME.equals(annoName)) { 
    	                 			 
    	                 			 String className = cn.name.replaceAll("/", ".");
    	                  			//获取注解的属性名对应的值，（values是一个集合，它将注解的属性和属性值都放在了values中，通常奇数为值偶数为属性名）
    	          		        	 String valu = mt.values.get(1).toString();
									 logger.info(className);
									 logger.info(valu);
    	          		        	 //判断是否已经存在
    	          		        	 String methodName = method.name;
    	          		        	 String nn = method.desc.substring(2, method.desc.length()-3).replaceAll("/", ".");
    	          		        	 Class<?> beanClazz = Class.forName(className);
    	          		        	 Class<?> methodParamClazz = Class.forName(nn);
    	          		        	 String key = className+"_"+methodName;
    	          		        	 Method listenerMethod = beanClazz.getDeclaredMethod(methodName, methodParamClazz); //监听的方法
    	          		        	 /**
    	          		        	  * private Class beanClazz;
										private String listenerQueueName;
										private Method listenerMehod;// 监听的方法
										private Class<Object> queueDataClazz  ; //队列数据类型,默认为Object类型，不允许其他类型
    	          		        	  */
    	          		        	QueueBeanDefinition beanDefinition = new QueueBeanDefinition(beanClazz, preparBuild(valu), listenerMethod, Object.class);
    	          		        	listenerQueueMethods.put(key, beanDefinition);
    	          		        	
    	                 		 }
    	                 	 }
    	        		 }
        	         }
    	         }
    	         
    	         if(annotations!=null) {//便利注解
                	 for(AnnotationNode an: annotations) {
                		 //获取注解的描述信息
                		 String anno = an.desc.replaceAll("/", ".");
                		 String annoName = anno.substring(1, anno.length()-1);
                		 if(QUEUE_ANNOTATION_NAME.equals(annoName)) {
                			 String className = cn.name.replaceAll("/", ".");
                			//获取注解的属性名对应的值，（values是一个集合，它将注解的属性和属性值都放在了values中，通常奇数为值偶数为属性名）
        		        	 String valu = an.values.get(1).toString();
        		        	 logger.info(className);
        		        	 logger.info(valu);
        		        	 //判断是否已经存在
        		        	 Class class1 = queues.get(valu);
        		        	 if(class1!=null) {
        		        		 throw new MHMQException("在注解 CreateQueue 的使用过程中，发现存在相同的name 值："+valu);
        		        	 }if(StringUtils.isNoneBlank(valu)) {
        		        		 queues.put(valu, Class.forName(className)) ;//根据匹配的注解，将其封装给具体的业务使用
        		        	 }
        		        	 
                		 }
    		         }
                	
                 }
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	public static ClassLoader contextClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	private static String resourceName(String name) {
		if (name != null) {
			String resourceName = name.replace(".", "/");
			resourceName = resourceName.replace("\\", "/");
			if (resourceName.startsWith("/")) {
				resourceName = resourceName.substring(1);
			}
			return resourceName;
		}
		return null;
	}

	public static ClassLoader[] classLoaders(ClassLoader... classLoaders) {
		if (classLoaders != null && classLoaders.length != 0) {
			return classLoaders;
		} else {
			ClassLoader contextClassLoader = contextClassLoader(), staticClassLoader = ScanQueueClazz.class.getClassLoader();
			return contextClassLoader != null ?
					staticClassLoader != null && contextClassLoader != staticClassLoader ?
							new ClassLoader[]{contextClassLoader, staticClassLoader} :
							new ClassLoader[]{contextClassLoader} :
					new ClassLoader[] {};

		}
	}

	public static Collection<URL> forResource(String resourceName, ClassLoader... classLoaders) {
		final List<URL> result = new ArrayList<>();
		final ClassLoader[] loaders = classLoaders(classLoaders);
		for (ClassLoader classLoader : loaders) {
			try {
				final Enumeration<URL> urls = classLoader.getResources(resourceName);
				while (urls.hasMoreElements()) {
					final URL url = urls.nextElement();
					int index = url.toExternalForm().lastIndexOf(resourceName);
					if (index != -1) {
						// Add old url as contextUrl to support exotic url handlers
						result.add(new URL(url, url.toExternalForm().substring(0, index)));
					} else {
						result.add(url);
					}
				}
			} catch (IOException e) {
				if (Reflections.log != null) {
					Reflections.log.error("error getting resources for " + resourceName, e);
				}
			}
		}
		return distinctUrls(result);
	}
	//http://michaelscharf.blogspot.co.il/2006/11/javaneturlequals-and-hashcode-make.html
	private static Collection<URL> distinctUrls(Collection<URL> urls) {
		Map<String, URL> distinct = new LinkedHashMap<>(urls.size());
		for (URL url : urls) {
			distinct.put(url.toExternalForm(), url);
		}
		return distinct.values();
	}

	private static void parseClassForMq(Set<Class<?>> allClazz) {
		if(allClazz!=null){
			for(Class clazz : allClazz){

				logger.info(clazz.getName());
				//解析创建队列
				CreateQueue cq = (CreateQueue)clazz.getDeclaredAnnotation(CreateQueue.class);
				if(cq!=null){
					queues.put(cq.name(),clazz);
				}

				//解析监听队列
				buidQueueBeanDefinition(clazz);
			}
		}

	}

	/**
	 * 构建QueueBeanDefinition
	 * @param clazz
	 */
	private static void buidQueueBeanDefinition(Class clazz) {
		Method[] declaredMethods = clazz.getDeclaredMethods();
		if(declaredMethods!=null&&declaredMethods.length>0){
			for(Method m : declaredMethods){
				m.setAccessible(true);
				ListenerQueue lq = m.getDeclaredAnnotation(ListenerQueue.class);
				if(lq!=null){
					QueueBeanDefinition queueBeanDefinition = new QueueBeanDefinition(clazz,lq.queues()[0],m,Object.class);
					String key = clazz.getName()+"_"+m.getName();
					listenerQueueMethods.put(key,queueBeanDefinition);
				}
			}
		}
	}

	private static Set<Class> getAllClass(Set<String> allTypes) {
		HashSet<Class> classes = new HashSet<>();
		if(allTypes!=null){
			for(String clazz : allTypes){
				try {
					Class<?> aClass = Class.forName(clazz);
					classes.add(aClass);
				}catch (ClassNotFoundException e){
					//ignore
				}
			}
		}
		return classes;
	}

	/**
	 * 预处理
	 * @param valu
	 * @return
	 */
	public static String preparBuild(String valu) {
		String a = valu;
		if(a.contains("[")||a.contains("]")) {
			a = a.replace("[", "");
			a = a.replace("]", "");
			return a;
		}
		
		return valu;
	}
	
	public static ConcurrentHashMap<String, QueueBeanDefinition> getlistenerQueueMethods(){
		return listenerQueueMethods;
	}
	/**
	 * 获取所有的class 文件
	 * @param file
	 * @return
	 */
	public static List<File> getAllClassFile(File file ) {
		List<File> list =  new ArrayList<File>();
		if(file==null) {
			return list;
		}
		File[] listFiles = file.listFiles();
		if(listFiles!=null&&listFiles.length>0) {
			for(File f : listFiles) {
			    if(!f.isDirectory()&&f.getName().endsWith("class")) {
			    	list.add(f);
				}
			    if(f.isDirectory()) {
			    	List<File> allClassFile = getAllClassFile(f);
			    	list.addAll(allClassFile);
			    }
			}
		}
		return list;
	}
	
	
	public  ConcurrentHashMap<String, Class>  getQueues(){
		return queues;
	}

	public static class QueueBeanDefinition{
		private Class beanClazz;
		private String listenerQueueName; //该属性已经被弃用,改为: 直接使用注解中的值,支持一个注解支持多个队列的监听消费, 为了兼容没有删除
		private Method listenerMehod;// 监听的方法
		private Class<Object> queueDataClazz  ; //队列数据类型,默认为Object类型，不允许其他类型
		
		public Class getBeanClazz() {
			return beanClazz;
		}
		public void setBeanClazz(Class beanClazz) {
			this.beanClazz = beanClazz;
		}
		public String getListenerQueueName() {
			return listenerQueueName;
		}
		public void setListenerQueueName(String listenerQueueName) {
			this.listenerQueueName = listenerQueueName;
		}
		public Method getListenerMehod() {
			return listenerMehod;
		}
		public void setListenerMehod(Method listenerMehod) {
			this.listenerMehod = listenerMehod;
		}
		public Class<Object> getQueueDataClazz() {
			return queueDataClazz;
		}
		public void setQueueDataClazz(Class<Object> queueDataClazz) {
			this.queueDataClazz = queueDataClazz;
		}
		public QueueBeanDefinition(Class beanClazz, String listenerQueueName, Method listenerMehod,
				Class<Object> queueDataClazz) {
			super();
			this.beanClazz = beanClazz;
			this.listenerQueueName = listenerQueueName;
			this.listenerMehod = listenerMehod;
			this.queueDataClazz = queueDataClazz;
		}
	}
	
	
	
	
	
	
}
