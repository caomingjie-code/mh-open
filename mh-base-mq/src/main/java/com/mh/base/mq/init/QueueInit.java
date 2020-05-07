package com.mh.base.mq.init;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;

import javax.annotation.Resource;
import javax.sql.rowset.spi.SyncResolver;

import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.ReceiveAndReplyCallback;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import com.mh.base.mq.client.MHMQClient;
import com.mh.base.mq.consumer.MHConsumer;
import com.mh.base.mq.process.ScanQueueClazz;
import com.mh.base.mq.process.ScanQueueClazz.QueueBeanDefinition;
import com.mh.base.utils.log.BaseLog;
import com.rabbitmq.client.Channel;

/**
 * 初始化队列数据
 * @author cmj
 *
 */
@Service
public class QueueInit implements ApplicationContextAware{
 
	@Resource
	private MHMQClient clinet;
	private final static String EXCAHNGE_NAME = "_MHEXCHANGE";
	private static final String QUEUE_NAME = "";//默认队列名称后缀
	private static final String ROUTING_KEY_NAME = "_MHMQC_ROUTING_KEY";
	private static volatile boolean initQueueStatus = true;
	private static volatile boolean initQueueListenerStatus = true;
	private static volatile Object lock = new Object();
	private static final ThreadLocal<Connection> cnn = new ThreadLocal<Connection>();
	
	
	public void init(ApplicationContext applicationContext) throws Exception {
		buildQueues(); //初始化队列
		buildQueuesListener(applicationContext);//初始化监听器
	}
	
	/**
	 * 初始化队列
	 */
	private void buildQueues() {
		synchronized (lock) {
			if(initQueueStatus) {
				BaseLog.printLog("初始化队列数据");
				initQueueStatus =false;
				ConcurrentHashMap<String,Class> queues = ScanQueueClazz.getQueues();//获取所有队列信息
				//设计原理每一个队列queue 对应一个 exchange
				AmqpAdmin amqpAdmin = clinet.getAmqpAdmin();
				Enumeration<String> keys = queues.keys();
				if(keys!=null) {
					for(;keys.hasMoreElements();) {
						String queueName = keys.nextElement();
						if(StringUtils.isNotBlank(queueName)) {
							queueName = queueName+QUEUE_NAME;
							String exchangeName = queueName+EXCAHNGE_NAME;
							amqpAdmin.declareQueue(new Queue(queueName, true)); //声明一个队列
							amqpAdmin.declareExchange( new DirectExchange(exchangeName, true, false));
							HashMap<String, Object> headers = new HashMap<String, Object>();
							headers.put("defaultExchange", "defaultExchange");
							amqpAdmin.declareBinding(new Binding(queueName, DestinationType.QUEUE, exchangeName, queueName+ROUTING_KEY_NAME, headers));
						}
					}
				}
			}
		}
	}
	
	/**
	 * 初始化队列
	 * @throws Exception 
	 */
	private void buildQueuesListener(ApplicationContext applicationContext) throws Exception {
		synchronized (lock) {
			if(initQueueListenerStatus) {
				BaseLog.printLog("初始化队列数据");
				initQueueListenerStatus =false;
				ConcurrentHashMap<String, QueueBeanDefinition> queuesListener = ScanQueueClazz.getlistenerQueueMethods();//获取所有队列信息
				//设计原理每一个队列queue 对应一个 exchange
				AmqpAdmin amqpAdmin = clinet.getAmqpAdmin();
				RabbitTemplate amqpTemplate = (RabbitTemplate)clinet.getAmqpTemplate();
				Enumeration<String> keys = queuesListener.keys();
				if(keys!=null) {
					for(;keys.hasMoreElements();) {
						String queueListenerName = keys.nextElement();//类名+"_"+方法
						QueueBeanDefinition qbd = queuesListener.get(queueListenerName);
						
						System.out.println("监听队列： "+qbd.getListenerQueueName());
						if(StringUtils.isNotBlank(qbd.getListenerQueueName())) {
							Connection connection = getMQConnect(amqpTemplate);
							Channel channel = connection.createChannel(false);//不要开启spring提供的事务机制，会导致ack手动认证不通过
							Object springSingleBean = null;
							try {
								springSingleBean = applicationContext.getBean(qbd.getBeanClazz()); //从spring容器中获取bean
							} catch (NoSuchBeanDefinitionException e) {
								springSingleBean = null;
							}
							MHConsumer mhConsumer = new MHConsumer(channel, qbd.getBeanClazz().newInstance(), qbd.getListenerQueueName(), qbd.getListenerMehod(),springSingleBean);
							mhConsumer.start();
						}
					}
				}
			}
		}
	}

	/**
	 * 获取当前MQ的连接，
	 * @param amqpTemplate
	 * @return
	 */
	private Connection getMQConnect(RabbitTemplate amqpTemplate) {
		Connection connection = cnn.get();//获取当前线程的连接
		if(connection==null) {
			connection = amqpTemplate.getConnectionFactory().createConnection();
			cnn.set(connection);
		}
		return connection;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		try {
			init(applicationContext);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
