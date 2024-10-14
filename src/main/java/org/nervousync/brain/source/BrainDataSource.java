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
import org.nervousync.brain.commons.BrainCommons;
import org.nervousync.brain.configs.Configure;
import org.nervousync.brain.configs.schema.SchemaConfig;
import org.nervousync.brain.configs.schema.impl.DistributeSchemaConfig;
import org.nervousync.brain.configs.schema.impl.JdbcSchemaConfig;
import org.nervousync.brain.configs.schema.impl.RemoteSchemaConfig;
import org.nervousync.brain.configs.transactional.TransactionalConfig;
import org.nervousync.brain.defines.ShardingDefine;
import org.nervousync.brain.defines.TableDefine;
import org.nervousync.brain.enumerations.ddl.DDLType;
import org.nervousync.brain.enumerations.ddl.DropOption;
import org.nervousync.brain.exceptions.sql.MultilingualSQLException;
import org.nervousync.brain.query.QueryInfo;
import org.nervousync.brain.query.builder.QueryBuilder;
import org.nervousync.brain.query.condition.Condition;
import org.nervousync.brain.schemas.BaseSchema;
import org.nervousync.brain.schemas.distribute.DistributeSchema;
import org.nervousync.brain.schemas.jdbc.JdbcSchema;
import org.nervousync.brain.schemas.remote.RemoteSchema;
import org.nervousync.commons.Globals;
import org.nervousync.utils.LoggerUtils;
import org.nervousync.utils.ObjectUtils;
import org.nervousync.utils.StringUtils;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;

/**
 * <h2 class="en-US">Nervousync brain data source</h2>
 * <h2 class="zh-CN">Nervousync 大脑数据源</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision : 1.0.0 $ $Date: Nov 12, 2020 12:20:49 $
 */
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
	 * <span class="en-US">Perform initialization operations when using</span>
	 * <span class="zh-CN">使用时再执行初始化操作</span>
	 */
	private final boolean lazyInit;
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
	 * <span class="en-US">Registered data source instance mapping table</span>
	 * <span class="zh-CN">注册的数据源实例映射表</span>
	 */
	private final Hashtable<String, BaseSchema<?>> registeredSchemas;
	/**
	 * <span class="en-US">Registered data table define information mapping table</span>
	 * <span class="zh-CN">注册的数据表定义信息</span>
	 */
	private final Hashtable<String, TableDefine> registeredTables;
	/**
	 * <span class="en-US">Mapping table of data table identification codes and table names</span>
	 * <span class="zh-CN">数据表识别代码与表名的映射表</span>
	 */
	private final Hashtable<String, String> identifyCodeMapping;

	/**
	 * <h3 class="en-US">Default constructor method for data source</h3>
	 * <h3 class="zh-CN">数据源的默认构造方法</h3>
	 *
	 * @param configure <span class="en-US">Data source configure information instance object</span>
	 *                  <span class="zh-CN">数据源配置信息实例对象</span>
	 */
	BrainDataSource(final Configure configure) {
		this.registeredSchemas = new Hashtable<>();
		this.registeredTables = new Hashtable<>();
		this.identifyCodeMapping = new Hashtable<>();
		this.ddlType = (configure.getDdlType() == null) ? DDLType.NONE : configure.getDdlType();
		this.lazyInit = configure.isLazyInitialize();
		this.jmxEnabled(configure.isJmxMonitor());
		for (SchemaConfig schemaConfig : configure.getSchemaConfigs()) {
			try {
				this.register(schemaConfig);
			} catch (Exception e) {
				LOGGER.error("Register_Schema_Config_Error", schemaConfig.toFormattedJson());
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Stack_Message_Error", e);
				}
			}
		}
	}

	/**
	 * <h3 class="en-US">Register schema configure</h3>
	 * <h3 class="zh-CN">注册配置信息</h3>
	 *
	 * @param schemaConfig <span class="en-US">Data source configure information</span>
	 *                     <span class="zh-CN">数据源配置信息</span>
	 * @throws Exception <span class="en-US">Database server information not found or sharding configuration error</span>
	 *                   <span class="zh-CN">数据库服务器信息未找到或分片配置出错</span>
	 */
	public void register(@Nonnull final SchemaConfig schemaConfig) throws Exception {
		if (this.registeredSchemas.containsKey(schemaConfig.getSchemaName())) {
			LOGGER.error("Registered_Data_Source", schemaConfig.getSchemaName());
			return;
		}
		BaseSchema<?> schema;
		if (schemaConfig instanceof DistributeSchemaConfig) {
			schema = new DistributeSchema((DistributeSchemaConfig) schemaConfig);
		} else if (schemaConfig instanceof JdbcSchemaConfig) {
			schema = new JdbcSchema((JdbcSchemaConfig) schemaConfig);
		} else if (schemaConfig instanceof RemoteSchemaConfig) {
			schema = new RemoteSchema((RemoteSchemaConfig) schemaConfig);
		} else {
			throw new MultilingualSQLException(0x00DB00000031L, schemaConfig.toFormattedJson());
		}
		this.registeredSchemas.put(schemaConfig.getSchemaName(), schema);
		if (schemaConfig.isDefaultSchema()) {
			if (StringUtils.notBlank(this.defaultName)) {
				LOGGER.error("Override_Default_Schema", this.defaultName, schemaConfig.getSchemaName());
			}
			this.defaultName = schemaConfig.getSchemaName();
		}
		if (!this.lazyInit) {
			this.initialize();
		}
		if (this.jmxEnabled) {
			ObjectUtils.registerMBean(JMX_OBJECT_NAME_PREFIX + schemaConfig.getSchemaName(), schema);
		}
	}

	/**
	 * <h3 class="en-US">Convert default value to string</h3>
	 * <h3 class="zh-CN">转换默认值为字符串</h3>
	 *
	 * @param schemaName <span class="en-US">Data schema name</span>
	 *                   <span class="zh-CN">数据源名称</span>
	 * @param jdbcType   <span class="en-US">JDBC type code</span>
	 *                   <span class="zh-CN">JDBC类型代码</span>
	 * @param length     <span class="en-US">Data column length</span>
	 *                   <span class="zh-CN">数据列长度</span>
	 * @param precision  <span class="en-US">The precision for a decimal (exact numeric) column</span>
	 *                   <span class="zh-CN">小数（精确数字）列的精度</span>
	 * @param scale      <span class="en-US">The scale for a decimal (exact numeric) column</span>
	 *                   <span class="zh-CN">小数（精确数字）列的比例</span>
	 * @param object     <span class="en-US">Default value instance object</span>
	 *                   <span class="zh-CN">默认值实例对象</span>
	 * @return <span class="en-US">Default value string</span>
	 * <span class="zh-CN">默认值字符串</span>
	 */
	public String defaultValue(final String schemaName, final int jdbcType, final int length, final int precision,
	                           final int scale, final Object object) {
		try {
			return Optional.of(this.retrieveSchema(schemaName))
					.map(schema -> schema.defaultValue(jdbcType, length, precision, scale, object))
					.orElse(Globals.DEFAULT_VALUE_STRING);
		} catch (SQLException e) {
			return Globals.DEFAULT_VALUE_STRING;
		}
	}

	/**
	 * <h3 class="en-US">Initialize data table</h3>
	 * <h3 class="zh-CN">初始化数据表</h3>
	 *
	 * @param tableDefine <span class="en-US">Table define information</span>
	 *                    <span class="zh-CN">数据表定义信息</span>
	 * @param database    <span class="en-US">Database sharding configuration information</span>
	 *                    <span class="zh-CN">数据库分片配置信息</span>
	 * @param table       <span class="en-US">Data table sharding configuration information</span>
	 *                    <span class="zh-CN">数据表分片配置信息</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public void initTable(@Nonnull final TableDefine tableDefine, final ShardingDefine<?> database,
	                      final ShardingDefine<?> table) throws Exception {
		this.retrieveSchema(tableDefine.getSchemaName()).initTable(this.ddlType, tableDefine, database, table);
		this.registeredTables.put(tableDefine.getTableName(), tableDefine);
		this.identifyCodeMapping.put(BrainCommons.identifyCode(tableDefine.getTableName()), tableDefine.getTableName());
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
	 * <h3 class="en-US">Truncate all data table</h3>
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
	public void truncateTable(@Nonnull final String tableName)
			throws Exception {
		TableDefine tableDefine = this.checkRegister(tableName);
		this.retrieveSchema(tableDefine.getSchemaName()).truncateTable(tableDefine);
	}

	/**
	 * <h3 class="en-US">Drop all data table</h3>
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
		TableDefine tableDefine = this.checkRegister(tableName);
		this.retrieveSchema(tableDefine.getSchemaName()).dropTable(tableDefine, dropOption);
	}

	/**
	 * <h3 class="en-US">Execute lock record command</h3>
	 * <h3 class="zh-CN">执行数据锁定命令</h3>
	 *
	 * @param tableName <span class="en-US">Data table name</span>
	 *                  <span class="zh-CN">数据表名称</span>
	 * @param filterMap <span class="en-US">Filter data mapping</span>
	 *                  <span class="zh-CN">查询数据映射表</span>
	 * @return <span class="en-US">Primary key value mapping table generated by database</span>
	 * <span class="zh-CN">数据库生成的主键值映射表</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public boolean lockRecord(@Nonnull final String tableName, @Nonnull final Map<String, Object> filterMap)
			throws Exception {
		TableDefine tableDefine = this.checkRegister(tableName);
		return this.retrieveSchema(tableDefine.getSchemaName()).lockRecord(tableDefine, filterMap);
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
		TableDefine tableDefine = this.checkRegister(tableName);
		return this.retrieveSchema(tableDefine.getSchemaName()).insert(tableDefine, dataMap);
	}

	/**
	 * <h3 class="en-US">Execute retrieve record command</h3>
	 * <h3 class="zh-CN">执行数据唯一检索命令</h3>
	 *
	 * @param tableName <span class="en-US">Data table name</span>
	 *                  <span class="zh-CN">数据表名称</span>
	 * @param columns   <span class="en-US">Query column names</span>
	 *                  <span class="zh-CN">查询数据列名</span>
	 * @param filterMap <span class="en-US">Retrieve filter mapping</span>
	 *                  <span class="zh-CN">查询条件映射表</span>
	 * @param forUpdate <span class="en-US">Retrieve result using for update record</span>
	 *                  <span class="zh-CN">检索结果用于更新记录</span>
	 * @return <span class="en-US">Data mapping table of retrieved records</span>
	 * <span class="zh-CN">检索到记录的数据映射表</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public Map<String, Object> retrieve(@Nonnull final String tableName, final String columns,
	                                    @Nonnull final Map<String, Object> filterMap, final boolean forUpdate)
			throws Exception {
		TableDefine tableDefine = this.checkRegister(tableName);
		return this.retrieveSchema(tableDefine.getSchemaName()).retrieve(tableDefine, columns, filterMap, forUpdate);
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
		TableDefine tableDefine = this.checkRegister(tableName);
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
		TableDefine tableDefine = this.checkRegister(tableName);
		return this.retrieveSchema(tableDefine.getSchemaName()).delete(tableDefine, filterMap);
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
	public List<Map<String, Object>> query(@Nonnull final QueryInfo queryInfo) throws Exception {
		String tableName = this.identifyCodeMapping.get(BrainCommons.identifyCode(queryInfo.getTableName()));
		if (StringUtils.isEmpty(tableName)) {
			throw new MultilingualSQLException(0x00DB00000034L, queryInfo.getTableName());
		}
		String schemaName = Optional.ofNullable(this.registeredTables.get(tableName))
				.map(TableDefine::getSchemaName)
				.orElseThrow(() -> new MultilingualSQLException(0x00DB00000034L, tableName));
		if (StringUtils.isEmpty(schemaName)) {
			schemaName = this.defaultName;
		}
		TableDefine tableDefine = this.checkRegister(schemaName, queryInfo.getTableName());
		return this.retrieveSchema(schemaName).query(tableDefine, queryInfo);
	}

	/**
	 * <h3 class="en-US">Execute query commands for data updates</h3>
	 * <h3 class="zh-CN">执行用于数据更新的查询命令</h3>
	 *
	 * @param tableName <span class="en-US">Data table name</span>
	 *                  <span class="zh-CN">数据表名称</span>
	 * @param columns   <span class="en-US">Query column names</span>
	 *                  <span class="zh-CN">查询数据列名</span>
	 * @param filterMap <span class="en-US">Retrieve filter mapping</span>
	 *                  <span class="zh-CN">查询条件映射表</span>
	 * @return <span class="en-US">List of data mapping tables for retrieved records</span>
	 * <span class="zh-CN">检索到记录的数据映射表列表</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public List<Map<String, Object>> query(@Nonnull final String tableName, final String columns,
	                                       final Map<String, Object> filterMap)
			throws Exception {
		QueryBuilder queryBuilder = QueryBuilder.newBuilder(tableName);
		for (String columnName : StringUtils.tokenizeToStringArray(columns, ",")) {
			if (StringUtils.notBlank(columnName.trim())) {
				queryBuilder.queryColumn(tableName, columnName.trim());
			}
		}
		filterMap.forEach((columnName, matchValue) -> queryBuilder.equalTo(tableName, columnName, matchValue));
		return this.query(queryBuilder.confirm());
	}

	/**
	 * <h3 class="en-US">Execute query commands for data updates</h3>
	 * <h3 class="zh-CN">执行用于数据更新的查询命令</h3>
	 *
	 * @param tableName     <span class="en-US">Data table name</span>
	 *                      <span class="zh-CN">数据表名称</span>
	 * @param conditionList <span class="en-US">Query condition instance list</span>
	 *                      <span class="zh-CN">查询条件实例对象列表</span>
	 * @param lockOption    <span class="en-US">Query record lock option</span>
	 *                      <span class="zh-CN">查询记录锁定选项</span>
	 * @return <span class="en-US">List of data mapping tables for retrieved records</span>
	 * <span class="zh-CN">检索到记录的数据映射表列表</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public List<Map<String, Object>> queryForUpdate(@Nonnull final String tableName,
	                                                final List<Condition> conditionList, final LockModeType lockOption)
			throws Exception {
		TableDefine tableDefine = this.checkRegister(tableName);
		return this.retrieveSchema(tableDefine.getSchemaName()).queryForUpdate(tableDefine, conditionList, lockOption);
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
	private BaseSchema<?> retrieveSchema(final String schemaName) throws SQLException {
		this.initialize();
		BaseSchema<?> baseSchema = StringUtils.isEmpty(schemaName)
				? this.registeredSchemas.get(this.defaultName)
				: this.registeredSchemas.get(schemaName);
		if (baseSchema == null) {
			throw new MultilingualSQLException(0x00DB00000032L, schemaName);
		}

		if (!baseSchema.isInitialized()) {
			baseSchema.initialize();
		}
		return baseSchema;
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
		TableDefine tableDefine = this.checkRegister(queryInfo.getTableName());
		return this.retrieveSchema(tableDefine.getSchemaName()).queryTotal(tableDefine, queryInfo);
	}

	/**
	 * <h3 class="en-US">Query total record count</h3>
	 * <h3 class="zh-CN">查询总记录数</h3>
	 *
	 * @param tableName <span class="en-US">Data table name</span>
	 *                  <span class="zh-CN">数据表名称</span>
	 * @param filterMap <span class="en-US">Retrieve filter mapping</span>
	 *                  <span class="zh-CN">查询条件映射表</span>
	 * @return <span class="en-US">Total record count</span>
	 * <span class="zh-CN">总记录条数</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public Long queryTotal(@Nonnull final String tableName, final Map<String, Serializable> filterMap) throws Exception {
		QueryBuilder queryBuilder = QueryBuilder.newBuilder(tableName);
		filterMap.forEach((columnName, matchValue) -> queryBuilder.equalTo(tableName, columnName, matchValue));
		return this.queryTotal(queryBuilder.confirm());
	}

	/**
	 * <h3 class="en-US">Get a list of non-lazy loading data column names for a given data table name</h3>
	 * <h3 class="zh-CN">获取给定数据表名的非懒加载数据列名列表</h3>
	 *
	 * @param tableName <span class="en-US">Data table name</span>
	 *                  <span class="zh-CN">数据表名称</span>
	 * @return <span class="en-US">Data column name list</span>
	 * <span class="zh-CN">数据列名列表</span>
	 * @throws SQLException <span class="en-US">An error occurred during execution</span>
	 *                      <span class="zh-CN">执行过程中出错</span>
	 */
	public List<String> queryColumns(final String tableName) throws SQLException {
		TableDefine tableDefine = this.checkRegister(tableName);
		List<String> columnList = new ArrayList<>();
		tableDefine.getColumnDefines()
				.stream()
				.filter(columnDefine -> !columnDefine.isLazyLoad())
				.forEach(columnDefine -> columnList.add(columnDefine.getColumnName()));
		return columnList;
	}

	/**
	 * <h3 class="en-US">Destroy current data source</h3>
	 * <h3 class="zh-CN">销毁当前数据源</h3>
	 */
	void close() {
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
		Iterator<Map.Entry<String, BaseSchema<?>>> iterator = this.registeredSchemas.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, BaseSchema<?>> entry = iterator.next();
			if (this.jmxEnabled) {
				ObjectUtils.unregisterMBean(JMX_OBJECT_NAME_PREFIX + entry.getKey());
			}
			try {
				entry.getValue().close();
			} catch (Exception e) {
				LOGGER.error("Close_DataSource_Error");
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Stack_Message_Error", e);
				}
			}
			iterator.remove();
		}
		if (this.jmxEnabled) {
			ObjectUtils.unregisterMBean(this);
		}
		this.initialized = Boolean.FALSE;
		this.defaultName = Globals.DEFAULT_VALUE_STRING;
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

	/**
	 * <h3 class="en-US">Check whether the data source and data table are registered</h3>
	 * <h3 class="zh-CN">检查数据源和数据表是否注册</h3>
	 *
	 * @param identifyCode <span class="en-US">Data table identify code</span>
	 *                     <span class="zh-CN">数据表识别代码</span>
	 * @throws SQLException <span class="en-US">The data source or data table is not registered</span>
	 *                      <span class="zh-CN">数据源或数据表未注册</span>
	 */
	private TableDefine checkRegister(@Nonnull final String identifyCode) throws SQLException {
		String tableName = this.identifyCodeMapping.getOrDefault(identifyCode, identifyCode);
		if (StringUtils.isEmpty(tableName)) {
			throw new MultilingualSQLException(0x00DB00000034L, tableName);
		}
		TableDefine tableDefine = this.registeredTables.get(tableName);
		if (tableDefine == null) {
			throw new MultilingualSQLException(0x00DB00000034L, tableName);
		}
		String schemaName = Optional.ofNullable(tableDefine.getSchemaName())
				.filter(StringUtils::notBlank)
				.orElse(this.defaultName);
		if (!this.registeredSchemas.containsKey(schemaName)) {
			throw new MultilingualSQLException(0x00DB00000032L, schemaName);
		}
		return tableDefine;
	}

	/**
	 * <h3 class="en-US">Check whether the data source and data table are registered</h3>
	 * <h3 class="zh-CN">检查数据源和数据表是否注册</h3>
	 *
	 * @param schemaName <span class="en-US">Data schema name</span>
	 *                   <span class="zh-CN">数据源名称</span>
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @throws SQLException <span class="en-US">The data source or data table is not registered</span>
	 *                      <span class="zh-CN">数据源或数据表未注册</span>
	 */
	private TableDefine checkRegister(@Nonnull final String schemaName, @Nonnull final String tableName) throws SQLException {
		if (!this.registeredSchemas.containsKey(schemaName)) {
			throw new MultilingualSQLException(0x00DB00000032L, schemaName);
		}
		return Optional.ofNullable(this.registeredTables.get(tableName))
				.orElseThrow(() -> new MultilingualSQLException(0x00DB00000034L, tableName));
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
			ObjectUtils.unregisterMBean(this);
			this.registeredSchemas.keySet()
					.forEach(name -> ObjectUtils.unregisterMBean(JMX_OBJECT_NAME_PREFIX + name));
		} else if (!this.jmxEnabled && enabled) {
			ObjectUtils.registerMBean(this);
			this.registeredSchemas.forEach((name, schema) ->
					ObjectUtils.registerMBean(JMX_OBJECT_NAME_PREFIX + name, schema));
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
}
