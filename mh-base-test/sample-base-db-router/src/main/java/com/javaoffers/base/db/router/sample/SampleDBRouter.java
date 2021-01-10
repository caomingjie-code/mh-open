package com.javaoffers.base.db.router.sample;
import com.javaoffers.Base;
import com.javaoffers.base.db.router.sample.mapper.RouterMapper;
import com.javaoffers.mh.db.router.annotation.DataSourceRoute;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Description: 测试路由db
 * @Auther: create by cmj on 2021/1/10 16:07
 */
@SpringBootApplication
@MapperScan("com.javaoffers.base.db.router.sample.mapper.**")
public class SampleDBRouter {
    public static void main(String[] args) {
       SpringApplication.run(SampleDBRouter.class, args);
        //run(SampleDBRouter.class, args);
        System.out.println("启动完成");
    }




}
