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

package org.nervousync.brain.dialects.remote.impl;

import jakarta.xml.ws.BindingProvider;
import org.nervousync.annotations.provider.Provider;
import org.nervousync.brain.annotations.dialect.SchemaDialect;
import org.nervousync.brain.commons.BrainCommons;
import org.nervousync.brain.configs.auth.Authentication;
import org.nervousync.brain.configs.auth.impl.UserAuthentication;
import org.nervousync.brain.configs.secure.TrustStore;
import org.nervousync.brain.dialects.remote.RemoteDialect;
import org.nervousync.brain.exceptions.dialects.DialectException;
import org.nervousync.utils.StringUtils;

import java.util.Properties;

/**
 * <h2 class="en-US">Simple remote database dialect implementation class</h2>
 * <h2 class="zh-CN">简单远程数据库方言实现类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Feb 18, 2019 10:38:52 $
 */
@Provider(name = BrainCommons.DEFAULT_REMOTE_DIALECT_NAME, titleKey = "Simple_Remote_Dialect_Title", descriptionKey = "Simple_Remote_Dialect_Description")
@SchemaDialect(supportJoin = true, types = {})
public final class SimpleRemoteDialectImpl extends RemoteDialect {

	/**
	 * <h3 class="en-US">Constructor method for remote database dialect implementation class</h3>
	 * <h3 class="zh-CN">远程数据库方言实现类的构造方法</h3>
	 *
	 * @throws DialectException <span class="en-US">If the implementation class does not find the org. nervousync. brain. annotations. dialect.SchemaDialect annotation</span>
	 *                          <span class="zh-CN">如果实现类未找到org. nervousync. brain. annotations. dialect.SchemaDialect注解</span>
	 */
	public SimpleRemoteDialectImpl() throws DialectException {
	}

	@Override
	public Properties properties(final TrustStore trustStore, final Authentication authentication) {
		Properties properties = new Properties();
		if (authentication instanceof UserAuthentication) {
			UserAuthentication userAuthentication = (UserAuthentication) authentication;
			if (StringUtils.notBlank(userAuthentication.getUserName())) {
				properties.setProperty(BindingProvider.USERNAME_PROPERTY, userAuthentication.getUserName());
			}
			if (StringUtils.notBlank(userAuthentication.getPassWord())) {
				properties.setProperty(BindingProvider.PASSWORD_PROPERTY, userAuthentication.getPassWord());
			}
		}
		return properties;
	}
}
