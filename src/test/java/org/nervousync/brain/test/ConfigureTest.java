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
import org.nervousync.brain.configs.Configure;
import org.nervousync.brain.configs.builder.ConfigureBuilder;
import org.nervousync.brain.configs.builder.SchemaConfigBuilder;
import org.nervousync.brain.enumerations.ddl.DDLType;
import org.nervousync.brain.enumerations.remote.RemoteType;
import org.nervousync.utils.*;

import java.net.Proxy;
import java.security.KeyPair;
import java.util.Date;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public final class ConfigureTest {

	static {
		LoggerUtils.initLoggerConfigure(Level.DEBUG);
	}

	@Test
	@Order(0)
	public void storageConfig() throws Exception {
		Configure configure = this.newBuilder(null)
				.storageConfigBuilder()
				.basePath("BasePath")
				.config(5, 30 * 24 * 60 * 60 * 1000L)
				.provider("StorageProvider")
				.confirmParent(ConfigureBuilder.class)
				.confirm();
		System.out.println(configure.toXML(Boolean.TRUE));
	}

	@Test
	@Order(10)
	public void distributeConfig() throws Exception {
		Configure configure =
				this.distribute(this.newBuilder(null).distributeConfigBuilder("Distribute"))
						.confirm();
		System.out.println(configure.toXML(Boolean.TRUE));
		configure = this.newBuilder(configure)
				.distributeConfigBuilder("Distribute")
				.removeServer("localhost", 2271)
				.serverLevel("localhost", 2270, 40)
				.trustStoreAuthentication()
				.trustStore("TrustPath", "TrustPassword")
				.certificate("CertName")
				.confirmParent(SchemaConfigBuilder.DistributeConfigBuilder.class)
				.confirmParent(ConfigureBuilder.class)
				.confirm();
		System.out.println(configure.toXML(Boolean.TRUE));
	}

	@Test
	@Order(20)
	public void jdbcConfig() throws Exception {
		Configure configure =
				this.jdbc(this.newBuilder(null).jdbcConfigBuilder("Jdbc")).confirm();
		System.out.println(configure.toXML(Boolean.TRUE));
		configure = this.newBuilder(configure)
				.jdbcConfigBuilder("Jdbc")
				.removeServer("localhost", 2271)
				.serverLevel("localhost", 2270, 40)
				.trustStoreAuthentication()
				.trustStore("TrustPath", "TrustPassword")
				.certificate("CertName")
				.confirmParent(SchemaConfigBuilder.JdbcConfigBuilder.class)
				.testConnection(Boolean.TRUE, Boolean.TRUE)
				.confirmParent(ConfigureBuilder.class)
				.confirm();
		System.out.println(configure.toXML(Boolean.TRUE));
	}

	@Test
	@Order(30)
	public void remoteConfig() throws Exception {
		Configure configure =
				this.remote(this.newBuilder(null).remoteConfigBuilder("Remote")).confirm();
		System.out.println(configure.toXML(Boolean.TRUE));
		configure = this.newBuilder(configure)
				.disableLazyInit()
				.disableJmxMonitor()
				.distributeConfigBuilder("Distribute")
				.removeServer("localhost", 2271)
				.serverLevel("localhost", 2270, 40)
				.trustStoreAuthentication()
				.trustStore("TrustPath", "TrustPassword")
				.certificate("CertName")
				.confirmParent(SchemaConfigBuilder.DistributeConfigBuilder.class)
				.confirmParent(ConfigureBuilder.class)
				.jdbcConfigBuilder("Jdbc")
				.removeServer("localhost", 2271)
				.serverLevel("localhost", 2270, 40)
				.trustStoreAuthentication()
				.trustStore("TrustPath", "TrustPassword")
				.certificate("CertName")
				.confirmParent(SchemaConfigBuilder.JdbcConfigBuilder.class)
				.testConnection(Boolean.TRUE, Boolean.TRUE)
				.confirmParent(ConfigureBuilder.class)
				.remoteConfigBuilder("Remote")
				.type(RemoteType.SOAP)
				.confirmParent(ConfigureBuilder.class)
				.confirm();
		System.out.println(configure.toXML(Boolean.TRUE));
	}

	@Test
	@Order(40)
	public void mixedConfig() throws Exception {
		ConfigureBuilder configureBuilder = this.newBuilder(null);
		configureBuilder = this.distribute(configureBuilder.distributeConfigBuilder("Distribute"));
		configureBuilder = this.jdbc(configureBuilder.jdbcConfigBuilder("Jdbc"));
		configureBuilder = this.remote(configureBuilder.remoteConfigBuilder("Remote"));
		Configure configure = configureBuilder.defaultSchema("Jdbc").confirm();
		String xml = configure.toXML(Boolean.TRUE);
		configure = StringUtils.stringToObject(xml, Configure.class, "https://nervousync.org/schemas/database");
		System.out.println(configure.toXML(Boolean.TRUE));
		configure = this.newBuilder(configure)
				.remoteConfigBuilder("Remote")
				.type(RemoteType.SOAP)
				.confirmParent(ConfigureBuilder.class)
				.defaultSchema("Remote")
				.confirm();
		System.out.println(configure.toXML(Boolean.TRUE));
	}

	private ConfigureBuilder distribute(final SchemaConfigBuilder.DistributeConfigBuilder configBuilder)
			throws Exception {
		return configBuilder.dialect("DistributeDialect")
				.addServer("datacenter1", "localhost", 2270, 10)
				.addServer("datacenter2", "localhost", 2271, 20)
				.addServer("datacenter3", "localhost", 2272, 30)
				.addServer("localhost", 2273, 0)
				.connectionPool(Boolean.TRUE, 2, 10)
				.databaseName("DatabaseName")
				.useSsl(Boolean.TRUE)
				.lowQuery(1000)
				.timeout(5, 5)
				.trustStore("StorePath", "changeit")
				.userAuthentication()
				.authenticate("username", "password")
				.confirmParent(SchemaConfigBuilder.DistributeConfigBuilder.class)
				.confirmParent(ConfigureBuilder.class);
	}

	private ConfigureBuilder jdbc(final SchemaConfigBuilder.JdbcConfigBuilder configBuilder)
			throws Exception {
		KeyPair keyPair = SecurityUtils.RSAKeyPair();
		return configBuilder.dialect("JdbcDialect")
				.serverArray(Boolean.TRUE)
				.addServer("localhost", 2270, 10)
				.addServer("localhost", 2271, 20)
				.addServer("localhost", 2272, 30)
				.connectionPool(Boolean.TRUE, 2, 10)
				.jdbcUrl("jdbc:url://testUrl")
				.lowQuery(1000)
				.timeout(5, 5)
				.trustStore("StorePath", "changeit")
				.x509Authenticator(CertificateUtils.x509(keyPair.getPublic(), IDUtils.snowflake(), new Date(),
						new Date(DateTimeUtils.expireMonth(2)), "TestCert",
						keyPair.getPrivate(), "SHA256withRSA"))
				.retry(3, 500L)
				.confirmParent(ConfigureBuilder.class);
	}

	private ConfigureBuilder remote(final SchemaConfigBuilder.RemoteConfigBuilder configBuilder) throws Exception {
		KeyPair keyPair = SecurityUtils.RSAKeyPair();
		return configBuilder
				.address("http://localhost")
				.type(RemoteType.Restful)
				.proxyConfig()
				.proxyType(Proxy.Type.HTTP)
				.serverConfig("http://localhost", 1080)
				.authenticator("proxyUser", "proxyPassword")
				.confirmParent(SchemaConfigBuilder.RemoteConfigBuilder.class)
				.lowQuery(1000)
				.timeout(5, 5)
				.trustStore("StorePath", "changeit")
				.x509Authenticator(CertificateUtils.x509(keyPair.getPublic(), IDUtils.snowflake(), new Date(),
						new Date(DateTimeUtils.expireMonth(2)), "TestCert",
						keyPair.getPrivate(), "SHA256withRSA"))
				.keepAlive(600)
				.confirmParent(ConfigureBuilder.class);
	}

	private ConfigureBuilder newBuilder(final Configure configure) {
		ConfigureBuilder configureBuilder =
				(configure == null) ? ConfigureBuilder.newBuilder() : ConfigureBuilder.newBuilder(configure);
		return configureBuilder.configDDL(DDLType.SYNCHRONIZE)
				.enableJmxMonitor()
				.enableLazyInit();
	}
}
