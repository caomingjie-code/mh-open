package com.javaoffers.dao;

import com.javaoffers.base.common.rpc.HttpClientUtils;

/**
 * @Description: 测试
 * @Auther: create by cmj on 2021/2/20 10:02
 */
public class DaoClient {

    public void test() throws Exception {
        String data = HttpClientUtils.getData("localhost:7070/testqueryDataForT4");


    }
}
