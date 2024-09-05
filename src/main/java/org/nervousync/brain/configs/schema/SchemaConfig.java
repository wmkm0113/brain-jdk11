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

package org.nervousync.brain.configs.schema;

import jakarta.xml.bind.annotation.*;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.brain.commons.BrainCommons;
import org.nervousync.brain.configs.auth.Authentication;
import org.nervousync.brain.configs.auth.impl.TrustStoreAuthentication;
import org.nervousync.brain.configs.auth.impl.UserAuthentication;
import org.nervousync.brain.configs.auth.impl.X509Authentication;
import org.nervousync.brain.configs.schema.impl.DistributeSchemaConfig;
import org.nervousync.brain.configs.schema.impl.JdbcSchemaConfig;
import org.nervousync.brain.configs.schema.impl.RemoteSchemaConfig;
import org.nervousync.brain.configs.secure.TrustStore;
import org.nervousync.commons.Globals;

/**
 * <h2 class="en-US">Data source configuration information abstract class</h2>
 * <h2 class="zh-CN">数据源配置信息抽象类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision : 1.0.0 $ $Date: Jul 12, 2020 16:15:09 $
 */
@XmlType(namespace = "https://nervousync.org/schemas/database")
@XmlSeeAlso({DistributeSchemaConfig.class, JdbcSchemaConfig.class, RemoteSchemaConfig.class})
@XmlAccessorType(XmlAccessType.NONE)
public abstract class SchemaConfig extends BeanObject {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = 6508704359955500086L;

	/**
	 * <span class="en-US">Data source name</span>
	 * <span class="zh-CN">数据源名称</span>
	 */
	@XmlElement(name = "schema_name")
	private String schemaName;
	/**
	 * <span class="en-US">Default data source flag</span>
	 * <span class="zh-CN">默认数据源标记</span>
	 */
	@XmlElement(name = "default_schema")
	private boolean defaultSchema = Boolean.FALSE;
	/**
	 * <span class="en-US">Data source dialect name</span>
	 * <span class="zh-CN">数据源方言名称</span>
	 */
	@XmlElement(name = "default_source")
	private String dialectName;
	/**
	 * <span class="en-US">Trust certificate store configuration information</span>
	 * <span class="zh-CN">信任证书库配置信息</span>
	 */
	@XmlElement(name = "trust_store")
	private TrustStore trustStore;
	/**
	 * <span class="en-US">Identity authentication configuration information</span>
	 * <span class="zh-CN">身份认证配置信息</span>
	 */
	@XmlElementRefs({
			@XmlElementRef(name = "user_authentication", type = UserAuthentication.class),
			@XmlElementRef(name = "x509_authentication", type = X509Authentication.class),
			@XmlElementRef(name = "trust_store_authentication", type = TrustStoreAuthentication.class)
	})
	private Authentication authentication;
	/**
	 * <span class="en-US">Low query timeout (Unit: milliseconds)</span>
	 * <span class="zh-CN">慢查询的临界时间（单位：毫秒）</span>
	 */
	@XmlElement(name = "low_query_timeout")
	private long lowQueryTimeout = Globals.DEFAULT_VALUE_LONG;
	/**
	 * <span class="en-US">Timeout value of connection validate (Unit: seconds)</span>
	 * <span class="zh-CN">连接检查超时时间（单位：秒）</span>
	 */
	@XmlElement(name = "validate_timeout")
	private int validateTimeout;
	/**
	 * <span class="en-US">Timeout value of create connection (Unit: seconds)</span>
	 * <span class="zh-CN">建立连接超时时间（单位：秒）</span>
	 */
	@XmlElement(name = "connect_timeout")
	private int connectTimeout;
	/**
	 * <span class="en-US">Data source support sharding</span>
	 * <span class="zh-CN">数据源是否支持分片</span>
	 */
	@XmlElement
	private boolean sharding = Boolean.FALSE;
	/**
	 * <span class="en-US">Default database sharding value</span>
	 * <span class="zh-CN">默认数据库分片值</span>
	 */
	@XmlElement(name = "sharding_default")
	private String shardingDefault = Globals.DEFAULT_VALUE_STRING;
	/**
	 * <span class="en-US">Data source allows connection pooling</span>
	 * <span class="zh-CN">数据源允许连接池</span>
	 */
	@XmlElement(name = "pooled")
	private boolean pooled = Boolean.FALSE;
	/**
	 * <span class="en-US">Minimum number of connections in the connection pool</span>
	 * <span class="zh-CN">连接池的最小连接数</span>
	 */
	@XmlElement(name = "min_connections")
	private int minConnections = BrainCommons.DEFAULT_MIN_CONNECTIONS;
	/**
	 * <span class="en-US">Maximum number of connections in the connection pool</span>
	 * <span class="zh-CN">连接池的最大连接数</span>
	 */
	@XmlElement(name = "max_connections")
	private int maxConnections = BrainCommons.DEFAULT_MAX_CONNECTIONS;

	/**
	 * <h3 class="en-US">Getter method for data source name</h3>
	 * <h3 class="zh-CN">数据源名称的Getter方法</h3>
	 *
	 * @return <span class="en-US">Data source name</span>
	 * <span class="zh-CN">数据源名称</span>
	 */
	public String getSchemaName() {
		return this.schemaName;
	}

	/**
	 * <h3 class="en-US">Setter method for data source name</h3>
	 * <h3 class="zh-CN">数据源名称的Setter方法</h3>
	 *
	 * @param schemaName <span class="en-US">Data source name</span>
	 *                   <span class="zh-CN">数据源名称</span>
	 */
	public void setSchemaName(final String schemaName) {
		this.schemaName = schemaName;
	}

	/**
	 * <h3 class="en-US">Getter method for default data source flag</h3>
	 * <h3 class="zh-CN">默认数据源标记的Getter方法</h3>
	 *
	 * @return <span class="en-US">Default data source flag</span>
	 * <span class="zh-CN">默认数据源标记</span>
	 */
	public boolean isDefaultSchema() {
		return this.defaultSchema;
	}

	/**
	 * <h3 class="en-US">Setter method for default data source flag</h3>
	 * <h3 class="zh-CN">默认数据源标记的Setter方法</h3>
	 *
	 * @param defaultSchema <span class="en-US">Default data source flag</span>
	 *                      <span class="zh-CN">默认数据源标记</span>
	 */
	public void setDefaultSchema(final boolean defaultSchema) {
		this.defaultSchema = defaultSchema;
	}

	/**
	 * <h3 class="en-US">Getter method for data source dialect name</h3>
	 * <h3 class="zh-CN">数据源方言名称的Getter方法</h3>
	 *
	 * @return <span class="en-US">Data source dialect name</span>
	 * <span class="zh-CN">数据源方言名称</span>
	 */
	public String getDialectName() {
		return this.dialectName;
	}

	/**
	 * <h3 class="en-US">Setter method for data source dialect name</h3>
	 * <h3 class="zh-CN">数据源方言名称的Setter方法</h3>
	 *
	 * @param dialectName <span class="en-US">Data source dialect name</span>
	 *                    <span class="zh-CN">数据源方言名称</span>
	 */
	public void setDialectName(final String dialectName) {
		this.dialectName = dialectName;
	}

	/**
	 * <h3 class="en-US">Getter method for trust certificate store configuration information</h3>
	 * <h3 class="zh-CN">信任证书库配置信息的Getter方法</h3>
	 *
	 * @return <span class="en-US">Trust certificate store configuration information</span>
	 * <span class="zh-CN">信任证书库配置信息</span>
	 */
	public TrustStore getTrustStore() {
		return this.trustStore;
	}

	/**
	 * <h3 class="en-US">Setter method for trust certificate store configuration information</h3>
	 * <h3 class="zh-CN">信任证书库配置信息的Setter方法</h3>
	 *
	 * @param trustStore <span class="en-US">Trust certificate store configuration information</span>
	 *                   <span class="zh-CN">信任证书库配置信息</span>
	 */
	public void setTrustStore(final TrustStore trustStore) {
		this.trustStore = trustStore;
	}

	/**
	 * <h3 class="en-US">Getter method for identity authentication configuration information</h3>
	 * <h3 class="zh-CN">身份认证配置信息的Getter方法</h3>
	 *
	 * @return <span class="en-US">Identity authentication configuration information</span>
	 * <span class="zh-CN">身份认证配置信息</span>
	 */
	public Authentication getAuthentication() {
		return this.authentication;
	}

	/**
	 * <h3 class="en-US">Setter method for identity authentication configuration information</h3>
	 * <h3 class="zh-CN">身份认证配置信息的Setter方法</h3>
	 *
	 * @param authentication <span class="en-US">Identity authentication configuration information</span>
	 *                       <span class="zh-CN">身份认证配置信息</span>
	 */
	public void setAuthentication(final Authentication authentication) {
		this.authentication = authentication;
	}

	/**
	 * <h3 class="en-US">Getter method for low query timeout</h3>
	 * <h3 class="zh-CN">慢查询的临界时间的Getter方法</h3>
	 *
	 * @return <span class="en-US">Low query timeout (Unit: milliseconds)</span>
	 * <span class="zh-CN">慢查询的临界时间（单位：毫秒）</span>
	 */
	public long getLowQueryTimeout() {
		return this.lowQueryTimeout;
	}

	/**
	 * <h3 class="en-US">Setter method for low query timeout</h3>
	 * <h3 class="zh-CN">慢查询的临界时间的Setter方法</h3>
	 *
	 * @param lowQueryTimeout <span class="en-US">Low query timeout (Unit: milliseconds)</span>
	 *                        <span class="zh-CN">慢查询的临界时间（单位：毫秒）</span>
	 */
	public void setLowQueryTimeout(final long lowQueryTimeout) {
		this.lowQueryTimeout = lowQueryTimeout;
	}

	/**
	 * <h3 class="en-US">Getter method for timeout value of connection validate</h3>
	 * <h3 class="zh-CN">连接检查超时时间的Getter方法</h3>
	 *
	 * @return <span class="en-US">Timeout value of connection validate (Unit: seconds)</span>
	 * <span class="zh-CN">连接检查超时时间（单位：秒）</span>
	 */
	public int getValidateTimeout() {
		return this.validateTimeout;
	}

	/**
	 * <h3 class="en-US">Setter method for timeout value of connection validate</h3>
	 * <h3 class="zh-CN">连接检查超时时间的Setter方法</h3>
	 *
	 * @param validateTimeout <span class="en-US">Timeout value of connection validate (Unit: seconds)</span>
	 *                        <span class="zh-CN">连接检查超时时间（单位：秒）</span>
	 */
	public void setValidateTimeout(final int validateTimeout) {
		this.validateTimeout = validateTimeout;
	}

	/**
	 * <h3 class="en-US">Getter method for timeout value of create connection</h3>
	 * <h3 class="zh-CN">建立连接超时时间的Getter方法</h3>
	 *
	 * @return <span class="en-US">Timeout value of create connection (Unit: seconds)</span>
	 * <span class="zh-CN">建立连接超时时间（单位：秒）</span>
	 */
	public int getConnectTimeout() {
		return this.connectTimeout;
	}

	/**
	 * <h3 class="en-US">Setter method for timeout value of create connection</h3>
	 * <h3 class="zh-CN">建立连接超时时间的Setter方法</h3>
	 *
	 * @param connectTimeout <span class="en-US">Timeout value of create connection (Unit: seconds)</span>
	 *                       <span class="zh-CN">建立连接超时时间（单位：秒）</span>
	 */
	public void setConnectTimeout(final int connectTimeout) {
		this.connectTimeout = connectTimeout;
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
	public String getShardingDefault() {
		return this.shardingDefault;
	}

	/**
	 * <h3 class="en-US">Setter method for default database sharding value</h3>
	 * <h3 class="zh-CN">默认数据库分片值的Setter方法</h3>
	 *
	 * @param shardingDefault <span class="en-US">Default database sharding value</span>
	 *                        <span class="zh-CN">默认数据库分片值</span>
	 */
	public void setShardingDefault(final String shardingDefault) {
		this.shardingDefault = shardingDefault;
	}

	/**
	 * <h3 class="en-US">Getter method for data source allows connection pooling</h3>
	 * <h3 class="zh-CN">数据源允许连接池的Getter方法</h3>
	 *
	 * @return <span class="en-US">Data source allows connection pooling</span>
	 * <span class="zh-CN">数据源允许连接池</span>
	 */
	public boolean isPooled() {
		return this.pooled;
	}

	/**
	 * <h3 class="en-US">Setter method for data source allows connection pooling</h3>
	 * <h3 class="zh-CN">数据源允许连接池的Setter方法</h3>
	 *
	 * @param pooled <span class="en-US">Data source allows connection pooling</span>
	 *               <span class="zh-CN">数据源允许连接池</span>
	 */
	public void setPooled(final boolean pooled) {
		this.pooled = pooled;
	}

	/**
	 * <h3 class="en-US">Getter method for minimum number of connections in the connection pool</h3>
	 * <h3 class="zh-CN">连接池的最小连接数的Getter方法</h3>
	 *
	 * @return <span class="en-US">Minimum number of connections in the connection pool</span>
	 * <span class="zh-CN">连接池的最小连接数</span>
	 */
	public int getMinConnections() {
		return this.minConnections;
	}

	/**
	 * <h3 class="en-US">Setter method for minimum number of connections in the connection pool</h3>
	 * <h3 class="zh-CN">连接池的最小连接数的Setter方法</h3>
	 *
	 * @param minConnections <span class="en-US">Minimum number of connections in the connection pool</span>
	 *                       <span class="zh-CN">连接池的最小连接数</span>
	 */
	public void setMinConnections(final int minConnections) {
		this.minConnections = minConnections;
	}

	/**
	 * <h3 class="en-US">Getter method for maximum number of connections in the connection pool</h3>
	 * <h3 class="zh-CN">连接池的最大连接数的Getter方法</h3>
	 *
	 * @return <span class="en-US">Maximum number of connections in the connection pool</span>
	 * <span class="zh-CN">连接池的最大连接数</span>
	 */
	public int getMaxConnections() {
		return this.maxConnections;
	}

	/**
	 * <h3 class="en-US">Setter method for maximum number of connections in the connection pool</h3>
	 * <h3 class="zh-CN">连接池的最大连接数的Setter方法</h3>
	 *
	 * @param maxConnections <span class="en-US">Maximum number of connections in the connection pool</span>
	 *                       <span class="zh-CN">连接池的最大连接数</span>
	 */
	public void setMaxConnections(final int maxConnections) {
		this.maxConnections = maxConnections;
	}
}
