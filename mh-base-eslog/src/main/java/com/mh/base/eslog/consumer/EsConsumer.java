package com.mh.base.eslog.consumer;

import com.mh.base.eslog.appender.RabbitMQAppender;
import com.mh.base.eslog.client.EsLogClient;
import com.mh.base.mq.annotation.ListenerQueue;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.common.util.ByteUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Description: 该消费者专门为eslog 消费, 包括自定义和默认
 * @Auther: create by cmj on 2020/8/18 20:19
 */
@Component
public class EsConsumer {

    //该属性,使用默认监听消费者时由spring注入,自定义时由appender调用setRestClient注入
    @Resource
    EsLogClient esClient;

    public static AtomicLong ato = new AtomicLong(0); //自++

    /**
     * 默认eslog消费通道
     * @param msg
     * @throws Exception
     */
    @ListenerQueue(queues = RabbitMQAppender.ES_QUEUE)
    public void logDefaultConsumer(Object msg) throws Exception {
        Date date = new Date();
        long increment = ato.incrementAndGet();
        String endPoint =  RabbitMQAppender.ES_QUEUE + "/"+DateFormatUtils.format(date,"yyyyMMdd")+"/"+getUniqueId();
        esClient.putJson(endPoint,msg+"");
    }

    /**
     * 自定义eslog消费通道
     * @param msg
     */
    public void logCustomConsumer(Object msg){

    }

    /**
     * 获取唯一id
     * @return
     * @throws Exception
     */
    public String getUniqueId() throws Exception {
        Date date = new Date();
        String bitTime = Long.toBinaryString(date.getTime());
        String workBit = getHostAddressBinary();
        String uniqueBit = bitTime + workBit + ato.incrementAndGet();
        return  binaryStr2Long(uniqueBit);
    }

    /**
     * 获取地址的10进制表示
     * @return
     * @throws Exception
     */
    public String getHostAddressBinary() throws Exception {
        String hostAddress = Inet4Address.getLocalHost().getHostAddress();
        String[] split = hostAddress.split("[.]");
        StringBuilder sb = new StringBuilder("");
        //32位二进制
        for(String s : split){
            int i = Integer.parseInt(s);
            String s1 = Long.toBinaryString(i);
            if(s1.length()<8){
                for(int index = s1.length();i<8;i++){
                    s1="0"+s1;
                }
            }
            sb.append(s1);
        }
        String s = binaryStr2Long(sb.toString());
        return s;
    }

    /**
     * 二进制转10进制
     * @param binaryString  00011
     * @return
     */
    public String binaryStr2Long(String binaryString){
        long l = 0;
        String[] split = binaryString.split("");
        boolean firstIndex = true;
        for(String bit : split){
            int i = Integer.parseInt(bit);
            if(firstIndex){
                l = l|i;
                firstIndex = true;
            }else{
                l = l<<1;
                l = l|i;
            }
        }
        return l+"";
    }



}
