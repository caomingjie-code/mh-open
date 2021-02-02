package com.javaoffers.mh.db.router.common;

import org.apache.commons.lang3.StringUtils;

/**
 * @Description: sql工具类
 * @Auther: create by cmj on 2021/2/1 16:24
 */
public class SQLUtils {

    /**
     * 检查sql是否为 读sql
     * @param sql
     * @return
     */
    public static boolean checkedSqlIsRead(String sql){
        if(StringUtils.isNotBlank(sql)){
            sql = sql.replaceAll(" ", "");//去掉所有空格
            sql = sql.toLowerCase(); //转为小写
            if(sql.indexOf("select")==0){
                return true;
            }
        }
        return false;
    }



}
