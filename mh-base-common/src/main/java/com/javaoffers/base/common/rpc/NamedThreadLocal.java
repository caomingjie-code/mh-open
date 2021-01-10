package com.javaoffers.base.common.rpc;

/**
 * @Description:
 * @Auther: create by cmj on 2021/1/10 00:14
 */
public class NamedThreadLocal<T> extends ThreadLocal<T> {
    private String  name;

    public NamedThreadLocal(String name) {
        this.name = name;
    }
}
