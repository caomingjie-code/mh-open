package com.mh.base.mq.msgdata;

import java.io.Serializable;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;

/**
 * springBoot封装的MQ会检查类是否实现了Serializable，如果实现了会帮你实例化。
 * SerializationUtils.serialize(object);
 * @author cmj
 *
 */
public class MQMsgImpl implements MqMsg, Serializable{
	
	Object msg = null; //默认数据

	public Object getMsg() {
		return msg;
	}

	public void setMsg(Object msg) {
		this.msg = msg;
	}

	public MQMsgImpl(Object msg) {
		super();
		this.msg = msg;
	}
}
