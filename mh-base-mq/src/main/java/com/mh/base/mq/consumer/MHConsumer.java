package com.mh.base.mq.consumer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import org.springframework.amqp.utils.SerializationUtils;

import com.mh.base.mq.client.MHMQClient;
import com.mh.base.mq.msgdata.MQMsgImpl;
import com.mh.base.mq.msgdata.MqMsg;
import com.mh.base.utils.concurrent.ConcurrentHashSet;
import com.mh.base.utils.protostuff.ProtostuffUtils;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * 消费数据
 * @author cmj
 *
 */
public class MHConsumer extends Thread implements Consumer,Runnable{
	
	private Channel channel = null; 
	private Object bean = null;  //实例(非spring容器中的bean)
    private String queueName = null;
    private Method beanMethod = null ;//方法
    private Object springSingleBean = null; //spring 容器中的bean,为了与spring容器环境整合
	
	public MHConsumer(Channel channel, Object bean, String queueName, Method beanMethod,Object springSingleBean) {
		super();
		this.channel = channel;
		this.bean = bean;
		this.queueName = queueName;
		this.beanMethod = beanMethod;
		this.springSingleBean = springSingleBean;
	}

	@Override
	public void handleShutdownSignal(String consumerTag,
			ShutdownSignalException sig) {
		//System.out.println("关闭连接后处理");
	}
	
	@Override
	public void handleRecoverOk(String consumerTag) {
		//System.out.println("当信息在ack之前恢复成功后处理");
	}
	 

	
	@SuppressWarnings("unused")
	@Override
	public void handleDelivery(String arg0, Envelope env,
			BasicProperties bp, byte[] body) throws IOException {
		try {
			MQMsgImpl mm = ProtostuffUtils.decode(body, MQMsgImpl.class);		
			Object	data = mm.getMsg();
			beanMethod.setAccessible(true);
			//优先执行springSingleBean
			if(springSingleBean!=null) {
				beanMethod.invoke(springSingleBean, data);
			}else {
				beanMethod.invoke(bean, data);
			}
			
		} catch (Exception e) {
			//channel.basicAck(0, true);//手动认证
			e.printStackTrace();
			return;
		}
		channel.basicAck(0, true);//手动认证
		
	}
	
	@Override
	public void handleConsumeOk(String consumerTag) {
		//System.out.println("消费方法注册成功后处理"+consumerTag);
	}
	
	@Override
	public void handleCancelOk(String consumerTag) {
		//System.out.println("取消成功后处理");
	}
	
	@Override
	public void handleCancel(String consumerTag) throws IOException {
		//System.out.println("消息取消时（比如队列被删除）处理");
	}

	@Override
	public void run() {
		try {
			channel.basicConsume(this.queueName, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
