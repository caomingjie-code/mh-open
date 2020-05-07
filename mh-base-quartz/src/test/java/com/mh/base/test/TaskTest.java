package com.mh.base.test;

import com.mh.base.quartz.annotation.BaseQuartz;

@BaseQuartz(name="TaskTest")
public class TaskTest {

	public void say(String name) throws Exception {
		System.out.println("test任务已经被执行。。。。。。"+ name);
		Thread.sleep(2000);
	}
}
