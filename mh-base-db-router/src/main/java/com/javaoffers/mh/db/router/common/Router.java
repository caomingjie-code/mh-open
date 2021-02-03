package com.javaoffers.mh.db.router.common;

/**
 * @Description: 路由对象
 * @Auther: create by cmj on 2021/2/3 19:17
 */
public class Router {

    private String routerName;
    private boolean isForce ;

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

    public Router() {
    }
}
