package com.mh.base.mq.consumer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.mh.base.common.log.LogUtils;
import com.mh.base.mq.config.MQProperties;
import com.rabbitmq.client.*;
import com.mh.base.mq.msgdata.MQMsgImpl;
import com.mh.base.common.protostuff.ProtostuffUtils;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * 消费数据
 * @author cmj
 *
 */
public class MHConsumer extends Thread implements Consumer,Runnable{
	private MQProperties mqProperties;//mq的配置文件
	private Channel channel = null;
	private Object bean = null;  //实例(非spring容器中的bean)
    private String queueName = null;
    private Method beanMethod = null ;//方法
    private Object springSingleBean = null; //spring 容器中的bean,为了与spring容器环境整合
	private boolean isBatch = false; //是否是批处理消费
	private int batchCount = 1;//默认批处理数量

	public MHConsumer(MQProperties mqProperties,Channel channel, Object bean, String queueName, Method beanMethod,Object springSingleBean,boolean isBatch,int batchCount) {
		super();
		this.channel = channel;
		this.bean = bean;
		this.queueName = queueName;
		this.beanMethod = beanMethod;
		this.springSingleBean = springSingleBean;
		this.isBatch = isBatch;
		this.batchCount = batchCount;
		this.mqProperties = mqProperties;
	}

	@Override
	public void handleShutdownSignal(String consumerTag,
			ShutdownSignalException sig) {
		//logger.info("关闭连接后处理");
	}
	
	@Override
	public void handleRecoverOk(String consumerTag) {
		//logger.info("当信息在ack之前恢复成功后处理");
	}
	 

	
	@SuppressWarnings("unused")
	@Override
	public void handleDelivery(String arg0, Envelope env,
			BasicProperties bp, byte[] body) throws IOException {
		Set<Long> deliverTag = new  HashSet();
		try {
			//获取消费数据
			Object deliveryData = getMsgObject(body);

			//判断是否是批处理
			if(this.isBatch){
				//声明 批处理的数据和 消息的tag 的集合
				ArrayList<Object> batchDatas = new ArrayList<>();
				//构建patch Data
				deliverTag = buildPatchDeliveryDatas(deliveryData,env, batchDatas);
				//优先执行springSingleBean
				invokeMethod(batchDatas);
			}else{
				//打印当前的 delivery tag
				if(mqProperties.isPrintDeliveryTag()){
					LogUtils.printLog("channel id : "+System.identityHashCode(channel)+", delivery tag data:  "+env.getDeliveryTag());
				}
				//优先执行springSingleBean
				invokeMethod(deliveryData);

				deliverTag.add(env.getDeliveryTag());
			}
		} catch (Exception e) {
			//channel.basicAck(0, true);//手动认证
			e.printStackTrace();
			return;
		}
		for(Long tag : deliverTag){
			//true 认证tag之前的所有, false 只认证当前的tag
			channel.basicAck(tag, false);//手动认证当前tag
		}

	}

	/**
	 * 批处理 是 push和pull的一种结合使用案例
	 * @param deliveryData
	 * @param env
	 * @param batchDatas
	 * @throws IOException
	 */
	private Set<Long> buildPatchDeliveryDatas(Object deliveryData, Envelope env, ArrayList<Object> batchDatas) throws IOException {

		//消息tags
		Set<Long> patchDelivertTags = new HashSet<>();

		//整理push
		builPush(deliveryData, env, batchDatas, patchDelivertTags);

		//整理pull
		buildPull(batchDatas, patchDelivertTags);

		//打印即将要被消费的消息tags
		printPatchDeliveryTags(patchDelivertTags);

		return patchDelivertTags;
	}

	/**
	 * 打印即将要被消费的消息tags
	 * @param patchDelivertTags
	 */
	private void printPatchDeliveryTags(Set<Long> patchDelivertTags) {
		if(mqProperties.isPrintDeliveryTag()){
			for(Long deiveryTag : patchDelivertTags){
				LogUtils.printLog("channel id : "+System.identityHashCode(channel)+", patch delivery tag data:  "+deiveryTag);
			}
		}
	}

	/**
	 * 整理push
	 * @param deliveryData
	 * @param env
	 * @param batchDatas
	 * @param patchDelivertTags
	 */
	private void builPush(Object deliveryData, Envelope env, ArrayList<Object> batchDatas, Set<Long> patchDelivertTags) {
		patchDelivertTags.add(env.getDeliveryTag());
		batchDatas.add(deliveryData);
	}

	/**
	 * 整理pull
	 * @param batchDatas
	 * @param patchDelivertTags
	 * @throws IOException
	 */
	private void buildPull(ArrayList<Object> batchDatas, Set<Long> patchDelivertTags) throws IOException {
		for(int i=1; i<batchCount;i++){ //i 从1 开始,因为 deliveryData 也算一个.如果batchCount 是1 ,则默认 就使用 deliveryData
			//开始批处理整理数据
			GetResponse getResponse = channel.basicGet(queueName, false);
			if(getResponse!=null){
				byte[] body1 = getResponse.getBody();
				Object msgObject = getMsgObject(body1);
				batchDatas.add(msgObject);
				patchDelivertTags.add(getResponse.getEnvelope().getDeliveryTag());
			}
		}
	}

	private void invokeMethod(Object data) throws IllegalAccessException, InvocationTargetException {
		if(springSingleBean!=null) {
			beanMethod.invoke(springSingleBean, data);
		}else {
			beanMethod.invoke(bean, data);
		}
	}

	private Object getMsgObject(byte[] body) {
		MQMsgImpl mm = ProtostuffUtils.decode(body, MQMsgImpl.class);
		Object	data = mm.getMsg();
		beanMethod.setAccessible(true);
		return data;
	}

	@Override
	public void handleConsumeOk(String consumerTag) {
		//logger.info("消费方法注册成功后处理"+consumerTag);
	}
	
	@Override
	public void handleCancelOk(String consumerTag) {
		//logger.info("取消成功后处理");
	}
	
	@Override
	public void handleCancel(String consumerTag) throws IOException {
		//logger.info("消息取消时（比如队列被删除）处理");
	}

	@Override
	public void run() {
		try {
			channel.basicQos(1); //每次欲读一条
			channel.basicConsume(this.queueName, this); //监听队列

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
