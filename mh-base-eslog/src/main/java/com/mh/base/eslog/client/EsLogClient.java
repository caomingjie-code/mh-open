package com.mh.base.eslog.client;

import org.apache.http.entity.StringEntity;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @Description: es 客户端
 * @Auther: create by cmj on 2020/8/19 11:41
 */
@Component
public class EsLogClient {

    @Resource
    RestClient restClient;

    /**
     * 保存或更新
     * @param endPoint  Indices/Types/Documents
     * @param jsonData  保存数据
     * @throws Exception
     */
    public void putJson(String endPoint,String jsonData) throws Exception {
        //设置json头
        Request request = new Request("put",endPoint);
        RequestOptions.Builder builder = request.getOptions().toBuilder();
        builder.addHeader("content-type","application/json");
        request.setOptions(builder);
        StringEntity stringEntity = new StringEntity(jsonData);
        request.setEntity(stringEntity);
        restClient.performRequest(request);
    }

}


