package com.javaoffers.base.kafka.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author cmj
 * @Description kafka 消费者全局参数
 * @createTime 2021年06月20日 17:28:00
 */

public class KafkaConsumerProperties {


    /**
     * 链接broker的地址，可以指定多个
     */
    private String boostrapServers;


    /**
     * 消费者所属组
     */
    private String groupId;


    /**
     * key的反序列化
     */
    private String keyDeserializer;



    /**
     * value的反序列化
      */
    private String valueDeserializer;


    /**
     * 该属性指定了消费者从服务器获取记录的最小字节数。 broker 在收到消费者的数据请求时,
     *     如果可用的数据量小于 fetch.min.bytes 指定的大小,那么它会等到有足够的可用数据时
     *     才把它返回给消费者。这样可以降低消费者和 broker 的工作负载,因为它们在主题不是很
     *     活跃的时候(或者一天里的低谷时段)就不需要来来回回地处理消息。如果没有很多可用
     *     数据,但消费者的 CPU 使用率却很高,那么就需要把该属性的值设得比默认值大。如果
     *     消费者的数量比较多,把该属性的值设置得大一点可以降低 broker 的工作负载。
      */
    private String fetchMinBytes;


    /**
     *     我们通过fetch.min.bytes告诉 Kafka ,等到有足够的数据时才把它返回给消费者。而fetch.max.wait.ms
     *     则用于指定 broker 的等待时间,默认是 500ms 。如果没有足够的数据流入Kafka ,消费者获取最小数据量的
     *     要求就得不到满足,最终导致 500ms 的延迟。 如果要降低潜在的延迟(为了满足 SLA ),可以把该参数值设置得
     *     小一些。如果fetch.max.wait.ms被设为 lOOms ,并且 fetch.min.bytes被设为 lMB ,那么 Kafka 在收到
     *     消费者的请求后,要么返回1MB 数据,要么在 lOOms 后返回所有可用的数据 , 就看哪个条件先得到满足。
     */
    private String fetchMaxWaitMs;


    /**
     *     该属性指定了服务器从每个分区里返回给消费者的最大字节数。它的默认值是 lMB , 也
     *     就是说, KafkaConsumer.poll()方法从每个分区里返回的记录最多不超过max.partition.fetch.bytes
     *     指定的字节。如果一个主题有 20 个分区和 5 个消费者,那么每个消费者需要
     *     至少 4MB 的可用内存来接收记录。在为消费者分配内存时,可以给它们多分配一些,因
     *     为如果群组里有消费者发生崩愤,剩下的消费者需要处理更多的分区。 max.partition.fetch.bytes的值
     *     必须比 broker 能够接收的最大消息的字节数(通过 max.message.size 属
     *             性配置 )大, 否则消费者可能无法读取这些消息,导致消费者一直挂起重试。在设置该属
     *     性时,另一个需要考虑的因素是消费者处理数据的时间。 消费者需要频繁调用 poll () 方法
     *     来避免会话过期和发生分区再均衡,如果单次调用 poll () 返回的数据太多,消费者需要更
     *     多的时间来处理,可能无法及时进行下一个轮询来避免会话过期。如果出现这种情况, 可
     *     以把 max.partition.fetch.bytes  值改小 ,或者延长会话过期时间。
     */
    private String maxPartitionFetchBytes;



    /**
     *     该属性指定了消费者在被认为死亡之前可以与服务器断开连接的时间,默认是 3s 。如
     *     果消费者没有在 session.timeout.ms 指定的时间内发送心跳给群组协调器,就被认为
     *     已经死亡,协调器就会触发再均衡,把它的分区分配给群组里的其他消费者 。该属性与
     *     heartbeat.interval.ms 紧密相关 。 heartbeat.interval.ms 指定了 poll () 方法向协调器
     *     发送心跳的频率, session.timeout.ms 指定多久没有发送心跳则认为消费者死亡。。所以, 一
     *     般需要同时修改这两个属性, heartbeat.interval.ms 必须比 session.timeout.ms 小, 一
     *     般是 session.timeout.ms 的 三 分之 一 。如果 session.timeout.ms 是 3s ,那么 heartbeat.interval.ms
     *     应该是 1s 。 把 session.timeout.ms 值设得比默认值小,可以更快地检测和恢
     *     复崩愤的节点,不过长时间的'轮询'或'垃圾收集'可能导致非预期的再均衡。把该属性的值设
     *     置得大一些,可以减少意外的再均衡 ,不过检测节点崩溃需要更长的时间。
     */
    private String sessionTimeoutMs ;

    private String heartbeatIntervalMs ;


    /**
     * 该属性指定了消费者在读取一个没有偏移量的分区或者偏移量无效的情况下(因消费者长
     * 时间失效,包含偏移量的记录已经过时井被删除)该作何处理。它的默认值是 latest , 意
     * 思是说,在偏移量无效的情况下,消费者将从最新的记录开始读取数据(在消费者启动之
     * 后生成的记录)。另一个值是 earliest ,意思是说,在偏移量无效的情况下 ,消费者将从
     *     起始位置读取分区的记录。
     */
    private String autoOffsetReset;



    /**
     *     该属性指定了消费者是否自动提交偏移
     *     量,默认值是 true 。为了尽量避免出现重复数据和数据丢失,可以把它设为 false ,由 自
     *     己控制何 时提 交偏移量。如果把它设为 true ,还 可以通过配置auto.commit.interval.ms
     *     属性来控制提交的频率。
     */
    private String enableAutoCommit;



    /**
     * 我们知道,分区会被分配给群组里的消费者。PartitionAssignor 根据给定的消费者和 主
     *     题,决定哪些分区应该被分配给哪个消费者。 Kafka 有两个默认的分配策略 。
     *
     *     Range
     *
     *     该策略会把主题的若干个连续的分区分配给消费者。假设消费者 C1 和消费者 C2 同时
     *     订阅了主题 Tl 和主题 T2 ,井且每个主题有 3 个分区。那么消费者 Cl 有可能分配到过
     *     两个主题的分区 0 和分区 1 ,而消费者 C2 分配到这两个主题 的分区 2。因为每个 主题
     *     拥有奇数个分区,而分配是在主题内独立完成的,第一个消费者最后分配到比第二个消
     *     费者更多的分区。只要使用了 Range 策略,而且分区数量无法被消费者数量整除,就会
     *     出现这种情况。
     *
     *     RoundRobin
     *
     *     该策略把主题的所有分区逐个分配给消费者。如果使用 RoundRobin 策略来给消费者 C1
     *     和消费者 C2 分配分区,那么消费者 C1 将分到主题 T1 的分区 0 和分区 2 以及主题 T2
     *     的分区 1 ,消费者 C2 将分配到主题 T1 的分区 l 以及主题口的分区 0 和分区 2 。一般
     *     来说 ,如果所有消费者都订阅相同的主题(这种情况很常见) , RoundRobin 策略会给所
     *     有消费者分配相同数量 的分区(或最多就差一个分区)。
     *
     *    选择或自定义分区策略
     *
     *     可以通过设置 partition.assignment.strategy来选择分区策略。默认使用的是 org.
     *     apache.kafka.clients.consumer.RangeAssignor, 这个类实现了 Range 策略,不过也可以
     *     把它改成 。org.apache.kafka.clients.consumer.RoundRobinAssignor。我们还可以使用自定
     *     义策略,在这种情况下 , partition.assignment.strategy 属性的值就是自定义类的名字。
     */
    private String partitionAssignmentStrategy;



    /**
     * 该属性可以是任意字符串 , broker 用它来标识从客户端发送过来的消息,通常被用在日志、
     *     度量指标和配额里。
     */
    private String clientId;



    /**
     * 该属性用于控制单次调用 call () 方住能够返回的记录数量,可以帮你控制在轮询里需要处
     *     理的数据量。
     */
    private String maxPollRecords;



    /**
     * socket 在读写数据时用到的 TCP 缓冲区也可以设置大小。如果它们被设为-1 ,就使用操
     *     作系统的默认值。如果生产者或消费者与 broker 处于不同的数据中心内,可以适当增大这
     *     些值,因为跨数据中心的网络一般都有 比较高的延迟和比较低的带宽 。
     */
    private String receiveBufferBytes;
    private String sendBufferBytes;

    public String getBoostrapServers() {
        return boostrapServers;
    }

    public void setBoostrapServers(String boostrapServers) {
        this.boostrapServers = boostrapServers;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getKeyDeserializer() {
        return keyDeserializer;
    }

    public void setKeyDeserializer(String keyDeserializer) {
        this.keyDeserializer = keyDeserializer;
    }

    public String getValueDeserializer() {
        return valueDeserializer;
    }

    public void setValueDeserializer(String valueDeserializer) {
        this.valueDeserializer = valueDeserializer;
    }

    public String getFetchMinBytes() {
        return fetchMinBytes;
    }

    public void setFetchMinBytes(String fetchMinBytes) {
        this.fetchMinBytes = fetchMinBytes;
    }

    public String getFetchMaxWaitMs() {
        return fetchMaxWaitMs;
    }

    public void setFetchMaxWaitMs(String fetchMaxWaitMs) {
        this.fetchMaxWaitMs = fetchMaxWaitMs;
    }

    public String getMaxPartitionFetchBytes() {
        return maxPartitionFetchBytes;
    }

    public void setMaxPartitionFetchBytes(String maxPartitionFetchBytes) {
        this.maxPartitionFetchBytes = maxPartitionFetchBytes;
    }

    public String getSessionTimeoutMs() {
        return sessionTimeoutMs;
    }

    public void setSessionTimeoutMs(String sessionTimeoutMs) {
        this.sessionTimeoutMs = sessionTimeoutMs;
    }

    public String getHeartbeatIntervalMs() {
        return heartbeatIntervalMs;
    }

    public void setHeartbeatIntervalMs(String heartbeatIntervalMs) {
        this.heartbeatIntervalMs = heartbeatIntervalMs;
    }

    public String getAutoOffsetReset() {
        return autoOffsetReset;
    }

    public void setAutoOffsetReset(String autoOffsetReset) {
        this.autoOffsetReset = autoOffsetReset;
    }

    public String getEnableAutoCommit() {
        return enableAutoCommit;
    }

    public void setEnableAutoCommit(String enableAutoCommit) {
        this.enableAutoCommit = enableAutoCommit;
    }

    public String getPartitionAssignmentStrategy() {
        return partitionAssignmentStrategy;
    }

    public void setPartitionAssignmentStrategy(String partitionAssignmentStrategy) {
        this.partitionAssignmentStrategy = partitionAssignmentStrategy;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getMaxPollRecords() {
        return maxPollRecords;
    }

    public void setMaxPollRecords(String maxPollRecords) {
        this.maxPollRecords = maxPollRecords;
    }

    public String getReceiveBufferBytes() {
        return receiveBufferBytes;
    }

    public void setReceiveBufferBytes(String receiveBufferBytes) {
        this.receiveBufferBytes = receiveBufferBytes;
    }

    public String getSendBufferBytes() {
        return sendBufferBytes;
    }

    public void setSendBufferBytes(String sendBufferBytes) {
        this.sendBufferBytes = sendBufferBytes;
    }
}
