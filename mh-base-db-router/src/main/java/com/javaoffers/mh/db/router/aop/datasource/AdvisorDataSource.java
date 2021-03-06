package com.javaoffers.mh.db.router.aop.datasource;
import com.javaoffers.mh.db.router.annotation.DataSourceRoute;
import org.aopalliance.aop.Advice;
import org.springframework.aop.*;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.annotation.AnnotationClassFilter;
import org.springframework.aop.support.annotation.AnnotationMethodMatcher;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Description: 研究 Advisor, 注意和 Advice 的关系和区别
 * @Auther: create by cmj on 2020/8/28 13:04
 */
public class AdvisorDataSource implements Advisor, PointcutAdvisor {

    private String name = "cmj"; //只做一个标记不起作用

    @Resource
    AbstractPlatformTransactionManager transactionManager;

    AdviceDataSourceForAdvisor advice = new AdviceDataSourceForAdvisor();

    @Override
    public Advice getAdvice() {
        advice.setAbstractPlatformTransactionManager(transactionManager);
        return advice;
    }

    @Override
    public boolean isPerInstance() {
        return true;
    }

    @Override
    public Pointcut getPointcut() {
        return new PointcutStr();
    }

    class PointcutStr implements Pointcut {

        @Override
        public ClassFilter getClassFilter() {
            return new AnnotationClassFilter(DataSourceRoute.class,true){
                @Override
                //类型匹配返回true,才会执行 方法匹配，逻辑在AopUtils.canApplyAdvisorDataSource
                public boolean matches(Class<?> clazz) {

                    if (IsMatches(clazz)) return true;

                    return super.matches(clazz);
                }
            };
        }

        @Override
        public MethodMatcher getMethodMatcher() {
            return  new AnnotationMethodMatcher(DataSourceRoute.class){
                @Override
                public boolean matches(Method method, Class<?> clazz) {

                    if (IsMatches(clazz)) return true;

                    return super.matches(method, clazz);
                }
            };
        }

        /**
         * 如果这个类的方法上存在DataSourceRoute 则返回 true
         * @param clazz
         * @return
         */
        private boolean IsMatches(Class<?> clazz) {
            if(Proxy.isProxyClass((clazz))){ //如果是代理
                Set<Class> interfaces = getInterfaces(clazz);
                if(interfaces!=null){
                    for(Class c : interfaces){
                        if (isMatches(c)) return true;
                    }
                }
            }
            if (isMatches(clazz)) return true;
            return false;
        }

        private boolean isMatches(Class<?> clazz) {
            Method[] declaredMethods = clazz.getDeclaredMethods();
            if(declaredMethods != null && declaredMethods.length>0){
                for(Method method : declaredMethods ){
                    method.setAccessible(true);
                    DataSourceRoute declaredAnnotation = method.getDeclaredAnnotation(DataSourceRoute.class);
                    if(declaredAnnotation!=null){
                        return true;
                    }
                }
            }
            return false;
        }

        public Set<Class> getInterfaces(Class<?> clazz){
            HashSet<Class> interfacesAll = new HashSet<>();
            Class<?>[] interfaces = clazz.getInterfaces();
            if(interfaces!=null){
                for(Class c : interfaces){
                    interfacesAll.addAll(getInterfaces(c));
                }
                interfacesAll.addAll(Arrays.asList(interfaces));
            }
            interfacesAll.add(clazz);
            return interfacesAll;
        }

    }

}
