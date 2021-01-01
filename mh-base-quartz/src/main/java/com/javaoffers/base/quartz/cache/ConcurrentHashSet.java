package com.javaoffers.base.quartz.cache;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName: ConcurrentHashSet
 * @Author: 曹明杰
 * @Description: 并发性安全性较高的Set集合
 * @Date: 2019/7/30 18:42
 * @Version: 1.0
 */
public class ConcurrentHashSet<E> implements Set<E> {

    private Object value = new Object();
    private ConcurrentHashMap<E,Object> dataContent = new ConcurrentHashMap<E,Object>(); //存放数据

    @Override
    public int size() {
        return dataContent.keySet().size();
    }

    @Override
    public boolean isEmpty() {
        return dataContent.keySet().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return dataContent.keySet().contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return dataContent.keySet().iterator();
    }

    @Override
    public Object[] toArray() {
        return dataContent.keySet().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return dataContent.keySet().toArray(a);
    }

    @Override
    public boolean add(E e) {
        Object put = dataContent.put(e, value);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        Object remove = dataContent.remove(o);
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return dataContent.keySet().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if(c!=null&&c.size()>0){
            for(E e : c){
                dataContent.put(e,value);
            }
        }
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return dataContent.keySet().retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if(c!=null&&c.size()>0){
            Iterator<?> iterator = c.iterator();
            while(iterator.hasNext()){
                Object next = iterator.next();
                dataContent.remove(next);
            }
        }
        return false;
    }

    @Override
    public void clear() {
        dataContent.clear();
        dataContent = new ConcurrentHashMap<E,Object>();
    }
}
