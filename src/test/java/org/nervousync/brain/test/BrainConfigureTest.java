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
import org.nervousync.utils.*;

import java.net.Proxy;
import java.security.KeyPair;
import java.util.Date;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public final class BrainConfigureTest {

	static {
		LoggerUtils.initLoggerConfigure(Level.DEBUG);
	}

	@Test
	@Order(10)
	public void distributeConfig() {
		BrainConfigure configure =
				this.distribute(this.newBuilder(null).distributeConfig("Distribute"))
						.build();
		System.out.println(configure.toString(StringUtils.StringType.XML));
		configure = this.newBuilder(configure)
				.distributeConfig("Distribute")
				.removeServer("localhost", 2271)
				.serverBuilder("localhost", 2270)
				.level(40)
				.confirm()
				.trustStoreAuth()
				.confirm()
				.confirm()
				.build();
		System.out.println(configure.toString(StringUtils.StringType.XML));
	}

	@Test
	@Order(20)
	public void jdbcConfig() throws Exception {
		BrainConfigure configure =
				this.jdbc(this.newBuilder(null).jdbcConfig("Jdbc")).build();
		System.out.println(configure.toString(StringUtils.StringType.XML));
		configure = this.newBuilder(configure)
				.jdbcConfig("Jdbc")
				.removeServer("localhost", 2271)
				.serverBuilder("localhost", 2270)
				.level(40)
				.userAuthenticationBuilder()
				.authenticate("testUser", "testPwd")
				.confirm()
				.confirm()
				.trustStoreAuth()
				.confirm()
				.testConnection(Boolean.TRUE, Boolean.TRUE)
				.confirm()
				.build();
		System.out.println(configure.toString(StringUtils.StringType.XML));
	}

	@Test
	@Order(30)
	public void remoteConfig() throws Exception {
		BrainConfigure configure =
				this.remote(this.newBuilder(null).remoteConfig("Remote")).build();
		System.out.println(configure.toString(StringUtils.StringType.XML));
		configure = this.newBuilder(configure)
				.disableLazyInit()
				.disableJmxMonitor()
				.remoteConfig("Remote")
				.confirm()
				.jdbcConfig("Jdbc")
				.removeServer("localhost", 2271)
				.testConnection(Boolean.TRUE, Boolean.TRUE)
				.confirm()
				.remoteConfig("Remote")
				.type(RemoteType.SOAP)
				.confirm()
				.build();
		System.out.println(configure.toString(StringUtils.StringType.XML));
	}

	@Test
	@Order(40)
	public void mixedConfig() throws Exception {
		BrainConfigureBuilder<?> brainConfigureBuilder = this.newBuilder(null);
		brainConfigureBuilder = this.distribute(brainConfigureBuilder.distributeConfig("Distribute"));
		brainConfigureBuilder = this.jdbc(brainConfigureBuilder.jdbcConfig("Jdbc"));
		brainConfigureBuilder = this.remote(brainConfigureBuilder.remoteConfig("Remote"));
		BrainConfigure configure = brainConfigureBuilder.defaultSchema("Jdbc").build();
		String xml = configure.toString(StringUtils.StringType.XML);
		configure = StringUtils.stringToObject(xml, BrainConfigure.class, "https://nervousync.org/schemas/brain");
		System.out.println(configure.toString(StringUtils.StringType.JSON));
		configure = this.newBuilder(configure)
				.remoteConfig("Remote")
				.type(RemoteType.SOAP)
				.confirm()
				.defaultSchema("Remote")
				.build();
		System.out.println(configure.toString(StringUtils.StringType.XML));
	}

	private BrainConfigureBuilder<?> distribute(final SchemaConfigBuilder.DistributeConfigBuilder configBuilder) {
		return configBuilder.dialect("DistributeDialect")
				.serverBuilder("localhost", 2270)
				.name("datacenter1")
				.level(10)
				.confirm()
				.serverBuilder("localhost", 2271)
				.name("datacenter2")
				.level(20)
				.confirm()
				.serverBuilder("localhost", 2272)
				.name("datacenter3")
				.level(30)
				.confirm()
				.serverBuilder("localhost", 2273)
				.name("datacenter")
				.level(0)
				.confirm()
				.connectionPool(Boolean.TRUE, 2, 10)
				.databaseName("DatabaseName")
				.useSsl(Boolean.TRUE)
				.request(10)
				.cacheSize(20)
				.lowQuery(1000L)
				.timeout(5, 5)
				.basicAuth()
				.authenticate("username", "password")
				.confirm()
				.confirm();
	}

	private BrainConfigureBuilder<?> jdbc(final SchemaConfigBuilder.JdbcConfigBuilder configBuilder)
			throws Exception {
		KeyPair keyPair = SecurityUtils.RSAKeyPair();
		return configBuilder.dialect("JdbcDialect")
				.serverBuilder("localhost", 2270)
				.level(10)
				.confirm()
				.serverBuilder("localhost", 2271)
				.level(20)
				.confirm()
				.serverBuilder("localhost", 2272)
				.level(30)
				.confirm()
				.connectionPool(Boolean.TRUE, 2, 10)
				.jdbcUrl("jdbc:url://testUrl")
				.lowQuery(1000L)
				.timeout(5, 5)
				.x509Auth()
				.x509(CertificateUtils.x509(keyPair.getPublic(), IDUtils.snowflake(), new Date(),
						new Date(DateTimeUtils.expireMonth(2)), "TestCert",
						keyPair.getPrivate(), "SHA256withRSA"))
				.confirm()
				.retry(3, 500L)
				.cacheSize(20)
				.confirm();
	}

	private BrainConfigureBuilder<?> remote(final SchemaConfigBuilder.RemoteConfigBuilder configBuilder) throws Exception {
		KeyPair keyPair = SecurityUtils.RSAKeyPair();
		return configBuilder
				.address("http://localhost")
				.type(RemoteType.Restful)
				.proxyConfig()
				.proxyType(Proxy.Type.HTTP)
				.serverConfig("http://localhost", 1080)
				.authenticator("proxyUser", "proxyPassword")
				.confirm()
				.lowQuery(1000L)
				.timeout(5, 5)
				.x509Auth()
				.x509(CertificateUtils.x509(keyPair.getPublic(), IDUtils.snowflake(), new Date(),
						new Date(DateTimeUtils.expireMonth(2)), "TestCert",
						keyPair.getPrivate(), "SHA256withRSA"))
				.confirm()
				.keepAlive(600)
				.confirm();
	}

	private BrainConfigureBuilder<?> newBuilder(final BrainConfigure configure) {
		return BrainConfigureBuilder.newBuilder(configure)
				.ddlMode(DDLType.SYNCHRONIZE)
				.enableJmxMonitor()
				.enableLazyInit();
	}
}
