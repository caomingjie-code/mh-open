package com.mh.base.test.config;

import com.mh.base.mq.annotation.ListenerQueue;

import java.util.Arrays;
import java.util.List;

public class TestQueue2 {

	@ListenerQueue(queues="testQueue")
	public void priceve2(Object obj) throws InterruptedException {
		System.out.println("TestQueue2: "+obj);
		Thread.sleep(1500);
	}

	/**
	 * 批处理测试
	 * @param msgs
	 */
	@ListenerQueue(queues="testQueue" ,isBatch = true,batchCount = 3)
	private void patchConsumer(List<Object> msgs) throws InterruptedException {
		System.out.println("patchConsumer: "+ Arrays.toString(msgs.toArray()));
		Thread.sleep(2000);
	}
}
