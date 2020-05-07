package com.mh.base.test.config;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.mh.base.mq.annotation.CreateQueue;
import com.mh.base.mq.annotation.ListenerQueue;

@CreateQueue(name="testQueue")
public class TestQueue { 
	
	@ListenerQueue(queues="testQueue")
	public void pricive(Object msg) {
		System.out.println("TestQueue: "+msg);
	}
	
	@ListenerQueue(queues="testQueue")
	public void pricive2(Object msg) {
		System.out.println("TestQueue2 String: : "+msg);
	}
	
}
