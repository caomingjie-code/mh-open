package com.mh.base.quartz;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.simpl.SimpleJobFactory;
import org.quartz.utils.Key;

import com.mh.base.quartz.exception.BaseTaskException;
import com.mh.base.quartz.task.BaseSimpleTask;
import com.mh.base.quartz.task.BaseStateTask;
import com.mh.base.quartz.task.data.TaskData;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.tree.*;
import org.slf4j.LoggerFactory;

/**
 * @author 曹明杰
 * @date 2019-05-15
 * @version 1.0
 * @copyright copyright (c) 2019
 */
@SuppressWarnings("restriction")
public class SchedulerUtil {
    private static Logger _logger = LoggerFactory.getLogger(SchedulerUtil.class);// log4j记录日志
    private final static SchedulerFactory schedulerfactory = new StdSchedulerFactory();
    private final static Map<String,Class> taskClazz = new HashMap<String,Class>();
    private final static String anno = "com.mh.base.quartz.annotation.BaseQuartz";
    private static Scheduler scheduler =  null;
    static {
    	try {
    		scheduler = schedulerfactory.getScheduler();//这是单例的;
    		String path = SchedulerUtil.class.getResource("/").getPath();//获取classpath路径
        	List<File> list = getAllClassFile(new File( path ));//此方法为获取classpath路径下所有的class文件（所有包）
        	for(File f: list) {//便利每一个class文件
    			//getClassName(f);
    			 ClassReader reader = new ClassReader(new FileInputStream(f));
    			 ClassNode cn = new ClassNode();//创建ClassNode,读取的信息会封装到这个类里面
    	         reader.accept(cn, 0);//开始读取
    	         List<AnnotationNode> annotations = cn.visibleAnnotations;//获取声明的所有注解
    	         if(annotations!=null) {//便利注解
                	 for(AnnotationNode an: annotations) {
                		 //获取注解的描述信息
                		 String anno = an.desc.replaceAll("/", ".");
                		 String annoName = anno.substring(1, anno.length()-1);
                		 
                		 if("com.mh.base.quartz.annotation.BaseQuartz".equals(annoName)) {
                			 String className = cn.name.replaceAll("/", ".");
                			//获取注解的属性名对应的值，（values是一个集合，它将注解的属性和属性值都放在了values中，通常奇数为值偶数为属性名）
        		        	 String valu = an.values.get(1).toString();
							 _logger.info(className);
        		        	 _logger.info(valu);
        		        	 taskClazz.put(valu, Class.forName(className));//根据匹配的注解，将其封装给具体的业务使用
                		 }
    		         }
                 }
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    }
    
    public static List<String> getAllStartingTaskNames() {
    	List<String> list = null;
		try {
			list = scheduler.getJobGroupNames();
			list = list==null?new ArrayList<String>():list;
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
    	return list;
    }
    
    public static List<File> getAllClassFile(File file ) {
		List<File> list =  new ArrayList<File>();
		if(file==null) {
			return list;
		}
		File[] listFiles = file.listFiles();
		if(listFiles!=null&&listFiles.length>0) {
			for(File f : listFiles) {
			    if(!f.isDirectory()&&f.getName().endsWith("class")) {
			    	list.add(f);
				}
			    if(f.isDirectory()) {
			    	List<File> allClassFile = getAllClassFile(f);
			    	list.addAll(allClassFile);
			    }
			}
		}
		return list;
	}
    
    /**
     * [简单任务调度:每次执行间隔为多少秒，执行多少次] <br>
     * @author Edon-Du <br>
     * @date 2018-6-25 <br>
     * @param jobName 任务名字
     * @param jobGroupName 任务组名字
     * @param triggerName 触发器名字
     * @param triggerGroupName 触发器组名字
     * @param jobClass 任务类
     * @param intevalTime 时间间隔
     * @param count 执行几次 负数为永远<br>
     */
    public static void handleSimpleTrigger(
    		String jobName, String jobGroupName,
            String triggerName, String triggerGroupName, 
            Class jobClass,
            BaseSimpleTask bmt,
            BaseStateTask bst,
            int time, int count) {
        // 通过schedulerFactory获取一个调度器
        
        
        try {
           
            // 创建jobDetail实例，绑定Job实现类
            // 指明job的名称，所在组的名称，以及绑定job类
            JobDetail job = getJob(jobName, jobGroupName, jobClass,bmt,bst);
            
            // 定义调度触发规则
             //使用simpleTrigger规则
             Trigger  trigger=TriggerBuilder.newTrigger().withIdentity(triggerName, triggerGroupName)
             .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(time).withRepeatCount(count))
             .startNow().build();
            // 把作业和触发器注册到任务调度中
            scheduler.scheduleJob(job, trigger);
            // 启动调度
            scheduler.start();
            
            
            
        } catch (Exception e) {
            _logger.warn("执行"+jobName+"组"+jobName+"任务出现异常E:["+ e.getMessage() + "]");
            e.printStackTrace();
        }
    }
    
    
	private static <V> JobDetail getJob(
			String jobName,
			String jobGroupName, 
			Class jobClass,
			BaseSimpleTask bmt,
            BaseStateTask bst
            ) throws Exception {
		if(bmt!=null) {
			jobClass = BaseSimpleTask.class;
		}
		if(bst!=null) {
			jobClass = BaseStateTask.class;
		}
		
		JobBuilder withIdentity = JobBuilder.newJob(jobClass)
		        .withIdentity(jobName, jobGroupName);
		JobDetailImpl job = (JobDetailImpl) withIdentity.build();
		if(bmt!=null&&bst!=null) {
			throw new Exception("BaseSimpleTask 和  BaseStateTask 对象必须存在其中一个为null,不允许在调用getJob 方法时，这两个对象都同时存在");
		}
		buildSaveTask(bmt, bst, job);
		return job;
	}

	private static void buildSaveTask(BaseSimpleTask bmt, BaseStateTask bst, JobDetailImpl job) {
		if(bmt!=null) {
			HashMap<String,Object> map = new HashMap<String, Object>();
			Class className = bmt.getClassName();
			Method method = bmt.getMethod();
			String param = bmt.getParam();
			map.put("className", className);
			map.put("method", method);
			map.put("param", param);
			job.setJobDataMap(new JobDataMap(map));
		}
		if(bst!=null) {
			HashMap<String,Object> map = new HashMap<String, Object>();
			Class className = bst.getClassName();
			Method method = bst.getMethod();
			String param = bst.getParam();
			map.put("className", className);
			map.put("method", method);
			map.put("param", param);
			job.setJobDataMap(new JobDataMap(map));
		}
	}
	
	
    public static void startSimpleTask(final TaskData td) {
    	try {
    		BaseSimpleTask baseSimpleTask = new BaseSimpleTask();
    		Class clazz = taskClazz.get(td.getJobClass().toString());
    		if(clazz==null) {
    			throw new BaseTaskException("jobClass "+td.getJobClass().toString()+" not found");
    		}
    		Method method = clazz.getDeclaredMethod(td.getMethodName(), String.class);
    		if(method==null) {
    			throw new BaseTaskException("带有一个String类型的参数 的方法"+td.getMethodName()+"在"+clazz.getName()+"中未找到");
    		}
    		baseSimpleTask.setClassName(clazz);
    		baseSimpleTask.setMethod(method);
    		baseSimpleTask.setParam(td.getMethodparamData());
    		if(td.getCount()!=null&&StringUtils.isNotBlank(td.getCount().toString())&&td.getSecond()!=null&&td.getCount()>0) {
        		handleSimpleTrigger(td.getTaskId().toString(), td.getTaskId().toString(), td.getTaskId().toString(), td.getTaskId().toString(), null, baseSimpleTask, null , td.getSecond(), td.getCount());
        	}else if(StringUtils.isNotBlank(td.getCron()+"")) {
    			hadleCronTrigger(td.getTaskId().toString(), td.getTaskId().toString(), td.getTaskId().toString(), td.getTaskId().toString(), null, baseSimpleTask, null, td.getCron());
    		}else if(td.getCount()!=null&&StringUtils.isNotBlank(td.getCount().toString())&&td.getSecond()!=null&&td.getCount()<0) { //永远执行
        		handleSimpleTrigger(td.getTaskId().toString(), td.getTaskId().toString(), td.getTaskId().toString(), td.getTaskId().toString(), null, baseSimpleTask, null , td.getSecond(), td.getCount());
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	
    }
    public static void startStateTask(final TaskData td) {
    	try {
    		BaseStateTask baseStateTask = new BaseStateTask();
    		Class clazz = taskClazz.get(td.getJobClass().toString());
    		if(clazz==null) {
    			throw new BaseTaskException("jobClass "+td.getJobClass().toString()+" not found");
    		}
    		Method method = clazz.getDeclaredMethod(td.getMethodName(), String.class);
    		if(method==null) {
    			throw new BaseTaskException("带有一个String类型的参数 的方法"+td.getMethodName()+"在"+clazz.getName()+"中未找到");
    		}
    		baseStateTask.setClassName(clazz);
    		baseStateTask.setMethod(method);
    		baseStateTask.setParam(td.getMethodparamData());
    		if(td.getCount()!=null&&StringUtils.isNotBlank(td.getCount().toString())&&td.getSecond()!=null) {
        		handleSimpleTrigger(td.getTaskId().toString(),td.getTaskId().toString(), td.getTaskId().toString(), td.getTaskId().toString(), null, null, baseStateTask , td.getSecond(), td.getCount());
        	}else if(StringUtils.isNotBlank(td.getCron())) {
    			hadleCronTrigger(td.getTaskId().toString(), td.getTaskId().toString(), td.getTaskId().toString(), td.getTaskId().toString(), null, null, baseStateTask, td.getCron());
    		}else if(td.getCount()!=null&&StringUtils.isNotBlank(td.getCount().toString())&&td.getSecond()!=null&&td.getCount()<0) { //永远执行
        		handleSimpleTrigger(td.getTaskId().toString(),td.getTaskId().toString(), td.getTaskId().toString(), td.getTaskId().toString(), null, null, baseStateTask , td.getSecond(), td.getCount());
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    }
    
    /**
     * [复杂任务调度：每天几点几分几秒定时执行任务] <br>
     * @author Edon-Du <br>
     * @date 2018-6-25 <br>
     * @param jobName 任务名字
     * @param jobGroupName 任务组名字
     * @param triggerName 触发器名字
     * @param triggerGroupName 触发器组名字
     * @param jobClass 任务类
     * @param cron 触发规则<br>
     */
    public static void hadleCronTrigger(
    		String jobName, String jobGroupName,
            String triggerName, String triggerGroupName, 
            Class jobClass,
            BaseSimpleTask bmt,
            BaseStateTask bst,
            String cron
            ) {
        // 通过schedulerFactory获取一个调度器
        
        Scheduler scheduler = null;
        try {
            // 通过schedulerFactory获取一个调度器
            scheduler = schedulerfactory.getScheduler();
            // 创建jobDetail实例，绑定Job实现类
            // 指明job的名称，所在组的名称，以及绑定job类
            JobDetail job = getJob(jobName, jobGroupName, jobClass,bmt,bst);
            // 定义调度触发规则
            //使用cornTrigger规则  每天18点30分  
            Trigger trigger=TriggerBuilder.newTrigger().withIdentity(triggerName, triggerGroupName)  
            .withSchedule(CronScheduleBuilder.cronSchedule(cron))  
            .startNow().build();    
            // 把作业和触发器注册到任务调度中
            scheduler.scheduleJob(job, trigger);
            if(!scheduler.isStarted()) {
            	// 启动调度
                scheduler.start();
            }
        } catch (Exception e) {
            _logger.warn("执行"+jobName+"组"+jobName+"任务出现异常E:["+ e.getMessage() + "]");
        }
    }
    
    
    public static void addJob(String jobName, String jobGroupName,
            String triggerName, String triggerGroupName, Class jobClass,
            BaseSimpleTask bmt,
            BaseStateTask bst,
            String cron) {
    	// 通过schedulerFactory获取一个调度器
        Scheduler scheduler = null;
        try {
            // 通过schedulerFactory获取一个调度器
            scheduler = schedulerfactory.getScheduler();//这是单例的
            // 创建jobDetail实例，绑定Job实现类
            // 指明job的名称，所在组的名称，以及绑定job类
            JobDetail job = getJob(jobName, jobGroupName, jobClass,bmt,bst);
            // 定义调度触发规则
            //使用cornTrigger规则  每天18点30分  
            Trigger trigger=TriggerBuilder.newTrigger().withIdentity(triggerName, triggerGroupName)  
            .withSchedule(CronScheduleBuilder.cronSchedule(cron))  
            .startNow().build();    
            // 把作业和触发器注册到任务调度中
            scheduler.scheduleJob(job, trigger);
        } catch (Exception e) {
            _logger.warn("执行"+jobName+"组"+jobName+"任务出现异常E:["+ e.getMessage() + "]");
        }
    }
    
    
    
    public static void addJob(
    		String jobName, String jobGroupName,
            String triggerName, String triggerGroupName, 
            Class jobClass,
            BaseSimpleTask bmt,
            BaseStateTask bst,
            int time, int count) {
        // 通过schedulerFactory获取一个调度器
        Scheduler scheduler = null;
        try {
            // 通过schedulerFactory获取一个调度器
            scheduler = schedulerfactory.getScheduler();//这是单例的
            // 创建jobDetail实例，绑定Job实现类
            // 指明job的名称，所在组的名称，以及绑定job类
            JobDetail job = getJob(jobName, jobGroupName, jobClass,bmt,bst);
            // 定义调度触发规则
             //使用simpleTrigger规则
             Trigger  trigger=TriggerBuilder.newTrigger().withIdentity(triggerName, triggerGroupName)
             .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(time).withRepeatCount(count))
             .startNow().build();
            // 把作业和触发器注册到任务调度中
            scheduler.scheduleJob(job, trigger);
            
        } catch (Exception e) {
            _logger.warn("执行"+jobName+"组"+jobName+"任务出现异常E:["+ e.getMessage() + "]");
        }
    }
    
    
    public static boolean isStarting(String taskId) {
    	Scheduler scheduler = null;
    	try {
    		scheduler = schedulerfactory.getScheduler();
    		JobDetail jobDetail = scheduler.getJobDetail(new JobKey(taskId,taskId));
    		List<String> jobGroupNames = scheduler.getJobGroupNames();
    		for(String taskId_ : jobGroupNames) {
    			if(taskId.equalsIgnoreCase(taskId_)) {
    				return true;
    			}
    		}
    		if(jobDetail==null) {
    			return  false;
    		}
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return true;
    }
    
    public static void stopTask(String taskName) {
    	Scheduler scheduler = null;
    	try {
    		scheduler = schedulerfactory.getScheduler();
    		scheduler.deleteJob(new JobKey(taskName,taskName) );
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static List<String> getAllStartingTaskJobNames() {
    	Scheduler scheduler = null;
    	try {
    		scheduler = schedulerfactory.getScheduler();
    		List<String> jobGroupNames = scheduler.getJobGroupNames();//在baseQuartz设置时JobName 和 JobGroupName是一样的
    	    return jobGroupNames;
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
    
    
    
    
    
    /**
	     *  字段 允许值 允许的特殊字符 
		秒 0-59 , - * / 
		分 0-59 , - * / 
		小时 0-23 , - * / 
		日期 1-31 , - * ? / L W C 
		月份 1-12 或者 JAN-DEC , - * / 
		星期 1-7 或者 SUN-SAT , - * ? / L C # 
		年（可选） 留空, 1970-2099 , - * / 
		表达式 意义 
		“0 0 12 * * ?” 每天中午12点触发 
		“0 15 10 ? * *” 每天上午10:15触发 
		“0 15 10 * * ?” 每天上午10:15触发 
		“0 15 10 * * ? *” 每天上午10:15触发 
		“0 15 10 * * ? 2005” 2005年的每天上午10:15触发 
		“0 * 14 * * ?” 在每天下午2点到下午2:59期间的每1分钟触发 
		“0 0/5 14 * * ?” 在每天下午2点到下午2:55期间的每5分钟触发 
		“0 0/5 14,18 * * ?” 在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发 
		“0 0-5 14 * * ?” 在每天下午2点到下午2:05期间的每1分钟触发 
		“0 10,44 14 ? 3 WED” 每年三月的星期三的下午2:10和2:44触发 
		“0 15 10 ? * MON-FRI” 周一至周五的上午10:15触发 
		“0 15 10 15 * ?” 每月15日上午10:15触发 
		“0 15 10 L * ?” 每月最后一日的上午10:15触发 
		“0 15 10 ? * 6L” 每月的最后一个星期五上午10:15触发 
		“0 15 10 ? * 6L 2002-2005” 2002年至2005年的每月的最后一个星期五上午10:15触发 
		“0 15 10 ? * 6#3” 每月的第三个星期五上午10:15触发 
		特殊字符 意义 
		* 表示所有值； 
		? 表示未说明的值，即不关心它为何值； 
		- 表示一个指定的范围； 
		, 表示附加一个可能值； 
		/ 符号前表示开始时间，符号后表示每次递增的值； 
		L(“last”) (“last”) “L” 用在day-of-month字段意思是 “这个月最后一天”；用在 day-of-week字段, 它简单意思是 “7” or “SAT”。 如果在day-of-week字段里和数字联合使用，它的意思就是 “这个月的最后一个星期几” – 例如： “6L” means “这个月的最后一个星期五”. 当我们用“L”时，不指明一个列表值或者范围是很重要的，不然的话，我们会得到一些意想不到的结果。 
		W(“weekday”) 只能用在day-of-month字段。用来描叙最接近指定天的工作日（周一到周五）。例如：在day-of-month字段用“15W”指“最接近这个月第15天的工作日”，即如果这个月第15天是周六，那么触发器将会在这个月第14天即周五触发；如果这个月第15天是周日，那么触发器将会在这个月第16天即周一触发；如果这个月第15天是周二，那么就在触发器这天触发。注意一点：这个用法只会在当前月计算值，不会越过当前月。“W”字符仅能在day-of-month指明一天，不能是一个范围或列表。也可以用“LW”来指定这个月的最后一个工作日。 
		只能用在day-of-week字段。用来指定这个月的第几个周几。例：在day-of-week字段用”6#3”指这个月第3个周五（6指周五，3指第3个）。如果指定的日期不存在，触发器就不会触发。 
		C 指和calendar联系后计算过的值。例：在day-of-month 字段用“5C”指在这个月第5天或之后包括calendar的第一天；在day-of-week字段用“1C”指在这周日或之后包括calendar的第一天
     */
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}