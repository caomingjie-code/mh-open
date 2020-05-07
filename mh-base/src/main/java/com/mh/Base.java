package com.mh;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * create by cmj
 */
@SpringBootApplication
@MapperScan({"com.mh.**.mapper","com.mh.**.dao","com.mh.**.mapping","com.mh.**.service"})
public class Base {

	public static void main(String[] args) {
		new Base().run(args);
	}
	public void run(String[] args) {
		SpringApplication.run(Base.class, args);
		System.out.println("系统启动完毕");
	}

}
