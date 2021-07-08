package com.javaoffers.base.kafka.anno;

import java.lang.annotation.*;

/**
 * @Description: 监听主题
 * @Auther: create by cmj on 2021/7/8 20:42
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ListenTopic {
}
