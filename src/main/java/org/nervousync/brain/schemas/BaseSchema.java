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

package org.nervousync.brain.schemas;

import jakarta.annotation.Nonnull;
import org.nervousync.brain.configs.auth.Authentication;
import org.nervousync.brain.configs.schema.SchemaConfig;
import org.nervousync.brain.configs.secure.TrustStore;
import org.nervousync.brain.configs.sharding.ShardingConfig;
import org.nervousync.brain.configs.transactional.TransactionalConfig;
import org.nervousync.brain.defines.ShardingDefine;
import org.nervousync.brain.defines.TableDefine;
import org.nervousync.brain.enumerations.ddl.DDLType;
import org.nervousync.brain.enumerations.ddl.DropOption;
import org.nervousync.brain.enumerations.query.ConditionType;
import org.nervousync.brain.enumerations.query.LockOption;
import org.nervousync.brain.enumerations.sharding.ShardingType;
import org.nervousync.brain.query.QueryInfo;
import org.nervousync.brain.query.condition.Condition;
import org.nervousync.brain.query.condition.impl.ColumnCondition;
import org.nervousync.brain.query.condition.impl.GroupCondition;
import org.nervousync.brain.query.param.AbstractParameter;
import org.nervousync.brain.query.param.impl.ConstantParameter;
import org.nervousync.commons.Globals;
import org.nervousync.utils.ClassUtils;
import org.nervousync.utils.LoggerUtils;
import org.nervousync.utils.StringUtils;

import java.io.Closeable;
import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Wrapper;
import java.util.*;

/**
 * <h2 class="en-US">Data source abstract implementation classes</h2>
 * <h2 class="zh-CN">数据源抽象实现类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Feb 18, 2019 10:15:08 $
 */
public abstract class BaseSchema implements Wrapper, Closeable, BaseSchemaMBean {

	/**
	 * <span class="en-US">Logger instance</span>
	 * <span class="zh-CN">日志实例</span>
	 */
	protected transient final LoggerUtils.Logger logger = LoggerUtils.getLogger(this.getClass());

	/**
	 * <span class="en-US">Identity authentication configuration information</span>
	 * <span class="zh-CN">身份认证配置信息</span>
	 */
	protected final Authentication authentication;
	/**
	 * <span class="en-US">Trust certificate store configuration information</span>
	 * <span class="zh-CN">信任证书库配置信息</span>
	 */
	protected final TrustStore trustStore;
	/**
	 * <span class="en-US">Low query timeout (Unit: milliseconds)</span>
	 * <span class="zh-CN">慢查询的临界时间（单位：毫秒）</span>
	 */
	private long lowQueryTimeout;
	/**
	 * <span class="en-US">Timeout value of connection validate</span>
	 * <span class="zh-CN">连接检查超时时间</span>
	 */
	private int validateTimeout;
	/**
	 * <span class="en-US">Timeout value of create connection</span>
	 * <span class="zh-CN">建立连接超时时间</span>
	 */
	private int connectTimeout;
	/**
	 * <span class="en-US">Data source support sharding</span>
	 * <span class="zh-CN">数据源是否支持分片</span>
	 */
	protected final boolean sharding;
	/**
	 * <span class="en-US">Default database sharding value</span>
	 * <span class="zh-CN">默认数据库分片值</span>
	 */
	protected final String shardingDefault;
	/**
	 * <span class="en-US">Sharding configure information mapping</span>
	 * <span class="zh-CN">分片配置信息映射表</span>
	 */
	protected final Hashtable<String, ShardingConfig> shardingConfigs = new Hashtable<>();
	/**
	 * <span class="en-US">Initialize status of data source</span>
	 * <span class="zh-CN">数据源初始化状态</span>
	 */
	protected boolean initialized = Boolean.FALSE;
	/**
	 * <span class="en-US">The transactional configure information used by the thread</span>
	 * <span class="zh-CN">线程使用的事务配置信息</span>
	 */
	protected final ThreadLocal<TransactionalConfig> txConfig = new ThreadLocal<>();

	/**
	 * <h3 class="en-US">Constructor method for data source abstract implementation classes</h3>
	 * <h3 class="zh-CN">数据源抽象实现类的构造方法</h3>
	 *
	 * @param schemaConfig <span class="en-US">Data source configure information</span>
	 *                     <span class="zh-CN">数据源配置信息</span>
	 * @throws SQLException <span class="en-US">Database server information not found or sharding configuration error</span>
	 *                      <span class="zh-CN">数据库服务器信息未找到或分片配置出错</span>
	 */
	protected BaseSchema(@Nonnull final SchemaConfig schemaConfig) throws SQLException {
		this.sharding = schemaConfig.isSharding();
		this.authentication = schemaConfig.getAuthentication();
		this.trustStore = schemaConfig.getTrustStore();
		this.lowQueryTimeout = schemaConfig.getLowQueryTimeout();
		this.validateTimeout = schemaConfig.getValidateTimeout();
		this.connectTimeout = schemaConfig.getConnectTimeout();
		if (this.sharding) {
			if (StringUtils.isEmpty(schemaConfig.getShardingDefault())) {
				throw new SQLException("Default sharding value is empty");
			}
			this.shardingDefault = schemaConfig.getShardingDefault();
		} else {
			this.shardingDefault = Globals.DEFAULT_VALUE_STRING;
		}
	}

	@Override
	public final <T> T unwrap(final Class<T> clazz) throws SQLException {
		try {
			return clazz.cast(this);
		} catch (ClassCastException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public final boolean isWrapperFor(final Class<?> clazz) {
		return ClassUtils.isAssignable(clazz, this.getClass());
	}

	@Override
	public final long getLowQueryTimeout() {
		return this.lowQueryTimeout;
	}

	@Override
	public final void lowQueryTimeout(final long timeout) {
		this.lowQueryTimeout = timeout;
	}

	@Override
	public final int getValidateTimeout() {
		return this.validateTimeout;
	}

	@Override
	public final void validateTimeout(final int timeout) {
		this.validateTimeout = timeout;
	}

	@Override
	public final int getConnectTimeout() {
		return this.connectTimeout;
	}

	@Override
	public final void connectTimeout(final int timeout) {
		this.connectTimeout = timeout;
	}

	@Override
	public final boolean isInitialized() {
		return this.initialized;
	}

	/**
	 * <h3 class="en-US">Calculate database sharding key</h3>
	 * <h3 class="zh-CN">计算数据库分片值</h3>
	 *
	 * @param tableName    <span class="en-US">Data table name</span>
	 *                     <span class="zh-CN">数据表明</span>
	 * @param parameterMap <span class="en-US">Columns data mapping</span>
	 *                     <span class="zh-CN">数据列信息映射表</span>
	 * @return <span class="en-US">Calculated sharding key result</span>
	 * <span class="zh-CN">分片计算结果值</span>
	 */
	protected final String shardingDatabase(final String tableName, final Map<String, Serializable> parameterMap) {
		if (!this.sharding) {
			return this.shardingDefault;
		}
		return Optional.ofNullable(this.shardingConfigs.get(tableName))
				.map(shardingConfig -> shardingConfig.shardingKey(ShardingType.DATABASE, parameterMap))
				.orElse(this.shardingDefault);
	}

	/**
	 * <h3 class="en-US">Calculate table sharding key</h3>
	 * <h3 class="zh-CN">计算数据表分片值</h3>
	 *
	 * @param tableName     <span class="en-US">Data table name</span>
	 *                      <span class="zh-CN">数据表明</span>
	 * @param conditionList <span class="en-US">Data column condition information list</span>
	 *                      <span class="zh-CN">数据列条件信息列表</span>
	 * @return <span class="en-US">Calculated sharding key result</span>
	 * <span class="zh-CN">分片计算结果值</span>
	 * @throws SQLException <span class="en-US">If an error occurs during parsing</span>
	 *                      <span class="zh-CN">如果解析过程出错</span>
	 */
	protected final String shardingDatabase(final String tableName, final List<Condition> conditionList)
			throws SQLException {
		if (!this.sharding) {
			return Globals.DEFAULT_VALUE_STRING;
		}
		ShardingConfig shardingConfig = this.shardingConfigs.get(tableName);
		if (shardingConfig == null) {
			return Globals.DEFAULT_VALUE_STRING;
		}
		return shardingConfig.shardingKey(ShardingType.DATABASE, this.parseConditions(conditionList));
	}

	/**
	 * <h3 class="en-US">Calculate table sharding key</h3>
	 * <h3 class="zh-CN">计算数据表分片值</h3>
	 *
	 * @param tableName    <span class="en-US">Data table name</span>
	 *                     <span class="zh-CN">数据表名</span>
	 * @param parameterMap <span class="en-US">Columns data mapping</span>
	 *                     <span class="zh-CN">数据列信息映射表</span>
	 * @return <span class="en-US">Calculated sharding key result</span>
	 * <span class="zh-CN">分片计算结果值</span>
	 */
	protected final String shardingTable(@Nonnull final String tableName, final Map<String, Serializable> parameterMap) {
		return Optional.ofNullable(this.shardingConfigs.get(tableName))
				.map(shardingConfig -> shardingConfig.shardingKey(ShardingType.TABLE, parameterMap))
				.orElse(tableName);
	}

	/**
	 * <h3 class="en-US">Calculate table sharding key</h3>
	 * <h3 class="zh-CN">计算数据表分片值</h3>
	 *
	 * @param tableName     <span class="en-US">Data table name</span>
	 *                      <span class="zh-CN">数据表明</span>
	 * @param conditionList <span class="en-US">Data column condition information list</span>
	 *                      <span class="zh-CN">数据列条件信息列表</span>
	 * @return <span class="en-US">Calculated sharding key result</span>
	 * <span class="zh-CN">分片计算结果值</span>
	 * @throws SQLException <span class="en-US">If an error occurs during parsing</span>
	 *                      <span class="zh-CN">如果解析过程出错</span>
	 */
	protected final String shardingTable(final String tableName, final List<Condition> conditionList)
			throws SQLException {
		if (!this.sharding) {
			return Globals.DEFAULT_VALUE_STRING;
		}
		ShardingConfig shardingConfig = this.shardingConfigs.get(tableName);
		if (shardingConfig == null) {
			return Globals.DEFAULT_VALUE_STRING;
		}
		return shardingConfig.shardingKey(ShardingType.TABLE, this.parseConditions(conditionList));
	}

	/**
	 * <h3 class="en-US">Parse the query matching condition list into a data mapping table</h3>
	 * <h3 class="zh-CN">解析查询匹配条件列表为数据映射表</h3>
	 *
	 * @param conditionList <span class="en-US">Query matching condition list</span>
	 *                      <span class="zh-CN">查询匹配条件列表</span>
	 * @return <span class="en-US">Converted data mapping table</span>
	 * <span class="zh-CN">数据映射表</span>
	 * @throws SQLException <span class="en-US">If an error occurs during parsing</span>
	 *                      <span class="zh-CN">如果解析过程出错</span>
	 */
	protected final Map<String, Serializable> parseConditions(final List<Condition> conditionList) throws SQLException {
		Map<String, Serializable> parameterMap = new HashMap<>();
		for (Condition condition : conditionList) {
			if (ConditionType.GROUP.equals(condition.getConditionType())) {
				parameterMap.putAll(this.parseConditions(condition.unwrap(GroupCondition.class).getConditionList()));
			} else {
				ColumnCondition columnCondition = condition.unwrap(ColumnCondition.class);
				AbstractParameter<?> abstractParameter = columnCondition.getConditionParameter();
				if (abstractParameter != null && abstractParameter.isWrapperFor(ConstantParameter.class)) {
					parameterMap.put(columnCondition.getColumnName(),
							abstractParameter.unwrap(ConstantParameter.class).getItemValue());
				}
			}
		}
		return parameterMap;
	}

	/**
	 * <h3 class="en-US">Initialize the current data source</h3>
	 * <h3 class="zh-CN">初始化当前数据源</h3>
	 */
	public abstract void initialize();

	/**
	 * <h3 class="en-US">Initialize the current thread used operator based on the given transaction configuration information</h3>
	 * <h3 class="zh-CN">根据给定的事务配置信息初始化当前线程的操作器</h3>
	 *
	 * @param transactionalConfig <span class="en-US">Transactional configure information</span>
	 *                            <span class="zh-CN">事务配置信息</span>
	 * @throws Exception <span class="en-US">If an error occurs during executing</span>
	 *                      <span class="zh-CN">如果执行过程出错</span>
	 */
	public final void initTransactional(final TransactionalConfig transactionalConfig) throws Exception {
		if (this.txConfig.get() == null) {
			this.txConfig.set(transactionalConfig);
		}
		this.beginTransactional();
	}

	/**
	 * <h3 class="en-US">Initialize data table</h3>
	 * <h3 class="zh-CN">初始化数据表</h3>
	 *
	 * @param ddlType     <span class="en-US">Enumeration value of DDL operate</span>
	 *                    <span class="zh-CN">操作类型枚举值</span>
	 * @param tableDefine <span class="en-US">Table define information</span>
	 *                    <span class="zh-CN">数据表定义信息</span>
	 * @param database    <span class="en-US">Database sharding configuration information</span>
	 *                    <span class="zh-CN">数据库分片配置信息</span>
	 * @param table       <span class="en-US">Data table sharding configuration information</span>
	 *                    <span class="zh-CN">数据表分片配置信息</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public final void initTable(@Nonnull final DDLType ddlType, @Nonnull final TableDefine tableDefine,
	                            final ShardingDefine<?> database, final ShardingDefine<?> table) throws Exception {
		ShardingConfig shardingConfig = null;
		if (this.sharding && (database != null || table != null)) {
			if (this.shardingConfigs.contains(tableDefine.getTableName())) {
				this.logger.warn("Registered_Sharding_Configure", tableDefine.getTableName());
			}
			shardingConfig = new ShardingConfig(tableDefine, database, table);
			this.shardingConfigs.put(tableDefine.getTableName(), shardingConfig);
		}

		String shardingDatabase =
				(shardingConfig == null)
						? this.shardingDefault
						: shardingConfig.shardingKey(ShardingType.DATABASE, Map.of());
		this.initSharding(shardingDatabase);
		this.initTable(ddlType, tableDefine, shardingDatabase);
	}

	protected final boolean matchesDatabaseKey(final String databaseName) {
		return this.shardingConfigs
				.values()
				.stream()
				.anyMatch(shardingConfig -> shardingConfig.matchKey(ShardingType.DATABASE, databaseName));
	}

	/**
	 * <h3 class="en-US">Begin transactional</h3>
	 * <h3 class="zh-CN">开启事务</h3>
	 *
	 * @throws Exception <span class="en-US">If an error occurs during execution</span>
	 *                   <span class="zh-CN">如果执行过程中出错</span>
	 */
	protected abstract void beginTransactional() throws Exception;

	/**
	 * <h3 class="en-US">Rollback transactional</h3>
	 * <h3 class="zh-CN">回滚事务</h3>
	 *
	 * @throws Exception <span class="en-US">If an error occurs during execution</span>
	 *                   <span class="zh-CN">如果执行过程中出错</span>
	 */
	public abstract void rollback() throws Exception;

	/**
	 * <h3 class="en-US">Submit transactional execute</h3>
	 * <h3 class="zh-CN">提交事务执行</h3>
	 *
	 * @throws Exception <span class="en-US">If an error occurs during execution</span>
	 *                   <span class="zh-CN">如果执行过程中出错</span>
	 */
	public abstract void commit() throws Exception;

	/**
	 * <h3 class="en-US">Truncate all data table</h3>
	 * <h3 class="zh-CN">清空所有数据表</h3>
	 *
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public abstract void truncateTables() throws Exception;

	/**
	 * <h3 class="en-US">Truncate data table</h3>
	 * <h3 class="zh-CN">清空数据表</h3>
	 *
	 * @param tableDefine <span class="en-US">Table define information</span>
	 *                    <span class="zh-CN">数据表定义信息</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public abstract void truncateTable(@Nonnull final TableDefine tableDefine) throws Exception;

	/**
	 * <h3 class="en-US">Drop all data table</h3>
	 * <h3 class="zh-CN">删除所有数据表</h3>
	 *
	 * @param dropOption <span class="en-US">Cascading delete options</span>
	 *                   <span class="zh-CN">级联删除选项</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public abstract void dropTables(final DropOption dropOption) throws Exception;

	/**
	 * <h3 class="en-US">Drop data table</h3>
	 * <h3 class="zh-CN">删除数据表</h3>
	 *
	 * @param tableDefine <span class="en-US">Table define information</span>
	 *                    <span class="zh-CN">数据表定义信息</span>
	 * @param dropOption  <span class="en-US">Cascading delete options</span>
	 *                    <span class="zh-CN">级联删除选项</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public abstract void dropTable(@Nonnull final TableDefine tableDefine, @Nonnull final DropOption dropOption) throws Exception;

	/**
	 * <h3 class="en-US">Execute insert record command</h3>
	 * <h3 class="zh-CN">执行插入数据命令</h3>
	 *
	 * @param tableDefine <span class="en-US">Table define information</span>
	 *                    <span class="zh-CN">数据表定义信息</span>
	 * @param dataMap     <span class="en-US">Insert data mapping</span>
	 *                    <span class="zh-CN">写入数据映射表</span>
	 * @return <span class="en-US">Primary key value mapping table generated by database</span>
	 * <span class="zh-CN">数据库生成的主键值映射表</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public abstract Map<String, Serializable> insert(@Nonnull final TableDefine tableDefine,
	                                                 @Nonnull final Map<String, Serializable> dataMap) throws Exception;

	/**
	 * <h3 class="en-US">Execute retrieve record command</h3>
	 * <h3 class="zh-CN">执行数据唯一检索命令</h3>
	 *
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
	public abstract Map<String, String> retrieve(@Nonnull final TableDefine tableDefine, final String columns,
	                                             @Nonnull final Map<String, Serializable> filterMap,
	                                             final boolean forUpdate, final LockOption lockOption) throws Exception;

	/**
	 * <h3 class="en-US">Execute update record command</h3>
	 * <h3 class="zh-CN">执行更新记录命令</h3>
	 *
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
	public abstract int update(@Nonnull final TableDefine tableDefine, @Nonnull final Map<String, Serializable> dataMap,
	                           @Nonnull final Map<String, Serializable> filterMap) throws Exception;

	/**
	 * <h3 class="en-US">Execute delete record command</h3>
	 * <h3 class="zh-CN">执行删除记录命令</h3>
	 *
	 * @param tableDefine <span class="en-US">Table define information</span>
	 *                    <span class="zh-CN">数据表定义信息</span>
	 * @param filterMap   <span class="en-US">Delete filter mapping</span>
	 *                    <span class="zh-CN">删除条件映射表</span>
	 * @return <span class="en-US">Deleted records count</span>
	 * <span class="zh-CN">删除记录条数</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public abstract int delete(@Nonnull final TableDefine tableDefine,
	                           @Nonnull final Map<String, Serializable> filterMap) throws Exception;

	/**
	 * <h3 class="en-US">Execute query record command</h3>
	 * <h3 class="zh-CN">执行数据检索命令</h3>
	 *
	 * @param queryInfo <span class="en-US">Query record information</span>
	 *                  <span class="zh-CN">数据检索信息</span>
	 * @return <span class="en-US">List of data mapping tables for retrieved records</span>
	 * <span class="zh-CN">检索到记录的数据映射表列表</span>
	 * @throws SQLException <span class="en-US">An error occurred during execution</span>
	 *                      <span class="zh-CN">执行过程中出错</span>
	 */
	public abstract List<Map<String, String>> query(@Nonnull final QueryInfo queryInfo) throws Exception;

	/**
	 * <h3 class="en-US">Execute query commands for data updates</h3>
	 * <h3 class="zh-CN">执行用于数据更新的查询命令</h3>
	 *
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
	public abstract List<Map<String, String>> queryForUpdate(@Nonnull final TableDefine tableDefine,
	                                                         final List<Condition> conditionList,
	                                                         final LockOption lockOption) throws Exception;

	/**
	 * <h3 class="en-US">Finish current transactional</h3>
	 * <h3 class="zh-CN">结束当前事务</h3>
	 *
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public final void endTransactional() throws Exception {
		this.clearTransactional();
		if (this.txConfig.get() != null) {
			this.txConfig.remove();
		}
	}

	/**
	 * <h3 class="en-US">Initialize sharding connections</h3>
	 * <h3 class="zh-CN">初始化分片连接</h3>
	 *
	 * @param shardingKey <span class="en-US">Database sharding value</span>
	 *                    <span class="zh-CN">数据库分片值</span>
	 * @throws SQLException <span class="en-US">If an error occurs during parsing</span>
	 *                      <span class="zh-CN">如果解析过程出错</span>
	 */
	protected abstract void initSharding(final String shardingKey) throws SQLException;

	/**
	 * <h3 class="en-US">Initialize data table</h3>
	 * <h3 class="zh-CN">初始化数据表</h3>
	 *
	 * @param ddlType          <span class="en-US">Enumeration value of DDL operate</span>
	 *                         <span class="zh-CN">操作类型枚举值</span>
	 * @param tableDefine      <span class="en-US">Table define information</span>
	 *                         <span class="zh-CN">数据表定义信息</span>
	 * @param shardingDatabase <span class="en-US">Sharded database name</span>
	 *                         <span class="zh-CN">分片数据库名</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	protected abstract void initTable(@Nonnull final DDLType ddlType, @Nonnull final TableDefine tableDefine,
	                                  final String shardingDatabase) throws Exception;

	/**
	 * <h3 class="en-US">Clear current transactional</h3>
	 * <h3 class="zh-CN">清理当前事务</h3>
	 *
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	protected abstract void clearTransactional() throws Exception;
}
