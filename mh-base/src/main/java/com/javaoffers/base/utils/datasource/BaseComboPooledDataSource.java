package com.javaoffers.base.utils.datasource;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import javax.naming.Referenceable;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;

import com.mchange.v2.c3p0.AbstractComboPooledDataSource;
import com.javaoffers.base.exception.BaseDataSourceException;

final public class BaseComboPooledDataSource extends AbstractComboPooledDataSource implements Serializable, Referenceable{
	
	private String slavename;
	//数据源的路由
	private static ThreadLocalNaming<String> dataSourceRoute = new ThreadLocalNaming<String>("DataSourceRouteThreadLocal");
	//存放子数据源
	private static  ConcurrentHashMap<String, DataSource> dataSourceSlaves = new ConcurrentHashMap<String, DataSource>();
	
	/**
	 * 设置route
	 * @param dataSourceSlaveName
	 * @throws SQLException
	 */
	public static void setDataSourceRoute(String dataSourceSlaveName) throws SQLException {
		if(!StringUtils.isNoneBlank(dataSourceSlaveName)) {
			throw BaseDataSourceException.getException("DataSourceSlave Is Null");
		}

		dataSourceRoute.set(dataSourceSlaveName);
	}
	
	public String getSlavename() {
		return slavename;
	}

	public void setSlavename(String slavename) {
		this.slavename = slavename;
	}

	/**
	 * 添加slave
	 * @param dataSourceSlaveName
	 * @param dataSourceSlave
	 */
	protected void addDataSourceSlaves(String dataSourceSlaveName,DataSource dataSourceSlave) {
		dataSourceSlaves.put(dataSourceSlaveName, dataSourceSlave);
	}
	public BaseComboPooledDataSource()
	    { super(); }

	    public BaseComboPooledDataSource( boolean autoregister )
	    { super( autoregister ); }

	    public BaseComboPooledDataSource(String configName)
	    { super( configName );  }


	    // serialization stuff -- set up bound/constrained property event handlers on deserialization
	    private static final long serialVersionUID = 1;
	    private static final short VERSION = 0x0002;

	    private void writeObject( ObjectOutputStream oos ) throws IOException
	    {
	        oos.writeShort( VERSION );
	    }

	    private void readObject( ObjectInputStream ois ) throws IOException, ClassNotFoundException
	    {
	        short version = ois.readShort();
	        switch (version)
	        {
	        case VERSION:
		    //ok
	            break;
	        default:
	            throw new IOException("Unsupported Serialized Version: " + version);
	        }
	    }
	    
	    public Connection getConnection() throws SQLException{
	    	//判断是否需要route
	    	String slaveDataSourceName = dataSourceRoute.get();
	    	if(StringUtils.isNoneBlank(slaveDataSourceName)) {
	    		try {
		    		Connection connectionSlaveonnection = ((BaseComboPooledDataSource) dataSourceSlaves.get(slaveDataSourceName)).getSuperConnection();
				    return connectionSlaveonnection;
	    		} catch (Exception e) {
					throw BaseDataSourceException.getException("config is error of data source slave ");
				  
	    		}
	    	}
	    	//获取MasterConnection
	    	Connection masterConnection = super.getConnection();
			return masterConnection;
	    
	    }
	    
	    /**
	     * 直接调取夫类方法获取连接,只给Slave调用
	     * @return
	     * @throws SQLException 
	     */
	    private Connection getSuperConnection() throws SQLException {
	    	Connection connection = super.getConnection();
	    	return connection;
	    }

	/**
	 * 清除陆游信息
 	 */
	public static void clean(){
		dataSourceRoute.set(null);
	}

}
