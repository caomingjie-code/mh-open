package com.javaoffers.mh.db.router.aop.datasource;

import com.javaoffers.mh.db.router.datasource.BaseComboPooledDataSource;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * create by cmj on 2021-02-08 20:52
 * 解决初始陆游为空 或在没有被 ‘陆游拦截器’ 所拦截，并且处于读写分离的场景下， 此时如果获取链接（并且没有在分析sql语句为读/写之前），会造成的浪费。
 * 所以使用空链接代替。在分析sql语句并获取真实链接后，此空链接会在RouterConnection 移除。
 */
public class NoneConntion implements Connection {

    private boolean autoCommit = true;
    private boolean isClosed = false;
    private boolean readOnly = false;
    private BaseComboPooledDataSource baseComboPooledDataSource ;
    Connection superConnection = null;//真实链接，该链接有可能会被创建出来

    public NoneConntion(BaseComboPooledDataSource baseComboPooledDataSource) {
        this.baseComboPooledDataSource = baseComboPooledDataSource;
    }

    @Override
    public Statement createStatement() throws SQLException {
        throwsExcetion();
        return superConnection.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        throwsExcetion();
        return superConnection.prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        throwsExcetion();
        return superConnection.prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        throwsExcetion();
        return superConnection.nativeSQL( sql);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        this.autoCommit = autoCommit;
        if(superConnection!=null){
            superConnection.setAutoCommit(autoCommit);
        }
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return autoCommit;
    }

    @Override
    public void commit() throws SQLException {
        if(superConnection!=null){
            superConnection.commit();
        }

    }

    @Override
    public void rollback() throws SQLException {

        if(superConnection!=null){
            superConnection.rollback();
        }

    }

    @Override
    public void close() throws SQLException {
        if(superConnection!=null){
            superConnection.close();
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        return isClosed;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        throwsExcetion();
        return superConnection.getMetaData();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        this.readOnly = readOnly;
        if(superConnection!=null){
            superConnection.setReadOnly(readOnly);
        }
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return readOnly;
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        throwsExcetion();
        if(superConnection!=null){
            superConnection.setCatalog(catalog);
        }
    }

    @Override
    public String getCatalog() throws SQLException {
        throwsExcetion();
        return superConnection.getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        throwsExcetion();
        if(superConnection!=null){
            superConnection.setTransactionIsolation(level);
        }
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        if(superConnection!=null){
            return superConnection.getTransactionIsolation();
        }
        return Connection.TRANSACTION_REPEATABLE_READ;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        throwsExcetion();
        return superConnection.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        if(superConnection!=null){
            superConnection.clearWarnings();
        }
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        throwsExcetion();
        return superConnection.createStatement( resultSetType,  resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        throwsExcetion();
        return superConnection.prepareStatement( sql,  resultSetType,  resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        throwsExcetion();
        return superConnection.prepareCall( sql,  resultSetType,  resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        throwsExcetion();
        return superConnection.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        throwsExcetion();
        superConnection.setTypeMap(map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        throwsExcetion();
        superConnection.setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        throwsExcetion();
        return superConnection.getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        throwsExcetion();
        return superConnection.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        throwsExcetion();
        return superConnection.setSavepoint( name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        throwsExcetion();
        superConnection.rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        throwsExcetion();
        superConnection.releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        throwsExcetion();
        return superConnection.createStatement( resultSetType,  resultSetConcurrency,  resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        throwsExcetion();
        return superConnection.prepareStatement( sql,  resultSetType,  resultSetConcurrency,  resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        throwsExcetion();
        return superConnection.prepareCall( sql,  resultSetType,  resultSetConcurrency,  resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        throwsExcetion();
        return superConnection.prepareStatement( sql,  autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        throwsExcetion();
        return superConnection.prepareStatement( sql,  columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        throwsExcetion();
        return superConnection.prepareStatement( sql,  columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
        throwsExcetion();
        return superConnection.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        throwsExcetion();
        return superConnection.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        throwsExcetion();
        return superConnection.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        throwsExcetion();
        return superConnection.createSQLXML() ;
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return false;
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        throwsExcetion();
        superConnection.setClientInfo(name,value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        throwsExcetion();
        superConnection.setClientInfo(properties);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        throwsExcetion();
        return superConnection.getClientInfo( name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        throwsExcetion();
        return superConnection.getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        throwsExcetion();
        return superConnection.createArrayOf(typeName,elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        throwsExcetion();
        return superConnection.createStruct(typeName,attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        throwsExcetion();
        superConnection.setSchema(schema);
    }

    @Override
    public String getSchema() throws SQLException {
        throwsExcetion();
        return superConnection.getSchema();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        throwsExcetion();
        superConnection.abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        throwsExcetion();
        superConnection.setNetworkTimeout(executor,1000*60);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return 1000*60;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throwsExcetion();
        return superConnection.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    public  void throwsExcetion(){
        try {
            if(superConnection==null) {
                synchronized (this) {
                    if (superConnection == null) {
                        superConnection = baseComboPooledDataSource.getSuperConnection();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("none connection be accessed !!! ");
    }
}
