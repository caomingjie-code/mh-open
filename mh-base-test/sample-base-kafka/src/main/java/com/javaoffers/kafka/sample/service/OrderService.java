package com.javaoffers.kafka.sample.service;

import com.javaoffers.kafka.sample.producer.OrderProducer;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

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
        long startTime = System.nanoTime();
        orderProducer.send("order-topic","orderId:12",""+System.nanoTime());
        long endTime = System.nanoTime();
        return "消耗了"+TimeUnit.NANOSECONDS.toSeconds(endTime-startTime)+ "s";
    }

    @RequestMapping("orderproducerWithCloseProducer")
    public String orderproducerWithCloseProducer(){
        long startTime = System.nanoTime();
        orderProducer.send("order-topic","orderId:12",""+System.nanoTime());
        /**
         * 此方法不要用于并发环境下，因为会导致大量的链接创建与销毁，性能及其底下，有可能会出现链接被耗尽（生产者和broker都有可能出现这种情况）,
         * 导致正常的链接不可用。 例如：[Producer clientId=producer-1110] Connection to node 0 could not be established. Broker may not be available.
         * 更严重的有可能会把kafka broker 搞崩溃
         */
        orderProducer.closeProducer();
        long endTime = System.nanoTime();
        return "消耗了"+TimeUnit.NANOSECONDS.toSeconds(endTime-startTime)+ "s";
    }
}
