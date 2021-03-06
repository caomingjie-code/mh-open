package com.javaoffers.mh.db.router.datasource;

/**
 * 带有名称的ThreadLocal
 * crete by cmj
 */
public class ThreadLocalNaming<T> extends ThreadLocal<T> {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ThreadLocalNaming(String name) {
		super();
		this.name = name;
	}
	

}
