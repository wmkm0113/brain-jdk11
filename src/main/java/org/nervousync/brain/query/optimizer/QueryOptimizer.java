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

package org.nervousync.brain.query.optimizer;

import jakarta.annotation.Nonnull;
import jakarta.persistence.LockModeType;
import org.nervousync.brain.defines.TableDefine;
import org.nervousync.brain.query.PartialCollection;
import org.nervousync.brain.query.QueryInfo;
import org.nervousync.brain.source.BrainDataSource;

import java.sql.SQLException;
import java.util.Map;

/**
 * <h2 class="en-US">Query optimizer interface</h2>
 * <h2 class="zh-CN">查询优化器接口</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0 $ $Date: Oct 28, 2020 17:02:19 $
 */
@SuppressWarnings("RedundantThrows")
public interface QueryOptimizer {

	/**
	 * <h3 class="en-US">Reset the current query optimizer instance object</h3>
	 * <h3 class="zh-CN">重置当前查询优化器实例对象</h3>
	 */
	void reset();

	/**
	 * <h3 class="en-US">Execute query record command</h3>
	 * <h3 class="zh-CN">执行数据检索命令</h3>
	 *
	 * @param tableDefine <span class="en-US">Table defines information</span>
	 *                    <span class="zh-CN">数据表定义信息</span>
	 * @param columns     <span class="en-US">Query column names</span>
	 *                    <span class="zh-CN">查询数据列名</span>
	 * @param filterMap   <span class="en-US">Retrieve filter mapping</span>
	 *                    <span class="zh-CN">查询条件映射表</span>
	 * @param forUpdate   <span class="en-US">Retrieve result using for update record</span>
	 *                    <span class="zh-CN">检索结果用于更新记录</span>
	 * @param lockMode    <span class="en-US">Lock option</span>
	 *                    <span class="zh-CN">数据锁选项</span>
	 * @return <span class="en-US">List of data mapping tables for retrieved records</span>
	 * <span class="zh-CN">检索到记录的数据映射表列表</span>
	 * @throws SQLException <span class="en-US">An error occurred during execution</span>
	 *                      <span class="zh-CN">执行过程中出错</span>
	 */
	PartialCollection query(@Nonnull BrainDataSource dataSource, @Nonnull final TableDefine tableDefine,
	                        final String columns, @Nonnull final Map<String, Object> filterMap,
	                        final boolean forUpdate, final LockModeType lockMode) throws SQLException;

	/**
	 * <h3 class="en-US">Analyze and optimize query plan, execute the optimized result and return query results</h3>
	 * <h3 class="zh-CN">分析优化查询步骤并执行查询</h3>
	 *
	 * @param dataSource <span class="en-US">Brain data source instance object</span>
	 *                   <span class="zh-CN">大脑数据源实例对象</span>
	 * @param queryInfo  <span class="en-US">Query information instance object</span>
	 *                   <span class="zh-CN">查询信息实例对象</span>
	 * @return <span class="en-US">Query optimize result</span>
	 * <span class="zh-CN">查询优化结果</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	@Nonnull
	PartialCollection query(@Nonnull BrainDataSource dataSource, @Nonnull final QueryInfo queryInfo) throws Exception;

	/**
	 * <h3 class="en-US">Query total record count</h3>
	 * <h3 class="zh-CN">查询总记录数</h3>
	 *
	 * @param dataSource <span class="en-US">Brain data source instance object</span>
	 *                   <span class="zh-CN">大脑数据源实例对象</span>
	 * @param queryInfo  <span class="en-US">Query record information</span>
	 *                   <span class="zh-CN">数据检索信息</span>
	 * @return <span class="en-US">Total record count</span>
	 * <span class="zh-CN">总记录条数</span>
	 * @throws Exception <span class="en-US">An error occurred during execution</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	@Nonnull
	Long queryTotal(@Nonnull BrainDataSource dataSource, @Nonnull final QueryInfo queryInfo) throws Exception;

}
