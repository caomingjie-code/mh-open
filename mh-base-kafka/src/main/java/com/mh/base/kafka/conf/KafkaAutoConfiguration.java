package com.mh.base.kafka.conf;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Auther: create by cmj on 2020/8/11 13:47
 */
@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaAutoConfiguration {


}
