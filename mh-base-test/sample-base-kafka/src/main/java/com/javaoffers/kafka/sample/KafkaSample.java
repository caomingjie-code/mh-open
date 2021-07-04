package com.javaoffers.kafka.sample;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class KafkaSample
{
    private static Logger logger = LoggerFactory.getLogger(KafkaSample.class);// log4j记录日志

    public static void main( String[] args )
    {
        SpringApplication.run(KafkaSample.class,args);
        logger.info( "Hello World!" );
    }
}
