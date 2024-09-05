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
import org.nervousync.brain.configs.auth.Authentication;
import org.nervousync.brain.enumerations.auth.AuthType;

/**
 * <h2 class="en-US">X.509 certificate authentication information</h2>
 * <h2 class="zh-CN">X.509证书认证信息</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision : 1.0.0 $ $Date: Apr 10, 2018 15:48:19 $
 */
@XmlType(name = "x509_authentication", namespace = "https://nervousync.org/schemas/database")
@XmlRootElement(name = "x509_authentication", namespace = "https://nervousync.org/schemas/database")
@XmlAccessorType(XmlAccessType.NONE)
public final class X509Authentication extends Authentication {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = -7123140631151784011L;

	/**
	 * <span class="en-US">X.509 certificate information (encoded using Base64)</span>
	 * <span class="zh-CN">X.509证书信息（使用Base64编码）</span>
	 */
	@XmlElement(name = "cert_data")
	private String certData;

	/**
	 * <h3 class="en-US">Constructor method for X.509 certificate authentication information</h3>
	 * <h3 class="zh-CN">X.509证书认证信息的构造方法</h3>
	 */
	public X509Authentication() {
		super(AuthType.CERTIFICATE);
	}

	/**
	 * <h3 class="en-US">Getter method for X.509 certificate information</h3>
	 * <h3 class="zh-CN">X.509证书信息的Getter方法</h3>
	 *
	 * @return
	 * <span class="en-US">X.509 certificate information (encoded using Base64)</span>
	 * <span class="zh-CN">X.509证书信息（使用Base64编码）</span>
	 */
	public String getCertData() {
		return this.certData;
	}

	/**
	 * <h3 class="en-US">Setter method for X.509 certificate information</h3>
	 * <h3 class="zh-CN">X.509证书信息的Setter方法</h3>
	 *
	 * @param certData
	 * <span class="en-US">X.509 certificate information (encoded using Base64)</span>
	 * <span class="zh-CN">X.509证书信息（使用Base64编码）</span>
	 */
	public void setCertData(String certData) {
		this.certData = certData;
	}
}
