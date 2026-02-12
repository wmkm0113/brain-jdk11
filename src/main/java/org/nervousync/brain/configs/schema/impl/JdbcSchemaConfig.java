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

package org.nervousync.brain.configs.schema.impl;

import jakarta.xml.bind.annotation.*;
import org.nervousync.brain.commons.BrainCommons;
import org.nervousync.brain.configs.schema.SchemaConfig;
import org.nervousync.brain.enumerations.dialect.DialectType;
import org.nervousync.commons.Globals;

/**
 * <h2 class="en-US">JDBC data source configuration information</h2>
 * <h2 class="zh-CN">JDBC数据源配置信息</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Jul 12, 2020 16:42:35 $
 */
@XmlType(name = "jdbc_schema", namespace = "https://nervousync.org/schemas/brain")
@XmlRootElement(name = "jdbc_schema", namespace = "https://nervousync.org/schemas/brain")
@XmlAccessorType(XmlAccessType.NONE)
public final class JdbcSchemaConfig extends SchemaConfig {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = -553668725998373198L;

	/**
	 * <span class="en-US">JDBC connection url</span>
	 * <span class="zh-CN">JDBC连接字符串</span>
	 */
	@XmlElement(name = "jdbc_url")
	private String jdbcUrl = Globals.DEFAULT_VALUE_STRING;
	/**
	 * <span class="en-US">Maximum number of connection retries</span>
	 * <span class="zh-CN">连接最大重试次数</span>
	 */
	@XmlElement(name = "retry_count")
	private int retryCount = BrainCommons.DEFAULT_RETRY_COUNT;
	/**
	 * <span class="en-US">Get the connection retry interval (unit: milliseconds)</span>
	 * <span class="zh-CN">获取连接的重试间隔时间（单位：毫秒）</span>
	 */
	@XmlElement(name = "retry_period")
	private long retryPeriod = BrainCommons.DEFAULT_RETRY_PERIOD;
	/**
	 * <span class="en-US">Maximum size of prepared statement</span>
	 * <span class="zh-CN">查询分析器的最大缓存结果</span>
	 */
	@XmlElement(name = "cache_limit_size")
	private int cachedLimitSize = Globals.DEFAULT_VALUE_INT;
	/**
	 * <span class="en-US">Check connection validate when obtains database connection</span>
	 * <span class="zh-CN">在获取连接时检查连接是否有效</span>
	 */
	@XmlElement(name = "test_on_borrow")
	private boolean testOnBorrow = Boolean.FALSE;
	/**
	 * <span class="en-US">Check connection validate when return database connection</span>
	 * <span class="zh-CN">在归还连接时检查连接是否有效</span>
	 */
	@XmlElement(name = "test_on_return")
	private boolean testOnReturn = Boolean.FALSE;
	/**
	 * <span class="en-US">Data source support sharding</span>
	 * <span class="zh-CN">数据源是否支持分片</span>
	 */
	@XmlElement
	private boolean sharding = Boolean.FALSE;
	/**
	 * <span class="en-US">Default database catalog value</span>
	 * <span class="zh-CN">默认数据库分片值</span>
	 */
	@XmlElement(name = "catalog")
	private String catalog = Globals.DEFAULT_VALUE_STRING;
	/**
	 * <span class="en-US">Parameter value of create databases</span>
	 * <span class="zh-CN">创建数据库时使用的参数信息</span>
	 */
	@XmlElement(name = "database_parameters")
	private String databaseParameters = Globals.DEFAULT_VALUE_STRING;

	/**
	 * <h3 class="en-US">Constructor method for relational data source configuration information</h3>
	 * <h3 class="zh-CN">关系型数据源配置信息的构造方法</h3>
	 */
	public JdbcSchemaConfig() {
		super(DialectType.Relational);
	}

	/**
	 * <h3 class="en-US">Getter method for JDBC connection url</h3>
	 * <h3 class="zh-CN">JDBC连接字符串的Getter方法</h3>
	 *
	 * @return <span class="en-US">JDBC connection url</span>
	 * <span class="zh-CN">JDBC连接字符串</span>
	 */
	public String getJdbcUrl() {
		return this.jdbcUrl;
	}

	/**
	 * <h3 class="en-US">Setter method for JDBC connection url</h3>
	 * <h3 class="zh-CN">JDBC连接字符串的Setter方法</h3>
	 *
	 * @param jdbcUrl <span class="en-US">JDBC connection url</span>
	 *                <span class="zh-CN">JDBC连接字符串</span>
	 */
	public void setJdbcUrl(final String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	/**
	 * <h3 class="en-US">Getter method for maximum number of connection retries</h3>
	 * <h3 class="zh-CN">连接最大重试次数的Getter方法</h3>
	 *
	 * @return <span class="en-US">Maximum number of connection retries</span>
	 * <span class="zh-CN">连接最大重试次数</span>
	 */
	public int getRetryCount() {
		return this.retryCount;
	}

	/**
	 * <h3 class="en-US">Setter method for maximum number of connection retries</h3>
	 * <h3 class="zh-CN">连接最大重试次数的Setter方法</h3>
	 *
	 * @param retryCount <span class="en-US">Maximum number of connection retries</span>
	 *                   <span class="zh-CN">连接最大重试次数</span>
	 */
	public void setRetryCount(final int retryCount) {
		this.retryCount = retryCount;
	}

	/**
	 * <h3 class="en-US">Getter method for retry count if obtains connection has error</h3>
	 * <h3 class="zh-CN">获取连接的重试次数的Getter方法</h3>
	 *
	 * @return <span class="en-US">Retry count if obtains connection has error</span>
	 * <span class="zh-CN">获取连接的重试次数</span>
	 */
	public long getRetryPeriod() {
		return this.retryPeriod;
	}

	/**
	 * <h3 class="en-US">Setter method for retry count if it gets connection has error</h3>
	 * <h3 class="zh-CN">获取连接的重试次数的Setter方法</h3>
	 *
	 * @param retryPeriod <span class="en-US">Retry count if obtains connection has error</span>
	 *                    <span class="zh-CN">获取连接的重试次数</span>
	 */
	public void setRetryPeriod(final long retryPeriod) {
		this.retryPeriod = retryPeriod;
	}

	/**
	 * <h3 class="en-US">Getter method for maximum size of the prepared statement</h3>
	 * <h3 class="zh-CN">查询分析器的最大缓存结果的Getter方法</h3>
	 *
	 * @return <span class="en-US">Maximum size of prepared statement</span>
	 * <span class="zh-CN">查询分析器的最大缓存结果</span>
	 */
	public int getCachedLimitSize() {
		return this.cachedLimitSize;
	}

	/**
	 * <h3 class="en-US">Setter method for maximum size of the prepared statement</h3>
	 * <h3 class="zh-CN">查询分析器的最大缓存结果的Setter方法</h3>
	 *
	 * @param cachedLimitSize <span class="en-US">Maximum size of prepared statement</span>
	 *                        <span class="zh-CN">查询分析器的最大缓存结果</span>
	 */
	public void setCachedLimitSize(final int cachedLimitSize) {
		this.cachedLimitSize = cachedLimitSize;
	}

	/**
	 * <h3 class="en-US">Getter method for check connection validate when gets database connection</h3>
	 * <h3 class="zh-CN">在获取连接时检查连接是否有效的Getter方法</h3>
	 *
	 * @return <span class="en-US">Check connection validate when obtains database connection</span>
	 * <span class="zh-CN">在获取连接时检查连接是否有效</span>
	 */
	public boolean isTestOnBorrow() {
		return this.testOnBorrow;
	}

	/**
	 * <h3 class="en-US">Setter method for check connection validate when gets database connection</h3>
	 * <h3 class="zh-CN">在获取连接时检查连接是否有效的Setter方法</h3>
	 *
	 * @param testOnBorrow <span class="en-US">Check connection validate when obtains database connection</span>
	 *                     <span class="zh-CN">在获取连接时检查连接是否有效</span>
	 */
	public void setTestOnBorrow(final boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	/**
	 * <h3 class="en-US">Getter method for check connection validate when return database connection</h3>
	 * <h3 class="zh-CN">在归还连接时检查连接是否有效的Getter方法</h3>
	 *
	 * @return <span class="en-US">Check connection validate when return database connection</span>
	 * <span class="zh-CN">在归还连接时检查连接是否有效</span>
	 */
	public boolean isTestOnReturn() {
		return this.testOnReturn;
	}

	/**
	 * <h3 class="en-US">Setter method for check connection validate when return database connection</h3>
	 * <h3 class="zh-CN">在归还连接时检查连接是否有效的Setter方法</h3>
	 *
	 * @param testOnReturn <span class="en-US">Check connection validate when return database connection</span>
	 *                     <span class="zh-CN">在归还连接时检查连接是否有效</span>
	 */
	public void setTestOnReturn(final boolean testOnReturn) {
		this.testOnReturn = testOnReturn;
	}

	/**
	 * <h3 class="en-US">Getter method for data source support sharding</h3>
	 * <h3 class="zh-CN">数据源是否支持分片的Getter方法</h3>
	 *
	 * @return <span class="en-US">Data source support sharding</span>
	 * <span class="zh-CN">数据源是否支持分片</span>
	 */
	public boolean isSharding() {
		return this.sharding;
	}

	/**
	 * <h3 class="en-US">Setter method for data source support sharding</h3>
	 * <h3 class="zh-CN">数据源是否支持分片的Setter方法</h3>
	 *
	 * @param sharding <span class="en-US">Data source support sharding</span>
	 *                 <span class="zh-CN">数据源是否支持分片</span>
	 */
	public void setSharding(final boolean sharding) {
		this.sharding = sharding;
	}

	/**
	 * <h3 class="en-US">Getter method for default database sharding value</h3>
	 * <h3 class="zh-CN">默认数据库分片值的Getter方法</h3>
	 *
	 * @return <span class="en-US">Default database sharding value</span>
	 * <span class="zh-CN">默认数据库分片值</span>
	 */
	public String getCatalog() {
		return this.catalog;
	}

	/**
	 * <h3 class="en-US">Setter method for default database sharding value</h3>
	 * <h3 class="zh-CN">默认数据库分片值的Setter方法</h3>
	 *
	 * @param catalog <span class="en-US">Default database sharding value</span>
	 *                        <span class="zh-CN">默认数据库分片值</span>
	 */
	public void setCatalog(final String catalog) {
		this.catalog = catalog;
	}

	/**
	 * <h3 class="en-US">Getter method for the parameter value of create databases</h3>
	 * <h3 class="zh-CN">创建数据库时使用的参数信息的Getter方法</h3>
	 *
	 * @return <span class="en-US">Parameter value of create databases</span>
	 * <span class="zh-CN">创建数据库时使用的参数信息</span>
	 */
	public String getDatabaseParameters() {
		return this.databaseParameters;
	}

	/**
	 * <h3 class="en-US">Setter method for the parameter value of create databases</h3>
	 * <h3 class="zh-CN">创建数据库时使用的参数信息的Setter方法</h3>
	 *
	 * @param databaseParameters <span class="en-US">Parameter value of create databases</span>
	 *                           <span class="zh-CN">创建数据库时使用的参数信息</span>
	 */
	public void setDatabaseParameters(final String databaseParameters) {
		this.databaseParameters = databaseParameters;
	}
}
