package com.javaoffers.base.kafka.core;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author cmj
 * @Description TODO
 * @createTime 2021年07月04日 23:39:00
 */
public class KafkaProducerInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        System.out.println("开始生产消息！！！");
       return invocation.proceed();

    }

    public KafkaProducerInterceptor() {
    }
}
