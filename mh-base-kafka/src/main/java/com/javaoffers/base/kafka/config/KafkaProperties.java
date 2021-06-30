package com.javaoffers.base.kafka.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description: kafka的配置类
 * @Auther: create by cmj on 2020/8/11 13:43
 */
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {

    //消费者
    KafkaConsumerProperties consumer = new KafkaConsumerProperties();

    //生产者
    KafkaProducerProperties producer = new KafkaProducerProperties();

    public KafkaConsumerProperties getConsumer() {
        return consumer;
    }

    public void setConsumer(KafkaConsumerProperties consumer) {
        this.consumer = consumer;
    }

    public KafkaProducerProperties getProducer() {
        return producer;
    }

    public void setProducer(KafkaProducerProperties producer) {
        this.producer = producer;
    }
}
