package com.javaoffers.base.quartz.task.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name="bs_task_data")
public class TaskData implements Serializable{

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Integer taskId;
	
	@Column(name = "jobName",columnDefinition="varchar(30) COMMENT '任务名名(不为空)'")
	String jobName; //任务名
	
	@Column(name = "jobClass",columnDefinition="varchar(50) COMMENT '任务类名，对应注解BaseQuartz中的name属性值(不为空)'")
    String jobClass; //任务类名，对应BeanName
	
	@Column(name = "methodName",columnDefinition="varchar(100) COMMENT '被定时执行的方法（不为空）'")
	String methodName; //被定时执行的方法
	
	@Column(name = "methodparamData",columnDefinition="varchar(100) COMMENT '方法参数（定时任务的方法一定要带有String类型的参数，切只有一个参数）（不为空）'")
	String methodparamData;//方法参数
	
	@Column(name = "second",columnDefinition="int(10) COMMENT '多少秒执行一次（second字段和cron字段必须有其一不为空，second优先级高于cron）'")
    Integer second; //多少秒执行一次
	
	@Column(name = "count",columnDefinition="int(100) COMMENT '总共执行多少次,负数为：永远执行（不为空），0和1都代表1次'")
    Integer count; // 总共执行多少次，
	
	@Column(name = "cron",columnDefinition="varchar(100) COMMENT '按照日期执行执行（配置quartz日期表达式，second字段和cron字段必须有其一不为空）'")
	String cron;   //按照日期执行执行
	
	@Column(name = "status",columnDefinition="int(1) COMMENT '任务状态 0 停止，1 开始(不为空)，2自然死亡（用于count次数），'")
	Integer status; //任务状态 0 停止，1 开始
	
	@Column(name = "parallelism",columnDefinition="int(1) COMMENT '任务模式，0 此任务未完成，不允许启动新任务，1 此任务未完成允许启动新的任务(不为空)'")
	Integer parallelism;//任务模式，0 此任务未完成，不允许启动新任务，1 此任务未完成允许启动新的任务
	
	public Integer getParallelism() {
		return parallelism;
	}
	public void setParallelism(Integer parallelism) {
		this.parallelism = parallelism;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getJobClass() {
		return jobClass;
	}
	public void setJobClass(String jobClass) {
		this.jobClass = jobClass;
	}
	public Integer getSecond() {
		return second;
	}
	public void setSecond(Integer second) {
		this.second = second;
	}
	public Integer getCount() {
		if(count>0) {
			return  count-1;
		}
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public String getCron() {
		return cron;
	}
	public void setCron(String cron) {
		this.cron = cron;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getMethodparamData() {
		return methodparamData;
	}
	public void setMethodparamData(String methodparamData) {
		this.methodparamData = methodparamData;
	}
	public Integer getTaskId() {
		return taskId;
	}
	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}
	
	
	
	
}
