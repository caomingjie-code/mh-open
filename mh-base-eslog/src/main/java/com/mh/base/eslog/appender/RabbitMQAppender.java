package com.mh.base.eslog.appender;

import ch.qos.logback.core.OutputStreamAppender;
import com.mh.base.eslog.client.EsLogClient;
import com.mh.base.eslog.conf.AutoConfigEslog;
import com.mh.base.eslog.conf.EsLogProperties;
import com.mh.base.eslog.consumer.EsConsumer;
import com.mh.base.mq.annotation.CreateQueue;
import com.mh.base.mq.client.MHMQClient;
import com.mh.base.mq.config.MQProperties;
import com.mh.base.mq.consumer.MHConsumer;
import com.mh.base.mq.init.QueueInit;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @Description: 自定义logbak appender
 * @Auther: create by cmj on 2020/8/18 09:45
 */
@ConditionalOnBean(AutoConfigEslog.class)
@CreateQueue(name = RabbitMQAppender.ES_QUEUE)
public class RabbitMQAppender extends OutputStreamAppender {

    private static boolean startMq = false;//开启mq队列 stored
    public static final String ES_QUEUE = "eslog"; //默认es的队列
    private static MHMQClient mhmqClient;
    private static EsLogProperties eslogProperties;
    private static QueueInit queueInit;

    @Autowired
    public void setMhmqClient(MHMQClient mhmqClient, EsLogProperties eslogProperties, QueueInit queueInit, MQProperties mqProperties, EsLogClient esLogClient) throws Exception {
        RabbitMQAppender.mhmqClient = mhmqClient;
        RabbitMQAppender.eslogProperties = eslogProperties;
        RabbitMQAppender.queueInit = queueInit;
        if(StringUtils.isNotBlank(eslogProperties.getLogQueueName())){
            RabbitMQAppender.queueInit.createQueue(eslogProperties.getLogQueueName());//创建queue
            Method logCustomConsumer = EsConsumer.class.getDeclaredMethod("logCustomConsumer", Object.class);
            logCustomConsumer.setAccessible(true);
            EsConsumer esConsumer = EsConsumer.class.newInstance();
            esConsumer.setEsClient(esLogClient);
            esConsumer.setEsLogProperties(eslogProperties);
            MHConsumer mhConsumer = new MHConsumer(mqProperties,queueInit.getChannel(), esConsumer, eslogProperties.getLogQueueName(), logCustomConsumer,null,false,1);
            mhConsumer.start();
        }
        RabbitMQAppender.startMq = true;
    }

    @Override
    public void start() {

        setOutputStream(new OutputStreamMq(null));
        super.start();
    }


    class OutputStreamMq extends OutputStream {

        OutputStream outputStream;

        @Override
        public void write(int b) throws IOException {
            if(outputStream!=null){
                outputStream.write(b);
            }
        }

        public OutputStreamMq() {
            super();
        }

        public OutputStreamMq(OutputStream outputStream) {
            this.outputStream = outputStream;

        }

        @Override
        public void write(byte[] b) throws IOException {
            if(outputStream!=null){
                outputStream.write(b);
            }
            if(started){
                String logQueueName = getSendQueueName();
                mhmqClient.sendMsg(logQueueName,new String(b,"utf-8"));
            }
        }

        /**
         * 获取将要发送队列的名称, 如果自定了则
         * @return
         */
        private String getSendQueueName() {
            String logQueueName = eslogProperties.getLogQueueName();
            if(!StringUtils.isNotBlank(logQueueName)){
                logQueueName = ES_QUEUE;
            }
            return logQueueName;
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            byte[] bytes = Arrays.copyOfRange(b, off, len);
            this.write(bytes);
        }

        @Override
        public void flush() throws IOException {
            if(outputStream!=null){
                outputStream.flush();
            }
        }

        @Override
        public void close() throws IOException {
            if(outputStream!=null){
                outputStream.close();
            }
        }
    }
}
