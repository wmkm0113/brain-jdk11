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

package org.nervousync.brain.source;

import jakarta.annotation.Nonnull;
import jakarta.persistence.LockModeType;
import org.nervousync.annotations.jmx.Monitor;
import org.nervousync.annotations.provider.Provider;
import org.nervousync.brain.commons.BrainCommons;
import org.nervousync.brain.configs.BrainConfigure;
import org.nervousync.brain.configs.schema.SchemaConfig;
import org.nervousync.brain.configs.schema.impl.DistributeSchemaConfig;
import org.nervousync.brain.configs.schema.impl.JdbcSchemaConfig;
import org.nervousync.brain.configs.schema.impl.RemoteSchemaConfig;
import org.nervousync.brain.configs.transactional.TransactionalConfig;
import org.nervousync.brain.defines.ColumnDefine;
import org.nervousync.brain.defines.StrategyDefine;
import org.nervousync.brain.defines.TableDefine;
import org.nervousync.brain.enumerations.ddl.DDLType;
import org.nervousync.brain.enumerations.ddl.DropOption;
import org.nervousync.brain.enumerations.dialect.DialectType;
import org.nervousync.brain.exceptions.sql.MultilingualSQLException;
import org.nervousync.brain.manager.TableManager;
import org.nervousync.brain.query.PartialCollection;
import org.nervousync.brain.query.QueryInfo;
import org.nervousync.brain.query.core.QueryFrom;
import org.nervousync.brain.query.from.FromSubQuery;
import org.nervousync.brain.query.from.FromTable;
import org.nervousync.brain.query.optimizer.QueryOptimizer;
import org.nervousync.brain.schemas.BaseSchema;
import org.nervousync.brain.schemas.distribute.DistributeSchema;
import org.nervousync.brain.schemas.jdbc.JdbcSchema;
import org.nervousync.brain.schemas.remote.RemoteSchema;
import org.nervousync.commons.Globals;
import org.nervousync.enumerations.beans.StringType;
import org.nervousync.utils.core.BeanUtils;
import org.nervousync.utils.core.ObjectUtils;
import org.nervousync.utils.core.StringUtils;
import org.nervousync.utils.jmx.JMXUtils;
import org.nervousync.utils.logger.LoggerUtils;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <h2 class="en-US">Nervousync brain data source</h2>
 * <h2 class="zh-CN">Nervousync 大脑数据源</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Nov 12, 2020 12:20:49 $
 */
@SuppressWarnings("unused")
@Monitor(domain = "org.nervousync", type = "DataSource", name = "Brain")
public final class BrainDataSource implements BrainDataSourceMBean {

	/**
	 * <span class="en-US">Multilingual supported logger instance</span>
	 * <span class="zh-CN">多语言支持的日志对象</span>
	 */
	private static final LoggerUtils.Logger LOGGER = LoggerUtils.getLogger(BrainDataSource.class);
	/**
	 * <span class="en-US">Prefix string for JMX object ObjectName</span>
	 * <span class="zh-CN">JMX对象ObjectName的前缀字符串</span>
	 */
	private static final String JMX_OBJECT_NAME_PREFIX = "org.nervousync:type=DataSource,name=";
	/**
	 * <span class="en-US">Registered implementation class of query optimizer</span>
	 * <span class="zh-CN">注册的查询优化器实现类</span>
	 */
	private static final Hashtable<String, Class<?>> REGISTERED_OPTIMIZERS = new Hashtable<>();

	/**
	 * <span class="en-US">Data source initialize status</span>
	 * <span class="zh-CN">数据源初始化状态</span>
	 */
	private boolean initialized = Boolean.FALSE;
	/**
	 * <span class="en-US">Default data source name</span>
	 * <span class="zh-CN">默认数据源名称</span>
	 */
	private String defaultName = Globals.DEFAULT_VALUE_STRING;
	/**
	 * <span class="en-US">Data source initialize status</span>
	 * <span class="zh-CN">数据源初始化状态</span>
	 */
	private boolean jmxEnabled = Boolean.FALSE;
	/**
	 * <span class="en-US">Data source initialize status</span>
	 * <span class="zh-CN">数据源初始化状态</span>
	 */
	private DDLType ddlType;
	/**
	 * <span class="en-US">Used identification code of query optimizer implementation class</span>
	 * <span class="zh-CN">使用的查询优化器实现类识别代码</span>
	 */
	private String optimizerName;
	/**
	 * <span class="en-US">Query optimizer pool size</span>
	 * <span class="zh-CN">查询优化器对象池大小</span>
	 */
	private int poolSize = BrainCommons.DEFAULT_OPTIMIZER_POOL_SIZE;

	/**
	 * <span class="en-US">Registered data source instance mapping table</span>
	 * <span class="zh-CN">注册的数据源实例映射表</span>
	 */
	private final Hashtable<String, BaseSchema<?>> registeredSchemas;
	/**
	 * <span class="en-US">Data table manager instance object</span>
	 * <span class="zh-CN">数据表管理器实例对象</span>
	 */
	private final TableManager tableManager;
	/**
	 * <span class="en-US">Last modified timestamp</span>
	 * <span class="zh-CN">最后修改时间戳</span>
	 */
	private long lastModified = Globals.DEFAULT_VALUE_LONG;

	/**
	 * <span class="en-US">Schedule task executor instance object</span>
	 * <span class="zh-CN">定时任务调度执行器</span>
	 */
	private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
	/**
	 * <span class="en-US">Query optimizer instance objects pool</span>
	 * <span class="zh-CN">查询分析器实例对象池</span>
	 */
	private final Queue<QueryOptimizer> optimizersPool = new LinkedList<>();
	/**
	 * <span class="en-US">Schedule task running flag</span>
	 * <span class="zh-CN">调度任务执行标记</span>
	 */
	private boolean scheduleRunning = Boolean.FALSE;

	static {
		ServiceLoader.load(QueryOptimizer.class)
				.forEach(queryOptimizer ->
						Optional.ofNullable(queryOptimizer.getClass().getAnnotation(Provider.class))
								.ifPresent(provider ->
										REGISTERED_OPTIMIZERS.put(provider.name(), queryOptimizer.getClass())));
	}

	/**
	 * <h3 class="en-US">Default constructor method for the data source</h3>
	 * <h3 class="zh-CN">数据源的默认构造方法</h3>
	 */
	BrainDataSource() {
		this.registeredSchemas = new Hashtable<>();
		this.tableManager = TableManager.getInstance();
		this.scheduledExecutorService.scheduleAtFixedRate(this::schedule, 0L, 1000L, TimeUnit.MILLISECONDS);
	}

	/**
	 * <h3 class="en-US">Static method is used to obtain the data source singleton instance object</h3>
	 * <h3 class="zh-CN">静态方法用于获取数据源单例实例对象</h3>
	 *
	 * @return <span class="en-US">Data source singleton instance object</span>
	 * <span class="zh-CN">数据源单例实例对象</span>
	 */
	public static BrainDataSource getInstance() {
		return BrainDataSourceHolder.getInstance();
	}

	/**
	 * <h3 class="en-US">Destroy current instance</h3>
	 * <h3 class="zh-CN">销毁当前实例</h3>
	 */
	public static void destroy() {
		BrainDataSourceHolder.destroy();
	}

	/**
	 * <h3 class="en-US">Registered implementation class of query optimizer</h3>
	 * <h3 class="zh-CN">注册的查询优化器实现类</h3>
	 *
	 * @return <span class="en-US">Registered implementation class of query optimizer</span>
	 * <span class="zh-CN">注册的查询优化器实现类</span>
	 */
	public static Map<String, Class<?>> registeredOptimizers() {
		return REGISTERED_OPTIMIZERS;
	}

	/**
	 * <h3 class="en-US">Initialize data source</h3>
	 * <h3 class="zh-CN">初始化数据源</h3>
	 *
	 * @param configure <span class="en-US">Data source configure information instance object</span>
	 *                  <span class="zh-CN">数据源配置信息实例对象</span>
	 */
	public void initialize(final BrainConfigure configure) {
		if (this.lastModified != Globals.DEFAULT_VALUE_LONG && this.lastModified == configure.getLastModified()) {
			return;
		}
		this.optimizerName = configure.getOptimizerName();
		this.poolSize = configure.getPoolSize();
		this.ddlType = (configure.getDdlType() == null) ? DDLType.NONE : configure.getDdlType();
		this.jmxEnabled(configure.isJmxMonitor());
		for (SchemaConfig schemaConfig : configure.getSchemaConfigs()) {
			try {
				this.register(schemaConfig);
			} catch (Exception e) {
				LOGGER.error("Register_Schema_Config_Error", BeanUtils.objectToString(schemaConfig, StringType.JSON));
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Stack_Message_Error", e);
				}
			}
		}
		this.lastModified = configure.getLastModified();
		if (!configure.isLazyInitialize()) {
			this.initialize();
		}
	}

	/**
	 * <h3 class="en-US">Check whether the given data source name was registered</h3>
	 * <h3 class="zh-CN">检查给定的数据源名称是否注册</h3>
	 *
	 * @param schemaName <span class="en-US">Data schema name</span>
	 *                   <span class="zh-CN">数据源名称</span>
	 * @return <span class="en-US">Check result</span>
	 * <span class="zh-CN">检查结果</span>
	 */
	public boolean registered(final String schemaName) {
		return this.registeredSchemas.containsKey(schemaName);
	}

	/**
	 * <h3 class="en-US">Check whether the given data source name is registered and whether the dialect type is consistent with the current dialect type</h3>
	 * <h3 class="zh-CN">检查给定的数据源名称是否注册，方言类型与当前方言类型是否一致</h3>
	 *
	 * @param schemaName  <span class="en-US">Data schema name</span>
	 *                    <span class="zh-CN">数据源名称</span>
	 * @param dialectType <span class="en-US">Data source dialect type enumeration value</span>
	 *                    <span class="zh-CN">数据源方言类型枚举值</span>
	 * @return <span class="en-US">Check result</span>
	 * <span class="zh-CN">检查结果</span>
	 */
	public boolean registered(final String schemaName, final DialectType dialectType) {
		try {
			return Optional.of(this.retrieveSchema(schemaName))
					.map(schema -> schema.match(dialectType))
					.orElse(Boolean.FALSE);
		} catch (SQLException e) {
			return Boolean.FALSE;
		}
	}

	/**
	 * <h3 class="en-US">Checks whether the given data source supports relational queries</h3>
	 * <h3 class="zh-CN">检查给定的数据源是否支持关联查询</h3>
	 *
	 * @param schemaName <span class="en-US">Data schema name</span>
	 *                   <span class="zh-CN">数据源名称</span>
	 * @return <span class="en-US">Support join query</span>
	 * <span class="zh-CN">支持关联查询</span>
	 */
	public boolean supportJoin(final String schemaName) {
		try {
			return Optional.of(this.retrieveSchema(schemaName))
					.map(BaseSchema::supportJoin)
					.orElse(Boolean.FALSE);
		} catch (SQLException e) {
			return Boolean.FALSE;
		}
	}

	/**
	 * <h3 class="en-US">Convert default value to string</h3>
	 * <h3 class="zh-CN">转换默认值为字符串</h3>
	 *
	 * @param schemaName   <span class="en-US">Data schema name</span>
	 *                     <span class="zh-CN">数据源名称</span>
	 * @param columnDefine <span class="en-US">Column define information</span>
	 *                     <span class="zh-CN">数据列定义信息</span>
	 * @param object       <span class="en-US">Default value instance object</span>
	 *                     <span class="zh-CN">默认值实例对象</span>
	 * @return <span class="en-US">Default value string</span>
	 * <span class="zh-CN">默认值字符串</span>
	 */
	public String defaultValue(final String schemaName, final ColumnDefine columnDefine, final Object object) {
		try {
			return Optional.of(this.retrieveSchema(schemaName))
					.map(schema -> schema.defaultValue(columnDefine, object))
					.orElse(Globals.DEFAULT_VALUE_STRING);
		} catch (SQLException e) {
			return Globals.DEFAULT_VALUE_STRING;
		}
	}

	/**
	 * <h3 class="en-US">Initialize data table</h3>
	 * <h3 class="zh-CN">初始化数据表</h3>
	 *
	 * @param tableDefine      <span class="en-US">Table defines information</span>
	 *                         <span class="zh-CN">数据表定义信息</span>
	 * @param databaseStrategy <span class="en-US">Database strategy defines information</span>
	 *                         <span class="zh-CN">数据库分片规则定义信息</span>
	 * @param tableStrategy    <span class="en-US">Data table strategy defines information</span>
	 *                         <span class="zh-CN">数据表分片规则定义信息</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public void initTable(@Nonnull final TableDefine tableDefine,
	                      final StrategyDefine databaseStrategy, final StrategyDefine tableStrategy) throws Exception {
		BaseSchema<?> schema = this.retrieveSchema(tableDefine.getSchemaName());
		if (schema instanceof JdbcSchema) {
			schema.unwrap(JdbcSchema.class).registerStrategy(tableDefine, databaseStrategy, tableStrategy);
		}
		schema.initTable(this.ddlType, tableDefine);
		this.tableManager.register(tableDefine);
	}

	/**
	 * <h3 class="en-US">Initialize the current thread used operator based on the given transaction configuration information</h3>
	 * <h3 class="zh-CN">根据给定的事务配置信息初始化当前线程的操作器</h3>
	 *
	 * @param transactionalConfig <span class="en-US">Transactional configure information</span>
	 *                            <span class="zh-CN">事务配置信息</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public void initTransactional(final TransactionalConfig transactionalConfig) throws Exception {
		for (final BaseSchema<?> schema : this.registeredSchemas.values()) {
			schema.initTransactional(transactionalConfig);
		}
	}

	/**
	 * <h3 class="en-US">Finish current transactional</h3>
	 * <h3 class="zh-CN">结束当前事务</h3>
	 *
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public void endTransactional() throws Exception {
		for (final BaseSchema<?> schema : this.registeredSchemas.values()) {
			schema.endTransactional();
		}
	}

	/**
	 * <h3 class="en-US">Rollback transactional</h3>
	 * <h3 class="zh-CN">回滚事务</h3>
	 *
	 * @param e <span class="en-US">Cached execution information</span>
	 *          <span class="zh-CN">捕获的异常信息</span>
	 * @throws Exception <span class="en-US">If an error occurs during execution</span>
	 *                   <span class="zh-CN">如果执行过程中出错</span>
	 */
	public void rollback(final Exception e) throws Exception {
		for (final BaseSchema<?> schema : this.registeredSchemas.values()) {
			schema.rollback(e);
		}
	}

	/**
	 * <h3 class="en-US">Submit transactional execute</h3>
	 * <h3 class="zh-CN">提交事务执行</h3>
	 *
	 * @throws Exception <span class="en-US">If an error occurs during execution</span>
	 *                   <span class="zh-CN">如果执行过程中出错</span>
	 */
	public void commit() throws Exception {
		for (final BaseSchema<?> schema : this.registeredSchemas.values()) {
			schema.commit();
		}
	}

	/**
	 * <h3 class="en-US">Truncate all data tables</h3>
	 * <h3 class="zh-CN">清空所有数据表</h3>
	 *
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public void truncateTables() throws Exception {
		for (final BaseSchema<?> schema : this.registeredSchemas.values()) {
			schema.truncateTables();
		}
	}

	/**
	 * <h3 class="en-US">Truncate data table</h3>
	 * <h3 class="zh-CN">清空数据表</h3>
	 *
	 * @param tableName <span class="en-US">Data table name</span>
	 *                  <span class="zh-CN">数据表名称</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public void truncateTable(@Nonnull final String tableName) throws Exception {
		TableDefine tableDefine = this.tableManager.define(tableName);
		this.retrieveSchema(tableDefine.getSchemaName()).truncateTable(tableDefine);
	}

	/**
	 * <h3 class="en-US">Drop all data tables</h3>
	 * <h3 class="zh-CN">删除所有数据表</h3>
	 *
	 * @param dropOption <span class="en-US">Cascading delete options</span>
	 *                   <span class="zh-CN">级联删除选项</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public void dropTables(final DropOption dropOption) throws Exception {
		for (final BaseSchema<?> schema : this.registeredSchemas.values()) {
			schema.dropTables(dropOption);
		}
	}

	/**
	 * <h3 class="en-US">Drop data table</h3>
	 * <h3 class="zh-CN">删除数据表</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名称</span>
	 * @param dropOption <span class="en-US">Cascading delete options</span>
	 *                   <span class="zh-CN">级联删除选项</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public void dropTable(@Nonnull final String tableName, @Nonnull final DropOption dropOption) throws Exception {
		TableDefine tableDefine = this.tableManager.define(tableName);
		this.retrieveSchema(tableDefine.getSchemaName()).dropTable(tableDefine, dropOption);
	}

	/**
	 * <h3 class="en-US">Execute lock record command</h3>
	 * <h3 class="zh-CN">执行数据锁定命令</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名称</span>
	 * @param filterMap  <span class="en-US">Filter data mapping</span>
	 *                   <span class="zh-CN">查询数据映射表</span>
	 * @param lockOption <span class="en-US">Lock option</span>
	 *                   <span class="zh-CN">数据锁选项</span>
	 * @return <span class="en-US">Primary key value mapping table generated by database</span>
	 * <span class="zh-CN">数据库生成的主键值映射表</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public boolean lockRecord(@Nonnull final String tableName, @Nonnull final Map<String, Object> filterMap,
	                          final LockModeType lockOption) throws Exception {
		TableDefine tableDefine = this.tableManager.define(tableName);
		return this.retrieveSchema(tableDefine.getSchemaName()).lockRecord(tableDefine, filterMap, lockOption);
	}

	/**
	 * <h3 class="en-US">Execute insert record command</h3>
	 * <h3 class="zh-CN">执行插入数据命令</h3>
	 *
	 * @param tableName <span class="en-US">Data table name</span>
	 *                  <span class="zh-CN">数据表名称</span>
	 * @param dataMap   <span class="en-US">Insert data mapping</span>
	 *                  <span class="zh-CN">写入数据映射表</span>
	 * @return <span class="en-US">Primary key value mapping table generated by database</span>
	 * <span class="zh-CN">数据库生成的主键值映射表</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public Map<String, Object> insert(@Nonnull final String tableName, @Nonnull final Map<String, Object> dataMap)
			throws Exception {
		TableDefine tableDefine = this.tableManager.define(tableName);
		return this.retrieveSchema(tableDefine.getSchemaName()).insert(tableDefine, dataMap);
	}

	/**
	 * <h3 class="en-US">Execute retrieve record command</h3>
	 * <h3 class="zh-CN">执行数据唯一检索命令</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名称</span>
	 * @param columns    <span class="en-US">Query column names</span>
	 *                   <span class="zh-CN">查询数据列名</span>
	 * @param filterMap  <span class="en-US">Retrieve filter mapping</span>
	 *                   <span class="zh-CN">查询条件映射表</span>
	 * @param forUpdate  <span class="en-US">Retrieve result using for update record</span>
	 *                   <span class="zh-CN">检索结果用于更新记录</span>
	 * @param lockOption <span class="en-US">Lock option</span>
	 *                   <span class="zh-CN">数据锁选项</span>
	 * @return <span class="en-US">Data mapping table of retrieved records</span>
	 * <span class="zh-CN">检索到记录的数据映射表</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public Map<String, Object> retrieve(@Nonnull final String tableName, final String columns,
	                                    @Nonnull final Map<String, Object> filterMap, final boolean forUpdate,
	                                    final LockModeType lockOption) throws Exception {
		TableDefine tableDefine = this.tableManager.define(tableName);
		return this.retrieveSchema(tableDefine.getSchemaName()).retrieve(tableDefine, columns, filterMap, forUpdate, lockOption);
	}

	/**
	 * <h3 class="en-US">Execute update record command</h3>
	 * <h3 class="zh-CN">执行更新记录命令</h3>
	 *
	 * @param tableName <span class="en-US">Data table name</span>
	 *                  <span class="zh-CN">数据表名称</span>
	 * @param dataMap   <span class="en-US">Update data mapping</span>
	 *                  <span class="zh-CN">更新数据映射表</span>
	 * @param filterMap <span class="en-US">Update filter mapping</span>
	 *                  <span class="zh-CN">更新条件映射表</span>
	 * @return <span class="en-US">Updated records count</span>
	 * <span class="zh-CN">更新记录条数</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public int update(@Nonnull final String tableName, @Nonnull final Map<String, Object> dataMap,
	                  @Nonnull final Map<String, Object> filterMap) throws Exception {
		TableDefine tableDefine = this.tableManager.define(tableName);
		return this.retrieveSchema(tableDefine.getSchemaName()).update(tableDefine, dataMap, filterMap);
	}

	/**
	 * <h3 class="en-US">Execute delete record command</h3>
	 * <h3 class="zh-CN">执行删除记录命令</h3>
	 *
	 * @param tableName <span class="en-US">Data table name</span>
	 *                  <span class="zh-CN">数据表名称</span>
	 * @param filterMap <span class="en-US">Delete filter mapping</span>
	 *                  <span class="zh-CN">删除条件映射表</span>
	 * @return <span class="en-US">Deleted records count</span>
	 * <span class="zh-CN">删除记录条数</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public int delete(@Nonnull final String tableName, @Nonnull final Map<String, Object> filterMap) throws Exception {
		TableDefine tableDefine = this.tableManager.define(tableName);
		return this.retrieveSchema(tableDefine.getSchemaName()).delete(tableDefine, filterMap);
	}

	/**
	 * <h3 class="en-US">Direct execute query record command</h3>
	 * <h3 class="zh-CN">直接执行数据检索命令</h3>
	 *
	 * @param queryInfo <span class="en-US">Query record information</span>
	 *                  <span class="zh-CN">数据检索信息</span>
	 * @return <span class="en-US">List of data mapping tables for queried records</span>
	 * <span class="zh-CN">查询到记录的数据映射表列表</span>
	 * @throws SQLException <span class="en-US">An error occurred during execution</span>
	 *                      <span class="zh-CN">执行过程中出错</span>
	 */
	public PartialCollection directQuery(@Nonnull final QueryInfo queryInfo) throws Exception {
		return this.retrieveSchema(queryInfo.getQueryFrom()).query(queryInfo);
	}

	/**
	 * <h3 class="en-US">Direct execute query total record count</h3>
	 * <h3 class="zh-CN">直接执行查询总记录数</h3>
	 *
	 * @param queryInfo <span class="en-US">Query record information</span>
	 *                  <span class="zh-CN">数据检索信息</span>
	 * @return <span class="en-US">Total record count</span>
	 * <span class="zh-CN">总记录条数</span>
	 * @throws SQLException <span class="en-US">An error occurred during execution</span>
	 *                      <span class="zh-CN">执行过程中出错</span>
	 */
	public Long directQueryTotal(@Nonnull final QueryInfo queryInfo) throws Exception {
		return this.retrieveSchema(queryInfo.getQueryFrom()).queryTotal(queryInfo);
	}

	/**
	 * <h3 class="en-US">Execute query record command</h3>
	 * <h3 class="zh-CN">执行数据检索命令</h3>
	 *
	 * @param queryInfo <span class="en-US">Query record information</span>
	 *                  <span class="zh-CN">数据检索信息</span>
	 * @return <span class="en-US">List of data mapping tables for queried records</span>
	 * <span class="zh-CN">查询到记录的数据映射表列表</span>
	 * @throws SQLException <span class="en-US">An error occurred during execution</span>
	 *                      <span class="zh-CN">执行过程中出错</span>
	 */
	public PartialCollection query(@Nonnull final QueryInfo queryInfo) throws Exception {
		QueryOptimizer optimizer = this.borrowOptimizer();
		if (optimizer == null) {
			return this.directQuery(queryInfo);
		} else {
			PartialCollection partialCollection = optimizer.query(this, queryInfo);
			this.returnOptimizer(optimizer);
			return partialCollection;
		}
	}

	/**
	 * <h3 class="en-US">Query total record count</h3>
	 * <h3 class="zh-CN">查询总记录数</h3>
	 *
	 * @param queryInfo <span class="en-US">Query record information</span>
	 *                  <span class="zh-CN">数据检索信息</span>
	 * @return <span class="en-US">Total record count</span>
	 * <span class="zh-CN">总记录条数</span>
	 * @throws SQLException <span class="en-US">An error occurred during execution</span>
	 *                      <span class="zh-CN">执行过程中出错</span>
	 */
	public Long queryTotal(@Nonnull final QueryInfo queryInfo) throws Exception {
		QueryOptimizer optimizer = this.borrowOptimizer();
		if (optimizer == null) {
			return this.directQueryTotal(queryInfo);
		} else {
			Long totalCount = optimizer.queryTotal(this, queryInfo);
			this.returnOptimizer(optimizer);
			return totalCount;
		}
	}

	/**
	 * <h3 class="en-US">Destroy current data source</h3>
	 * <h3 class="zh-CN">销毁当前数据源</h3>
	 */
	public synchronized void close() {
		this.scheduledExecutorService.shutdown();
		this.optimizersPool.clear();
		if (DDLType.CREATE_DROP.equals(this.ddlType)) {
			for (final BaseSchema<?> schema : this.registeredSchemas.values()) {
				try {
					schema.dropTables(DropOption.CASCADE);
				} catch (Exception e) {
					LOGGER.error("Drop_Table_Error");
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Stack_Message_Error", e);
					}
				}
			}
		}
		if (DDLType.CREATE_TRUNCATE.equals(this.ddlType)) {
			for (final BaseSchema<?> schema : this.registeredSchemas.values()) {
				try {
					schema.truncateTables();
				} catch (Exception e) {
					LOGGER.error("Truncate_Table_Error");
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Stack_Message_Error", e);
					}
				}
			}
		}

		this.registeredSchemas.forEach((name, schema) -> {
			if (this.jmxEnabled) {
				JMXUtils.unregister(JMX_OBJECT_NAME_PREFIX + name);
			}
			schema.close();
		});
		this.registeredSchemas.clear();
		if (this.jmxEnabled) {
			JMXUtils.unregister(this);
		}
		this.initialized = Boolean.FALSE;
		this.defaultName = Globals.DEFAULT_VALUE_STRING;
		TableManager.destroy();
	}

	/**
	 * <h3 class="en-US">The scheduling method is used to maintain the query optimizer object pool</h3>
	 * <h3 class="zh-CN">调度方法用于维护查询优化器对象池</h3>
	 */
	private void schedule() {
		if (this.scheduleRunning) {
			return;
		}

		this.scheduleRunning = Boolean.TRUE;
		if (StringUtils.isEmpty(this.optimizerName) || !REGISTERED_OPTIMIZERS.containsKey(this.optimizerName)) {
			if (!this.optimizersPool.isEmpty()) {
				this.optimizersPool.clear();
			}
		} else {
			Class<?> optimizerClass = REGISTERED_OPTIMIZERS.get(this.optimizerName);
			this.optimizersPool.removeIf(queryOptimizer -> !queryOptimizer.getClass().equals(optimizerClass));

			while (this.optimizersPool.size() < this.poolSize) {
				this.optimizersPool.offer(newOptimizer());
			}

			while (this.poolSize < this.optimizersPool.size()) {
				this.optimizersPool.poll();
			}
		}
		this.scheduleRunning = Boolean.FALSE;
	}

	/**
	 * <h3 class="en-US">Get the query optimizer implementation class instance object</h3>
	 * <h3 class="zh-CN">获取查询优化器实例对象</h3>
	 *
	 * @return <span class="en-US">Query optimizer implementation class instance object</span>
	 * <span class="zh-CN">查询优化器实现类实例对象</span>
	 */
	private QueryOptimizer borrowOptimizer() {
		QueryOptimizer optimizer = this.optimizersPool.poll();
		if (optimizer == null) {
			optimizer = newOptimizer();
		}
		return optimizer;
	}

	/**
	 * <h3 class="en-US">Return the query optimizer implementation class instance object</h3>
	 * <h3 class="zh-CN">归还查询优化器实例对象</h3>
	 *
	 * @param optimizer <span class="en-US">Query optimizer implementation class instance object</span>
	 *                  <span class="zh-CN">查询优化器实现类实例对象</span>
	 */
	private void returnOptimizer(@Nonnull final QueryOptimizer optimizer) {
		optimizer.reset();
		if (this.optimizersPool.size() < this.poolSize) {
			this.optimizersPool.offer(optimizer);
		}
	}

	/**
	 * <h3 class="en-US">Initialize the query optimizer implementation class instance object</h3>
	 * <h3 class="zh-CN">初始化查询优化器实现类实例对象</h3>
	 *
	 * @return <span class="en-US">Query optimizer implementation class instance object</span>
	 * <span class="zh-CN">查询优化器实现类实例对象</span>
	 */
	private QueryOptimizer newOptimizer() {
		return (QueryOptimizer) Optional.ofNullable(this.optimizerName)
				.filter(StringUtils::notBlank)
				.filter(REGISTERED_OPTIMIZERS::containsKey)
				.map(REGISTERED_OPTIMIZERS::get)
				.map(ObjectUtils::newInstance)
				.orElse(null);
	}

	/**
	 * <h3 class="en-US">Retrieve target data schema</h3>
	 * <h3 class="zh-CN">获取目标数据源</h3>
	 *
	 * @param queryFrom <span class="en-US">Query from information</span>
	 *                  <span class="zh-CN">查询来源信息</span>
	 * @return <span class="en-US">Data schema instance object</span>
	 * <span class="zh-CN">数据源实例对象</span>
	 * @throws SQLException <span class="en-US">If data schema not found</span>
	 *                      <span class="zh-CN">如果数据源未找到</span>
	 */
	private BaseSchema<?> retrieveSchema(@Nonnull final QueryFrom queryFrom) throws SQLException {
		if (queryFrom instanceof FromTable) {
			return this.retrieveSchema(this.tableManager.define(((FromTable) queryFrom).getTableName()).getSchemaName());
		} else if (queryFrom instanceof FromSubQuery) {
			return this.retrieveSchema(((FromSubQuery) queryFrom).getQueryData().getQueryFrom());
		} else {
			throw new MultilingualSQLException(0x00DB00000032L);
		}
	}

	/**
	 * <h3 class="en-US">Retrieve target data schema</h3>
	 * <h3 class="zh-CN">获取目标数据源</h3>
	 *
	 * @param schemaName <span class="en-US">Data schema name</span>
	 *                   <span class="zh-CN">数据源名称</span>
	 * @return <span class="en-US">Data schema instance object</span>
	 * <span class="zh-CN">数据源实例对象</span>
	 * @throws SQLException <span class="en-US">If data schema not found</span>
	 *                      <span class="zh-CN">如果数据源未找到</span>
	 */
	@Nonnull
	private BaseSchema<?> retrieveSchema(final String schemaName) throws SQLException {
		this.initialize();
		BaseSchema<?> baseSchema =
				this.registeredSchemas.get(StringUtils.isEmpty(schemaName) ? this.defaultName : schemaName);
		if (baseSchema == null) {
			throw new MultilingualSQLException(0x00DB00000032L, schemaName);
		}
		return baseSchema;
	}

	/**
	 * <h3 class="en-US">Register schema configure</h3>
	 * <h3 class="zh-CN">注册配置信息</h3>
	 *
	 * @param schemaConfig <span class="en-US">Data source configure information</span>
	 *                     <span class="zh-CN">数据源配置信息</span>
	 * @throws Exception <span class="en-US">Database server information hasn't found or sharding configuration error</span>
	 *                   <span class="zh-CN">数据库服务器信息未找到或分片配置出错</span>
	 */
	private void register(@Nonnull final SchemaConfig schemaConfig) throws Exception {
		if (this.registeredSchemas.containsKey(schemaConfig.getSchemaName())) {
			BaseSchema<?> schema = this.registeredSchemas.get(schemaConfig.getSchemaName());
			if (schema.match(schemaConfig.getLastModified())) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Registered_Data_Source", schemaConfig.getSchemaName());
				}
				return;
			}
			schema.close();
			this.registeredSchemas.remove(schemaConfig.getSchemaName());
		}
		BaseSchema<?> schema;
		if (schemaConfig instanceof DistributeSchemaConfig) {
			schema = new DistributeSchema((DistributeSchemaConfig) schemaConfig);
		} else if (schemaConfig instanceof JdbcSchemaConfig) {
			schema = new JdbcSchema((JdbcSchemaConfig) schemaConfig);
		} else if (schemaConfig instanceof RemoteSchemaConfig) {
			schema = new RemoteSchema((RemoteSchemaConfig) schemaConfig);
		} else {
			throw new MultilingualSQLException(0x00DB00000031L, BeanUtils.objectToString(schemaConfig, StringType.JSON));
		}
		this.registeredSchemas.put(schemaConfig.getSchemaName(), schema);
		if (schemaConfig.isDefaultSchema()) {
			if (StringUtils.notBlank(this.defaultName)) {
				LOGGER.error("Override_Default_Schema", this.defaultName, schemaConfig.getSchemaName());
			}
			this.defaultName = schemaConfig.getSchemaName();
		}
		if (this.jmxEnabled) {
			JMXUtils.register(JMX_OBJECT_NAME_PREFIX + schemaConfig.getSchemaName(), schema);
		}
	}

	/**
	 * <h3 class="en-US">Initialize registered data source</h3>
	 * <h3 class="zh-CN">初始化已注册的数据源</h3>
	 */
	private void initialize() {
		if (this.initialized) {
			return;
		}
		for (BaseSchema<?> schema : this.registeredSchemas.values()) {
			if (!schema.isInitialized()) {
				schema.initialize();
			}
		}
		this.initialized = Boolean.TRUE;
	}

	@Override
	public String getDefaultSchema() {
		return this.defaultName;
	}

	@Override
	public void defaultSchema(final String schema) {
		if (this.registeredSchemas.containsKey(schema)) {
			this.defaultName = schema;
		}
	}

	@Override
	public boolean isJmxEnabled() {
		return this.jmxEnabled;
	}

	@Override
	public void jmxEnabled(final boolean enabled) {
		if (this.jmxEnabled && !enabled) {
			JMXUtils.unregister(this);
			this.registeredSchemas.keySet()
					.forEach(name -> JMXUtils.unregister(JMX_OBJECT_NAME_PREFIX + name));
		} else if (!this.jmxEnabled && enabled) {
			JMXUtils.register(this);
			this.registeredSchemas.forEach((name, schema) ->
					JMXUtils.register(JMX_OBJECT_NAME_PREFIX + name, schema));
		}
		this.jmxEnabled = enabled;
	}

	@Override
	public DDLType getDDLType() {
		return this.ddlType;
	}

	@Override
	public void ddlType(final DDLType ddlType) {
		this.ddlType = ddlType;
	}

	@Override
	public boolean isInitialized() {
		return this.initialized;
	}

	private static final class BrainDataSourceHolder {
		private static BrainDataSource INSTANCE = null;

		static synchronized BrainDataSource getInstance() {
			if (INSTANCE == null) {
				INSTANCE = new BrainDataSource();
			}
			return INSTANCE;
		}

		static synchronized void destroy() {
			if (INSTANCE != null) {
				INSTANCE.close();
				INSTANCE = null;
			}
		}
	}
}
