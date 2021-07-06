package com.javaoffers.base.kafka.core;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;

/**
 * @author cmj
 * @Description kafka 生产者，继承原生生产者是为了争强某些功能（目前还没有实现。不过这样做是为了以后扩展）
 * @createTime 2021年07月05日 16:19:00
 */
public class KafkaProducerImpl extends org.apache.kafka.clients.producer.KafkaProducer<Object,Object> implements KafkaProducer{

    Logger logger;

    public KafkaProducerImpl(Map configs) {
        super(configs);
    }

    public KafkaProducerImpl(Map configs, Serializer keySerializer, Serializer valueSerializer) {
        super(configs, keySerializer, valueSerializer);
    }

    public KafkaProducerImpl(Properties properties) {
        super(properties);
    }

    public KafkaProducerImpl(Properties properties, Serializer keySerializer, Serializer valueSerializer) {
        super(properties, keySerializer, valueSerializer);
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void send(String topic, Object key, Object msg){
        send(new ProducerRecord<Object,Object>(topic,key,msg));
    }

    public boolean sendWithAck(String topic, String key, Object msg) {
        try {
            Future<RecordMetadata> send = send(new ProducerRecord<Object, Object>(topic, key, msg));
            RecordMetadata recordMetadata = send.get();
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            return false;
        }
        return true;
    }
}
