package com.javaoffers.base.mq.config;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.javaoffers.base.mq.msgconverter.MHMsgConverter;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * MHMQ的配置文件
 * @author cmj
 *
 */

@EnableConfigurationProperties(MQProperties.class)
public class MHMQConifg {
	
	@Bean(name="myFactory")
	public SimpleRabbitListenerContainerFactory myFactory(
			SimpleRabbitListenerContainerFactoryConfigurer configurer,ConnectionFactory connectionFactory) {
		SimpleRabbitListenerContainerFactory factory =
				new SimpleRabbitListenerContainerFactory();
		configurer.configure(factory, connectionFactory);
		factory.setMessageConverter(myMessageConverter());
		return factory;
	}

	private MessageConverter myMessageConverter() {
		return new MHMsgConverter();
	}
	
	
	
	
	
}
