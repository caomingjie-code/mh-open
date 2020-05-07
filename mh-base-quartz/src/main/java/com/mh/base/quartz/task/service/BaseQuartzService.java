package com.mh.base.quartz.task.service;

import java.io.Serializable;

import com.mh.base.dao.BaseBatis;
import com.mh.base.dao.BaseJPA;
import com.mh.base.quartz.task.data.TaskData;

public interface BaseQuartzService extends BaseJPA<TaskData, Serializable>,BaseBatis<TaskData, Serializable>{

}
