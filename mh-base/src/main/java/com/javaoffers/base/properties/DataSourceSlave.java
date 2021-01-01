package com.javaoffers.base.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring")
public class DataSourceSlave {
	
	DataSourceProperteis datasource_slaves = new DataSourceProperteis();

	public DataSourceProperteis getDatasource_slaves() {
		return datasource_slaves;
	}

	public void setDatasource_slaves(DataSourceProperteis datasource_slaves) {
		this.datasource_slaves = datasource_slaves;
	}
	
}
