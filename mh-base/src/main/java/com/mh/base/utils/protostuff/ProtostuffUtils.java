/**   
 * Copyright © 2019 mh615 Info.  All rights reserved.
 * 
 * 功能描述：
 * @Package: com.mh615.springstudy.redisutils 
 * @author: cmj   
 * @date: 2019-1-30 上午11:30:00 
 */
package com.mh.base.utils.protostuff;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**   
 * Copyright: Copyright (c) 2019 LanRu-Caifu
 * 
 * @ClassName: ProtostuffUtils.java
 * @Description: 该类的功能描述: 序列化和反序列使用
 *
 * @version: v1.0.0
 * @author: 曹明杰
 * @date: 2019-1-30 上午11:30:00 
 *
 * Modification History:
 * Date         Author          Version            Description
 *---------------------------------------------------------*
 * 2019-1-30     cmj           v1.0.0               修改原因
 */
public class ProtostuffUtils {

	/**
	 * 序列化
	 */
	public static <T> byte[] encode(T message){
		if(message==null) {
			return null;
		}
		RuntimeSchema schema = RuntimeSchema.createFrom(message.getClass());
		@SuppressWarnings("unchecked")
		byte[] bt = ProtostuffIOUtil.toByteArray(message, schema, LinkedBuffer.allocate());
		return bt;
	}
	/**
	 * 
	 * @Title: encode   
	 * @Description: TODO(作用：反序列化)   
	 * @param:       
	 * @return: void  
	 * @Auther: cmj
	 * @throws
	 */
	public static<T> T decode(byte[] bt,Class<T> clazz){
		if(bt==null||bt.length==0) {
			return null;
		}
		RuntimeSchema<T> schema = RuntimeSchema.createFrom(clazz);
		T message = schema.newMessage();
		try {
		ProtostuffIOUtil.mergeFrom(bt, message, schema);
		return message;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
}
