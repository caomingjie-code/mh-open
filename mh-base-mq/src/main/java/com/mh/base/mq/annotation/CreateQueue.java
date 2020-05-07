package com.mh.base.mq.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 *  该注解表示在：在MQ服务器上声明一个队列
 * @author cmj
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface CreateQueue {
	/**
	 * 代表队里名称
	 * @return
	 */
	String name(); 
}
