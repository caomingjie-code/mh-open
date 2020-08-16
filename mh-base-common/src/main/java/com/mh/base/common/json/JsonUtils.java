package com.mh.base.common.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.NumberDeserializer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.*;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * create by cmj on 2020-06-01
 */
public class JsonUtils extends JSON {


    /**
     * 迭代查找 key -> value，解决 json 深层查找时需要大量的get方法
     * @param jsonObject
     * @param key
     * @return  返回最近的key 对应的 value
     */
    public static Object ddGet(String jsonObject,String key) {
        if(StringUtils.isNotBlank(jsonObject)&& StringUtils.isNotBlank(key)){
            com.alibaba.fastjson.JSONObject jo = (JSONObject) JSON.parseObject(jsonObject);
            return ddGet(jo,key);
        }
        return null;
    }


    /**
     * 迭代查找 key -> value，解决 json 深层查找时需要大量的get方法
     * @param jsonObject
     * @param key
     * @return  返回最近的key 对应的 value
     */
    public static Object ddGet(com.alibaba.fastjson.JSONObject jsonObject,String key) {
        if(jsonObject!=null&&key!=null){
            //将json中的所有key解析出来
            LinkedList<KeyData> keyDatas = new LinkedList<>();
            dd(JsonUtils.parseObject(jsonObject.toJSONString()),keyDatas);
            //查找最近的key，
            for(KeyData kd : keyDatas){
                if(key.equals(kd.key)){
                    return kd.data;
                }
            }
        }
        return null;
    }




    /**
     * 迭代设置 key -> value, 解决 json 深层k -v 修改时需要大量的getter和setter
     * @param jo
     * @param key
     * @param value
     * @return  修改最近值，返回true 代表修改成功
     */
    public static boolean ddSet(com.alibaba.fastjson.JSONObject jo,String key,Object value){
        if(jo!=null&&value!=null){
            //将json中的所有key解析出来
            LinkedList<KeyData> keyDatas = new LinkedList<>();
            dd(JsonUtils.parseObject(jo.toJSONString()),keyDatas);
            //设置最近的key，
            for(KeyData kd : keyDatas){
                if(key.equals(kd.key)){
                    kd.entry.setValue(value);
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 迭代设置 key -> value, 解决 json 深层k -v 修改时需要大量的getter和setter
     * @param jsonObject
     * @param key
     * @param value
     * @return  修改后的jsonObject
     */
    public static com.alibaba.fastjson.JSONObject ddSet(String jsonObject,String key,Object value){
        //将json中的所有key解析出来
        com.alibaba.fastjson.JSONObject jo = (JSONObject) JSON.parseObject(jsonObject);
        ddSet(jo,key,value);
        return jo;
    }

    /**
     * 迭代查找，
     * @param map
     * @param keyDatas
     */
    private static void dd(Map map, LinkedList<KeyData> keyDatas){
        if(map==null || map.size()==0 || keyDatas == null){
            return;
        }
        Set<Map.Entry> sets = map.entrySet();
        for(Map.Entry entry : sets){
            Object k = entry.getKey();
            Object v = entry.getValue();
            KeyData keyData = new KeyData(k+"", v,entry);
            keyDatas.add(keyData);
            if(v!=null && v instanceof Map){
                Map m = (Map)v;
                dd(m,keyDatas);
            }
            if(v!=null && v instanceof Collection && ((Collection) v).size()>0){
                Collection ll = (Collection)v;
                ddList(ll,keyDatas);
            }
        }

    }

    /**
     * 迭代jsonArray查询
     * @param list
     * @param keyDatas
     */
    private static void ddList(Collection<Object> list, LinkedList<KeyData> keyDatas){
        if(list!=null&&list.size()>0){
            for(Object obj : list){
                if(obj instanceof Map){
                    Map map = (Map)obj;
                    dd(map,keyDatas);
                }
                if(obj instanceof Collection){
                    Collection ll = (Collection)obj;
                    ddList(ll,keyDatas);
                }
            }
        }
    }

    /**
     * key -> value 的封装
     */
    static class KeyData implements Comparable<KeyData>{
        String key = "";
        Object data;
        Map.Entry entry;
        public KeyData(String key, Object data,Map.Entry entry) {
            this.key = key;
            this.data = data;
            this.entry = entry;
        }

        @Override
        public int compareTo(KeyData o) {
            return this.key.compareTo(o.key);
        }
    }

}
