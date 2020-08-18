package com.mh.base.eslog.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Auther: create by cmj on 2020/8/18 00:55
 */
@Configuration
@EnableConfigurationProperties(EsLogProperties.class)
public class EslogConf {

}
