package com.javaoffers.base.common.clone;

import com.javaoffers.base.common.protostuff.ProtostuffUtils;

/**
 * @ClassName: Clone
 * @Author: 曹明杰
 * @Description: 深度clone
 * @Date: 2019/8/15 14:37
 * @Version: 1.0
 */
public class Clone {
    /**
     *@Autour  cmj  on 2019/8/15 14:38
     *@Description : 深度clone
     */
    public static <E> E clone(E e){
        InnerCloneData innerCloneData = new InnerCloneData(e);
        InnerCloneData decode = (InnerCloneData)ProtostuffUtils.decode(ProtostuffUtils.encode(innerCloneData), InnerCloneData.class);
        E data = (E)decode.getData();
        return data;
    }
    /**
     *@Autour  cmj  on 2019/8/15 14:50
     *@Description : 内部类，封装数据
     */
     private static class InnerCloneData{
        Object data;

         public Object getData() {
             return data;
         }

         public void setData(Object data) {
             this.data = data;
         }

         public InnerCloneData(Object data) {
             this.data = data;
         }
     }
}
