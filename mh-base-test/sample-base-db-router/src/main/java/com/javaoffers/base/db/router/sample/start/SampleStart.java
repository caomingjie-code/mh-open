package com.javaoffers.base.db.router.sample.start;

import com.javaoffers.base.db.router.sample.SampleDBRouter;
import org.springframework.boot.SpringApplication;

public class SampleStart {
    public static void start(String[] args){
        System.out.println("kai shi qi dong !!! ");
        SpringApplication.run(SampleDBRouter.class, args);
        //run(SampleDBRouter.class, args);
        System.out.println("启动完成");
    }
}
