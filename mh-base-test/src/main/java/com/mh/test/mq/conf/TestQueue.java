package com.mh.test.mq.conf;

import com.mh.base.mq.init.QueueInit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import com.mh.base.mq.annotation.CreateQueue;
import com.mh.base.mq.annotation.ListenerQueue;

@CreateQueue(name="testQueue")
public class TestQueue {
	private static final Log logger = LogFactory.getLog(TestQueue.class);

	//@ListenerQueue(queues="testQueue")
	public void pricive(Object msg) {
		logger.info("TestQueue: "+msg);
	}
	
	//@ListenerQueue(queues="testQueue")
	public void pricive2(Object msg) {
		logger.info("TestQueue2 String: : "+msg);
	}
	
}
