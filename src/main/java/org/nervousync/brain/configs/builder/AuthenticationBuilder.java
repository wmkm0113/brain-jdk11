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

package org.nervousync.brain.configs.builder;

import jakarta.annotation.Nonnull;
import org.nervousync.brain.configs.auth.impl.TrustStoreAuthentication;
import org.nervousync.brain.configs.auth.impl.UserAuthentication;
import org.nervousync.builder.AbstractBuilder;
import org.nervousync.builder.ParentBuilder;
import org.nervousync.exceptions.builder.BuilderException;
import org.nervousync.utils.StringUtils;

/**
 * <h2 class="en-US">Abstract class of the authentication information</h2>
 * <h2 class="zh-CN">认证信息构建器的抽象类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision : 1.0.0 $ $Date: Apr 10, 2018 15:48:19 $
 */
public abstract class AuthenticationBuilder<T> extends AbstractBuilder<T> {

	/**
	 * <h3 class="en-US">Protected constructor for AbstractBuilder</h3>
	 * <h3 class="zh-CN">AbstractBuilder的构造函数</h3>
	 *
	 * @param parentBuilder <span class="en-US">Generics Type instance</span>
	 *                      <span class="zh-CN">泛型类实例对象</span>
	 */
	protected AuthenticationBuilder(final ParentBuilder parentBuilder) {
		super(parentBuilder);
	}

	/**
	 * <h2 class="en-US">Builder implementation class of use the authentication information of the X.509 certificate in the certificate store</h2>
	 * <h2 class="zh-CN">使用证书库中X.509证书的认证信息的构建器</h2>
	 *
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision : 1.0.0 $ $Date: Apr 10, 2018 15:48:19 $
	 */
	public static final class TrustStoreAuthenticationBuilder extends AuthenticationBuilder<TrustStoreAuthentication> {

		/**
		 * <span class="en-US">Use the authentication information of the X.509 certificate in the certificate store</span>
		 * <span class="zh-CN">使用证书库中X.509证书的认证信息</span>
		 */
		private final TrustStoreAuthentication trustStoreAuthentication;

		/**
		 * <h3 class="en-US">Constructor method for builder implementation class of use the authentication information of the X.509 certificate in the certificate store</h3>
		 * <h3 class="zh-CN">使用证书库中X.509证书的认证信息构建器的构造方法</h3>
		 *
		 * @param parentBuilder            <span class="en-US">Parent builder instance object</span>
		 *                                 <span class="zh-CN">父构建器实例对象</span>
		 * @param trustStoreAuthentication <span class="en-US">Use the authentication information of the X.509 certificate in the certificate store</span>
		 *                                 <span class="zh-CN">使用证书库中X.509证书的认证信息</span>
		 */
		TrustStoreAuthenticationBuilder(final SchemaConfigBuilder<?> parentBuilder,
		                                @Nonnull final TrustStoreAuthentication trustStoreAuthentication) {
			super(parentBuilder);
			this.trustStoreAuthentication = trustStoreAuthentication;
		}

		/**
		 * <h3 class="en-US">Configure trust store information</h3>
		 * <h3 class="zh-CN">设置信任证书库</h3>
		 *
		 * @param trustStorePath     <span class="en-US">Trust certificate store path</span>
		 *                           <span class="zh-CN">信任证书库地址</span>
		 * @param trustStorePassword <span class="en-US">Trust certificate store password</span>
		 *                           <span class="zh-CN">信任证书库密码</span>
		 * @return <span class="en-US">The current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public TrustStoreAuthenticationBuilder trustStore(final String trustStorePath, final String trustStorePassword) {
			this.trustStoreAuthentication.setTrustStorePath(trustStorePath);
			this.trustStoreAuthentication.setTrustStorePassword(trustStorePassword);
			return this;
		}

		/**
		 * <h3 class="en-US">Configure certificate name</h3>
		 * <h3 class="zh-CN">设置使用证书名称</h3>
		 *
		 * @param certificateName <span class="en-US">Certificate name</span>
		 *                        <span class="zh-CN">证书名称</span>
		 * @return <span class="en-US">The current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public TrustStoreAuthenticationBuilder certificate(final String certificateName) {
			this.trustStoreAuthentication.setCertificateName(certificateName);
			return this;
		}

		@Override
		public TrustStoreAuthentication confirm() throws BuilderException {
			return this.trustStoreAuthentication;
		}
	}

	/**
	 * <h2 class="en-US">Builder implementation class of basic authentication information</h2>
	 * <h2 class="zh-CN">基本身份认证信息的构建器</h2>
	 *
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision : 1.0.0 $ $Date: Apr 10, 2018 15:48:19 $
	 */
	public static final class UserAuthenticationBuilder extends AuthenticationBuilder<UserAuthentication> {

		/**
		 * <span class="en-US">Basic authentication information</span>
		 * <span class="zh-CN">基本身份认证信息</span>
		 */
		private final UserAuthentication userAuthentication;

		/**
		 * <h3 class="en-US">Constructor method for builder implementation class of basic authentication information</h3>
		 * <h3 class="zh-CN">基本身份认证信息构建器的构造方法</h3>
		 *
		 * @param parentBuilder      <span class="en-US">Parent builder instance object</span>
		 *                           <span class="zh-CN">父构建器实例对象</span>
		 * @param userAuthentication <span class="en-US">Basic authentication information</span>
		 *                           <span class="zh-CN">基本身份认证信息</span>
		 */
		UserAuthenticationBuilder(final SchemaConfigBuilder<?> parentBuilder,
		                          @Nonnull final UserAuthentication userAuthentication) {
			super(parentBuilder);
			this.userAuthentication = userAuthentication;
		}

		/**
		 * <h3 class="en-US">Set identity authentication information</h3>
		 * <h3 class="zh-CN">设置身份认证信息</h3>
		 *
		 * @param userName <span class="en-US">Username</span>
		 *                 <span class="zh-CN">用户名</span>
		 * @param passWord <span class="en-US">Password</span>
		 *                 <span class="zh-CN">密码</span>
		 * @return <span class="en-US">The current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public UserAuthenticationBuilder authenticate(final String userName, final String passWord) {
			if (StringUtils.notBlank(userName)) {
				this.userAuthentication.setUserName(userName);
				this.userAuthentication.setPassWord(passWord);
			}
			return this;
		}

		@Override
		public UserAuthentication confirm() throws BuilderException {
			return this.userAuthentication;
		}
	}
}
