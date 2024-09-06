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

package org.nervousync.brain.configs.auth.impl;

import jakarta.xml.bind.annotation.*;
import org.nervousync.annotations.configs.Password;
import org.nervousync.brain.configs.auth.Authentication;
import org.nervousync.brain.enumerations.auth.AuthType;
import org.nervousync.commons.Globals;

/**
 * <h2 class="en-US">Use the authentication information of the X.509 certificate in the certificate store</h2>
 * <h2 class="zh-CN">使用证书库中X.509证书的认证信息</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision : 1.0.0 $ $Date: Apr 10, 2018 15:48:19 $
 */
@XmlType(name = "trust_store_authentication", namespace = "https://nervousync.org/schemas/database")
@XmlRootElement(name = "trust_store_authentication", namespace = "https://nervousync.org/schemas/database")
@XmlAccessorType(XmlAccessType.NONE)
public final class TrustStoreAuthentication extends Authentication {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = -4390434525413289669L;

	/**
	 * <span class="en-US">Trust certificate store path</span>
	 * <span class="zh-CN">信任证书库地址</span>
	 */
	@XmlElement(name = "store_path")
	private String trustStorePath = Globals.DEFAULT_VALUE_STRING;
	/**
	 * <span class="en-US">Trust certificate store password</span>
	 * <span class="zh-CN">信任证书库密码</span>
	 */
	@XmlElement(name = "store_password")
	@Password
	private String trustStorePassword = Globals.DEFAULT_VALUE_STRING;
	/**
	 * <span class="en-US">Certificate name</span>
	 * <span class="zh-CN">证书名称</span>
	 */
	@XmlElement(name = "cert_name")
	private String certificateName = Globals.DEFAULT_VALUE_STRING;

	/**
	 * <h3 class="en-US">Constructor method for X.509 certificate authentication information</h3>
	 * <h3 class="zh-CN">X.509证书认证信息的构造方法</h3>
	 */
	public TrustStoreAuthentication() {
		super(AuthType.TRUST_STORE);
	}

	/**
	 * <h3 class="en-US">Getter method for trust certificate store path</h3>
	 * <h3 class="zh-CN">信任证书库地址的Getter方法</h3>
	 *
	 * @return <span class="en-US">Trust certificate store path</span>
	 * <span class="zh-CN">信任证书库地址</span>
	 */
	public String getTrustStorePath() {
		return this.trustStorePath;
	}

	/**
	 * <h3 class="en-US">Setter method for trust certificate store path</h3>
	 * <h3 class="zh-CN">信任证书库地址的Setter方法</h3>
	 *
	 * @param trustStorePath <span class="en-US">Trust certificate store path</span>
	 *                       <span class="zh-CN">信任证书库地址</span>
	 */
	public void setTrustStorePath(String trustStorePath) {
		this.trustStorePath = trustStorePath;
	}

	/**
	 * <h3 class="en-US">Getter method for trust certificate store password</h3>
	 * <h3 class="zh-CN">信任证书库密码的Getter方法</h3>
	 *
	 * @return <span class="en-US">Trust certificate store password</span>
	 * <span class="zh-CN">信任证书库密码</span>
	 */
	public String getTrustStorePassword() {
		return this.trustStorePassword;
	}

	/**
	 * <h3 class="en-US">Setter method for trust certificate store password</h3>
	 * <h3 class="zh-CN">信任证书库密码的Setter方法</h3>
	 *
	 * @param trustStorePassword <span class="en-US">Trust certificate store password</span>
	 *                           <span class="zh-CN">信任证书库密码</span>
	 */
	public void setTrustStorePassword(String trustStorePassword) {
		this.trustStorePassword = trustStorePassword;
	}

	/**
	 * <h3 class="en-US">Getter method for certificate name</h3>
	 * <h3 class="zh-CN">证书名称的Getter方法</h3>
	 *
	 * @return <span class="en-US">Certificate name</span>
	 * <span class="zh-CN">证书名称</span>
	 */
	public String getCertificateName() {
		return this.certificateName;
	}

	/**
	 * <h3 class="en-US">Setter method for certificate name</h3>
	 * <h3 class="zh-CN">证书名称的Setter方法</h3>
	 *
	 * @param certificateName <span class="en-US">Certificate name</span>
	 *                        <span class="zh-CN">证书名称</span>
	 */
	public void setCertificateName(String certificateName) {
		this.certificateName = certificateName;
	}
}
