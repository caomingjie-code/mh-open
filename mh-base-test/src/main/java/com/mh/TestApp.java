package com.mh;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * mh web后台
 * create by cmj
 */

@SpringBootApplication(scanBasePackages = {"com.mh.test.dao","com.mh.security"})
public class TestApp extends Base
{
    public static void main( String[] args )
    {
       new TestApp().run(args);
    }
}
