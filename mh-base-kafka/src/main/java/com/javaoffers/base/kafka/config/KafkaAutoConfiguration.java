package com.javaoffers.base.kafka.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @Description:
 * @Auther: create by cmj on 2020/8/11 13:47
 */
@Configuration
@EnableConfigurationProperties({KafkaProperties.class})
@Import(KafkaConfigurationClassPostProcessor.class)
public class KafkaAutoConfiguration {




}
