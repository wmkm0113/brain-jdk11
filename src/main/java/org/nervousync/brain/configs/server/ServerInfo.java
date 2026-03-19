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

package org.nervousync.brain.configs.server;

import jakarta.xml.bind.annotation.*;
import org.nervousync.annotations.beans.OutputConfig;
import org.nervousync.brain.configs.auth.Authentication;
import org.nervousync.brain.configs.auth.impl.TokenAuthentication;
import org.nervousync.brain.configs.auth.impl.TrustStoreAuthentication;
import org.nervousync.brain.configs.auth.impl.UserAuthentication;
import org.nervousync.brain.configs.auth.impl.X509Authentication;
import org.nervousync.brain.configs.secure.TrustStore;
import org.nervousync.commons.Globals;
import org.nervousync.utils.core.ObjectUtils;
import org.nervousync.utils.core.StringUtils;

import java.io.Serializable;

/**
 * <h2 class="en-US">Server configuration information</h2>
 * <h2 class="zh-CN">服务器配置信息</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Dec 12, 2020 09:27:39 $
 */
@OutputConfig
@XmlType(name = "server_info", namespace = "https://nervousync.org/schemas/brain")
@XmlRootElement(name = "server_info", namespace = "https://nervousync.org/schemas/brain")
@XmlAccessorType(XmlAccessType.NONE)
public final class ServerInfo implements Serializable {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = -5393915530015766873L;

	/**
	 * <span class="en-US">Server name</span>
	 * <span class="zh-CN">服务器名称</span>
	 */
	@XmlElement(name = "server_name")
	private String serverName = Globals.DEFAULT_VALUE_STRING;
	/**
	 * <span class="en-US">Server address</span>
	 * <span class="zh-CN">服务器地址</span>
	 */
	@XmlElement(name = "server_address")
	private String serverAddress = Globals.DEFAULT_VALUE_STRING;
	/**
	 * <span class="en-US">Server port number</span>
	 * <span class="zh-CN">服务器端口号</span>
	 */
	@XmlElement(name = "server_port")
	private int serverPort = Globals.DEFAULT_VALUE_INT;
	/**
	 * <span class="en-US">Server level</span>
	 * <span class="zh-CN">服务器等级</span>
	 */
	@XmlElement(name = "server_level")
	private int serverLevel = Globals.DEFAULT_VALUE_INT;
	/**
	 * <span class="en-US">Last modified timestamp</span>
	 * <span class="zh-CN">最后修改时间戳</span>
	 */
	@XmlElement(name = "last_modified")
	private long lastModified = Globals.DEFAULT_VALUE_LONG;
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
			@XmlElementRef(name = "trust_store_authentication", type = TrustStoreAuthentication.class),
			@XmlElementRef(name = "token_authentication", type = TokenAuthentication.class)
	})
	private Authentication authentication;

	/**
	 * <h3 class="en-US">Constructor method for server configuration information</h3>
	 * <h3 class="zh-CN">服务器配置信息的构造方法</h3>
	 */
	public ServerInfo() {
	}

	/**
	 * <h3 class="en-US">Getter method for server name</h3>
	 * <h3 class="zh-CN">服务器名称的Getter方法</h3>
	 *
	 * @return <span class="en-US">Server name</span>
	 * <span class="zh-CN">服务器名称</span>
	 */
	public String getServerName() {
		return this.serverName;
	}

	/**
	 * <h3 class="en-US">Setter method for server name</h3>
	 * <h3 class="zh-CN">服务器名称的Setter方法</h3>
	 *
	 * @param serverName <span class="en-US">Server name</span>
	 *                   <span class="zh-CN">服务器名称</span>
	 */
	public void setServerName(final String serverName) {
		this.serverName = serverName;
	}

	/**
	 * <h3 class="en-US">Getter method for server address</h3>
	 * <h3 class="zh-CN">服务器地址的Getter方法</h3>
	 *
	 * @return <span class="en-US">Server address</span>
	 * <span class="zh-CN">服务器地址</span>
	 */
	public String getServerAddress() {
		return this.serverAddress;
	}

	/**
	 * <h3 class="en-US">Setter method for server address</h3>
	 * <h3 class="zh-CN">服务器地址的Setter方法</h3>
	 *
	 * @param serverAddress <span class="en-US">Server address</span>
	 *                      <span class="zh-CN">服务器地址</span>
	 */
	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	/**
	 * <h3 class="en-US">Getter method for server port number</h3>
	 * <h3 class="zh-CN">服务器端口号的Getter方法</h3>
	 *
	 * @return <span class="en-US">Server port number</span>
	 * <span class="zh-CN">服务器端口号</span>
	 */
	public int getServerPort() {
		return this.serverPort;
	}

	/**
	 * <h3 class="en-US">Setter method for server port number</h3>
	 * <h3 class="zh-CN">服务器端口号的Setter方法</h3>
	 *
	 * @param serverPort <span class="en-US">Server port number</span>
	 *                   <span class="zh-CN">服务器端口号</span>
	 */
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	/**
	 * <h3 class="en-US">Getter method for server level</h3>
	 * <h3 class="zh-CN">服务器等级的Getter方法</h3>
	 *
	 * @return <span class="en-US">Server level</span>
	 * <span class="zh-CN">服务器等级</span>
	 */
	public int getServerLevel() {
		return this.serverLevel;
	}

	/**
	 * <h3 class="en-US">Setter method for server level</h3>
	 * <h3 class="zh-CN">服务器等级的Setter方法</h3>
	 *
	 * @param serverLevel <span class="en-US">Server level</span>
	 *                    <span class="zh-CN">服务器等级</span>
	 */
	public void setServerLevel(int serverLevel) {
		this.serverLevel = serverLevel;
	}

	/**
	 * <h3 class="en-US">Getter method for the last modified timestamp</h3>
	 * <h3 class="zh-CN">最后修改时间戳的Getter方法</h3>
	 *
	 * @return <span class="en-US">Last modified timestamp</span>
	 * <span class="zh-CN">最后修改时间戳</span>
	 */
	public long getLastModified() {
		return this.lastModified;
	}

	/**
	 * <h3 class="en-US">Setter method for the last modified timestamp</h3>
	 * <h3 class="zh-CN">最后修改时间戳的Setter方法</h3>
	 *
	 * @param lastModified <span class="en-US">Last modified timestamp</span>
	 *                     <span class="zh-CN">最后修改时间戳</span>
	 */
	public void setLastModified(final long lastModified) {
		this.lastModified = lastModified;
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
	 * <h3 class="en-US">Convert server information to string</h3>
	 * <h3 class="zh-CN">转换服务器信息为字符串</h3>
	 *
	 * @return <span class="en-US">Converted string</span>
	 * <span class="zh-CN">转换后的字符串</span>
	 */
	public String info() {
		StringBuilder stringBuilder = new StringBuilder();
		if (StringUtils.isIPv6Address(this.serverAddress)) {
			stringBuilder.append("[").append(this.getServerAddress()).append("]");
		} else {
			stringBuilder.append(this.getServerAddress());
		}
		if (this.serverPort > Globals.INITIALIZE_INT_VALUE) {
			stringBuilder.append(":").append(this.getServerPort());
		}
		return stringBuilder.toString();
	}

	/**
	 * <h3 class="en-US">Checks whether the given database server information is consistent with the current configuration</h3>
	 * <h3 class="zh-CN">检查给定的数据库服务器信息是否与当前配置一致</h3>
	 *
	 * @param serverAddress <span class="en-US">Server address</span>
	 *                      <span class="zh-CN">服务器地址</span>
	 * @param serverPort    <span class="en-US">Server port number</span>
	 *                      <span class="zh-CN">服务器端口号</span>
	 * @return <span class="en-US">Check result</span>
	 * <span class="zh-CN">检查结果</span>
	 */
	public boolean match(final String serverAddress, final int serverPort) {
		return ObjectUtils.nullSafeEquals(this.serverAddress, serverAddress)
				&& ObjectUtils.nullSafeEquals(this.serverPort, serverPort);
	}
}
