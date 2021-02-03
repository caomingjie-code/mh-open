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
            }else if(declaredAnnotation==null&&BaseComboPooledDataSource.getRouterSourceName_()!=null){
                value = BaseComboPooledDataSource.getRouterSourceName_();//继承栈顶路由,如果存在
            }else{//执行默认路由master,此时栈中是不存在路由的(如果master中存在slave 读则优先取读数据库)
                value = BaseComboPooledDataSource.DEFAULT_ROUTER;
                String[] readDataSources = BaseComboPooledDataSource.getReadDataSource(value);
                if(readDataSources!=null&&readDataSources.length>0){
                    int randIndex = ((int)System.nanoTime() & 1)% readDataSources.length;
                    value =  readDataSources[randIndex];
                }
            }
            BaseComboPooledDataSource.pushStackRouter(value);
            logger.info("start router : "+value);//打印即将路由的信息名称
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
