package com.javaoffers.base.db.router.sample;
import com.javaoffers.base.db.router.sample.start.SampleStart;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description: 测试路由db
 * @Auther: create by cmj on 2021/1/10 16:07
 */
@SpringBootApplication
@MapperScan("com.javaoffers.base.db.router.sample.mapper.**")
public class SampleDBRouter {
    public static void main(String[] args) {
        SampleStart.start(args);
    }





}
