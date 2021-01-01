package com.javaoffers.base.eslog.conf;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * @Description:
 * @Auther: create by cmj on 2020/8/21 17:18
 */

public class AutoConfigEslog implements ImportSelector , EnvironmentAware {

    private static boolean stop = false;

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        //基础检验
        if(stop){  return null; }
        return new String[]{
                "com.javaoffers.base.eslog.appender.RabbitMQAppender"
                ,"com.javaoffers.base.eslog.client.EsLogClient"
                ,"com.javaoffers.base.eslog.consumer.EsConsumer"
        };
    }

    @Override
    public void setEnvironment(Environment environment) {
        String property = environment.getProperty("javaoffers.eslog.workId");
        try {
            String hostAddress = Inet4Address.getLocalHost().getHostAddress();
            if(StringUtils.isBlank(hostAddress)&&StringUtils.isBlank(property)){
                UnknownHostException host_address_is_blank = new UnknownHostException("host Address and work-id of eslog are blank,  one must be not blank ");
                host_address_is_blank.printStackTrace();
                stop = true;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            stop = true;
        }

    }
}
