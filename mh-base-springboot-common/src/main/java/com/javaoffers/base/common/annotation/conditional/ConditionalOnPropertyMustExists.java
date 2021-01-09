package com.javaoffers.base.common.annotation.conditional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.lang.annotation.*;
import java.util.Collection;
import java.util.Map;

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(ConditionalOnPropertyMustExistsProcess.class)
public @interface ConditionalOnPropertyMustExists {
    String value();
}

 class ConditionalOnPropertyMustExistsProcess extends SpringBootCondition  {

     Environment environment;

     @Override
     public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
         ConditionOutcome match = ConditionOutcome.noMatch("springCondition is empty"); //不具有资格成为beanDefinition

         Map<String, Object> ConditionalOnPropertyMustExists = metadata.getAnnotationAttributes(ConditionalOnPropertyMustExists.class.getName());
         Environment environment = context.getEnvironment();
         if(ConditionalOnPropertyMustExists!=null&&ConditionalOnPropertyMustExists.size()>0){
             Collection<Object> values = ConditionalOnPropertyMustExists.values();
             if(values!=null){
                 for(Object v : values){
                     if(v instanceof String){
                         String str = (String) v;
                         String property = environment.getProperty(str);
                         if(StringUtils.isNotBlank(property)){
                             match =    ConditionOutcome.match();
                         }
                     }
                 }
             }
         }

         return match;
     }

 }
