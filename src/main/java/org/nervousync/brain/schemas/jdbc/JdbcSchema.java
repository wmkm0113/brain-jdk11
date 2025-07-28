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
import org.nervousync.brain.command.GeneratedCommand;
import org.nervousync.brain.commons.BrainCommons;
import org.nervousync.brain.configs.schema.impl.JdbcSchemaConfig;
import org.nervousync.brain.configs.server.ServerInfo;
import org.nervousync.brain.configs.sharding.StrategyConfig;
import org.nervousync.brain.configs.transactional.TransactionalConfig;
import org.nervousync.brain.defines.InitOption;
import org.nervousync.brain.defines.StrategyDefine;
import org.nervousync.brain.defines.TableDefine;
import org.nervousync.brain.dialects.DialectFactory;
import org.nervousync.brain.dialects.jdbc.JdbcDialect;
import org.nervousync.brain.enumerations.ddl.DDLType;
import org.nervousync.brain.enumerations.ddl.DropOption;
import org.nervousync.brain.exceptions.data.DropException;
import org.nervousync.brain.exceptions.data.InsertException;
import org.nervousync.brain.exceptions.data.RetrieveException;
import org.nervousync.brain.exceptions.data.UpdateException;
import org.nervousync.brain.exceptions.sql.MultilingualSQLException;
import org.nervousync.brain.query.PartialCollection;
import org.nervousync.brain.query.QueryInfo;
import org.nervousync.brain.query.condition.Condition;
import org.nervousync.brain.query.core.QueryFrom;
import org.nervousync.brain.query.core.QueryItem;
import org.nervousync.brain.query.data.QueryData;
import org.nervousync.brain.query.from.FromSubQuery;
import org.nervousync.brain.query.from.FromTable;
import org.nervousync.brain.query.item.SubQueryItem;
import org.nervousync.brain.query.sort.OrderBy;
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
import java.util.stream.Collectors;

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
	 * <span class="en-US">Data source support sharding</span>
	 * <span class="zh-CN">数据源是否支持分片</span>
	 */
	private final boolean sharding;
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
	 * <span class="en-US">Sharding configure information mapping</span>
	 * <span class="zh-CN">分片配置信息映射表</span>
	 */
	private final Hashtable<String, StrategyConfig> strategyConfigs = new Hashtable<>();
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
	private final Map<Integer, JdbcConnectionPool> registeredPools = new HashMap<>();
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
	 * @throws SQLException <span class="en-US">Database server information hasn't found or sharding configuration error</span>
	 *                      <span class="zh-CN">数据库服务器信息未找到或分片配置出错</span>
	 */
	public JdbcSchema(@Nonnull final JdbcSchemaConfig schemaConfig) throws SQLException {
		super(schemaConfig, DialectFactory.retrieve(schemaConfig.getDialectName()).unwrap(JdbcDialect.class));
		this.pooled = schemaConfig.isPooled();
		this.jdbcUrl = schemaConfig.getJdbcUrl();
		String defaultCatalog = Globals.DEFAULT_VALUE_STRING;
		if (schemaConfig.isSharding()) {
			if (this.dialect.isDatabaseSharding()) {
				if (!this.jdbcUrl.contains("{catalog}")) {
					throw new MultilingualSQLException(0x00DB00000025L, this.jdbcUrl);
				}
				defaultCatalog = schemaConfig.getShardingDefault();
			} else {
				this.logger.warn("");
			}
		}
		this.defaultCatalog = defaultCatalog;
		this.sharding = schemaConfig.isSharding() && this.dialect.isDatabaseSharding();
		this.databaseParameters = schemaConfig.getDatabaseParameters();
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
		List<ServerInfo> serverList = schemaConfig.getServerList();
		if (serverList == null || serverList.isEmpty()) {
			this.serverList = Collections.emptyList();
			this.serverInfo = null;
		} else {
			serverList.sort((o1, o2) -> Integer.compare(o2.getServerLevel(), o1.getServerLevel()));
			this.serverInfo = serverList.get(0);
			this.serverList = new ArrayList<>();
			for (int i = 1; i < serverList.size(); i++) {
				this.serverList.add(serverList.get(i));
			}
		}
		this.initPools();
	}

	/**
	 * <h3 class="en-US">Generate parameter information required for connection</h3>
	 * <h3 class="zh-CN">生成连接需要使用的参数信息</h3>
	 *
	 * @return <span class="en-US">Connect properties instance object</span>
	 * <span class="zh-CN">连接属性值</span>
	 */
	Properties properties(final ServerInfo serverInfo) {
		if (serverInfo == null) {
			return this.dialect.properties(this.trustStore, this.authentication);
		} else {
			return this.dialect.properties(
					(serverInfo.getTrustStore() == null) ? this.trustStore : serverInfo.getTrustStore(),
					(serverInfo.getAuthentication() == null) ? this.authentication : serverInfo.getAuthentication());
		}
	}

	/**
	 * <h3 class="en-US">Checks whether two tables are in the same database, according to the given query conditions</h3>
	 * <h3 class="zh-CN">根据给定的查询条件检查两个数据表是否在同一数据库中</h3>
	 *
	 * @param identifyCodes <span class="en-US">Data table identify code list</span>
	 *                      <span class="zh-CN">数据表识别代码列表</span>
	 * @param conditions    <span class="en-US">Query condition instance list</span>
	 *                      <span class="zh-CN">查询条件实例对象列表</span>
	 * @return <span class="en-US">Check result</span>
	 * <span class="zh-CN">检查结果</span>
	 */
	public boolean sameCatalog(@Nonnull final List<String> identifyCodes, @Nonnull final List<Condition> conditions) {
		if (identifyCodes.isEmpty() || identifyCodes.size() == 1 || conditions.isEmpty()) {
			return Boolean.TRUE;
		}
		List<String> catalogs = Optional.ofNullable(this.strategyConfigs.get(identifyCodes.get(0)))
				.map(strategyConfig -> strategyConfig.dbKeys(conditions))
				.orElse(Collections.emptyList());
		if (catalogs.isEmpty()) {
			return Boolean.TRUE;
		}
		if (catalogs.size() > 1) {
			return Boolean.FALSE;
		}

		String catalog = catalogs.get(0);

		for (int i = 1; i < identifyCodes.size(); i++) {
			List<String> checkCatalogs = Optional.ofNullable(this.strategyConfigs.get(identifyCodes.get(i)))
					.map(strategyConfig -> strategyConfig.dbKeys(conditions))
					.orElse(Collections.emptyList());
			if (checkCatalogs.size() != 1) {
				return Boolean.FALSE;
			}
			if (!ObjectUtils.nullSafeEquals(catalog, checkCatalogs.get(0))) {
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
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
		this.registeredPools.replaceAll((identifyCode, connectionPool) -> {
			connectionPool.configPooled(pooled);
			return connectionPool;
		});
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
		int poolCount = Globals.INITIALIZE_INT_VALUE;
		for (JdbcConnectionPool connectionPool : this.registeredPools.values()) {
			poolCount += connectionPool.poolCount();
		}
		return poolCount;
	}

	@Override
	public int getActiveCount() {
		int activeCount = Globals.INITIALIZE_INT_VALUE;
		for (JdbcConnectionPool connectionPool : this.registeredPools.values()) {
			activeCount += connectionPool.activeCount();
		}
		return activeCount;
	}

	@Override
	public int getWaitCount() {
		int waitCount = Globals.INITIALIZE_INT_VALUE;
		for (JdbcConnectionPool connectionPool : this.registeredPools.values()) {
			waitCount += connectionPool.waitCount();
		}
		return waitCount;
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

		if (this.pooled) {
			this.executorService = Executors.newSingleThreadScheduledExecutor();
			this.executorService.scheduleWithFixedDelay(
					() -> this.registeredPools.values().forEach(JdbcConnectionPool::createConnections),
					SCHEDULE_PERIOD_TIME, SCHEDULE_PERIOD_TIME, TimeUnit.MILLISECONDS);
		}
		this.initialized = Boolean.TRUE;
	}

	int identifyCode(final ServerInfo serverInfo) throws SQLException {
		if (serverInfo == null) {
			return this.jdbcUrl.hashCode();
		}
		String serverAddress = serverInfo.info();
		if (StringUtils.isEmpty(serverAddress)) {
			throw new MultilingualSQLException(0x00DB00000026L);
		}
		return serverAddress.hashCode();
	}

	/**
	 * <h3 class="en-US">Initialize sharding connections</h3>
	 * <h3 class="zh-CN">初始化分片连接</h3>
	 */
	private void initPools() throws SQLException {
		if (this.serverList.isEmpty()) {
			if (this.registeredPools.isEmpty()) {
				JdbcConnectionPool connectionPool =
						new JdbcConnectionPool(this, this.dialect, this.pooled, this.serverInfo,
								this.defaultCatalog, this.databaseParameters);
				this.registeredPools.put(connectionPool.getIdentifyCode(), connectionPool);
			}
		} else {
			for (ServerInfo serverInfo : this.serverList) {
				int identifyCode = this.identifyCode(serverInfo);
				if (!this.registeredPools.containsKey(identifyCode)) {
					JdbcConnectionPool connectionPool =
							new JdbcConnectionPool(this, this.dialect, this.pooled, serverInfo,
									this.defaultCatalog, this.databaseParameters);
					this.registeredPools.put(identifyCode, connectionPool);
				}
			}
		}
	}

	@Override
	public void close() {
		this.executorService.shutdown();
		this.registeredPools.values().forEach(JdbcConnectionPool::destroy);
		this.registeredPools.clear();
		this.executorService = null;
		this.initialized = Boolean.FALSE;
	}

	/**
	 * <h3 class="en-US">Generate JDBC connection url string</h3>
	 * <h3 class="zh-CN">生成JDBC连接字符串</h3>
	 *
	 * @param serverInfo <span class="en-US">Server information</span>
	 *                   <span class="zh-CN">服务器信息</span>
	 * @return <span class="en-US">JDBC connection url string</span>
	 * <span class="zh-CN">JDBC连接字符串</span>
	 */
	String shardingUrl(final ServerInfo serverInfo) throws SQLException {
		String shardingUrl = this.jdbcUrl;
		if (serverInfo != null) {
			String serverAddress = serverInfo.info();
			if (StringUtils.isEmpty(serverAddress)) {
				throw new MultilingualSQLException(0x00DB00000026L);
			}
			shardingUrl = StringUtils.replace(shardingUrl, "{serverAddress}", serverAddress);
		}
		return shardingUrl;
	}

	/**
	 * <h3 class="en-US">Collect server information. If it is not written to the server, use polling mode to collect server information.</h3>
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
	public void truncateTables() throws SQLException {
		for (JdbcConnectionPool connectionPool : this.registeredPools.values()) {
			connectionPool.truncateTables();
		}
	}

	@Override
	public void truncateTable(@Nonnull final TableDefine tableDefine) throws Exception {
		for (JdbcConnectionPool connectionPool : this.registeredPools.values()) {
			connectionPool.truncateTable(this.strategyConfigs.get(tableDefine.getTableName()));
		}
	}

	@Override
	public void dropTables(final DropOption dropOption) throws SQLException {
		for (JdbcConnectionPool connectionPool : this.registeredPools.values()) {
			connectionPool.dropTables(dropOption);
		}
	}

	@Override
	public void dropTable(@Nonnull final TableDefine tableDefine, @Nonnull final DropOption dropOption)
			throws Exception {
		for (JdbcConnectionPool connectionPool : this.registeredPools.values()) {
			connectionPool.dropTable(tableDefine, dropOption, this.strategyConfigs.get(tableDefine.getTableName()));
		}
	}

	@Override
	public boolean lockRecord(@Nonnull final TableDefine tableDefine, @Nonnull final Map<String, Object> filterMap,
	                          final LockModeType lockOption) throws Exception {
		return !this.retrieve(tableDefine, Globals.DEFAULT_VALUE_STRING, filterMap, Boolean.TRUE, lockOption).isEmpty();
	}

	@Override
	public Map<String, Object> insert(@Nonnull final TableDefine tableDefine, @Nonnull final Map<String, Object> dataMap)
			throws SQLException, InsertException {
		StrategyConfig strategyConfig = this.strategyConfigs.get(tableDefine.getTableName());
		String catalog = strategyConfig.dbKey(dataMap);
		String shardingName = strategyConfig.tableKey(dataMap);
		//  Initialize table
		JdbcConnectionPool connectionPool = this.registeredPools.get(this.identifyCode(this.currentServer(Boolean.TRUE)));
		if (connectionPool == null) {
			throw new MultilingualSQLException(0x00DB00000027L);
		}
		connectionPool.initTable(tableDefine, catalog, shardingName, strategyConfig.shardingTable(), Map.of());
		GeneratedCommand sqlCommand = this.dialect.insertCommand(tableDefine, shardingName, dataMap);
		try (Connection connection = this.obtainConnection(Boolean.TRUE, catalog);
		     PreparedStatement statement =
				     connection.prepareStatement(sqlCommand.getCommand(), Statement.RETURN_GENERATED_KEYS)) {
			this.configTimeout(statement);
			int index = Globals.INITIALIZE_INT_VALUE;
			for (Object object : sqlCommand.getParameters()) {
				statement.setObject(index + 1, object);
				index++;
			}
			if (statement.executeUpdate() == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next()) {
					return this.parseResultSet(sqlCommand.getJdbcTypeMap(), resultSet, this.dialect);
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
		}
	}

	@Override
	public Map<String, Object> retrieve(@Nonnull final TableDefine tableDefine, final String columns,
	                                    @Nonnull final Map<String, Object> filterMap, final boolean forUpdate,
	                                    final LockModeType lockOption)
			throws SQLException, RetrieveException {
		StrategyConfig strategyConfig = this.strategyConfigs.get(tableDefine.getTableName());
		String catalog = strategyConfig.dbKey(filterMap);
		String shardingTable = strategyConfig.tableKey(filterMap);
		String queryColumns = StringUtils.isEmpty(columns) ? SELECT_ALL_COLUMNS : columns;
		GeneratedCommand sqlCommand =
				this.dialect.retrieveCommand(tableDefine, shardingTable, queryColumns, filterMap, forUpdate, lockOption);
		try (Connection connection = this.obtainConnection(forUpdate, catalog);
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
				resultMap.putAll(this.parseResultSet(sqlCommand.getJdbcTypeMap(), resultSet, this.dialect));
			}
			return resultMap;
		}
	}

	@Override
	public int update(@Nonnull final TableDefine tableDefine, @Nonnull final Map<String, Object> dataMap,
	                  @Nonnull final Map<String, Object> filterMap) throws SQLException, UpdateException {
		StrategyConfig strategyConfig = this.strategyConfigs.get(tableDefine.getTableName());
		String catalog = strategyConfig.dbKey(filterMap);
		Map<String, Object> updatedMap = new HashMap<>(filterMap);
		updatedMap.putAll(dataMap);
		String newCatalog = strategyConfig.dbKey(updatedMap);
		if (!ObjectUtils.nullSafeEquals(catalog, newCatalog)) {
			//  After update operated, will result of data migration
			throw new UpdateException(0x00DB00000043L, tableDefine.getTableName(),
					StringUtils.objectToString(dataMap, StringUtils.StringType.JSON, Boolean.TRUE),
					StringUtils.objectToString(filterMap, StringUtils.StringType.JSON, Boolean.TRUE),
					catalog, newCatalog);
		}
		String tableName = strategyConfig.tableKey(filterMap);
		GeneratedCommand sqlCommand = this.dialect.updateCommand(tableName, dataMap, filterMap);
		int count = Globals.INITIALIZE_INT_VALUE;
		try (Connection connection = this.obtainConnection(Boolean.TRUE, catalog);
		     PreparedStatement statement = connection.prepareStatement(sqlCommand.getCommand())) {
			this.configTimeout(statement);
			int index = 1;
			for (Object object : sqlCommand.getParameters()) {
				statement.setObject(index, object);
				index++;
			}
			count += statement.executeUpdate();
		} catch (SQLException e) {
			throw new UpdateException(0x00DB00000040L, e, tableDefine.getTableName(),
					StringUtils.objectToString(dataMap, StringUtils.StringType.JSON, Boolean.TRUE),
					StringUtils.objectToString(filterMap, StringUtils.StringType.JSON, Boolean.TRUE));
		}
		return count;
	}

	@Override
	public int delete(@Nonnull final TableDefine tableDefine, @Nonnull final Map<String, Object> filterMap)
			throws SQLException, DropException {
		StrategyConfig strategyConfig = this.strategyConfigs.get(tableDefine.getTableName());
		String tableName = strategyConfig.tableKey(filterMap);
		GeneratedCommand sqlCommand = this.dialect.deleteCommand(tableName, filterMap);
		int count = Globals.INITIALIZE_INT_VALUE;
		for (String catalog : strategyConfig.dbKeys(filterMap)) {
			try (Connection connection = this.obtainConnection(Boolean.TRUE, catalog);
			     PreparedStatement statement = connection.prepareStatement(sqlCommand.getCommand())) {
				this.configTimeout(statement);
				int index = 1;
				for (Object object : sqlCommand.getParameters()) {
					statement.setObject(index, object);
					index++;
				}
				count += statement.executeUpdate();
			} catch (SQLException e) {
				throw new DropException(0x00DB00000041L, e, tableDefine.getTableName(),
						StringUtils.objectToString(filterMap, StringUtils.StringType.JSON, Boolean.TRUE));
			}
		}
		return count;
	}

	@Override
	public Long queryTotal(final QueryInfo queryInfo) throws Exception {
		GeneratedCommand sqlCommand = this.dialect.queryTotalCommand(queryInfo);
		long totalCount = 0L;
		for (String catalog : this.dbKeys(queryInfo)) {
			try (Connection connection = this.obtainConnection(Boolean.TRUE, catalog);
			     PreparedStatement statement = connection.prepareStatement(sqlCommand.getCommand())) {
				this.configTimeout(statement);
				int index = 1;
				for (Object object : sqlCommand.getParameters()) {
					statement.setObject(index, object);
					index++;
				}

				ResultSet resultSet = statement.executeQuery();
				if (resultSet.next()) {
					totalCount += resultSet.getLong(1);
				}
			}
		}
		return totalCount;
	}

	private List<String> dbKeys(@Nonnull final QueryInfo queryInfo) {
		List<String> catalogs = new ArrayList<>();
		for (QueryFrom queryFrom : queryInfo.getQueryFrom()) {
			if (queryFrom instanceof FromTable) {
				this.dbKeys(((FromTable) queryFrom).getTableName(), queryInfo.getConditionList())
						.stream()
						.filter(catalog -> !catalogs.contains(catalog))
						.forEach(catalogs::add);
			} else if (queryFrom instanceof FromSubQuery) {
				this.dbKeys(((FromSubQuery) queryFrom).getQueryData())
						.stream()
						.filter(catalog -> !catalogs.contains(catalog))
						.forEach(catalogs::add);
			}
		}
		for (QueryItem queryItem : queryInfo.getItemList()) {
			if (queryItem instanceof SubQueryItem) {
				this.dbKeys(((SubQueryItem) queryItem).getQueryData())
						.stream()
						.filter(catalog -> !catalogs.contains(catalog))
						.forEach(catalogs::add);
			}
		}
		return catalogs;
	}

	@Nonnull
	private List<String> dbKeys(@Nonnull final String tableName, @Nonnull final List<Condition> conditionList) {
		return Optional.ofNullable(this.strategyConfigs.get(tableName))
				.map(strategyConfig -> strategyConfig.dbKeys(conditionList))
				.orElse(Collections.emptyList());
	}

	@Nonnull
	private List<String> dbKeys(@Nonnull QueryData queryData) {
		List<String> catalogs = new ArrayList<>();
		this.dbKeys(queryData.getTableName(), queryData.getConditionList())
				.stream()
				.filter(catalog -> !catalogs.contains(catalog))
				.forEach(catalogs::add);
		for (QueryItem queryItem : queryData.getItemList()) {
			if (queryItem instanceof SubQueryItem) {
				this.dbKeys(((SubQueryItem) queryItem).getQueryData())
						.stream()
						.filter(catalog -> !catalogs.contains(catalog))
						.forEach(catalogs::add);
			}
		}
		return catalogs;
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
	public PartialCollection query(@Nonnull final QueryInfo queryInfo) throws Exception {
		List<String> catalogs = this.dbKeys(queryInfo);
		if (catalogs.isEmpty()) {
			return new PartialCollection(Collections.emptyList(), 0L);
		} else if (catalogs.size() == 1) {
			long totalCount = this.queryTotal(queryInfo);
			return new PartialCollection(
					this.executeQuery(catalogs.get(0),
							this.dialect.queryCommand(queryInfo, Boolean.TRUE), Boolean.FALSE),
					totalCount);
		} else {
			List<Map<String, Object>> resultList = new ArrayList<>();
			for (String catalog : catalogs) {
				GeneratedCommand sqlCommand = this.dialect.queryCommand(queryInfo, Boolean.FALSE);
				resultList.addAll(this.executeQuery(catalog, sqlCommand, queryInfo.isForUpdate()));
			}

			List<OrderBy> orderByList = queryInfo.getOrderByList();
			orderByList.sort((o1, o2) -> Integer.compare(o2.getSortCode(), o1.getSortCode()));
			resultList.sort((o1, o2) -> {
				for (OrderBy orderBy : orderByList) {
					int result = 0;
					switch (orderBy.getOrderType()) {
						case ASC:
							result = ObjectUtils.nullSafeCompare(o1.get(orderBy.getColumnName()), o2.get(orderBy.getColumnName()));
							break;
						case DESC:
							result = ObjectUtils.nullSafeCompare(o2.get(orderBy.getColumnName()), o1.get(orderBy.getColumnName()));
							break;
					}
					if (result != 0) {
						return result;
					}
				}
				return 0;
			});
			int totalCount = resultList.size();
			if (queryInfo.getPageNo() > 1 || queryInfo.getPageLimit() > 0) {
				int pageNo = queryInfo.getPageNo() > 0 ? queryInfo.getPageNo() : BrainCommons.DEFAULT_PAGE_NO;
				int pageLimit = (queryInfo.getPageLimit() > 0) ? queryInfo.getPageLimit() : BrainCommons.DEFAULT_PAGE_LIMIT;
				return new PartialCollection(
						resultList
								.stream()
								.skip(pageLimit * (pageNo - 1L))
								.limit(pageLimit)
								.collect(Collectors.toList()),
						totalCount);
			}
			return new PartialCollection(resultList, totalCount);
		}
	}

	@Override
	public void initTable(@Nonnull final DDLType ddlType, @Nonnull final TableDefine tableDefine,
	                      @Nonnull final Map<String, InitOption> initOptionsMap) throws Exception {
		StrategyConfig strategyConfig = this.strategyConfigs.get(tableDefine.getTableName());
		for (JdbcConnectionPool connectionPool : this.registeredPools.values()) {
			connectionPool.initTable(ddlType, tableDefine, strategyConfig, initOptionsMap);
		}
	}

	/**
	 * <h3 class="en-US">Register strategy define information</h3>
	 * <h3 class="zh-CN">注册分片配置信息</h3>
	 *
	 * @param tableDefine      <span class="en-US">Table defines information</span>
	 *                         <span class="zh-CN">数据表定义信息</span>
	 * @param databaseStrategy <span class="en-US">Database strategy defines information</span>
	 *                         <span class="zh-CN">数据库分片规则定义信息</span>
	 * @param tableStrategy    <span class="en-US">Data table strategy defines information</span>
	 *                         <span class="zh-CN">数据表分片规则定义信息</span>
	 */
	public void registerStrategy(final TableDefine tableDefine, final StrategyDefine databaseStrategy,
	                             final StrategyDefine tableStrategy) {
		if (!this.strategyConfigs.containsKey(tableDefine.getTableName())) {
			this.strategyConfigs.put(tableDefine.getTableName(),
					new StrategyConfig(tableDefine, databaseStrategy, tableStrategy));
		}

	}

	/**
	 * <h3 class="en-US">Obtain database connection</h3>
	 * <h3 class="zh-CN">获取数据库连接</h3>
	 *
	 * @param forUpdate <span class="en-US">Retrieve result using for update record</span>
	 *                  <span class="zh-CN">检索结果用于更新记录</span>
	 * @param catalog   <span class="en-US">Sharded database name</span>
	 *                  <span class="zh-CN">分片数据库名</span>
	 * @return <span class="en-US">Database connection</span>
	 * <span class="zh-CN">数据库连接</span>
	 * @throws SQLException <span class="en-US">An error occurred during execution</span>
	 *                      <span class="zh-CN">执行过程中出错</span>
	 */
	private JdbcConnection obtainConnection(final boolean forUpdate, final String catalog)
			throws SQLException {
		ServerInfo serverInfo = this.currentServer(forUpdate);
		int identifyCode = this.identifyCode(serverInfo);
		JdbcConnection connection = null;
		int isolation = (this.txConfig.get() != null) ? this.txConfig.get().getIsolation() : Connection.TRANSACTION_NONE;
		if (isolation != Connection.TRANSACTION_NONE) {
			connection = this.currentConnections.get().stream()
					.filter(jdbcConnection -> jdbcConnection.match(identifyCode, catalog))
					.findFirst()
					.orElse(null);
		}

		if (connection == null) {
			JdbcConnectionPool connectionPool = this.registeredPools.get(identifyCode);
			if (connectionPool == null) {
				throw new MultilingualSQLException(0x00DB00000027L);
			}
			connection = connectionPool.obtainConnection(catalog, isolation);
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
	 * @param catalog    <span class="en-US">Sharded database name</span>
	 *                   <span class="zh-CN">分片数据库名</span>
	 * @param sqlCommand <span class="en-US">SQL command to execute</span>
	 *                   <span class="zh-CN">要执行的SQL命令</span>
	 * @param forUpdate  <span class="en-US">Retrieve result using for update record</span>
	 *                   <span class="zh-CN">检索结果用于更新记录</span>
	 * @return <span class="en-US">Query record list</span>
	 * <span class="zh-CN">查询到的记录列表</span>
	 * @throws SQLException <span class="en-US">An error occurred during execution</span>
	 *                      <span class="zh-CN">执行过程中出错</span>
	 */
	private List<Map<String, Object>> executeQuery(@Nonnull final String catalog,
	                                               @Nonnull final GeneratedCommand sqlCommand,
	                                               final boolean forUpdate) throws SQLException {
		try (Connection connection = this.obtainConnection(forUpdate, catalog);
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
				resultList.add(this.parseResultSet(sqlCommand.getJdbcTypeMap(), resultSet, this.dialect));
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
	private Map<String, Object> parseResultSet(@Nonnull final Map<String, Integer> jdbcTypeMap,
	                                           final ResultSet resultSet, final JdbcDialect jdbcDialect)
			throws SQLException {
		ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
		Map<String, Object> resultMap = new HashMap<>();
		int columnCount = resultSetMetaData.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			String columnLabel = jdbcDialect.nameCase(resultSetMetaData.getColumnLabel(i));
			if (!jdbcTypeMap.containsKey(columnLabel)) {
				continue;
			}
			switch (jdbcTypeMap.get(columnLabel)) {
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
