package com.javaoffers.base.kafka.core;

/**
 * @author cmj
 * @Description kafka生产接口，
 * @createTime 2021年07月03日 20:09:00
 */
public interface KafkaProducer {


    public void send(String topic, Object key, Object msg);

    public boolean sendWithAck(String topic, String key, Object msg) ;


}
