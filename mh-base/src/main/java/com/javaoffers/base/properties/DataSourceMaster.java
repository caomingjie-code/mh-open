package com.javaoffers.base.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 从数据配置
 */
@Component
@ConfigurationProperties(prefix = "spring")
public class DataSourceMaster {
	
	DataSourceProperteis datasource_master = new DataSourceProperteis();

    public DataSourceProperteis getDatasource_master() {
		return datasource_master;
	}

	public void setDatasource_master(DataSourceProperteis datasource_master) {
		this.datasource_master = datasource_master;
	}


	
}
