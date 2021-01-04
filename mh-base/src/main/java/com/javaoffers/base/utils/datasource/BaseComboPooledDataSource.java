package com.javaoffers.base.utils.datasource;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;
import javax.naming.Referenceable;
import javax.sql.DataSource;

import com.javaoffers.base.aop.datasource.RouterConnection;
import org.apache.commons.lang3.StringUtils;

import com.mchange.v2.c3p0.AbstractComboPooledDataSource;
import com.javaoffers.base.exception.BaseDataSourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据源Master , 该master包含slave
 */
final public class BaseComboPooledDataSource extends AbstractComboPooledDataSource implements Serializable, Referenceable{
	public static final String DEFAULT_ROUTER = "MASTER_ROUTER";
	//路由的名称
	private String routerName;
	//数据源的路由
	private static ThreadLocalNaming<ConcurrentLinkedDeque<String>> dataSourceRoute = new ThreadLocalNaming<ConcurrentLinkedDeque<String>>("DataSourceRouteThreadLocal");
	//将要clean，具体在close链接时使用
	private static ThreadLocalNaming<String> meanClean = new ThreadLocalNaming<>("DataSourceRouteThreadLocal"); //如果该值存在则说明此链接属于一路由链接并且数据源管理是JpaTransactionManager
	//存放子数据源
	private static  ConcurrentHashMap<String, DataSource> dataSourceSlaves = new ConcurrentHashMap<String, DataSource>();

	Logger logger = LoggerFactory.getLogger(BaseComboPooledDataSource.class);

	private static AtomicLong ai = new AtomicLong(0);
	/**
	 * 设置route
	 * @param dataSourceSlaveName
	 * @throws SQLException
	 */
	public static void pushStackRouter(String dataSourceSlaveName) throws SQLException {
		if(!StringUtils.isNoneBlank(dataSourceSlaveName)) {
			throw BaseDataSourceException.getException("DataSourceSlave Is Null");
		}
		ConcurrentLinkedDeque stack = dataSourceRoute.get();
		if(stack==null){
			stack = new ConcurrentLinkedDeque();
			dataSourceRoute.set(stack);
		}
		stack.push(dataSourceSlaveName); //放入stack 顶部

	}

	public static String getRouterSourceName() {
		ConcurrentLinkedDeque<String> stackRouter = dataSourceRoute.get();
		if(stackRouter!=null&&stackRouter.size()>0){
			return stackRouter.peekFirst();
		}
		return null;
	}

	/**
	 * 设置即将路由的数据源名称
	 * @param routerSourceName
	 */
	public static void meanClean(String routerSourceName) {
		meanClean.set(routerSourceName);
	}

	/**
	 * 判断是否是路由链接并且数据源管理为JpaTransactionManager
	 * @return
	 */
	public static boolean isRouterConnection() {
		return meanClean.get()!=null;
	}

	/**
	 * 获取当前即将清除的链接名称，并且数据源管理为JpaTransactionManager
	 * @return
	 */
	public static String getMeanClean() {
		return meanClean.get();
	}

	/**
	 * 解析并添加slave数据源
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
			Connection concurrentConnection = null;
			ConcurrentLinkedDeque<String> stackRouter = dataSourceRoute.get();
			String routerName = DEFAULT_ROUTER;
	    	if(stackRouter!=null&&stackRouter.size()>0) {
	    		try {
	    			routerName = stackRouter.peekFirst();
					concurrentConnection = ((BaseComboPooledDataSource) dataSourceSlaves.get(routerName)).getSuperConnection();
	    		} catch (Exception e) {
					throw BaseDataSourceException.getException("config is error of data source slave ["+e.getMessage()+"]");
				  
	    		}
	    	}else{
				//获取MasterConnection
				concurrentConnection = super.getConnection();
			}
			//concurrentConnection.setAutoCommit(false);
			RouterConnection routerConnection = new RouterConnection(this);
	    	routerConnection.putConcurrentConnection(routerName,concurrentConnection);
			logger.info("opened jdbc connection counts : "+ai.addAndGet(1));
			return routerConnection;
	    
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
		ConcurrentLinkedDeque<String> stack = dataSourceRoute.get();
		if(stack!=null&&stack.size()>0){
			stack.pop();
		}
		if(stack==null||stack.size()==0){
			dataSourceRoute.set(null); // help gc
		}
	}

	public String getRouterName() {
		return routerName;
	}

	public void setRouterName(String routerName) {
		this.routerName = routerName;
	}
}
