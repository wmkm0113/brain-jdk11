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

import java.io.Closeable;
import java.sql.SQLException;

/**
 * <h2 class="en-US">Distribute database client interface</h2>
 * <h2 class="zh-CN">分布式数据库客户端接口</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Feb 18, 2019 10:38:52 $
 */
public interface DistributeClient extends Closeable {

	/**
	 * @param retryCount      <span class="en-US">Maximum number of connection retries</span>
	 *                        <span class="zh-CN">连接最大重试次数</span>
	 * @param retryPeriod     <span class="en-US">Retry count if obtains connection has error</span>
	 *                        <span class="zh-CN">获取连接的重试次数</span>
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
	 * <h3 class="en-US">Initialize operator instance</h3>
	 * <h3 class="zh-CN">初始化操作器</h3>
	 *
	 * @return <span class="en-US">Initialized operator instance object</span>
	 * <span class="zh-CN">初始化的操作器实例对象</span>
	 */
	Operator newOperator();
}
