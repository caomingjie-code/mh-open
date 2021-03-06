package com.javaoffers.mh.db.router.aop.datasource;

import com.javaoffers.mh.db.router.annotation.DataSourceRoute;
import com.javaoffers.mh.db.router.common.Router;
import com.javaoffers.mh.db.router.datasource.BaseComboPooledDataSource;
import com.javaoffers.mh.db.router.exception.BaseDataSourceException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Description: 路由拦截器
 * @Auther: create by cmj on 2020/8/28 13:09
 */
public class AdviceDataSourceForAdvisor implements MethodInterceptor {
    Logger logger = LoggerFactory.getLogger(AdviceDataSourceForAdvisor.class);
    AtomicInteger rand = new AtomicInteger(0);

    AbstractPlatformTransactionManager tm;
    private static String className;

    public void setAbstractPlatformTransactionManager(AbstractPlatformTransactionManager abstractPlatformTransactionManager) {
        this.tm = abstractPlatformTransactionManager;
        this.className = tm.getClass().getSimpleName();
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Router router = null;
        boolean printRoute = false;
        try {
            //获取方法的注解
            DataSourceRoute declaredAnnotation = invocation.getMethod().getDeclaredAnnotation(DataSourceRoute.class);
            if(declaredAnnotation!=null){ //如果存在,则开始陆游

                 router = new Router(declaredAnnotation.value(), declaredAnnotation.isForce());
                 printRoute = true;

            }else if(declaredAnnotation==null&&BaseComboPooledDataSource.getRouter()!=null){
                //继承栈顶路由,(虚假陆游也需要继承)
                logger.info("extends router : "+BaseComboPooledDataSource.getRouter().getRouterName()+" [extends]");
                router = new Router(BaseComboPooledDataSource.getRouter().getRouterName(), false,true);
            }
            if(printRoute){
                logger.info("put     router : "+ router.getRouterName());//打印即将路由的信息名称
            }
            if(router!=null){
                BaseComboPooledDataSource.pushStackRouter(router); //放入栈顶
            }
            return  invocation.proceed();
        }catch ( Exception e){
            e.printStackTrace();
        } finally {
            if(router!=null&&router==BaseComboPooledDataSource.getRouter()){ //这是同一个对象所以用 == ，如果不是则报错
                if("DataSourceTransactionManager".equalsIgnoreCase(className)){//该DataSourceTransactionManager数据源管理是关闭数据库链接，跳出invocation.proceed();
                    logger.info("clean router：" + BaseComboPooledDataSource.getRouterSourceName()+(router.isExtends()?" [extends]":""));
                    BaseComboPooledDataSource.clean();
                }
                if("JpaTransactionManager".equalsIgnoreCase(className)){//该 JpaTransactionManager 数据源管理是跳出invocation.proceed()后， 再关闭数据库链接。
                    if(!router.isSham()) {
                        //主要解决JpaTransactionManager关闭时，找不到真是链接：
                        // 因为 DataSourceTransactionManager 关闭链接时栈顶为对应的路由（此时如果有其他操作依然可以找到对应的真实链接），而 JpaTransactionManager 关闭时，
                        // 栈顶已清空，为了也让其有与之对应的路由所以使用meanClean来记录 ,解决在关闭时（有可能有其他操作，比如此链接是否是已经关闭 isClosed ）获取对应的真实链接缺失。
                        BaseComboPooledDataSource.meanClean(BaseComboPooledDataSource.getRouterSourceName());//
                    }
                    logger.info("clean router：" + BaseComboPooledDataSource.getRouterSourceName()+(router.isExtends()?" [extends]":""));
                    BaseComboPooledDataSource.clean();
                }
            }else if(router!=null){
                BaseDataSourceException.getException("router stack is error ").printStackTrace();
            }
        }
        return null;
    }

    private String getDefaultRouter() {
        String value;
        value = BaseComboPooledDataSource.DEFAULT_ROUTER;
        String[] readDataSources = BaseComboPooledDataSource.getReadDataSource(value);
        if(readDataSources!=null&&readDataSources.length>0){//优先取读数据库
            int randIndex = rand.incrementAndGet()% readDataSources.length;
            value =  readDataSources[randIndex];
            if(rand.get()>=100){
                rand.getAndSet(0);
            }
        }
        return value;
    }
}
