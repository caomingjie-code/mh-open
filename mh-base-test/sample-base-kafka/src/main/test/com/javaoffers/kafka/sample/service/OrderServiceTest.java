package com.javaoffers.kafka.sample.service;

import com.javaoffers.base.common.log.LogUtils;
import com.javaoffers.base.common.rpc.HttpClientUtils;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.*;

/**
 * @Description:
 * @Auther: create by cmj on 2021/7/8 13:32
 */
public class OrderServiceTest {

    /**
     * 模拟10秒并发
     * @throws Exception
     */
    @Test
    public void orderproducer() throws Exception{
        long startTime = System.nanoTime();
        int secondTest = 10; //测试10秒并发
        AtomicLong count = new AtomicLong(0);
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1000);
        Thread te = new Thread(()->{
            try {
                HttpClientUtils.getData("http://localhost:8080/orderproducer");
                count.incrementAndGet();
            }catch (Exception e){
                e.printStackTrace();
            }

        });
        for(;;){
            if(TimeUnit.NANOSECONDS.toSeconds(System.nanoTime()-startTime)>secondTest){break;}
            fixedThreadPool.execute(te);
        }

        LogUtils.printLog("总共发送了" + count.get());

    }
}