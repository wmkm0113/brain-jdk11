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
import jakarta.persistence.LockModeType;
import org.jetbrains.annotations.NotNull;
import org.nervousync.brain.command.GeneratedCommand;
import org.nervousync.brain.commons.BrainCommons;
import org.nervousync.brain.configs.schema.impl.JdbcSchemaConfig;
import org.nervousync.brain.configs.server.ServerInfo;
import org.nervousync.brain.configs.sharding.ShardingConfig;
import org.nervousync.brain.configs.transactional.TransactionalConfig;
import org.nervousync.brain.defines.ColumnDefine;
import org.nervousync.brain.defines.IndexDefine;
import org.nervousync.brain.defines.TableDefine;
import org.nervousync.brain.dialects.DialectFactory;
import org.nervousync.brain.dialects.jdbc.JdbcDialect;
import org.nervousync.brain.enumerations.ddl.DDLType;
import org.nervousync.brain.enumerations.ddl.DropOption;
import org.nervousync.brain.enumerations.sharding.ShardingType;
import org.nervousync.brain.exceptions.data.DropException;
import org.nervousync.brain.exceptions.data.InsertException;
import org.nervousync.brain.exceptions.data.RetrieveException;
import org.nervousync.brain.exceptions.data.UpdateException;
import org.nervousync.brain.exceptions.sql.MultilingualSQLException;
import org.nervousync.brain.query.QueryInfo;
import org.nervousync.brain.query.condition.Condition;
import org.nervousync.brain.schemas.BaseSchema;
import org.nervousync.commons.Globals;
import org.nervousync.utils.*;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <h2 class="en-US">JDBC data source implementation class</h2>
 * <h2 class="zh-CN">JDBC数据源实现类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Feb 18, 2019 10:38:52 $
 */
public final class JdbcSchema extends BaseSchema<JdbcDialect> implements JdbcSchemaMBean {

	/**
	 * <span class="en-US">JDBC connection url string</span>
	 * <span class="zh-CN">JDBC连接字符串</span>
	 */
	private final String jdbcUrl;
	/**
	 * <span class="en-US">Data source allows connection pooling</span>
	 * <span class="zh-CN">数据源允许连接池</span>
	 */
	private boolean pooled;
	/**
	 * <span class="en-US">Maximum number of connection retries</span>
	 * <span class="zh-CN">连接最大重试次数</span>
	 */
	int retryCount;
	/**
	 * <span class="en-US">Retry count if obtains connection has error</span>
	 * <span class="zh-CN">获取连接的重试次数</span>
	 */
	long retryPeriod;
	/**
	 * <span class="en-US">Maximum size of prepared statement</span>
	 * <span class="zh-CN">查询分析器的最大缓存结果</span>
	 */
	int cachedLimitSize;
	/**
	 * <span class="en-US">Minimum connection limit</span>
	 * <span class="zh-CN">最小连接数</span>
	 */
	int minConnections;
	/**
	 * <span class="en-US">Maximum connection limit</span>
	 * <span class="zh-CN">最大连接数</span>
	 */
	int maxConnections;
	/**
	 * <span class="en-US">Check connection validate when obtains database connection</span>
	 * <span class="zh-CN">在获取连接时检查连接是否有效</span>
	 */
	boolean testOnBorrow;
	/**
	 * <span class="en-US">Check connection validate when return database connection</span>
	 * <span class="zh-CN">在归还连接时检查连接是否有效</span>
	 */
	boolean testOnReturn;
	/**
	 * <span class="en-US">Database main/writable server info</span>
	 * <span class="zh-CN">数据库主服务器（写入服务器）</span>
	 */
	private final ServerInfo serverInfo;
	/**
	 * <span class="en-US">Database secondary/readable server info list</span>
	 * <span class="zh-CN">数据库从服务器列表（只读服务器）</span>
	 */
	private final List<ServerInfo> serverList;
	/**
	 * <span class="en-US">Secondary/Readable server list index value</span>
	 * <span class="zh-CN">从服务器列表索引值</span>
	 */
	private final AtomicInteger serverIndex = new AtomicInteger(Globals.INITIALIZE_INT_VALUE);
	/**
	 * <span class="en-US">The interval between scheduled task executions</span>
	 * <span class="zh-CN">调度任务执行的间隔时间</span>
	 */
	private static final long SCHEDULE_PERIOD_TIME = 1000L;
	/**
	 * <span class="en-US">System scheduling task execution service</span>
	 * <span class="zh-CN">系统调度任务执行服务</span>
	 */
	private ScheduledExecutorService executorService = null;
	/**
	 * <span class="en-US">Database connection pools mapping</span>
	 * <span class="zh-CN">数据库连接池映射表</span>
	 */
	private final List<JdbcConnectionPool> registeredPools = new ArrayList<>();
	/**
	 * <span class="en-US">List of database connections used by the current thread</span>
	 * <span class="zh-CN">当前线程使用的数据库连接列表</span>
	 */
	private final ThreadLocal<List<JdbcConnection>> currentConnections = new ThreadLocal<>();

	/**
	 * <h3 class="en-US">Constructor method for JDBC data source implementation class</h3>
	 * <h3 class="zh-CN">JDBC数据源实现类的构造方法</h3>
	 *
	 * @param schemaConfig <span class="en-US">JDBC data source configure information</span>
	 *                     <span class="zh-CN">JDBC数据源配置信息</span>
	 * @throws SQLException <span class="en-US">Database server information not found or sharding configuration error</span>
	 *                      <span class="zh-CN">数据库服务器信息未找到或分片配置出错</span>
	 */
	public JdbcSchema(@NotNull final JdbcSchemaConfig schemaConfig) throws SQLException {
		super(schemaConfig, DialectFactory.retrieve(schemaConfig.getDialectName()).unwrap(JdbcDialect.class));
		this.pooled = schemaConfig.isPooled();
		this.jdbcUrl = schemaConfig.getJdbcUrl();
		this.cachedLimitSize = schemaConfig.getCachedLimitSize();
		this.retryCount = schemaConfig.getRetryCount();
		this.retryPeriod = schemaConfig.getRetryPeriod();
		if (this.pooled && this.dialect.isConnectionPool()) {
			this.minConnections = schemaConfig.getMinConnections();
			this.maxConnections = schemaConfig.getMaxConnections();
		} else {
			this.minConnections = Globals.DEFAULT_VALUE_INT;
			this.maxConnections = Globals.DEFAULT_VALUE_INT;
		}
		this.testOnBorrow = schemaConfig.isTestOnBorrow();
		this.testOnReturn = schemaConfig.isTestOnReturn();
		if (schemaConfig.isServerArray()) {
			List<ServerInfo> serverList = schemaConfig.getServerList();
			if (serverList == null || serverList.isEmpty()) {
				throw new SQLException("");
			}
			serverList.sort((o1, o2) -> Integer.compare(o2.getServerLevel(), o1.getServerLevel()));
			this.serverInfo = serverList.get(0);
			this.serverList = new ArrayList<>();
			for (int i = 1; i < serverList.size(); i++) {
				this.serverList.add(serverList.get(i));
			}
		} else {
			this.serverList = Collections.emptyList();
			this.serverInfo = null;
		}
		if (this.sharding && !this.jdbcUrl.contains("{shardingKey}")) {
			throw new MultilingualSQLException(0x00DB00000025L, this.jdbcUrl);
		}
	}

	/**
	 * <h3 class="en-US">Generate parameter information required for connection</h3>
	 * <h3 class="zh-CN">生成连接需要使用的参数信息</h3>
	 *
	 * @return <span class="en-US">Connect properties instance object</span>
	 * <span class="zh-CN">连接属性值</span>
	 */
	Properties properties() {
		return this.dialect.properties(this.trustStore, this.authentication);
	}

	/**
	 * <h3 class="en-US">Connection verification query command</h3>
	 * <h3 class="zh-CN">连接验证查询命令</h3>
	 *
	 * @return <span class="en-US">Connection verification query command</span>
	 * <span class="zh-CN">连接验证查询命令</span>
	 */
	String validationQuery() {
		return this.dialect.getValidationQuery();
	}

	@Override
	public int getRetryCount() {
		return this.retryCount;
	}

	@Override
	public long getRetryPeriod() {
		return this.retryPeriod;
	}

	@Override
	public void configCacheLimitSize(final int cacheLimitSize) {
		this.cachedLimitSize = cacheLimitSize;
	}

	@Override
	public int getCachedLimitSize() {
		return this.cachedLimitSize;
	}

	@Override
	public void configTest(final boolean testOnBorrow, final boolean testOnReturn) {
		this.testOnBorrow = testOnBorrow;
		this.testOnReturn = testOnReturn;
	}

	@Override
	public void configPool(final boolean pooled, final int minConnections, final int maxConnections) {
		this.registeredPools.forEach(connectionPool -> connectionPool.configPooled(pooled));
		this.pooled = pooled;
		if (this.pooled) {
			this.minConnections = minConnections;
			this.maxConnections = maxConnections;
		} else {
			this.minConnections = Globals.DEFAULT_VALUE_INT;
			this.maxConnections = Globals.DEFAULT_VALUE_INT;
		}
	}

	@Override
	public boolean isPooled() {
		return this.pooled;
	}

	@Override
	public int getPoolCount() {
		final AtomicInteger count = new AtomicInteger(Globals.INITIALIZE_INT_VALUE);
		this.registeredPools.forEach(connectionPool -> count.addAndGet(connectionPool.poolCount()));
		return count.get();
	}

	@Override
	public int getActiveCount() {
		final AtomicInteger count = new AtomicInteger(Globals.INITIALIZE_INT_VALUE);
		this.registeredPools.forEach(connectionPool -> count.addAndGet(connectionPool.activeCount()));
		return count.get();
	}

	@Override
	public int getWaitCount() {
		final AtomicInteger count = new AtomicInteger(Globals.INITIALIZE_INT_VALUE);
		this.registeredPools.forEach(connectionPool -> count.addAndGet(connectionPool.waitCount()));
		return count.get();
	}

	@Override
	public int getMinConnections() {
		return this.minConnections;
	}

	@Override
	public int getMaxConnections() {
		return this.maxConnections;
	}

	@Override
	public void configRetry(final int retryCount, final long retryPeriod) {
		this.retryCount = retryCount;
		this.retryPeriod = retryPeriod;
	}

	@Override
	public boolean isTestOnBorrow() {
		return this.testOnBorrow;
	}

	@Override
	public boolean isTestOnReturn() {
		return this.testOnReturn;
	}

	@Override
	public String getJdbcUrl() {
		return this.jdbcUrl;
	}

	@Override
	public void initialize() {
		if (this.initialized || this.sharding) {
			return;
		}

		try {
			this.initSharding(this.shardingDefault);
			if (this.pooled) {
				this.executorService = Executors.newSingleThreadScheduledExecutor();
				this.executorService.scheduleWithFixedDelay(
						() -> this.registeredPools.forEach(JdbcConnectionPool::createConnections),
						SCHEDULE_PERIOD_TIME, SCHEDULE_PERIOD_TIME, TimeUnit.MILLISECONDS);
			}
			this.initialized = Boolean.TRUE;
		} catch (SQLException e) {
			this.logger.error("Initialize_Schema_Error");
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Stack_Message_Error", e);
			}
		}
	}

	int identifyCode(final ServerInfo serverInfo, final String shardingKey) throws SQLException {
		if (serverInfo == null) {
			return shardingKey.hashCode();
		}
		String serverAddress = serverInfo.info();
		if (StringUtils.isEmpty(serverAddress)) {
			throw new MultilingualSQLException(0x00DB00000026L);
		}
		return (serverAddress + "_" + shardingKey).hashCode();
	}

	/**
	 * <h3 class="en-US">Initialize sharding connections</h3>
	 * <h3 class="zh-CN">初始化分片连接</h3>
	 *
	 * @param shardingKey <span class="en-US">Database sharding value</span>
	 *                    <span class="zh-CN">数据库分片值</span>
	 */
	@Override
	protected void initSharding(final String shardingKey) throws SQLException {
		if (this.serverList.isEmpty()) {
			int identifyCode = this.identifyCode(null, shardingKey);
			if (this.registeredPools.stream()
					.noneMatch(connectionPool -> connectionPool.getIdentifyCode() == identifyCode)) {
				this.registeredPools.add(new JdbcConnectionPool(this, this.pooled, null, shardingKey));
			}
		} else {
			for (ServerInfo serverInfo : this.serverList) {
				if (!this.databaseNames(serverInfo).contains(shardingKey)) {
					int identifyCode = this.identifyCode(serverInfo, this.shardingDefault);
					for (JdbcConnectionPool connectionPool : this.registeredPools) {
						if (connectionPool.getIdentifyCode() == identifyCode) {
							try (Connection connection = connectionPool.obtainConnection();
							     Statement statement = connection.createStatement()) {
								statement.execute(this.dialect.createDatabase(shardingKey));
							}
						}
					}
				}
				int identifyCode = this.identifyCode(serverInfo, shardingKey);
				if (this.registeredPools.stream()
						.noneMatch(connectionPool -> connectionPool.getIdentifyCode() == identifyCode)) {
					this.registeredPools.add(new JdbcConnectionPool(this, this.pooled, serverInfo, shardingKey));
				}
			}
		}
	}

	@Override
	public void close() {
		this.executorService.shutdown();
		this.registeredPools.forEach(JdbcConnectionPool::close);
		this.registeredPools.clear();
		this.executorService = null;
		this.initialized = Boolean.FALSE;
	}

	/**
	 * <h3 class="en-US">Generate JDBC connection url string</h3>
	 * <h3 class="zh-CN">生成JDBC连接字符串</h3>
	 *
	 * @param serverInfo  <span class="en-US">Server information</span>
	 *                    <span class="zh-CN">服务器信息</span>
	 * @param shardingKey <span class="en-US">Database sharding value</span>
	 *                    <span class="zh-CN">数据库分片值</span>
	 * @return <span class="en-US">JDBC connection url string</span>
	 * <span class="zh-CN">JDBC连接字符串</span>
	 */
	String shardingUrl(final ServerInfo serverInfo, final String shardingKey) throws SQLException {
		String shardingUrl = this.jdbcUrl;
		if (serverInfo != null) {
			String serverAddress = serverInfo.info();
			if (StringUtils.isEmpty(serverAddress)) {
				throw new MultilingualSQLException(0x00DB00000026L);
			}
			shardingUrl = StringUtils.replace(shardingUrl, "{serverAddress}", serverAddress);
		}

		if (this.sharding) {
			if (StringUtils.isEmpty(shardingKey)) {
				shardingUrl = StringUtils.replace(shardingUrl, "{shardingKey}", this.shardingDefault);
			} else {
				shardingUrl = StringUtils.replace(shardingUrl, "{shardingKey}", shardingKey);
			}
		}
		return shardingUrl;
	}

	/**
	 * <h3 class="en-US">Obtain server information. If it is not written to the server, use polling mode to obtain server information.</h3>
	 * <h3 class="zh-CN">获取服务器信息，如果非写入服务器，使用轮询模式获取服务器信息</h3>
	 *
	 * @param forUpdate <span class="en-US">Obtain main server flag</span>
	 *                  <span class="zh-CN">获取主服务器标识</span>
	 * @return <span class="en-US">Server information</span>
	 * <span class="zh-CN">服务器信息</span>
	 */
	private ServerInfo currentServer(final boolean forUpdate) throws SQLException {
		if (forUpdate || this.serverList.isEmpty()) {
			return this.serverInfo;
		}
		ServerInfo serverInfo = this.serverList.get(this.serverIndex.getAndIncrement());
		if (this.serverIndex.get() >= this.serverList.size()) {
			this.serverIndex.set(Globals.INITIALIZE_INT_VALUE);
		}
		if (serverInfo == null) {
			throw new MultilingualSQLException(0x00DB00000026L);
		}
		return serverInfo;
	}

	private JdbcConnectionPool connectionPool(final ServerInfo serverInfo) throws SQLException {
		return this.connectionPool(serverInfo, this.shardingDefault);
	}

	private JdbcConnectionPool connectionPool(final ServerInfo serverInfo, final String shardingKey) throws SQLException {
		int identifyCode = this.identifyCode(serverInfo, shardingKey);
		return this.registeredPools
				.stream()
				.filter(connectionPool -> connectionPool.getIdentifyCode() == identifyCode)
				.findFirst()
				.orElseThrow(() -> new MultilingualSQLException(0x00DB00000027L));
	}

	@Override
	public void beginTransactional() {
		if (this.currentConnections.get() == null) {
			this.currentConnections.set(new ArrayList<>());
		}
	}

	@Override
	public void rollback(final Exception e) throws Exception {
		TransactionalConfig transactionalConfig = this.txConfig.get();
		if (transactionalConfig != null && transactionalConfig.getIsolation() != Connection.TRANSACTION_NONE
				&& transactionalConfig.rollback(e)) {
			for (Connection connection : this.currentConnections.get()) {
				connection.rollback();
			}
		}
	}

	@Override
	public void commit() throws Exception {
		if (this.txConfig.get() != null && this.txConfig.get().getIsolation() != Connection.TRANSACTION_NONE) {
			for (Connection connection : this.currentConnections.get()) {
				connection.commit();
			}
		}
	}

	@Override
	public void truncateTables() throws Exception {
		for (JdbcConnectionPool connectionPool : this.registeredPools) {
			try (Connection connection = connectionPool.obtainConnection();
			     Statement statement = connection.createStatement()) {
				for (String tableName : this.tableNames(connection, Globals.DEFAULT_VALUE_STRING)) {
					statement.execute(this.dialect.truncateTable(tableName));
				}
			}
		}
	}

	@Override
	public void truncateTable(@Nonnull final TableDefine tableDefine) throws Exception {
		ShardingConfig shardingConfig = this.shardingConfigs.get(tableDefine.getTableName());
		if (shardingConfig == null) {
			String sqlCmd = this.dialect.truncateTable(tableDefine.getTableName());
			if (StringUtils.isEmpty(sqlCmd)) {
				return;
			}
			if (this.serverList.isEmpty()) {
				try (Connection connection = this.connectionPool(null).obtainConnection();
				     Statement statement = connection.createStatement()) {
					statement.execute(sqlCmd);
				}
			} else {
				for (ServerInfo serverInfo : this.serverList) {
					try (Connection connection = this.connectionPool(serverInfo).obtainConnection();
					     Statement statement = connection.createStatement()) {
						statement.execute(sqlCmd);
					}
				}
			}
		} else {
			if (this.serverList.isEmpty()) {
				for (String shardingDatabase : this.databaseNames(null)) {
					try (Connection connection = this.connectionPool(null, shardingDatabase).obtainConnection();
					     Statement statement = connection.createStatement()) {
						for (String tableName : this.tableNames(connection, tableDefine.getTableName())) {
							String sqlCmd = this.dialect.truncateTable(tableName);
							if (StringUtils.notBlank(sqlCmd)) {
								statement.execute(sqlCmd);
							}
						}
					}
				}
			} else {
				for (ServerInfo serverInfo : this.serverList) {
					for (String shardingDatabase : this.databaseNames(serverInfo)) {
						try (Connection connection = this.connectionPool(serverInfo, shardingDatabase).obtainConnection();
						     Statement statement = connection.createStatement()) {
							for (String tableName : this.tableNames(connection, tableDefine.getTableName())) {
								String sqlCmd = this.dialect.truncateTable(tableName);
								if (StringUtils.notBlank(sqlCmd)) {
									statement.execute(sqlCmd);
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void dropTables(final DropOption dropOption) throws Exception {
		for (JdbcConnectionPool connectionPool : this.registeredPools) {
			try (Connection connection = connectionPool.obtainConnection();
			     Statement statement = connection.createStatement()) {
				for (String tableName : this.tableNames(connection, Globals.DEFAULT_VALUE_STRING)) {
					statement.execute(this.dialect.dropTableCommand(tableName, dropOption));
				}
			}
		}
	}

	@Override
	public void dropTable(@Nonnull final TableDefine tableDefine, @Nonnull final DropOption dropOption)
			throws Exception {
		ShardingConfig shardingConfig = this.shardingConfigs.get(tableDefine.getTableName());
		if (shardingConfig == null) {
			if (this.serverList.isEmpty()) {
				try (Connection connection = this.connectionPool(null).obtainConnection();
				     Statement statement = connection.createStatement()) {
					String tableName = tableDefine.getTableName();
					for (IndexDefine indexDefine : tableDefine.getIndexDefines()) {
						statement.execute(this.dialect.dropIndexCommand(indexDefine.getIndexName(), tableName));
					}
					statement.execute(this.dialect.dropTableCommand(tableName, dropOption));
				}
			} else {
				for (ServerInfo serverInfo : this.serverList) {
					try (Connection connection = this.connectionPool(serverInfo).obtainConnection();
					     Statement statement = connection.createStatement()) {
						String tableName = tableDefine.getTableName();
						for (IndexDefine indexDefine : tableDefine.getIndexDefines()) {
							statement.execute(this.dialect.dropIndexCommand(indexDefine.getIndexName(), tableName));
						}
						statement.execute(this.dialect.dropTableCommand(tableName, dropOption));
					}
				}
			}
		} else {
			if (this.serverList.isEmpty()) {
				for (String shardingDatabase : this.databaseNames(null)) {
					try (Connection connection = this.connectionPool(null, shardingDatabase).obtainConnection();
					     Statement statement = connection.createStatement()) {
						for (String tableName
								: this.tableNames(connection, super.shardingTemplate(tableDefine.getTableName()))) {
							for (IndexDefine indexDefine : tableDefine.getIndexDefines()) {
								statement.execute(this.dialect.dropIndexCommand(indexDefine.getIndexName(), tableName));
							}
							statement.execute(this.dialect.dropTableCommand(tableName, dropOption));
						}
					}
				}

			} else {
				for (ServerInfo serverInfo : this.serverList) {
					for (String shardingDatabase : this.databaseNames(serverInfo)) {
						try (Connection connection = this.connectionPool(serverInfo, shardingDatabase).obtainConnection();
						     Statement statement = connection.createStatement()) {
							for (String tableName
									: this.tableNames(connection, super.shardingTemplate(tableDefine.getTableName()))) {
								for (IndexDefine indexDefine : tableDefine.getIndexDefines()) {
									statement.execute(this.dialect.dropIndexCommand(indexDefine.getIndexName(), tableName));
								}
								statement.execute(this.dialect.dropTableCommand(tableName, dropOption));
							}
						}
					}
				}
			}
		}
	}

	@Override
	public boolean lockRecord(@NotNull final TableDefine tableDefine,
	                          @NotNull final Map<String, Object> filterMap) throws Exception {
		return !this.retrieve(tableDefine, Globals.DEFAULT_VALUE_STRING, filterMap, Boolean.TRUE).isEmpty();
	}

	@Override
	public Map<String, Object> insert(@Nonnull final TableDefine tableDefine,
	                                  @Nonnull final Map<String, Object> dataMap)
			throws SQLException, InsertException {
		String shardingTable = this.shardingTable(tableDefine.getTableName(), dataMap);
		GeneratedCommand sqlCommand = this.dialect.insertCommand(tableDefine, shardingTable, dataMap);
		PreparedStatement statement = null;
		try (Connection connection =
				     this.obtainConnection(Boolean.TRUE, this.shardingDatabase(tableDefine.getTableName(), dataMap))) {
			this.initTable(connection, DDLType.SYNCHRONIZE, tableDefine, shardingTable);
			statement = connection.prepareStatement(sqlCommand.getCommand(), Statement.RETURN_GENERATED_KEYS);
			this.configTimeout(statement);
			int index = Globals.INITIALIZE_INT_VALUE;
			for (Object object : sqlCommand.getParameters()) {
				statement.setObject(index + 1, object);
				index++;
			}
			if (statement.executeUpdate() == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next()) {
					return this.parseResultSet(tableDefine, resultSet, this.dialect);
				}
				return Map.of();
			}
			throw new InsertException(0x00DB00000038L, tableDefine.getTableName(),
					StringUtils.objectToString(dataMap, StringUtils.StringType.JSON, Boolean.TRUE));
		} catch (SQLException | InsertException e) {
			if (e instanceof InsertException) {
				throw e;
			}
			throw new InsertException(0x00DB00000039L, e, tableDefine.getTableName(),
					StringUtils.objectToString(dataMap, StringUtils.StringType.JSON, Boolean.TRUE));
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}

	@Override
	public Map<String, Object> retrieve(@Nonnull final TableDefine tableDefine, final String columns,
	                                    @Nonnull final Map<String, Object> filterMap, final boolean forUpdate)
			throws SQLException, RetrieveException {
		String queryColumns = StringUtils.isEmpty(columns) ? super.queryColumns(tableDefine, forUpdate) : columns;
		GeneratedCommand sqlCommand =
				this.dialect.retrieveCommand(this.shardingTable(tableDefine.getTableName(), filterMap),
						queryColumns, filterMap, forUpdate, tableDefine.getLockOption());
		try (Connection connection =
				     this.obtainConnection(forUpdate, this.shardingDatabase(tableDefine.getTableName(), filterMap));
		     PreparedStatement statement = connection.prepareStatement(sqlCommand.getCommand())) {
			this.configTimeout(statement);
			int index = Globals.INITIALIZE_INT_VALUE;
			for (Object object : sqlCommand.getParameters()) {
				statement.setObject(index + 1, object);
				index++;
			}
			ResultSet resultSet = statement.executeQuery();
			Map<String, Object> resultMap = new HashMap<>();
			while (resultSet.next()) {
				if (!resultMap.isEmpty()) {
					throw new RetrieveException(0x00DB00000028L, tableDefine.getTableName(),
							StringUtils.objectToString(filterMap, StringUtils.StringType.JSON, Boolean.TRUE));
				}
				resultMap.putAll(this.parseResultSet(tableDefine, resultSet, this.dialect));
			}
			return resultMap;
		}
	}

	@Override
	public int update(@Nonnull final TableDefine tableDefine, @Nonnull final Map<String, Object> dataMap,
	                  @Nonnull final Map<String, Object> filterMap) throws SQLException, UpdateException {
		String shardingDatabase = this.shardingDatabase(tableDefine.getTableName(), filterMap);
		GeneratedCommand sqlCommand =
				this.dialect.updateCommand(tableDefine, this.shardingTable(tableDefine.getTableName(), filterMap),
						dataMap, filterMap);
		try (Connection connection = this.obtainConnection(Boolean.TRUE, shardingDatabase);
		     PreparedStatement statement = connection.prepareStatement(sqlCommand.getCommand())) {
			this.configTimeout(statement);
			int index = 1;
			for (Object object : sqlCommand.getParameters()) {
				statement.setObject(index, object);
				index++;
			}
			return statement.executeUpdate();
		} catch (SQLException e) {
			throw new UpdateException(0x00DB00000040L, e, tableDefine.getTableName(),
					StringUtils.objectToString(dataMap, StringUtils.StringType.JSON, Boolean.TRUE),
					StringUtils.objectToString(filterMap, StringUtils.StringType.JSON, Boolean.TRUE));
		}
	}

	@Override
	public int delete(@Nonnull final TableDefine tableDefine, @Nonnull final Map<String, Object> filterMap)
			throws SQLException, DropException {
		String shardingDatabase = this.shardingDatabase(tableDefine.getTableName(), filterMap);
		GeneratedCommand sqlCommand =
				this.dialect.deleteCommand(this.shardingTable(tableDefine.getTableName(), filterMap), filterMap);
		try (Connection connection = this.obtainConnection(Boolean.TRUE, shardingDatabase);
		     PreparedStatement statement = connection.prepareStatement(sqlCommand.getCommand())) {
			this.configTimeout(statement);
			int index = 1;
			for (Object object : sqlCommand.getParameters()) {
				statement.setObject(index, object);
				index++;
			}
			return statement.executeUpdate();
		} catch (SQLException e) {
			throw new DropException(0x00DB00000041L, e, tableDefine.getTableName(),
					StringUtils.objectToString(filterMap, StringUtils.StringType.JSON, Boolean.TRUE));
		}
	}

	@Override
	public List<Map<String, Object>> queryForUpdate(@Nonnull final TableDefine tableDefine,
	                                                final List<Condition> conditionList, final LockModeType lockOption)
			throws SQLException {
		StringBuilder stringBuilder = new StringBuilder();
		for (ColumnDefine columnDefine : tableDefine.getColumnDefines()) {
			stringBuilder.append(BrainCommons.DEFAULT_SPLIT_CHARACTER).append(columnDefine.getColumnName());
		}
		if (stringBuilder.length() == 0) {
			throw new MultilingualSQLException(0x00DB00000011L);
		}
		String tableName = this.shardingTable(tableDefine.getTableName(), conditionList);
		return this.executeQuery(tableDefine,
				this.shardingDatabase(tableDefine.getTableName(), conditionList),
				this.dialect.queryCommand(tableName,
						stringBuilder.substring(BrainCommons.DEFAULT_SPLIT_CHARACTER.length()),
						conditionList, LockModeType.NONE.equals(lockOption) ? tableDefine.getLockOption() : lockOption),
				Boolean.TRUE);
	}

	@Override
	public Long queryTotal(@NotNull final TableDefine tableDefine, final QueryInfo queryInfo) throws Exception {
		String shardingDatabase = this.shardingDatabase(tableDefine.getTableName(), queryInfo.getConditionList());
		String tableName = this.shardingTable(tableDefine.getTableName(), queryInfo.getConditionList());
		if (StringUtils.isEmpty(tableName)) {
			tableName = tableDefine.getTableName();
		}
		GeneratedCommand sqlCommand =
				this.dialect.queryTotalCommand(tableName, queryInfo.getQueryJoins(), queryInfo.getConditionList());
		try (Connection connection = this.obtainConnection(Boolean.TRUE, shardingDatabase);
		     PreparedStatement statement = connection.prepareStatement(sqlCommand.getCommand())) {
			this.configTimeout(statement);
			int index = 1;
			for (Object object : sqlCommand.getParameters()) {
				statement.setObject(index, object);
				index++;
			}

			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return resultSet.getLong(1);
			}
		}
		return Globals.DEFAULT_VALUE_LONG;
	}

	@Override
	public void clearTransactional() throws SQLException {
		if (this.txConfig.get() != null
				&& this.txConfig.get().getIsolation() != Connection.TRANSACTION_NONE) {
			for (JdbcConnection connection : this.currentConnections.get()) {
				connection.forceClose();
			}
			this.currentConnections.remove();
		}
	}

	@Override
	public List<Map<String, Object>> query(@NotNull final TableDefine tableDefine,
	                                       @Nonnull final QueryInfo queryInfo) throws Exception {
		return this.executeQuery(tableDefine,
				this.shardingDatabase(queryInfo.getTableName(), queryInfo.getConditionList()),
				this.dialect.queryCommand(queryInfo), Boolean.FALSE);
	}

	@Override
	protected void initTable(@Nonnull final DDLType ddlType, @Nonnull final TableDefine tableDefine,
	                         final String shardingDatabase) throws Exception {
		if (this.serverList.isEmpty()) {
			try (Connection connection = this.connectionPool(null, shardingDatabase).obtainConnection()) {
				this.initTable(connection, ddlType, tableDefine);
			}
			return;
		}
		for (ServerInfo serverInfo : this.serverList) {
			try (Connection connection = this.connectionPool(serverInfo, shardingDatabase).obtainConnection()) {
				this.initTable(connection, ddlType, tableDefine);
			}
		}
	}

	/**
	 * <h3 class="en-US">Initialize data table</h3>
	 * <h3 class="zh-CN">初始化数据表</h3>
	 *
	 * @param connection  <span class="en-US">Database connection</span>
	 *                    <span class="zh-CN">数据库连接</span>
	 * @param ddlType     <span class="en-US">Enumeration value of DDL operate</span>
	 *                    <span class="zh-CN">操作类型枚举值</span>
	 * @param tableDefine <span class="en-US">Table define information</span>
	 *                    <span class="zh-CN">数据表定义信息</span>
	 * @throws SQLException <span class="en-US">An error occurred during execution</span>
	 *                      <span class="zh-CN">执行过程中出错</span>
	 */
	private void initTable(@Nonnull final Connection connection, @Nonnull final DDLType ddlType,
	                       @Nonnull final TableDefine tableDefine) throws SQLException {
		List<String> shardingNames = this.tableNames(connection, tableDefine.getTableName());
		if (shardingNames.isEmpty()) {
			this.initTable(connection, ddlType, tableDefine, this.shardingTable(tableDefine.getTableName(), Map.of()));
		} else {
			for (String tableName : shardingNames) {
				this.initTable(connection, ddlType, tableDefine, tableName);
			}
		}
	}

	/**
	 * <h3 class="en-US">Initialize data table</h3>
	 * <h3 class="zh-CN">初始化数据表</h3>
	 *
	 * @param connection  <span class="en-US">Database connection</span>
	 *                    <span class="zh-CN">数据库连接</span>
	 * @param ddlType     <span class="en-US">Enumeration value of DDL operate</span>
	 *                    <span class="zh-CN">操作类型枚举值</span>
	 * @param tableDefine <span class="en-US">Table define information</span>
	 *                    <span class="zh-CN">数据表定义信息</span>
	 * @param tableName   <span class="en-US">Data table name</span>
	 *                    <span class="zh-CN">数据表名</span>
	 * @throws SQLException <span class="en-US">An error occurred during execution</span>
	 *                      <span class="zh-CN">执行过程中出错</span>
	 */
	private void initTable(@Nonnull final Connection connection, @Nonnull final DDLType ddlType,
	                       @Nonnull final TableDefine tableDefine, final String tableName) throws SQLException {
		DatabaseMetaData databaseMetaData = connection.getMetaData();
		String tableNameCase = this.dialect.nameCase(tableName);
		if (databaseMetaData.getTables(connection.getCatalog(), null,
				tableNameCase, new String[]{"TABLE"}).next()) {
			ResultSet primaryKeyResultSet =
					databaseMetaData.getPrimaryKeys(connection.getCatalog(), null, tableNameCase);
			List<String> primaryKeys = new ArrayList<>();
			while (primaryKeyResultSet.next()) {
				primaryKeys.add(primaryKeyResultSet.getString("COLUMN_NAME"));
			}

			List<String> uniqueKeys = new ArrayList<>();
			ResultSet indexResultSet =
					databaseMetaData.getIndexInfo(connection.getCatalog(), null, tableNameCase,
							Boolean.TRUE, Boolean.TRUE);
			while (indexResultSet.next()) {
				uniqueKeys.add(indexResultSet.getString("COLUMN_NAME"));
			}

			ResultSet columnResultSet =
					databaseMetaData.getColumns(connection.getCatalog(), connection.getSchema(),
							tableNameCase, null);
			List<ColumnDefine> existColumns = new ArrayList<>();
			while (columnResultSet.next()) {
				existColumns.add(ColumnDefine.newInstance(columnResultSet, this.dialect, primaryKeys, uniqueKeys));
			}

			if (DDLType.VALIDATE.equals(ddlType)) {
				tableDefine.validate(existColumns);
			} else if (DDLType.SYNCHRONIZE.equals(ddlType)) {
				List<String> sqlCmdList = this.dialect.alterTableCommand(tableDefine, tableName, existColumns);
				if (!sqlCmdList.isEmpty()) {
					try (Statement statement = connection.createStatement()) {
						for (String alterCmd : sqlCmdList) {
							if (StringUtils.notBlank(alterCmd)) {
								statement.execute(alterCmd);
							}
						}
					}
				}
			}
		} else {
			if (DDLType.CREATE.equals(ddlType) || DDLType.CREATE_DROP.equals(ddlType)
					|| DDLType.CREATE_TRUNCATE.equals(ddlType) || DDLType.SYNCHRONIZE.equals(ddlType)) {
				String sqlCmd = this.dialect.createTableCommand(tableDefine, tableName);
				if (StringUtils.isEmpty(sqlCmd)) {
					throw new MultilingualSQLException(0x00DB00000029L);
				}
				try (Statement statement = connection.createStatement()) {
					statement.execute(sqlCmd);
					for (String indexCmd : this.dialect.createIndexCommand(tableDefine, tableName)) {
						if (StringUtils.notBlank(indexCmd)) {
							statement.execute(indexCmd);
						}
					}
					if (StringUtils.notBlank(super.shardingTemplate(tableDefine.getTableName()))) {
						statement.execute(this.dialect.createShardingView(tableDefine,
								this.tableNames(connection, tableDefine.getTableName())));
					}
				}
			}
		}
	}

	/**
	 * <h3 class="en-US">Get all sharded data table name list based on the given data table name</h3>
	 * <h3 class="zh-CN">根据给定的数据表名获取所有分片数据表名列表</h3>
	 *
	 * @param connection <span class="en-US">Database connection</span>
	 *                   <span class="zh-CN">数据库连接</span>
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">分片数据表名列表</span>
	 * @return <span class="en-US">Sharded data table name list</span>
	 * <span class="zh-CN">数据表名列表</span>
	 * @throws SQLException <span class="en-US">An error occurred during execution</span>
	 *                      <span class="zh-CN">执行过程中出错</span>
	 */
	private List<String> tableNames(@Nonnull final Connection connection, final String tableName) throws SQLException {
		List<String> shardingNames = new ArrayList<>();
		ShardingConfig shardingConfig = this.shardingConfigs.get(tableName);
		DatabaseMetaData databaseMetaData = connection.getMetaData();
		ResultSet resultSet = databaseMetaData.getTables(connection.getCatalog(),
				"*", "*", new String[]{"TABLE"});
		while (resultSet.next()) {
			String shardingName = resultSet.getString("TABLE_NAME");
			if (StringUtils.isEmpty(tableName) || ObjectUtils.nullSafeEquals(shardingName, tableName)
					|| (shardingConfig != null && shardingConfig.matchKey(ShardingType.TABLE, shardingName))) {
				shardingNames.add(shardingName);
			}
		}
		return shardingNames;
	}

	/**
	 * <h3 class="en-US">Get all sharded database name list based on the given data table name</h3>
	 * <h3 class="zh-CN">根据给定的数据表名获取所有分片数据库名列表</h3>
	 *
	 * @param serverInfo <span class="en-US">Database server information</span>
	 *                   <span class="zh-CN">数据库服务器信息</span>
	 * @return <span class="en-US">Sharded database name list</span>
	 * <span class="zh-CN">数据库名列表</span>
	 * @throws SQLException <span class="en-US">An error occurred during execution</span>
	 *                      <span class="zh-CN">执行过程中出错</span>
	 */
	private List<String> databaseNames(final ServerInfo serverInfo) throws SQLException {
		List<String> shardingNames = new ArrayList<>();
		try (Connection connection = this.connectionPool(serverInfo).obtainConnection()) {
			ResultSet resultSet = connection.getMetaData().getCatalogs();
			while (resultSet.next()) {
				String databaseName = resultSet.getString("TABLE_CAT");
				if (ObjectUtils.nullSafeEquals(this.shardingDefault, databaseName)
						|| super.matchesDatabaseKey(databaseName)) {
					shardingNames.add(databaseName);
				}
			}
		}
		return shardingNames;
	}

	/**
	 * <h3 class="en-US">Obtain database connection</h3>
	 * <h3 class="zh-CN">获取数据库连接</h3>
	 *
	 * @param forUpdate        <span class="en-US">Retrieve result using for update record</span>
	 *                         <span class="zh-CN">检索结果用于更新记录</span>
	 * @param shardingDatabase <span class="en-US">Sharded database name</span>
	 *                         <span class="zh-CN">分片数据库名</span>
	 * @return <span class="en-US">Database connection</span>
	 * <span class="zh-CN">数据库连接</span>
	 * @throws SQLException <span class="en-US">An error occurred during execution</span>
	 *                      <span class="zh-CN">执行过程中出错</span>
	 */
	private JdbcConnection obtainConnection(final boolean forUpdate, final String shardingDatabase)
			throws SQLException {
		ServerInfo serverInfo = this.currentServer(forUpdate);
		int identifyCode = this.identifyCode(serverInfo, shardingDatabase);
		JdbcConnection connection = null;
		int isolation = (this.txConfig.get() != null) ? this.txConfig.get().getIsolation() : Connection.TRANSACTION_NONE;
		if (isolation != Connection.TRANSACTION_NONE) {
			connection = this.currentConnections.get().stream()
					.filter(jdbcConnection -> jdbcConnection.identifyCode() == identifyCode)
					.findFirst()
					.orElse(null);
		}

		if (connection == null) {
			connection = this.connectionPool(serverInfo, shardingDatabase).obtainConnection(isolation);
			if (isolation != Connection.TRANSACTION_NONE) {
				this.currentConnections.get().add(connection);
			}
		}
		return connection;
	}

	/**
	 * <h3 class="en-US">Configure query transaction timeout</h3>
	 * <h3 class="zh-CN">配置查询的事务超时时间</h3>
	 *
	 * @param preparedStatement <span class="en-US">Parameterized query instance object</span>
	 *                          <span class="zh-CN">参数化查询实例对象</span>
	 * @throws SQLException <span class="en-US">An error occurred during execution</span>
	 *                      <span class="zh-CN">执行过程中出错</span>
	 */
	private void configTimeout(@Nonnull final PreparedStatement preparedStatement) throws SQLException {
		TransactionalConfig txConfig = this.txConfig.get();
		if (txConfig != null && txConfig.getIsolation() != Connection.TRANSACTION_NONE && txConfig.getTimeout() > 0) {
			preparedStatement.setQueryTimeout(txConfig.getTimeout());
		}
	}

	/**
	 * <h3 class="en-US">Execute data query</h3>
	 * <h3 class="zh-CN">执行数据查询</h3>
	 *
	 * @param shardingDatabase <span class="en-US">Sharded database name</span>
	 *                         <span class="zh-CN">分片数据库名</span>
	 * @param sqlCommand       <span class="en-US">SQL command to execute</span>
	 *                         <span class="zh-CN">要执行的SQL命令</span>
	 * @return <span class="en-US">Query record list</span>
	 * <span class="zh-CN">查询到的记录列表</span>
	 * @throws SQLException <span class="en-US">An error occurred during execution</span>
	 *                      <span class="zh-CN">执行过程中出错</span>
	 */
	private List<Map<String, Object>> executeQuery(@Nonnull final TableDefine tableDefine,
	                                               @Nonnull final String shardingDatabase,
	                                               @Nonnull final GeneratedCommand sqlCommand,
	                                               final boolean forUpdate) throws SQLException {
		try (Connection connection = this.obtainConnection(forUpdate, shardingDatabase);
		     PreparedStatement statement = connection.prepareStatement(sqlCommand.getCommand())) {
			this.configTimeout(statement);
			int index = 1;
			for (Object object : sqlCommand.getParameters()) {
				statement.setObject(index, object);
				index++;
			}
			ResultSet resultSet = statement.executeQuery();
			List<Map<String, Object>> resultList = new ArrayList<>();
			while (resultSet.next()) {
				resultList.add(this.parseResultSet(tableDefine, resultSet, this.dialect));
			}
			return resultList;
		}
	}

	/**
	 * <h3 class="en-US">Parse the query result set into a data mapping table</h3>
	 * <h3 class="zh-CN">解析查询结果集为数据映射表</h3>
	 *
	 * @param resultSet   <span class="en-US">Query results to parse</span>
	 *                    <span class="zh-CN">要解析的查询结果</span>
	 * @param jdbcDialect <span class="en-US">JDBC dialect instance object</span>
	 *                    <span class="zh-CN">数据库方言实例对象</span>
	 * @return <span class="en-US">Converted data mapping table</span>
	 * <span class="zh-CN">数据映射表</span>
	 * @throws SQLException <span class="en-US">If an error occurs while parse the result set</span>
	 *                      <span class="zh-CN">如果解析时出错</span>
	 */
	private Map<String, Object> parseResultSet(@Nonnull final TableDefine tableDefine,
	                                           final ResultSet resultSet, final JdbcDialect jdbcDialect)
			throws SQLException {
		ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
		Map<String, Object> resultMap = new HashMap<>();
		int columnCount = resultSetMetaData.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			String columnLabel = jdbcDialect.nameCase(resultSetMetaData.getColumnLabel(i));
			ColumnDefine columnDefine = tableDefine.column(columnLabel);
			if (columnDefine == null) {
				continue;
			}
			switch (columnDefine.getJdbcType()) {
				case Types.BLOB:
				case Types.VARBINARY:
				case Types.LONGVARBINARY:
					resultMap.put(columnLabel, jdbcDialect.readBlob(resultSet, i));
					break;
				case Types.NCLOB:
				case Types.CLOB:
					resultMap.put(columnLabel, new String(jdbcDialect.readClob(resultSet, i)));
					break;
				case Types.NCHAR:
				case Types.NVARCHAR:
				case Types.LONGNVARCHAR:
					resultMap.put(columnLabel, resultSet.getNString(i));
					break;
				case Types.CHAR:
				case Types.VARCHAR:
				case Types.LONGVARCHAR:
					resultMap.put(columnLabel, resultSet.getString(i));
					break;
				case Types.DATE:
					long dateLong = resultSet.getDate(i).getTime();
					if (this.logger.isDebugEnabled()) {
						this.logger.debug("Read date long value: {}", dateLong);
					}
					resultMap.put(columnLabel, new Date(dateLong));
					break;
				case Types.TIME:
					long timeLong = resultSet.getTime(i).getTime();
					if (this.logger.isDebugEnabled()) {
						this.logger.debug("Read time long value: {}", timeLong);
					}
					resultMap.put(columnLabel, new Date(timeLong));
					break;
				case Types.TIMESTAMP:
					long timestamp = resultSet.getTimestamp(i).getTime();
					resultMap.put(columnLabel, new Date(timestamp));
					break;
				case Types.BIT:
				case Types.BOOLEAN:
					resultMap.put(columnLabel, resultSet.getBoolean(i));
					break;
				case Types.TINYINT:
					resultMap.put(columnLabel, resultSet.getByte(i));
					break;
				case Types.SMALLINT:
					resultMap.put(columnLabel, resultSet.getShort(i));
					break;
				case Types.INTEGER:
					resultMap.put(columnLabel, resultSet.getInt(i));
					break;
				case Types.BIGINT:
					resultMap.put(columnLabel, resultSet.getLong(i));
					break;
				case Types.REAL:
					resultMap.put(columnLabel, resultSet.getFloat(i));
					break;
				case Types.FLOAT:
				case Types.DOUBLE:
					resultMap.put(columnLabel, resultSet.getDouble(i));
					break;
				case Types.DECIMAL:
				case Types.NUMERIC:
					resultMap.put(columnLabel, resultSet.getBigDecimal(i));
					break;
				default:
					resultMap.put(columnLabel, resultSet.getObject(i));
					break;
			}
		}
		return resultMap;
	}
}
