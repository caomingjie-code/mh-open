package com.javaoffers;

import org.slf4j.Logger;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * create by cmj
 */
@SpringBootApplication
@MapperScan({"com.javaoffers.**.mapper","com.javaoffers.**.dao","com.javaoffers.**.mapping","com.javaoffers.**.service"})
public class Base {
	private static Logger logger = LoggerFactory.getLogger(Base.class);// log4j记录日志
	public static void main(String[] args) {
		new Base().run(args);
	}
	public void run(String[] args) {
		SpringApplication.run(Base.class, args);
		logger.info("系统启动完毕");
	}

}
