package com.mh.base.mq.annotation;

import com.mh.base.mq.config.MHMQConifg;
import org.springframework.context.annotation.Import;

/**
 * @Description: 启动注解
 * @Auther: create by cmj on 2020/10/30 10:05
 */
@Import(MHMQConifg.class)
public @interface EnableMQ {
}
