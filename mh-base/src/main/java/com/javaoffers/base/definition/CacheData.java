package com.javaoffers.base.definition;

import java.io.Serializable;

/**
 *    缓存数据对象
 * @author mj
 *
 */
public class CacheData implements Serializable{
	private static final long serialVersionUID = 1L;
	private Object data;

	public CacheData(Object data) {
		super();
		this.data = data;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
