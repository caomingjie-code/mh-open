package com.javaoffers.base.common.annotation.model;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于model数据的过滤
 * 该注解在属性上,该model属性的父类失效
 * @author lf
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BaseCancelSuper {

}
