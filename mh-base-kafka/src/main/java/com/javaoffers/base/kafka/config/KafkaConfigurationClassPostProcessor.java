package com.javaoffers.base.kafka.config;

import com.javaoffers.base.kafka.anno.EnableKafkaProducer;
import com.javaoffers.base.kafka.core.KafkaProducer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author cmj
 * @Description kafka 启动配置类型
 * @createTime 2021年07月04日 20:35:00
 */

public class KafkaConfigurationClassPostProcessor implements BeanDefinitionRegistryPostProcessor, PriorityOrdered {

    private static final String KAFKA_PRODUCER_CLASS = "com.javaoffers.base.kafka.core.KafkaProducerAndConsumerFactoryBean";

    private static final String KAFKA_PRODUCER_METHOD_INTERCEPTOR_CLASS = "com.javaoffers.base.kafka.core.KafkaProducerInterceptor";

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        Set<BeanDefinition> candidateComponents = new LinkedHashSet<>();
        KafkaScaner kafkaScaner = new KafkaScaner(registry);
        kafkaScaner.resetFilters(false);
        kafkaScaner.setIsCandidateClass(KafkaProducer.class);
        kafkaScaner.addIncludeFilter(new AnnotationTypeFilter(EnableKafkaProducer.class));
        doScanKafkaProducer(registry, candidateComponents, kafkaScaner);
        for(BeanDefinition beanDefinitionOfKafkaProducer : candidateComponents){
            ScannedGenericBeanDefinition scannedGenericBeanDefinition = (ScannedGenericBeanDefinition) beanDefinitionOfKafkaProducer;
            String beanClassName = scannedGenericBeanDefinition.getBeanClassName();
            scannedGenericBeanDefinition.setBeanClassName(KAFKA_PRODUCER_CLASS);

            scannedGenericBeanDefinition.getPropertyValues().add("className",beanClassName);
            scannedGenericBeanDefinition.getPropertyValues().add("methodInterceptorClass",KAFKA_PRODUCER_METHOD_INTERCEPTOR_CLASS);
            scannedGenericBeanDefinition.getPropertyValues().add("metadata",scannedGenericBeanDefinition.getMetadata());
            scannedGenericBeanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);

            registry.registerBeanDefinition(beanClassName,beanDefinitionOfKafkaProducer);


        }

    }

    private void doScanKafkaProducer(BeanDefinitionRegistry registry, Set<BeanDefinition> candidateComponents, KafkaScaner kafkaScaner) {
        if(registry instanceof DefaultListableBeanFactory){
            DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory)registry;
            String[] beanNamesForAnnotation = defaultListableBeanFactory.getBeanNamesForAnnotation(SpringBootApplication.class);
            if(!Objects.isNull(beanNamesForAnnotation)&&beanNamesForAnnotation.length>0){
                String beanNameOfSpringBootApplication = beanNamesForAnnotation[0];
                Object bean = defaultListableBeanFactory.getBean(beanNameOfSpringBootApplication);
                Package aPackage = bean.getClass().getPackage();
                String name = aPackage.getName()+".**";
                candidateComponents.addAll( kafkaScaner.findCandidateComponents(name));

            }
        }else {
            kafkaScaner.scan();
            candidateComponents.addAll( kafkaScaner.getBeanDefinitionHolders().stream().map(bh->{ return bh.getBeanDefinition();}).collect(Collectors.toSet()));
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
