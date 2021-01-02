package com.javaoffers.base.common.rpc;

import com.javaoffers.base.common.json.JsonUtils;
import com.javaoffers.base.common.utils.Utils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * create by cmj
 * 方法命名以 httpMethod + body参数的数据结构为主:  postJsonStr(String url, String bodyJson)
 * 如果body缺失,或body 为Map,则以 'Data' 补充  : getData(String url) 缺失
 *                                            postData(String targetUrl, Map<String, String> bodyParam, Map<String, String> headers)  body为Map
 */
public class HttpClientUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);
    private static final NamedThreadLocal<Integer> httpResponseCode = new NamedThreadLocal<>("response status code");//存放每个请求对应的响应状态码,如 200,300,400,500等
    private static final NamedThreadLocal<String>  httpResponseErrorMasg = new NamedThreadLocal<>("response masg error");//存放响应错误请求的数据
    private static final CopyOnWriteArraySet<Integer> globalThrowsCode = new CopyOnWriteArraySet<>();//异常code, 如果设置了throws code, 则跑出对应的异常,全局
    private static final String NULL = "";




    /**
     * 自定义request方法请求,body 为字符类型
     *
     * @param requestMethod
     * @param bodyStr
     * @param headerParam
     * @return
     */
    public static String requestMethodsHttpData(HttpRequestBase requestMethod, Map<String,Object> urlParam, String bodyStr, Map<String, String> headerParam) throws Exception {
        logger.info("执行 rpc 请求体: bodyStr->| "+bodyStr+" |");
        HttpEntity resEntity = requestMethodsHttpEntry(requestMethod, urlParam, new StringEntity(bodyStr), headerParam);
        return parseString( resEntity);
    }


    /**
     * 自定义request方法请求,body为字节类型
     *
     * @param requestMethod
     * @param bodyStr
     * @param headerParam
     * @return
     */
    public static HttpEntity requestMethodsHttpByteData(HttpRequestBase requestMethod, Map<String,Object> urlParam, byte[] bodyStr, Map<String, String> headerParam) throws Exception {
        logger.info("执行 rpc 请求体: bodyStr->| "+ Arrays.toString(bodyStr) +" |");
        return requestMethodsHttpEntry(requestMethod,urlParam,new ByteArrayEntity(bodyStr),headerParam);
    }


    /**
     * 直接发送 enry 数据
     * @param url
     * @param httpEntity
     * @param headers
     * @return
     * @throws Exception
     */
    public static HttpEntity postHttpEntry(String url, HttpEntity httpEntity, Map<String,String> headers) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        return requestMethodsHttpEntry(httpPost,Collections.emptyMap(),httpEntity,headers);
    }

    /**
     * 自定义request方法请求,body为字节类型
     *
     * @param requestMethod
     * @param headerParam
     * @return
     */
    public static HttpEntity requestMethodsHttpEntry(HttpRequestBase requestMethod, Map<String,Object> urlParam, HttpEntity httpEntityBody, Map<String, String> headerParam) throws Exception {
        HttpEntity httpEntity = null;
        HttpClient httpClient = new SSLClient();

        try {
            //设置头
            buildHeaders(headerParam, requestMethod);

            //拼接url 参数
            buildUrlParams(urlParam, requestMethod);

            //设置body entry
            buildBody(httpEntityBody, requestMethod);

            //发送
            httpEntity = send( httpClient, requestMethod);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return httpEntity;
    }

    /**
     * 自定义request方法请求,body为字节类型
     *
     * @param requestMethod
     * @param headerParam
     * @return
     */
    public static HttpResponse requestMethodsHttpResponse(HttpRequestBase requestMethod, Map<String,Object> urlParam, HttpEntity httpEntityBody, Map<String, String> headerParam) throws Exception {
        HttpClient httpClient = new SSLClient();
        HttpResponse response = null;
        try {
            //设置头
            buildHeaders(headerParam, requestMethod);

            //拼接url 参数
            buildUrlParams(urlParam, requestMethod);

            //设置body entry
            buildBody(httpEntityBody, requestMethod);

            //发送
            response = sendBackHttpResponse( httpClient, requestMethod);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return response;
    }

    /**
     * 将 httpEntity解析为String
     * @param resEntity
     * @return 返回string 数据
     * @throws IOException
     */
    public static String parseString(HttpEntity resEntity) throws IOException {
        String responseStr = null;
        if(resEntity!=null){
            responseStr = EntityUtils.toString(resEntity, "UTF-8");
            httpResponseErrorMasg.set(responseStr);//存放每次请求响应的数据,如果出错时,可以从 httpResponseErrorMasg 中获取错误的响应数据
            logger.warn("执行 rpc 的请求结果: responseBody -> "+responseStr); //打印响应信息
        }
        return responseStr;
    }






    /**
     * get 请求
     * @param url
     * @return
     */
    public static String getData(String url) throws Exception {
        HttpGet httpGet = new HttpGet(url);
        String s = requestMethodsHttpData(httpGet, Collections.emptyMap(), NULL, Collections.EMPTY_MAP);
        return s;
    }


    /**
     * get 请求
     * @param url
     * @param headers
     * @return
     */
    public static String getData(String url, Map<String, String> headers) throws Exception {
        HttpGet httpGet = new HttpGet(url);
        String s = requestMethodsHttpData(httpGet, Collections.emptyMap(), NULL, headers);
        return s;
    }

    /**
     * get 请求
     * @param url
     * @param headers
     * @return
     */
    public static <T> T getData(String url, Map<String, String> headers,Result<T> result) throws Exception {
        String s = getData(url, headers);
        T apply = result.apply(s);
        return apply;
    }


    /**
     * get 请求
     * @param url
     * @param headers
     * @return
     */
    public static String getData(String url,Map<String,Object> formParam, Map<String, String> headers) throws Exception {
        HttpGet httpGet = new HttpGet(url);
        String s = requestMethodsHttpData(httpGet, formParam, NULL, headers);
        return s;
    }


    /**
     * get 请求
     * @param url
     * @param headers
     * @param formParam 表单参数
     * @return
     */
    public static <T> T getData(String url, Map<String,Object> formParam,Map<String, String> headers,Result<T> result) throws Exception {
        HttpGet httpGet = new HttpGet(url);
        String s = requestMethodsHttpData(httpGet, formParam, NULL, headers);
        T apply = result.apply(s);
        return apply;
    }

    /**
     * 发送xml数据
     *
     * @param url
     * @param xml
     * @return
     */
    public static String postXML(String url, String xml)throws Exception {
        //httpPost.setHeader("Content-Type", "text/xml; charset=UTF-8");
        HttpPost httpPost = new HttpPost(url);
        HashMap<String, String> headers = Utils.startBuildParam("Content-Type", "text/xml; charset=UTF-8").endBuildStringParam();
        String s = requestMethodsHttpData(httpPost, Collections.emptyMap(), xml, headers);
        return s;
    }

    /**
     * 以post的方式发送字节数据
     * @param url
     * @param bytes
     * @return
     * @throws Exception
     */
    public static String postBytes(String url, byte[] bytes, Map<String,String> headers )throws Exception {
        HttpPost httpPost = new HttpPost(url);
        HttpEntity httpEntity = requestMethodsHttpByteData(httpPost, Collections.emptyMap(), bytes, headers);
        return parseString(httpEntity);
    }



    /**
     * 以post方式发送json数据,header 自定义.
     * @param targetUrl
     * @param bodyParam
     * @param headers
     * @return
     */
    public static String postData(String targetUrl, Map<String, Object> bodyParam, Map<String, String> headers) throws Exception {
        HttpPost httpPost = new HttpPost(targetUrl);
        headers.put("Content-Type", "application/json");
        return requestMethodsHttpData(httpPost, Collections.emptyMap(), JsonUtils.toJSONString(bodyParam), headers);
    }

    /**
     * 以post的方式发送 param 数据,
     * @param targetUrl
     * @param bodyParam
     * @param headers
     * @return
     * @throws Exception
     */
    public static String postParamData(String targetUrl,Map<String, Object> bodyParam, Map<String, String> headers) throws Exception {
        HttpPost httpPost = new HttpPost(targetUrl);
        Map headers_ = Utils.startBuildParam("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8").endBuildStringParam();
        headers_.putAll(headers);
        return requestMethodsHttpData(httpPost, Collections.emptyMap(), getParamData(new HashMap<String,Object>(bodyParam)), headers_);
    }

    /**
     * 以post的方式发送 param 数据,
     * @param targetUrl
     * @param bodyParam
     * @return
     * @throws Exception
     */
    public static String postParamData(String targetUrl,Map<String, Object> bodyParam) throws Exception {
        return postParamData(targetUrl,bodyParam,Collections.emptyMap());
    }

    /**
     * 以post的方式发送 param 数据,
     * @param targetUrl
     * @param bodyParam 该对象会转换成Map
     * @return
     * @throws Exception
     */
    public static String postParamData(String targetUrl,Object bodyParam) throws Exception {
        return postParamData(targetUrl,JsonUtils.parseObject(JsonUtils.toJSONString(bodyParam),Map.class),Collections.emptyMap());
    }

    /**
     * 发送 json 格式数据
     *
     * @param url
     * @param bodyJson
     * @return
     */
    public static String postJsonStr(String url, String bodyJson) throws Exception {
        //httpPost.setHeader("Content-Type", "application/json");
        String s = postJsonStr(url, bodyJson, new HashMap<String,String>());
        return s;
    }

    /**
     * 发送 json 格式数据
     *
     * @param url
     * @param bodyJson
     * @return
     */
    public static String postJsonStr(String url, String bodyJson,Map<String,String> headers) throws Exception {
        // httpPost.setHeader("Content-Type", "application/json");
        HttpPost httpPost = new HttpPost(url);
        headers.put("Content-Type", "application/json");
        String s = requestMethodsHttpData(httpPost, Collections.emptyMap(), bodyJson, headers);
        return s;
    }

    /**
     * 发送 json 格式数据
     *
     * @param url
     * @param bodyJson
     * @return
     */
    public static <T>T  postJsonStr(String url, String bodyJson,Map<String,String> headers,Result<T> r) throws Exception {
        // httpPost.setHeader("Content-Type", "application/json");
        String jsonStr = postJsonStr(url, bodyJson, headers);
        return r.apply(jsonStr);
    }


    /**
     * 发送 json 格式数据
     *
     * @param url
     * @param bodyJson
     * @return
     */
    public static String patchJsonStr(String url, String bodyJson,Map<String,String> headers) throws Exception {
        //httpPost.setHeader("Content-Type", "application/json-patch+json");
        HttpPatch httpPatch = new HttpPatch(url);
        headers.put("Content-Type", "application/json-patch+json");
        String s = requestMethodsHttpData(httpPatch, Collections.emptyMap(), bodyJson, headers);
        return s;
    }

    /**
     * 发送 json 格式数据
     *
     * @param url
     * @param bodyJson
     * @return
     */
    public static String patchJsonStr(String url, String bodyJson) throws Exception {
        //httpPost.setHeader("Content-Type", "application/json-patch+json");
        String s = patchJsonStr(url, bodyJson, new HashMap<String,String>());
        return s;
    }
    /**
     * put请求
     * @param url
     * @param bodyJson
     * @param headers
     * @return
     */
    public static String putJsonStr(String url,String bodyJson,Map<String,String> headers) throws Exception {
        //httpPost.setHeader("Content-Type","application/json");
        HttpPut httpPut = new HttpPut(url);
        headers.put("Content-Type","application/json");
        String s = requestMethodsHttpData(httpPut, Collections.emptyMap(), bodyJson, headers);
        return s;
    }

    /**
     * put请求
     * @param url
     * @param bodyJson
     * @return
     */
    public static String putJsonStr(String url,String bodyJson) throws Exception {
        //httpPost.setHeader("Content-Type","application/json");
        HttpPut httpPut = new HttpPut(url);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type","application/json");
        String s = requestMethodsHttpData(httpPut, Collections.emptyMap(), bodyJson, headers);
        return s;
    }


    /**
     * put请求
     * @param url
     * @param bodyJson
     * @return
     */
    public static HttpResponse putJsonStrBackHttpResponse(String url,String bodyJson) throws Exception {
        //httpPost.setHeader("Content-Type","application/json");
        HttpPut httpPut = new HttpPut(url);
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type","application/json");

        HttpResponse response = requestMethodsHttpResponse(httpPut, Collections.emptyMap(), new StringEntity(bodyJson), headers);
        return response;
    }




    /**
     * put请求
     * @param url
     * @param bodyJson
     * @param headers
     * @return
     */
    public static <T> T putJsonStr(String url,String bodyJson,Map<String,String> headers,Result<T> r) throws Exception {
        //httpPost.setHeader("Content-Type","application/json");
        HttpPut httpPut = new HttpPut(url);
        headers.put("Content-Type","application/json");
        String s = requestMethodsHttpData(httpPut, Collections.emptyMap(), bodyJson, headers);
        T apply = r.apply(s);
        return apply;
    }

    /**
     * 删除数据
     * @param url
     * @param urlParam
     * @param bodyStr
     * @param headerParam
     * @return
     * @throws Exception
     */
    public static int deleteData(String url,Map<String,Object> urlParam, String bodyStr, Map<String, String> headerParam) throws Exception {
        HttpDelete httpDelete = new HttpDelete(url);
        requestMethodsHttpData(httpDelete,urlParam,bodyStr,headerParam);
        return watchResponseCode();
    }


    /**
     * 删除数据
     * @param url
     * @param urlParam
     * @param headerParam
     * @return
     * @throws Exception
     */
    public static int deleteData(String url,Map<String,Object> urlParam,  Map<String, String> headerParam) throws Exception {
        return deleteData(url,urlParam,NULL,headerParam);
    }

    /**
     * 删除数据
     * @param url
     * @param urlParam
     * @return
     * @throws Exception
     */
    public static int deleteData(String url,Map<String,Object> urlParam) throws Exception {
        return deleteData(url,urlParam,Collections.emptyMap());
    }

    /**
     * 删除数据
     * @param url
     * @return
     * @throws Exception
     */
    public static int deleteData(String url) throws Exception {
        return deleteData(url,Collections.emptyMap());
    }

    /**
     * 查看当前线程最后一次请求的响应状态码
     * @return
     */
    public static int watchResponseCode(){
        Integer responseCode = httpResponseCode.get();
        return responseCode;
    }

    /**
     * 查看当前线程最后一次请求的错误响应的数据
     * @return
     */
    public static String watchResponseErrorMasg(){
        String responseMasg = httpResponseErrorMasg.get();
        return responseMasg;
    }

    @FunctionalInterface
    public  interface Result<T>{
        public  T apply(String t);
    }

    /**
     * 发送请求, 该方法只关注响应状态,不关心响应数据实体.并会将响应实体返回给具体的业务处理
     * @param httpClient
     * @param httpMethod
     * @return  返回响应数据
     * @throws IOException
     */
    private static HttpEntity send( HttpClient httpClient, HttpRequestBase httpMethod) throws Exception {
        HttpEntity resEntity = null;
        long startTime = System.currentTimeMillis();    //获取开始时间
        logger.warn("执行 rpc 调用开始: url->[ "+httpMethod.getURI()+"]");
        HttpResponse response = httpClient.execute(httpMethod);//开始执行
        long endTime = System.currentTimeMillis();    //获取结束时间
        if (response != null) {
            StatusLine statusLine = response.getStatusLine();
            httpResponseCode.set(statusLine.getStatusCode());//储存响应code
            logger.warn("执行 rpc 调用结束: 响应状态 -> [ " + statusLine+"] ");
            resEntity = response.getEntity();
            globalThrowsCode(httpResponseCode.get());//指定需要抛出的异常code
        }
        logger.warn("执行 rpc 调用请求耗时: " + (endTime - startTime) + "ms*");
        return resEntity;
    }

    /**
     * 发送请求,将 HttpResponse 返回
     * @param httpClient
     * @param httpMethod
     * @return  返回响应数据
     * @throws IOException
     */
    private static HttpResponse sendBackHttpResponse( HttpClient httpClient, HttpRequestBase httpMethod) throws Exception {
        long startTime = System.currentTimeMillis();    //获取开始时间
        logger.warn("执行 rpc 调用开始: url->[ "+httpMethod.getURI()+"]");
        HttpResponse response = httpClient.execute(httpMethod);//开始执行
        long endTime = System.currentTimeMillis();    //获取结束时间
        if (response != null) {
            StatusLine statusLine = response.getStatusLine();
            httpResponseCode.set(statusLine.getStatusCode());//储存响应code
            logger.warn("执行 rpc 调用结束: 响应状态 -> [ " + statusLine+"] ");
            globalThrowsCode(httpResponseCode.get());//指定需要抛出的异常code
        }
        logger.warn("执行 rpc 调用请求耗时: " + (endTime - startTime) + "ms*");
        return response;
    }



    /**
     * 抛出全局设置的异常
     * @param statusCode
     */
    private static void globalThrowsCode(int statusCode) throws Exception{
        for(Integer code : globalThrowsCode){
            if(statusCode == code){
                throw new HttpRequestException(HttpClientUtils.watchResponseErrorMasg());
            }
        }
    }



    /**
     * 构建参body
     * @param bodyStr
     * @param httpMethod
     * @throws UnsupportedEncodingException
     */
    private static void buildBody(byte[] bodyStr, HttpRequestBase httpMethod) throws UnsupportedEncodingException {
        if(bodyStr!=null&&bodyStr.length>0){
            ByteArrayEntity entityParams = new ByteArrayEntity(bodyStr);
            if(httpMethod instanceof HttpEntityEnclosingRequest){
                HttpEntityEnclosingRequest bodyMethod = (HttpEntityEnclosingRequest) httpMethod;
                bodyMethod.setEntity(entityParams);
            }
        }
    }

    /**
     * 构建参原始body entry
     * @param httpMethod
     * @throws UnsupportedEncodingException
     */
    private static void buildBody(HttpEntity enetry, HttpRequestBase httpMethod) throws UnsupportedEncodingException {
        if(enetry!=null){
            if(httpMethod instanceof HttpEntityEnclosingRequest){
                HttpEntityEnclosingRequest bodyMethod = (HttpEntityEnclosingRequest) httpMethod;
                bodyMethod.setEntity(enetry);
            }
        }
    }

    /**
     * 构建url 拼接参数
     * @param urlParam
     * @param requestMethod
     */
    private static void buildUrlParams(Map<String, Object> urlParam, HttpRequestBase requestMethod) throws URISyntaxException {
        String uri = requestMethod.getURI().toString();
        //拼接参数
        if(urlParam!=null&&urlParam.size()>0){
            uri = uri+"?"+getParamData(urlParam);
        }
        //重新设置uri
        requestMethod.setURI(new URI(uri));

    }

    /**
     * 拼接表单参数
     * @param urlParam
     * @return
     */
    private static String  getParamData(Map<String, Object> urlParam) {
        StringBuffer parmBuffer = new StringBuffer("");
        urlParam.forEach((k,v)->{
            //v只分两种类型: String , List<String>
            if(v!=null && v instanceof String ){
                parmBuffer.append(k+"="+v+"&");
            }else if(v!=null && v instanceof Collection){
                Collection lv = (Collection)v;
                lv.forEach(s->{
                    String strPram  = s+"";
                    parmBuffer.append(k+"="+strPram+"&");
                });
            }else if(v!=null && v.getClass().isArray()){ //只支持一维数组
                int length = Array.getLength(v);
                for(int i=0; i<length;i++){
                    Object s = Array.get(v, i);
                    String strPram  = s+"";
                    parmBuffer.append(k+"="+strPram+"&");
                }
            }
        });
        return parmBuffer.toString();
    }

    /**
     * 构建请求header
     * @param headerParam
     * @param httpMethod
     */
    private static void buildHeaders(Map<String, String> headerParam, HttpRequestBase httpMethod) {
        if(headerParam!=null){
            headerParam.forEach((k,v)->{
                httpMethod.addHeader(k+"", v+"");
            });
        }
    }
    /**
     * 异常信息
     */
    static class HttpRequestException extends Exception{
        private Integer code;
        public HttpRequestException(String message) {
            super(message);
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }
    }


}
