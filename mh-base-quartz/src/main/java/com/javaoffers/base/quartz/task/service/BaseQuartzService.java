package com.javaoffers.base.quartz.task.service;

import java.io.Serializable;

import com.javaoffers.base.dao.BaseBatis;
import com.javaoffers.base.dao.BaseJPA;
import com.javaoffers.base.quartz.task.data.TaskData;

public interface BaseQuartzService extends BaseJPA<TaskData, Serializable>,BaseBatis<TaskData, Serializable>{

}
