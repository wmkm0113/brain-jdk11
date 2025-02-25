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

package org.nervousync.brain.dialects.distribute;

import jakarta.annotation.Nonnull;
import jakarta.persistence.LockModeType;
import org.jetbrains.annotations.NotNull;
import org.nervousync.brain.configs.transactional.TransactionalConfig;
import org.nervousync.brain.defines.TableDefine;
import org.nervousync.brain.enumerations.ddl.DDLType;
import org.nervousync.brain.enumerations.ddl.DropOption;
import org.nervousync.brain.query.QueryInfo;
import org.nervousync.brain.query.condition.Condition;

import java.io.Closeable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * <h2 class="en-US">Distribute database client interface</h2>
 * <h2 class="zh-CN">分布式数据库客户端接口</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Feb 18, 2019 10:38:52 $
 */
public interface DistributeClient extends Closeable {

	/**
	 * @param retryCount  <span class="en-US">Maximum number of connection retries</span>
	 *                    <span class="zh-CN">连接最大重试次数</span>
	 * @param retryPeriod <span class="en-US">Retry count if obtains connection has error</span>
	 *                    <span class="zh-CN">获取连接的重试次数</span>
	 */
	void configRetry(final int retryCount, final long retryPeriod);

	/**
	 * <h3 class="en-US">Initialize sharding connections</h3>
	 * <h3 class="zh-CN">初始化分片连接</h3>
	 *
	 * @param shardingKey <span class="en-US">Database sharding value</span>
	 *                    <span class="zh-CN">数据库分片值</span>
	 * @throws SQLException <span class="en-US">If an error occurs during parsing</span>
	 *                      <span class="zh-CN">如果解析过程出错</span>
	 */
	void initSharding(final String shardingKey) throws SQLException;

	/**
	 * <h3 class="en-US">Begin transactional</h3>
	 * <h3 class="zh-CN">开启事务</h3>
	 *
	 * @param transactionalConfig <span class="en-US">Transactional configure information</span>
	 *                            <span class="zh-CN">事务配置信息</span>
	 * @throws Exception <span class="en-US">If an error occurs during execution</span>
	 *                   <span class="zh-CN">如果执行过程中出错</span>
	 */
	void beginTransactional(final TransactionalConfig transactionalConfig) throws Exception;

	/**
	 * <h3 class="en-US">Rollback transactional</h3>
	 * <h3 class="zh-CN">回滚事务</h3>
	 *
	 * @throws Exception <span class="en-US">If an error occurs during execution</span>
	 *                   <span class="zh-CN">如果执行过程中出错</span>
	 */
	void rollback() throws Exception;

	/**
	 * <h3 class="en-US">Submit transactional execute</h3>
	 * <h3 class="zh-CN">提交事务执行</h3>
	 *
	 * @throws Exception <span class="en-US">If an error occurs during execution</span>
	 *                   <span class="zh-CN">如果执行过程中出错</span>
	 */
	void commit() throws Exception;

	/**
	 * <h3 class="en-US">Finish current transactional</h3>
	 * <h3 class="zh-CN">结束当前事务</h3>
	 *
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	void clearTransactional() throws Exception;

	/**
	 * <h3 class="en-US">Truncate all data table</h3>
	 * <h3 class="zh-CN">清空所有数据表</h3>
	 *
	 * @throws SQLException <span class="en-US">An error occurred during execution</span>
	 *                      <span class="zh-CN">执行过程中出错</span>
	 */
	void truncateTables() throws Exception;

	/**
	 * <h3 class="en-US">Truncate data table</h3>
	 * <h3 class="zh-CN">清空数据表</h3>
	 *
	 * @param tableDefine <span class="en-US">Table define information</span>
	 *                    <span class="zh-CN">数据表定义信息</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	void truncateTable(@Nonnull final TableDefine tableDefine) throws Exception;

	/**
	 * <h3 class="en-US">Drop all data table</h3>
	 * <h3 class="zh-CN">删除所有数据表</h3>
	 *
	 * @param dropOption <span class="en-US">Cascading delete options</span>
	 *                   <span class="zh-CN">级联删除选项</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	void dropTables(final DropOption dropOption) throws Exception;

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
	void dropTable(@Nonnull final TableDefine tableDefine, @Nonnull final DropOption dropOption) throws Exception;

	/**
	 * <h3 class="en-US">Execute lock record command</h3>
	 * <h3 class="zh-CN">执行数据锁定命令</h3>
	 *
	 * @param shardingDatabase <span class="en-US">Sharded database name</span>
	 *                         <span class="zh-CN">分片数据库名</span>
	 * @param tableDefine      <span class="en-US">Table define information</span>
	 *                         <span class="zh-CN">数据表定义信息</span>
	 * @param filterMap        <span class="en-US">Filter data mapping</span>
	 *                         <span class="zh-CN">查询数据映射表</span>
	 * @return <span class="en-US">Process result</span>
	 * <span class="zh-CN">操作结果</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	boolean lockRecord(final String shardingDatabase, @Nonnull final TableDefine tableDefine,
	                   @Nonnull final Map<String, Object> filterMap)
			throws Exception;

	/**
	 * <h3 class="en-US">Execute insert record command</h3>
	 * <h3 class="zh-CN">执行插入数据命令</h3>
	 *
	 * @param shardingDatabase <span class="en-US">Sharded database name</span>
	 *                         <span class="zh-CN">分片数据库名</span>
	 * @param tableDefine      <span class="en-US">Table define information</span>
	 *                         <span class="zh-CN">数据表定义信息</span>
	 * @param dataMap          <span class="en-US">Insert data mapping</span>
	 *                         <span class="zh-CN">写入数据映射表</span>
	 * @return <span class="en-US">Primary key value mapping table generated by database</span>
	 * <span class="zh-CN">数据库生成的主键值映射表</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	Map<String, Object> insert(final String shardingDatabase, @Nonnull final TableDefine tableDefine,
	                           @Nonnull final Map<String, Object> dataMap) throws Exception;

	/**
	 * <h3 class="en-US">Execute retrieve record command</h3>
	 * <h3 class="zh-CN">执行数据唯一检索命令</h3>
	 *
	 * @param shardingDatabase <span class="en-US">Sharded database name</span>
	 *                         <span class="zh-CN">分片数据库名</span>
	 * @param tableDefine      <span class="en-US">Table define information</span>
	 *                         <span class="zh-CN">数据表定义信息</span>
	 * @param columns          <span class="en-US">Query column names</span>
	 *                         <span class="zh-CN">查询数据列名</span>
	 * @param filterMap        <span class="en-US">Retrieve filter mapping</span>
	 *                         <span class="zh-CN">查询条件映射表</span>
	 * @param forUpdate        <span class="en-US">Retrieve result using for update record</span>
	 *                         <span class="zh-CN">检索结果用于更新记录</span>
	 * @return <span class="en-US">Data mapping table of retrieved records</span>
	 * <span class="zh-CN">检索到记录的数据映射表</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	Map<String, Object> retrieve(final String shardingDatabase, @Nonnull final TableDefine tableDefine,
	                             final String columns, @Nonnull final Map<String, Object> filterMap,
	                             final boolean forUpdate) throws Exception;

	/**
	 * <h3 class="en-US">Execute update record command</h3>
	 * <h3 class="zh-CN">执行更新记录命令</h3>
	 *
	 * @param shardingDatabase <span class="en-US">Sharded database name</span>
	 *                         <span class="zh-CN">分片数据库名</span>
	 * @param tableDefine      <span class="en-US">Table define information</span>
	 *                         <span class="zh-CN">数据表定义信息</span>
	 * @param dataMap          <span class="en-US">Update data mapping</span>
	 *                         <span class="zh-CN">更新数据映射表</span>
	 * @param filterMap        <span class="en-US">Update filter mapping</span>
	 *                         <span class="zh-CN">更新条件映射表</span>
	 * @return <span class="en-US">Updated records count</span>
	 * <span class="zh-CN">更新记录条数</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	int update(final String shardingDatabase, @Nonnull final TableDefine tableDefine,
	           @Nonnull final Map<String, Object> dataMap,
	           @Nonnull final Map<String, Object> filterMap) throws Exception;

	/**
	 * <h3 class="en-US">Execute delete record command</h3>
	 * <h3 class="zh-CN">执行删除记录命令</h3>
	 *
	 * @param shardingDatabase <span class="en-US">Sharded database name</span>
	 *                         <span class="zh-CN">分片数据库名</span>
	 * @param tableDefine      <span class="en-US">Table define information</span>
	 *                         <span class="zh-CN">数据表定义信息</span>
	 * @param filterMap        <span class="en-US">Delete filter mapping</span>
	 *                         <span class="zh-CN">删除条件映射表</span>
	 * @return <span class="en-US">Deleted records count</span>
	 * <span class="zh-CN">删除记录条数</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	int delete(final String shardingDatabase, @Nonnull final TableDefine tableDefine,
	           @Nonnull final Map<String, Object> filterMap) throws Exception;

	/**
	 * <h3 class="en-US">Execute query record command</h3>
	 * <h3 class="zh-CN">执行数据检索命令</h3>
	 *
	 * @param shardingDatabase <span class="en-US">Sharded database name</span>
	 *                         <span class="zh-CN">分片数据库名</span>
	 * @param tableDefine      <span class="en-US">Table define information</span>
	 *                         <span class="zh-CN">数据表定义信息</span>
	 * @param queryInfo        <span class="en-US">Query record information</span>
	 *                         <span class="zh-CN">数据检索信息</span>
	 * @return <span class="en-US">List of data mapping tables for retrieved records</span>
	 * <span class="zh-CN">检索到记录的数据映射表列表</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	List<Map<String, Object>> query(final String shardingDatabase, @NotNull final TableDefine tableDefine,
	                                @Nonnull final QueryInfo queryInfo) throws Exception;

	/**
	 * <h3 class="en-US">Execute query commands for data updates</h3>
	 * <h3 class="zh-CN">执行用于数据更新的查询命令</h3>
	 *
	 * @param shardingDatabase <span class="en-US">Sharded database name</span>
	 *                         <span class="zh-CN">分片数据库名</span>
	 * @param tableDefine      <span class="en-US">Table define information</span>
	 *                         <span class="zh-CN">数据表定义信息</span>
	 * @param conditionList    <span class="en-US">Query condition instance list</span>
	 *                         <span class="zh-CN">查询条件实例对象列表</span>
	 * @return <span class="en-US">List of data mapping tables for retrieved records</span>
	 * <span class="zh-CN">检索到记录的数据映射表列表</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	List<Map<String, Object>> queryForUpdate(final String shardingDatabase, @Nonnull final TableDefine tableDefine,
	                                         final List<Condition> conditionList)
			throws Exception;

	/**
	 * <h3 class="en-US">Query total record count</h3>
	 * <h3 class="zh-CN">查询总记录数</h3>
	 *
	 * @param shardingDatabase <span class="en-US">Sharded database name</span>
	 *                         <span class="zh-CN">分片数据库名</span>
	 * @param tableDefine      <span class="en-US">Table define information</span>
	 *                         <span class="zh-CN">数据表定义信息</span>
	 * @param queryInfo        <span class="en-US">Query record information</span>
	 *                         <span class="zh-CN">数据检索信息</span>
	 * @return <span class="en-US">Total record count</span>
	 * <span class="zh-CN">总记录条数</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	Long queryTotal(final String shardingDatabase, @Nonnull final TableDefine tableDefine,
	                final QueryInfo queryInfo) throws Exception;

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
	void initTable(@Nonnull final DDLType ddlType, @Nonnull final TableDefine tableDefine,
	               final String shardingDatabase) throws Exception;
}
