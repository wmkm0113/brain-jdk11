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

package org.nervousync.brain.test;

import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.*;
import org.nervousync.brain.configs.BrainConfigure;
import org.nervousync.brain.configs.builder.BrainConfigureBuilder;
import org.nervousync.brain.configs.builder.SchemaConfigBuilder;
import org.nervousync.brain.enumerations.ddl.DDLType;
import org.nervousync.brain.enumerations.remote.RemoteType;
import org.nervousync.cache.builder.CacheConfigBuilder;
import org.nervousync.cache.commons.CacheGlobals;
import org.nervousync.commons.Globals;
import org.nervousync.utils.*;

import java.net.Proxy;
import java.security.KeyPair;
import java.util.Date;
import java.util.Properties;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public final class BrainConfigureTest {

	static {
		LoggerUtils.initLoggerConfigure(Level.DEBUG);
	}

	@Test
	@Order(0)
	public void storageConfig() {
		BrainConfigure configure = this.newBuilder(null)
				.storageConfig()
				.basePath("BasePath")
				.config(5, 30 * 24 * 60 * 60 * 1000L)
				.provider("StorageProvider")
				.confirmParent(BrainConfigureBuilder.class)
				.confirm();
		System.out.println(configure.toString(StringUtils.StringType.XML));
	}

	@Test
	@Order(10)
	public void distributeConfig() {
		BrainConfigure configure =
				this.distribute(this.newBuilder(null).distributeConfig("Distribute"))
						.confirm();
		System.out.println(configure.toString(StringUtils.StringType.XML));
		configure = this.newBuilder(configure)
				.distributeConfig("Distribute")
				.removeServer("localhost", 2271)
				.serverBuilder("localhost", 2270)
				.level(40)
				.confirmParent(SchemaConfigBuilder.DistributeConfigBuilder.class)
				.trustStoreAuthenticationBuilder()
				.confirmParent(SchemaConfigBuilder.DistributeConfigBuilder.class)
				.confirmParent(BrainConfigureBuilder.class)
				.confirm();
		System.out.println(configure.toString(StringUtils.StringType.XML));
	}

	@Test
	@Order(20)
	public void jdbcConfig() throws Exception {
		BrainConfigure configure =
				this.jdbc(this.newBuilder(null).jdbcConfig("Jdbc")).confirm();
		System.out.println(configure.toString(StringUtils.StringType.XML));
		configure = this.newBuilder(configure)
				.jdbcConfig("Jdbc")
				.removeServer("localhost", 2271)
				.serverBuilder("localhost", 2270)
				.level(40)
				.userAuthenticationBuilder()
				.authenticate("testUser", "testPwd")
				.confirmParent(SchemaConfigBuilder.ServerInfoBuilder.class)
				.confirmParent(SchemaConfigBuilder.JdbcConfigBuilder.class)
				.trustStoreAuthenticationBuilder()
				.confirmParent(SchemaConfigBuilder.JdbcConfigBuilder.class)
				.testConnection(Boolean.TRUE, Boolean.TRUE)
				.confirmParent(BrainConfigureBuilder.class)
				.confirm();
		System.out.println(configure.toString(StringUtils.StringType.XML));
	}

	@Test
	@Order(30)
	public void remoteConfig() throws Exception {
		BrainConfigure configure =
				this.remote(this.newBuilder(null).remoteConfig("Remote")).confirm();
		System.out.println(configure.toString(StringUtils.StringType.XML));
		configure = this.newBuilder(configure)
				.disableLazyInit()
				.disableJmxMonitor()
				.remoteConfig("Remote")
				.confirmParent(BrainConfigureBuilder.class)
				.jdbcConfig("Jdbc")
				.removeServer("localhost", 2271)
				.testConnection(Boolean.TRUE, Boolean.TRUE)
				.confirmParent(BrainConfigureBuilder.class)
				.remoteConfig("Remote")
				.type(RemoteType.SOAP)
				.confirmParent(BrainConfigureBuilder.class)
				.confirm();
		System.out.println(configure.toString(StringUtils.StringType.XML));
	}

	@Test
	@Order(40)
	public void mixedConfig() throws Exception {
		BrainConfigureBuilder brainConfigureBuilder = this.newBuilder(null);
		brainConfigureBuilder = this.distribute(brainConfigureBuilder.distributeConfig("Distribute"));
		brainConfigureBuilder = this.jdbc(brainConfigureBuilder.jdbcConfig("Jdbc"));
		brainConfigureBuilder = this.remote(brainConfigureBuilder.remoteConfig("Remote"));
		brainConfigureBuilder = this.cache(brainConfigureBuilder.cacheConfig());
		BrainConfigure configure = brainConfigureBuilder.defaultSchema("Jdbc").confirm();
		String xml = configure.toString(StringUtils.StringType.XML);
		configure = StringUtils.stringToObject(xml, BrainConfigure.class, "https://nervousync.org/schemas/brain");
		System.out.println(configure.toString(StringUtils.StringType.XML));
		configure = this.newBuilder(configure)
				.remoteConfig("Remote")
				.type(RemoteType.SOAP)
				.confirmParent(BrainConfigureBuilder.class)
				.defaultSchema("Remote")
				.confirm();
		System.out.println(configure.toString(StringUtils.StringType.XML));
	}

	private BrainConfigureBuilder distribute(final SchemaConfigBuilder.DistributeConfigBuilder configBuilder) {
		return configBuilder.dialect("DistributeDialect")
				.serverBuilder("localhost", 2270)
				.name("datacenter1")
				.level(10)
				.confirmParent(SchemaConfigBuilder.DistributeConfigBuilder.class)
				.serverBuilder("localhost", 2271)
				.name("datacenter2")
				.level(20)
				.confirmParent(SchemaConfigBuilder.DistributeConfigBuilder.class)
				.serverBuilder("localhost", 2272)
				.name("datacenter3")
				.level(30)
				.confirmParent(SchemaConfigBuilder.DistributeConfigBuilder.class)
				.serverBuilder("localhost", 2273)
				.name("datacenter")
				.level(0)
				.confirmParent(SchemaConfigBuilder.DistributeConfigBuilder.class)
				.connectionPool(Boolean.TRUE, 2, 10)
				.databaseName("DatabaseName")
				.useSsl(Boolean.TRUE)
				.request(10)
				.cacheSize(20)
				.lowQuery(1000L)
				.timeout(5, 5)
				.userAuthenticationBuilder()
				.authenticate("username", "password")
				.confirmParent(SchemaConfigBuilder.DistributeConfigBuilder.class)
				.confirmParent(BrainConfigureBuilder.class);
	}

	private BrainConfigureBuilder jdbc(final SchemaConfigBuilder.JdbcConfigBuilder configBuilder)
			throws Exception {
		KeyPair keyPair = SecurityUtils.RSAKeyPair();
		return configBuilder.dialect("JdbcDialect")
				.servers("localhost|2270|10" + FileUtils.LF + "localhost|2271|20" + FileUtils.LF + "localhost|2272|30")
				.connectionPool(Boolean.TRUE, 2, 10)
				.jdbcUrl("jdbc:url://testUrl")
				.lowQuery(1000L)
				.timeout(5, 5)
				.x509AuthenticationBuilder()
				.x509(CertificateUtils.x509(keyPair.getPublic(), IDUtils.snowflake(), new Date(),
						new Date(DateTimeUtils.expireMonth(2)), "TestCert",
						keyPair.getPrivate(), "SHA256withRSA"))
				.confirmParent(SchemaConfigBuilder.JdbcConfigBuilder.class)
				.retry(3, 500L)
				.cacheSize(20)
				.confirmParent(BrainConfigureBuilder.class);
	}

	private BrainConfigureBuilder remote(final SchemaConfigBuilder.RemoteConfigBuilder configBuilder) throws Exception {
		KeyPair keyPair = SecurityUtils.RSAKeyPair();
		return configBuilder
				.address("http://localhost")
				.type(RemoteType.Restful)
				.proxyConfig()
				.proxyType(Proxy.Type.HTTP)
				.serverConfig("http://localhost", 1080)
				.authenticator("proxyUser", "proxyPassword")
				.confirmParent(SchemaConfigBuilder.RemoteConfigBuilder.class)
				.lowQuery(1000L)
				.timeout(5, 5)
				.x509AuthenticationBuilder()
				.x509(CertificateUtils.x509(keyPair.getPublic(), IDUtils.snowflake(), new Date(),
						new Date(DateTimeUtils.expireMonth(2)), "TestCert",
						keyPair.getPrivate(), "SHA256withRSA"))
				.confirmParent(SchemaConfigBuilder.RemoteConfigBuilder.class)
				.keepAlive(600)
				.confirmParent(BrainConfigureBuilder.class);
	}

	private BrainConfigureBuilder cache(final CacheConfigBuilder cacheConfigBuilder) {
		Properties properties = PropertiesUtils.loadProperties("src/test/resources/authorization.xml");
		if (!properties.isEmpty()) {
			cacheConfigBuilder.providerName("JedisProvider")
					.connectTimeout(CacheGlobals.DEFAULT_CONNECTION_TIMEOUT)
					.expireTime(5)
					.clientPoolSize(CacheGlobals.DEFAULT_CLIENT_POOL_SIZE)
					.maximumClient(CacheGlobals.DEFAULT_MAXIMUM_CLIENT)
					.serverBuilder()
					.serverConfig(properties.getProperty("ServerAddress"), Integer.parseInt(properties.getProperty("ServerPort")))
					.serverWeight(properties.containsKey("ServerWeight")
							? Integer.parseInt(properties.getProperty("ServerWeight"))
							: Globals.DEFAULT_VALUE_INT)
					.confirmParent(CacheConfigBuilder.class)
					.authorization(properties.getProperty("UserName"), properties.getProperty("PassWord"));
		}
		return cacheConfigBuilder.confirmParent(BrainConfigureBuilder.class);
	}

	private BrainConfigureBuilder newBuilder(final BrainConfigure configure) {
		BrainConfigureBuilder brainConfigureBuilder =
				(configure == null) ? BrainConfigureBuilder.newBuilder() : BrainConfigureBuilder.newBuilder(configure);
		return brainConfigureBuilder.configDDL(DDLType.SYNCHRONIZE)
				.enableJmxMonitor()
				.enableLazyInit();
	}
}
