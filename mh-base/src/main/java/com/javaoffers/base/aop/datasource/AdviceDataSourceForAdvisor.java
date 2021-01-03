package com.javaoffers.base.aop.datasource;

import com.javaoffers.base.annotation.datasource.DataSourceRoute;
import com.javaoffers.base.utils.datasource.BaseComboPooledDataSource;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @Description:
 * @Auther: create by cmj on 2020/8/28 13:09
 */
public class AdviceDataSourceForAdvisor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String value = null;
        try {
            //获取方法的注解
            DataSourceRoute declaredAnnotation = invocation.getMethod().getDeclaredAnnotation(DataSourceRoute.class);
            if(declaredAnnotation!=null){ //如果存在,则开始陆游
                 value = declaredAnnotation.value();
                BaseComboPooledDataSource.pushStackRouter(value);
            }
            return  invocation.proceed();
        }catch ( Exception e){
            e.printStackTrace();
        } finally {
            BaseComboPooledDataSource.meanClean(value);
        }
        return null;
    }
}
