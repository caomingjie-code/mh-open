package com.javaoffers.kafka.sample.producer;

import com.javaoffers.base.kafka.anno.EnableKafkaProducer;
import com.javaoffers.base.kafka.core.KafkaProducer;

/**
 * @author cmj
 * @Description TODO
 * @createTime 2021年07月04日 23:03:00
 */
@EnableKafkaProducer("order-topic")
public interface OrderProducer extends KafkaProducer {
}
