package com.javaoffers.kafka.sample.service;

import com.javaoffers.kafka.sample.producer.OrderProducer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author cmj
 * @Description TODO
 * @createTime 2021年07月05日 00:15:00
 */
@Service
public class OrderService {

    @Resource
    OrderProducer orderProducer;
}
