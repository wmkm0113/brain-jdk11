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

package org.nervousync.brain.configs.secure;

import jakarta.xml.bind.annotation.*;
import org.nervousync.annotations.configs.Password;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.commons.Globals;

/**
 * <h2 class="en-US">Trust certificate store configuration information</h2>
 * <h2 class="zh-CN">信任证书库配置信息</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision : 1.0.0 $ $Date: Jul 12, 2020 16:22:41 $
 */
@XmlType(name = "trust_store", namespace = "https://nervousync.org/schemas/database")
@XmlRootElement(name = "trust_store", namespace = "https://nervousync.org/schemas/database")
@XmlAccessorType(XmlAccessType.NONE)
public final class TrustStore extends BeanObject {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = -4499806650339880635L;

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
	 * <h3 class="en-US">Constructor method for trust certificate store configuration information</h3>
	 * <h3 class="zh-CN">信任证书库配置信息的构造方法</h3>
	 */
	public TrustStore() {
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
}
