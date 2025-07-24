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

package org.nervousync.brain.schemas.remote;

import jakarta.annotation.Nonnull;
import jakarta.persistence.LockModeType;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.xml.ws.BindingProvider;
import org.glassfish.jersey.client.ClientProperties;
import org.nervousync.brain.commons.BrainCommons;
import org.nervousync.brain.configs.auth.impl.TrustStoreAuthentication;
import org.nervousync.brain.configs.auth.impl.UserAuthentication;
import org.nervousync.brain.configs.schema.impl.RemoteSchemaConfig;
import org.nervousync.brain.configs.transactional.TransactionalConfig;
import org.nervousync.brain.defines.IndexDefine;
import org.nervousync.brain.defines.InitOption;
import org.nervousync.brain.defines.TableDefine;
import org.nervousync.brain.dialects.DialectFactory;
import org.nervousync.brain.dialects.remote.RemoteClient;
import org.nervousync.brain.dialects.remote.RemoteDialect;
import org.nervousync.brain.enumerations.ddl.DDLType;
import org.nervousync.brain.enumerations.ddl.DropOption;
import org.nervousync.brain.enumerations.remote.RemoteType;
import org.nervousync.brain.exceptions.sql.MultilingualSQLException;
import org.nervousync.brain.query.PartialCollection;
import org.nervousync.brain.query.QueryInfo;
import org.nervousync.brain.schemas.BaseSchema;
import org.nervousync.commons.Globals;
import org.nervousync.http.cert.TrustCert;
import org.nervousync.http.security.GeneX509TrustManager;
import org.nervousync.proxy.ProxyConfig;
import org.nervousync.utils.*;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <h2 class="en-US">Remote data source implementation class</h2>
 * <h2 class="zh-CN">远程数据源实现类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Feb 18, 2019 10:38:52 $
 */
public final class RemoteSchema extends BaseSchema<RemoteDialect> implements RemoteSchemaMBean {

	/**
	 * <span class="en-US">Remote type</span>
	 * <span class="zh-CN">远程类型</span>
	 */
	private final RemoteType remoteType;
	/**
	 * <span class="en-US">Remote address</span>
	 * <span class="zh-CN">远端地址</span>
	 */
	private final String remoteAddress;
	/**
	 * <span class="en-US">Request header information map</span>
	 * <span class="zh-CN">请求头部信息映射</span>
	 */
	private final Map<String, String> configMap = new HashMap<>();
	/**
	 * <span class="en-US">Configured client generator</span>
	 * <span class="zh-CN">配置好的客户端生成器</span>
	 */
	private final ClientBuilder clientBuilder;
	/**
	 * <span class="en-US">The remote data source client instance object used by the current thread</span>
	 * <span class="zh-CN">当前线程使用的远程数据源客户端实例对象</span>
	 */
	private final ThreadLocal<RemoteClient> operatorThreadLocal = new ThreadLocal<>();
	/**
	 * <span class="en-US">Using connection list</span>
	 * <span class="zh-CN">使用中的连接列表</span>
	 */
	private final AtomicInteger activeConnections;

	/**
	 * <h3 class="en-US">Constructor method for remote data source implementation class</h3>
	 * <h3 class="zh-CN">远程数据源实现类的构造方法</h3>
	 *
	 * @param schemaConfig <span class="en-US">Remote data source configure information</span>
	 *                     <span class="zh-CN">远程数据源配置信息</span>
	 * @throws SQLException <span class="en-US">Database server information hasn't found or sharding configuration error</span>
	 *                      <span class="zh-CN">数据库服务器信息未找到或分片配置出错</span>
	 */
	public RemoteSchema(@Nonnull final RemoteSchemaConfig schemaConfig) throws SQLException {
		super(schemaConfig, DialectFactory.retrieve(schemaConfig.getDialectName()).unwrap(RemoteDialect.class));
		this.remoteType = schemaConfig.getRemoteType();
		this.remoteAddress = schemaConfig.getRemoteAddress();
		if (RemoteType.Restful.equals(this.remoteType)) {
			this.clientBuilder = ClientBuilder.newBuilder()
					.connectTimeout(this.getConnectTimeout(), TimeUnit.SECONDS)
					.property(ClientProperties.FOLLOW_REDIRECTS, HttpClient.Redirect.NORMAL)
					.property(ClientProperties.CONNECT_TIMEOUT, this.getConnectTimeout());

			if (this.trustStore != null) {
				try {
					TrustCert trustCert =
							TrustCert.newInstance(FileUtils.readFileBytes(this.trustStore.getStorePath()),
									this.trustStore.getStorePassword());
					SSLContext sslContext = SSLContext.getInstance("TLS");
					GeneX509TrustManager x509TrustManager =
							GeneX509TrustManager.newInstance(this.trustStore.getStorePassword(), List.of(trustCert));
					sslContext.init(new KeyManager[0], new TrustManager[]{x509TrustManager}, new SecureRandom());
					this.clientBuilder.sslContext(sslContext);
				} catch (Exception e) {
					throw new MultilingualSQLException(0x00DB00000044L, e);
				}
			}
			Optional.ofNullable(this.trustStore)
					.map(store ->
							CertificateUtils.loadKeyStore(store.getStorePath(), store.getStorePassword()))
					.ifPresent(this.clientBuilder::trustStore);

			if (this.authentication != null) {
				if (this.authentication instanceof TrustStoreAuthentication) {
					TrustStoreAuthentication tsAuth = (TrustStoreAuthentication) this.authentication;
					KeyStore keyStore = CertificateUtils.loadKeyStore(tsAuth.getStorePath(), tsAuth.getStorePassword());
					if (keyStore != null) {
						this.clientBuilder.keyStore(keyStore, tsAuth.getStorePassword());
					}
				}
				if (this.authentication instanceof UserAuthentication) {
					UserAuthentication userAuthentication = (UserAuthentication) this.authentication;
					if (StringUtils.notBlank(userAuthentication.getUserName())) {
						this.configMap.put(BindingProvider.USERNAME_PROPERTY, userAuthentication.getUserName());
					}
					if (StringUtils.notBlank(userAuthentication.getPassWord())) {
						this.configMap.put(BindingProvider.PASSWORD_PROPERTY, userAuthentication.getPassWord());
					}
				}
			}

			Optional.ofNullable(schemaConfig.getProxyConfig())
					.filter(proxyConfig -> StringUtils.notBlank(proxyConfig.getProxyAddress()))
					.ifPresent(proxyConfig -> {
						String proxyURI = proxyConfig.getProxyAddress();
						if (proxyConfig.getProxyPort() != Globals.DEFAULT_VALUE_INT) {
							proxyURI += ":" + proxyConfig.getProxyPort();
						}
						this.clientBuilder.property(ClientProperties.PROXY_URI, proxyURI);
						String authentication = Globals.DEFAULT_VALUE_STRING;
						if (StringUtils.notBlank(proxyConfig.getUserName())) {
							authentication += proxyConfig.getUserName() + ":";
							this.clientBuilder.property(ClientProperties.PROXY_USERNAME, proxyConfig.getUserName());
						}
						if (StringUtils.notBlank(proxyConfig.getPassword())) {
							authentication += proxyConfig.getPassword();
							this.clientBuilder.property(ClientProperties.PROXY_PASSWORD, proxyConfig.getPassword());
						}
						this.configMap.put("Proxy-Authorization",
								StringUtils.base64Encode(authentication.getBytes(Charset.forName(Globals.DEFAULT_ENCODING))));
					});
		} else {
			this.clientBuilder = null;
			if (this.getConnectTimeout() > 0) {
				this.configMap.put("sun.net.client.defaultConnectTimeout", Integer.toString(this.getConnectTimeout()));
			}
			Optional.ofNullable(schemaConfig.getProxyConfig())
					.filter(proxyConfig -> StringUtils.notBlank(proxyConfig.getProxyAddress()))
					.ifPresent(proxyConfig -> {
						ProxySelector.setDefault(new ServiceProxySelector(this.remoteAddress, proxyConfig));
						if (StringUtils.notBlank(proxyConfig.getUserName())) {
							Authenticator.setDefault(
									new ProxyAuthenticator(proxyConfig.getUserName(), proxyConfig.getPassword()));
						}
					});
		}
		this.activeConnections = new AtomicInteger(Globals.INITIALIZE_INT_VALUE);
	}

	/**
	 * <h2 class="en-US">Proxy server identity authentication implementation class</h2>
	 * <h2 class="zh-CN">代理服务器身份认证实现类</h2>
	 *
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision: 1.0.0 $ $Date: Feb 18, 2019 10:38:52 $
	 */
	private static final class ProxyAuthenticator extends Authenticator {

		/**
		 * <span class="en-US">Identity authentication information</span>
		 * <span class="zh-CN">身份认证信息</span>
		 */
		private final PasswordAuthentication passwordAuthentication;

		/**
		 * <h3 class="en-US">Constructor method for proxy server identity authentication implementation class</h3>
		 * <h3 class="zh-CN">代理服务器身份认证实现类的构造方法</h3>
		 *
		 * @param userName <span class="en-US">User name</span>
		 *                 <span class="zh-CN">用户名</span>
		 * @param passWord <span class="en-US">Password</span>
		 *                 <span class="zh-CN">密码</span>
		 */
		ProxyAuthenticator(final String userName, final String passWord) {
			this.passwordAuthentication =
					new PasswordAuthentication(userName, (passWord == null) ? new char[0] : passWord.toCharArray());
		}

		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return this.passwordAuthentication;
		}
	}

	/**
	 * <h2 class="en-US">Proxy server selector implementation class</h2>
	 * <h2 class="zh-CN">代理服务器选择器实现类</h2>
	 *
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision: 1.0.0 $ $Date: Feb 18, 2019 10:38:52 $
	 */
	private static final class ServiceProxySelector extends ProxySelector {

		/**
		 * <span class="en-US">Need to use the access address of the proxy server</span>
		 * <span class="zh-CN">需要使用代理服务器的访问地址</span>
		 */
		private final String remoteAddress;
		/**
		 * <span class="en-US">System default proxy server selector</span>
		 * <span class="zh-CN">系统默认的代理服务器选择器</span>
		 */
		private final ProxySelector defaultSelector;
		/**
		 * <span class="en-US">Proxy server configuration information</span>
		 * <span class="zh-CN">代理服务器配置信息</span>
		 */
		private final ProxyConfig proxyConfig;

		/**
		 * <h3 class="en-US">Constructor method for proxy server selector implementation class</h3>
		 * <h3 class="zh-CN">代理服务器选择器实现类的构造方法</h3>
		 *
		 * @param remoteAddress <span class="en-US">Need to use the access address of the proxy server</span>
		 *                      <span class="zh-CN">需要使用代理服务器的访问地址</span>
		 * @param proxyConfig   <span class="en-US">Proxy server configuration information</span>
		 *                      <span class="zh-CN">代理服务器配置信息</span>
		 */
		ServiceProxySelector(@Nonnull final String remoteAddress, @Nonnull final ProxyConfig proxyConfig) {
			this.remoteAddress = remoteAddress;
			this.defaultSelector = ProxySelector.getDefault();
			this.proxyConfig = proxyConfig;
		}

		@Override
		public List<Proxy> select(final URI uri) {
			StringBuilder stringBuilder = new StringBuilder(uri.getScheme()).append("://").append(uri.getHost());
			if (uri.getPort() != -1) {
				stringBuilder.append(":").append(uri.getPort());
			}
			stringBuilder.append(uri.getPath());
			if (this.remoteAddress.startsWith(stringBuilder.toString())) {
				return Collections.singletonList(new Proxy(Proxy.Type.HTTP,
						new InetSocketAddress(this.proxyConfig.getProxyAddress(), this.proxyConfig.getProxyPort())));
			}
			return this.defaultSelector.select(uri);
		}

		@Override
		public void connectFailed(final URI uri, final SocketAddress sa, final IOException ioe) {
		}
	}

	@Override
	public void initialize() {
	}

	@Override
	public void beginTransactional() throws SQLException {
		if (this.operatorThreadLocal.get() == null) {
			try {
				RemoteClient remoteClient;
				switch (this.remoteType) {
					case SOAP:
						remoteClient = this.dialect.SOAPClient(this.remoteAddress, this.configMap);
						break;
					case Restful:
						remoteClient = this.dialect.restfulClient(this.remoteAddress, this.clientBuilder, this.configMap);
						break;
					default:
						throw new MultilingualSQLException(0x00DB00000030L, this.remoteType);
				}
				TransactionalConfig transactionalConfig = this.txConfig.get();
				if (transactionalConfig != null) {
					remoteClient.beginTransactional(transactionalConfig.getTransactionalCode(),
							transactionalConfig.getIsolation(), transactionalConfig.getTimeout());
				}
				this.operatorThreadLocal.set(remoteClient);
			} catch (MalformedURLException e) {
				throw new MultilingualSQLException(0x00DB00000035L, e, this.remoteType.toString(), this.remoteAddress);
			}
		}
		this.activeConnections.incrementAndGet();
	}

	@Override
	public void rollback(final Exception e) {
		TransactionalConfig transactionalConfig = this.txConfig.get();
		if (transactionalConfig != null && transactionalConfig.getIsolation() != Connection.TRANSACTION_NONE
				&& transactionalConfig.rollback(e)) {
			this.operatorThreadLocal.get().rollback(this.txConfig.get().getTransactionalCode());
		}
	}

	@Override
	public void commit() {
		if (this.txConfig.get() != null) {
			this.operatorThreadLocal.get().commit(this.txConfig.get().getTransactionalCode());
		}
	}

	@Override
	public void truncateTables() {
		this.operatorThreadLocal.get().truncateTables();
	}

	@Override
	public void truncateTable(@Nonnull final TableDefine tableDefine) {
		this.operatorThreadLocal.get().truncateTable(tableDefine.getTableName());
	}

	@Override
	public void dropTables(final DropOption dropOption) {
		this.operatorThreadLocal.get().dropTables(dropOption);
	}

	@Override
	public void dropTable(@Nonnull final TableDefine tableDefine, @Nonnull final DropOption dropOption) {
		StringBuilder indexNames = new StringBuilder();
		for (IndexDefine indexDefine : tableDefine.getIndexDefines()) {
			indexNames.append(BrainCommons.DEFAULT_SPLIT_CHARACTER).append(indexDefine.getIndexName());
		}
		this.operatorThreadLocal.get().dropTable(tableDefine.getTableName(),
				indexNames.length() > 0
						? indexNames.substring(BrainCommons.DEFAULT_SPLIT_CHARACTER.length())
						: indexNames.toString(),
				dropOption);
	}

	@Override
	public boolean lockRecord(@Nonnull final TableDefine tableDefine, @Nonnull final Map<String, Object> filterMap,
	                          final LockModeType lockOption) {
		return this.operatorThreadLocal.get().lockRecord(tableDefine.getTableName(),
				StringUtils.objectToString(filterMap, StringUtils.StringType.JSON, Boolean.FALSE));
	}

	@Override
	public Map<String, Object> insert(@Nonnull final TableDefine tableDefine,
	                                  @Nonnull final Map<String, Object> dataMap) {
		return Optional.ofNullable(this.operatorThreadLocal.get())
				.map(remoteClient ->
						remoteClient.insert(tableDefine.getTableName(),
								StringUtils.objectToString(dataMap, StringUtils.StringType.JSON, Boolean.FALSE)))
				.filter(StringUtils::notBlank)
				.map(responseData -> StringUtils.dataToMap(responseData, StringUtils.StringType.JSON))
				.orElse(Map.of());
	}

	@Override
	public Map<String, Object> retrieve(@Nonnull final TableDefine tableDefine, final String columns,
	                                    @Nonnull final Map<String, Object> filterMap, final boolean forUpdate,
	                                    final LockModeType lockOption) {
		return Optional.ofNullable(this.operatorThreadLocal.get())
				.map(remoteClient ->
						remoteClient.retrieve(tableDefine.getTableName(),
								StringUtils.isEmpty(columns) ? SELECT_ALL_COLUMNS : columns,
								StringUtils.objectToString(filterMap, StringUtils.StringType.JSON, Boolean.FALSE),
								forUpdate, lockOption))
				.filter(StringUtils::notBlank)
				.map(responseData -> StringUtils.dataToMap(responseData, StringUtils.StringType.JSON))
				.orElse(Map.of());
	}

	@Override
	public int update(@Nonnull final TableDefine tableDefine, @Nonnull final Map<String, Object> dataMap,
	                  @Nonnull final Map<String, Object> filterMap) {
		return this.operatorThreadLocal.get().update(tableDefine.getTableName(),
				StringUtils.objectToString(dataMap, StringUtils.StringType.JSON, Boolean.FALSE),
				StringUtils.objectToString(filterMap, StringUtils.StringType.JSON, Boolean.FALSE));
	}

	@Override
	public int delete(@Nonnull final TableDefine tableDefine, @Nonnull final Map<String, Object> filterMap) {
		return this.operatorThreadLocal.get().delete(tableDefine.getTableName(),
				StringUtils.objectToString(filterMap, StringUtils.StringType.JSON, Boolean.FALSE));
	}

	@Override
	public PartialCollection query(@Nonnull final QueryInfo queryInfo) {
		return PartialCollection.parse(this.operatorThreadLocal.get().query(queryInfo.toString(StringUtils.StringType.JSON)));
	}

	@Override
	public Long queryTotal(final QueryInfo queryInfo) {
		return this.operatorThreadLocal.get().queryTotal(queryInfo.toString(StringUtils.StringType.JSON));
	}

	@Override
	public void initTable(@Nonnull final DDLType ddlType, @Nonnull final TableDefine tableDefine,
	                      @Nonnull final Map<String, InitOption> initOptionsMap) {
		//  Not support the table initialize operating
	}

	@Override
	protected void clearTransactional() {
		this.operatorThreadLocal.remove();
		this.activeConnections.decrementAndGet();
	}

	@Override
	public void close() {
	}

	@Override
	public String getRemoteAddress() {
		return this.remoteAddress;
	}

	@Override
	public int getActiveCount() {
		return this.activeConnections.get();
	}
}
