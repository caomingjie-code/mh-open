package com.javaoffers.mh.db.router.common;

/**
 * @Description: 路由对象
 * @Auther: create by cmj on 2021/2/3 19:17
 */
public class Router {

    private String routerName;
    private boolean isForce ; //强制性陆游，指优先级高于读写分离，强制使用该陆游中的数据库链接
    private boolean isSham = true; //是否是虚假陆游，指该陆游被压入栈顶，但从未获取过对应的链接,(默认是虚假链接)

    public boolean isForce() {
        return isForce;
    }

    public void setForce(boolean force) {
        isForce = force;
    }

    public String getRouterName() {
        return routerName;
    }

    public void setRouterName(String routerName) {
        this.routerName = routerName;
    }

    public Router(String routerName, boolean isForce) {
        this.routerName = routerName;
        this.isForce = isForce;
    }

    public boolean isSham() {
        return isSham;
    }

    public void setSham(boolean sham) {
        isSham = sham;
    }

    public Router() {
    }
}
