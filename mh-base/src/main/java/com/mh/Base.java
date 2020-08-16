package com.mh;

import com.mh.base.utils.model.ModelUtils;
import org.slf4j.Logger;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * create by cmj
 */
@SpringBootApplication
@MapperScan({"com.mh.**.mapper","com.mh.**.dao","com.mh.**.mapping","com.mh.**.service"})
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
