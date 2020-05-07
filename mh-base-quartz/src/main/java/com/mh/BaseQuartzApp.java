package com.mh;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 定时任务app
 */
@SpringBootApplication
public class BaseQuartzApp extends Base {

	public static void main(String[] args) {
		new BaseQuartzApp().run(args);   
	}

} 
