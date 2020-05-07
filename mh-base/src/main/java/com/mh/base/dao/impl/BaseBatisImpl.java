package com.mh.base.dao.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import com.mh.base.dao.BaseBatis;
import com.mh.base.utils.model.ModelUtils;
import com.mh.base.utils.param.ParamUtils;

public class BaseBatisImpl<T, ID> implements BaseBatis<T, ID> {
	

	@Resource(name = "jdbcTemplate")
	private JdbcTemplate jdbcTemplate;
	private T t = null;
	@Resource
	private SqlSessionTemplate sqlSessionTemplate;
    
	
	private Class clazzT;
	private Class clazzId;
	
	public void saveData(String sql) {
		this.jdbcTemplate.execute(sql);
	}

	public void deleteData(String sql) {
		this.jdbcTemplate.execute(sql);
	}

	public void updateData(String sql) {
		this.jdbcTemplate.execute(sql);
	}

	
	/********************************** JPA ************************************/

	public List<Map<String, Object>> queryData(String sql) {
		List<Map<String, Object>> queryForList = this.jdbcTemplate.queryForList(sql);
		return queryForList;
	}

	/**
	 * 支持Pojo和Model
	 */
	public <E> List<E> queryDataForT(String sql, Class<E> clazz) {
		List<Map<String, Object>> list_map = this.jdbcTemplate.queryForList(sql);
		ArrayList<E> list = ModelUtils.converterMap2T(clazz, list_map);
		return list;
	}



	public Integer batchUpdate(String sql, BatchPreparedStatementSetter pss) {
		int[] is = this.jdbcTemplate.batchUpdate("BASE." + sql, pss);
		return Integer.valueOf(is.length);
	}

	/****************************** BATIS ***********************************/
	
	

	
	
	/**
	 * 支持Pojo和Model
	 */
	@Override
	public <E> List<E> queryDataForT2(String sqlId, Class<E> clazz) {
		List<Map<String, Object>> list = selectSql(sqlId);
		ArrayList<E> list2 = ModelUtils.converterMap2T(clazz, list);
		return list2;
	}
	/**
	 * 支持Pojo和Model
	 */
	@Override
	public <E> List<E> queryDataForT3(String sqlId, Map<String, Object> map, Class<E> clazz) {
		List<Map<String, Object>> list = selectSql(sqlId, map);
		ArrayList<E> list2 = ModelUtils.converterMap2T(clazz, list);
		return list2;
	}
	
	@Override
	public List<Map<String, Object>> executorSQL(String sqlId) {
		HashMap<String, Object> pm = ParamUtils.buildParamsMap(null);
		return sqlSessionTemplate.selectList("BASE." + sqlId, pm);
	}

	@Override
	public List<Map<String, Object>> executorSQL(String sqlId, Map<String, Object> paramMap) {
		HashMap<String, Object> pm = ParamUtils.buildParamsMap(paramMap);
		return sqlSessionTemplate.selectList("BASE." + sqlId, pm);
	}

	@Override
	public List<Map<String, Object>> executorSQLByPojo(String sqlId, Object pojo) {
		HashMap<String, Object> pm = ParamUtils.buildParamsMap(pojo);
		return sqlSessionTemplate.selectList("BASE." + sqlId, pm);
	}


	@Override
	public void insertSql(String sqlId) {
		executorSQL(sqlId);

	}

	@Override
	public void deleteSql(String sqlId) {
		executorSQL(sqlId);
	}

	@Override
	public void updateSql(String sqlId) {
		executorSQL(sqlId);
	}

	@Override
	public List<Map<String, Object>> selectSql(String sqlId) {
		return executorSQL(sqlId);
	}

	@Override
	public void insertSql(String sqlId, Map<String, Object> paramMap) {
		executorSQL(sqlId, paramMap);
	}

	@Override
	public void deleteSql(String sqlId, Map<String, Object> paramMap) {
		executorSQL(sqlId, paramMap);
	}

	@Override
	public void updateSql(String sqlId, Map<String, Object> paramMap) {
		executorSQL(sqlId, paramMap);
	}

	@Override
	public List<Map<String, Object>> selectSql(String sqlId, Map<String, Object> paramMap) {
		return executorSQL(sqlId, paramMap);
	}

	@Override
	public void insertSqlByPojo(String sqlId, Object pojo) {
		executorSQLByPojo(sqlId, pojo);
	}

	@Override
	public void deleteSqlByPojo(String sqlId, Object pojo) {
		executorSQLByPojo(sqlId, pojo);
	}

	@Override
	public void updateSqlByPojo(String sqlId, Object pojo) {
		executorSQLByPojo(sqlId, pojo);
	}

	@Override
	public List<Map<String, Object>> selectSqlByPojo(String sqlId, Object pojo) {
		return executorSQLByPojo(sqlId, pojo);
	}
    /**
     * 分页
     */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public FPage selectSqlForFPage(String sqlId, FPage fpage) {
		Map<String, Object> paramMap = fpage.getParams();
		int begeinIndex = fpage.getBegeinIndex();// 查询数据的开始索引
		int pageSize = fpage.getPageSize(); //查询的数据量
		paramMap.put("begeinIndex", begeinIndex + "");
		paramMap.put("pageSize", pageSize + "");
		List list = executorSQLByPojo(sqlId, paramMap);
		if(list==null||list.size()==0) { //如果没有数据直接返回
			return fpage;
		}
		@SuppressWarnings("rawtypes")
		Map map = (Map) list.get(0);
		int tatol = map.get("tatol") == null ? 0 : Integer.parseInt(map.get("tatol").toString());
		fpage.initFPage(tatol, list);
		return fpage; 
	}

	@Override
	public <E> List<E> queryDataForTAndSort(String sqlId,Map<String,Object> map,Class<E> clazz,String mainModelFieldName) {
		List<Map<String, Object>> list = selectSql(sqlId, map);
		ArrayList<E> list2 = ModelUtils.converterMap2TAndSort(clazz, list, mainModelFieldName);
		return list2;
	}

	

	@Override
	public <E> List<E> queryDataForTAndSort(String sql, Class<E> clazz, String mainModelFieldName) {
		List<Map<String, Object>> list_map = this.jdbcTemplate.queryForList(sql);
		ArrayList<E> list2 = ModelUtils.converterMap2TAndSort(clazz, list_map, mainModelFieldName);
		return list2;
	}

	
	/*************************************************Utils*******************************************************/



}
