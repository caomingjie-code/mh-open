package com.javaoffers.base.kafka.core;

import com.javaoffers.base.kafka.anno.EnableKafkaProducer;
import com.javaoffers.base.kafka.config.KafkaProducerProperties;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.annotation.AnnotationUtils;
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

/**
 * @author cmj
 * @Description TODO
 * @createTime 2021年07月04日 23:39:00
 */
public class KafkaProducerInterceptor implements MethodInterceptor {

    private AnnotationMetadata metadata;

    private Environment environment;

    private KafkaProducerProperties producer;


    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        System.out.println("开始生产消息！！！");
        Properties kafkaProducer = new Properties();
        MultiValueMap<String, Object> allAnnotationAttributes = metadata.getAllAnnotationAttributes(EnableKafkaProducer.class.getName());
        Map<String, Object> kafkaProducerProperties = allAnnotationAttributes.toSingleValueMap();
        Set<String> kafkaProKeys = kafkaProducerProperties.keySet();
        Map<String, Object> producerMap = producer2Map(producer);
        for(String kafkaProKey : kafkaProKeys){
            //与yml文件进行合并参数，注解参数高于yml文件的参数。
            String propertyV = producerMap.get(kafkaProKey).toString();
            if(!StringUtils.isEmpty(allAnnotationAttributes.get(kafkaProKey))){ //如果注解中的信息不为空
                propertyV = allAnnotationAttributes.get(kafkaProKey).toString();//优先取注解中的属性
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
            kafkaProducer.put(kafkaKey,propertyV);
        }


        Object[] arguments = invocation.getArguments();
        Method method = invocation.getMethod();


        return "";

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

    public KafkaProducerProperties getProducer() {
        return producer;
    }

    public void setProducer(KafkaProducerProperties producer) {
        this.producer = producer;
    }
}
