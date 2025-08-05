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
import org.nervousync.brain.query.PartialCollection;
import org.nervousync.brain.query.QueryInfo;
import org.nervousync.brain.source.BrainDataSource;

/**
 * <h2 class="en-US">Query optimizer interface</h2>
 * <h2 class="zh-CN">查询优化器接口</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0 $ $Date: Oct 28, 2020 17:02:19 $
 */
public interface QueryOptimizer {

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
