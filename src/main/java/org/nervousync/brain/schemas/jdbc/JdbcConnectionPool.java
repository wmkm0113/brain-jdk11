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
import org.nervousync.brain.configs.sharding.StrategyConfig;
import org.nervousync.brain.defines.ColumnDefine;
import org.nervousync.brain.defines.IndexDefine;
import org.nervousync.brain.defines.TableDefine;
import org.nervousync.brain.dialects.jdbc.JdbcDialect;
import org.nervousync.brain.enumerations.ddl.DDLType;
import org.nervousync.brain.enumerations.ddl.DropOption;
import org.nervousync.brain.exceptions.sql.MultilingualSQLException;
import org.nervousync.commons.Globals;
import org.nervousync.utils.core.DateTimeUtils;
import org.nervousync.utils.core.StringUtils;
import org.nervousync.utils.logger.LoggerUtils;

import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
	private final Queue<JdbcConnection> createdConnections;
	/**
	 * <span class="en-US">Using database connection list</span>
	 * <span class="zh-CN">使用中的数据库连接列表</span>
	 */
	private final List<JdbcConnection> activeConnections;
	/**
	 * <span class="en-US">Waiting to acquire connection counter</span>
	 * <span class="zh-CN">等待获取连接计数器</span>
	 */
	private final AtomicInteger waitCount = new AtomicInteger(Globals.INITIALIZE_INT_VALUE);
	/**
	 * <span class="en-US">Thread lock instance object</span>
	 * <span class="zh-CN">线程锁实例对象</span>
	 */
	private final Lock lock = new ReentrantLock();
	/**
	 * <span class="en-US">Waiting condition</span>
	 * <span class="zh-CN">等待线程</span>
	 */
	private final Condition waitCondition = this.lock.newCondition();
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
	 * <span class="en-US">Using connection pool</span>
	 * <span class="zh-CN">使用连接池</span>
	 */
	private boolean pooled;
	/**
	 * <span class="en-US">Create connection task execution status</span>
	 * <span class="zh-CN">创建连接任务执行状态</span>
	 */
	private final AtomicBoolean createRunning = new AtomicBoolean(Boolean.FALSE);


	/**
	 * <h3 class="en-US">Private constructor method for database connection pool</h3>
	 * <h3 class="zh-CN">数据库连接池的私有构造方法</h3>
	 *
	 * @param jdbcSchema         <span class="en-US">JDBC data source instance object</span>
	 *                           <span class="zh-CN">JDBC数据源实例对象</span>
	 * @param dialect            <span class="en-US">Database dialect instance object</span>
	 *                           <span class="zh-CN">数据库方言实例对象</span>
	 * @param pooled             <span class="en-US">Using connection pool</span>
	 *                           <span class="zh-CN">使用连接池</span>
	 * @param identifyCode       <span class="en-US">Connection pool identify code</span>
	 *                           <span class="zh-CN">连接池识别代码</span>
	 * @param properties         <span class="en-US">Connect properties instance object</span>
	 *                           <span class="zh-CN">连接属性值</span>
	 * @param jdbcUrl            <span class="en-US">Database JDBC connection string</span>
	 *                           <span class="zh-CN">数据库JDBC连接字符串</span>
	 * @param defaultCatalog     <span class="en-US">Default database sharding value</span>
	 *                           <span class="zh-CN">默认的数据库分片值</span>
	 * @param databaseParameters <span class="en-US">Parameter value of create databases</span>
	 *                           <span class="zh-CN">创建数据库时使用的参数信息</span>
	 */
	JdbcConnectionPool(final JdbcSchema jdbcSchema, final JdbcDialect dialect, final boolean pooled,
	                   final int identifyCode, final Properties properties, final String jdbcUrl,
	                   final String defaultCatalog, final String databaseParameters) throws SQLException {
		this.schema = jdbcSchema;
		this.dialect = dialect;
		this.properties = properties;
		this.identifyCode = identifyCode;
		this.jdbcUrl = jdbcUrl;
		this.defaultCatalog = defaultCatalog;
		this.databaseParameters = StringUtils.isEmpty(databaseParameters) ? Globals.DEFAULT_VALUE_STRING : databaseParameters;
		this.pooled = pooled;
		this.createdConnections = new LinkedList<>();
		this.activeConnections = new ArrayList<>();
		this.databaseNames = new ArrayList<>();
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
	JdbcConnection createConnection() throws SQLException {
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
				if (retryCount < this.schema.getRetryCount()) {
					try {
						retryCount++;
						Thread.sleep(this.schema.getRetryPeriod());
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
		return new JdbcConnection(this, connection,
				this.schema.getLowQueryTimeout(), this.schema.getCachedLimitSize());
	}

	void configPooled(final boolean pooled) {
		boolean original = this.pooled;
		this.pooled = pooled;
		if (original && !pooled) {
			this.closePool();
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
	boolean invalidConnection(final JdbcConnection connection) {
		if (connection == null) {
			return Boolean.TRUE;
		}
		boolean validate;
		try {
			validate = connection.isValid(this.schema.getValidateTimeout());
		} catch (SQLException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Check_Connection_Error", e);
			}
			validate = Boolean.FALSE;
		}

		if (!validate) {
			this.destroyConnection(connection);
		}
		return !validate;
	}

	/**
	 * <h3 class="en-US">Total number of connections in the connection pool</h3>
	 * <h3 class="zh-CN">获取连接池中的总连接数</h3>
	 *
	 * @return <span class="en-US">Number of connections</span>
	 * <span class="zh-CN">总连接数</span>
	 */
	int poolCount() {
		this.lock.lock();
		try {
			return this.createdConnections.size() + this.activeConnections.size();
		} finally {
			this.lock.unlock();
		}
	}

	/**
	 * <h3 class="en-US">Number of connections in the connection queue</h3>
	 * <h3 class="zh-CN">获取当前队列中的连接数</h3>
	 *
	 * @return <span class="en-US">Number of connections</span>
	 * <span class="zh-CN">连接数</span>
	 */
	int queueCount() {
		this.lock.lock();
		try {
			return this.createdConnections.size();
		} finally {
			this.lock.unlock();
		}
	}

	/**
	 * <h3 class="en-US">Number of the using connections</h3>
	 * <h3 class="zh-CN">使用中的连接数</h3>
	 *
	 * @return <span class="en-US">Number of connections</span>
	 * <span class="zh-CN">连接数</span>
	 */
	int activeCount() {
		this.lock.lock();
		try {
			return this.activeConnections.size();
		} finally {
			this.lock.unlock();
		}
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
				for (String tableName : this.tableNames(connection, null)) {
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
					for (String tableName : this.tableNames(connection, strategyConfig)) {
						String sqlCmd = this.dialect.truncateTable(tableName);
						if (StringUtils.notBlank(sqlCmd)) {
							statement.addBatch(sqlCmd);
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
				for (String tableName : this.tableNames(connection, null)) {
					statement.addBatch(this.dialect.dropTableCommand(tableName, dropOption));
				}
				statement.executeBatch();
			}
		}
	}

	/**
	 * <h3 class="en-US">Drop the data table</h3>
	 * <h3 class="zh-CN">删除数据表</h3>
	 *
	 * @param tableDefine    <span class="en-US">Table defines information</span>
	 *                       <span class="zh-CN">数据表定义信息</span>
	 * @param dropOption     <span class="en-US">Cascading delete options</span>
	 *                       <span class="zh-CN">级联删除选项</span>
	 * @param strategyConfig <span class="en-US">Data table strategy configure information</span>
	 *                       <span class="zh-CN">数据表分片配置信息</span>
	 * @throws SQLException <span class="en-US">An error occurred during execution</span>
	 *                      <span class="zh-CN">执行过程中出错</span>
	 */
	void dropTable(@Nonnull final TableDefine tableDefine, final DropOption dropOption,
	               @Nonnull final StrategyConfig strategyConfig) throws SQLException {
		for (String catalog : this.databaseNames) {
			if (strategyConfig.dbMatch(catalog)) {
				try (Connection connection = this.obtainConnection(catalog);
				     Statement statement = connection.createStatement()) {
					for (String tableName : this.tableNames(connection, strategyConfig)) {
						for (IndexDefine indexDefine : tableDefine.getIndexDefines()) {
							statement.addBatch(this.dialect.dropIndexCommand(indexDefine.getIndexName(), tableName));
						}
						statement.addBatch(this.dialect.dropTableCommand(tableName, dropOption));
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
	 * <h3 class="en-US">Get data table names list</h3>
	 * <h3 class="zh-CN">获取数据表名称列表</h3>
	 *
	 * @param connection     <span class="en-US">Used database connection instance object</span>
	 *                       <span class="zh-CN">使用的数据库连接实例对象</span>
	 * @param strategyConfig <span class="en-US">Data table strategy configure information</span>
	 *                       <span class="zh-CN">数据表分片配置信息</span>
	 * @return <span class="en-US">Table names list</span>
	 * <span class="zh-CN">数据表名列表</span>
	 * @throws SQLException <span class="en-US">An error occurred during execution</span>
	 *                      <span class="zh-CN">执行过程中出错</span>
	 */
	private List<String> tableNames(@Nonnull final Connection connection, StrategyConfig strategyConfig)
			throws SQLException {
		Set<String> tableNames = new HashSet<>();
		try (ResultSet resultSet =
				     connection.getMetaData().getTables(connection.getCatalog(), connection.getSchema(),
						     "%", new String[]{"TABLE"})) {
			while (resultSet.next()) {
				String tableName = resultSet.getString("TABLE_NAME");
				if (strategyConfig == null || strategyConfig.tableMatch(tableName)) {
					tableNames.add(tableName);
				}
			}
		}
		return new ArrayList<>(tableNames);
	}

	/**
	 * <h3 class="en-US">Initialize data table</h3>
	 * <h3 class="zh-CN">初始化数据表</h3>
	 *
	 * @param connection     <span class="en-US">Used database connection instance object</span>
	 *                       <span class="zh-CN">使用的数据库连接实例对象</span>
	 * @param statement      <span class="en-US">Executor instance object that executes SQL statements</span>
	 *                       <span class="zh-CN">执行SQL语句的执行器实例对象</span>
	 * @param ddlType        <span class="en-US">Enumeration value of DDL operate</span>
	 *                       <span class="zh-CN">操作类型枚举值</span>
	 * @param tableDefine    <span class="en-US">Table defines information</span>
	 *                       <span class="zh-CN">数据表定义信息</span>
	 * @param strategyConfig <span class="en-US">Data table strategy configure information</span>
	 *                       <span class="zh-CN">数据表分片配置信息</span>
	 * @param tableName      <span class="en-US">Table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @throws SQLException <span class="en-US">An error occurred during execution</span>
	 *                      <span class="zh-CN">执行过程中出错</span>
	 */
	private void initTable(final JdbcConnection connection, final Statement statement, @Nonnull final DDLType ddlType,
	                       @Nonnull final TableDefine tableDefine, final StrategyConfig strategyConfig,
	                       @Nonnull final String tableName) throws SQLException {
		String catalog = connection.getCatalog();
		if (StringUtils.isEmpty(catalog)) {
			catalog = Globals.DEFAULT_VALUE_STRING;
		}
		String schema = connection.getSchema();
		String tableNamePattern = this.dialect.nameCase(tableName);
		DatabaseMetaData metaData = connection.getMetaData();
		try (ResultSet resultSet = metaData.getTables(catalog, schema, tableNamePattern, new String[]{"TABLE"})) {
			if (resultSet.next()) {
				if (DDLType.SYNCHRONIZE.equals(ddlType) || DDLType.VALIDATE.equals(ddlType)) {
					List<String> primaryKeys = new ArrayList<>();
					try (ResultSet primaryKeyResultSet = metaData.getPrimaryKeys(catalog, schema, tableNamePattern)) {
						while (primaryKeyResultSet.next()) {
							primaryKeys.add(primaryKeyResultSet.getString("COLUMN_NAME"));
						}
					}

					List<String> uniqueKeys = new ArrayList<>();
					try (ResultSet indexResultSet = metaData.getIndexInfo(catalog, schema, tableNamePattern, Boolean.TRUE, Boolean.TRUE)) {
						while (indexResultSet.next()) {
							uniqueKeys.add(indexResultSet.getString("COLUMN_NAME"));
						}
					}


					List<ColumnDefine> existColumns = new ArrayList<>();
					try (ResultSet columnResultSet = metaData.getColumns(catalog, schema, tableNamePattern, "%")) {
						while (columnResultSet.next()) {
							existColumns.add(ColumnDefine.newInstance(columnResultSet, this.dialect, primaryKeys, uniqueKeys));
						}
					}

					switch (ddlType) {
						case VALIDATE:
							tableDefine.validate(existColumns);
							break;
						case SYNCHRONIZE:
							for (String sqlCmd : this.dialect.alterTableCommand(tableDefine, tableName, existColumns)) {
								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug("Execute_Query_Log", sqlCmd);
								}
								statement.addBatch(sqlCmd);
							}
							break;
					}
				}
			} else {
				if (DDLType.CREATE.equals(ddlType) || DDLType.CREATE_DROP.equals(ddlType)
						|| DDLType.CREATE_TRUNCATE.equals(ddlType) || DDLType.SYNCHRONIZE.equals(ddlType)) {
					String sqlCmd = this.dialect.createTableCommand(tableDefine, tableName);
					if (StringUtils.isEmpty(sqlCmd)) {
						throw new MultilingualSQLException(0x00DB00000029L);
					}
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Execute_Query_Log", sqlCmd);
					}
					statement.addBatch(sqlCmd);
					for (String indexCmd : this.dialect.createIndexCommand(tableDefine, tableName)) {
						if (StringUtils.notBlank(indexCmd)) {
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("Execute_Query_Log", indexCmd);
							}
							statement.addBatch(indexCmd);
						}
					}
				}
			}
		}
		if (strategyConfig.shardingTable()) {
			statement.addBatch(this.dialect.createShardingView(tableDefine.getTableName(),
					this.tableNames(connection, strategyConfig)));
		}
	}

	/**
	 * <h3 class="en-US">Initialize data table</h3>
	 * <h3 class="zh-CN">初始化数据表</h3>
	 *
	 * @param tableDefine    <span class="en-US">Table defines information</span>
	 *                       <span class="zh-CN">数据表定义信息</span>
	 * @param strategyConfig <span class="en-US">Data table strategy configure information</span>
	 *                       <span class="zh-CN">数据表分片配置信息</span>
	 * @param catalog        <span class="en-US">Catalog name</span>
	 *                       <span class="zh-CN">数据库名</span>
	 * @param tableName      <span class="en-US">Table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @throws SQLException <span class="en-US">An error occurred during execution</span>
	 *                      <span class="zh-CN">执行过程中出错</span>
	 */
	void initTable(@Nonnull final TableDefine tableDefine, final StrategyConfig strategyConfig,
	               @Nonnull final String catalog, @Nonnull final String tableName) throws SQLException {
		String currentCatalog = StringUtils.isEmpty(catalog) ? this.defaultCatalog : catalog;
		try (JdbcConnection connection = this.obtainConnection(currentCatalog)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Transactional_Level_Debug", connection.getTransactionIsolation());
			}
			if (StringUtils.notBlank(currentCatalog) && !this.databaseNames.contains(currentCatalog)) {
				//  Create the database
				try (Statement statement = connection.createStatement()) {
					statement.execute(this.dialect.createDatabase(currentCatalog, this.databaseParameters));
				}
				this.databaseNames.add(currentCatalog);
			}
			try (Statement statement = connection.createStatement()) {
				this.initTable(connection, statement, DDLType.CREATE, tableDefine, strategyConfig, tableName);
				statement.executeBatch();
			}
		}
	}

	/**
	 * <h3 class="en-US">Initialize data table</h3>
	 * <h3 class="zh-CN">初始化数据表</h3>
	 *
	 * @param ddlType        <span class="en-US">Enumeration value of DDL operate</span>
	 *                       <span class="zh-CN">操作类型枚举值</span>
	 * @param tableDefine    <span class="en-US">Table defines information</span>
	 *                       <span class="zh-CN">数据表定义信息</span>
	 * @param strategyConfig <span class="en-US">Data table strategy configure information</span>
	 *                       <span class="zh-CN">数据表分片配置信息</span>
	 * @throws SQLException <span class="en-US">An error occurred during execution</span>
	 *                      <span class="zh-CN">执行过程中出错</span>
	 */
	void initTable(@Nonnull final DDLType ddlType, @Nonnull final TableDefine tableDefine,
	               @Nonnull final StrategyConfig strategyConfig) throws SQLException {
		for (String catalog : this.databaseNames) {
			if (strategyConfig.dbMatch(catalog)) {
				try (JdbcConnection connection = this.obtainConnection(catalog);
				     Statement statement = connection.createStatement()) {
					List<String> tableNames = this.tableNames(connection, strategyConfig);
					String currentName = strategyConfig.tableKey(Map.of());
					if (!tableNames.contains(currentName)) {
						this.initTable(connection, statement, ddlType, tableDefine, strategyConfig, currentName);
					}
					if (this.dialect.isDatabaseSharding()) {
						for (String tableName : tableNames) {
							if (strategyConfig.tableMatch(tableName)) {
								this.initTable(connection, statement, ddlType, tableDefine, strategyConfig, tableName);
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
	 * @param catalog <span class="en-US">Sharded database name</span>
	 *                <span class="zh-CN">分片数据库名</span>
	 * @throws SQLException <span class="en-US">An error occurred while obtaining the connection</span>
	 *                      <span class="zh-CN">获得连接过程中出错</span>
	 */
	JdbcConnection obtainConnection(final String catalog) throws SQLException {
		return this.obtainConnection(catalog, Connection.TRANSACTION_NONE);
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
		this.lock.lock();
		try {
			JdbcConnection connection;
			if (this.pooled) {
				this.waitCount.incrementAndGet();
				long beginTimestamp = DateTimeUtils.currentUTCTimeMillis();
				connection = this.pollConnection();
				while (connection == null) {
					if (this.waitCondition.await(this.schema.getConnectTimeout(), TimeUnit.SECONDS)) {
						connection = this.pollConnection();
					}
					if ((this.schema.getConnectTimeout() * 1000L) <= (DateTimeUtils.currentUTCTimeMillis() - beginTimestamp)) {
						//  Obtain connection timeout
						throw new MultilingualSQLException(0x00DB00000024L);
					}
				}
				if (StringUtils.notBlank(catalog)) {
					this.initDatabase(connection, catalog);
					connection.setCatalog(catalog);
				}
			} else {
				connection = this.createConnection();
			}
			if (isolation != Connection.TRANSACTION_NONE) {
				connection.setAutoCommit(Boolean.FALSE);
				connection.setTransactionIsolation(isolation);
			}
			this.activeConnections.add(connection);
			return connection;
		} catch (InterruptedException e) {
			throw new MultilingualSQLException(0x00DB00000024L, e);
		} finally {
			if (this.pooled) {
				this.waitCount.decrementAndGet();
			}
			this.lock.unlock();
		}
	}

	/**
	 * <h3 class="en-US">Get the first connection from the connection queue</h3>
	 * <h3 class="zh-CN">获得连接队列中的第一个连接</h3>
	 *
	 * @return <span class="en-US">Obtained connection</span>
	 * <span class="zh-CN">获得的连接</span>
	 */
	private JdbcConnection pollConnection() {
		JdbcConnection connection = this.createdConnections.poll();
		if (this.schema.isTestOnBorrow() && this.invalidConnection(connection)) {
			this.destroyConnection(connection);
			connection = null;
		}
		return connection;
	}

	/**
	 * <h3 class="en-US">Destroy the current connection pool</h3>
	 * <h3 class="zh-CN">销毁当前数据库连接池</h3>
	 */
	void shutdownNow() {
		this.lock.lock();
		try {
			//  Close all activated connection
			this.activeConnections.forEach(this::destroyConnection);
			this.activeConnections.clear();
			this.closePool();
		} finally {
			this.lock.unlock();
		}
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
	void closeConnection(@Nonnull final JdbcConnection connection) throws SQLException {
		this.lock.lock();
		try {
			this.activeConnections.remove(connection);
			boolean destroy = Boolean.FALSE;
			if (!this.pooled || connection.isClosed()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Closed_Destroy_Connection");
				}
				destroy = Boolean.TRUE;
			}
			if (this.schema.getMinConnections() <= this.queueCount()
					|| this.schema.getMaxConnections() <= this.poolCount()) {
				//  The connection pool is full or the connections count is greater than the max connections
				destroy = Boolean.TRUE;
			}

			if (!destroy && this.schema.isTestOnReturn() && this.invalidConnection(connection)) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Invalid_Destroy_Connection");
				}
				destroy = Boolean.TRUE;
			}

			if (destroy) {
				this.destroyConnection(connection);
			} else {
				connection.reset();
				this.createdConnections.add(connection);
				this.waitCondition.signal();
			}
		} finally {
			this.lock.unlock();
		}
	}

	/**
	 * <h3 class="en-US">Check the current connections count is greater or equal to the maximum connections</h3>
	 * <h3 class="zh-CN">检查当前连接数是否超过最大连接数</h3>
	 *
	 * @return <span class="en-US">Check result</span>
	 * <span class="zh-CN">检查结果</span>
	 */
	private boolean limitConnections() {
		return (this.activeCount() + this.poolCount()) >= this.schema.getMaxConnections();
	}

	/**
	 * <h3 class="en-US">Add a connection to the connection pool</h3>
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
		this.lock.lock();
		try {
			if (this.needConnections()) {
				this.createdConnections.add(connection);
				destroy = Boolean.FALSE;
				this.waitCondition.signal();
			}
		} finally {
			this.lock.unlock();
		}
		if (destroy) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Pool_Full_Destroy_Connection");
			}
			this.destroyConnection(connection);
		}
	}

	/**
	 * <h3 class="en-US">Check current connections count in the connection pool is less than the pool maximum connections</h3>
	 * <h3 class="zh-CN">检查当前连接池中的连接数是否小于连接池最大连接数</h3>
	 *
	 * @return <span class="en-US">Check result</span>
	 * <span class="zh-CN">检查结果</span>
	 */
	private boolean needConnections() {
		return this.pooled ? (this.queueCount() < this.schema.getMinConnections()) : Boolean.FALSE;
	}

	/**
	 * <h3 class="en-US">Check whether the number of connections in the database connection pool meets the configuration requirements</h3>
	 * <h3 class="zh-CN">检查数据库连接池中的连接数是否满足配置需求</h3>
	 */
	void createConnections() {
		if (!this.createRunning.compareAndSet(Boolean.FALSE, Boolean.TRUE) || !this.pooled) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Create_Connection_Running");
			}
			//  Reset flag to not running
			this.createRunning.set(Boolean.FALSE);
			return;
		}

		this.lock.lock();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Create_Connection_Begin_Debug");
			}
			if (this.limitConnections()) {
				LOGGER.debug("Create_Connection_Full");
				this.createRunning.set(Boolean.FALSE);
				return;
			}

			this.createdConnections.removeIf(connection -> {
				if (this.invalidConnection(connection)) {
					this.destroyConnection(connection);
					return Boolean.TRUE;
				}
				return Boolean.FALSE;
			});
			while (this.needConnections()) {
				try {
					this.addConnection(this.createConnection());
				} catch (SQLException e) {
					LOGGER.error("Create_Connection_Error");
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("", e);
					}
					break;
				}
			}
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Stack_Message_Error", e);
			}
		} finally {
			this.createRunning.set(Boolean.FALSE);
			this.lock.unlock();
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Create_Connection_End_Debug");
		}
	}

	private void initDatabase(@Nonnull final JdbcConnection connection, @Nonnull final String catalog) throws SQLException {
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
	}

	void scanExists() throws SQLException {
		try (JdbcConnection connection = this.obtainConnection(Globals.DEFAULT_VALUE_STRING);
		     ResultSet resultSet = connection.getMetaData().getCatalogs()) {
			while (resultSet.next()) {
				String databaseName = resultSet.getString("TABLE_CAT");
				this.databaseNames.add(databaseName);
			}
		}
		if (this.databaseNames.isEmpty()) {
			this.databaseNames.add(Globals.DEFAULT_VALUE_STRING);
		}
	}
}
