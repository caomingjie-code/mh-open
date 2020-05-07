package com.mh.base.annotation.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 弃用缓存，针对于业务层数据缓存，减少数据库查询的压力
 * NOTE： 该注解只适合用于Controller层
 * @author mingjie
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UnableCache {

	/**
	 *  @描述：
	   *      缓存keys, 在底层逻辑会遍历keys for( key : keys) 根据  sessionId + key 进行关联多个 uniquekey 
	   *      在Redis中的key 为 uniquekey，当逻辑需要删除时 会根据 sessionId + key 查询关联的所有  uniquekey 
	   *      然后全部删除
	 * @return
	 */
	String[] value() default {};
}
 
