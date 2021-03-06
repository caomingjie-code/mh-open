package com.javaoffers.test.mq.conf;

import com.javaoffers.base.mq.annotation.CreateQueue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


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
