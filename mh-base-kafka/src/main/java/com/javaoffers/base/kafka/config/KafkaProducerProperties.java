package com.javaoffers.base.kafka.config;

/**
 * @author cmj
 * @Description kafka生产者全局参数
 * @createTime 2021年06月20日 17:28:00
 */
public class KafkaProducerProperties {

    /**
     * 链接broker的地址，可以指定多个
     */
    private String bootstrapServers = "127.0.0.1:9092"; //默认

    /**
     * 默认序列化
     */
    private String keySerializer = "org.apache.kafka.common.serialization.StringSerializer";

    private String valueSerializer = "org.apache.kafka.common.serialization.StringSerializer";

    /**
     * kafka生产者对消息写入成功的认证机制，分别有三种：
     * 1： acks=0 生产者在消息写入成功之前不会等待任何响应，也就是说发送之后就不管是否成功被写入，这样优点是可以更大的提高并发度，缺点是数据有可能会丢失。
     * 2： acks=1 只要集群的首领节点收到消息，就会给对应的消息生产者发送一个成功消息。优点是相对于acks=0丢失率减少，缺点是仍然存在丢失的问题，但吞吐量取
     *     决于使用的是同步还是异步，如果是异步则延迟问题得到缓解，因为吞吐量还是会受到发送消息的数量限制（比如，生产者在受到服务器响应之前可以发送多少个消息）
     * 3： acks=all 集群中的所有节点全部接收到消息时，生产者才会收到来自服务器的成功消息， 这种模式是最安全的，但是生产者接收到服务的成功消息所产生的时间延迟也是最高的。
     */
    private String acks = "0";

    /**
     * 该参数是用来设置生产者内存缓存区的大小，生产者用它来缓存要发送服务器的消息。注意：如果生产者生产消息（包含发送动作）的数度大于消息从网络到服务器的数度，则会导致
     * 生产者内存空间不足。这个时候send方法要么被阻塞或者抛异常。（抛异常取决于 block.on.buffe.full参数，0.9.0.0版本里被替换成了max.block.ms, 表示在阻塞时间在内存满时，
     * 如果超过则会报错）
     */
    private String bufferMemory;
    private String blockOnBuffeFull;


    /**
     * 指消息在发送到服务器之前使用那一种压缩算法，将消息进行压缩，默认不压缩。kafka提供了三种压缩算法分别是：snappy, gzip, lz4 。 snappy 是由google发明，优点: 占用cpu较少和可观的
     * 压缩比率，压缩的速度（性能）较好。 如果关注性能和宽带建议使用这种算法。gzip 优点：压缩比更高，缺点：占用cpu较高。如果宽带有限制，则推荐此压缩算法。
     */
    private String compressionType;

    /**
     * 生产者收到服务器的临时错误（例如： 分区找不到首领。）在这种情况下retries决定了生产者可以重发消息的次数。如果生产者重试的次数达到这个次数时仍然不成功则生产者就会抛错。默认情况下重试之间的时间
     * 间隔是100ms。 该值可以通过 retry.backoff.ms 参数来改变时间间隔的值。
     */
    private String retries;

    /**
     * 当多个消息要被发送到同一个分区时，生产者会把他们放入同一个批次里。该参数制定一个批次使用的内存大小（按照字节，并非消息的个数）。 注意：当批次的内存被填满时会将批次里的消息发送出去，但是不满时
     * （甚至只有一个消息）也会被发送，并不是会一致等待直至满时才会发送。所以如果批次给的空间很大只会造成浪费，但是很小会使消息发送的更频繁。
     */
    private String batchSize;

    /**
     * 指生产者在发送批次之前所等待的时间。KafkaProducer会在批次被填满或达到此等待时间时会把消息发出去。默认是0,即使批次里有一个消息生产者也会把消息发送出去了。如果把linger.ms设置为大于0，
     * 则会把更多的消息放入批次里，提高吞吐量，缺点是增加了延迟。
     */
    private String lingerMs;

    /**
     *该参数可以为任意字符串，用于表示信息的唯一来源。
     */
    private String clientId;

    /**
     *
     指定了生产者在收到服务器之前可以发送多少个消息。值越高越占用内存，不过吞吐量会大大提高。如果值为1，则可以保证消息是按照顺序写入服务器的，即使发生了重试。
     （设置此值是1表示kafka broker在响应请求之前client不能再向同一个broker发送请求。注意：设置此参数是为了避免消息乱序）

     */
    private String maxInFlightRequestsPerConnection;


    /**
     * 指定了broker等待同步副本返回消息的确认消息，与acks相匹配，如果在指定的时间内没有收到确认消息则会报错。
     */
    private String timeoutMs;

    /**
     * 指生产者在发送数据时等待服务器返回的时间。
     */
    private String requestTimeoutMs;

    /**
     * 指生产者在获取元数据（获取目标分区的首领是谁）时等待服务器的时间。
     */
    private String metadataFatchTimeoutMs;


    /**
     * 配置控制KafkaProducer的send()， partitionsFor()， initTransactions()， sendOffsetsToTransaction()， commitTransaction()和abortTransaction()
     * 方法将阻塞多长时间。对于send()，这个超时限制了等待元数据获取和缓冲区分配的总时间(用户提供的序列化器或分区器中的阻塞不计算在这个超时中)。对于partitionsFor()，如果
     * 元数据不可用，则此超时限制了等待元数据的时间。与事务相关的方法总是阻塞的，但如果无法发现事务协调器，则可能超时
     */
    private String maxBlockMs;


    /**
     * 用于控制生产者发送请求的大小，它可以指发送单个消息的最大值。也可以指单个请求中所有消息的总大小。例如： 如果这个值设置为1M， 那么可以发送单个消息为1M，也可以发送一个批次的大小为1M
     * , 如果批次里的每个消息为1KB，则可以发送1024个消息（刚好为1M）。
     */
    private String  maxRequestSize;


    /**
     * 这个两个参数指 tcp socket 接收和发送缓冲区的大小。如果设置为-1则缓冲区的大小又系统决定（通常为系统的默认值）。如果生产者，消费者和broker在不同的网络架构下则可以适当调大该值，
     * 因为在不同的网络架构下网络通常有较高的延迟和较低的宽带
     */
    private String receiveBufferBytes;
    private String sendBufferBytes;


    /**
     * 实现了org.apache.kafka.clients.producer.的分区器类。分区程序界面。可以自定义分区策略
     */
    private String partitionerClass;

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getKeySerializer() {
        return keySerializer;
    }

    public void setKeySerializer(String keySerializer) {
        this.keySerializer = keySerializer;
    }

    public String getValueSerializer() {
        return valueSerializer;
    }

    public void setValueSerializer(String valueSerializer) {
        this.valueSerializer = valueSerializer;
    }

    public String getAcks() {
        return acks;
    }

    public void setAcks(String acks) {
        this.acks = acks;
    }

    public String getBufferMemory() {
        return bufferMemory;
    }

    public void setBufferMemory(String bufferMemory) {
        this.bufferMemory = bufferMemory;
    }

    public String getCompressionType() {
        return compressionType;
    }

    public void setCompressionType(String compressionType) {
        this.compressionType = compressionType;
    }

    public String getRetries() {
        return retries;
    }

    public void setRetries(String retries) {
        this.retries = retries;
    }

    public String getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(String batchSize) {
        this.batchSize = batchSize;
    }

    public String getLingerMs() {
        return lingerMs;
    }

    public void setLingerMs(String lingerMs) {
        this.lingerMs = lingerMs;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getMaxInFlightRequestsPerConnection() {
        return maxInFlightRequestsPerConnection;
    }

    public void setMaxInFlightRequestsPerConnection(String maxInFlightRequestsPerConnection) {
        this.maxInFlightRequestsPerConnection = maxInFlightRequestsPerConnection;
    }

    public String getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(String timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public String getRequestTimeoutMs() {
        return requestTimeoutMs;
    }

    public void setRequestTimeoutMs(String requestTimeoutMs) {
        this.requestTimeoutMs = requestTimeoutMs;
    }

    public String getMetadataFatchTimeoutMs() {
        return metadataFatchTimeoutMs;
    }

    public void setMetadataFatchTimeoutMs(String metadataFatchTimeoutMs) {
        this.metadataFatchTimeoutMs = metadataFatchTimeoutMs;
    }

    public String getMaxBlockMs() {
        return maxBlockMs;
    }

    public void setMaxBlockMs(String maxBlockMs) {
        this.maxBlockMs = maxBlockMs;
    }

    public String getMaxRequestSize() {
        return maxRequestSize;
    }

    public void setMaxRequestSize(String maxRequestSize) {
        this.maxRequestSize = maxRequestSize;
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

    public String getPartitionerClass() {
        return partitionerClass;
    }

    public void setPartitionerClass(String partitionerClass) {
        this.partitionerClass = partitionerClass;
    }

    public String getBlockOnBuffeFull() {
        return blockOnBuffeFull;
    }

    public void setBlockOnBuffeFull(String blockOnBuffeFull) {
        this.blockOnBuffeFull = blockOnBuffeFull;
    }
}
