package com.mh.base.kafka;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class KafkaApp
{
    private static Logger logger = LoggerFactory.getLogger(KafkaApp.class);// log4j记录日志

    public static void main( String[] args )
    {
        SpringApplication.run(KafkaApp.class,args);
        logger.info( "Hello World!" );
    }
}
