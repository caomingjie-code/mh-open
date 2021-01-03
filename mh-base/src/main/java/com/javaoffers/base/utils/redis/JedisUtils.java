/**   
 * Copyright © 2019 mh615 Info.  All rights reserved.
 * 
 * 功能描述：
 * @Package: com.javaoffers615.springstudy.utils
 * @author: cmj   
 * @date: 2019-1-30 下午3:10:31 
 */
package com.javaoffers.base.utils.redis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.xerces.impl.dv.util.Base64;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import com.javaoffers.base.common.protostuff.ProtostuffUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Copyright: Copyright (c) 2019 LanRu-Caifu
 * 
 * @ClassName: JedisUtils.java
 * @Description: 该类的功能描述: jedis 使用的一种封装，解决 key 和  value 可以存放任意对象不受限制。
 * 				 备注： 如果该工具类在 springboot项目，则将该工具类中的@Value("${XXX}") 改为springboot 
 * 				中的Jedis 配置key  即可。注意：回收资源 returnResource()
 *
 * @version: v1.0.0
 * @author: 曹明杰
 * @date: 2019-1-30 下午3:10:31
 *
 *        Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2019-1-30
 *        cmj v1.0.0 修改原因
 */
@Component
@ConditionalOnProperty(prefix = "spring.redis",name = "host")
public class JedisUtils implements InitializingBean, EnvironmentAware {

	private static String reduesUrl; // redis url
	private static String jedisPW; // redis 密码
	private static Integer jedisPort; // redis 端口号
	private static Integer jedisTimeOut=60;// 超时时间
	private static Integer connectionCount=10; //链接数

	public final static ThreadLocal<Jedis> tl = new ThreadLocal<Jedis>();
	private static JedisPool jedisPool = null;
	final static JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
	@Resource
	private RedisProperties redisProperties;

	//@Value("${spring.redis.host}")
	private void setUrl(String url) {
		reduesUrl = url;
	}

	//@Value("${spring.redis.password}")
	private void setPw(String pw) {
		jedisPW = pw;
	}

	//@Value("${spring.redis.port}")
	private void setPt(Integer jp) {
		jedisPort = jp;
	}

	private void setConnectionCount(Integer cc){
		connectionCount = cc;
	}

	//@Value("${spring.redis.timeout}")
	private void setjedisTimeOut(String jedisTimeOutStr) {
		int timeout_ = 1;
		String[] split = jedisTimeOutStr.split("[*]");
		if (split != null && split.length > 1) {

			for (String to : split) {
				int parseInt = Integer.parseInt(to);
				timeout_ = timeout_ * parseInt;
			}
		}
		if (timeout_ == 1) {
			timeout_ = 1000 * 60 * 3;
		}
		jedisTimeOut = timeout_;
	}

	@Override
	public void setEnvironment(Environment environment) {
		String host = environment.getProperty("spring.redis.host");
		if(StringUtils.isNotBlank(host)){
			setUrl(host);
		}

		String password = environment.getProperty("spring.redis.password");
		if(StringUtils.isNotBlank(password)){
			setPw(password);
		}

		String port = environment.getProperty("spring.redis.port");
		if(StringUtils.isNotBlank(port)){
			setPt(Integer.parseInt(port));
		}

		String timeout = environment.getProperty("spring.redis.timeout");
		if(StringUtils.isNotBlank(timeout)){
			setjedisTimeOut(timeout);
		}

		String cc = environment.getProperty("spring.redis.jedis.pool.max-active");
		if(StringUtils.isNotBlank(cc)){
			setConnectionCount(Integer.parseInt(cc));
		}

		jedisPoolConfig.setMaxWaitMillis(1000 * 60 * 3);
		jedisPoolConfig.setMinIdle(10);
		jedisPoolConfig.setMaxIdle(20);
		jedisPoolConfig.setMaxTotal(Math.max(connectionCount,10));

	}

	public static Jedis getClient() {
		Jedis jedis = tl.get();
		if (jedis == null) {
			Jedis resource = jedisPool.getResource();
			tl.set(resource);
			return resource;
		}
		return jedis;
	}

	// List start

	/**
	 * 向右追加一元素
	 * 
	 * @param key
	 * @param t
	 */
	public static <T> void appendRJList(Object key, T t) {
		byte[] key_encode = ProtostuffUtils.encode(new Key<Object>(key));
		byte[] encode = ProtostuffUtils.encode(new Data(t));
		String base64Key = Base64.encode(key_encode);
		String base64StrObject = Base64.encode(encode);
		Jedis client = getClient();
		client.rpush(base64Key, base64StrObject);
	}

	/**
	 * 向右追加一元素,并设置过期时间 seconds秒
	 * 
	 * @param key
	 * @param t
	 */
	public static <T> void appendRJList(Object key, T t, int seconds) {
		byte[] key_encode = ProtostuffUtils.encode(new Key<Object>(key));
		byte[] encode = ProtostuffUtils.encode(new Data(t));
		
		String base64Key = Base64.encode(key_encode);
		String base64StrObject = Base64.encode(encode);
		Jedis client = getClient();
		client.rpush(base64Key, base64StrObject);
		client.expire(base64Key, seconds);
	}

	/**
	 * 从右边弹出一个元素
	 * 
	 * @param key
	 * @param clazz
	 * @return
	 */
	public static <T> T popRJList(Object key, Class<T> clazz) {
		byte[] encode = ProtostuffUtils.encode(new Key<Object>(key));
		String base64Key = Base64.encode(encode);

		Jedis client = getClient();
		String spop = client.rpop(base64Key);
		if(spop == null) return null;
		byte[] decode = Base64.decode(spop);
		T decode2 = (T)ProtostuffUtils.decode(decode, Data.class).getData();
		return decode2;

	}

	/**
	 * 获取全部list
	 * 
	 * @param key
	 * @param clazz
	 * @return
	 */
	public static <T> List<T> allJList(Object key, Class<T> clazz) {
		byte[] encode = ProtostuffUtils.encode(new Key<Object>(key));
		String base64Key = Base64.encode(encode);
		Jedis client = getClient();
		List<String> smembers = client.lrange(base64Key, 0, -1);
		ArrayList<T> list = new ArrayList<T>();
		if (smembers != null && smembers.size() > 0) {
			for (String elem : smembers) {
				byte[] decode = Base64.decode(elem);
				T decode2 = (T)ProtostuffUtils.decode(decode, Data.class).getData();
				list.add(decode2);
			}
		}
		return list;

	}
	
	/**
	 * @描述： 根据索引查询list中的元素
	 * @param key
	 * @param index
	 * @param clazz
	 * @return
	 */
	public static <T> T getTByIndexOfList(Object key,int index,Class<T> clazz) {
		T t = null;
		byte[] encode = ProtostuffUtils.encode(new Key<Object>(key));
		String base64Key = Base64.encode(encode);
		Jedis client = getClient();
		String lindexElementOfList = client.lindex(base64Key, index);
		if(lindexElementOfList == null) return null;
		byte[] decode = Base64.decode(lindexElementOfList);
		t = (T)ProtostuffUtils.decode(decode, Data.class).getData();
		return t;
	}

	// List end

	// Set start

	/**
	 * 向左追加一个元素
	 * 
	 * @param key
	 * @param t
	 */
	public static <T> void appendLJSet(Object key, T t) {
		byte[] key_encode = ProtostuffUtils.encode(new Key<Object>(key));
		byte[] encode = ProtostuffUtils.encode(new Data(t));
		String base64Key = Base64.encode(key_encode);
		String base64StrObject = Base64.encode(encode);
		Jedis client = getClient();
		client.sadd(base64Key, base64StrObject);
	}

	/**
	 * 向左追加一个元素，并设置过期时间 seconds 秒
	 * 
	 * @param key
	 * @param t
	 */
	public static <T> void appendLJSet(Object key, T t, int seconds) {
		byte[] key_encode = ProtostuffUtils.encode(new Key<Object>(key));
		byte[] encode = ProtostuffUtils.encode(new Data(t));
		String base64Key = Base64.encode(key_encode);
		String base64StrObject = Base64.encode(encode);
		Jedis client = getClient();
		client.sadd(base64Key, base64StrObject);
		client.expire(base64Key, seconds);
	}

	/**
	 * 从左边弹出一个元素
	 * 
	 * @param key
	 * @param clazz
	 * @return
	 */
	public static <T> T popLJSet(Object key, Class<T> clazz) {
		byte[] encode = ProtostuffUtils.encode(new Key<Object>(key));
		String base64Key = Base64.encode(encode);

		Jedis client = getClient();
		String spop = client.spop(base64Key);
		if(spop == null) return null;
		byte[] decode = Base64.decode(spop);
		T decode2 = (T)ProtostuffUtils.decode(decode, Data.class).getData();
		return decode2;

	}

	/**
	 * 获取整个set 集合
	 * 
	 * @param key
	 * @param clazz
	 * @return
	 */
	public static <T> Set<T> allJSet(Object key, Class<T> clazz) {
		byte[] encode = ProtostuffUtils.encode(new Key<Object>(key));
		String base64Key = Base64.encode(encode);
		HashSet<T> hashSet = new HashSet<T>();
		Jedis client = getClient();
		Set<String> smembers = client.smembers(base64Key);
		if (smembers != null && smembers.size() > 0) {
			for (String elem : smembers) {
				byte[] decode = Base64.decode(elem);
				Data decode2 = ProtostuffUtils.decode(decode, Data.class);
				T data = (T)decode2.getData();
				hashSet.add(data);
			}
		}
		return hashSet;

	}

	// Set end

	// Data start

	/**
	 * 放入一个对象
	 * 
	 * @param key
	 * @param t
	 */
	public static <T> void putData(Object key, T t) {

		byte[] key_encode = ProtostuffUtils.encode(new Key<Object>(key));
		byte[] encode = ProtostuffUtils.encode(new Data<T>(t));
		Jedis client = getClient();
		client.set(key_encode, encode);
	}

	/**
	 * 放入一个对象,并设置过期时间 seconds 秒
	 * 
	 * @param key
	 * @param t
	 */
	public static <T> void putData(Object key, T t, int seconds) {
		byte[] key_encode = ProtostuffUtils.encode(new Key<Object>(key));
		byte[] encode = ProtostuffUtils.encode(new Data<T>(t));
		Jedis client = getClient();
		client.setex(key_encode, seconds, encode);
	}

	/**
	 * 放入一个对象,如果当前t 不存在
	 * 
	 * @param key
	 * @param t
	 */
	public static <T> void putDataIfAbsent(Object key, T t) {

		byte[] key_encode = ProtostuffUtils.encode(new Key<Object>(key));
		byte[] encode = ProtostuffUtils.encode(new Data<T>(t));
		Jedis client = getClient();
		client.setnx(key_encode, encode);
	}

	/**
	 * 根据key 返回 clazz 类型的对象
	 * 
	 * @param key
	 * @param clazz
	 * @return
	 */
	public static <T> T getData(Object key, Class<T> clazz) {
		byte[] key_encode = ProtostuffUtils.encode(new Key<Object>(key));
		byte[] bs = getClient().get(key_encode);
		@SuppressWarnings("unchecked")
		Data<T> decode = ProtostuffUtils.decode(bs, Data.class);
		if(decode == null) return null;
		T data = decode.getData();
		return data;
	}



	// Data start

	/**
	 * 所有的key 都需要包装
	 * @author mj
	 *
	 * @param <T>
	 */
	private static class Key<T> {
		T data;

		public T getData() {
			return data;
		}

		public void setData(T data) {
			this.data = data;
		}

		public Key(T data) {
			super();
			this.data = data;
		}

	}
	/**
	 *  所有的value 都需要包装
	 * 
	 * @author mingjie
	 * @param <T>
	 */
 	private static class Data<T> {
		T data;

		public T getData() {
			return data;
		}

		public void setData(T data) {
			this.data = data;
		}

		public Data(T data) {
			super();
			this.data = data;
		}

	}

	/**
	 * <p>
	 * Title: afterPropertiesSet
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @throws Exception
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {



		if(redisProperties!=null&&!StringUtils.isBlank(redisProperties.getHost())) {
			if(jedisPW==null){
				jedisPool = new JedisPool(jedisPoolConfig, reduesUrl);
			}else{
				jedisPool = new JedisPool(jedisPoolConfig, reduesUrl,jedisPort,jedisTimeOut,jedisPW);
			}
		}

	}

	static class PrimitiveException extends Exception {

		private String error = null;

		/**
		 * @Function: JedisUtils.java
		 * @Description: 该函数的功能描述
		 *
		 * @param:参数描述
		 * @version: v1.0.0
		 * @author: cmj
		 * @date: 2019年2月2日 下午3:25:01
		 */
		public PrimitiveException(String error) {
			super(error);
			this.error = error;
		}
	}

	/**
	 * 回收 资源
	 */
	public static void returnResource() {
		Jedis jedis = tl.get();
		if (jedis != null) {
			try {
				jedisPool.returnResource(jedis); // 回收资源到线程池
				tl.remove(); // 解除当前线程的绑定
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 删除Data
	 * 
	 * @param key
	 */
	public static void deleteDataValue(Object key) {
		Jedis client = getClient();
		byte[] key_encode = ProtostuffUtils.encode(new Key<Object>(key));
		client.del(key_encode);
	}

	/**
	 * 删除Collection 包含 JSet 和 JList
	 * 
	 * @param key
	 */
	public static void deleteCollectionValue(Object key) {
		Jedis client = getClient();
		byte[] key_encode = ProtostuffUtils.encode(new Key<Object>(key));
		String base64Key = Base64.encode(key_encode);
		Long del = client.del(base64Key);
	}
}
