package com.mh.base.mq.client;

import java.io.Serializable;
import java.util.HashMap;

import javax.annotation.Resource;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mh.base.mq.msgconverter.MHMsgConverter;
import com.mh.base.mq.msgdata.MQMsgImpl;
import com.mh.base.mq.msgdata.MqMsg;

/**
 *  mhMQ 客户端，用于发送和接受消息,会初始化一个默认队列。
 * @author cmj
 *
 */

public class MHMQClient  {
	  
	private static final long serialVersionUID = 7600492377783562841L;
	
	private final AmqpAdmin amqpAdmin;  
	private final AmqpTemplate amqpTemplate;
	
	private static final String DEFAULT_EXCHANGE = "MHMQClientExchange"; //默认exchange
	private static final String DEFAULT_QUEUE = "MHMQClient";//默认队列名称
	private static final String ROUTING_KEY = "MHMQC_ROUTING_KEY";
	
	private final static String EXCAHNGE_NAME = "_MHEXCHANGE";//非默认的exchange 后缀
	private static final String ROUTING_KEY_NAME = "_MHMQC_ROUTING_KEY";//非默认的的routKey
	private static final String QUEUE_NAME = "";//非默认队列名称后缀
 
	
	
	@Autowired
	public MHMQClient(AmqpAdmin amqpAdmin, AmqpTemplate amqpTemplate) {
		this.amqpAdmin = amqpAdmin;
		amqpAdmin.declareQueue(new Queue(DEFAULT_QUEUE, true));
		HashMap<String, Object> headers = new HashMap<String, Object>();
		headers.put("defaultExchange", "defaultExchange");
		amqpAdmin.declareExchange( new DirectExchange(DEFAULT_EXCHANGE, true, false));
		amqpAdmin.declareBinding(new Binding(DEFAULT_QUEUE, DestinationType.QUEUE, DEFAULT_EXCHANGE, ROUTING_KEY, headers));
		RabbitTemplate amqpTemplate_ = (RabbitTemplate)amqpTemplate;
		amqpTemplate_.setMessageConverter(new MHMsgConverter());//设置默认转换器
		this.amqpTemplate = amqpTemplate_;
	}

	/**
	 * 发送到默认的队列中
	 * @param message
	 */
	public void sendMsg(Object message) {
		amqpTemplate.convertAndSend(DEFAULT_EXCHANGE,ROUTING_KEY,buildMsg(message));
	}
	
	
	/**
	 * 默认的队列中消费数据
	 */
	@SuppressWarnings("unused")
	public Object processMsg() {
		Object obj = amqpTemplate.receiveAndConvert(DEFAULT_QUEUE);
        if(obj!=null&&obj instanceof MqMsg) {
        	MqMsg mqMsg = (MqMsg) obj; 
        	Object msg = mqMsg.getMsg();
        	return msg;
        }
		return obj;
	}
	
	/**
	 * 发送到指定的队列中
	 * @param message
	 */
	public void sendMsg(String queueName,Object message) {
		amqpTemplate.convertAndSend(queueName+EXCAHNGE_NAME,queueName+ROUTING_KEY_NAME,buildMsg(message));
	}
	
	
	/**
	 * 从指定的队列名称中消费数据
	 */
	@SuppressWarnings("unused")
	public Object processMsg(String queueName) {
		Object obj = amqpTemplate.receiveAndConvert(queueName+QUEUE_NAME);
        if(obj!=null&&obj instanceof MqMsg) {
        	MqMsg mqMsg = (MqMsg) obj;
        	Object msg = mqMsg.getMsg();
        	return msg;
        }
		return obj;
	}
	
	
	
	
	/**
	 * 创建MQMsg用于保存数据
	 * @param obj
	 * @return
	 */
	private MQMsgImpl buildMsg(Object obj) {
		return new MQMsgImpl(obj);
	}



	public AmqpAdmin getAmqpAdmin() {
		return amqpAdmin;   
	}


	public AmqpTemplate getAmqpTemplate() {
		return amqpTemplate;
	}

	
	
	
	
}
