package com.javaoffers.base.kafka.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

/**
 * @Description: kafka 扫描器
 * @Auther: create by cmj on 2021/7/4 20:01
 */
public class KafkaScaner extends ClassPathBeanDefinitionScanner {

    private BeanFactory beanFactory;

    public KafkaScaner(BeanDefinitionRegistry registry) {
        super(registry);
    }




}
