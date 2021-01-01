package com.javaoffers.base.quartz.task;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.StatefulJob;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import com.javaoffers.base.quartz.SchedulerUtil;
import com.javaoffers.base.quartz.cache.JobsCache;
import com.javaoffers.base.quartz.exception.BaseTaskException;
import com.javaoffers.base.quartz.task.data.TaskData;
import com.javaoffers.base.quartz.task.service.BaseQuartzService;
/**
 * 该Job的入口点,串行
 * @author cmj
 *
 */
@Component
public class BaseJob  implements StatefulJob,BeanPostProcessor{
	private static Logger _logger = LoggerFactory.getLogger(SchedulerUtil.class);// log4j记录日志
	
	static Boolean isStart = false;
	static BaseQuartzService bqs = null; 
	@Resource
	public void setBqs(BaseQuartzService bqs_) {
		 bqs = bqs_;
	}

	@Override
	public void execute(JobExecutionContext context) {
		List<TaskData> findAll = null;
		try {
			if(bqs!=null) {
				if(JobsCache.getCaches().size()==0) {
					findAll = bqs.queryDataForT("select * from  bs_task_data",TaskData.class);
					JobsCache.addCaches(findAll);
				}else {
					findAll = JobsCache.getCaches();
				}
				
			}else {
				_logger.error("BaseQuartzService 实例为 null,如果当前为项目为热部署重启则不需要关注");
			}
		} catch (Exception e) {
			_logger.error("BaseQuartzService 实例为 null,如果当前为项目为热部署重启则不需要关注");
		}
		
		//List<TaskData> findAll = bqs.findAll();
        
		if(findAll!=null&&findAll.size()!=0) {
			List<String> startingTaskNames = SchedulerUtil.getAllStartingTaskNames();
			filterDeleteTask(findAll, startingTaskNames);//数据库中的任务表中的一条记录删除时，对应task任务也应该被删除
			for(TaskData td : findAll) {
				try {
					check(td);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				String taskId = td.getTaskId()+"";
				Integer status = td.getStatus();
				
				
				if(SchedulerUtil.isStarting(taskId)) {
					
					if(status == 0) {
						SchedulerUtil.stopTask(taskId);
					}
				}else {
					if(status == 1) {
						//任务模式，0 此任务未完成，不允许启动新任务，1 此任务未完成允许启动新的任务
						Integer parallelism = td.getParallelism();
						if(parallelism==0) {
							SchedulerUtil.startStateTask(td);
						}
						if(parallelism==1) {
							SchedulerUtil.startSimpleTask(td);
						}
						//有次数限制时,声明周期由自己决定
						if(td.getCount()!=null&&td.getCount()>=0&&td.getStatus()==1) { //当前任务执行完count次数就会被停止，
							td.setStatus(2); //设置为2，由task自己决定
							bqs.save(td);
			    		}
					}
				}
			}
		}
	}

	private void filterDeleteTask(List<TaskData> findAll, List<String> startingTaskIds) {
		for(String taskId : startingTaskIds) {
			boolean isStopTask = true;
			for(TaskData td : findAll) { //过滤数据中一条task被删除时，也要将被删除的task停止
				if(taskId.equals(td.getTaskId().toString())) {
					isStopTask = false;
				}
			}
			if(isStopTask) {
				if(!taskId.equals("BaseQuartzJob")) {
					SchedulerUtil.stopTask(taskId);	
				}
				
			}
		}
	}
	public  void  check(TaskData task) throws Exception {
		String jobName = task.getJobName();
		Integer count = task.getCount();
		String cron = task.getCron();
		String jobClass = task.getJobClass();
		String methodName = task.getMethodName();
		String methodparamData = task.getMethodparamData();
		Integer parallelism = task.getParallelism();
		Integer second = task.getSecond();
		Integer status = task.getStatus();
		
		if(StringUtils.isBlank(jobName)) {
			throw new BaseTaskException("jobName不能为空");
		}
		if((count!=null&&second!=null)&&cron!=null) {
			if(StringUtils.isBlank(cron)) {
				throw new BaseTaskException("(count,second) 和 cron 有一不能为空");
			}
		}
		
		if(StringUtils.isBlank(jobClass)) {
			throw new BaseTaskException("jobClass不能为空");
		}
		if(StringUtils.isBlank(methodName)) {
			throw new BaseTaskException("methodName不能为空");
		}
		if(StringUtils.isBlank(methodparamData)) {
			throw new BaseTaskException("methodparamData不能为空");
		}
		if(parallelism==null) {
			throw new BaseTaskException("parallelism不能为空");
		}

		if(status==null) {
			throw new BaseTaskException("status不能为空");
		}
		
		
	}
	

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		synchronized (isStart) {
			if(!isStart) {
				_logger.info("===================================定时系统开始===========================================");
				SchedulerUtil.handleSimpleTrigger("BaseQuartzJob", "BaseQuartzJob", "BaseQuartzJob", "BaseQuartzJob", BaseJob.class, null,null, 1, -1);
				isStart = true;
				}
		}
		
		return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
	}

	public BaseJob() {
		super();
	}
	
	

}
