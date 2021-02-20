package com.javaoffers.test.mq.conf;

import com.javaoffers.base.mq.annotation.ListenerQueue;
import com.javaoffers.dao.DaoService;
import com.javaoffers.dao.EntryTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Service
public class TestQueue2 {
	private static Logger logger = LoggerFactory.getLogger(TestQueue2.class);// log4j记录日志

	@Resource
	DaoService daoService;

	@ListenerQueue(queues="testQueue")
	public void priceve2(Object obj) throws InterruptedException {
		logger.info("TestQueue2: "+obj);
		EntryTest entryTest = new EntryTest();
		entryTest.setId(Integer.parseInt(obj+""));
		entryTest.setValue(obj+"");
		daoService.save(entryTest);
		Thread.sleep(1500);
	}

	/**
	 * 批处理测试
	 * @param msgs
	 */
	@ListenerQueue(queues="testQueue",batchCount = 3,consumerCount = 3)
	private void patchConsumer(List<Object> msgs) throws InterruptedException {
		logger.info("patchConsumer: "+ Arrays.toString(msgs.toArray()));
		if(msgs!=null){
			for(Object obj : msgs){
				EntryTest entryTest = new EntryTest();
				entryTest.setId(Integer.parseInt(obj+""));
				entryTest.setValue(obj+"");
				daoService.save(entryTest);
			}
		}
		Thread.sleep(2000);
	}
}
