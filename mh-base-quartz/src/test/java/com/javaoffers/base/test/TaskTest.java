package com.javaoffers.base.test;

import com.javaoffers.base.quartz.annotation.BaseQuartz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@BaseQuartz(name="TaskTest")
public class TaskTest {
	private static Logger logger = LoggerFactory.getLogger(TaskTest.class);// log4j记录日志

	public void say(String name) throws Exception {
		logger.info("test任务已经被执行。。。。。。"+ name);
		Thread.sleep(2000);
	}
}
