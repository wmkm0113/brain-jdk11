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
import org.nervousync.brain.configs.auth.impl.TokenAuthentication;
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
import org.nervousync.exceptions.builder.BuilderException;
import org.nervousync.proxy.ProxyConfig;
import org.nervousync.proxy.ProxyConfigBuilder;
import org.nervousync.utils.core.DateTimeUtils;
import org.nervousync.utils.core.ObjectUtils;
import org.nervousync.utils.core.StringUtils;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <h2 class="en-US">Abstract class of data schema configure information builder</h2>
 * <h2 class="zh-CN">数据源配置信息构建器的抽象类</h2>
 *
 * @param <T> <span class="en-US">Configuration information generic class</span>
 *            <span class="zh-CN">配置信息泛型类</span>
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Apr 10, 2018 15:48:19 $
 */
@SuppressWarnings({"unused", "unchecked"})
public abstract class SchemaConfigBuilder<P extends ParentBuilder, T extends SchemaConfig>
		extends AbstractBuilder<P, T> {

	/**
	 * <span class="en-US">Configure information instance object</span>
	 * <span class="zh-CN">配置信息实例对象</span>
	 */
	protected final T schemaConfig;
	/**
	 * <span class="en-US">Configure information modified flag</span>
	 * <span class="zh-CN">配置信息修改标记</span>
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
	protected SchemaConfigBuilder(final P parentBuilder, final T schemaConfig) {
		super(parentBuilder);
		this.schemaConfig = schemaConfig;
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
		} else if (object instanceof TrustStore) {
			TrustStore trustStore = (TrustStore) object;
			TrustStore current = this.schemaConfig.getTrustStore();
			if (current == null) {
				this.schemaConfig.setTrustStore(trustStore);
				this.modified = Boolean.TRUE;
			} else {
				if (!ObjectUtils.nullSafeEquals(current.getStorePath(), trustStore.getStorePath())
						|| !ObjectUtils.nullSafeEquals(current.getStorePassword(), trustStore.getStorePassword())) {
					this.schemaConfig.setTrustStore(trustStore);
					this.modified = Boolean.TRUE;
				}
			}
		} else if (object instanceof ServerInfo) {
			ServerInfo serverInfo = (ServerInfo) object;
			List<ServerInfo> serverList = this.schemaConfig.getServerList();
			AtomicBoolean modified = new AtomicBoolean(Boolean.FALSE);
			if (serverList.stream().noneMatch(existServer ->
					existServer.match(serverInfo.getServerAddress(), serverInfo.getServerPort()))) {
				serverList.add(serverInfo);
				modified.set(Boolean.TRUE);
			} else {
				serverList.replaceAll(existServer -> {
					if (existServer.match(serverInfo.getServerAddress(), serverInfo.getServerPort())) {
						if (existServer.getLastModified() != serverInfo.getLastModified()) {
							modified.set(Boolean.TRUE);
						}
						return serverInfo;
					}
					return existServer;
				});
			}
			if (modified.get()) {
				this.schemaConfig.setServerList(serverList);
				this.modified = Boolean.TRUE;
			}
		}
	}

	@Override
	public final T build() {
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
	 * <h3 class="en-US">Generate basic identity authentication information builder instance object based on existing identity authentication information</h3>
	 * <h3 class="zh-CN">根据现有的身份认证信息生成基本身份认证信息构建器实例对象</h3>
	 *
	 * @param userName     <span class="en-US">Username</span>
	 *                     <span class="zh-CN">用户名</span>
	 * @param passWord     <span class="en-US">Password</span>
	 *                     <span class="zh-CN">密码</span>
	 * @param builderClass <span class="en-US">Returned object type</span>
	 *                     <span class="zh-CN">返回的对象类型</span>
	 * @param <B>          <span class="en-US">Generic class that returns the object type</span>
	 *                     <span class="zh-CN">返回对象类型的泛型类</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	protected final <B> B basicAuth(final String userName, final String passWord, final Class<B> builderClass) {
		UserAuthentication authentication = authentication(this.schemaConfig.getAuthentication(), userName, passWord);
		if (this.schemaConfig.getAuthentication() == null) {
			this.modified = Boolean.TRUE;
		} else {
			this.modified = this.schemaConfig.getAuthentication().getLastModified() != authentication.getLastModified();
		}
		this.schemaConfig.setAuthentication(authentication);
		return builderClass.cast(this);
	}

	/**
	 * <h3 class="en-US">Generate a builder instance object that uses the trust store authentication information
	 * in the certificate store based on the existing authentication information.</h3>
	 * <h3 class="zh-CN">根据现有的身份认证信息生成使用证书库中X.509证书认证信息的构建器实例对象</h3>
	 *
	 * @param storePath       <span class="en-US">Trust certificate store path</span>
	 *                        <span class="zh-CN">信任证书库地址</span>
	 * @param storePassword   <span class="en-US">Trust certificate store password</span>
	 *                        <span class="zh-CN">信任证书库密码</span>
	 * @param certificateName <span class="en-US">Certificate name</span>
	 *                        <span class="zh-CN">证书名称</span>
	 * @param builderClass    <span class="en-US">Returned object type</span>
	 *                        <span class="zh-CN">返回的对象类型</span>
	 * @param <B>             <span class="en-US">Generic class that returns the object type</span>
	 *                        <span class="zh-CN">返回对象类型的泛型类</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	protected final <B> B trustStoreAuth(final String storePath, final String storePassword,
	                                     final String certificateName, final Class<B> builderClass) {
		TrustStoreAuthentication authentication =
				authentication(this.schemaConfig.getAuthentication(), storePath, storePassword, certificateName);
		if (this.schemaConfig.getAuthentication() == null) {
			this.modified = Boolean.TRUE;
		} else {
			this.modified = this.schemaConfig.getAuthentication().getLastModified() != authentication.getLastModified();
		}
		this.schemaConfig.setAuthentication(authentication);
		return builderClass.cast(this);
	}

	/**
	 * <h3 class="en-US">Generate a builder instance object that uses the X.509 certificate authentication information.</h3>
	 * <h3 class="zh-CN">根据现有的身份认证信息生成X.509证书认证信息的构建器实例对象</h3>
	 *
	 * @param x509Certificate <span class="en-US">X.509 certificate</span>
	 *                        <span class="zh-CN">X.509证书</span>
	 * @param builderClass    <span class="en-US">Returned object type</span>
	 *                        <span class="zh-CN">返回的对象类型</span>
	 * @param <B>             <span class="en-US">Generic class that returns the object type</span>
	 *                        <span class="zh-CN">返回对象类型的泛型类</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	protected final <B> B x509Auth(@Nonnull final X509Certificate x509Certificate, final Class<B> builderClass) {
		X509Authentication authentication = authentication(this.schemaConfig.getAuthentication(), x509Certificate);
		if (this.schemaConfig.getAuthentication() == null) {
			this.modified = Boolean.TRUE;
		} else {
			this.modified = this.schemaConfig.getAuthentication().getLastModified() != authentication.getLastModified();
		}
		this.schemaConfig.setAuthentication(authentication);
		return builderClass.cast(this);
	}

	/**
	 * <h3 class="en-US">Generate a builder instance object that uses the X.509 certificate authentication information.</h3>
	 * <h3 class="zh-CN">根据现有的身份认证信息生成X.509证书认证信息的构建器实例对象</h3>
	 *
	 * @param keyId        <span class="en-US">Identify Key ID</span>
	 *                     <span class="zh-CN">识别ID</span>
	 * @param secretKey    <span class="en-US">Identify secret key</span>
	 *                     <span class="zh-CN">识别密钥</span>
	 * @param sessionToken <span class="en-US">Session token</span>
	 *                     <span class="zh-CN">会话Token</span>
	 * @param builderClass <span class="en-US">Returned object type</span>
	 *                     <span class="zh-CN">返回的对象类型</span>
	 * @param <B>          <span class="en-US">Generic class that returns the object type</span>
	 *                     <span class="zh-CN">返回对象类型的泛型类</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	protected final <B> B tokenAuth(@Nonnull final String keyId, @Nonnull final String secretKey,
	                                final String sessionToken, final Class<B> builderClass) {
		TokenAuthentication authentication =
				token(this.schemaConfig.getAuthentication(), keyId, secretKey, sessionToken);
		if (this.schemaConfig.getAuthentication() == null) {
			this.modified = Boolean.TRUE;
		} else {
			this.modified = this.schemaConfig.getAuthentication().getLastModified() != authentication.getLastModified();
		}
		this.schemaConfig.setAuthentication(authentication);
		return builderClass.cast(this);
	}

	/**
	 * <h3 class="en-US">Update the authentication configure information</h3>
	 * <h3 class="zh-CN">更新身份认证信息设置</h3>
	 *
	 * @param existAuth       <span class="en-US">Exists authentication information</span>
	 *                        <span class="zh-CN">已存在的认证信息</span>
	 * @param storePath       <span class="en-US">Trust certificate store path</span>
	 *                        <span class="zh-CN">信任证书库地址</span>
	 * @param storePassword   <span class="en-US">Trust certificate store password</span>
	 *                        <span class="zh-CN">信任证书库密码</span>
	 * @param certificateName <span class="en-US">Certificate name</span>
	 *                        <span class="zh-CN">证书名称</span>
	 * @return <span class="en-US">Generated trust store authentication instance object</span>
	 * <span class="zh-CN">证书库中X.509证书认证信息实例对象</span>
	 */
	private static final UserAuthentication authentication(final Authentication existAuth,
	                                                       final String userName, final String passWord) {
		UserAuthentication authentication;
		if (existAuth instanceof UserAuthentication) {
			authentication = (UserAuthentication) existAuth;
		} else {
			authentication = new UserAuthentication();
		}
		boolean modified = Boolean.FALSE;
		if (!ObjectUtils.nullSafeEquals(authentication.getUserName(), userName)) {
			authentication.setUserName(userName);
			modified = Boolean.TRUE;
		}
		if (!ObjectUtils.nullSafeEquals(authentication.getPassWord(), passWord)) {
			authentication.setPassWord(passWord);
			modified = Boolean.TRUE;
		}
		if (modified) {
			authentication.setLastModified(DateTimeUtils.currentUTCTimeMillis());
		}
		return authentication;
	}

	/**
	 * <h3 class="en-US">Update the authentication configure information</h3>
	 * <h3 class="zh-CN">更新身份认证信息设置</h3>
	 *
	 * @param existAuth       <span class="en-US">Exists authentication information</span>
	 *                        <span class="zh-CN">已存在的认证信息</span>
	 * @param storePath       <span class="en-US">Trust certificate store path</span>
	 *                        <span class="zh-CN">信任证书库地址</span>
	 * @param storePassword   <span class="en-US">Trust certificate store password</span>
	 *                        <span class="zh-CN">信任证书库密码</span>
	 * @param certificateName <span class="en-US">Certificate name</span>
	 *                        <span class="zh-CN">证书名称</span>
	 * @return <span class="en-US">Generated trust store authentication instance object</span>
	 * <span class="zh-CN">证书库中X.509证书认证信息实例对象</span>
	 */
	private static final TrustStoreAuthentication authentication(final Authentication existAuth,
	                                                             final String storePath, final String storePassword,
	                                                             final String certificateName) {
		TrustStoreAuthentication authentication;
		if (existAuth instanceof TrustStoreAuthentication) {
			authentication = (TrustStoreAuthentication) existAuth;
		} else {
			authentication = new TrustStoreAuthentication();
		}
		boolean modified = Boolean.FALSE;
		if (!ObjectUtils.nullSafeEquals(authentication.getStorePath(), storePath)) {
			authentication.setStorePath(storePath);
			modified = Boolean.TRUE;
		}
		if (!ObjectUtils.nullSafeEquals(authentication.getStorePassword(), storePassword)) {
			authentication.setStorePassword(storePassword);
			modified = Boolean.TRUE;
		}
		if (!ObjectUtils.nullSafeEquals(authentication.getCertificateName(), certificateName)) {
			authentication.setCertificateName(certificateName);
			modified = Boolean.TRUE;
		}
		if (modified) {
			authentication.setLastModified(DateTimeUtils.currentUTCTimeMillis());
		}
		return authentication;
	}

	/**
	 * <h3 class="en-US">Update the authentication configure information</h3>
	 * <h3 class="zh-CN">更新身份认证信息设置</h3>
	 *
	 * @param existAuth       <span class="en-US">Exists authentication information</span>
	 *                        <span class="zh-CN">已存在的认证信息</span>
	 * @param storePath       <span class="en-US">Trust certificate store path</span>
	 *                        <span class="zh-CN">信任证书库地址</span>
	 * @param storePassword   <span class="en-US">Trust certificate store password</span>
	 *                        <span class="zh-CN">信任证书库密码</span>
	 * @param certificateName <span class="en-US">Certificate name</span>
	 *                        <span class="zh-CN">证书名称</span>
	 * @return <span class="en-US">Generated trust store authentication instance object</span>
	 * <span class="zh-CN">证书库中X.509证书认证信息实例对象</span>
	 */
	private static final X509Authentication authentication(final Authentication existAuth,
	                                                       @Nonnull final X509Certificate x509Certificate) {
		X509Authentication authentication;
		if (existAuth instanceof X509Authentication) {
			authentication = (X509Authentication) existAuth;
		} else {
			authentication = new X509Authentication();
		}
		try {
			String certData = StringUtils.base64Encode(x509Certificate.getEncoded());
			boolean modified = Boolean.FALSE;
			if (!ObjectUtils.nullSafeEquals(authentication.getCertData(), certData)) {
				authentication.setCertData(certData);
				modified = Boolean.TRUE;
			}
			if (modified) {
				authentication.setLastModified(DateTimeUtils.currentUTCTimeMillis());
			}
		} catch (CertificateEncodingException e) {
		}
		return authentication;
	}

	/**
	 * <h3 class="en-US">Update the authentication configure information</h3>
	 * <h3 class="zh-CN">更新身份认证信息设置</h3>
	 *
	 * @param existAuth    <span class="en-US">Exists authentication information</span>
	 *                     <span class="zh-CN">已存在的认证信息</span>
	 * @param keyId        <span class="en-US">Identify Key ID</span>
	 *                     <span class="zh-CN">识别ID</span>
	 * @param secretKey    <span class="en-US">Identify secret key</span>
	 *                     <span class="zh-CN">识别密钥</span>
	 * @param sessionToken <span class="en-US">Session token</span>
	 *                     <span class="zh-CN">会话Token</span>
	 * @return <span class="en-US">Generated trust store authentication instance object</span>
	 * <span class="zh-CN">证书库中X.509证书认证信息实例对象</span>
	 */
	private static final TokenAuthentication token(final Authentication existAuth,
	                                               @Nonnull final String keyId, @Nonnull final String secretKey,
	                                               final String sessionToken) {
		TokenAuthentication authentication;
		if (existAuth instanceof X509Authentication) {
			authentication = (TokenAuthentication) existAuth;
		} else {
			authentication = new TokenAuthentication();
		}
		boolean modified = Boolean.FALSE;
		if (!ObjectUtils.nullSafeEquals(authentication.getKeyId(), keyId)) {
			authentication.setKeyId(keyId);
			modified = Boolean.TRUE;
		}
		if (!ObjectUtils.nullSafeEquals(authentication.getSecretKey(), secretKey)) {
			authentication.setSecretKey(secretKey);
			modified = Boolean.TRUE;
		}
		if (!ObjectUtils.nullSafeEquals(authentication.getSessionToken(), sessionToken)) {
			authentication.setSessionToken(sessionToken);
			modified = Boolean.TRUE;
		}
		if (modified) {
			authentication.setLastModified(DateTimeUtils.currentUTCTimeMillis());
		}
		return authentication;
	}

	/**
	 * <h2 class="en-US">Implementation class of trust store configure information builder</h2>
	 * <h2 class="zh-CN">信任证书库配置信息构建器实现类</h2>
	 *
	 * @param <P> <span class="en-US">Generics Type of parent builder</span>
	 *            <span class="zh-CN">父构建器泛型类</span>
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision: 1.0.0 $ $Date: Jun 23, 2025 15:56:28 $
	 */
	public static final class TrustStoreBuilder<P extends ParentBuilder> extends AbstractBuilder<P, TrustStore> {

		/**
		 * <span class="en-US">Trust store configure information</span>
		 * <span class="zh-CN">信任证书库配置信息</span>
		 */
		private final TrustStore trustStore;

		/**
		 * <h3 class="en-US">Constructor method for the implementation class of trust store configure information builder</h3>
		 * <h3 class="zh-CN">信任证书库配置信息构建器实现类的构造方法</h3>
		 *
		 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
		 *                      <span class="zh-CN">父构建器实例对象</span>
		 * @param trustStore    <span class="en-US">Trust store configure information</span>
		 *                      <span class="zh-CN">信任证书库配置信息</span>
		 */
		TrustStoreBuilder(final P parentBuilder, @Nonnull final TrustStore trustStore) {
			super(parentBuilder);
			this.trustStore = trustStore;
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
		public TrustStoreBuilder<P> config(final String storePath, final String storePassword) {
			if (!ObjectUtils.nullSafeEquals(this.trustStore.getStorePath(), storePath)) {
				this.trustStore.setStorePath(storePath);
			}
			if (!ObjectUtils.nullSafeEquals(this.trustStore.getStorePassword(), storePassword)) {
				this.trustStore.setStorePassword(storePassword);
			}
			return this;
		}

		@Override
		public TrustStore build() throws BuilderException {
			return this.trustStore;
		}
	}

	/**
	 * <h2 class="en-US">Implementation class of database server configure information builder</h2>
	 * <h2 class="zh-CN">数据库服务器配置信息构建器实现类</h2>
	 *
	 * @param <P> <span class="en-US">Generics Type of parent builder</span>
	 *            <span class="zh-CN">父构建器泛型类</span>
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision: 1.0.0 $ $Date: Jun 23, 2025 15:56:28 $
	 */
	public static final class ServerInfoBuilder<P extends ParentBuilder> extends AbstractBuilder<P, ServerInfo> {

		/**
		 * <h2 class="en-US">Server information</h2>
		 * <h2 class="zh-CN">服务器信息</h2>
		 */
		private final ServerInfo serverInfo;
		/**
		 * <h2 class="en-US">Configure information modified flag</h2>
		 * <h2 class="zh-CN">配置信息修改标记</h2>
		 */
		private boolean modified = Boolean.FALSE;

		/**
		 * <h3 class="en-US">Constructor method for implementation class of database server configure information builder</h3>
		 * <h3 class="zh-CN">数据库服务器配置信息构建器实现类的构造函数</h3>
		 *
		 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
		 *                      <span class="zh-CN">父构建器实例对象</span>
		 * @param serverInfo    <h2 class="en-US">Server information</h2>
		 *                      <h2 class="zh-CN">服务器信息</h2>
		 */
		ServerInfoBuilder(@Nonnull final P parentBuilder, @Nonnull final ServerInfo serverInfo) {
			super(parentBuilder);
			this.serverInfo = serverInfo;
		}

		/**
		 * <h3 class="en-US">Generate basic identity authentication information builder instance object based on existing identity authentication information</h3>
		 * <h3 class="zh-CN">根据现有的身份认证信息生成基本身份认证信息构建器实例对象</h3>
		 *
		 * @param userName <span class="en-US">Username</span>
		 *                 <span class="zh-CN">用户名</span>
		 * @param passWord <span class="en-US">Password</span>
		 *                 <span class="zh-CN">密码</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public ServerInfoBuilder<P> basicAuth(final String userName, final String passWord) {
			UserAuthentication authentication = authentication(this.serverInfo.getAuthentication(), userName, passWord);
			if (this.serverInfo.getAuthentication() == null) {
				this.modified = Boolean.TRUE;
			} else {
				this.modified = this.serverInfo.getAuthentication().getLastModified() != authentication.getLastModified();
			}
			this.serverInfo.setAuthentication(authentication);
			return this;
		}

		/**
		 * <h3 class="en-US">Generate a builder instance object that uses the trust store authentication information
		 * in the certificate store based on the existing authentication information.</h3>
		 * <h3 class="zh-CN">根据现有的身份认证信息生成使用证书库中X.509证书认证信息的构建器实例对象</h3>
		 *
		 * @param storePath       <span class="en-US">Trust certificate store path</span>
		 *                        <span class="zh-CN">信任证书库地址</span>
		 * @param storePassword   <span class="en-US">Trust certificate store password</span>
		 *                        <span class="zh-CN">信任证书库密码</span>
		 * @param certificateName <span class="en-US">Certificate name</span>
		 *                        <span class="zh-CN">证书名称</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public ServerInfoBuilder<P> trustStoreAuth(final String storePath, final String storePassword,
		                                           final String certificateName) {
			TrustStoreAuthentication authentication =
					authentication(this.serverInfo.getAuthentication(), storePath, storePassword, certificateName);
			if (this.serverInfo.getAuthentication() == null) {
				this.modified = Boolean.TRUE;
			} else {
				this.modified = this.serverInfo.getAuthentication().getLastModified() != authentication.getLastModified();
			}
			this.serverInfo.setAuthentication(authentication);
			return this;
		}

		/**
		 * <h3 class="en-US">Generate a builder instance object that uses the X.509 certificate authentication information.</h3>
		 * <h3 class="zh-CN">根据现有的身份认证信息生成X.509证书认证信息的构建器实例对象</h3>
		 *
		 * @param x509Certificate <span class="en-US">X.509 certificate</span>
		 *                        <span class="zh-CN">X.509证书</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public ServerInfoBuilder<P> x509Auth(@Nonnull final X509Certificate x509Certificate) {
			X509Authentication authentication = authentication(this.serverInfo.getAuthentication(), x509Certificate);
			if (this.serverInfo.getAuthentication() == null) {
				this.modified = Boolean.TRUE;
			} else {
				this.modified = this.serverInfo.getAuthentication().getLastModified() != authentication.getLastModified();
			}
			this.serverInfo.setAuthentication(authentication);
			return this;
		}

		/**
		 * <h3 class="en-US">Generate a builder instance object that uses the X.509 certificate authentication information.</h3>
		 * <h3 class="zh-CN">根据现有的身份认证信息生成X.509证书认证信息的构建器实例对象</h3>
		 *
		 * @param keyId        <span class="en-US">Identify Key ID</span>
		 *                     <span class="zh-CN">识别ID</span>
		 * @param secretKey    <span class="en-US">Identify secret key</span>
		 *                     <span class="zh-CN">识别密钥</span>
		 * @param sessionToken <span class="en-US">Session token</span>
		 *                     <span class="zh-CN">会话Token</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public ServerInfoBuilder<P> tokenAuth(@Nonnull final String keyId, @Nonnull final String secretKey,
		                                      final String sessionToken) {
			TokenAuthentication authentication =
					token(this.serverInfo.getAuthentication(), keyId, secretKey, sessionToken);
			if (this.serverInfo.getAuthentication() == null) {
				this.modified = Boolean.TRUE;
			} else {
				this.modified = this.serverInfo.getAuthentication().getLastModified() != authentication.getLastModified();
			}
			this.serverInfo.setAuthentication(authentication);
			return this;
		}

		/**
		 * <h3 class="en-US">Set the trust store information</h3>
		 * <h3 class="zh-CN">设置信任证书库信息</h3>
		 *
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public TrustStoreBuilder<ServerInfoBuilder<P>> trustStore() {
			return new TrustStoreBuilder<>(this,
					(this.serverInfo.getTrustStore() == null) ? new TrustStore() : this.serverInfo.getTrustStore());
		}

		/**
		 * <h3 class="en-US">Update server name</h3>
		 * <h3 class="zh-CN">修改服务器名称</h3>
		 *
		 * @param serverName <span class="en-US">Server name</span>
		 *                   <span class="zh-CN">服务器名称</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public ServerInfoBuilder<P> name(final String serverName) {
			if (!ObjectUtils.nullSafeEquals(this.serverInfo.getServerName(), serverName)) {
				this.serverInfo.setServerName(serverName);
				this.modified = Boolean.TRUE;
			}
			return this;
		}

		/**
		 * <h3 class="en-US">Update server level information</h3>
		 * <h3 class="zh-CN">修改服务器等级信息</h3>
		 *
		 * @param serverLevel <span class="en-US">Server level</span>
		 *                    <span class="zh-CN">服务器等级</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public ServerInfoBuilder<P> level(final int serverLevel) {
			if (this.serverInfo.getServerLevel() != serverLevel) {
				this.serverInfo.setServerLevel(serverLevel);
				this.modified = Boolean.TRUE;
			}
			return this;
		}

		@Override
		public ServerInfo build() throws BuilderException {
			if (this.modified) {
				this.serverInfo.setLastModified(DateTimeUtils.currentUTCTimeMillis());
			}
			return this.serverInfo;
		}

		@Override
		public void confirm(final Object object) throws BuilderException {
			if (object instanceof Authentication) {
				Authentication authentication = this.serverInfo.getAuthentication();
				if (authentication == null
						|| !ObjectUtils.nullSafeEquals(authentication.getAuthType(), ((Authentication) object).getAuthType())
						|| authentication.getLastModified() != ((Authentication) object).getLastModified()) {
					this.serverInfo.setAuthentication((Authentication) object);
					this.modified = Boolean.TRUE;
				}
			} else if (object instanceof TrustStore) {
				TrustStore trustStore = (TrustStore) object;
				TrustStore existedTrustStore = this.serverInfo.getTrustStore();
				if (existedTrustStore == null || !this.serverInfo.getTrustStore().equals(trustStore)) {
					this.serverInfo.setTrustStore(trustStore);
					this.modified = Boolean.TRUE;
				}
			}
		}
	}

	/**
	 * <h3 class="en-US">Generate database server configure information</h3>
	 * <h3 class="zh-CN">生成数据库服务器配置信息</h3>
	 *
	 * @param serverAddress <span class="en-US">Server address</span>
	 *                      <span class="zh-CN">服务器地址</span>
	 * @param serverPort    <span class="en-US">Server port number</span>
	 *                      <span class="zh-CN">服务器端口号</span>
	 * @return <span class="en-US">Database server configure information</span>
	 * <span class="zh-CN">数据库服务器配置信息</span>
	 */
	private static ServerInfo newInstance(final String serverAddress, final int serverPort) {
		ServerInfo serverInfo = new ServerInfo();
		serverInfo.setServerAddress(serverAddress);
		serverInfo.setServerPort(serverPort);
		return serverInfo;
	}

	/**
	 * <h2 class="en-US">Implementation class of distribute data schema configure information builder</h2>
	 * <h2 class="zh-CN">分布式数据源配置信息构建器实现类</h2>
	 *
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision: 1.0.0 $ $Date: Apr 10, 2018 15:48:19 $
	 */
	public static final class DistributeConfigBuilder<P extends ParentBuilder> extends SchemaConfigBuilder<P, DistributeSchemaConfig> {

		/**
		 * <h3 class="en-US">Constructor method for implementation class of distribute data schema configure information builder</h3>
		 * <h3 class="zh-CN">分布式数据源配置信息构建器实现类的构造方法</h3>
		 *
		 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
		 *                      <span class="zh-CN">父构建器实例对象</span>
		 * @param schemaConfig  <span class="en-US">Configure information instance object</span>
		 *                      <span class="zh-CN">配置信息实例对象</span>
		 */
		DistributeConfigBuilder(final P parentBuilder,
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
		public DistributeConfigBuilder<P> dialect(final String dialectName) {
			return super.dialect(dialectName, DistributeConfigBuilder.class);
		}

		/**
		 * <h3 class="en-US">Trust store configure information builder </h3>
		 * <h3 class="zh-CN">信任证书库配置信息构建器</h3>
		 *
		 * @return <span class="en-US">Builder instance object</span>
		 * <span class="zh-CN">构建器实例对象</span>
		 */
		public TrustStoreBuilder<DistributeConfigBuilder<P>> trustStore() {
			return new TrustStoreBuilder<>(this,
					(this.schemaConfig.getTrustStore() == null) ? new TrustStore() : this.schemaConfig.getTrustStore());
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
		public DistributeConfigBuilder<P> lowQuery(final long lowQueryTimeout) {
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
		public DistributeConfigBuilder<P> request(final int requestTimeout) {
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
		public DistributeConfigBuilder<P> timeout(final int validateTimeout, final int connectTimeout) {
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
		public DistributeConfigBuilder<P> connectionPool(final boolean pooled, final int minConnections,
		                                                 final int maxConnections) {
			return super.connectionPool(pooled, minConnections, maxConnections, DistributeConfigBuilder.class);
		}

		/**
		 * <h3 class="en-US">Generate basic identity authentication information builder instance object based on existing identity authentication information</h3>
		 * <h3 class="zh-CN">根据现有的身份认证信息生成基本身份认证信息构建器实例对象</h3>
		 *
		 * @param userName <span class="en-US">Username</span>
		 *                 <span class="zh-CN">用户名</span>
		 * @param passWord <span class="en-US">Password</span>
		 *                 <span class="zh-CN">密码</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public DistributeConfigBuilder<P> basicAuth(final String userName, final String passWord) {
			return super.basicAuth(userName, passWord, DistributeConfigBuilder.class);
		}

		/**
		 * <h3 class="en-US">Generate a builder instance object that uses the trust store authentication information
		 * in the certificate store based on the existing authentication information.</h3>
		 * <h3 class="zh-CN">根据现有的身份认证信息生成使用证书库中X.509证书认证信息的构建器实例对象</h3>
		 *
		 * @param storePath       <span class="en-US">Trust certificate store path</span>
		 *                        <span class="zh-CN">信任证书库地址</span>
		 * @param storePassword   <span class="en-US">Trust certificate store password</span>
		 *                        <span class="zh-CN">信任证书库密码</span>
		 * @param certificateName <span class="en-US">Certificate name</span>
		 *                        <span class="zh-CN">证书名称</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public DistributeConfigBuilder<P> trustStoreAuth(final String storePath, final String storePassword,
		                                                 final String certificateName) {
			return super.trustStoreAuth(storePath, storePassword, certificateName, DistributeConfigBuilder.class);
		}

		/**
		 * <h3 class="en-US">Generate a builder instance object that uses the X.509 certificate authentication information.</h3>
		 * <h3 class="zh-CN">根据现有的身份认证信息生成X.509证书认证信息的构建器实例对象</h3>
		 *
		 * @param x509Certificate <span class="en-US">X.509 certificate</span>
		 *                        <span class="zh-CN">X.509证书</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public DistributeConfigBuilder<P> x509Auth(@Nonnull final X509Certificate x509Certificate) {
			return super.x509Auth(x509Certificate, DistributeConfigBuilder.class);
		}

		/**
		 * <h3 class="en-US">Generate a builder instance object that uses the X.509 certificate authentication information.</h3>
		 * <h3 class="zh-CN">根据现有的身份认证信息生成X.509证书认证信息的构建器实例对象</h3>
		 *
		 * @param keyId        <span class="en-US">Identify Key ID</span>
		 *                     <span class="zh-CN">识别ID</span>
		 * @param secretKey    <span class="en-US">Identify secret key</span>
		 *                     <span class="zh-CN">识别密钥</span>
		 * @param sessionToken <span class="en-US">Session token</span>
		 *                     <span class="zh-CN">会话Token</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public DistributeConfigBuilder<P> tokenAuth(@Nonnull final String keyId, @Nonnull final String secretKey,
		                                            final String sessionToken) {
			return super.tokenAuth(keyId, secretKey, sessionToken, DistributeConfigBuilder.class);
		}

		/**
		 * <h3 class="en-US">Database server configure information builder</h3>
		 * <h3 class="zh-CN">数据库服务器配置信息构建器</h3>
		 *
		 * @param serverAddress <span class="en-US">Server address</span>
		 *                      <span class="zh-CN">服务器地址</span>
		 * @param serverPort    <span class="en-US">Server port number</span>
		 *                      <span class="zh-CN">服务器端口号</span>
		 * @return <span class="en-US">Database server configure information builder instance object</span>
		 * <span class="zh-CN">数据库服务器配置信息构建器实例对象</span>
		 */
		public ServerInfoBuilder<DistributeConfigBuilder<P>> serverBuilder(final String serverAddress, final int serverPort) {
			ServerInfo serverInfo = this.schemaConfig.getServerList()
					.stream()
					.filter(server -> server.match(serverAddress, serverPort))
					.findFirst()
					.orElse(newInstance(serverAddress, serverPort));
			return new ServerInfoBuilder<>(this, serverInfo);
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
		public DistributeConfigBuilder<P> removeServer(final String serverAddress, final int serverPort) {
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
		public DistributeConfigBuilder<P> databaseName(final String databaseName) {
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
		public DistributeConfigBuilder<P> useSsl(final boolean useSsl) {
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
		public DistributeConfigBuilder<P> cacheSize(final int cachedLimitSize) {
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
	 * @version $Revision: 1.0.0 $ $Date: Apr 10, 2018 15:48:19 $
	 */
	public static final class JdbcConfigBuilder<P extends ParentBuilder> extends SchemaConfigBuilder<P, JdbcSchemaConfig> {

		/**
		 * <h3 class="en-US">Constructor method for implementation class of JDBC data schema configure information builder</h3>
		 * <h3 class="zh-CN">JDBC数据源配置信息构建器实现类的构造函数</h3>
		 *
		 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
		 *                      <span class="zh-CN">父构建器实例对象</span>
		 * @param schemaConfig  <span class="en-US">Configure information instance object</span>
		 *                      <span class="zh-CN">配置信息实例对象</span>
		 */
		JdbcConfigBuilder(final P parentBuilder, final JdbcSchemaConfig schemaConfig) {
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
		public JdbcConfigBuilder<P> dialect(final String dialectName) {
			return super.dialect(dialectName, JdbcConfigBuilder.class);
		}

		/**
		 * <h3 class="en-US">Trust store configure information builder </h3>
		 * <h3 class="zh-CN">信任证书库配置信息构建器</h3>
		 *
		 * @return <span class="en-US">Builder instance object</span>
		 * <span class="zh-CN">构建器实例对象</span>
		 */
		public TrustStoreBuilder<JdbcConfigBuilder<P>> trustStore() {
			return new TrustStoreBuilder<>(this,
					(this.schemaConfig.getTrustStore() == null) ? new TrustStore() : this.schemaConfig.getTrustStore());
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
		public JdbcConfigBuilder<P> lowQuery(final long lowQueryTimeout) {
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
		public JdbcConfigBuilder<P> timeout(final int validateTimeout, final int connectTimeout) {
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
		public JdbcConfigBuilder<P> connectionPool(final boolean pooled, final int minConnections,
		                                           final int maxConnections) {
			return super.connectionPool(pooled, minConnections, maxConnections, JdbcConfigBuilder.class);
		}

		/**
		 * <h3 class="en-US">Generate basic identity authentication information builder instance object based on existing identity authentication information</h3>
		 * <h3 class="zh-CN">根据现有的身份认证信息生成基本身份认证信息构建器实例对象</h3>
		 *
		 * @param userName <span class="en-US">Username</span>
		 *                 <span class="zh-CN">用户名</span>
		 * @param passWord <span class="en-US">Password</span>
		 *                 <span class="zh-CN">密码</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public JdbcConfigBuilder<P> basicAuth(final String userName, final String passWord) {
			return super.basicAuth(userName, passWord, JdbcConfigBuilder.class);
		}

		/**
		 * <h3 class="en-US">Generate a builder instance object that uses the trust store authentication information
		 * in the certificate store based on the existing authentication information.</h3>
		 * <h3 class="zh-CN">根据现有的身份认证信息生成使用证书库中X.509证书认证信息的构建器实例对象</h3>
		 *
		 * @param storePath       <span class="en-US">Trust certificate store path</span>
		 *                        <span class="zh-CN">信任证书库地址</span>
		 * @param storePassword   <span class="en-US">Trust certificate store password</span>
		 *                        <span class="zh-CN">信任证书库密码</span>
		 * @param certificateName <span class="en-US">Certificate name</span>
		 *                        <span class="zh-CN">证书名称</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public JdbcConfigBuilder<P> trustStoreAuth(final String storePath, final String storePassword,
		                                           final String certificateName) {
			return super.trustStoreAuth(storePath, storePassword, certificateName, JdbcConfigBuilder.class);
		}

		/**
		 * <h3 class="en-US">Generate a builder instance object that uses the X.509 certificate authentication information.</h3>
		 * <h3 class="zh-CN">根据现有的身份认证信息生成X.509证书认证信息的构建器实例对象</h3>
		 *
		 * @param x509Certificate <span class="en-US">X.509 certificate</span>
		 *                        <span class="zh-CN">X.509证书</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public JdbcConfigBuilder<P> x509Auth(@Nonnull final X509Certificate x509Certificate) {
			return super.x509Auth(x509Certificate, JdbcConfigBuilder.class);
		}

		/**
		 * <h3 class="en-US">Generate a builder instance object that uses the X.509 certificate authentication information.</h3>
		 * <h3 class="zh-CN">根据现有的身份认证信息生成X.509证书认证信息的构建器实例对象</h3>
		 *
		 * @param keyId        <span class="en-US">Identify Key ID</span>
		 *                     <span class="zh-CN">识别ID</span>
		 * @param secretKey    <span class="en-US">Identify secret key</span>
		 *                     <span class="zh-CN">识别密钥</span>
		 * @param sessionToken <span class="en-US">Session token</span>
		 *                     <span class="zh-CN">会话Token</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public JdbcConfigBuilder<P> tokenAuth(@Nonnull final String keyId, @Nonnull final String secretKey,
		                                      final String sessionToken) {
			return super.tokenAuth(keyId, secretKey, sessionToken, JdbcConfigBuilder.class);
		}

		/**
		 * <h3 class="en-US">Database server configure information builder</h3>
		 * <h3 class="zh-CN">数据库服务器配置信息构建器</h3>
		 *
		 * @param serverAddress <span class="en-US">Server address</span>
		 *                      <span class="zh-CN">服务器地址</span>
		 * @param serverPort    <span class="en-US">Server port number</span>
		 *                      <span class="zh-CN">服务器端口号</span>
		 * @return <span class="en-US">Database server configure information builder instance object</span>
		 * <span class="zh-CN">数据库服务器配置信息构建器实例对象</span>
		 */
		public ServerInfoBuilder<JdbcConfigBuilder<P>> serverBuilder(final String serverAddress, final int serverPort) {
			ServerInfo serverInfo = this.schemaConfig.getServerList()
					.stream()
					.filter(server -> server.match(serverAddress, serverPort))
					.findFirst()
					.orElse(newInstance(serverAddress, serverPort));
			return new ServerInfoBuilder<>(this, serverInfo);
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
		public JdbcConfigBuilder<P> removeServer(final String serverAddress, final int serverPort) {
			List<ServerInfo> serverList = this.schemaConfig.getServerList();
			if (serverList.removeIf(serverInfo -> serverInfo.match(serverAddress, serverPort))) {
				this.schemaConfig.setServerList(serverList);
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
		public JdbcConfigBuilder<P> jdbcUrl(final String jdbcUrl) {
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
		public JdbcConfigBuilder<P> retry(final int retryCount, final long retryPeriod) {
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
		public JdbcConfigBuilder<P> cacheSize(final int cachedLimitSize) {
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
		public JdbcConfigBuilder<P> testConnection(final boolean testOnBorrow, final boolean testOnReturn) {
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

		/**
		 * <h3 class="en-US">Set sharding configure information</h3>
		 * <h3 class="zh-CN">设置分片配置信息</h3>
		 *
		 * @param sharding <span class="en-US">Data source support sharding</span>
		 *                 <span class="zh-CN">数据源是否支持分片</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public JdbcConfigBuilder<P> sharding(final boolean sharding) {
			if (sharding && this.schemaConfig.getJdbcUrl().contains("{catalog}")) {
				if (!this.schemaConfig.isSharding()) {
					this.schemaConfig.setSharding(Boolean.TRUE);
					this.modified = Boolean.TRUE;
				}
			} else {
				if (this.schemaConfig.isSharding()) {
					this.schemaConfig.setSharding(Boolean.FALSE);
					this.modified = Boolean.TRUE;
				}
				if (StringUtils.notBlank(this.schemaConfig.getCatalog())) {
					this.schemaConfig.setCatalog(Globals.DEFAULT_VALUE_STRING);
					this.modified = Boolean.TRUE;
				}
			}
			return this;
		}

		/**
		 * <h3 class="en-US">Set default database name</h3>
		 * <h3 class="zh-CN">设置默认数据库名</h3>
		 *
		 * @param catalog <span class="en-US">Database name</span>
		 *                <span class="zh-CN">数据库名</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public JdbcConfigBuilder<P> catalog(final String catalog) {
			if (!ObjectUtils.nullSafeEquals(this.schemaConfig.getCatalog(), catalog)) {
				this.modified = Boolean.TRUE;
			}
			this.schemaConfig.setCatalog(catalog);
			return this;
		}

		/**
		 * <h3 class="en-US">Set the parameter value of create databases</h3>
		 * <h3 class="zh-CN">设置创建数据库时使用的参数信息</h3>
		 *
		 * @param databaseParameters <span class="en-US">Parameter value of create databases</span>
		 *                           <span class="zh-CN">创建数据库时使用的参数信息</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public JdbcConfigBuilder<P> databaseParameters(final String databaseParameters) {
			if (!ObjectUtils.nullSafeEquals(this.schemaConfig.getDatabaseParameters(), databaseParameters)) {
				this.schemaConfig.setDatabaseParameters(databaseParameters);
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
	 * @version $Revision: 1.0.0 $ $Date: Apr 10, 2018 15:48:19 $
	 */
	public static final class RemoteConfigBuilder<P extends ParentBuilder> extends SchemaConfigBuilder<P, RemoteSchemaConfig> {

		/**
		 * <h3 class="en-US">Constructor method for implementation class of Remote data schema configure information builder</h3>
		 * <h3 class="zh-CN">远程数据源配置信息构建器实现类的构造函数</h3>
		 *
		 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
		 *                      <span class="zh-CN">父构建器实例对象</span>
		 * @param schemaConfig  <span class="en-US">Configure information instance object</span>
		 *                      <span class="zh-CN">配置信息实例对象</span>
		 */
		RemoteConfigBuilder(final P parentBuilder, final RemoteSchemaConfig schemaConfig) {
			super(parentBuilder, schemaConfig);
		}

		/**
		 * <h3 class="en-US">Trust store configure information builder </h3>
		 * <h3 class="zh-CN">信任证书库配置信息构建器</h3>
		 *
		 * @return <span class="en-US">Builder instance object</span>
		 * <span class="zh-CN">构建器实例对象</span>
		 */
		public TrustStoreBuilder<RemoteConfigBuilder<P>> trustStore() {
			return new TrustStoreBuilder<>(this,
					(this.schemaConfig.getTrustStore() == null) ? new TrustStore() : this.schemaConfig.getTrustStore());
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
		public RemoteConfigBuilder<P> lowQuery(final long lowQueryTimeout) {
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
		public RemoteConfigBuilder<P> timeout(final int validateTimeout, final int connectTimeout) {
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
		public RemoteConfigBuilder<P> type(final RemoteType remoteType) {
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
		public RemoteConfigBuilder<P> address(final String remoteAddress) {
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
		public RemoteConfigBuilder<P> keepAlive(final int keepAlive) {
			if (!ObjectUtils.nullSafeEquals(this.schemaConfig.getKeepAlive(), keepAlive)) {
				this.schemaConfig.setKeepAlive(keepAlive);
				this.modified = Boolean.TRUE;
			}
			return this;
		}

		/**
		 * <h3 class="en-US">Generate basic identity authentication information builder instance object based on existing identity authentication information</h3>
		 * <h3 class="zh-CN">根据现有的身份认证信息生成基本身份认证信息构建器实例对象</h3>
		 *
		 * @param userName <span class="en-US">Username</span>
		 *                 <span class="zh-CN">用户名</span>
		 * @param passWord <span class="en-US">Password</span>
		 *                 <span class="zh-CN">密码</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public RemoteConfigBuilder<P> basicAuth(final String userName, final String passWord) {
			return super.basicAuth(userName, passWord, RemoteConfigBuilder.class);
		}

		/**
		 * <h3 class="en-US">Generate a builder instance object that uses the trust store authentication information
		 * in the certificate store based on the existing authentication information.</h3>
		 * <h3 class="zh-CN">根据现有的身份认证信息生成使用证书库中X.509证书认证信息的构建器实例对象</h3>
		 *
		 * @param storePath       <span class="en-US">Trust certificate store path</span>
		 *                        <span class="zh-CN">信任证书库地址</span>
		 * @param storePassword   <span class="en-US">Trust certificate store password</span>
		 *                        <span class="zh-CN">信任证书库密码</span>
		 * @param certificateName <span class="en-US">Certificate name</span>
		 *                        <span class="zh-CN">证书名称</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public RemoteConfigBuilder<P> trustStoreAuth(final String storePath, final String storePassword,
		                                             final String certificateName) {
			return super.trustStoreAuth(storePath, storePassword, certificateName, RemoteConfigBuilder.class);
		}

		/**
		 * <h3 class="en-US">Generate a builder instance object that uses the X.509 certificate authentication information.</h3>
		 * <h3 class="zh-CN">根据现有的身份认证信息生成X.509证书认证信息的构建器实例对象</h3>
		 *
		 * @param x509Certificate <span class="en-US">X.509 certificate</span>
		 *                        <span class="zh-CN">X.509证书</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public RemoteConfigBuilder<P> x509Auth(@Nonnull final X509Certificate x509Certificate) {
			return super.x509Auth(x509Certificate, RemoteConfigBuilder.class);
		}

		/**
		 * <h3 class="en-US">Generate a builder instance object that uses the X.509 certificate authentication information.</h3>
		 * <h3 class="zh-CN">根据现有的身份认证信息生成X.509证书认证信息的构建器实例对象</h3>
		 *
		 * @param keyId        <span class="en-US">Identify Key ID</span>
		 *                     <span class="zh-CN">识别ID</span>
		 * @param secretKey    <span class="en-US">Identify secret key</span>
		 *                     <span class="zh-CN">识别密钥</span>
		 * @param sessionToken <span class="en-US">Session token</span>
		 *                     <span class="zh-CN">会话Token</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public RemoteConfigBuilder<P> tokenAuth(@Nonnull final String keyId, @Nonnull final String secretKey,
		                                        final String sessionToken) {
			return super.tokenAuth(keyId, secretKey, sessionToken, RemoteConfigBuilder.class);
		}

		/**
		 * <h3 class="en-US">Database server configure information builder</h3>
		 * <h3 class="zh-CN">数据库服务器配置信息构建器</h3>
		 *
		 * @param serverAddress <span class="en-US">Server address</span>
		 *                      <span class="zh-CN">服务器地址</span>
		 * @param serverPort    <span class="en-US">Server port number</span>
		 *                      <span class="zh-CN">服务器端口号</span>
		 * @return <span class="en-US">Database server configure information builder instance object</span>
		 * <span class="zh-CN">数据库服务器配置信息构建器实例对象</span>
		 */
		public ServerInfoBuilder<RemoteConfigBuilder<P>> serverBuilder(final String serverAddress, final int serverPort) {
			ServerInfo serverInfo = this.schemaConfig.getServerList()
					.stream()
					.filter(server -> server.match(serverAddress, serverPort))
					.findFirst()
					.orElse(newInstance(serverAddress, serverPort));
			return new ServerInfoBuilder<>(this, serverInfo);
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
		public RemoteConfigBuilder<P> removeServer(final String serverAddress, final int serverPort) {
			List<ServerInfo> serverList = this.schemaConfig.getServerList();
			if (serverList.removeIf(serverInfo -> serverInfo.match(serverAddress, serverPort))) {
				this.schemaConfig.setServerList(serverList);
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
		public ProxyConfigBuilder<RemoteConfigBuilder<P>> proxyConfig() {
			return new ProxyConfigBuilder<>(this,
					Optional.ofNullable(this.schemaConfig.getProxyConfig()).orElse(new ProxyConfig()));
		}

		/**
		 * <h3 class="en-US">Delete current proxy configure information</h3>
		 * <h3 class="zh-CN">删除代理服务器配置信息</h3>
		 *
		 * @return <span class="en-US">Current builder instance</span>
		 * <span class="zh-CN">当前构造器实例对象</span>
		 */
		public RemoteConfigBuilder<P> removeProxyConfig() {
			if (this.schemaConfig.getProxyConfig() != null) {
				this.schemaConfig.setProxyConfig(null);
				this.modified = Boolean.TRUE;
			}
			return this;
		}

		/**
		 * <h3 class="en-US">Generate basic identity authentication information builder instance object based on existing identity authentication information</h3>
		 * <h3 class="zh-CN">根据现有的身份认证信息生成基本身份认证信息构建器实例对象</h3>
		 *
		 * @return <span class="en-US">Builder implementation class of basic authentication information instance object</span>
		 * <span class="zh-CN">基本身份认证信息构建器实例对象</span>
		 */
		public AuthenticationBuilder.UserAuthenticationBuilder<RemoteConfigBuilder<P>> basicAuth() {
			return new AuthenticationBuilder.UserAuthenticationBuilder<>(this,
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
		public AuthenticationBuilder.TrustStoreAuthenticationBuilder<RemoteConfigBuilder<P>> trustStoreAuth() {
			return new AuthenticationBuilder.TrustStoreAuthenticationBuilder<>(this,
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
		public AuthenticationBuilder.X509AuthenticationBuilder<RemoteConfigBuilder<P>> x509Auth() {
			return new AuthenticationBuilder.X509AuthenticationBuilder<>(this,
					Optional.ofNullable(this.schemaConfig.getAuthentication())
							.filter(authentication -> authentication instanceof X509Authentication)
							.map(authentication -> (X509Authentication) authentication)
							.orElse(new X509Authentication()));
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
