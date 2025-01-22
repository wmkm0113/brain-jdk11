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

@XmlType(name = "token_authentication", namespace = "https://nervousync.org/schemas/brain")
@XmlRootElement(name = "token_authentication", namespace = "https://nervousync.org/schemas/brain")
@XmlAccessorType(XmlAccessType.NONE)
public final class TokenAuthentication extends Authentication {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = -6528437316806953933L;

	/**
	 * <span class="en-US">Identify Key ID</span>
	 * <span class="zh-CN">识别ID</span>
	 */
	@XmlElement(name = "key_id")
	private String keyId;
	/**
	 * <span class="en-US">Identify secret key</span>
	 * <span class="zh-CN">识别密钥</span>
	 */
	@Password
	@XmlElement(name = "secret_key")
	private String secretKey;
	/**
	 * <span class="en-US">Session token</span>
	 * <span class="zh-CN">会话Token</span>
	 */
	@XmlElement(name = "session_token")
	private String sessionToken;
	/**
	 * <h3 class="en-US">Constructor method for token authentication information implementation class</h3>
	 * <h3 class="zh-CN">Token认证信息实现类的构造方法</h3>
	 */
	public TokenAuthentication() {
		super(AuthType.TOKEN);
	}

	/**
	 * <h3 class="en-US">Getter method for identify Key ID</h3>
	 * <h3 class="zh-CN">识别ID的Getter方法</h3>
	 *
	 * @return
	 * <span class="en-US">Identify Key ID</span>
	 * <span class="zh-CN">识别ID</span>
	 */
	public String getKeyId() {
		return this.keyId;
	}

	/**
	 * <h3 class="en-US">Setter method for identify Key ID</h3>
	 * <h3 class="zh-CN">识别ID的Setter方法</h3>
	 *
	 * @param keyId
	 * <span class="en-US">Identify Key ID</span>
	 * <span class="zh-CN">识别ID</span>
	 */
	public void setKeyId(final String keyId) {
		this.keyId = keyId;
	}

	/**
	 * <h3 class="en-US">Getter method for identify secret key</h3>
	 * <h3 class="zh-CN">识别密钥的Getter方法</h3>
	 *
	 * @return
	 * <span class="en-US">Identify secret key</span>
	 * <span class="zh-CN">识别密钥</span>
	 */
	public String getSecretKey() {
		return this.secretKey;
	}

	/**
	 * <h3 class="en-US">Setter method for identify secret key</h3>
	 * <h3 class="zh-CN">识别密钥的Setter方法</h3>
	 *
	 * @param secretKey
	 * <span class="en-US">Identify secret key</span>
	 * <span class="zh-CN">识别密钥</span>
	 */
	public void setSecretKey(final String secretKey) {
		this.secretKey = secretKey;
	}

	/**
	 * <h3 class="en-US">Getter method for session token</h3>
	 * <h3 class="zh-CN">会话Token的Getter方法</h3>
	 *
	 * @return
	 * <span class="en-US">Session token</span>
	 * <span class="zh-CN">会话Token</span>
	 */
	public String getSessionToken() {
		return this.sessionToken;
	}

	/**
	 * <h3 class="en-US">Setter method for session token</h3>
	 * <h3 class="zh-CN">会话Token的Setter方法</h3>
	 *
	 * @param sessionToken
	 * <span class="en-US">Session token</span>
	 * <span class="zh-CN">会话Token</span>
	 */
	public void setSessionToken(final String sessionToken) {
		this.sessionToken = sessionToken;
	}
}
