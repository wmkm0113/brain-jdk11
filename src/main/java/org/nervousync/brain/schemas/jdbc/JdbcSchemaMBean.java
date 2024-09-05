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

package org.nervousync.brain.schemas.jdbc;

import org.nervousync.brain.schemas.BaseSchemaMBean;

/**
 * <h2 class="en-US">MBean define class for JDBC data source implementation class</h2>
 * <h2 class="zh-CN">JDBC数据源实现类的MBean定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Nov 12, 2020 11:47:07 $
 */
public interface JdbcSchemaMBean extends BaseSchemaMBean {

	/**
	 * <h3 class="en-US">Setup datasource using connection pool</h3>
	 * <h3 class="zh-CN">设置数据源是否使用连接池</h3>
	 *
	 * @param pooled         <span class="en-US">Using connection pool status</span>
	 *                       <span class="zh-CN">使用连接池状态</span>
	 * @param minConnections <span class="en-US">Minimum connection limit</span>
	 *                       <span class="zh-CN">最小连接数</span>
	 * @param maxConnections <span class="en-US">Maximum connection limit</span>
	 *                       <span class="zh-CN">最大连接数</span>
	 */
	void configPool(final boolean pooled, final int minConnections, final int maxConnections);

	/**
	 * <h3 class="en-US">Read datasource using connection pool</h3>
	 * <h3 class="zh-CN">获取数据源是否使用连接池</h3>
	 *
	 * @return <span class="en-US">Using connection pool status</span>
	 * <span class="zh-CN">使用连接池状态</span>
	 */
	boolean isPooled();

	/**
	 * <h3 class="en-US">Connections count in connection pool</h3>
	 * <h3 class="zh-CN">连接池中现有连接数</h3>
	 *
	 * @return <span class="en-US">Connections count</span>
	 * <span class="zh-CN">连接数</span>
	 */
	int getPoolCount();

	/**
	 * <h3 class="en-US">Activated connections count</h3>
	 * <h3 class="zh-CN">使用中的连接数</h3>
	 *
	 * @return <span class="en-US">Connections count</span>
	 * <span class="zh-CN">连接数</span>
	 */
	int getActiveCount();

	/**
	 * <h3 class="en-US">Waiting to get count of connections</h3>
	 * <h3 class="zh-CN">等待获取连接的计数</h3>
	 *
	 * @return <span class="en-US">Waiting to get count of connections</span>
	 * <span class="zh-CN">等待获取连接的计数</span>
	 */
	int getWaitCount();

	/**
	 * <h3 class="en-US">Minimum connection limit of database connection pool</h3>
	 * <h3 class="zh-CN">获取数据库连接池最小连接数</h3>
	 *
	 * @return <span class="en-US">Minimum connection limit</span>
	 * <span class="zh-CN">最小连接数</span>
	 */
	int getMinConnections();

	/**
	 * <h3 class="en-US">Maximum connection limit of database connection pool</h3>
	 * <h3 class="zh-CN">获取数据库连接池最大连接数</h3>
	 *
	 * @return <span class="en-US">Maximum connection limit</span>
	 * <span class="zh-CN">最大连接数</span>
	 */
	int getMaxConnections();

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

	/**
	 * <h3 class="en-US">Read retry count if obtains connection has error</h3>
	 * <h3 class="zh-CN">获取获取连接的重试次数</h3>
	 *
	 * @return <span class="en-US">Retry count if obtains connection has error</span>
	 * <span class="zh-CN">获取连接的重试次数</span>
	 */
	int getRetryCount();

	/**
	 * <h3 class="en-US">Read retry period value if obtains connection has error</h3>
	 * <h3 class="zh-CN">获取获取连接的重试间隔时间</h3>
	 *
	 * @return <span class="en-US">Retry interval time</span>
	 * <span class="zh-CN">试间隔时间</span>
	 */
	long getRetryPeriod();

	/**
	 * <h3 class="en-US">Setup maximum size of prepared statement</h3>
	 * <h3 class="zh-CN">设置查询分析器的最大缓存结果</h3>
	 *
	 * @param cacheLimitSize <span class="en-US">Maximum size of prepared statement</span>
	 *                       <span class="zh-CN">查询分析器的最大缓存结果</span>
	 */
	void configCacheLimitSize(final int cacheLimitSize);

	/**
	 * <h3 class="en-US">Read maximum size of prepared statement</h3>
	 * <h3 class="zh-CN">获取查询分析器的最大缓存结果</h3>
	 *
	 * @return <span class="en-US">Maximum size of prepared statement</span>
	 * <span class="zh-CN">查询分析器的最大缓存结果</span>
	 */
	int getCachedLimitSize();

	/**
	 * <h3 class="en-US">Setup check connection validate</h3>
	 * <h3 class="zh-CN">设置连接检查</h3>
	 *
	 * @param testOnBorrow <span class="en-US">Check connection validate when obtains database connection</span>
	 *                     <span class="zh-CN">在获取连接时检查连接是否有效</span>
	 * @param testOnReturn <span class="en-US">Check connection validate when return database connection</span>
	 *                     <span class="zh-CN">在归还连接时检查连接是否有效</span>
	 */
	void configTest(final boolean testOnBorrow, final boolean testOnReturn);

	/**
	 * <h3 class="en-US">Read check connection validate when obtains database connection</h3>
	 * <h3 class="zh-CN">获取在获取连接时检查连接是否有效</h3>
	 *
	 * @return <span class="en-US">Check connection validate when obtains database connection</span>
	 * <span class="zh-CN">在获取连接时检查连接是否有效</span>
	 */
	boolean isTestOnBorrow();

	/**
	 * <h3 class="en-US">Read check connection validate when return database connection</h3>
	 * <h3 class="zh-CN">获取在归还连接时检查连接是否有效</h3>
	 *
	 * @return <span class="en-US">Check connection validate when return database connection</span>
	 * <span class="zh-CN">在归还连接时检查连接是否有效</span>
	 */
	boolean isTestOnReturn();

	/**
	 * <h3 class="en-US">Read JDBC connection url string</h3>
	 * <h3 class="zh-CN">获取JDBC连接字符串</h3>
	 *
	 * @return <span class="en-US">JDBC connection url string</span>
	 * <span class="zh-CN">JDBC连接字符串</span>
	 */
	String getJdbcUrl();
}
