package com.javaoffers;
import org.springframework.data.repository.config.FragmentMetadata;
import com.javaoffers.base.utils.jar.JarUtils;
import org.slf4j.Logger;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.repository.config.FragmentMetadata;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;

/**
 * create by cmj
 */
@SpringBootApplication
@MapperScan({"com.javaoffers.**.mapper","com.javaoffers.**.dao","com.javaoffers.**.mapping","com.javaoffers.**.service"})
public class Base {
	private static Logger logger = LoggerFactory.getLogger(Base.class);// log4j记录日志
	static URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
	public static void main(String[] args) throws IOException {
		new Base().run(args);
	}
	public static void run(Class boot,String[] args)  {
		try {
			loadClass();
		} catch (Exception e) {
			e.printStackTrace();
		}
		SpringApplication.run(boot, args);
		logger.info("系统启动完毕");
	}
	public  void run(String[] args)  {
		try {
			loadClass();
		} catch (Exception e) {
			e.printStackTrace();
		}
		SpringApplication.run(Base.class, args);
		logger.info("系统启动完毕");
	}


	public static void loadClass() throws Exception {
		String name = FragmentMetadata.class.getName();
		Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
		method.setAccessible(true);
		Set<JarUtils.ClassFormJar> classFromJar = JarUtils.getClassFromJar("mh-base-dao");
		if(classFromJar!=null){
			for(JarUtils.ClassFormJar classFormJar : classFromJar){
				if("org.springframework.data.repository.config.FragmentMetadata".equals(classFormJar.getName())){
					URL url = classFormJar.getClassFile().toURI().toURL();
					method.invoke(classLoader, url.toURI().toURL());
				}
			}
		}
	}


}
