package com.mh.base.eslog.consumer;

import com.mh.base.eslog.appender.RabbitMQAppender;
import com.mh.base.mq.annotation.ListenerQueue;
import org.springframework.stereotype.Component;

/**
 * @Description: 该消费者专门为eslog 消费, 包括自定义和默认
 * @Auther: create by cmj on 2020/8/18 20:19
 */
@Component
public class EsConsumer {

    @ListenerQueue(queues = RabbitMQAppender.ES_QUEUE)
    public void logDefaultConsumer(Object msg){
        System.out.println(msg);
    }

    public void logCustomConsumer(Object msg){

    }

}
