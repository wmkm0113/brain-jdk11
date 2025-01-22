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

package org.nervousync.brain.configs.builder;

import jakarta.annotation.Nonnull;
import org.nervousync.brain.configs.auth.Authentication;
import org.nervousync.brain.configs.auth.impl.TrustStoreAuthentication;
import org.nervousync.brain.configs.auth.impl.UserAuthentication;
import org.nervousync.brain.configs.auth.impl.X509Authentication;
import org.nervousync.brain.configs.schema.SchemaConfig;
import org.nervousync.brain.configs.schema.impl.DistributeSchemaConfig;
import org.nervousync.brain.configs.schema.impl.JdbcSchemaConfig;
import org.nervousync.brain.configs.schema.impl.RemoteSchemaConfig;
import org.nervousync.brain.configs.secure.TrustStore;
import org.nervousync.brain.configs.server.ServerInfo;
import org.nervousync.brain.enumerations.remote.RemoteType;
import org.nervousync.builder.AbstractBuilder;
import org.nervousync.builder.ParentBuilder;
import org.nervousync.commons.Globals;
import org.nervousync.proxy.ProxyConfig;
import org.nervousync.proxy.ProxyConfigBuilder;
import org.nervousync.utils.DateTimeUtils;
import org.nervousync.utils.FileUtils;
import org.nervousync.utils.ObjectUtils;
import org.nervousync.utils.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * <h2 class="en-US">Abstract class of data schema configure information builder</h2>
 * <h2 class="zh-CN">数据源配置信息构建器的抽象类</h2>
 *
 * @param <T> <span class="en-US">Configuration information generic class</span>
 *            <span class="zh-CN">配置信息泛型类</span>
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision : 1.0.0 $ $Date: Apr 10, 2018 15:48:19 $
 */
public abstract class SchemaConfigBuilder<T extends SchemaConfig> extends AbstractBuilder<T> implements ParentBuilder {

	/**
	 * <span class="en-US">Configure information instance object</span>
	 * <span class="zh-CN">配置信息实例对象</span>
	 */
	protected final T schemaConfig;
	/**
	 * <h2 class="en-US">Configure information modified flag</h2>
	 * <h2 class="zh-CN">配置信息修改标记</h2>
	 */
	protected boolean modified = Boolean.FALSE;

	/**
	 * <h3 class="en-US">Constructor method for abstract class of data schema configure information builder</h3>
	 * <h3 class="zh-CN">数据源配置信息构建器抽象类的构造方法</h3>
	 *
	 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
	 *                      <span class="zh-CN">父构建器实例对象</span>
	 * @param schemaConfig  <span class="en-US">Configure information instance object</span>
	 *                      <span class="zh-CN">配置信息实例对象</span>
	 */
	protected SchemaConfigBuilder(final BrainConfigureBuilder parentBuilder, final T schemaConfig) {
		super(parentBuilder);
		this.schemaConfig = schemaConfig;
	}

	/**
	 * <h3 class="en-US">Generate basic identity authentication information builder instance object based on existing identity authentication information</h3>
	 * <h3 class="zh-CN">根据现有的身份认证信息生成基本身份认证信息构建器实例对象</h3>
	 *
	 * @return <span class="en-US">Builder implementation class of basic authentication information instance object</span>
	 * <span class="zh-CN">基本身份认证信息构建器实例对象</span>
	 */
	public final AuthenticationBuilder.UserAuthenticationBuilder userAuthenticationBuilder() {
		return new AuthenticationBuilder.UserAuthenticationBuilder(this,
				Optional.ofNullable(this.schemaConfig.getAuthentication())
						.filter(authentication -> authentication instanceof UserAuthentication)
						.map(authentication -> (UserAuthentication) authentication)
						.orElse(new UserAuthentication()));
	}

	/**
	 * <h3 class="en-US">Generate a builder instance object that uses the trust store authentication information
	 * in the certificate store based on the existing authentication information.</h3>
	 * <h3 class="zh-CN">根据现有的身份认证信息生成使用证书库中X.509证书认证信息的构建器实例对象</h3>
	 *
	 * @return <span class="en-US">Builder implementation class of use the authentication information of the X.509 certificate in the certificate store</span>
	 * <span class="zh-CN">使用证书库中X.509证书的认证信息的构建器</span>
	 */
	public final AuthenticationBuilder.TrustStoreAuthenticationBuilder trustStoreAuthenticationBuilder() {
		return new AuthenticationBuilder.TrustStoreAuthenticationBuilder(this,
				Optional.ofNullable(this.schemaConfig.getAuthentication())
						.filter(authentication -> authentication instanceof TrustStoreAuthentication)
						.map(authentication -> (TrustStoreAuthentication) authentication)
						.orElse(new TrustStoreAuthentication()));
	}

	/**
	 * <h3 class="en-US">Generate a builder instance object that uses the X.509 certificate authentication information.</h3>
	 * <h3 class="zh-CN">根据现有的身份认证信息生成X.509证书认证信息的构建器实例对象</h3>
	 *
	 * @return <span class="en-US">Builder implementation class of use the authentication information of the X.509 certificate</span>
	 * <span class="zh-CN">使用X.509证书的认证信息的构建器</span>
	 */
	public final AuthenticationBuilder.X509AuthenticationBuilder x509AuthenticationBuilder() {
		return new AuthenticationBuilder.X509AuthenticationBuilder(this,
				Optional.ofNullable(this.schemaConfig.getAuthentication())
						.filter(authentication -> authentication instanceof X509Authentication)
						.map(authentication -> (X509Authentication) authentication)
						.orElse(new X509Authentication()));
	}

	@Override
	public void confirm(final Object object) {
		if (object instanceof Authentication) {
			Authentication authentication = this.schemaConfig.getAuthentication();
			if (authentication == null
					|| !ObjectUtils.nullSafeEquals(authentication.getAuthType(), ((Authentication) object).getAuthType())
					|| authentication.getLastModified() != ((Authentication) object).getLastModified()) {
				this.schemaConfig.setAuthentication((Authentication) object);
				this.modified = Boolean.TRUE;
			}
		}
	}

	@Override
	public final T confirm() {
		if (this.modified) {
			this.schemaConfig.setLastModified(DateTimeUtils.currentUTCTimeMillis());
		}
		return this.schemaConfig;
	}

	/**
	 * <h3 class="en-US">Set the dialect name used</h3>
	 * <h3 class="zh-CN">设置使用的方言名称</h3>
	 *
	 * @param dialectName  <span class="en-US">Data source dialect name</span>
	 *                     <span class="zh-CN">数据源方言名称</span>
	 * @param builderClass <span class="en-US">Returned object type</span>
	 *                     <span class="zh-CN">返回的对象类型</span>
	 * @param <B>          <span class="en-US">Generic class that returns the object type</span>
	 *                     <span class="zh-CN">返回对象类型的泛型类</span>
	 * @return <span class="en-US">Instance object of specified return type</span>
	 * <span class="zh-CN">指定返回类型的实例对象</span>
	 */
	protected final <B> B dialect(final String dialectName, final Class<B> builderClass) {
		if (StringUtils.isEmpty(dialectName)
				|| ObjectUtils.nullSafeEquals(this.schemaConfig.getDialectName(), dialectName)) {
			return builderClass.cast(this);
		}
		this.schemaConfig.setDialectName(dialectName);
		this.modified = Boolean.TRUE;
		return builderClass.cast(this);
	}

	/**
	 * <h3 class="en-US">Set the trust store information</h3>
	 * <h3 class="zh-CN">设置信任证书库信息</h3>
	 *
	 * @param storePath     <span class="en-US">Trust certificate store path</span>
	 *                      <span class="zh-CN">信任证书库地址</span>
	 * @param storePassword <span class="en-US">Trust certificate store password</span>
	 *                      <span class="zh-CN">信任证书库密码</span>
	 * @param builderClass  <span class="en-US">Returned object type</span>
	 *                      <span class="zh-CN">返回的对象类型</span>
	 * @param <B>           <span class="en-US">Generic class that returns the object type</span>
	 *                      <span class="zh-CN">返回对象类型的泛型类</span>
	 * @return <span class="en-US">Instance object of specified return type</span>
	 * <span class="zh-CN">指定返回类型的实例对象</span>
	 */
	protected final <B> B trustStore(final String storePath, final String storePassword, final Class<B> builderClass) {
		if (StringUtils.notBlank(storePath)) {
			TrustStore trustStore = this.schemaConfig.getTrustStore();
			if (trustStore == null) {
				trustStore = new TrustStore();
				this.modified = Boolean.TRUE;
			}

			if (!ObjectUtils.nullSafeEquals(trustStore.getTrustStorePath(), storePath)) {
				trustStore.setTrustStorePath(storePath);
				this.modified = Boolean.TRUE;
			}

			if (StringUtils.notBlank(storePassword)
					&& !ObjectUtils.nullSafeEquals(trustStore.getTrustStorePassword(), storePassword)) {
				trustStore.setTrustStorePassword(storePassword);
				this.modified = Boolean.TRUE;
			}

			this.schemaConfig.setTrustStore(trustStore);
		}
		return builderClass.cast(this);
	}

	/**
	 * <h3 class="en-US">Set slow query-critical time</h3>
	 * <h3 class="zh-CN">设置慢查询临界时间</h3>
	 *
	 * @param lowQueryTimeout <span class="en-US">Low query timeout (Unit: milliseconds)</span>
	 *                        <span class="zh-CN">慢查询的临界时间（单位：毫秒）</span>
	 * @param builderClass    <span class="en-US">Returned object type</span>
	 *                        <span class="zh-CN">返回的对象类型</span>
	 * @param <B>             <span class="en-US">Generic class that returns the object type</span>
	 *                        <span class="zh-CN">返回对象类型的泛型类</span>
	 * @return <span class="en-US">Instance object of specified return type</span>
	 * <span class="zh-CN">指定返回类型的实例对象</span>
	 */
	protected final <B> B lowQuery(final long lowQueryTimeout, final Class<B> builderClass) {
		if (lowQueryTimeout > 0 && this.schemaConfig.getLowQueryTimeout() != lowQueryTimeout) {
			this.schemaConfig.setLowQueryTimeout(lowQueryTimeout);
			this.modified = Boolean.TRUE;
		}
		return builderClass.cast(this);
	}

	/**
	 * <h3 class="en-US">Set value of timeout</h3>
	 * <h3 class="zh-CN">设置超时时间</h3>
	 *
	 * @param validateTimeout <span class="en-US">Timeout value of connection validate (Unit: seconds)</span>
	 *                        <span class="zh-CN">连接检查超时时间（单位：秒）</span>
	 * @param connectTimeout  <span class="en-US">Timeout value of create connection (Unit: seconds)</span>
	 *                        <span class="zh-CN">建立连接超时时间（单位：秒）</span>
	 * @param builderClass    <span class="en-US">Returned object type</span>
	 *                        <span class="zh-CN">返回的对象类型</span>
	 * @param <B>             <span class="en-US">Generic class that returns the object type</span>
	 *                        <span class="zh-CN">返回对象类型的泛型类</span>
	 * @return <span class="en-US">Instance object of specified return type</span>
	 * <span class="zh-CN">指定返回类型的实例对象</span>
	 */
	protected final <B> B timeout(final int validateTimeout, final int connectTimeout, final Class<B> builderClass) {
		if (validateTimeout > 0 && this.schemaConfig.getValidateTimeout() != validateTimeout) {
			this.schemaConfig.setValidateTimeout(validateTimeout);
			this.modified = Boolean.TRUE;
		}
		if (connectTimeout > 0 && this.schemaConfig.getConnectTimeout() != connectTimeout) {
			this.schemaConfig.setConnectTimeout(connectTimeout);
			this.modified = Boolean.TRUE;
		}
		return builderClass.cast(this);
	}

	/**
	 * <h3 class="en-US">Set connection pool configure information</h3>
	 * <h3 class="zh-CN">设置连接池配置信息</h3>
	 *
	 * @param pooled         <span class="en-US">Data source allows connection pooling</span>
	 *                       <span class="zh-CN">数据源允许连接池</span>
	 * @param minConnections <span class="en-US">Minimum number of connections in the connection pool</span>
	 *                       <span class="zh-CN">连接池的最小连接数</span>
	 * @param maxConnections <span class="en-US">Maximum number of connections in the connection pool</span>
	 *                       <span class="zh-CN">连接池的最大连接数</span>
	 * @param builderClass   <span class="en-US">Returned object type</span>
	 *                       <span class="zh-CN">返回的对象类型</span>
	 * @param <B>            <span class="en-US">Generic class that returns the object type</span>
	 *                       <span class="zh-CN">返回对象类型的泛型类</span>
	 * @return <span class="en-US">Instance object of specified return type</span>
	 * <span class="zh-CN">指定返回类型的实例对象</span>
	 */
	protected final <B> B connectionPool(final boolean pooled, final int minConnections, final int maxConnections,
	                                     final Class<B> builderClass) {
		this.schemaConfig.setPooled(pooled);
		if (pooled) {
			this.schemaConfig.setMinConnections(minConnections);
			this.schemaConfig.setMaxConnections(maxConnections);
		} else {
			this.schemaConfig.setMinConnections(Globals.DEFAULT_VALUE_INT);
			this.schemaConfig.setMaxConnections(Globals.DEFAULT_VALUE_INT);
		}
		return builderClass.cast(this);
	}

	/**
	 * <h3 class="en-US">Update or insert server information</h3>
	 * <h3 class="zh-CN">修改或新增服务器等级信息</h3>
	 *
	 * @param serverList    <span class="en-US">Exists server information list</span>
	 *                      <span class="zh-CN">现有服务器列表</span>
	 * @param serverName    <span class="en-US">Server name</span>
	 *                      <span class="zh-CN">服务器名称</span>
	 * @param serverAddress <span class="en-US">Server address</span>
	 *                      <span class="zh-CN">服务器地址</span>
	 * @param serverPort    <span class="en-US">Server port number</span>
	 *                      <span class="zh-CN">服务器端口号</span>
	 * @param serverLevel   <span class="en-US">Server level</span>
	 *                      <span class="zh-CN">服务器等级</span>
	 */
	protected final void upsertServer(@Nonnull final List<ServerInfo> serverList, final String serverName,
	                                  final String serverAddress, final int serverPort, final int serverLevel) {
		if (serverList.stream().noneMatch(serverInfo -> serverInfo.match(serverAddress, serverPort))) {
			ServerInfo serverInfo = new ServerInfo();
			serverInfo.setServerName(serverName);
			serverInfo.setServerAddress(serverAddress);
			serverInfo.setServerPort(serverPort);
			serverInfo.setServerLevel(serverLevel);
			serverList.add(serverInfo);
			this.modified = Boolean.TRUE;
		} else {
			serverList.replaceAll(serverInfo -> {
				if (serverInfo.match(serverAddress, serverPort)) {
					if (serverInfo.getServerLevel() != serverLevel) {
						serverInfo.setServerLevel(serverLevel);
						this.modified = Boolean.TRUE;
					}
					if (StringUtils.notBlank(serverName)
							&& ObjectUtils.nullSafeEquals(serverInfo.getServerName(), serverName)) {
						serverInfo.setServerName(serverName);
						this.modified = Boolean.TRUE;
					}
				}
				return serverInfo;
			});
		}
	}

	/**
	 * <h2 class="en-US">Implementation class of distribute data schema configure information builder</h2>
	 * <h2 class="zh-CN">分布式数据源配置信息构建器实现类</h2>
	 *
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision : 1.0.0 $ $Date: Apr 10, 2018 15:48:19 $
	 */
	public static final class DistributeConfigBuilder extends SchemaConfigBuilder<DistributeSchemaConfig> {

		/**
		 * <h3 class="en-US">Constructor method for implementation class of distribute data schema configure information builder</h3>
		 * <h3 class="zh-CN">分布式数据源配置信息构建器实现类的构造方法</h3>
		 *
		 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
		 *                      <span class="zh-CN">父构建器实例对象</span>
		 * @param schemaConfig  <span class="en-US">Configure information instance object</span>
		 *                      <span class="zh-CN">配置信息实例对象</span>
		 */
		DistributeConfigBuilder(final BrainConfigureBuilder parentBuilder,
		                        final DistributeSchemaConfig schemaConfig) {
			super(parentBuilder, schemaConfig);
		}

		/**
		 * <h3 class="en-US">Set the dialect name used</h3>
		 * <h3 class="zh-CN">设置使用的方言名称</h3>
		 *
		 * @param dialectName <span class="en-US">Data source dialect name</span>
		 *                    <span class="zh-CN">数据源方言名称</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public DistributeConfigBuilder dialect(final String dialectName) {
			return super.dialect(dialectName, DistributeConfigBuilder.class);
		}

		/**
		 * <h3 class="en-US">Set the trust store information</h3>
		 * <h3 class="zh-CN">设置信任证书库信息</h3>
		 *
		 * @param storePath     <span class="en-US">Trust certificate store path</span>
		 *                      <span class="zh-CN">信任证书库地址</span>
		 * @param storePassword <span class="en-US">Trust certificate store password</span>
		 *                      <span class="zh-CN">信任证书库密码</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public DistributeConfigBuilder trustStore(final String storePath, final String storePassword) {
			return super.trustStore(storePath, storePassword, DistributeConfigBuilder.class);
		}

		/**
		 * <h3 class="en-US">Set slow query-critical time</h3>
		 * <h3 class="zh-CN">设置慢查询临界时间</h3>
		 *
		 * @param lowQueryTimeout <span class="en-US">Low query timeout (Unit: milliseconds)</span>
		 *                        <span class="zh-CN">慢查询的临界时间（单位：毫秒）</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public DistributeConfigBuilder lowQuery(final long lowQueryTimeout) {
			return super.lowQuery(lowQueryTimeout, DistributeConfigBuilder.class);
		}

		/**
		 * <h3 class="en-US">Set request timeout</h3>
		 * <h3 class="zh-CN">设置请求超时时间</h3>
		 *
		 * @param requestTimeout <span class="en-US">Request timeout value</span>
		 *                       <span class="zh-CN">请求超时时间</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public DistributeConfigBuilder request(final int requestTimeout) {
			if (requestTimeout > 0 && this.schemaConfig.getRequestTimeout() != requestTimeout) {
				this.schemaConfig.setRequestTimeout(requestTimeout);
				this.modified = Boolean.TRUE;
			}
			return this;
		}

		/**
		 * <h3 class="en-US">Set value of timeout</h3>
		 * <h3 class="zh-CN">设置超时时间</h3>
		 *
		 * @param validateTimeout <span class="en-US">Timeout value of connection validate (Unit: seconds)</span>
		 *                        <span class="zh-CN">连接检查超时时间（单位：秒）</span>
		 * @param connectTimeout  <span class="en-US">Timeout value of create connection (Unit: seconds)</span>
		 *                        <span class="zh-CN">建立连接超时时间（单位：秒）</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public DistributeConfigBuilder timeout(final int validateTimeout, final int connectTimeout) {
			return super.timeout(validateTimeout, connectTimeout, DistributeConfigBuilder.class);
		}

		/**
		 * <h3 class="en-US">Set connection pool configure information</h3>
		 * <h3 class="zh-CN">设置连接池配置信息</h3>
		 *
		 * @param pooled         <span class="en-US">Data source allows connection pooling</span>
		 *                       <span class="zh-CN">数据源允许连接池</span>
		 * @param minConnections <span class="en-US">Minimum number of connections in the connection pool</span>
		 *                       <span class="zh-CN">连接池的最小连接数</span>
		 * @param maxConnections <span class="en-US">Maximum number of connections in the connection pool</span>
		 *                       <span class="zh-CN">连接池的最大连接数</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public DistributeConfigBuilder connectionPool(final boolean pooled, final int minConnections,
		                                              final int maxConnections) {
			return super.connectionPool(pooled, minConnections, maxConnections, DistributeConfigBuilder.class);
		}

		/**
		 * <h3 class="en-US">Configure server information</h3>
		 * <h3 class="zh-CN">设置服务器相关信息</h3>
		 *
		 * @param serverInfo <span class="en-US">Database server information</span>
		 *                   <span class="zh-CN">数据库服务器信息</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public DistributeConfigBuilder servers(final String serverInfo) {
			List<ServerInfo> serverList = this.schemaConfig.getServerList();
			for (String serverData : StringUtils.tokenizeToStringArray(serverInfo, Character.toString(FileUtils.LF))) {
				String[] dataInfo = StringUtils.tokenizeToStringArray(serverData, "|");
				if (dataInfo.length >= 3) {
					String serverName = dataInfo[0], serverAddress = dataInfo[1];
					int serverPort = Integer.parseInt(dataInfo[2]);
					int serverLevel = dataInfo.length == 4 ? Integer.parseInt(dataInfo[3]) : Globals.DEFAULT_VALUE_INT;
					super.upsertServer(serverList, serverName, serverAddress, serverPort, serverLevel);
				}
			}
			return this;
		}

		/**
		 * <h3 class="en-US">Add new server information</h3>
		 * <h3 class="zh-CN">添加新的服务器信息</h3>
		 *
		 * @param serverAddress <span class="en-US">Server address</span>
		 *                      <span class="zh-CN">服务器地址</span>
		 * @param serverPort    <span class="en-US">Server port number</span>
		 *                      <span class="zh-CN">服务器端口号</span>
		 * @param serverLevel   <span class="en-US">Server level</span>
		 *                      <span class="zh-CN">服务器等级</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public DistributeConfigBuilder addServer(final String serverAddress,
		                                         final int serverPort, final int serverLevel) {
			return this.addServer(Globals.DEFAULT_VALUE_STRING, serverAddress, serverPort, serverLevel);
		}

		/**
		 * <h3 class="en-US">Add new server information</h3>
		 * <h3 class="zh-CN">添加新的服务器信息</h3>
		 *
		 * @param serverName    <span class="en-US">Server name</span>
		 *                      <span class="zh-CN">服务器名称</span>
		 * @param serverAddress <span class="en-US">Server address</span>
		 *                      <span class="zh-CN">服务器地址</span>
		 * @param serverPort    <span class="en-US">Server port number</span>
		 *                      <span class="zh-CN">服务器端口号</span>
		 * @param serverLevel   <span class="en-US">Server level</span>
		 *                      <span class="zh-CN">服务器等级</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public DistributeConfigBuilder addServer(final String serverName, final String serverAddress,
		                                         final int serverPort, final int serverLevel) {
			List<ServerInfo> serverList = this.schemaConfig.getServerList();
			super.upsertServer(serverList, serverName, serverAddress, serverPort, serverLevel);
			this.schemaConfig.setServerList(serverList);
			return this;
		}

		/**
		 * <h3 class="en-US">Update server level information</h3>
		 * <h3 class="zh-CN">修改服务器等级信息</h3>
		 *
		 * @param serverAddress <span class="en-US">Server address</span>
		 *                      <span class="zh-CN">服务器地址</span>
		 * @param serverPort    <span class="en-US">Server port number</span>
		 *                      <span class="zh-CN">服务器端口号</span>
		 * @param serverLevel   <span class="en-US">Server level</span>
		 *                      <span class="zh-CN">服务器等级</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public DistributeConfigBuilder serverLevel(final String serverAddress, final int serverPort,
		                                           final int serverLevel) {
			List<ServerInfo> serverList = this.schemaConfig.getServerList();
			super.upsertServer(serverList, Globals.DEFAULT_VALUE_STRING, serverAddress, serverPort, serverLevel);
			this.schemaConfig.setServerList(serverList);
			return this;
		}

		/**
		 * <h3 class="en-US">Delete server level information</h3>
		 * <h3 class="zh-CN">删除服务器等级信息</h3>
		 *
		 * @param serverAddress <span class="en-US">Server address</span>
		 *                      <span class="zh-CN">服务器地址</span>
		 * @param serverPort    <span class="en-US">Server port number</span>
		 *                      <span class="zh-CN">服务器端口号</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public DistributeConfigBuilder removeServer(final String serverAddress, final int serverPort) {
			List<ServerInfo> serverList = this.schemaConfig.getServerList();
			if (serverList.removeIf(serverInfo -> serverInfo.match(serverAddress, serverPort))) {
				this.schemaConfig.setServerList(serverList);
				this.modified = Boolean.TRUE;
			}
			return this;
		}

		/**
		 * <h3 class="en-US">Set database name</h3>
		 * <h3 class="zh-CN">设置数据库名称</h3>
		 *
		 * @param databaseName <span class="en-US">Database name</span>
		 *                     <span class="zh-CN">数据库名称</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public DistributeConfigBuilder databaseName(final String databaseName) {
			if (StringUtils.notBlank(databaseName)
					&& !ObjectUtils.nullSafeEquals(this.schemaConfig.getDatabaseName(), databaseName)) {
				this.schemaConfig.setDatabaseName(databaseName);
				this.modified = Boolean.TRUE;
			}
			return this;
		}

		/**
		 * <h3 class="en-US">Set using SSL when connect to server</h3>
		 * <h3 class="zh-CN">设置使用SSL连接</h3>
		 *
		 * @param useSsl <span class="en-US">Using SSL when connect to server</span>
		 *               <span class="zh-CN">使用SSL连接</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public DistributeConfigBuilder useSsl(final boolean useSsl) {
			if (!ObjectUtils.nullSafeEquals(this.schemaConfig.isUseSsl(), useSsl)) {
				this.schemaConfig.setUseSsl(useSsl);
				this.modified = Boolean.TRUE;
			}
			return this;
		}

		/**
		 * <h3 class="en-US">Set the maximum size of the prepared statement</h3>
		 * <h3 class="zh-CN">设置查询分析器的最大缓存结果</h3>
		 *
		 * @param cachedLimitSize <span class="en-US">Maximum size of prepared statement</span>
		 *                        <span class="zh-CN">查询分析器的最大缓存结果</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public DistributeConfigBuilder cacheSize(final int cachedLimitSize) {
			if (cachedLimitSize > 0 && this.schemaConfig.getCachedLimitSize() != cachedLimitSize) {
				this.schemaConfig.setCachedLimitSize(cachedLimitSize);
				this.modified = Boolean.TRUE;
			}
			return this;
		}
	}

	/**
	 * <h2 class="en-US">Implementation class of JDBC data schema configure information builder</h2>
	 * <h2 class="zh-CN">JDBC数据源配置信息构建器实现类</h2>
	 *
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision : 1.0.0 $ $Date: Apr 10, 2018 15:48:19 $
	 */
	public static final class JdbcConfigBuilder extends SchemaConfigBuilder<JdbcSchemaConfig> {

		/**
		 * <h3 class="en-US">Constructor method for implementation class of JDBC data schema configure information builder</h3>
		 * <h3 class="zh-CN">JDBC数据源配置信息构建器实现类的构造函数</h3>
		 *
		 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
		 *                      <span class="zh-CN">父构建器实例对象</span>
		 * @param schemaConfig  <span class="en-US">Configure information instance object</span>
		 *                      <span class="zh-CN">配置信息实例对象</span>
		 */
		JdbcConfigBuilder(final BrainConfigureBuilder parentBuilder, final JdbcSchemaConfig schemaConfig) {
			super(parentBuilder, schemaConfig);
		}

		/**
		 * <h3 class="en-US">Set the dialect name used</h3>
		 * <h3 class="zh-CN">设置使用的方言名称</h3>
		 *
		 * @param dialectName <span class="en-US">Data source dialect name</span>
		 *                    <span class="zh-CN">数据源方言名称</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public JdbcConfigBuilder dialect(final String dialectName) {
			return super.dialect(dialectName, JdbcConfigBuilder.class);
		}

		/**
		 * <h3 class="en-US">Set the trust store information</h3>
		 * <h3 class="zh-CN">设置信任证书库信息</h3>
		 *
		 * @param storePath     <span class="en-US">Trust certificate store path</span>
		 *                      <span class="zh-CN">信任证书库地址</span>
		 * @param storePassword <span class="en-US">Trust certificate store password</span>
		 *                      <span class="zh-CN">信任证书库密码</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public JdbcConfigBuilder trustStore(final String storePath, final String storePassword) {
			return super.trustStore(storePath, storePassword, JdbcConfigBuilder.class);
		}

		/**
		 * <h3 class="en-US">Set slow query-critical time</h3>
		 * <h3 class="zh-CN">设置慢查询临界时间</h3>
		 *
		 * @param lowQueryTimeout <span class="en-US">Low query timeout (Unit: milliseconds)</span>
		 *                        <span class="zh-CN">慢查询的临界时间（单位：毫秒）</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public JdbcConfigBuilder lowQuery(final long lowQueryTimeout) {
			return super.lowQuery(lowQueryTimeout, JdbcConfigBuilder.class);
		}

		/**
		 * <h3 class="en-US">Set value of timeout</h3>
		 * <h3 class="zh-CN">设置超时时间</h3>
		 *
		 * @param validateTimeout <span class="en-US">Timeout value of connection validate (Unit: seconds)</span>
		 *                        <span class="zh-CN">连接检查超时时间（单位：秒）</span>
		 * @param connectTimeout  <span class="en-US">Timeout value of create connection (Unit: seconds)</span>
		 *                        <span class="zh-CN">建立连接超时时间（单位：秒）</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public JdbcConfigBuilder timeout(final int validateTimeout, final int connectTimeout) {
			return super.timeout(validateTimeout, connectTimeout, JdbcConfigBuilder.class);
		}

		/**
		 * <h3 class="en-US">Set connection pool configure information</h3>
		 * <h3 class="zh-CN">设置连接池配置信息</h3>
		 *
		 * @param pooled         <span class="en-US">Data source allows connection pooling</span>
		 *                       <span class="zh-CN">数据源允许连接池</span>
		 * @param minConnections <span class="en-US">Minimum number of connections in the connection pool</span>
		 *                       <span class="zh-CN">连接池的最小连接数</span>
		 * @param maxConnections <span class="en-US">Maximum number of connections in the connection pool</span>
		 *                       <span class="zh-CN">连接池的最大连接数</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public JdbcConfigBuilder connectionPool(final boolean pooled, final int minConnections,
		                                        final int maxConnections) {
			return super.connectionPool(pooled, minConnections, maxConnections, JdbcConfigBuilder.class);
		}

		/**
		 * <h3 class="en-US">Configure server information</h3>
		 * <h3 class="zh-CN">设置服务器相关信息</h3>
		 *
		 * @param serverArray <span class="en-US">Using server array</span>
		 *                    <span class="zh-CN">使用服务器组</span>
		 * @param serverInfo <span class="en-US">Database server information</span>
		 *                   <span class="zh-CN">数据库服务器信息</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public JdbcConfigBuilder servers(final boolean serverArray, final String serverInfo) {
			if (!ObjectUtils.nullSafeEquals(this.schemaConfig.isServerArray(), serverArray)) {
				this.schemaConfig.setServerArray(serverArray);
				this.modified = Boolean.TRUE;
			}
			List<ServerInfo> serverList = this.schemaConfig.getServerList();
			for (String serverData : StringUtils.tokenizeToStringArray(serverInfo, Character.toString(FileUtils.LF))) {
				String[] dataInfo = StringUtils.tokenizeToStringArray(serverData, "|");
				if (dataInfo.length >= 2) {
					if (!this.schemaConfig.isServerArray()) {
						serverList.clear();
					}
					String serverAddress = dataInfo[0];
					int serverPort = Integer.parseInt(dataInfo[1]);
					int serverLevel = dataInfo.length == 3 ? Integer.parseInt(dataInfo[2]) : Globals.DEFAULT_VALUE_INT;
					super.upsertServer(serverList, Globals.DEFAULT_VALUE_STRING, serverAddress, serverPort, serverLevel);
				}
			}
			return this;
		}

		/**
		 * <h3 class="en-US">Add new server information</h3>
		 * <h3 class="zh-CN">添加新的服务器信息</h3>
		 *
		 * @param serverAddress <span class="en-US">Server address</span>
		 *                      <span class="zh-CN">服务器地址</span>
		 * @param serverPort    <span class="en-US">Server port number</span>
		 *                      <span class="zh-CN">服务器端口号</span>
		 * @param serverLevel   <span class="en-US">Server level</span>
		 *                      <span class="zh-CN">服务器等级</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public JdbcConfigBuilder addServer(final String serverAddress,
		                                   final int serverPort, final int serverLevel) {
			List<ServerInfo> serverList = this.schemaConfig.getServerList();
			if (!this.schemaConfig.isServerArray() && !serverList.isEmpty()) {
				serverList.clear();
				this.modified = Boolean.TRUE;
			}
			super.upsertServer(serverList, Globals.DEFAULT_VALUE_STRING, serverAddress, serverPort, serverLevel);
			this.schemaConfig.setServerList(serverList);
			return this;
		}

		/**
		 * <h3 class="en-US">Update server level information</h3>
		 * <h3 class="zh-CN">修改服务器等级信息</h3>
		 *
		 * @param serverAddress <span class="en-US">Server address</span>
		 *                      <span class="zh-CN">服务器地址</span>
		 * @param serverPort    <span class="en-US">Server port number</span>
		 *                      <span class="zh-CN">服务器端口号</span>
		 * @param serverLevel   <span class="en-US">Server level</span>
		 *                      <span class="zh-CN">服务器等级</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public JdbcConfigBuilder serverLevel(final String serverAddress, final int serverPort,
		                                     final int serverLevel) {
			List<ServerInfo> serverList = this.schemaConfig.getServerList();
			super.upsertServer(serverList, Globals.DEFAULT_VALUE_STRING, serverAddress, serverPort, serverLevel);
			this.schemaConfig.setServerList(serverList);
			return this;
		}

		/**
		 * <h3 class="en-US">Delete server level information</h3>
		 * <h3 class="zh-CN">删除服务器等级信息</h3>
		 *
		 * @param serverAddress <span class="en-US">Server address</span>
		 *                      <span class="zh-CN">服务器地址</span>
		 * @param serverPort    <span class="en-US">Server port number</span>
		 *                      <span class="zh-CN">服务器端口号</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public JdbcConfigBuilder removeServer(final String serverAddress, final int serverPort) {
			List<ServerInfo> serverList = this.schemaConfig.getServerList();
			if (serverList.removeIf(serverInfo -> serverInfo.match(serverAddress, serverPort))) {
				this.schemaConfig.setServerList(serverList);
				this.modified = Boolean.TRUE;
			}
			return this;
		}

		/**
		 * <h3 class="en-US">Set sharding configure information</h3>
		 * <h3 class="zh-CN">设置分片配置信息</h3>
		 *
		 * @param sharding        <span class="en-US">Data source support sharding</span>
		 *                        <span class="zh-CN">数据源是否支持分片</span>
		 * @param shardingDefault <span class="en-US">Default database sharding value</span>
		 *                        <span class="zh-CN">默认数据库分片值</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public JdbcConfigBuilder sharding(final boolean sharding, final String shardingDefault) {
			if (!ObjectUtils.nullSafeEquals(this.schemaConfig.isSharding(), sharding)
					|| !ObjectUtils.nullSafeEquals(this.schemaConfig.getShardingDefault(), shardingDefault)) {
				this.schemaConfig.setSharding(sharding);
				if (sharding) {
					this.schemaConfig.setShardingDefault(shardingDefault);
				} else {
					this.schemaConfig.setShardingDefault(Globals.DEFAULT_VALUE_STRING);
				}
				this.modified = Boolean.TRUE;
			}
			return this;
		}

		/**
		 * <h3 class="en-US">Set JDBC connection url</h3>
		 * <h3 class="zh-CN">设置JDBC连接字符串</h3>
		 *
		 * @param jdbcUrl <span class="en-US">JDBC connection url</span>
		 *                <span class="zh-CN">JDBC连接字符串</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public JdbcConfigBuilder jdbcUrl(final String jdbcUrl) {
			if (StringUtils.notBlank(jdbcUrl) && !ObjectUtils.nullSafeEquals(this.schemaConfig.getJdbcUrl(), jdbcUrl)) {
				this.schemaConfig.setJdbcUrl(jdbcUrl);
				this.modified = Boolean.TRUE;
			}
			return this;
		}

		/**
		 * <h3 class="en-US">Set connection retry</h3>
		 * <h3 class="zh-CN">设置连接重试</h3>
		 *
		 * @param retryCount  <span class="en-US">Maximum number of connection retries</span>
		 *                    <span class="zh-CN">连接最大重试次数</span>
		 * @param retryPeriod <span class="en-US">Retry count if obtains connection has error</span>
		 *                    <span class="zh-CN">获取连接的重试次数</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public JdbcConfigBuilder retry(final int retryCount, final long retryPeriod) {
			if (retryCount > 0 && this.schemaConfig.getRetryCount() != retryCount) {
				this.schemaConfig.setRetryCount(retryCount);
				this.modified = Boolean.TRUE;
			}
			if (retryPeriod > 0 && this.schemaConfig.getRetryPeriod() != retryPeriod) {
				this.schemaConfig.setRetryPeriod(retryPeriod);
				this.modified = Boolean.TRUE;
			}
			return this;
		}

		/**
		 * <h3 class="en-US">Set the maximum size of the prepared statement</h3>
		 * <h3 class="zh-CN">设置查询分析器的最大缓存结果</h3>
		 *
		 * @param cachedLimitSize <span class="en-US">Maximum size of prepared statement</span>
		 *                        <span class="zh-CN">查询分析器的最大缓存结果</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public JdbcConfigBuilder cacheSize(final int cachedLimitSize) {
			if (cachedLimitSize > 0 && this.schemaConfig.getCachedLimitSize() != cachedLimitSize) {
				this.schemaConfig.setCachedLimitSize(cachedLimitSize);
				this.modified = Boolean.TRUE;
			}
			return this;
		}

		/**
		 * <h3 class="en-US">Set connection test</h3>
		 * <h3 class="zh-CN">设置连接检查</h3>
		 *
		 * @param testOnBorrow <span class="en-US">Check connection validate when obtains database connection</span>
		 *                     <span class="zh-CN">在获取连接时检查连接是否有效</span>
		 * @param testOnReturn <span class="en-US">Check connection validate when return database connection</span>
		 *                     <span class="zh-CN">在归还连接时检查连接是否有效</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public JdbcConfigBuilder testConnection(final boolean testOnBorrow, final boolean testOnReturn) {
			if (!ObjectUtils.nullSafeEquals(this.schemaConfig.isTestOnBorrow(), testOnBorrow)) {
				this.schemaConfig.setTestOnBorrow(testOnBorrow);
				this.modified = Boolean.TRUE;
			}
			if (!ObjectUtils.nullSafeEquals(this.schemaConfig.isTestOnReturn(), testOnReturn)) {
				this.schemaConfig.setTestOnReturn(testOnReturn);
				this.modified = Boolean.TRUE;
			}
			return this;
		}
	}

	/**
	 * <h2 class="en-US">Implementation class of Remote data schema configure information builder</h2>
	 * <h2 class="zh-CN">远程数据源配置信息构建器实现类</h2>
	 *
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision : 1.0.0 $ $Date: Apr 10, 2018 15:48:19 $
	 */
	public static final class RemoteConfigBuilder extends SchemaConfigBuilder<RemoteSchemaConfig> {

		/**
		 * <h3 class="en-US">Constructor method for implementation class of Remote data schema configure information builder</h3>
		 * <h3 class="zh-CN">远程数据源配置信息构建器实现类的构造函数</h3>
		 *
		 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
		 *                      <span class="zh-CN">父构建器实例对象</span>
		 * @param schemaConfig  <span class="en-US">Configure information instance object</span>
		 *                      <span class="zh-CN">配置信息实例对象</span>
		 */
		RemoteConfigBuilder(final BrainConfigureBuilder parentBuilder, final RemoteSchemaConfig schemaConfig) {
			super(parentBuilder, schemaConfig);
		}

		/**
		 * <h3 class="en-US">Set the trust store information</h3>
		 * <h3 class="zh-CN">设置信任证书库信息</h3>
		 *
		 * @param storePath     <span class="en-US">Trust certificate store path</span>
		 *                      <span class="zh-CN">信任证书库地址</span>
		 * @param storePassword <span class="en-US">Trust certificate store password</span>
		 *                      <span class="zh-CN">信任证书库密码</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public RemoteConfigBuilder trustStore(final String storePath, final String storePassword) {
			return super.trustStore(storePath, storePassword, RemoteConfigBuilder.class);
		}

		/**
		 * <h3 class="en-US">Set slow query-critical time</h3>
		 * <h3 class="zh-CN">设置慢查询临界时间</h3>
		 *
		 * @param lowQueryTimeout <span class="en-US">Low query timeout (Unit: milliseconds)</span>
		 *                        <span class="zh-CN">慢查询的临界时间（单位：毫秒）</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public RemoteConfigBuilder lowQuery(final long lowQueryTimeout) {
			return super.lowQuery(lowQueryTimeout, RemoteConfigBuilder.class);
		}

		/**
		 * <h3 class="en-US">Set value of timeout</h3>
		 * <h3 class="zh-CN">设置超时时间</h3>
		 *
		 * @param validateTimeout <span class="en-US">Timeout value of connection validate (Unit: seconds)</span>
		 *                        <span class="zh-CN">连接检查超时时间（单位：秒）</span>
		 * @param connectTimeout  <span class="en-US">Timeout value of create connection (Unit: seconds)</span>
		 *                        <span class="zh-CN">建立连接超时时间（单位：秒）</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public RemoteConfigBuilder timeout(final int validateTimeout, final int connectTimeout) {
			return super.timeout(validateTimeout, connectTimeout, RemoteConfigBuilder.class);
		}

		/**
		 * <h3 class="en-US">Set remote type</h3>
		 * <h3 class="zh-CN">设置远程类型</h3>
		 *
		 * @param remoteType <span class="en-US">Remote type</span>
		 *                   <span class="zh-CN">远程类型</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public RemoteConfigBuilder type(final RemoteType remoteType) {
			if (!ObjectUtils.nullSafeEquals(this.schemaConfig.getRemoteType(), remoteType)) {
				this.schemaConfig.setRemoteType(remoteType);
				this.modified = Boolean.TRUE;
			}
			return this;
		}

		/**
		 * <h3 class="en-US">Set remote address</h3>
		 * <h3 class="zh-CN">设置远端地址</h3>
		 *
		 * @param remoteAddress <span class="en-US">Remote address</span>
		 *                      <span class="zh-CN">远端地址</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public RemoteConfigBuilder address(final String remoteAddress) {
			if (!ObjectUtils.nullSafeEquals(this.schemaConfig.getRemoteAddress(), remoteAddress)) {
				this.schemaConfig.setRemoteAddress(remoteAddress);
				this.modified = Boolean.TRUE;
			}
			return this;
		}

		/**
		 * <h3 class="en-US">Set keep-alive timeout</h3>
		 * <h3 class="zh-CN">设置长连接超时时间</h3>
		 *
		 * @param keepAlive <span class="en-US">Keep-alive timeout (Unit: seconds)</span>
		 *                  <span class="zh-CN">长连接超时时间（单位：秒）</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public RemoteConfigBuilder keepAlive(final int keepAlive) {
			if (!ObjectUtils.nullSafeEquals(this.schemaConfig.getKeepAlive(), keepAlive)) {
				this.schemaConfig.setKeepAlive(keepAlive);
				this.modified = Boolean.TRUE;
			}
			return this;
		}

		/**
		 * <h3 class="en-US">Using current proxy configure information to create ProxyConfigBuilder instance</h3>
		 * <h3 class="zh-CN">使用当前的代理服务器配置信息生成代理服务器配置构建器实例对象</h3>
		 *
		 * @return <span class="en-US">ProxyConfigBuilder instance</span>
		 * <span class="zh-CN">代理服务器配置构建器实例对象</span>
		 */
		public ProxyConfigBuilder proxyConfig() {
			return new ProxyConfigBuilder(this,
					Optional.ofNullable(this.schemaConfig.getProxyConfig()).orElse(new ProxyConfig()));
		}

		/**
		 * <h3 class="en-US">Delete current proxy configure information</h3>
		 * <h3 class="zh-CN">删除代理服务器配置信息</h3>
		 *
		 * @return <span class="en-US">Current builder instance</span>
		 * <span class="zh-CN">当前构造器实例对象</span>
		 */
		public RemoteConfigBuilder removeProxyConfig() {
			if (this.schemaConfig.getProxyConfig() != null) {
				this.schemaConfig.setProxyConfig(null);
				this.modified = Boolean.TRUE;
			}
			return this;
		}

		@Override
		public void confirm(final Object object) {
			if (object instanceof ProxyConfig) {
				if (this.schemaConfig.getProxyConfig() == null
						|| this.schemaConfig.getProxyConfig().getLastModified() != ((ProxyConfig) object).getLastModified()) {
					this.schemaConfig.setProxyConfig((ProxyConfig) object);
					this.modified = Boolean.TRUE;
				}
			} else {
				super.confirm(object);
			}
		}
	}
}
