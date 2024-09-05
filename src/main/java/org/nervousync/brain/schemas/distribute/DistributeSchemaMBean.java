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

package org.nervousync.brain.schemas.distribute;

import org.nervousync.brain.schemas.BaseSchemaMBean;

/**
 * <h2 class="en-US">MBean define class for distribute data source implementation class</h2>
 * <h2 class="zh-CN">分布式数据源实现类的MBean定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Nov 12, 2020 11:47:07 $
 */
public interface DistributeSchemaMBean extends BaseSchemaMBean {

	/**
	 * <h3 class="en-US">Read database server information</h3>
	 * <h3 class="zh-CN">获取数据库服务器信息</h3>
	 *
	 * @return <span class="en-US">Database server information</span>
	 * <span class="zh-CN">数据库服务器信息</span>
	 */
	String getServerInfo();

	/**
	 * <h3 class="en-US">Read main database server information</h3>
	 * <h3 class="zh-CN">获取主数据库服务器信息</h3>
	 *
	 * @return <span class="en-US">Main database server information</span>
	 * <span class="zh-CN">主数据库服务器信息</span>
	 */
	String getMainServer();

	/**
	 * <h3 class="en-US">Read use ssl status when connect to database</h3>
	 * <h3 class="zh-CN">获取是否使用SSL连接数据库</h3>
	 *
	 * @return <span class="en-US">Use ssl status</span>
	 * <span class="zh-CN">使用SSL连接</span>
	 */
	boolean isUseSsl();

	/**
	 * <h3 class="en-US">Setup retry configure</h3>
	 * <h3 class="zh-CN">设置获取连接的重试配置</h3>
	 *
	 * @param retryCount  <span class="en-US">Retry count if obtains connection has error</span>
	 *                    <span class="zh-CN">获取连接的重试次数</span>
	 * @param retryPeriod <span class="en-US">Retry interval time</span>
	 *                    <span class="zh-CN">试间隔时间</span>
	 */
	void configRetry(final int retryCount, final long retryPeriod);
}
