package com.javaoffers.dao;

import com.javaoffers.base.common.log.LogUtils;
import com.javaoffers.base.common.rpc.HttpClientUtils;
import org.junit.Test;

/**
 * @Description: 测试
 * @Auther: create by cmj on 2021/2/20 10:02
 */
public class DaoClient {

    public static String host = "http://localhost:7070";
    @Test
    public void test() throws Exception {
        String data = HttpClientUtils.getData(host+"/testqueryDataForT4");
        LogUtils.printLog(data);
    }
}
