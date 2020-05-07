package com.mh.base.test.config;

import com.mh.base.mq.annotation.ListenerQueue;

public class TestQueue2 {

	@ListenerQueue(queues="testQueue")
	public void priceve2(Object obj) {
		System.out.println("TestQueue2: "+obj);
	}
}
