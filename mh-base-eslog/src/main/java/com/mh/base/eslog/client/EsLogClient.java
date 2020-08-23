package com.mh.base.eslog.client;

import com.mh.base.eslog.conf.EsLogProperties;
import org.apache.http.entity.StringEntity;
import org.elasticsearch.client.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @Description: es 客户端
 * @Auther: create by cmj on 2020/8/19 11:41
 */
@EnableConfigurationProperties(EsLogProperties.class)
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
        StringEntity stringEntity = new StringEntity(jsonData,"UTF-8");
        request.setEntity(stringEntity);
        restClient.performRequestAsync(request,new NotProcessResponseListener());
    }

    //成功失败不做任何处理, 避免 Request cannot be executed; I/O reactor status: STOPPED
    static class NotProcessResponseListener implements ResponseListener{
        @Override
        public void onSuccess(Response response) {
            //不做处理
        }

        @Override
        public void onFailure(Exception exception) {
            //不做处理
        }
    }

}


