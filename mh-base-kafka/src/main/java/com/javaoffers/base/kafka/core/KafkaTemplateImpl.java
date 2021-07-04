package com.javaoffers.base.kafka.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @Description:
 * @Auther: create by cmj on 2021/7/4 19:34
 */
public class KafkaTemplateImpl implements KafkaTemplate , ApplicationContextAware {

    ApplicationContext applicationContext;

    @Override
    public void send(String topic, Object key, Object msg) {

    }

    @Override
    public boolean sendWithAck(String topic, String key, Object msg) {
        return false;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
