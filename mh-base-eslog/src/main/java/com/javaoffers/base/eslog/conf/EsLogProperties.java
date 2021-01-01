package com.javaoffers.base.eslog.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description: yml support eslog config
 * @Auther: create by cmj on 2020/8/18 23:56
 */
@ConfigurationProperties(prefix = "javaoffers.eslog")
public class EsLogProperties {

    //Elasticsearch -> Indices -> Types -> Documents -> Fields
    private String indexName="ESLOG"; //Indices Naming
    private String logQueueName = null; //队列名称
    private String esVersion = "6";//es 版本,因为不同的版本在put时type会有所变化
    private String workId;  //工作id, 用于区分不同的部署服务器,如果不存在默认使用ip地址,如果两个都不存在则会报错.

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getLogQueueName() {
        return logQueueName;
    }

    public void setLogQueueName(String logQueueName) {
        this.logQueueName = logQueueName;
    }

    public String getEsVersion() {
        return esVersion;
    }

    public void setEsVersion(String esVersion) {
        this.esVersion = esVersion;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }
}
