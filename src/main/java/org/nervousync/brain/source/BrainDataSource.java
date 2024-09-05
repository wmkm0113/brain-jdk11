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
import org.nervousync.annotations.jmx.Monitor;
import org.nervousync.brain.configs.schema.SchemaConfig;
import org.nervousync.brain.configs.schema.impl.DistributeSchemaConfig;
import org.nervousync.brain.configs.schema.impl.JdbcSchemaConfig;
import org.nervousync.brain.configs.schema.impl.RemoteSchemaConfig;
import org.nervousync.brain.configs.transactional.TransactionalConfig;
import org.nervousync.brain.defines.ShardingDefine;
import org.nervousync.brain.defines.TableDefine;
import org.nervousync.brain.enumerations.ddl.DDLType;
import org.nervousync.brain.enumerations.ddl.DropOption;
import org.nervousync.brain.enumerations.query.LockOption;
import org.nervousync.brain.query.QueryInfo;
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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
	private final Hashtable<String, BaseSchema> registeredSchemas;

	/**
	 * <h3 class="en-US">Default constructor method for data source</h3>
	 * <h3 class="zh-CN">数据源的默认构造方法</h3>
	 *
	 * @param jmxEnabled <span class="en-US">Enable the JMX monitor</span>
	 *                   <span class="zh-CN">开启JMX监控</span>
	 * @param lazyInit   <span class="en-US">Perform initialization operations when using</span>
	 *                   <span class="zh-CN">使用时再执行初始化操作</span>
	 * @param ddlType    <span class="en-US">Enumeration value of DDL operate</span>
	 *                   <span class="zh-CN">操作类型枚举值</span>
	 */
	BrainDataSource(final boolean jmxEnabled, final boolean lazyInit, final DDLType ddlType) {
		this.registeredSchemas = new Hashtable<>();
		this.ddlType = (ddlType == null) ? DDLType.NONE : ddlType;
		this.lazyInit = lazyInit;
		this.jmxEnabled(jmxEnabled);
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
		BaseSchema schema;
		if (schemaConfig instanceof DistributeSchemaConfig) {
			schema = new DistributeSchema((DistributeSchemaConfig) schemaConfig);
		} else if (schemaConfig instanceof JdbcSchemaConfig) {
			schema = new JdbcSchema((JdbcSchemaConfig) schemaConfig);
		} else if (schemaConfig instanceof RemoteSchemaConfig) {
			schema = new RemoteSchema((RemoteSchemaConfig) schemaConfig);
		} else {
			throw new Exception("Unknown schema configure: " + schemaConfig.toFormattedJson());
		}
		this.registeredSchemas.put(schemaConfig.getSchemaName(), schema);
		if (schemaConfig.isDefaultSchema()) {
			if (StringUtils.notBlank(this.defaultName)) {
				LOGGER.error("", this.defaultName, schemaConfig.getSchemaName());
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
	 * <h3 class="en-US">Initialize data table</h3>
	 * <h3 class="zh-CN">初始化数据表</h3>
	 *
	 * @param tableDefine <span class="en-US">Table define information</span>
	 *                    <span class="zh-CN">数据表定义信息</span>
	 * @param database    <span class="en-US">Database sharding configuration information</span>
	 *                    <span class="zh-CN">数据库分片配置信息</span>
	 * @param table       <span class="en-US">Data table sharding configuration information</span>
	 *                    <span class="zh-CN">数据表分片配置信息</span>
	 * @param schemaNames <span class="en-US">Data schema name array</span>
	 *                    <span class="zh-CN">数据源名称数组</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public void initTable(@Nonnull final TableDefine tableDefine, final ShardingDefine<?> database,
	                      final ShardingDefine<?> table, @Nonnull final String... schemaNames) throws Exception {
		for (String schemaName : schemaNames) {
			if (!this.registeredSchemas.containsKey(schemaName)) {
				throw new Exception("Not found schema name: " + schemaName);
			}
			this.registeredSchemas.get(schemaName).initTable(this.ddlType, tableDefine, database, table);
		}
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
		for (final BaseSchema schema : this.registeredSchemas.values()) {
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
		for (final BaseSchema schema : this.registeredSchemas.values()) {
			schema.endTransactional();
		}
	}

	/**
	 * <h3 class="en-US">Rollback transactional</h3>
	 * <h3 class="zh-CN">回滚事务</h3>
	 *
	 * @throws Exception <span class="en-US">If an error occurs during execution</span>
	 *                   <span class="zh-CN">如果执行过程中出错</span>
	 */
	public void rollback() throws Exception {
		for (final BaseSchema schema : this.registeredSchemas.values()) {
			schema.rollback();
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
		for (final BaseSchema schema : this.registeredSchemas.values()) {
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
		for (final BaseSchema schema : this.registeredSchemas.values()) {
			schema.truncateTables();
		}
	}

	/**
	 * <h3 class="en-US">Truncate data table</h3>
	 * <h3 class="zh-CN">清空数据表</h3>
	 *
	 * @param tableDefine <span class="en-US">Table define information</span>
	 *                    <span class="zh-CN">数据表定义信息</span>
	 * @param schemaNames <span class="en-US">Data schema name array</span>
	 *                    <span class="zh-CN">数据源名称数组</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public void truncateTable(@Nonnull final TableDefine tableDefine, @Nonnull final String... schemaNames)
			throws Exception {
		for (String schemaName : schemaNames) {
			if (!this.registeredSchemas.containsKey(schemaName)) {
				throw new Exception("Not found schema name: " + schemaName);
			}
			this.registeredSchemas.get(schemaName).truncateTable(tableDefine);
		}
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
		for (final BaseSchema schema : this.registeredSchemas.values()) {
			schema.dropTables(dropOption);
		}
	}

	/**
	 * <h3 class="en-US">Drop data table</h3>
	 * <h3 class="zh-CN">删除数据表</h3>
	 *
	 * @param tableDefine <span class="en-US">Table define information</span>
	 *                    <span class="zh-CN">数据表定义信息</span>
	 * @param dropOption  <span class="en-US">Cascading delete options</span>
	 *                    <span class="zh-CN">级联删除选项</span>
	 * @param schemaNames <span class="en-US">Data schema name array</span>
	 *                    <span class="zh-CN">数据源名称数组</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public void dropTable(@Nonnull final TableDefine tableDefine, @Nonnull final DropOption dropOption,
	                      @Nonnull final String... schemaNames) throws Exception {
		for (String schemaName : schemaNames) {
			if (!this.registeredSchemas.containsKey(schemaName)) {
				throw new Exception("Not found schema name: " + schemaName);
			}
			this.registeredSchemas.get(schemaName).dropTable(tableDefine, dropOption);
		}
	}

	/**
	 * <h3 class="en-US">Execute insert record command</h3>
	 * <h3 class="zh-CN">执行插入数据命令</h3>
	 *
	 * @param schemaName  <span class="en-US">Data schema name</span>
	 *                    <span class="zh-CN">数据源名称</span>
	 * @param tableDefine <span class="en-US">Table define information</span>
	 *                    <span class="zh-CN">数据表定义信息</span>
	 * @param dataMap     <span class="en-US">Insert data mapping</span>
	 *                    <span class="zh-CN">写入数据映射表</span>
	 * @return <span class="en-US">Primary key value mapping table generated by database</span>
	 * <span class="zh-CN">数据库生成的主键值映射表</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public Map<String, Serializable> insert(@Nonnull final String schemaName, @Nonnull final TableDefine tableDefine,
	                                        @Nonnull final Map<String, Serializable> dataMap) throws Exception {
		if (!this.registeredSchemas.containsKey(schemaName)) {
			throw new Exception("Not found schema name: " + schemaName);
		}
		return this.registeredSchemas.get(schemaName).insert(tableDefine, dataMap);
	}

	/**
	 * <h3 class="en-US">Execute retrieve record command</h3>
	 * <h3 class="zh-CN">执行数据唯一检索命令</h3>
	 *
	 * @param schemaName  <span class="en-US">Data schema name</span>
	 *                    <span class="zh-CN">数据源名称</span>
	 * @param tableDefine <span class="en-US">Table define information</span>
	 *                    <span class="zh-CN">数据表定义信息</span>
	 * @param columns     <span class="en-US">Query column names</span>
	 *                    <span class="zh-CN">查询数据列名</span>
	 * @param filterMap   <span class="en-US">Retrieve filter mapping</span>
	 *                    <span class="zh-CN">查询条件映射表</span>
	 * @param forUpdate   <span class="en-US">Retrieve result using for update record</span>
	 *                    <span class="zh-CN">检索结果用于更新记录</span>
	 * @param lockOption  <span class="en-US">Query record lock option</span>
	 *                    <span class="zh-CN">查询记录锁定选项</span>
	 * @return <span class="en-US">Data mapping table of retrieved records</span>
	 * <span class="zh-CN">检索到记录的数据映射表</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public Map<String, String> retrieve(@Nonnull final String schemaName, @Nonnull final TableDefine tableDefine,
	                                    final String columns, @Nonnull final Map<String, Serializable> filterMap,
	                                    final boolean forUpdate, final LockOption lockOption) throws Exception {
		if (!this.registeredSchemas.containsKey(schemaName)) {
			throw new Exception("Not found schema name: " + schemaName);
		}
		return this.registeredSchemas.get(schemaName)
				.retrieve(tableDefine, columns, filterMap, forUpdate, lockOption);
	}

	/**
	 * <h3 class="en-US">Execute update record command</h3>
	 * <h3 class="zh-CN">执行更新记录命令</h3>
	 *
	 * @param schemaName  <span class="en-US">Data schema name</span>
	 *                    <span class="zh-CN">数据源名称</span>
	 * @param tableDefine <span class="en-US">Table define information</span>
	 *                    <span class="zh-CN">数据表定义信息</span>
	 * @param dataMap     <span class="en-US">Update data mapping</span>
	 *                    <span class="zh-CN">更新数据映射表</span>
	 * @param filterMap   <span class="en-US">Update filter mapping</span>
	 *                    <span class="zh-CN">更新条件映射表</span>
	 * @return <span class="en-US">Updated records count</span>
	 * <span class="zh-CN">更新记录条数</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public int update(@Nonnull final String schemaName, @Nonnull final TableDefine tableDefine,
	                  @Nonnull final Map<String, Serializable> dataMap,
	                  @Nonnull final Map<String, Serializable> filterMap) throws Exception {
		if (!this.registeredSchemas.containsKey(schemaName)) {
			throw new Exception("Not found schema name: " + schemaName);
		}
		return this.registeredSchemas.get(schemaName).update(tableDefine, dataMap, filterMap);
	}

	/**
	 * <h3 class="en-US">Execute delete record command</h3>
	 * <h3 class="zh-CN">执行删除记录命令</h3>
	 *
	 * @param schemaName  <span class="en-US">Data schema name</span>
	 *                    <span class="zh-CN">数据源名称</span>
	 * @param tableDefine <span class="en-US">Table define information</span>
	 *                    <span class="zh-CN">数据表定义信息</span>
	 * @param filterMap   <span class="en-US">Delete filter mapping</span>
	 *                    <span class="zh-CN">删除条件映射表</span>
	 * @return <span class="en-US">Deleted records count</span>
	 * <span class="zh-CN">删除记录条数</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public int delete(@Nonnull final String schemaName, @Nonnull final TableDefine tableDefine,
	                  @Nonnull final Map<String, Serializable> filterMap) throws Exception {
		if (!this.registeredSchemas.containsKey(schemaName)) {
			throw new Exception("Not found schema name: " + schemaName);
		}
		return this.registeredSchemas.get(schemaName).delete(tableDefine, filterMap);
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
	public List<Map<String, String>> query(@Nonnull final QueryInfo queryInfo) throws Exception {
		if (!this.registeredSchemas.containsKey(queryInfo.getSchemaName())) {
			throw new Exception("Not found schema name: " + queryInfo.getSchemaName());
		}
		return this.registeredSchemas.get(queryInfo.getSchemaName()).query(queryInfo);
	}

	/**
	 * <h3 class="en-US">Execute query commands for data updates</h3>
	 * <h3 class="zh-CN">执行用于数据更新的查询命令</h3>
	 *
	 * @param schemaName    <span class="en-US">Data schema name</span>
	 *                      <span class="zh-CN">数据源名称</span>
	 * @param tableDefine   <span class="en-US">Table define information</span>
	 *                      <span class="zh-CN">数据表定义信息</span>
	 * @param conditionList <span class="en-US">Query condition instance list</span>
	 *                      <span class="zh-CN">查询条件实例对象列表</span>
	 * @param lockOption    <span class="en-US">Query record lock option</span>
	 *                      <span class="zh-CN">查询记录锁定选项</span>
	 * @return <span class="en-US">List of data mapping tables for retrieved records</span>
	 * <span class="zh-CN">检索到记录的数据映射表列表</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public List<Map<String, String>> queryForUpdate(
			@Nonnull final String schemaName, @Nonnull final TableDefine tableDefine,
			final List<Condition> conditionList, final LockOption lockOption) throws Exception {
		if (!this.registeredSchemas.containsKey(schemaName)) {
			throw new Exception("Not found schema name: " + schemaName);
		}
		return this.registeredSchemas.get(schemaName).queryForUpdate(tableDefine, conditionList, lockOption);
	}

	/**
	 * <h3 class="en-US">Destroy current data source</h3>
	 * <h3 class="zh-CN">销毁当前数据源</h3>
	 */
	void close() {
		if (DDLType.CREATE_DROP.equals(this.ddlType)) {
			for (final BaseSchema schema : this.registeredSchemas.values()) {
				try {
					schema.dropTables(DropOption.CASCADE);
				} catch (Exception e) {
					LOGGER.error("", e);
				}
			}
		}
		if (DDLType.CREATE_TRUNCATE.equals(this.ddlType)) {
			for (final BaseSchema schema : this.registeredSchemas.values()) {
				try {
					schema.truncateTables();
				} catch (Exception e) {
					LOGGER.error("", e);
				}
			}
		}
		Iterator<Map.Entry<String, BaseSchema>> iterator = this.registeredSchemas.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, BaseSchema> entry = iterator.next();
			if (this.jmxEnabled) {
				ObjectUtils.unregisterMBean(JMX_OBJECT_NAME_PREFIX + entry.getKey());
			}
			try {
				entry.getValue().close();
			} catch (Exception e) {
				LOGGER.error("", e);
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
		for (BaseSchema schema : this.registeredSchemas.values()) {
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
