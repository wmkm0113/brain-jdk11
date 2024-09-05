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

package org.nervousync.brain.configs.auth;

import jakarta.xml.bind.annotation.*;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.brain.configs.auth.impl.TrustStoreAuthentication;
import org.nervousync.brain.configs.auth.impl.UserAuthentication;
import org.nervousync.brain.configs.auth.impl.X509Authentication;
import org.nervousync.brain.enumerations.auth.AuthType;

/**
 * <h2 class="en-US">Authentication information abstract class</h2>
 * <h2 class="zh-CN">认证信息抽象类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision : 1.0.0 $ $Date: Apr 10, 2018 15:44:07 $
 */
@XmlType(namespace = "https://nervousync.org/schemas/database")
@XmlSeeAlso({TrustStoreAuthentication.class, UserAuthentication.class, X509Authentication.class})
@XmlAccessorType(XmlAccessType.NONE)
public abstract class Authentication extends BeanObject {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = -8821543983761509904L;

	/**
	 * <span class="en-US">Enumeration value of authentication type</span>
	 * <span class="zh-CN">身份认证类型的枚举值</span>
	 */
	@XmlElement(name = "auth_type")
	private final AuthType authType;

	/**
	 * <h3 class="en-US">Constructor method for authentication information abstract class</h3>
	 * <h3 class="zh-CN">认证信息抽象类的构造方法</h3>
	 *
	 * @param authType <span class="en-US">Enumeration value of authentication type</span>
	 *                 <span class="zh-CN">身份认证类型的枚举值</span>
	 */
	protected Authentication(final AuthType authType) {
		this.authType = authType;
	}

	/**
	 * <h3 class="en-US">Getter method for enumeration value of authentication type</h3>
	 * <h3 class="zh-CN">身份认证类型的枚举值的Getter方法</h3>
	 *
	 * @return <span class="en-US">Enumeration value of authentication type</span>
	 * <span class="zh-CN">身份认证类型的枚举值</span>
	 */
	public AuthType getAuthType() {
		return this.authType;
	}
}
