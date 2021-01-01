package com.javaoffers.base.annotation.model;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于model数据的过滤
 * 该注解在属性上,该属性失效(该属性的父类属性同样会失效)
 * @author lf
 *
 */
@Target({ElementType.FIELD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BaseCancel {

}
