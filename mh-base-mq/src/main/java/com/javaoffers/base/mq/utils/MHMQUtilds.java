package com.javaoffers.base.mq.utils;

import javax.annotation.Resource;

import com.javaoffers.base.mq.client.MHMQClient;

/**
 * 创建mq的工具类，直接发送消息和接收消息,该工具类必须在spring容器创建完成后使用。
 * 防止mhmq在没有被赋值的情况下使用回报null异常。
 * @author cmj
 *
 */

public class MHMQUtilds {
	
	private static MHMQClient mhmq; 

	@Resource
	public void setMHMQClinet(MHMQClient client) {
		mhmq = client;
	}
	
	public static void sendMsg(Object dataMsg) {
		if(dataMsg ==null) {
			dataMsg = "";
		}
		mhmq.sendMsg(dataMsg);
	}
	
	public static Object processMsg() {
		return mhmq.processMsg();
	}
	
	
	public static void sendMsg(Object dataMsg,String queueName) {
		if(dataMsg ==null) {
			dataMsg = "";
		}
		mhmq.sendMsg(queueName,dataMsg);
	}
	
	public static Object processMsg(String queueName) {
		return mhmq.processMsg(queueName);
	}
	
	

}
