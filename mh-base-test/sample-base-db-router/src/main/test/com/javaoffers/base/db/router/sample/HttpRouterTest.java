package com.javaoffers.base.db.router.sample;

import com.javaoffers.base.common.rpc.HttpClientUtils;
import org.junit.Test;

public class HttpRouterTest {


    @Test
    public void defaultRouter() throws Exception {
        HttpClientUtils.getData("http://localhost:8080/router/defaultRouter?num=10");
    }
    @Test
    public void defaultRouterWhile() throws Exception {
        HttpClientUtils.getData("http://localhost:8080/router/defaultRouterWhile?num=10");
    }
    @Test
    public void mulRouter() throws Exception {
        HttpClientUtils.getData("http://localhost:8080/router/mulRouter?num=10");
    }
    @Test
    public void mulRouter2() throws Exception {
        HttpClientUtils.getData("http://localhost:8080/router/mulRouter2?num=10");
    }
    @Test
    public void nestingRouter() throws Exception {
        HttpClientUtils.getData("http://localhost:8080/router/nestingRouter?num=10");
    }


    /*******************************************************************************/

    @Test
    public void readWriteSep() throws Exception {
        HttpClientUtils.getData("http://localhost:8080/readWrite/readWriteSep?num=10");
    }
    @Test
    public void readWriteSepNonTransaction() throws Exception {
        HttpClientUtils.getData("http://localhost:8080/readWrite/readWriteSepNonTransaction?num=10");
    }

    /*******************************************************************************/


    @Test
    public void noneRouter() throws Exception {
        HttpClientUtils.getData("http://localhost:8080/noneRouter/defaultRouterWhile?num=10");
    }



}
