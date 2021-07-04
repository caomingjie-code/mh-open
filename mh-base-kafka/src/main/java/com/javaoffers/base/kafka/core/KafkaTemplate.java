package com.javaoffers.base.kafka.core;

/**
 * @author cmj
 * @Description kfka 模板，主要提供生产方法，以及kakfaServer数据
 * @createTime 2021年07月03日 22:51:00
 */
public interface KafkaTemplate {

    //发送信息
    void send(Object msg,String topic);

    boolean sendWithAck(Object msg,String topic);



}
