package com.javaoffers.base.kafka.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @Description: kafka 扫描器
 * @Auther: create by cmj on 2021/7/4 20:01
 */
public class KafkaScaner extends ClassPathBeanDefinitionScanner {

    private Set<BeanDefinitionHolder> beanDefinitionHolders;

    private Class isCandidateClass;

    public KafkaScaner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
        this.beanDefinitionHolders = beanDefinitionHolders;
        return beanDefinitionHolders;
    }

    public void setBeanDefinitionHolders(Set<BeanDefinitionHolder> beanDefinitionHolders) {
        this.beanDefinitionHolders = beanDefinitionHolders;
    }

    public Set<BeanDefinitionHolder> getBeanDefinitionHolders() {
        return beanDefinitionHolders;
    }

    public void setIsCandidateClass(Class isCandidateClass) {
        this.isCandidateClass = isCandidateClass;
    }

    /**
     * Determine whether the given bean definition qualifies as candidate.
     * <p>The default implementation checks whether the class is not an interface
     * and not dependent on an enclosing class.
     * <p>Can be overridden in subclasses.
     * @param beanDefinition the bean definition to check
     * @return whether the bean definition qualifies as a candidate component
     */
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        String beanClassName = beanDefinition.getBeanClassName();
        try {
            Class<?> clazz = Class.forName(beanClassName);
            if(isCandidateClass.isAssignableFrom(clazz))return true;
        }catch (Exception e){
            // igonor
        }
        return false;
    }
}
