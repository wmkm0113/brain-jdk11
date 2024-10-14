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
import org.nervousync.utils.StringUtils;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
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
	 * <h3 class="en-US">Constructor method for abstract class of data schema configure information builder</h3>
	 * <h3 class="zh-CN">数据源配置信息构建器抽象类的构造方法</h3>
	 *
	 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
	 *                      <span class="zh-CN">父构建器实例对象</span>
	 * @param schemaConfig  <span class="en-US">Configure information instance object</span>
	 *                      <span class="zh-CN">配置信息实例对象</span>
	 */
	protected SchemaConfigBuilder(final ConfigureBuilder parentBuilder, final T schemaConfig) {
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
	public final AuthenticationBuilder.UserAuthenticationBuilder userAuthentication() {
		return new AuthenticationBuilder.UserAuthenticationBuilder(this,
				Optional.ofNullable(this.schemaConfig.getAuthentication())
						.filter(authentication -> authentication instanceof UserAuthentication)
						.map(authentication -> (UserAuthentication) authentication)
						.orElse(new UserAuthentication()));
	}

	/**
	 * <h3 class="en-US">Generate a builder instance object that uses the X.509 certificate authentication information in the certificate store based on the existing authentication information.</h3>
	 * <h3 class="zh-CN">根据现有的身份认证信息生成使用证书库中X.509证书认证信息的构建器实例对象</h3>
	 *
	 * @return <span class="en-US">Builder implementation class of use the authentication information of the X.509 certificate in the certificate store</span>
	 * <span class="zh-CN">使用证书库中X.509证书的认证信息的构建器</span>
	 */
	public final AuthenticationBuilder.TrustStoreAuthenticationBuilder trustStoreAuthentication() {
		return new AuthenticationBuilder.TrustStoreAuthenticationBuilder(this,
				Optional.ofNullable(this.schemaConfig.getAuthentication())
						.filter(authentication -> authentication instanceof TrustStoreAuthentication)
						.map(authentication -> (TrustStoreAuthentication) authentication)
						.orElse(new TrustStoreAuthentication()));
	}

	@Override
	public void confirm(final Object object) {
		if (object instanceof Authentication) {
			this.schemaConfig.setAuthentication((Authentication) object);
		}
	}

	@Override
	public final T confirm() {
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
		this.schemaConfig.setDialectName(dialectName);
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
			TrustStore trustStore = Optional.ofNullable(this.schemaConfig.getTrustStore()).orElse(new TrustStore());
			trustStore.setTrustStorePath(storePath);
			trustStore.setTrustStorePassword(storePassword);
			this.schemaConfig.setTrustStore(trustStore);
		}
		return builderClass.cast(this);
	}

	/**
	 * <h3 class="en-US">Set X.509 certificate authentication information</h3>
	 * <h3 class="zh-CN">设置X.509证书身份认证信息</h3>
	 *
	 * @param x509Certificate <span class="en-US">X.509 certificate</span>
	 *                        <span class="zh-CN">X.509证书</span>
	 * @param builderClass    <span class="en-US">Returned object type</span>
	 *                        <span class="zh-CN">返回的对象类型</span>
	 * @param <B>             <span class="en-US">Generic class that returns the object type</span>
	 *                        <span class="zh-CN">返回对象类型的泛型类</span>
	 * @return <span class="en-US">Instance object of specified return type</span>
	 * <span class="zh-CN">指定返回类型的实例对象</span>
	 * @throws CertificateEncodingException <span class="en-US">Error while reading certificate</span>
	 *                                      <span class="zh-CN">读取证书时出错</span>
	 */
	protected final <B> B x509Authenticator(@Nonnull final X509Certificate x509Certificate,
	                                        final Class<B> builderClass) throws CertificateEncodingException {
		X509Authentication x509Authentication = new X509Authentication();
		x509Authentication.setCertData(StringUtils.base64Encode(x509Certificate.getEncoded()));
		this.schemaConfig.setAuthentication(x509Authentication);
		return builderClass.cast(this);
	}

	/**
	 * <h3 class="en-US">Set slow query critical time</h3>
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
	protected final <B> B lowQuery(final int lowQueryTimeout, final Class<B> builderClass) {
		this.schemaConfig.setLowQueryTimeout(lowQueryTimeout);
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
		this.schemaConfig.setValidateTimeout(validateTimeout);
		this.schemaConfig.setConnectTimeout(connectTimeout);
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
	 * <h3 class="en-US">Add new server information</h3>
	 * <h3 class="zh-CN">添加新的服务器信息</h3>
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
	protected final void insertServer(final List<ServerInfo> serverList, final String serverName,
	                                  final String serverAddress, final int serverPort, final int serverLevel) {
		if (serverList.stream().noneMatch(serverInfo -> serverInfo.match(serverAddress, serverPort))) {
			ServerInfo serverInfo = new ServerInfo();
			serverInfo.setServerName(serverName);
			serverInfo.setServerAddress(serverAddress);
			serverInfo.setServerPort(serverPort);
			serverInfo.setServerLevel(serverLevel);
			serverList.add(serverInfo);
		}
	}

	/**
	 * <h3 class="en-US">Update server level information</h3>
	 * <h3 class="zh-CN">修改服务器等级信息</h3>
	 *
	 * @param serverList    <span class="en-US">Exists server information list</span>
	 *                      <span class="zh-CN">现有服务器列表</span>
	 * @param serverAddress <span class="en-US">Server address</span>
	 *                      <span class="zh-CN">服务器地址</span>
	 * @param serverPort    <span class="en-US">Server port number</span>
	 *                      <span class="zh-CN">服务器端口号</span>
	 * @param serverLevel   <span class="en-US">Server level</span>
	 *                      <span class="zh-CN">服务器等级</span>
	 */
	protected final void updateServer(final List<ServerInfo> serverList, final String serverAddress,
	                                  final int serverPort, final int serverLevel) {
		serverList.replaceAll(serverInfo -> {
			if (serverInfo.match(serverAddress, serverPort)) {
				serverInfo.setServerLevel(serverLevel);
			}
			return serverInfo;
		});
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
		DistributeConfigBuilder(final ConfigureBuilder parentBuilder,
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
		 * <h3 class="en-US">Set X.509 certificate authentication information</h3>
		 * <h3 class="zh-CN">设置X.509证书身份认证信息</h3>
		 *
		 * @param x509Certificate <span class="en-US">X.509 certificate</span>
		 *                        <span class="zh-CN">X.509证书</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 * @throws CertificateEncodingException <span class="en-US">Error while reading certificate</span>
		 *                                      <span class="zh-CN">读取证书时出错</span>
		 */
		public DistributeConfigBuilder x509Authenticator(@Nonnull final X509Certificate x509Certificate)
				throws CertificateEncodingException {
			return super.x509Authenticator(x509Certificate, DistributeConfigBuilder.class);
		}

		/**
		 * <h3 class="en-US">Set slow query critical time</h3>
		 * <h3 class="zh-CN">设置慢查询临界时间</h3>
		 *
		 * @param lowQueryTimeout <span class="en-US">Low query timeout (Unit: milliseconds)</span>
		 *                        <span class="zh-CN">慢查询的临界时间（单位：毫秒）</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public DistributeConfigBuilder lowQuery(final int lowQueryTimeout) {
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
			if (requestTimeout > 0) {
				this.schemaConfig.setRequestTimeout(requestTimeout);
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
			List<ServerInfo> serverList = this.schemaConfig.getServerList();
			super.insertServer(serverList, Globals.DEFAULT_VALUE_STRING, serverAddress, serverPort, serverLevel);
			this.schemaConfig.setServerList(serverList);
			return this;
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
			super.insertServer(serverList, serverName, serverAddress, serverPort, serverLevel);
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
			super.updateServer(serverList, serverAddress, serverPort, serverLevel);
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
			serverList.removeIf(serverInfo -> serverInfo.match(serverAddress, serverPort));
			this.schemaConfig.setServerList(serverList);
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
			if (StringUtils.notBlank(databaseName)) {
				this.schemaConfig.setDatabaseName(databaseName);
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
			this.schemaConfig.setUseSsl(useSsl);
			return this;
		}

		/**
		 * <h3 class="en-US">Set maximum size of prepared statement</h3>
		 * <h3 class="zh-CN">设置查询分析器的最大缓存结果</h3>
		 *
		 * @param cachedLimitSize <span class="en-US">Maximum size of prepared statement</span>
		 *                        <span class="zh-CN">查询分析器的最大缓存结果</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public DistributeConfigBuilder cacheSize(final int cachedLimitSize) {
			this.schemaConfig.setCachedLimitSize(cachedLimitSize);
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
		JdbcConfigBuilder(final ConfigureBuilder parentBuilder, final JdbcSchemaConfig schemaConfig) {
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
		 * <h3 class="en-US">Set X.509 certificate authentication information</h3>
		 * <h3 class="zh-CN">设置X.509证书身份认证信息</h3>
		 *
		 * @param x509Certificate <span class="en-US">X.509 certificate</span>
		 *                        <span class="zh-CN">X.509证书</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 * @throws CertificateEncodingException <span class="en-US">Error while reading certificate</span>
		 *                                      <span class="zh-CN">读取证书时出错</span>
		 */
		public JdbcConfigBuilder x509Authenticator(@Nonnull final X509Certificate x509Certificate)
				throws CertificateEncodingException {
			return super.x509Authenticator(x509Certificate, JdbcConfigBuilder.class);
		}

		/**
		 * <h3 class="en-US">Set slow query critical time</h3>
		 * <h3 class="zh-CN">设置慢查询临界时间</h3>
		 *
		 * @param lowQueryTimeout <span class="en-US">Low query timeout (Unit: milliseconds)</span>
		 *                        <span class="zh-CN">慢查询的临界时间（单位：毫秒）</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public JdbcConfigBuilder lowQuery(final int lowQueryTimeout) {
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
		 * <h3 class="en-US">Set using server array</h3>
		 * <h3 class="zh-CN">设置使用服务器组</h3>
		 *
		 * @param serverArray <span class="en-US">Using server array</span>
		 *                    <span class="zh-CN">使用服务器组</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public JdbcConfigBuilder serverArray(final boolean serverArray) {
			this.schemaConfig.setServerArray(serverArray);
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
			}
			super.insertServer(serverList, Globals.DEFAULT_VALUE_STRING, serverAddress, serverPort, serverLevel);
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
			super.updateServer(serverList, serverAddress, serverPort, serverLevel);
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
			serverList.removeIf(serverInfo -> serverInfo.match(serverAddress, serverPort));
			this.schemaConfig.setServerList(serverList);
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
			this.schemaConfig.setSharding(sharding);
			if (sharding) {
				this.schemaConfig.setShardingDefault(shardingDefault);
			} else {
				this.schemaConfig.setShardingDefault(Globals.DEFAULT_VALUE_STRING);
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
			this.schemaConfig.setJdbcUrl(jdbcUrl);
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
			this.schemaConfig.setRetryCount(retryCount);
			this.schemaConfig.setRetryPeriod(retryPeriod);
			return this;
		}

		/**
		 * <h3 class="en-US">Set maximum size of prepared statement</h3>
		 * <h3 class="zh-CN">设置查询分析器的最大缓存结果</h3>
		 *
		 * @param cachedLimitSize <span class="en-US">Maximum size of prepared statement</span>
		 *                        <span class="zh-CN">查询分析器的最大缓存结果</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public JdbcConfigBuilder cacheSize(final int cachedLimitSize) {
			this.schemaConfig.setCachedLimitSize(cachedLimitSize);
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
			this.schemaConfig.setTestOnBorrow(testOnBorrow);
			this.schemaConfig.setTestOnReturn(testOnReturn);
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
		RemoteConfigBuilder(final ConfigureBuilder parentBuilder, final RemoteSchemaConfig schemaConfig) {
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
		 * <h3 class="en-US">Set X.509 certificate authentication information</h3>
		 * <h3 class="zh-CN">设置X.509证书身份认证信息</h3>
		 *
		 * @param x509Certificate <span class="en-US">X.509 certificate</span>
		 *                        <span class="zh-CN">X.509证书</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 * @throws CertificateEncodingException <span class="en-US">Error while reading certificate</span>
		 *                                      <span class="zh-CN">读取证书时出错</span>
		 */
		public RemoteConfigBuilder x509Authenticator(@Nonnull final X509Certificate x509Certificate)
				throws CertificateEncodingException {
			return super.x509Authenticator(x509Certificate, RemoteConfigBuilder.class);
		}

		/**
		 * <h3 class="en-US">Set slow query critical time</h3>
		 * <h3 class="zh-CN">设置慢查询临界时间</h3>
		 *
		 * @param lowQueryTimeout <span class="en-US">Low query timeout (Unit: milliseconds)</span>
		 *                        <span class="zh-CN">慢查询的临界时间（单位：毫秒）</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public RemoteConfigBuilder lowQuery(final int lowQueryTimeout) {
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
			this.schemaConfig.setRemoteType(remoteType);
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
			this.schemaConfig.setRemoteAddress(remoteAddress);
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
			this.schemaConfig.setKeepAlive(keepAlive);
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

		@Override
		public void confirm(final Object object) {
			if (object instanceof ProxyConfig) {
				this.schemaConfig.setProxyConfig((ProxyConfig) object);
			} else {
				super.confirm(object);
			}
		}
	}
}
