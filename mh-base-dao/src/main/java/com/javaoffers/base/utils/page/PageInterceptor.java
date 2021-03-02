package com.javaoffers.base.utils.page;

import com.javaoffers.base.dao.impl.FPage;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @Description: 做分页
 * @Auther: create by cmj on 2020/9/25 14:57
 */
@Intercepts({@Signature(
        type= StatementHandler.class,
        method = "prepare",
        args = {Connection.class,Integer.class})})
public class PageInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler)invocation.getTarget();
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        Object methodParam = metaObject.getValue("delegate.parameterHandler.parameterObject");
        if(methodParam instanceof FPage){
            startingLimit(metaObject, (FPage) methodParam);
        }
        return invocation.proceed();
    }

    //开始做分页
    private void startingLimit(MetaObject metaObject, FPage fPage) {

        //获取原始sql
        String sql = (String) metaObject.getValue("delegate.boundSql.sql");

        //分页sql后缀
        String limit = " limit "+ fPage.getBegeinIndex() +","+ fPage.getPageSize();

        //处理分页sql
        processSql(metaObject, fPage, sql, limit);

    }

    //处理分页sql, 以allCount是否大于0作为区分是否需要sql统计 allCount(总条数)
    private void processSql(MetaObject metaObject, FPage fPage, String sql, String limit) {
        if(fPage.getAllCount()==0){
            sql  =  "SELECT\n" +
                    "\to_alias_o.*,\n" +
                    "\tu_alias_u.* \n" +
                    "FROM\n" +
                    "\t( SELECT count(*) AS allCount FROM ( "+sql+" ) AS i_alias_i ) o_alias_o\n" +
                    "\tINNER JOIN ( "+sql+" ) u_alias_u \n" + limit;

            List value = (List)metaObject.getValue("delegate.boundSql.parameterMappings");
            ArrayList<Object> objects = new ArrayList<>();
            if(value!=null&&value.size()>0){
                for(int i=0;i<2;i++){
                    for(Object o : value){
                        objects.add(o);
                    }
                }
            }
            metaObject.setValue("delegate.boundSql.parameterMappings", objects);
        }else{
            sql =sql + limit;
        }
        metaObject.setValue("delegate.boundSql.sql", sql);
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target,this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
