package com.mh.base.mq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description:
 * @Auther: create by cmj on 2020/8/15 22:59
 */
@ConfigurationProperties(prefix = "mq")
public class MQProperties {
    boolean printDeliveryTag = false;

    public boolean isPrintDeliveryTag() {
        return printDeliveryTag;
    }

    public void setPrintDeliveryTag(boolean printDeliveryTag) {
        this.printDeliveryTag = printDeliveryTag;
    }
}
