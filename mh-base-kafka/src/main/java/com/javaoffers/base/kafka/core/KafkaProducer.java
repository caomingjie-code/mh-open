package com.javaoffers.base.kafka.core;

import org.apache.kafka.clients.producer.Producer;

import java.util.concurrent.TimeUnit;

/**
 * @author cmj
 * @Description kafka生产接口，
 * @createTime 2021年07月03日 20:09:00
 */
public interface KafkaProducer extends Producer<Object, Object> {


    public void send(String topic, Object key, Object msg);

    public boolean sendWithAck(String topic, String key, Object msg) ;

    public void closeProducer();

    /**
     * 推荐使用closeProducer
     * 该方法只会调用flush,并不会真正的关闭
     */
    @Deprecated
    void close();

    /**
     * 推荐使用closeProducer
     * 该方法只会调用flush,并不会真正的关闭
     */
    @Deprecated
    void close(long timeout, TimeUnit unit);
}
