package com.javaoffers.base.mq.init;

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import com.javaoffers.base.mq.annotation.ListenerQueue;
import com.javaoffers.base.mq.config.MQProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.javaoffers.base.mq.client.MHMQClient;
import com.javaoffers.base.mq.consumer.MHConsumer;
import com.javaoffers.base.mq.process.ScanQueueClazz;
import com.javaoffers.base.mq.process.ScanQueueClazz.QueueBeanDefinition;
import com.rabbitmq.client.Channel;

/**
 * 初始化队列数据
 * @author cmj
 *
 */

public class QueueInit implements ApplicationContextAware{
	private static final Log logger = LogFactory.getLog(QueueInit.class);
	@Resource
	ScanQueueClazz scanQueueClazz;
	@Resource
	private MHMQClient clinet;
	@Resource
	private MQProperties mqProperties;
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
				logger.info("初始化队列数据");
				initQueueStatus =false;
				ConcurrentHashMap<String,Class> queues = scanQueueClazz.getQueues();//获取所有队列信息
				//设计原理每一个队列queue 对应一个 exchange

				Enumeration<String> keys = queues.keys();
				if(keys!=null) {
					for(;keys.hasMoreElements();) {
						String queueName = keys.nextElement();
						if(StringUtils.isNotBlank(queueName)) {
							createQueue(queueName);
						}
					}
				}
			}
		}
	}

	/**
	 * 创建队列
	 * @param queueName
	 */
	public void createQueue(String queueName) {
		AmqpAdmin amqpAdmin = clinet.getAmqpAdmin();
		queueName = queueName+QUEUE_NAME;
		String exchangeName = queueName+EXCAHNGE_NAME;
		amqpAdmin.declareQueue(new Queue(queueName, true)); //声明一个队列
		amqpAdmin.declareExchange( new DirectExchange(exchangeName, true, false));
		HashMap<String, Object> headers = new HashMap<String, Object>();
		headers.put("defaultExchange", "defaultExchange");
		amqpAdmin.declareBinding(new Binding(queueName, DestinationType.QUEUE, exchangeName, queueName+ROUTING_KEY_NAME, headers));
	}


	/**
	 * 初始化队列
	 * @throws Exception 
	 */
	private void buildQueuesListener(ApplicationContext applicationContext) throws Exception {
		synchronized (lock) {
			if(initQueueListenerStatus) {
				logger.info("初始化队列数据");
				initQueueListenerStatus =false;
				ConcurrentHashMap<String, QueueBeanDefinition> queuesListener = ScanQueueClazz.getlistenerQueueMethods();//获取所有队列信息
				//设计原理每一个队列queue 对应一个 exchange
				AmqpAdmin amqpAdmin = clinet.getAmqpAdmin();
				Enumeration<String> keys = queuesListener.keys();
				if(keys!=null) {
					for(;keys.hasMoreElements();) {
						String queueListenerName = keys.nextElement();//类名+"_"+方法
						QueueBeanDefinition qbd = queuesListener.get(queueListenerName);
						Method listenerMehod = qbd.getListenerMehod();
						ListenerQueue listenerQueue = listenerMehod.getDeclaredAnnotation(ListenerQueue.class);
						boolean batch = false;
						int batchCount = 1;
						int consumerCount = 1;
						if(listenerQueue!=null){
							batchCount = listenerQueue.batchCount()<=1?1:listenerQueue.batchCount();
							batch = batchCount>1?true:false;
							consumerCount = listenerQueue.consumerCount()<=1?1:listenerQueue.consumerCount();
						}

						logger.info("监听队列： "+qbd.getListenerQueueName());
						if(StringUtils.isNotBlank(qbd.getListenerQueueName())) {
							Channel channel = getChannel();
							Object springSingleBean = null;
							try {
								springSingleBean = applicationContext.getBean(qbd.getBeanClazz()); //从spring容器中获取bean
							} catch (NoSuchBeanDefinitionException e) {
								springSingleBean = null;
							}

							//监听队列,生成消费者
							listennerQueues(qbd, listenerQueue, batch, batchCount, consumerCount, channel, springSingleBean);

						}
					}
				}
			}
		}
	}

	/**
	 * 获取channel
	 * @return
	 */
	public Channel getChannel() {
		RabbitTemplate amqpTemplate = (RabbitTemplate)clinet.getAmqpTemplate();
		Connection connection = getMQConnect(amqpTemplate);
		return connection.createChannel(false);
	}

	//监听队列,生成消费者
	private void listennerQueues(QueueBeanDefinition qbd, ListenerQueue listenerQueue, boolean batch, int batchCount, int consumerCount, Channel channel, Object springSingleBean) throws InstantiationException, IllegalAccessException {
		String[] queues = listenerQueue.queues();
		if(queues!=null&&queues.length>0){
			for(String qn : queues){
				for(;consumerCount>0;){
					consumerCount--;
					MHConsumer mhConsumer = new MHConsumer(mqProperties,channel, qbd.getBeanClazz().newInstance(), qn, qbd.getListenerMehod(),springSingleBean,batch,batchCount);
					mhConsumer.start();
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
