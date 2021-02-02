package com.javaoffers.mh.db.router.common;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @Description: 支持并发
 * @Auther: create by cmj on 2021/2/1 17:20
 */
public class ConcurrentMapSet<T,S> {

    private Map<T, ConcurrentSkipListSet> data = new ConcurrentHashMap<>();


    public int size() {
        return data.size();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public boolean containsKey(Object key) {
        return data.containsKey(key);
    }

    public boolean containsValue(Object value) {
        Collection<ConcurrentSkipListSet> values = data.values();
        if(values==null) return false;
        for(ConcurrentSkipListSet set : values){
            if(set.contains(value)){
                return true;
            }
        }
        return false;
    }

    public ConcurrentSkipListSet<S> get(Object key) {
        ConcurrentSkipListSet v = data.get(key);
        return v;
    }

    public boolean put(T key, S value) {
        ConcurrentSkipListSet v = data.get(key);
        if(v==null){
            synchronized (data){
                if(v==null)  v = new ConcurrentSkipListSet();
            }
            data.put(key,v);
        }
        v.add(value);
        return true;
    }

    public boolean remove(Object key) {
         if(data.remove(key)!=null) return true;
         return false;
    }

    public void clear() {
        data.clear();
    }

    public Set<T> keySet() {
        return data.keySet();
    }

    public Collection <ConcurrentSkipListSet> values() {
        return data.values();
    }

    public Set <Map.Entry<T, ConcurrentSkipListSet>> entrySet() {
        return data.entrySet();
    }
}
