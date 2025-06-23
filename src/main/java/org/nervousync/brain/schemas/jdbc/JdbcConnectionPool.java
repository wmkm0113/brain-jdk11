/*
 * Licensed to the Nervousync Studio (NSYC) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nervousync.brain.schemas.jdbc;

import jakarta.annotation.Nonnull;
import org.intellij.lang.annotations.MagicConstant;
import org.nervousync.brain.configs.server.ServerInfo;
import org.nervousync.brain.configs.sharding.StrategyConfig;
import org.nervousync.brain.defines.ColumnDefine;
import org.nervousync.brain.defines.IndexDefine;
import org.nervousync.brain.defines.InitOption;
import org.nervousync.brain.defines.TableDefine;
import org.nervousync.brain.dialects.jdbc.JdbcDialect;
import org.nervousync.brain.enumerations.ddl.DDLType;
import org.nervousync.brain.enumerations.ddl.DropOption;
import org.nervousync.brain.exceptions.sql.MultilingualSQLException;
import org.nervousync.commons.Globals;
import org.nervousync.utils.DateTimeUtils;
import org.nervousync.utils.LoggerUtils;
import org.nervousync.utils.StringUtils;

import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <h2 class="en-US">JDBC database connection pool</h2>
 * <h2 class="zh-CN">JDBC数据源创建连接池</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Nov 12, 2019 19:17:06 $
 */
public final class JdbcConnectionPool {

    /**
     * <span class="en-US">Logger instance</span>
     * <span class="zh-CN">日志实例</span>
     */
    private static final LoggerUtils.Logger LOGGER = LoggerUtils.getLogger(JdbcConnectionPool.class);

    /**
     * <span class="en-US">JDBC dialect instance object</span>
     * <span class="zh-CN">JDBC方言实例对象</span>
     */
    private final JdbcSchema schema;
    /**
     * <span class="en-US">Connect properties instance object</span>
     * <span class="zh-CN">连接属性值</span>
     */
    private final Properties properties;
    /**
     * <span class="en-US">Database dialect instance object</span>
     * <span class="zh-CN">数据库方言实例对象</span>
     */
    private final JdbcDialect dialect;
    /**
     * <span class="en-US">Connection pool identify code</span>
     * <span class="zh-CN">连接池识别代码</span>
     */
    private final int identifyCode;
    /**
     * <span class="en-US">Database JDBC connection string</span>
     * <span class="zh-CN">数据库JDBC连接字符串</span>
     */
    private final String jdbcUrl;
    /**
     * <span class="en-US">Database connection queue</span>
     * <span class="zh-CN">数据库连接队列</span>
     */
    private final List<JdbcConnection> createdConnections;
    /**
     * <span class="en-US">Using database connection list</span>
     * <span class="zh-CN">使用中的数据库连接列表</span>
     */
    private final List<JdbcConnection> activeConnections;
    /**
     * <span class="en-US">Waiting to get count of connections</span>
     * <span class="zh-CN">等待获取连接的计数</span>
     */
    private final AtomicInteger waitCount;
    /**
     * <span class="en-US">Default database sharding value</span>
     * <span class="zh-CN">默认数据库分片值</span>
     */
    private final String defaultCatalog;
    /**
     * <span class="en-US">Parameter value of create databases</span>
     * <span class="zh-CN">创建数据库时使用的参数信息</span>
     */
    private final String databaseParameters;
    /**
     * <span class="en-US">List of existed database names</span>
     * <span class="zh-CN">已存在的数据库名列表</span>
     */
    private final List<String> databaseNames;
    /**
     * <span class="en-US">Mapping of database name and list of existed table names in the current database</span>
     * <span class="zh-CN">已存在的数据库名列表</span>
     */
    private final Map<String, List<String>> tableNames;
    /**
     * <span class="en-US">Using connection pool</span>
     * <span class="zh-CN">使用连接池</span>
     */
    private boolean pooled;
    /**
     * <span class="en-US">Create connection task execution status</span>
     * <span class="zh-CN">创建连接任务执行状态</span>
     */
    private boolean createRunning = Boolean.FALSE;


    /**
     * <h2 class="en-US">Private constructor method for database connection pool</h2>
     * <h2 class="zh-CN">数据库连接池的私有构造方法</h2>
     *
     * @param jdbcSchema     <span class="en-US">JDBC data source instance object</span>
     *                       <span class="zh-CN">JDBC数据源实例对象</span>
     * @param dialect
     * <span class="en-US">Database dialect instance object</span>
     * <span class="zh-CN">数据库方言实例对象</span>
     * @param pooled         <span class="en-US">Using connection pool</span>
     *                       <span class="zh-CN">使用连接池</span>
     * @param serverInfo     <span class="en-US">Server information</span>
     *                       <span class="zh-CN">服务器信息</span>
     * @param defaultCatalog <span class="en-US">Default database sharding value</span>
     *                       <span class="zh-CN">默认的数据库分片值</span>
     * @param databaseParameters
     * <span class="en-US">Parameter value of create databases</span>
     * <span class="zh-CN">创建数据库时使用的参数信息</span>
     */
    JdbcConnectionPool(final JdbcSchema jdbcSchema, final JdbcDialect dialect,
                       final boolean pooled, final ServerInfo serverInfo, final String defaultCatalog,
                       final String databaseParameters) throws SQLException {
        this.schema = jdbcSchema;
        this.dialect = dialect;
        this.properties = jdbcSchema.properties(serverInfo.getTrustStore(), serverInfo.getAuthentication());
        this.identifyCode = jdbcSchema.identifyCode(serverInfo);
        this.jdbcUrl = this.schema.shardingUrl(serverInfo);
        this.defaultCatalog = defaultCatalog;
        this.databaseParameters = StringUtils.isEmpty(databaseParameters) ? Globals.DEFAULT_VALUE_STRING : databaseParameters;
        this.pooled = pooled;
        this.createdConnections = new ArrayList<>();
        this.activeConnections = new ArrayList<>();
        this.waitCount = new AtomicInteger(Globals.INITIALIZE_INT_VALUE);
        this.databaseNames = new ArrayList<>();
        this.tableNames = new HashMap<>();
        this.createConnections();
        this.scanExists();
    }

    /**
     * <h3 class="en-US">Getter method for connection pool identify code</h3>
     * <h3 class="zh-CN">连接池识别代码的Getter方法</h3>
     *
     * @return <span class="en-US">Connection pool identify code</span>
     * <span class="zh-CN">连接池识别代码</span>
     */
    public int getIdentifyCode() {
        return this.identifyCode;
    }

    /**
     * <h3 class="en-US">Establish a real database connection</h3>
     * <h3 class="zh-CN">建立真实的数据库连接</h3>
     *
     * @return <span class="en-US">Database connection instance object</span>
     * <span class="zh-CN">数据库连接实例对象</span>
     */
    JdbcConnection createConnection(final String catalog) throws SQLException {
        boolean process = Boolean.TRUE;
        int retryCount = Globals.INITIALIZE_INT_VALUE;
        Connection connection = null;
        while (process) {
            try {
                connection = DriverManager.getConnection(this.jdbcUrl, this.properties);
            } catch (SQLException e) {
                LOGGER.error("Create_Connection_Error");
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Stack_Message_Error", e);
                }
            }
            if (connection == null) {
                if (retryCount < this.schema.retryCount) {
                    try {
                        retryCount++;
                        Thread.sleep(this.schema.retryPeriod);
                    } catch (InterruptedException e) {
                        LOGGER.error("Thread_Sleep_Error");
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Stack_Message_Error", e);
                        }
                        process = Boolean.FALSE;
                    }
                } else {
                    process = Boolean.FALSE;
                }
            } else {
                process = Boolean.FALSE;
            }
        }

        if (connection == null) {
            throw new MultilingualSQLException(0x00DB00000023L);
        }
        if (StringUtils.notBlank(catalog)) {
            if (!this.databaseNames.contains(catalog)) {
                String sqlCmd = this.dialect.createDatabase(catalog, this.databaseParameters);
                if (StringUtils.notBlank(sqlCmd)) {
                    try (Statement statement = connection.createStatement()) {
                        statement.execute(sqlCmd);
                    }
                }
            }
            connection.setCatalog(catalog);
        }
        return new JdbcConnection(this, connection,
                this.schema.getLowQueryTimeout(), this.schema.cachedLimitSize);
    }

    void configPooled(final boolean pooled) {
        boolean original = this.pooled;
        this.pooled = pooled;
        if (original) {
            if (!pooled) {
                this.closePool();
            }
        }
        if (this.pooled) {
            this.createConnections();
        }
    }

    /**
     * <h3 class="en-US">Destroy the given database connection</h3>
     * <h3 class="zh-CN">销毁给定的数据库连接</h3>
     *
     * @param connection <span class="en-US">Database connection instance object</span>
     *                   <span class="zh-CN">数据库连接实例对象</span>
     */
    void destroyConnection(final JdbcConnection connection) {
        try {
            if (connection == null || connection.isClosed()) {
                return;
            }
            connection.destroy();
        } catch (SQLException e) {
            LOGGER.error("Close_Connection_Error");
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Stack_Message_Error", e);
            }
        }
    }

    /**
     * <h3 class="en-US">Checks whether the given connection has expired</h3>
     * <h3 class="zh-CN">检查给定的连接是否已经失效</h3>
     *
     * @param connection <span class="en-US">Obtained connection</span>
     *                   <span class="zh-CN">获得的连接</span>
     * @return <span class="en-US">Check result</span>
     * <span class="zh-CN">检查结果</span>
     */
    boolean invalidConnection(@Nonnull final JdbcConnection connection) {
        boolean validate;
        Statement statement = null;
        try {
            String validateQuery = this.dialect.getValidationQuery();
            if (StringUtils.isEmpty(validateQuery)) {
                validate = connection.isValid(this.schema.getValidateTimeout());
            } else {
                statement = connection.createStatement();
                statement.setQueryTimeout(this.schema.getValidateTimeout());
                validate = statement.execute(validateQuery);
            }
        } catch (SQLException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Check_Connection_Error", e);
            }
            validate = Boolean.FALSE;
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ignore) {
                }
            }
        }

        if (!validate) {
            this.destroyConnection(connection);
        }
        return !validate;
    }

    /**
     * <h3 class="en-US">Number of connections in the connection queue</h3>
     * <h3 class="zh-CN">连接队列中的连接数</h3>
     *
     * @return <span class="en-US">Number of connections</span>
     * <span class="zh-CN">连接数</span>
     */
    int poolCount() {
        return this.createdConnections.size();
    }

    /**
     * <h3 class="en-US">Number of the using connections</h3>
     * <h3 class="zh-CN">使用中的连接数</h3>
     *
     * @return <span class="en-US">Number of connections</span>
     * <span class="zh-CN">连接数</span>
     */
    int activeCount() {
        return this.activeConnections.size();
    }

    /**
     * <h3 class="en-US">Number of clients waiting to get a connection</h3>
     * <h3 class="zh-CN">等待获得连接的客户端数量</h3>
     *
     * @return <span class="en-US">Number of clients waiting</span>
     * <span class="zh-CN">等待的客户端数量</span>
     */
    int waitCount() {
        return this.waitCount.get();
    }

    /**
     * <h3 class="en-US">Truncate all data tables</h3>
     * <h3 class="zh-CN">清空所有数据表</h3>
     *
     * @throws SQLException <span class="en-US">An error occurred during execution</span>
     *                      <span class="zh-CN">执行过程中出错</span>
     */
    void truncateTables() throws SQLException {
        for (String catalog : this.databaseNames) {
            try (Connection connection = this.obtainConnection(catalog);
                 Statement statement = connection.createStatement()) {
                for (String tableName : this.tableNames.getOrDefault(catalog, Collections.emptyList())) {
                    statement.addBatch(this.dialect.truncateTable(tableName));
                }
                statement.executeBatch();
            }
        }
    }

    /**
     * <h3 class="en-US">Truncate data table</h3>
     * <h3 class="zh-CN">清空数据表</h3>
     *
     * @param strategyConfig <span class="en-US">Database strategy configure information</span>
     *                       <span class="zh-CN">数据库分片配置信息</span>
     * @throws SQLException <span class="en-US">An error occurred during execution</span>
     *                      <span class="zh-CN">执行过程中出错</span>
     */
    void truncateTable(@Nonnull final StrategyConfig strategyConfig) throws SQLException {
        for (String catalog : this.databaseNames) {
            if (strategyConfig.dbMatch(catalog)) {
                try (Connection connection = this.obtainConnection(catalog);
                     Statement statement = connection.createStatement()) {
                    for (String tableName : this.tableNames.getOrDefault(catalog, Collections.emptyList())) {
                        if (strategyConfig.tableMatch(tableName)) {
                            statement.addBatch(this.dialect.truncateTable(tableName));
                        }
                    }
                    statement.executeBatch();
                }
            }
        }
    }

    /**
     * <h3 class="en-US">Drop all data tables</h3>
     * <h3 class="zh-CN">删除所有数据表</h3>
     *
     * @param dropOption <span class="en-US">Cascading delete options</span>
     *                   <span class="zh-CN">级联删除选项</span>
     * @throws SQLException <span class="en-US">An error occurred during execution</span>
     *                      <span class="zh-CN">执行过程中出错</span>
     */
    void dropTables(final DropOption dropOption) throws SQLException {
        for (String catalog : this.databaseNames) {
            try (Connection connection = this.obtainConnection(catalog);
                 Statement statement = connection.createStatement()) {
                for (String tableName : this.tableNames.getOrDefault(catalog, Collections.emptyList())) {
                    statement.addBatch(this.dialect.dropTableCommand(tableName, dropOption));
                }
                statement.executeBatch();
            }
        }
    }

    /**
     * <h3 class="en-US">Drop data table</h3>
     * <h3 class="zh-CN">删除数据表</h3>
     *
     * @param tableDefine       <span class="en-US">Table defines information</span>
     *                          <span class="zh-CN">数据表定义信息</span>
     * @param dropOption        <span class="en-US">Cascading delete options</span>
     *                          <span class="zh-CN">级联删除选项</span>
     * @param strategyConfig    <span class="en-US">Data table strategy configure information</span>
     *                          <span class="zh-CN">数据表分片配置信息</span>
     * @throws SQLException <span class="en-US">An error occurred during execution</span>
     *                      <span class="zh-CN">执行过程中出错</span>
     */
    void dropTable(@Nonnull final TableDefine tableDefine, final DropOption dropOption,
                   @Nonnull final StrategyConfig strategyConfig) throws SQLException {
        for (String catalog : this.databaseNames) {
            if (strategyConfig.dbMatch(catalog)) {
                try (Connection connection = this.obtainConnection(catalog);
                     Statement statement = connection.createStatement()) {
                    for (String tableName : this.tableNames.getOrDefault(catalog, Collections.emptyList())) {
                        if (strategyConfig.tableMatch(tableName)) {
                            for (IndexDefine indexDefine : tableDefine.getIndexDefines()) {
                                statement.addBatch(this.dialect.dropIndexCommand(indexDefine.getIndexName(), tableName));
                            }
                            statement.addBatch(this.dialect.dropTableCommand(tableName, dropOption));
                        }
                    }
                    if (strategyConfig.shardingTable()) {
                        statement.addBatch(this.dialect.dropShardingView(tableDefine));
                    }
                    statement.executeBatch();
                }
            }
        }
    }

    /**
     * <h3 class="en-US">Initialize data table</h3>
     * <h3 class="zh-CN">初始化数据表</h3>
     *
     * @param connection    <span class="en-US">Used database connection instance object</span>
     *                      <span class="zh-CN">使用的数据库连接实例对象</span>
     * @param statement     <span class="en-US">Executor instance object that executes SQL statements</span>
     *                      <span class="zh-CN">执行SQL语句的执行器实例对象</span>
     * @param ddlType       <span class="en-US">Enumeration value of DDL operate</span>
     *                      <span class="zh-CN">操作类型枚举值</span>
     * @param tableDefine   <span class="en-US">Table defines information</span>
     *                      <span class="zh-CN">数据表定义信息</span>
     * @param tableName     <span class="en-US">Table name</span>
     *                      <span class="zh-CN">数据表名</span>
     * @param shardingTable <span class="en-US">Table sharding flag</span>
     *                      <span class="zh-CN">数据表分片标记</span>
     * @param initOptionsMap   <span class="en-US">Data column initialize option</span>
     *                         <span class="zh-CN">数据列初始化选项</span>
     * @throws SQLException <span class="en-US">An error occurred during execution</span>
     *                      <span class="zh-CN">执行过程中出错</span>
     */
    private void initTable(final Connection connection, final Statement statement, @Nonnull final DDLType ddlType,
                           @Nonnull final TableDefine tableDefine, @Nonnull final String tableName,
                           final boolean shardingTable, @Nonnull final Map<String, InitOption> initOptionsMap) throws SQLException {
        ResultSet resultSet = null;
        try {
            if (this.tableNames.getOrDefault(connection.getCatalog(), Collections.emptyList()).contains(tableName)) {
                DatabaseMetaData databaseMetaData = connection.getMetaData();
                resultSet = databaseMetaData.getTables(connection.getCatalog(), null, tableName, new String[]{"TABLE"});
                if (resultSet.next()) {
                    ResultSet primaryKeyResultSet =
                            databaseMetaData.getPrimaryKeys(connection.getCatalog(), null, tableName);
                    List<String> primaryKeys = new ArrayList<>();
                    while (primaryKeyResultSet.next()) {
                        primaryKeys.add(primaryKeyResultSet.getString("COLUMN_NAME"));
                    }

                    List<String> uniqueKeys = new ArrayList<>();
                    ResultSet indexResultSet =
                            databaseMetaData.getIndexInfo(connection.getCatalog(), null, tableName,
                                    Boolean.TRUE, Boolean.TRUE);
                    while (indexResultSet.next()) {
                        uniqueKeys.add(indexResultSet.getString("COLUMN_NAME"));
                    }

                    ResultSet columnResultSet =
                            databaseMetaData.getColumns(connection.getCatalog(), connection.getSchema(),
                                    tableName, null);
                    List<ColumnDefine> existColumns = new ArrayList<>();
                    while (columnResultSet.next()) {
                        existColumns.add(ColumnDefine.newInstance(columnResultSet, this.dialect, primaryKeys, uniqueKeys));
                    }

                    if (DDLType.VALIDATE.equals(ddlType)) {
                        tableDefine.validate(existColumns);
                    } else if (DDLType.SYNCHRONIZE.equals(ddlType)) {
                        for (String sqlCmd : this.dialect.alterTableCommand(tableDefine, tableName, existColumns, initOptionsMap)) {
                            statement.addBatch(sqlCmd);
                        }
                    }
                }
            } else {
                if (DDLType.CREATE.equals(ddlType) || DDLType.CREATE_DROP.equals(ddlType)
                        || DDLType.CREATE_TRUNCATE.equals(ddlType) || DDLType.SYNCHRONIZE.equals(ddlType)) {
                    String sqlCmd = this.dialect.createTableCommand(tableDefine, tableName, initOptionsMap);
                    if (StringUtils.isEmpty(sqlCmd)) {
                        throw new MultilingualSQLException(0x00DB00000029L);
                    }
                    statement.addBatch(sqlCmd);
                    for (String indexCmd : this.dialect.createIndexCommand(tableDefine, tableName)) {
                        if (StringUtils.notBlank(indexCmd)) {
                            statement.addBatch(indexCmd);
                        }
                    }
                }
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }
        List<String> tableNames = this.tableNames.getOrDefault(connection.getCatalog(), Collections.emptyList());
        tableNames.add(tableName);
        this.tableNames.put(connection.getCatalog(), tableNames);
        if (shardingTable) {
            statement.addBatch(this.dialect.createShardingView(tableDefine.getTableName(), tableNames));
        }
    }

    /**
     * <h3 class="en-US">Initialize data table</h3>
     * <h3 class="zh-CN">初始化数据表</h3>
     *
     * @param tableDefine   <span class="en-US">Table defines information</span>
     *                      <span class="zh-CN">数据表定义信息</span>
     * @param catalog       <span class="en-US">Catalog name</span>
     *                      <span class="zh-CN">数据库名</span>
     * @param tableName     <span class="en-US">Table name</span>
     *                      <span class="zh-CN">数据表名</span>
     * @param shardingTable <span class="en-US">Table sharding flag</span>
     *                      <span class="zh-CN">数据表分片标记</span>
     * @param initOptionsMap   <span class="en-US">Data column initialize option</span>
     *                         <span class="zh-CN">数据列初始化选项</span>
     * @throws SQLException <span class="en-US">An error occurred during execution</span>
     *                      <span class="zh-CN">执行过程中出错</span>
     */
    void initTable(@Nonnull final TableDefine tableDefine, @Nonnull final String catalog,
                   @Nonnull final String tableName, final boolean shardingTable,
                   @Nonnull final Map<String, InitOption> initOptionsMap) throws SQLException {
        if (!this.databaseNames.contains(catalog)) {
            //  Create the database
            try (Connection connection = this.obtainConnection(this.defaultCatalog);
                 Statement statement = connection.createStatement()) {
                statement.execute(this.dialect.createDatabase(catalog, this.databaseParameters));
            }
            this.databaseNames.add(catalog);
        }
        try (Connection connection = this.obtainConnection(catalog);
             Statement statement = connection.createStatement()) {
            this.initTable(connection, statement, DDLType.SYNCHRONIZE, tableDefine, tableName, shardingTable, initOptionsMap);
            statement.executeBatch();
        }
    }

    /**
     * <h3 class="en-US">Initialize data table</h3>
     * <h3 class="zh-CN">初始化数据表</h3>
     *
     * @param ddlType     <span class="en-US">Enumeration value of DDL operate</span>
     *                    <span class="zh-CN">操作类型枚举值</span>
     * @param tableDefine <span class="en-US">Table defines information</span>
     *                    <span class="zh-CN">数据表定义信息</span>
     * @param initOptionsMap   <span class="en-US">Data column initialize option</span>
     *                         <span class="zh-CN">数据列初始化选项</span>
     * @throws SQLException <span class="en-US">An error occurred during execution</span>
     *                      <span class="zh-CN">执行过程中出错</span>
     */
    void initTable(@Nonnull final DDLType ddlType, @Nonnull final TableDefine tableDefine,
                   @Nonnull final StrategyConfig strategyConfig, @Nonnull final Map<String, InitOption> initOptionsMap)
            throws SQLException {
        for (String catalog : this.databaseNames) {
            if (strategyConfig.dbMatch(catalog)) {
                try (Connection connection = this.obtainConnection(catalog);
                     Statement statement = connection.createStatement()) {
                    List<String> tableNames = this.tableNames.getOrDefault(catalog, Collections.emptyList());
                    if (tableNames.isEmpty()) {
                        this.initTable(connection, statement, ddlType, tableDefine, strategyConfig.tableKey(Map.of()),
                                strategyConfig.shardingTable(), initOptionsMap);
                    } else {
                        for (String tableName : tableNames) {
                            if (strategyConfig.tableMatch(tableName)) {
                                this.initTable(connection, statement, ddlType, tableDefine, tableName,
                                        strategyConfig.shardingTable(), initOptionsMap);
                            }
                        }
                    }
                    statement.executeBatch();
                }
            }
        }
    }

    /**
     * <h3 class="en-US">Get a connection without transactional configured</h3>
     * <h3 class="zh-CN">获得无事务连接</h3>
     *
     * @param catalog   <span class="en-US">Sharded database name</span>
     *                  <span class="zh-CN">分片数据库名</span>
     * @throws SQLException <span class="en-US">An error occurred while obtaining the connection</span>
     *                      <span class="zh-CN">获得连接过程中出错</span>
     */
    JdbcConnection obtainConnection(final String catalog) throws SQLException {
        return this.obtainConnection(catalog, Connection.TRANSACTION_NONE);
    }

    /**
     * <h3 class="en-US">Get a connection without transactional configured</h3>
     * <h3 class="zh-CN">获得无事务连接</h3>
     *
     * @param catalog   <span class="en-US">Sharded database name</span>
     *                  <span class="zh-CN">分片数据库名</span>
     * @throws SQLException <span class="en-US">An error occurred while obtaining the connection</span>
     *                      <span class="zh-CN">获得连接过程中出错</span>
     */
    synchronized JdbcConnection retrieveConnection(final String catalog) throws SQLException {
        if (this.createdConnections.isEmpty()) {
            return null;
        }
        final String matchCatalog = StringUtils.isEmpty(catalog) ? this.defaultCatalog : catalog;
        JdbcConnection connection = this.createdConnections.stream()
                .filter(jdbcConnection -> jdbcConnection.match(this.identifyCode, matchCatalog))
                .findFirst()
                .orElse(this.createdConnections.get(0));
        connection.setCatalog(catalog);
        this.createdConnections.remove(connection);
        return connection;
    }

    /**
     * <h3 class="en-US">Get a connection</h3>
     * <h3 class="zh-CN">获得连接</h3>
     *
     * @param catalog   <span class="en-US">Sharded database name</span>
     *                  <span class="zh-CN">分片数据库名</span>
     * @param isolation <span class="en-US">Transactional isolation code value</span>
     *                  <span class="zh-CN">事务等级代码</span>
     * @return <span class="en-US">Obtained connection</span>
     * <span class="zh-CN">获得的连接</span>
     * @throws SQLException <span class="en-US">An error occurred while obtaining the connection</span>
     *                      <span class="zh-CN">获得连接过程中出错</span>
     */
    JdbcConnection obtainConnection(final String catalog,
                                    @MagicConstant(valuesFromClass = Connection.class) final int isolation)
            throws SQLException {
        JdbcConnection connection = null;
        if (!this.pooled) {
            connection = this.createConnection(catalog);
        } else {
            long beginTime = DateTimeUtils.currentUTCTimeMillis();
            long timeOutTime = this.schema.getConnectTimeout() * 1000L;

            boolean waitCount = Boolean.FALSE;

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Connection_Wait_Count", this.waitCount.get());
            }
            synchronized (this.createdConnections) {
                while (connection == null) {
                    connection = this.retrieveConnection(catalog);
                    if (connection == null) {
                        try {
                            connection = this.createConnection(catalog);
                        } catch (SQLException e) {
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("Create_Connection_Error", e);
                            }
                        }
                    }

                    if (connection != null && this.schema.testOnBorrow && this.invalidConnection(connection)) {
                        this.destroyConnection(connection);
                        connection = null;
                    }

                    if (connection == null) {
                        if (!waitCount) {
                            this.waitCount.incrementAndGet();
                            waitCount = Boolean.TRUE;
                        }

                        if (timeOutTime < (DateTimeUtils.currentUTCTimeMillis() - beginTime)) {
                            break;
                        }
                    }
                }
            }

            if (waitCount) {
                this.waitCount.decrementAndGet();
            }

            if (LOGGER.isDebugEnabled()) {
                if (waitCount) {
                    LOGGER.debug("Connection_From_Create");
                } else {
                    LOGGER.debug("Connection_From_Pool");
                }
                LOGGER.debug("Connection_Used_Time",
                        DateTimeUtils.currentUTCTimeMillis() - beginTime);
                LOGGER.debug("Pool_Connection_Debug", this.activeConnections.size(),
                        this.createdConnections.size());
            }
        }

        if (connection == null) {
            throw new MultilingualSQLException(0x00DB00000024L);
        }

        if (isolation != Connection.TRANSACTION_NONE) {
            connection.setAutoCommit(Boolean.FALSE);
            connection.setTransactionIsolation(isolation);
        }

        this.activeConnections.add(connection);
        connection.setCachedLimitSize(this.schema.cachedLimitSize);
        return connection;
    }

    /**
     * <h3 class="en-US">Destroy the current connection pool</h3>
     * <h3 class="zh-CN">销毁当前数据库连接池</h3>
     */
    void destroy() {
        //  Close all activated connection
        this.activeConnections.forEach(this::destroyConnection);
        this.activeConnections.clear();
        this.closePool();
    }

    /**
     * <h3 class="en-US">Close the all connections by the current connection pool</h3>
     * <h3 class="zh-CN">关闭所有连接池中已打开的连接</h3>
     */
    private void closePool() {
        this.createdConnections.forEach(this::destroyConnection);
        this.createdConnections.clear();
    }

    /**
     * <h3 class="en-US">Close the given connection object</h3>
     * <h3 class="zh-CN">关闭给定的连接对象</h3>
     *
     * @param connection <span class="en-US">Obtained connection</span>
     *                   <span class="zh-CN">获得的连接</span>
     * @throws SQLException <span class="en-US">An error occurred while close the connection</span>
     *                      <span class="zh-CN">关闭连接过程中出错</span>
     */
    void closeConnection(final JdbcConnection connection) throws SQLException {
        if (connection == null) {
            return;
        }

        this.activeConnections.remove(connection);

        if (!this.pooled || connection.getTransactionIsolation() != Connection.TRANSACTION_NONE) {
            this.destroyConnection(connection);
            return;
        }

        if (connection.isClosed()) {
            return;
        }

        if (this.schema.testOnReturn && this.invalidConnection(connection)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Invalid_Destroy_Connection");
            }
            this.destroyConnection(connection);
            return;
        }

        this.addConnection(connection);
    }

    /**
     * <h3 class="en-US">Check current connections count is greater or equal the maximum connections</h3>
     * <h3 class="zh-CN">检查当前连接数是否超过最大连接数</h3>
     *
     * @return <span class="en-US">Check result</span>
     * <span class="zh-CN">检查结果</span>
     */
    private boolean limitConnections() {
        return this.schema.maxConnections < (this.activeCount() + this.poolCount());
    }

    /**
     * <h3 class="en-US">Add connection to connection pool</h3>
     * <h3 class="zh-CN">添加连接到连接池</h3>
     *
     * @param connection <span class="en-US">Obtained connection</span>
     *                   <span class="zh-CN">获得的连接</span>
     */
    private void addConnection(@Nonnull final JdbcConnection connection) {
        if (!this.pooled) {
            this.destroyConnection(connection);
            return;
        }
        boolean destroy = Boolean.TRUE;
        synchronized (this.createdConnections) {
            if (this.needConnections()) {
                this.createdConnections.add(connection);
                destroy = Boolean.FALSE;
            }
        }
        if (destroy) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Pool_Full_Destroy_Connection");
            }
            this.destroyConnection(connection);
        }
    }

    /**
     * <h3 class="en-US">Check current connections count in connection pool is less than the pool maximum connections</h3>
     * <h3 class="zh-CN">检查当前连接池中的连接数是否小于连接池最大连接数</h3>
     *
     * @return <span class="en-US">Check result</span>
     * <span class="zh-CN">检查结果</span>
     */
    private boolean needConnections() {
        return this.pooled ? (this.poolCount() < this.schema.minConnections) : Boolean.FALSE;
    }

    /**
     * <h3 class="en-US">Check whether the number of connections in the database connection pool meets the configuration requirements</h3>
     * <h3 class="zh-CN">检查数据库连接池中的连接数是否满足配置需求</h3>
     */
    void createConnections() {
        if (this.createRunning || !this.pooled) {
            return;
        }

        this.createRunning = Boolean.TRUE;
        if (this.limitConnections()) {
            LOGGER.debug("Create_Connection_Full");
            return;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Create_Connection_Begin_Debug");
        }
        int poolSize = this.poolCount(), count = Globals.INITIALIZE_INT_VALUE;
        while (count < poolSize) {
            JdbcConnection connection = this.createdConnections.get(count);
            count++;
            if (connection == null) {
                continue;
            }
            if (this.invalidConnection(connection)) {
                this.destroyConnection(connection);
                this.createdConnections.remove(connection);
            }
        }
        synchronized (this.createdConnections) {
            while (this.needConnections()) {
                try {
                    this.addConnection(this.createConnection(this.defaultCatalog));
                } catch (SQLException e) {
                    LOGGER.error("Create_Connection_Error");
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("", e);
                    }
                    break;
                }
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Create_Connection_End_Debug");
        }
        this.createRunning = Boolean.FALSE;
    }

    void scanExists() throws SQLException {
        try (Connection connection = this.obtainConnection(this.defaultCatalog)) {
            ResultSet resultSet = connection.getMetaData().getCatalogs();
            while (resultSet.next()) {
                String databaseName = resultSet.getString("TABLE_CAT");
                this.databaseNames.add(databaseName);
            }
        }
        for (String catalog : this.databaseNames) {
            try (Connection connection = this.obtainConnection(catalog)) {
                DatabaseMetaData databaseMetaData = connection.getMetaData();
                ResultSet resultSet = databaseMetaData.getTables(connection.getCatalog(),
                        "*", "*", new String[]{"TABLE"});
                List<String> existTables = new ArrayList<>();
                while (resultSet.next()) {
                    String tableName = resultSet.getString("TABLE_NAME");
                    if (StringUtils.notBlank(tableName)) {
                        existTables.add(tableName);
                    }
                }
                this.tableNames.put(catalog, existTables);
            }
        }
    }
}
