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
import org.nervousync.brain.configs.schema.SchemaConfig;
import org.nervousync.brain.enumerations.remote.RemoteType;
import org.nervousync.proxy.ProxyConfig;

/**
 * <h2 class="en-US">Remote data source configuration information</h2>
 * <h2 class="zh-CN">远端数据源配置信息</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision : 1.0.0 $ $Date: Jul 12, 2020 16:42:35 $
 */
@XmlType(name = "remote_schema", namespace = "https://nervousync.org/schemas/database")
@XmlRootElement(name = "remote_schema", namespace = "https://nervousync.org/schemas/database")
@XmlAccessorType(XmlAccessType.NONE)
public final class RemoteSchemaConfig extends SchemaConfig {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = 9008779344156016934L;

	/**
	 * <span class="en-US">Remote type</span>
	 * <span class="zh-CN">远程类型</span>
	 */
	@XmlElement(name = "remote_type")
	private RemoteType remoteType;
	/**
	 * <span class="en-US">Remote address</span>
	 * <span class="zh-CN">远端地址</span>
	 */
	@XmlElement(name = "remote_address")
	private String remoteAddress;
	/**
	 * <span class="en-US">Proxy configure information</span>
	 * <span class="zh-CN">代理服务器配置信息</span>
	 */
	@XmlElement(name = "proxy_config", namespace = "https://nervousync.org/schemas/proxy")
	private ProxyConfig proxyConfig;
	/**
	 * <span class="en-US">Keep-alive timeout (Unit: seconds)</span>
	 * <span class="zh-CN">长连接超时时间（单位：秒）</span>
	 */
	@XmlElement(name = "keep_alive")
	private int keepAlive = 1200;

	/**
	 * <h3 class="en-US">Constructor method for remote data source configuration information</h3>
	 * <h3 class="zh-CN">远端数据源配置信息的构造方法</h3>
	 */
	public RemoteSchemaConfig() {
	}

	/**
	 * <h3 class="en-US">Getter method for remote type</h3>
	 * <h3 class="zh-CN">远程类型的Getter方法</h3>
	 *
	 * @return <span class="en-US">Remote type</span>
	 * <span class="zh-CN">远程类型</span>
	 */
	public RemoteType getRemoteType() {
		return this.remoteType;
	}

	/**
	 * <h3 class="en-US">Setter method for remote type</h3>
	 * <h3 class="zh-CN">远程类型的Setter方法</h3>
	 *
	 * @param remoteType <span class="en-US">Remote type</span>
	 *                   <span class="zh-CN">远程类型</span>
	 */
	public void setRemoteType(final RemoteType remoteType) {
		this.remoteType = remoteType;
	}

	/**
	 * <h3 class="en-US">Getter method for remote address</h3>
	 * <h3 class="zh-CN">远端地址的Getter方法</h3>
	 *
	 * @return <span class="en-US">Remote address</span>
	 * <span class="zh-CN">远端地址</span>
	 */
	public String getRemoteAddress() {
		return this.remoteAddress;
	}

	/**
	 * <h3 class="en-US">Setter method for remote address</h3>
	 * <h3 class="zh-CN">远端地址的Setter方法</h3>
	 *
	 * @param remoteAddress <span class="en-US">Remote address</span>
	 *                      <span class="zh-CN">远端地址</span>
	 */
	public void setRemoteAddress(final String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	/**
	 * <h3 class="en-US">Getter method for proxy configure information</h3>
	 * <h3 class="zh-CN">代理服务器配置信息的Getter方法</h3>
	 *
	 * @return <span class="en-US">Proxy configure information</span>
	 * <span class="zh-CN">代理服务器配置信息</span>
	 */
	public ProxyConfig getProxyConfig() {
		return this.proxyConfig;
	}

	/**
	 * <h3 class="en-US">Setter method for proxy configure information</h3>
	 * <h3 class="zh-CN">代理服务器配置信息的Setter方法</h3>
	 *
	 * @param proxyConfig <span class="en-US">Proxy configure information</span>
	 *                    <span class="zh-CN">代理服务器配置信息</span>
	 */
	public void setProxyConfig(final ProxyConfig proxyConfig) {
		this.proxyConfig = proxyConfig;
	}

	/**
	 * <h3 class="en-US">Getter method for Keep-alive timeout</h3>
	 * <h3 class="zh-CN">长连接超时时间的Getter方法</h3>
	 *
	 * @return <span class="en-US">Keep-alive timeout (Unit: seconds)</span>
	 * <span class="zh-CN">长连接超时时间（单位：秒）</span>
	 */
	public int getKeepAlive() {
		return this.keepAlive;
	}

	/**
	 * <h3 class="en-US">Setter method for Keep-alive timeout</h3>
	 * <h3 class="zh-CN">长连接超时时间的Setter方法</h3>
	 *
	 * @param keepAlive <span class="en-US">Keep-alive timeout (Unit: seconds)</span>
	 *                  <span class="zh-CN">长连接超时时间（单位：秒）</span>
	 */
	public void setKeepAlive(final int keepAlive) {
		this.keepAlive = keepAlive;
	}
}
