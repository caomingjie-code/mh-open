package com.javaoffers.mh.db.router.aop.datasource;

import com.javaoffers.mh.db.router.common.Router;
import com.javaoffers.mh.db.router.common.SQLUtils;
import com.javaoffers.mh.db.router.datasource.BaseComboPooledDataSource;
import com.javaoffers.mh.db.router.exception.BaseDataSourceException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description: 连接里面套连接,  spring把多个链接看成一个链接
 * RouterConnection中的所有链接状态具有一致性。（有可能元数据不一样， 因为每个链接有可能代表不同的数据库）
 * @Auther: create by cmj on 2020/9/4 11:31
 */
public class RouterConnection implements Connection {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouterConnection.class);
    public static AtomicInteger ai = new AtomicInteger(0);
    private static String nonoConnectionName = "None Conntion";
    private Map<String, Connection> routerConnection = new ConcurrentHashMap<>();
    private BaseComboPooledDataSource baseComboPooledDataSource;
    private Connection masterCon; //真实链接
    private NoneConntion noneConntion;

    public RouterConnection(BaseComboPooledDataSource baseComboPooledDataSource) {
        this.baseComboPooledDataSource = baseComboPooledDataSource;
    }

    /**
     * 保存当前获取的数据库连接
     *
     * @param key        数据源名称
     * @param connection 真实的链接
     * @return
     */
    public RouterConnection putConcurrentConnection(String key, Connection connection) {

        if (connection != null && connection instanceof NoneConntion) {
            noneConntion = (NoneConntion) connection;
            routerConnection.put(nonoConnectionName, noneConntion);
            return this;
        } else {
            routerConnection.put(key, connection);
        }
        masterCon = connection;
        return this;
    }

    /**
     * 获取栈顶对应的链接并解决在事物下的双重路由不能切换数据链接
     *
     * @return
     * @throws SQLException
     */
    private Connection getRouterConnection() {
        String routerSourceName = baseComboPooledDataSource.getRouterSourceName();

        Connection connection = getConnection(routerSourceName); //获取链接

        //如果是临时陆游立刻clean
        checkedIsTempRouter();

        return connection;
    }

    @Override
    public Statement createStatement() throws SQLException {
        Connection connection = getRouterConnection();
        return connection.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        readWriteSeparation(sql);
        return getRouterConnection().prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        readWriteSeparation(sql);
        return getRouterConnection().prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        readWriteSeparation(sql);
        return getRouterConnection().nativeSQL(sql);
    }

    /**
     * 因为 初始化和setAutoCommit保持同步状态,所以该方法也属于默认同步状态.(每个连接的状态一样)
     *
     * @return
     * @throws SQLException
     */
    @Override
    public boolean getAutoCommit() throws SQLException {
        //优化，RouterConnection中的链接所有状态具有一致性
        Collection<Connection> rcs = routerConnection.values();
        if (rcs != null && rcs.size() > 0) {
            for (Connection c : rcs) {
                return c.getAutoCommit();
            }
        }
        return getRouterConnection().getAutoCommit();
    }

    /**
     * 内嵌的所有连接状态保持同步
     *
     * @param autoCommit
     * @throws SQLException
     */
    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        funcRouterConnection(c -> {
            c.setAutoCommit(autoCommit);
        }, 0);
    }

    /**
     * 如果连接 未 '关闭'和'自动提交',则开始提交
     *
     * @throws SQLException
     */
    @Override
    public void commit() throws SQLException {
        funcRouterConnection(c -> {
            c.commit();
        }, 1);
    }

    /**
     * 对每个链接的函数处理
     *
     * @param func      对Connecton的函数处理
     * @param choseFunc 0:默认(只判断没有关闭),1:判断未开启自动提交
     * @throws SQLException
     */
    private void funcRouterConnection(VoidFunction<Connection> func, int choseFunc) throws SQLException {
        Collection<Connection> values = this.routerConnection.values();
        if (values != null && values.size() > 0) {
            for (Connection c : values) {
                if (!c.isClosed()) { //如果没有关闭
                    if (choseFunc == 0) { //默认逻辑
                        func.accept(c);
                    }
                    if (choseFunc == 1) { //没有自动提交
                        if (!c.getAutoCommit()) {
                            func.accept(c);
                        }
                    }
                }
            }
        }
    }

    /**
     * 如果连接 未 '关闭'和'自动提交',则开始回滚
     *
     * @throws SQLException
     */
    @Override
    public void rollback() throws SQLException {
        funcRouterConnection(c -> {
            c.rollback();
        }, 1);
    }

    /**
     * 如果连接 未 '关闭',则开始关闭
     *
     * @throws SQLException
     */
    @Override
    public void close() throws SQLException {
        funcRouterConnection(c -> {
            c.close();
            if (!(c instanceof NoneConntion)) {
                int i = ai.incrementAndGet();
                LOGGER.info("close  jdbc connection id[" + c.hashCode() + "] counts : " + i);
            }
        }, 0);
    }

    @Override
    public boolean isClosed() throws SQLException {

        //优化，RouterConnection中的链接所有状态具有一致性
        Collection<Connection> rcs = routerConnection.values();
        if (rcs != null && rcs.size() > 0) {
            for (Connection c : rcs) {
                return c.isClosed();
            }
        }

        return getRouterConnection().isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return getRouterConnection().getMetaData();
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        //优化，RouterConnection中的链接所有状态具有一致性
        Collection<Connection> rcs = routerConnection.values();
        if (rcs != null && rcs.size() > 0) {
            for (Connection c : rcs) {
                return c.isReadOnly();
            }
        }
        return getRouterConnection().isReadOnly();
    }

    /**
     * 所有连接保持同步: 如果连接未关闭则设置readOnly
     *
     * @param readOnly
     * @throws SQLException
     */
    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        funcRouterConnection(c -> {
            c.setReadOnly(readOnly);
        }, 0);
    }

    /**
     * 保持默认
     *
     * @return
     * @throws SQLException
     */
    @Override
    public String getCatalog() throws SQLException {
        return getRouterConnection().getCatalog();
    }

    /**
     * 保持默认
     * catalog指代数据库名称。
     * 1如果该参数不存在，则必须使用setCatalog设置数据库名称，否则会抛出 java.sql.SQLException: No database selected 异常。
     * 2如果在url中已经存在该参数，则可以使用setCatalog更换数据库名称。
     *
     * @param catalog
     * @throws SQLException
     */
    @Override
    public void setCatalog(String catalog) throws SQLException {
        getRouterConnection().setCatalog(catalog);
    }

    /**
     * 保持默认
     *
     * @return
     * @throws SQLException
     */
    @Override
    public int getTransactionIsolation() throws SQLException {
        return getRouterConnection().getTransactionIsolation();
    }

    /**
     * 保持默认
     *
     * @return
     * @throws SQLException
     */
    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        getRouterConnection().setTransactionIsolation(level);
    }

    /**
     * 保持默认
     *
     * @return
     * @throws SQLException
     */
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return getRouterConnection().getWarnings();
    }

    /**
     * 保持默认
     *
     * @return
     * @throws SQLException
     */
    @Override
    public void clearWarnings() throws SQLException {
        getRouterConnection().clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return getRouterConnection().createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        readWriteSeparation(sql);
        return getRouterConnection().prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        readWriteSeparation(sql);
        return getRouterConnection().prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    /**
     * 保持默认
     *
     * @return
     * @throws SQLException
     */
    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return getRouterConnection().getTypeMap();
    }

    /**
     * 保持默认
     *
     * @return
     * @throws SQLException
     */
    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        getRouterConnection().setTypeMap(map);
    }

    @Override
    public int getHoldability() throws SQLException {
        return getRouterConnection().getHoldability();
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        getRouterConnection().setHoldability(holdability);
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return getRouterConnection().setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return getRouterConnection().setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        getRouterConnection().rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        getRouterConnection().releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return getRouterConnection().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        readWriteSeparation(sql);
        return getRouterConnection().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        readWriteSeparation(sql);
        return getRouterConnection().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        readWriteSeparation(sql);
        return getRouterConnection().prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        readWriteSeparation(sql);
        return getRouterConnection().prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        readWriteSeparation(sql);
        return getRouterConnection().prepareStatement(sql, columnNames);
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
        //优化，RouterConnection中的链接所有状态具有一致性
        Collection<Connection> rcs = routerConnection.values();
        if (rcs != null && rcs.size() > 0) {
            for (Connection c : rcs) {
                return c.isValid(timeout);
            }
        }
        return getRouterConnection().isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        getRouterConnection().setClientInfo(name, value);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return getRouterConnection().getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return getRouterConnection().getClientInfo();
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        getRouterConnection().setClientInfo(properties);
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return getRouterConnection().createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return getRouterConnection().createStruct(typeName, attributes);
    }

    @Override
    public String getSchema() throws SQLException {
        return getRouterConnection().getSchema();
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        getRouterConnection().setSchema(schema);
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        getRouterConnection().abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        getRouterConnection().setNetworkTimeout(executor, milliseconds);
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
        //优化，RouterConnection中的链接所有状态具有一致性
        Collection<Connection> rcs = routerConnection.values();
        if (rcs != null && rcs.size() > 0) {
            for (Connection c : rcs) {
                return c.isWrapperFor(iface);
            }
        }
        return getRouterConnection().isWrapperFor(iface);
    }

    //读写分离
    private void readWriteSeparation(String sql) throws SQLException {
        Router router = baseComboPooledDataSource.getRouter();
        if (router == null) {
            //创建临时陆游
            router = new Router(true);
            router.setRouterName(BaseComboPooledDataSource.DEFAULT_ROUTER);//临时陆游设置为master, 在下面逻辑有可能会切换为读
            BaseComboPooledDataSource.pushStackRouter(router); //将临时陆游放入栈顶
        } else if (router.isForce()) {
            return;
        }
        boolean b = SQLUtils.checkedSqlIsRead(sql);
        String routerSourceName = router.getRouterName();
        if (b) { //如果是读sql
            if (!BaseComboPooledDataSource.checkedIsReadDataSource(routerSourceName)) { //如果不是读,则切换为读
                String[] readDataSources = baseComboPooledDataSource.getReadDataSource(routerSourceName);
                if (readDataSources != null && readDataSources.length > 0) {
                    //判断是否已经缓存了读，如果有直接使用，避免再此占用一个读链接
                    boolean isCached = false;//是否被缓存了
                    String cacheDb = null;
                    for (String readDB : readDataSources) {
                        if (routerConnection.containsKey(readDB)) {
                            cacheDb = readDB;
                            isCached = true;
                            LOGGER.info(" The cache " + cacheDb + " and " + routerSourceName + " functions the same " + ", so it switches to a " + cacheDb);
                            break;
                        }
                    }
                    if (!isCached) { //如果缓存没有命中
                        long l = (System.nanoTime() & 1) % readDataSources.length; //随机策略
                        cacheDb = readDataSources[(int) l];
                        if (StringUtils.isBlank(cacheDb)) {
                            throw BaseDataSourceException.getException("ReadDataSource Is Null");
                        }
                    }
                    router.setRouterName(cacheDb);
                    router.setSham(true);//还原初始状态，虚假陆游
                    BaseComboPooledDataSource.replaceFirstStackElement(router).getRouterName(); //返回旧的栈顶元素
                    LOGGER.info(routerSourceName + " Switch to read database : " + cacheDb);
                }
            } else { //优先使用已经存在的读链接
                String cacheDb = null;
                String[] writeDataSource = baseComboPooledDataSource.getWriteDataSource(routerSourceName);
                if (writeDataSource != null && writeDataSource.length > 0) {
                    for (int i = 0; i < writeDataSource.length; i++) {
                        String[] readDataSources = baseComboPooledDataSource.getReadDataSource(writeDataSource[i]);
                        if (readDataSources != null && readDataSources.length > 0) {
                            //判断是否已经缓存了读，如果有直接使用，避免再此占用一个读链接
                            for (String readDB : readDataSources) {
                                if (routerConnection.containsKey(readDB)) {
                                    cacheDb = readDB;
                                    if (!cacheDb.equalsIgnoreCase(routerSourceName)) { //不相同则使用缓存， 相同就忽略此不步骤，因为当前路由的和缓存的名称一样。
                                        router.setRouterName(cacheDb);
                                        LOGGER.info(" The cache " + cacheDb + " and " + routerSourceName + " functions the same " + ", so it switches to a " + cacheDb);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }

            }
        } else { //写sql
            if (!BaseComboPooledDataSource.checkedIsWriteDataSource(routerSourceName)) {//如果不是写，则切换为写
                String[] writeDataSources = baseComboPooledDataSource.getWriteDataSource(routerSourceName);
                if (writeDataSources != null && writeDataSources.length > 0) {
                    long l = (System.nanoTime() & 1) % writeDataSources.length; //随机策略
                    String wd = writeDataSources[(int) l];
                    if (StringUtils.isBlank(wd)) {
                        throw BaseDataSourceException.getException("WriteDataSource Is Null");
                    }
                    router.setRouterName(wd);
                    router.setSham(true);//还原初始状态，虚假陆游
                    BaseComboPooledDataSource.replaceFirstStackElement(router).getRouterName();
                    LOGGER.info(routerSourceName + " Switch to write database : " + wd);
                }
            }
        }
    }

    //获取真实链接
    private Connection getConnection(String routerSourceName) {
        Connection connection = routerConnection.get(routerSourceName); //真实链接
        if (connection == null) {
            try {
                RouterConnection rc = (RouterConnection) baseComboPooledDataSource.getConnection();//包装后的链接
                Collection<Connection> values = rc.routerConnection.values();
                if (values == null || values.size() != 1) {
                    new BaseDataSourceException("error get connection , connection is null or conntion size not is one").printStackTrace();
                } else {
                    ArrayList<Connection> connections = new ArrayList<>(values);
                    connection = connections.get(0);//hou qu zhen shi lian jie
                    routerConnection.put(routerSourceName, connection);
                    if (masterCon == null) {
                        connection.setAutoCommit(noneConntion.getAutoCommit());
                        //clean none connection
                        routerConnection.remove(nonoConnectionName);
                        noneConntion.close();

                    } else {
                        connection.setAutoCommit(masterCon.getAutoCommit());
                    }

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            //动态切换陆游时，有可能使用到缓存陆游链接，
            Router router = baseComboPooledDataSource.getRouter();
            if (router != null && router.isSham()) {
                router.setSham(false);
            }

        }
        return connection;
    }

    //检查是否是临时陆游
    private void checkedIsTempRouter() {
        Router router = baseComboPooledDataSource.getRouter();
        if (router != null && router.isTemporary()) {
            BaseComboPooledDataSource.clean();
        }
    }

    @FunctionalInterface
    interface VoidFunction<T> {
        void accept(T t) throws SQLException;
    }
}
