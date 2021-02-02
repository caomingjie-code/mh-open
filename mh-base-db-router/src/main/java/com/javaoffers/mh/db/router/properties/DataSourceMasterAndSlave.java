package com.javaoffers.mh.db.router.properties;

import com.javaoffers.mh.db.router.exception.BaseDataSourceException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
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
public class DataSourceMasterAndSlave implements InitializingBean {
	
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

	@Override
	public void afterPropertiesSet() throws Exception {
		//校验数据，以及整理数据
		String readDataSources = datasource_master.getReadDataSources();
		boolean readWriteSeparation = datasource_master.getReadWriteSeparation();
		if(readWriteSeparation&& StringUtils.isNotBlank(readDataSources)){
			throw  BaseDataSourceException.getException(" readWriteSeparation and readDataSources can not occur simultaneously");
		}

		if(readWriteSeparation && datasource_slaves!=null&&datasource_slaves.size()>0){
			StringBuilder sb = new StringBuilder();
			for(DataSourceProperteis dp : datasource_slaves){
				String slavename = dp.getSlavename();
				sb.append(slavename+",");
			}
			String readDS = sb.toString().subSequence(0,sb.length()-1)+"";
			datasource_master.setReadDataSources(readDS);
		}


	}
}
