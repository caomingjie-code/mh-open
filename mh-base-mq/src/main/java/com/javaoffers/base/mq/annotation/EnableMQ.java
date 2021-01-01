package com.javaoffers.base.mq.annotation;

import com.javaoffers.base.mq.client.MHMQClient;
import com.javaoffers.base.mq.config.MHMQConifg;
import com.javaoffers.base.mq.init.QueueInit;
import com.javaoffers.base.mq.utils.MHMQUtilds;
import org.springframework.context.annotation.Import;

/**
 * @Description: 启动注解
 * @Auther: create by cmj on 2020/10/30 10:05
 */
@Import({MHMQConifg.class, MHMQClient.class, QueueInit.class, MHMQUtilds.class})
public @interface EnableMQ {
}
