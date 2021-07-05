package com.javaoffers.base.kafka.core;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author cmj
 * @Description TODO
 * @createTime 2021年07月04日 22:55:00
 */
public class KafkaProducerAndConsumerFactoryBean implements FactoryBean {

    public String className;

    public Class<MethodInterceptor> methodInterceptorClass;

    public AnnotationMetadata metadata;

    @Override
    public Object getObject() throws Exception {

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(Class.forName(className));
        MethodInterceptor methodInterceptor = methodInterceptorClass.newInstance();
        isKafkaProducerInterceptor(methodInterceptor);
        proxyFactory.addAdvice(methodInterceptor);
        Object proxy = proxyFactory.getProxy();
        return proxy;
    }

    private void isKafkaProducerInterceptor(MethodInterceptor methodInterceptor) {
        if(methodInterceptor instanceof KafkaProducerInterceptor){
            KafkaProducerInterceptor kafkaProducerInterceptor = (KafkaProducerInterceptor) methodInterceptor;
            kafkaProducerInterceptor.setMetadata(metadata);
        }
    }

    @Override
    public Class<?> getObjectType() {
        try {
            Class<?> aClass = Class.forName(className);
            return aClass;
        }catch (Exception e){

        }
        return null;
    }

    public AnnotationMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(AnnotationMetadata metadata) {
        this.metadata = metadata;
    }

    public boolean isSingleton() {
        return true;
    }


    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Class<MethodInterceptor> getMethodInterceptorClass() {
        return methodInterceptorClass;
    }

    public void setMethodInterceptorClass(Class<MethodInterceptor> methodInterceptorClass) {
        this.methodInterceptorClass = methodInterceptorClass;
    }

    public KafkaProducerAndConsumerFactoryBean() {
    }

    public KafkaProducerAndConsumerFactoryBean(String className, Class<MethodInterceptor> methodInterceptorClass) {
        this.className = className;
        this.methodInterceptorClass = methodInterceptorClass;
    }
}
