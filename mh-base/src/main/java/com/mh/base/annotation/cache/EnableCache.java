package com.mh.base.annotation.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启缓存,针对于业务层数据缓存，减少数据库查询的压力
 * NOTE： 该注解只适合用于Controller层
 * @author mingjie
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableCache {
	
	/** 
	 *  @描述：
	   *      缓存key  全局唯一, 在底层逻辑会根据  sessionId + key 进行关联多个 uniquekey 
	   *      在Redis中的key 为 uniquekey，当逻辑需要删除时 会根据 sessionId + key 查询关联的所有  uniquekey 
	   *      然后全部删除
	 * @return
	 */
	String value() ;

	boolean session() default true; //会话级别缓存

	boolean methodArgs() default false;//根据 controller中的方法参数进行缓存,可以防止可以攻击,免疫根据url随机参数

	boolean requestParams() default true; //根据 request.getParams

}
