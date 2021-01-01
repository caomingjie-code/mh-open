package com.javaoffers.base.mq.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 监听指定的队列
 * @author cmj
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ListenerQueue  {
	String[] queues() default {};
	int batchCount()   default 1; //批处理数量,默认为1
	int consumerCount() default 1;//消费者数量,提高并发消费

}
