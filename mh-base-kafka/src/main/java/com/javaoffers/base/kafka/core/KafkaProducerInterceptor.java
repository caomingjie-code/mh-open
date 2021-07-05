package com.javaoffers.base.kafka.core;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.reflect.Method;

/**
 * @author cmj
 * @Description TODO
 * @createTime 2021年07月04日 23:39:00
 */
public class KafkaProducerInterceptor implements MethodInterceptor {

    public AnnotationMetadata metadata;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        System.out.println("开始生产消息！！！");

        Object[] arguments = invocation.getArguments();
        Method method = invocation.getMethod();




        return "";

    }

    public KafkaProducerInterceptor() {
    }

    public AnnotationMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(AnnotationMetadata metadata) {
        this.metadata = metadata;
    }
}
