package com.mh.base.quartz.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.mh.base.quartz.task.data.TaskData;

/**
 * 用作非并发缓存，只针对定时任务缓存。
 * @author cmj
 *
 */
public class JobsCache {

	private static Set<TaskData> jobsCaches = new ConcurrentHashSet<TaskData>(); //做缓存
	
	/**
	 * 添加缓存
	 * @param caches
	 */
	public synchronized static void addCaches(Collection<TaskData> caches) {
		jobsCaches.addAll(caches);
	}
	
	/**
	 * 获取当前缓存，该缓存为复制品
	 * @return
	 */
	public synchronized static List<TaskData> getCaches(){
		ConcurrentHashSet clone = ProtostuffUtils.decode(ProtostuffUtils.encode(jobsCaches), ConcurrentHashSet.class);
		return new ArrayList<TaskData>(clone);
	}
	
	/**
	 * 清空缓存
	 */
	public synchronized static void cleanAll() {
		jobsCaches = new ConcurrentHashSet<TaskData>();
	}
	
	
	
	
	
	
}
