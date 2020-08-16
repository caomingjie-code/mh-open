package com.mh.base.aop.datasource;

import java.lang.reflect.Method;
import java.sql.SQLException;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import com.mh.base.annotation.datasource.DataSourceRoute;
import com.mh.base.exception.BaseDataSourceException;
import com.mh.base.common.clone.Clone;
import com.mh.base.utils.datasource.BaseComboPooledDataSource;

/**
 * 多数据源配置
 * @author cmj
 * 
 */
//@Component
//@Aspect
public class MultipartDataSource implements ApplicationContextAware{

	/**
	 * spring 容器
	 */
	private ApplicationContext appContext;
	
	@Resource(name="dataSourceMater")
	private BaseComboPooledDataSource dataSourceSlave;
	
	/**
	 * 切点
	 */
	@Pointcut("@annotation(com.mh.base.annotation.datasource.DataSourceRoute)")
	public void dataSourceRoute() {}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		appContext = applicationContext;
	}
	
	@Around("dataSourceRoute()")
	public Object doDataSourceRouteAround(ProceedingJoinPoint joinPoint) throws SQLException {
	
		Signature signature = joinPoint.getSignature();
		if(signature instanceof MethodSignature) {
			MethodSignature methodSignature = (MethodSignature) signature;
			Method method = methodSignature.getMethod();
			DataSourceRoute dataSourceRoute = method.getDeclaredAnnotation(DataSourceRoute.class);
			String value = dataSourceRoute.value();
			if(!StringUtils.isNoneBlank(value)) {
				throw BaseDataSourceException.getException("dataSourceRoute is null");
			} 
			//开启事物
			dataSourceSlave.setDataSourceRoute(value);
			
			
			
			
		}
		  
		
		
		return null;
	}
	
	
	
	
}
