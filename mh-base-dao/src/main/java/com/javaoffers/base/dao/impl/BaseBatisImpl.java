package com.javaoffers.base.dao.impl;
import java.util.*;
import javax.annotation.Resource;
import com.javaoffers.base.dao.SQL;
import com.javaoffers.base.utils.sql.SQLParse;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import com.javaoffers.base.dao.BaseBatis;
import com.javaoffers.base.common.model.ModelUtils;
import com.javaoffers.base.utils.param.ParamUtils;

public class BaseBatisImpl<T, ID> implements BaseBatis<T, ID> {
	

	@Resource(name = "jdbcTemplate")
	private JdbcTemplate jdbcTemplate;
	private T t = null;
	@Resource
	private SqlSessionTemplate sqlSessionTemplate;
    
	
	private Class clazzT;
	private Class clazzId;
	
	public int saveData(String sql) {
		return saveData(sql,Collections.EMPTY_MAP);
	}

	@Override
	public int saveData(String sql, Map<String, Object> map) {
		SQL sql_ = SQLParse.getSQL(sql, map);
		return this.jdbcTemplate.update(sql_.getSql(),  new ArgumentPreparedStatementSetter(sql_.getArgsParam().get(0)));
	}

	public int deleteData(String sql) {
		return deleteData(sql,Collections.EMPTY_MAP);
	}

	@Override
	public int deleteData(String sql, Map<String, Object> map) {
		SQL sql_ = SQLParse.getSQL(sql, map);
		return this.jdbcTemplate.update(sql_.getSql(),  new ArgumentPreparedStatementSetter(sql_.getArgsParam().get(0)));
	}

	public int updateData(String sql) {
		return updateData(sql, Collections.EMPTY_MAP);
	}

	@Override
	public int updateData(String sql, Map<String, Object> map) {
		SQL sql_ = SQLParse.getSQL(sql, map);
		return this.jdbcTemplate.update(sql_.getSql(),  new ArgumentPreparedStatementSetter(sql_.getArgsParam().get(0)));
	}

	/********************************** JPA ************************************/

	public List<Map<String, Object>> queryData(String sql) {
		List<Map<String, Object>> queryForList = this.jdbcTemplate.queryForList(sql);
		return queryForList;
	}

	@Override
	public List<Map<String, Object>> queryData(String sql, Map<String, Object> map) {
		SQL batchSQL = SQLParse.getSQL(sql, map);
		 //query(sql, args, getColumnMapRowMapper());
		List<Map<String, Object>> result = this.jdbcTemplate.query(batchSQL.getSql(), batchSQL.getArgsParam().get(0),new ColumnMapRowMapper());
		return result;
	}

	/**
	 * 支持Pojo和Model
	 */
	public <E> List<E> queryDataForT(String sql, Class<E> clazz) {
		List<Map<String, Object>> list_map = this.jdbcTemplate.queryForList(sql);
		ArrayList<E> list = ModelUtils.converterMap2T(clazz, list_map);
		return list;
	}

	public Integer batchUpdate(String sql,List<Map<String,Object>> paramMap ) {
		SQL batchSQL = SQLParse.parseSqlParams(sql, paramMap);
		int[] is = this.jdbcTemplate.batchUpdate( batchSQL.getSql(), batchSQL);
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
	public <E> List<E> queryDataForT4(String sql, Map<String, Object> map, Class<E> clazz) {
		List<Map<String, Object>> maps = queryData(sql, map);
		return ModelUtils.converterMap2T(clazz, maps);
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
	public int insertSql(String sqlId) {
		return sqlSessionTemplate.insert("BASE."+sqlId);
	}

	@Override
	public int deleteSql(String sqlId) {
		return sqlSessionTemplate.delete("BASE."+sqlId);
	}

	@Override
	public int updateSql(String sqlId) {
		return sqlSessionTemplate.update("BASE."+sqlId);
	}

	@Override
	public List<Map<String, Object>> selectSql(String sqlId) {
		return executorSQL(sqlId);
	}

	@Override
	public int insertSql(String sqlId, Map<String, Object> paramMap) {
		return sqlSessionTemplate.insert("BASE."+sqlId,paramMap);
	}

	@Override
	public int deleteSql(String sqlId, Map<String, Object> paramMap) {
		return sqlSessionTemplate.delete("BASE."+sqlId, paramMap);
	}

	@Override
	public int updateSql(String sqlId, Map<String, Object> paramMap) {
		return sqlSessionTemplate.update("BASE."+sqlId, paramMap);
	}

	@Override
	public List<Map<String, Object>> selectSql(String sqlId, Map<String, Object> paramMap) {
		return executorSQL(sqlId, paramMap);
	}

	@Override
	public int insertSqlByPojo(String sqlId, Object pojo) {
		return sqlSessionTemplate.insert("BASE."+sqlId, pojo);
	}

	@Override
	public int deleteSqlByPojo(String sqlId, Object pojo) {
		return sqlSessionTemplate.delete("BASE."+sqlId, pojo);
	}

	@Override
	public int updateSqlByPojo(String sqlId, Object pojo) {
		return sqlSessionTemplate.update("BASE."+sqlId, pojo);
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
