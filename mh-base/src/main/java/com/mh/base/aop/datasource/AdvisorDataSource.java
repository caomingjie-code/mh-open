package com.mh.base.aop.datasource;


import com.mh.base.annotation.datasource.DataSourceRoute;
import org.aopalliance.aop.Advice;
import org.springframework.aop.*;
import org.springframework.aop.support.annotation.AnnotationClassFilter;
import org.springframework.aop.support.annotation.AnnotationMethodMatcher;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @Description: 研究 Advisor, 注意和 Advice 的关系和区别
 * @Auther: create by cmj on 2020/8/28 13:04
 */
@Component
public class AdvisorDataSource implements Advisor, PointcutAdvisor {

    private String name = "cmj"; //只做一个标记不起作用

    Advice advice = new AdviceDataSourceForAdvisor();

    @Override
    public Advice getAdvice() {
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


    }

}
