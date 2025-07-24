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
import jakarta.persistence.LockModeType;
import org.nervousync.brain.configs.auth.Authentication;
import org.nervousync.brain.configs.schema.SchemaConfig;
import org.nervousync.brain.configs.secure.TrustStore;
import org.nervousync.brain.configs.transactional.TransactionalConfig;
import org.nervousync.brain.defines.InitOption;
import org.nervousync.brain.defines.TableDefine;
import org.nervousync.brain.dialects.core.BaseDialect;
import org.nervousync.brain.enumerations.ddl.DDLType;
import org.nervousync.brain.enumerations.ddl.DropOption;
import org.nervousync.brain.enumerations.dialect.DialectType;
import org.nervousync.brain.exceptions.sql.MultilingualSQLException;
import org.nervousync.brain.query.PartialCollection;
import org.nervousync.brain.query.QueryInfo;
import org.nervousync.commons.Globals;
import org.nervousync.utils.ClassUtils;
import org.nervousync.utils.LoggerUtils;

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
public abstract class BaseSchema<D extends BaseDialect> implements Wrapper, BaseSchemaMBean {

	/**
	 * <span class="en-US">Logger instance</span>
	 * <span class="zh-CN">日志实例</span>
	 */
	protected transient final LoggerUtils.Logger logger = LoggerUtils.getLogger(this.getClass());

	/**
	 * <span class="en-US">Select all columns command</span>
	 * <span class="zh-CN">选择所有数据列命令</span>
	 */
	protected static final String SELECT_ALL_COLUMNS = "*";

	/**
	 * <span class="en-US">Last modified timestamp</span>
	 * <span class="zh-CN">最后修改时间戳</span>
	 */
	private final long lastModified;
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
	 * <span class="en-US">Database dialect instance object</span>
	 * <span class="zh-CN">数据库方言实例对象</span>
	 */
	protected final D dialect;
	/**
	 * <span class="en-US">Low query timeout (Unit: milliseconds)</span>
	 * <span class="zh-CN">慢查询的临界时间（单位：毫秒）</span>
	 */
	private long lowQueryTimeout;
	/**
	 * <span class="en-US">Timeout value of connection validates</span>
	 * <span class="zh-CN">连接检查超时时间</span>
	 */
	private int validateTimeout;
	/**
	 * <span class="en-US">Timeout value of create connection</span>
	 * <span class="zh-CN">建立连接超时时间</span>
	 */
	private int connectTimeout;
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
	 * @param dialect      <span class="en-US">Database dialect instance object</span>
	 *                     <span class="zh-CN">数据库方言实例对象</span>
	 * @throws SQLException <span class="en-US">Database dialect type isn't matched</span>
	 *                      <span class="zh-CN">数据库方言不匹配</span>
	 */
	protected BaseSchema(@Nonnull final SchemaConfig schemaConfig, @Nonnull final D dialect) throws SQLException {
		if (!dialect.type().equals(schemaConfig.getDialectType())) {
			throw new MultilingualSQLException(0x00DB00000045L, dialect.type(), schemaConfig.getDialectType());
		}
		this.lastModified = schemaConfig.getLastModified();
		this.authentication = schemaConfig.getAuthentication();
		this.trustStore = schemaConfig.getTrustStore();
		this.dialect = dialect;
		this.lowQueryTimeout = schemaConfig.getLowQueryTimeout();
		this.validateTimeout = schemaConfig.getValidateTimeout();
		this.connectTimeout = schemaConfig.getConnectTimeout();
	}

	/**
	 * <h3 class="en-US">Check whether the modification time of the current configuration information is consistent with the given modification time</h3>
	 * <h3 class="zh-CN">检查当前配置信息的修改时间是否与给定的修改时间一致</h3>
	 *
	 * @param lastModified <span class="en-US">Last modified timestamp</span>
	 *                     <span class="zh-CN">最后修改时间戳</span>
	 * @return <span class="en-US">Check result</span>
	 * <span class="zh-CN">检查结果</span>
	 */
	public final boolean match(final long lastModified) {
		return lastModified != Globals.DEFAULT_VALUE_LONG && this.lastModified == lastModified;
	}

	/**
	 * <h3 class="en-US">Checks whether the given dialect type is consistent with the current dialect type</h3>
	 * <h3 class="zh-CN">检查给定的方言类型与当前方言类型是否一致</h3>
	 *
	 * @param dialectType <span class="en-US">Data source dialect type enumeration value</span>
	 *                    <span class="zh-CN">数据源方言类型枚举值</span>
	 * @return <span class="en-US">Check result</span>
	 * <span class="zh-CN">检查结果</span>
	 */
	public final boolean match(final DialectType dialectType) {
		return this.dialect.match(dialectType);
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
	 * <h3 class="en-US">Initialize the current data source</h3>
	 * <h3 class="zh-CN">初始化当前数据源</h3>
	 */
	public abstract void initialize();

	/**
	 * <h3 class="en-US">Close the current data source</h3>
	 * <h3 class="zh-CN">关闭当前数据源</h3>
	 */
	public abstract void close();

	/**
	 * <h3 class="en-US">Initialize the current thread used operator based on the given transaction configuration information</h3>
	 * <h3 class="zh-CN">根据给定的事务配置信息初始化当前线程的操作器</h3>
	 *
	 * @param transactionalConfig <span class="en-US">Transactional configure information</span>
	 *                            <span class="zh-CN">事务配置信息</span>
	 * @throws Exception <span class="en-US">If an error occurs during executing</span>
	 *                   <span class="zh-CN">如果执行过程出错</span>
	 */
	public final void initTransactional(final TransactionalConfig transactionalConfig) throws Exception {
		if (this.txConfig.get() == null) {
			this.txConfig.set(transactionalConfig);
		}
		this.beginTransactional();
	}

	/**
	 * <h3 class="en-US">Convert default value to string</h3>
	 * <h3 class="zh-CN">转换默认值为字符串</h3>
	 *
	 * @param jdbcType  <span class="en-US">JDBC type code</span>
	 *                  <span class="zh-CN">JDBC类型代码</span>
	 * @param length    <span class="en-US">Data column length</span>
	 *                  <span class="zh-CN">数据列长度</span>
	 * @param precision <span class="en-US">The precision for a decimal (exact numeric) column</span>
	 *                  <span class="zh-CN">小数（精确数字）列的精度</span>
	 * @param scale     <span class="en-US">The scale for a decimal (exact numeric) column</span>
	 *                  <span class="zh-CN">小数（精确数字）列的比例</span>
	 * @param object    <span class="en-US">Default value instance object</span>
	 *                  <span class="zh-CN">默认值实例对象</span>
	 * @return <span class="en-US">Default value string</span>
	 * <span class="zh-CN">默认值字符串</span>
	 */
	public final String defaultValue(final int jdbcType, final int length, final int precision, final int scale,
	                                 final Object object) {
		return Optional.ofNullable(this.dialect.defaultValue(jdbcType, length, precision, scale, object))
				.orElse(Globals.DEFAULT_VALUE_STRING);
	}

	/**
	 * <h3 class="en-US">Submit transactional execute</h3>
	 * <h3 class="zh-CN">提交事务执行</h3>
	 *
	 * @throws Exception <span class="en-US">If an error occurs during execution</span>
	 *                   <span class="zh-CN">如果执行过程中出错</span>
	 */
	public abstract void commit() throws Exception;

	/**
	 * <h3 class="en-US">Truncate all data tables</h3>
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
	 * @param tableDefine <span class="en-US">Table defines information</span>
	 *                    <span class="zh-CN">数据表定义信息</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public abstract void truncateTable(@Nonnull final TableDefine tableDefine) throws Exception;

	/**
	 * <h3 class="en-US">Drop all data tables</h3>
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
	 * @param tableDefine <span class="en-US">Table defines information</span>
	 *                    <span class="zh-CN">数据表定义信息</span>
	 * @param dropOption  <span class="en-US">Cascading delete options</span>
	 *                    <span class="zh-CN">级联删除选项</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public abstract void dropTable(@Nonnull final TableDefine tableDefine, @Nonnull final DropOption dropOption)
			throws Exception;

	/**
	 * <h3 class="en-US">Execute lock record command</h3>
	 * <h3 class="zh-CN">执行数据锁定命令</h3>
	 *
	 * @param tableDefine <span class="en-US">Table defines information</span>
	 *                    <span class="zh-CN">数据表定义信息</span>
	 * @param filterMap   <span class="en-US">Filter data mapping</span>
	 *                    <span class="zh-CN">查询数据映射表</span>
	 * @param lockOption  <span class="en-US">Lock option</span>
	 *                    <span class="zh-CN">数据锁选项</span>
	 * @return <span class="en-US">Primary key value mapping table generated by database</span>
	 * <span class="zh-CN">数据库生成的主键值映射表</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public abstract boolean lockRecord(@Nonnull final TableDefine tableDefine,
	                                   @Nonnull final Map<String, Object> filterMap,
	                                   final LockModeType lockOption) throws Exception;

	/**
	 * <h3 class="en-US">Execute insert record command</h3>
	 * <h3 class="zh-CN">执行插入数据命令</h3>
	 *
	 * @param tableDefine <span class="en-US">Table defines information</span>
	 *                    <span class="zh-CN">数据表定义信息</span>
	 * @param dataMap     <span class="en-US">Insert data mapping</span>
	 *                    <span class="zh-CN">写入数据映射表</span>
	 * @return <span class="en-US">Primary key value mapping table generated by database</span>
	 * <span class="zh-CN">数据库生成的主键值映射表</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public abstract Map<String, Object> insert(@Nonnull final TableDefine tableDefine,
	                                           @Nonnull final Map<String, Object> dataMap) throws Exception;

	/**
	 * <h3 class="en-US">Execute retrieve record command</h3>
	 * <h3 class="zh-CN">执行数据唯一检索命令</h3>
	 *
	 * @param tableDefine <span class="en-US">Table defines information</span>
	 *                    <span class="zh-CN">数据表定义信息</span>
	 * @param columns     <span class="en-US">Query column names</span>
	 *                    <span class="zh-CN">查询数据列名</span>
	 * @param filterMap   <span class="en-US">Retrieve filter mapping</span>
	 *                    <span class="zh-CN">查询条件映射表</span>
	 * @param forUpdate   <span class="en-US">Retrieve result using for update record</span>
	 *                    <span class="zh-CN">检索结果用于更新记录</span>
	 * @param lockOption  <span class="en-US">Lock option</span>
	 *                    <span class="zh-CN">数据锁选项</span>
	 * @return <span class="en-US">Data mapping table of retrieved records</span>
	 * <span class="zh-CN">检索到记录的数据映射表</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public abstract Map<String, Object> retrieve(@Nonnull final TableDefine tableDefine, final String columns,
	                                             @Nonnull final Map<String, Object> filterMap, final boolean forUpdate,
	                                             final LockModeType lockOption) throws Exception;

	/**
	 * <h3 class="en-US">Execute update record command</h3>
	 * <h3 class="zh-CN">执行更新记录命令</h3>
	 *
	 * @param tableDefine <span class="en-US">Table defines information</span>
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
	public abstract int update(@Nonnull final TableDefine tableDefine, @Nonnull final Map<String, Object> dataMap,
	                           @Nonnull final Map<String, Object> filterMap) throws Exception;

	/**
	 * <h3 class="en-US">Execute delete record command</h3>
	 * <h3 class="zh-CN">执行删除记录命令</h3>
	 *
	 * @param tableDefine <span class="en-US">Table defines information</span>
	 *                    <span class="zh-CN">数据表定义信息</span>
	 * @param filterMap   <span class="en-US">Delete filter mapping</span>
	 *                    <span class="zh-CN">删除条件映射表</span>
	 * @return <span class="en-US">Deleted records count</span>
	 * <span class="zh-CN">删除记录条数</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public abstract int delete(@Nonnull final TableDefine tableDefine,
	                           @Nonnull final Map<String, Object> filterMap) throws Exception;

	/**
	 * <h3 class="en-US">Execute query record command</h3>
	 * <h3 class="zh-CN">执行数据检索命令</h3>
	 *
	 * @param queryInfo <span class="en-US">Query record information</span>
	 *                  <span class="zh-CN">数据检索信息</span>
	 * @return <span class="en-US">List of data mapping tables for retrieved records</span>
	 * <span class="zh-CN">检索到记录的数据映射表列表</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public abstract PartialCollection query(@Nonnull final QueryInfo queryInfo) throws Exception;

	/**
	 * <h3 class="en-US">Query total record count</h3>
	 * <h3 class="zh-CN">查询总记录数</h3>
	 *
	 * @param queryInfo <span class="en-US">Query record information</span>
	 *                  <span class="zh-CN">数据检索信息</span>
	 * @return <span class="en-US">Total record count</span>
	 * <span class="zh-CN">总记录条数</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public abstract Long queryTotal(final QueryInfo queryInfo)
			throws Exception;

	/**
	 * <h3 class="en-US">Finish current transactional</h3>
	 * <h3 class="zh-CN">结束当前事务</h3>
	 *
	 * @throws SQLException <span class="en-US">An error occurred during execution</span>
	 *                      <span class="zh-CN">执行过程中出错</span>
	 */
	public final void endTransactional() throws Exception {
		this.clearTransactional();
		if (this.txConfig.get() != null) {
			this.txConfig.remove();
		}
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
	 * @param e <span class="en-US">Cached execution information</span>
	 *          <span class="zh-CN">捕获的异常信息</span>
	 * @throws Exception <span class="en-US">If an error occurs during execution</span>
	 *                   <span class="zh-CN">如果执行过程中出错</span>
	 */
	public abstract void rollback(final Exception e) throws Exception;

	/**
	 * <h3 class="en-US">Initialize data table</h3>
	 * <h3 class="zh-CN">初始化数据表</h3>
	 *
	 * @param ddlType        <span class="en-US">Enumeration value of DDL operate</span>
	 *                       <span class="zh-CN">操作类型枚举值</span>
	 * @param tableDefine    <span class="en-US">Table defines information</span>
	 *                       <span class="zh-CN">数据表定义信息</span>
	 * @param initOptionsMap <span class="en-US">Data column initialize option</span>
	 *                       <span class="zh-CN">数据列初始化选项</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	public abstract void initTable(@Nonnull final DDLType ddlType, @Nonnull final TableDefine tableDefine,
	                               @Nonnull final Map<String, InitOption> initOptionsMap) throws Exception;

	/**
	 * <h3 class="en-US">Clear current transactional</h3>
	 * <h3 class="zh-CN">清理当前事务</h3>
	 *
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	protected abstract void clearTransactional() throws Exception;
}
