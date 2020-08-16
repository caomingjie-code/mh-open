package com.mh.base.kafka.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description: kafka的配置类
 * @Auther: create by cmj on 2020/8/11 13:43
 */
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {

    public  Zk zk = new Zk();

    public Zk getZk() {
        return zk;
    }

    public void setZk(Zk zk) {
        this.zk = zk;
    }

    class Zk{
        private String nodes;

        public String getNodes() {
            return nodes;
        }

        public void setNodes(String nodes) {
            this.nodes = nodes;
        }
    }


}
