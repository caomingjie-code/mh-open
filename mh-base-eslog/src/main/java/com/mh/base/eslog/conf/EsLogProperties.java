package com.mh.base.eslog.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description:
 * @Auther: create by cmj on 2020/8/18 23:56
 */
@ConfigurationProperties(prefix = "mh.eslog")
public class EsLogProperties {

    //Elasticsearch -> Indices -> Types -> Documents -> Fields
    private String indexName="ESLOG"; //Indices Naming
    private String logQueueName = null; //队列名称

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
}
