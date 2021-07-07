package com.javaoffers.base.kafka.core;

import com.javaoffers.base.kafka.anno.EnableKafkaProducer;
import com.javaoffers.base.kafka.config.KafkaProducerProperties;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author cmj
 * @Description TODO
 * @createTime 2021年07月04日 23:39:00
 */
public class KafkaProducerInterceptor implements MethodInterceptor {

    private AnnotationMetadata metadata;

    private Environment environment;

    private KafkaProducerProperties producerProperties;

    private KafkaProducerImpl producer; //原生 生产者

    private String logClassName; //被代理的class类的名称

    private int sendCount = 250000; //发送并发量

    private Semaphore sendCounter = new Semaphore(sendCount);//当前时间并发统计量

    private AtomicLong closeCounter = new AtomicLong(0);



    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        System.out.println("开始生产消息！！！");

        Method method = invocation.getMethod();

        Object[] arguments = invocation.getArguments();

        Object invoke = null;

        if(!method.getName().equals("closeProducer")){ //非关闭方法
            try {
                sendCounter.acquire();

                initKafkaProducer();

                invoke = method.invoke(producer, arguments);

            }catch (Exception e){
                throw e;
            }finally {
                sendCounter.release();
            }
        }else {
            synchronized (sendCounter){
                while (true){
                    sendCounter.acquire();
                    long closeCount = closeCounter.incrementAndGet();
                    if(closeCount==sendCount){
                        try {
                            //关闭生产
                            if(producer!=null){
                                producer.close();
                            }
                            producer = null;
                        }catch (Exception e){
                            throw  e;
                        }finally {
                            //开始释放令牌
                            for(long c = closeCounter.get(); c >0 ;c--){
                                sendCounter.release();
                            }
                            closeCounter.set(0);//恢复原值
                            return null; //跳出循环
                        }
                    }
                }
            }
        }

        return invoke;

    }

    /**
     * 初始化kafka producer，只初始化一次
     * @throws Exception
     */
    private void initKafkaProducer() throws Exception {
        if(producer==null){
            synchronized (this){
                if(producer == null){
                    Properties kafkaProducer = new Properties();

                    MultiValueMap<String, Object> allAnnotationAttributes = metadata.getAllAnnotationAttributes(EnableKafkaProducer.class.getName());

                    Map<String, Object> kafkaProducerPropertiesOfAnnotation = allAnnotationAttributes.toSingleValueMap();

                    Map<String, Object> producerMapOfProperty = producer2Map(producerProperties);

                    Set<String> kafkaProKeys = producerMapOfProperty.keySet();

                    getKafkaProducerParams(kafkaProducer, kafkaProducerPropertiesOfAnnotation, kafkaProKeys, producerMapOfProperty);

                    KafkaProducerImpl producer = new KafkaProducerImpl(kafkaProducer);

                    producer.setLogger(LoggerFactory.getLogger(logClassName));

                    this.producer = producer;
                }
            }
        }
    }

    /**
     * 获取kafka 生产者参数
     * @param kafkaProducerProperties
     * @param kafkaProducerPropertiesOfAnnotation
     * @param kafkaProKeys
     * @param producerMapOfProperty
     */
    private void getKafkaProducerParams(Properties kafkaProducerProperties, Map<String, Object> kafkaProducerPropertiesOfAnnotation, Set<String> kafkaProKeys, Map<String, Object> producerMapOfProperty) {
        for(String kafkaProKey : kafkaProKeys){

            //与yml文件进行合并参数，注解参数高于yml文件的参数。
            String propertyV = producerMapOfProperty.get(kafkaProKey)==null?"":producerMapOfProperty.get(kafkaProKey).toString();

            if(!StringUtils.isEmpty( kafkaProducerPropertiesOfAnnotation.get(kafkaProKey) )){ //如果注解中的信息不为空

                propertyV = kafkaProducerPropertiesOfAnnotation.get(kafkaProKey).toString();//优先取注解中的属性

            }
            if(StringUtils.isEmpty(propertyV)){ //如果属性为空则 继续遍历
                continue;
            }

            String[] kafkaProKeySplit = kafkaProKey.split("");

            StringBuilder kafkaKey = new StringBuilder();

            for(String key : kafkaProKeySplit){

                if(key.getBytes()[0]>=65&&key.getBytes()[0]<=90){ //大写字母

                    kafkaKey.append(".");

                    kafkaKey.append(key.toLowerCase());

                }else {

                    kafkaKey.append(key);

                }
            }

            kafkaProducerProperties.put(kafkaKey.toString(),propertyV.toString());
        }
    }

    public  Map<String,Object> producer2Map(KafkaProducerProperties producer) throws Exception{
        Field[] kafkaFields = producer.getClass().getDeclaredFields();
        HashMap<String, Object> result = new HashMap<>();
        try {
            if(kafkaFields!=null){
                for(Field f :  kafkaFields){
                    f.setAccessible(true);
                    String name = f.getName();
                    Object value = f.get(producer);
                    result.put(name,value);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            throw  e;
        }
        return result;
    }



    public KafkaProducerInterceptor() {
    }

    public AnnotationMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(AnnotationMetadata metadata) {
        this.metadata = metadata;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public KafkaProducerProperties getProducerProperties() {
        return producerProperties;
    }

    public void setProducerProperties(KafkaProducerProperties producerProperties) {
        this.producerProperties = producerProperties;
    }

    public String getLogClassName() {
        return logClassName;
    }

    public void setLogClassName(String logClassName) {
        this.logClassName = logClassName;
    }
}
