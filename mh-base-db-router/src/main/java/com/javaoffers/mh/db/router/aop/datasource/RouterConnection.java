package com.javaoffers.mh.db.router.aop.datasource;

import com.javaoffers.mh.db.router.datasource.BaseComboPooledDataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description: 连接里面套连接,  spring把多个链接看成一个链接
 * @Auther: create by cmj on 2020/9/4 11:31
 */
public class RouterConnection implements Connection {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouterConnection.class);

    private Map<String,Connection> routerConnection = new ConcurrentHashMap<>();
    private BaseComboPooledDataSource baseComboPooledDataSource;
    public static AtomicInteger ai = new AtomicInteger(0);
    private Connection masterCon;

    public RouterConnection(BaseComboPooledDataSource baseComboPooledDataSource) {
        this.baseComboPooledDataSource = baseComboPooledDataSource;
    }

    /**
     * 保存当前获取的数据库连接
     * @param key
     * @param connection
     * @return
     */
    public RouterConnection putConcurrentConnection(String key ,Connection connection){
        routerConnection.put(key,connection);
        masterCon = connection;
        return this;
    }

    /**
     * 获取栈顶对应的链接并解决在事物下的双重路由不能切换数据链接
     * @return
     * @throws SQLException
     */
    private Connection getRouterConnection()  {
        String routerSourceName = BaseComboPooledDataSource.getRouterSourceName();
        //判断是否属于路由链接，并且数据源管理为JpaTransactionManager
        if(StringUtils.isBlank(routerSourceName)){
            if(BaseComboPooledDataSource.isRouterConnection()){
                routerSourceName = BaseComboPooledDataSource.getMeanClean();
            }else {
                routerSourceName = BaseComboPooledDataSource.DEFAULT_ROUTER;
            }

        }
        Connection connection = routerConnection.get(routerSourceName);
        if(connection ==null){
            try {
                connection = baseComboPooledDataSource.getConnection();
                if(connection instanceof RouterConnection ){
                    RouterConnection rc = (RouterConnection) connection;
                    Collection<Connection> values = rc.routerConnection.values();
                    for(Connection c : values){
                        c.setAutoCommit(masterCon.getAutoCommit()); //保持当前事物状态
                        routerConnection.put(routerSourceName,c);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    @Override
    public Statement createStatement() throws SQLException {
        Connection connection = getRouterConnection();
        return connection.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return getRouterConnection().prepareStatement( sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return getRouterConnection().prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return getRouterConnection().nativeSQL(sql);
    }

    /**
     * 内嵌的所有连接状态保持同步
     * @param autoCommit
     * @throws SQLException
     */
    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        funcRouterConnection(c->{c.setAutoCommit(autoCommit);},0);
    }

    /**
     * 因为 初始化和setAutoCommit保持同步状态,所以该方法也属于默认同步状态.(每个连接的状态一样)
     * @return
     * @throws SQLException
     */
    @Override
    public boolean getAutoCommit() throws SQLException {
        return getRouterConnection().getAutoCommit();
    }

    /**
     * 如果连接 未 '关闭'和'自动提交',则开始提交
     * @throws SQLException
     */
    @Override
    public void commit() throws SQLException {
        funcRouterConnection(c->{c.commit();},1);
    }

    /**
     * 对每个链接的函数处理
     * @param func 对Connecton的函数处理
     * @param choseFunc 0:默认(只判断没有关闭),1:判断未开启自动提交
     * @throws SQLException
     */
    private void funcRouterConnection(VoidFunction<Connection> func, int choseFunc) throws SQLException {
        Collection<Connection> values = this.routerConnection.values();
        if(values!=null && values.size()>0){
            for(Connection c : values){
                if(!c.isClosed()){ //如果没有关闭
                    if(choseFunc == 0){ //默认逻辑
                        func.accept(c);
                    }
                    if(choseFunc == 1){ //没有自动提交
                        if(!c.getAutoCommit()){
                            func.accept(c);
                        }
                    }
                }
            }
        }
    }

    /**
     * 如果连接 未 '关闭'和'自动提交',则开始回滚
     * @throws SQLException
     */
    @Override
    public void rollback() throws SQLException {
        funcRouterConnection(c->{c.rollback();},1);
    }

    /**
     * 如果连接 未 '关闭',则开始关闭
     * @throws SQLException
     */
    @Override
    public void close() throws SQLException {
        funcRouterConnection(c->{
            c.close();
            int i = ai.incrementAndGet();
            LOGGER.info("close jdbc connection counts : "+i);
            },0);
    }

    @Override
    public boolean isClosed() throws SQLException {
        return getRouterConnection().isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return getRouterConnection().getMetaData();
    }

    /**
     * 所有连接保持同步: 如果连接未关闭则设置readOnly
     * @param readOnly
     * @throws SQLException
     */
    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        funcRouterConnection(c->{c.setReadOnly(readOnly);},0);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return getRouterConnection().isReadOnly();
    }

    /**
     * 保持默认
     * catalog指代数据库名称。
     * 1如果该参数不存在，则必须使用setCatalog设置数据库名称，否则会抛出 java.sql.SQLException: No database selected 异常。
     * 2如果在url中已经存在该参数，则可以使用setCatalog更换数据库名称。
     * @param catalog
     * @throws SQLException
     */
    @Override
    public void setCatalog(String catalog) throws SQLException {
        getRouterConnection().setCatalog( catalog);
    }

    /**
     * 保持默认
     * @return
     * @throws SQLException
     */
    @Override
    public String getCatalog() throws SQLException {
        return getRouterConnection().getCatalog();
    }
    /**
     * 保持默认
     * @return
     * @throws SQLException
     */
    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        getRouterConnection().setTransactionIsolation( level);
    }
    /**
     * 保持默认
     * @return
     * @throws SQLException
     */
    @Override
    public int getTransactionIsolation() throws SQLException {
        return getRouterConnection().getTransactionIsolation();
    }

    /**
     * 保持默认
     * @return
     * @throws SQLException
     */
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return getRouterConnection().getWarnings();
    }
    /**
     * 保持默认
     * @return
     * @throws SQLException
     */
    @Override
    public void clearWarnings() throws SQLException {
        getRouterConnection().clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return getRouterConnection().createStatement( resultSetType,  resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return getRouterConnection().prepareStatement( sql,  resultSetType,  resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return getRouterConnection(). prepareCall( sql,  resultSetType,  resultSetConcurrency);
    }
    /**
     * 保持默认
     * @return
     * @throws SQLException
     */
    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return getRouterConnection().getTypeMap();
    }
    /**
     * 保持默认
     * @return
     * @throws SQLException
     */
    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        getRouterConnection().setTypeMap( map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        getRouterConnection().setHoldability( holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        return getRouterConnection().getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return getRouterConnection().setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return getRouterConnection().setSavepoint( name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        getRouterConnection().rollback( savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        getRouterConnection().releaseSavepoint( savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return getRouterConnection().createStatement( resultSetType,  resultSetConcurrency,  resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return getRouterConnection().prepareStatement( sql,  resultSetType,  resultSetConcurrency,  resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return getRouterConnection().prepareCall( sql,  resultSetType,  resultSetConcurrency,  resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return getRouterConnection().prepareStatement( sql,  autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return getRouterConnection().prepareStatement( sql,  columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return getRouterConnection().prepareStatement( sql, columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
        return getRouterConnection().createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return getRouterConnection().createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return getRouterConnection().createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return getRouterConnection().createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return getRouterConnection().isValid( timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        getRouterConnection().setClientInfo( name,  value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        getRouterConnection().setClientInfo( properties);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return getRouterConnection().getClientInfo( name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return getRouterConnection().getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return getRouterConnection().createArrayOf( typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return getRouterConnection().createStruct( typeName, attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        getRouterConnection().setSchema(schema);
    }

    @Override
    public String getSchema() throws SQLException {
        return getRouterConnection().getSchema();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        getRouterConnection().abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        getRouterConnection().setNetworkTimeout(executor,milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return getRouterConnection().getNetworkTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return getRouterConnection().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return getRouterConnection().isWrapperFor(iface);
    }

    @FunctionalInterface
    interface VoidFunction<T>{
        void accept(T t) throws SQLException ;
    }
}
