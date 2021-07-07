package com.javaoffers.kafka.sample.service;

import com.javaoffers.kafka.sample.producer.OrderProducer;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author cmj
 * @Description TODO
 * @createTime 2021年07月05日 00:15:00
 */
@Service
@RequestMapping("/")
@ResponseBody
public class OrderService {

    @Resource
    OrderProducer orderProducer;

    @RequestMapping("orderproducer")
    public String orderproducer(){
        orderProducer.send("order-topic","orderId:12",""+System.nanoTime());
        orderProducer.closeProducer();
        return "ok";
    }
}
