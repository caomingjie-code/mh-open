package com.javaoffers.base.kafka.core;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.Future;

/**
 * @author cmj
 * @Description TODO
 * @createTime 2021年07月05日 16:19:00
 */
public class KafkaProducerImpl implements KafkaProducer {

    org.apache.kafka.clients.producer.KafkaProducer kafkaProducer ;

    @Override
    public void send(String topic, Object key, Object msg) {
        ProducerRecord producerRecord = new ProducerRecord(topic,key, msg);
        kafkaProducer.send(producerRecord);
    }

    @Override
    public boolean sendWithAck(String topic, String key, Object msg) {
        ProducerRecord producerRecord = new ProducerRecord(topic,key, msg);
        Future<RecordMetadata> send = kafkaProducer.send(producerRecord);
        try {
           send.get();
           return true;
        }catch (Exception e){

        }
        return false;
    }
}
