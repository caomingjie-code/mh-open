package com.javaoffers.mh.db.router.aop.datasource;

import com.javaoffers.mh.db.router.annotation.DataSourceRoute;
import com.javaoffers.mh.db.router.datasource.BaseComboPooledDataSource;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

/**
 * @Description: 路由拦截器
 * @Auther: create by cmj on 2020/8/28 13:09
 */
public class AdviceDataSourceForAdvisor implements MethodInterceptor {
    Logger logger = LoggerFactory.getLogger(AdviceDataSourceForAdvisor.class);

    AbstractPlatformTransactionManager tm;
    private static String className;

    public void setAbstractPlatformTransactionManager(AbstractPlatformTransactionManager abstractPlatformTransactionManager) {
        this.tm = abstractPlatformTransactionManager;
        this.className = tm.getClass().getSimpleName();
    }

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
            if(value!=null){
                if("DataSourceTransactionManager".equalsIgnoreCase(className)){//该DataSourceTransactionManager数据源管理是关闭数据库链接，跳出invocation.proceed();
                    logger.info("clean router："+BaseComboPooledDataSource.getRouterSourceName());
                    BaseComboPooledDataSource.clean();
                }
                if("JpaTransactionManager".equalsIgnoreCase(className)){//该 JpaTransactionManager 数据源管理是跳出invocation.proceed()后， 再关闭数据库链接。
                    logger.info("clean router："+BaseComboPooledDataSource.getRouterSourceName());
                    BaseComboPooledDataSource.meanClean(BaseComboPooledDataSource.getRouterSourceName());
                    BaseComboPooledDataSource.clean();
                }
            }
        }
        return null;
    }
}
