package com.javaoffers.mh.db.router.annotation;

import com.javaoffers.mh.db.router.datasource.BaseComboPooledDataSource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据源切换，该注解默认切master(默认)
 * create by cmj
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSourceRoute {
    String value() default BaseComboPooledDataSource.DEFAULT_ROUTER;
}
