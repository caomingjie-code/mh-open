package com.javaoffers.mh.db.router.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * 从数据配置
 * create by cmj
 */
@Component
@ConfigurationProperties(prefix = "spring",ignoreInvalidFields = true )
public class DataSourceMasterAndSlave {
	
	DataSourceProperteis datasource_master = new DataSourceProperteis();
	List<DataSourceProperteis> datasource_slaves = new LinkedList<>();

    public DataSourceProperteis getDatasource_master() {
		return datasource_master;
	}

	public void setDatasource_master(DataSourceProperteis datasource_master) {
		this.datasource_master = datasource_master;
	}

	public List<DataSourceProperteis> getDatasource_slaves() {
		return datasource_slaves;
	}

	public void setDatasource_slaves(List<DataSourceProperteis> datasource_slaves) {
		this.datasource_slaves = datasource_slaves;
	}
}
