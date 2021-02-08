package com.javaoffers.mh.db.router.common;

/**
 * @Description: 路由对象
 * @Auther: create by cmj on 2021/2/3 19:17
 */
public class Router {

    private String routerName; //准备陆游的名称
    private boolean isForce =false; //强制性陆游，指优先级高于读写分离，强制使用该陆游中的数据库链接
    private boolean isSham = true; //是否是虚假陆游，指该陆游被压入栈顶，但从未获取过对应的链接,(默认是虚假链接), 解决meanClean中存在虚假陆游，否则有可能在关闭链接时拿到的是虚假陆游的链接造成链接浪费
    private boolean isTemporary = false; //是否是临时陆游， 应用场景：没有经过router aop(路由拦截器)并且在读写分离的场景下时， 则为true.为true时则在获取对应的链接后立即移除栈顶。(默认为false,不是临时陆游)
    private boolean isExtends = false;//是否属于继承陆游

    public boolean isExtends() {
        return isExtends;
    }

    public void setExtends(boolean anExtends) {
        isExtends = anExtends;
    }

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

    public boolean isTemporary() {
        return isTemporary;
    }

    public void setTemporary(boolean temporary) {
        isTemporary = temporary;
    }

    public boolean isSham() {
        return isSham;
    }

    public void setSham(boolean sham) {
        isSham = sham;
    }

    public Router() {
    }
    public Router(boolean isTemporary) {
        this.isTemporary = isTemporary;
    }

    public Router(String routerName, boolean isForce) {
        this.routerName = routerName;
        this.isForce = isForce;
    }

    public Router(String routerName, boolean isForce, boolean isExtends) {
        this.routerName = routerName;
        this.isForce = isForce;
        this.isExtends = isExtends;
    }

    public Router(String routerName) {
        this.routerName = routerName;
    }
}
