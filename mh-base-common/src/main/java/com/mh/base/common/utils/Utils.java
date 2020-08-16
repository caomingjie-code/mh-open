package com.mh.base.common.utils;
import org.apache.commons.lang3.StringUtils;
import java.math.BigInteger;
import java.util.HashMap;

public final class Utils {


    /**
     * create by cmj
     * 连续构建参数
     * @param key
     * @param value
     * @return
     */
    public static <V> Utils startBuildParam(String key,Object value){
        HashMap<String,Object> param = new HashMap<>();
        param.put(key,value);
        Utils udrUtils = new Utils();
        udrUtils.param = param;
        return udrUtils;
    }
    public Utils buildParam(String key,Object value){
        this.param.put(key,value);
        return this;
    }
    HashMap<String,Object> param = new HashMap<>();//防止new对象调用 buildParm时 param为 null
    public HashMap<String,Object> endBuildParam(){
        return this.param;
    }

    public HashMap<String,String> endBuildStringParam(){
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        this.param.forEach((k,v)->{
            stringStringHashMap.put(k,v+"");
        });
        return stringStringHashMap;
    }





    /**
     * 返回 16 进制的长度 必须是 偶数,  不够的高位补0
     * @param sequenceNumber 16进制的字符
     * @param addNum
     * @return
     */
    public static  String hexAdd( String sequenceNumber,long addNum) {
        if(StringUtils.isNotBlank(sequenceNumber)){
            BigInteger bigint=new BigInteger(sequenceNumber, 16);
            long l = bigint.longValue();
            l= l+addNum;
            String hexString = Long.toHexString(l);
            if(hexString.length() % 2 != 0){
                hexString = "0"+hexString;
            }
            //保证长度为不小于12 的偶数
            if(hexString.length()<12){
                for(;;){
                    hexString="0"+hexString;
                    if(!(hexString.length()<12)){
                        break;
                    }
                }
            }
            return hexString;
        }
        return null;
    }

}
