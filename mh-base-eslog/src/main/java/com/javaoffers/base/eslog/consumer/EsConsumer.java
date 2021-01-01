package com.javaoffers.base.eslog.consumer;

import com.javaoffers.base.common.json.JsonUtils;
import com.javaoffers.base.eslog.appender.RabbitMQAppender;
import com.javaoffers.base.eslog.client.EsLogClient;
import com.javaoffers.base.eslog.conf.EsLogProperties;
import com.javaoffers.base.mq.annotation.ListenerQueue;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import javax.annotation.Resource;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Description: 该消费者专门为eslog 消费, 包括自定义和默认
 * @Auther: create by cmj on 2020/8/18 20:19
 */
public class EsConsumer {

    //该属性,使用默认监听消费者时由spring注入,自定义时由appender调用setRestClient注入
    @Resource
    EsLogClient esClient;

    @Resource
    EsLogProperties esLogProperties;

    public static AtomicLong ato = new AtomicLong(0); //自++

    /**
     * 默认eslog消费通道
     * @param msg
     * @throws Exception
     */
    @ListenerQueue(queues = RabbitMQAppender.ES_QUEUE)
    public void logDefaultConsumer(Object msg) throws Exception {
        putEslog(parseJson(msg+""));
    }

    /**
     * 自定义eslog消费通道
     * @param msg
     */
    public void logCustomConsumer(Object msg) throws Exception {
        putEslog(parseJson(msg+""));
    }

    /**
     * 解析 +|+ 代表分割  ^|^ 代表k v 对,
     * a ^|^ 1 +|+ b ^|^ 2 解析后为 {"a":"1","b":"2"}
     * @param msg
     * @return
     */
    public String parseJson(String msg) throws UnknownHostException {
        HashMap<String, String> map = new HashMap<>();
        String[] s = msg.split("\\+\\|\\+");
        for(String s1 : s){
            String[] split = s1.split("\\^\\|\\^");
            if(split!=null&&split.length==2){
                map.put(split[0],split[1]);
            }
        }
        //默认的一个字段
        Date date = new Date();
        map.put("ymd",DateFormatUtils.format(date,"yyyyMMdd"));
        map.put("hms",DateFormatUtils.format(date,"HHmmss.SSS"));
        map.put("workIp",DateFormatUtils.format(date,Inet4Address.getLocalHost().getHostAddress()));
        String json = JsonUtils.toJSONString(map);
        return json;
    }

    /**
     * put es
     * @param msg
     * @throws Exception
     */
    private void putEslog(Object msg) throws Exception {
        Date date = new Date();
        String index = RabbitMQAppender.ES_QUEUE;
        if(StringUtils.isNotBlank(esLogProperties.getIndexName())){
            index = esLogProperties.getIndexName();
        }
        String endPoint = null;
        //这形式是es7规定的,我们取默认,  相同的index -> type不能变, 为了能够按时间区分,我们在log数据中加了默认字段 ymd 代表年月日
        endPoint = getEndPoint(index, endPoint);

        esClient.putJson(endPoint,msg+"");
    }

    /**
     * 获取endPoint
     * @param index
     * @param endPoint
     * @return
     * @throws Exception
     */
    private String getEndPoint(String index, String endPoint) throws Exception {
        if(StringUtils.isNotBlank(esLogProperties.getEsVersion())){
            if(esLogProperties.getEsVersion().equals("7")){
                endPoint =  "/"+index + "/"+ "_create" +"/"+getUniqueId();
            }else if(esLogProperties.getEsVersion().equals("6")){
                endPoint =  "/"+index + "/"+ "create" +"/"+getUniqueId();
            }
        }else{
            endPoint =  "/"+index + "/"+ "create" +"/"+getUniqueId();
        }
        return endPoint;
    }

    /**
     * 获取唯一id
     * @return
     * @throws Exception
     */
    public String getUniqueId() throws Exception {
        Date date = new Date();
        String hexTime = Long.toHexString(date.getTime());
        String hexWork = getWorkIdBinary();
        String uniqueId = hexTime + hexWork + ato.incrementAndGet();
        return  uniqueId;
    }

    /**
     * 获取工作id二进制标识
     * @return
     */
    public String getWorkIdBinary() throws Exception {
        String workId = esLogProperties.getWorkId();//有限取workId
        if(StringUtils.isNotBlank(workId)){
            return bytes2Hex(workId.getBytes());
        }else{
            return Long.toHexString(Long.parseLong(binaryStr2Long(getHostAddressBinary())));
        }
    }

    public String bytes2Hex(byte[] bs){
        StringBuffer sb = new StringBuffer();
        if(bs!=null){
            for(byte b : bs){
                sb.append(Long.toHexString(b));
            }
        }
        return sb.toString();
    }

    public String bytes2Binary(byte[] bs){
        if(bs!=null){
            long l = 0;
            boolean first = true;
            for(byte b : bs){
                if(first){
                    first = false;
                    l = l|b;
                }else{
                    l= l<<8 | b;
                }
            }
            return Long.toBinaryString(l);
        }
        return null;
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
        if(split!=null){
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
        }



        return sb.toString();
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
                firstIndex = false;
            }else{
                l = l<<1;
                l = l|i;
            }
        }
        return l+"";
    }

    public void setEsClient(EsLogClient esClient) {
        this.esClient = esClient;
    }

    public void setEsLogProperties(EsLogProperties esLogProperties) {
        this.esLogProperties = esLogProperties;
    }
}
