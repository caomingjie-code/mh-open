package com.javaoffers.base.kafka.config;

import com.javaoffers.base.kafka.anno.KafkaProducer;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Auther: create by cmj on 2020/8/11 13:47
 */
@Configuration
@EnableConfigurationProperties({KafkaProperties.class})
public class KafkaAutoConfiguration {

    @Bean
    public KafkaScaner kafkaScaner(BeanDefinitionRegistry registry){
        KafkaScaner kafkaScaner = new KafkaScaner(registry);
        kafkaScaner.resetFilters(false);
        kafkaScaner.addIncludeFilter(new AnnotationTypeFilter(KafkaProducer.class));
        return kafkaScaner;
    }

}
