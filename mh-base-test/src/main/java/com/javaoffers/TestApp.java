package com.javaoffers;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * javaoffers web后台
 * create by cmj
 */

@SpringBootApplication(scanBasePackages = {"com.javaoffers.test.dao", "com.javaoffers.security"})
public class TestApp extends Base
{
    public static void main( String[] args )
    {
       new TestApp().run(args);
    }
}
