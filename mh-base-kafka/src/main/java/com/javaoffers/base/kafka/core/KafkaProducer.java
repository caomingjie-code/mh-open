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

    /**
     * 此方法不要用于并发环境下，因为会导致大量的链接创建与销毁，性能及其底下，有可能会出现链接被耗尽（生产者和broker都有可能出现这种情况）,
     * 导致正常的链接不可用。 例如：[Producer clientId=producer-1110] Connection to node 0 could not be established. Broker may not be available.
     * 严重情况下：可能会把kafka broker 搞崩溃，因为打开的文件描述符太多。
     */
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
