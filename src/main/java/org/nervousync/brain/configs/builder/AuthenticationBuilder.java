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
import org.nervousync.brain.configs.auth.Authentication;
import org.nervousync.brain.configs.auth.impl.TrustStoreAuthentication;
import org.nervousync.brain.configs.auth.impl.UserAuthentication;
import org.nervousync.brain.configs.auth.impl.X509Authentication;
import org.nervousync.builder.AbstractBuilder;
import org.nervousync.builder.ParentBuilder;
import org.nervousync.utils.DateTimeUtils;
import org.nervousync.utils.ObjectUtils;
import org.nervousync.utils.StringUtils;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

/**
 * <h2 class="en-US">Abstract class of the authentication information</h2>
 * <h2 class="zh-CN">认证信息构建器的抽象类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision : 1.0.0 $ $Date: Apr 10, 2018 15:48:19 $
 */
public abstract class AuthenticationBuilder<T extends Authentication> extends AbstractBuilder<T> {

	/**
	 * <span class="en-US">The authentication information</span>
	 * <span class="zh-CN">认证信息</span>
	 */
	protected final T authentication;
	/**
	 * <h2 class="en-US">Configure information modified flag</h2>
	 * <h2 class="zh-CN">配置信息修改标记</h2>
	 */
	protected boolean modified = Boolean.FALSE;

	/**
	 * <h3 class="en-US">Protected constructor for AbstractBuilder</h3>
	 * <h3 class="zh-CN">AbstractBuilder的构造函数</h3>
	 *
	 * @param parentBuilder  <span class="en-US">Generics Type instance</span>
	 *                       <span class="zh-CN">泛型类实例对象</span>
	 * @param authentication <span class="en-US">The authentication information</span>
	 *                       <span class="zh-CN">认证信息</span>
	 */
	protected AuthenticationBuilder(final ParentBuilder parentBuilder, final T authentication) {
		super(parentBuilder);
		this.authentication = authentication;
	}

	@Override
	public final T confirm() {
		if (this.modified) {
			this.authentication.setLastModified(DateTimeUtils.currentUTCTimeMillis());
		}
		return this.authentication;
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
		 * <h3 class="en-US">Constructor method for builder implementation class of use the authentication information of the X.509 certificate in the certificate store</h3>
		 * <h3 class="zh-CN">使用证书库中X.509证书的认证信息构建器的构造方法</h3>
		 *
		 * @param parentBuilder  <span class="en-US">Parent builder instance object</span>
		 *                       <span class="zh-CN">父构建器实例对象</span>
		 * @param authentication <span class="en-US">Use the authentication information of the X.509 certificate in the certificate store</span>
		 *                       <span class="zh-CN">使用证书库中X.509证书的认证信息</span>
		 */
		TrustStoreAuthenticationBuilder(final SchemaConfigBuilder<?> parentBuilder,
		                                @Nonnull final TrustStoreAuthentication authentication) {
			super(parentBuilder, authentication);
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
			if (StringUtils.notBlank(trustStorePath)
					&& !ObjectUtils.nullSafeEquals(this.authentication.getTrustStorePath(), trustStorePath)) {
				this.authentication.setTrustStorePath(trustStorePath);
				this.modified = Boolean.TRUE;
			}
			if (StringUtils.notBlank(trustStorePassword)
					&& !ObjectUtils.nullSafeEquals(this.authentication.getTrustStorePassword(), trustStorePassword)) {
				this.authentication.setTrustStorePassword(trustStorePassword);
				this.modified = Boolean.TRUE;
			}
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
			if (StringUtils.isEmpty(certificateName)
					|| ObjectUtils.nullSafeEquals(this.authentication.getCertificateName(), certificateName)) {
				return this;
			}
			this.authentication.setCertificateName(certificateName);
			this.modified = Boolean.TRUE;
			return this;
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
		 * <h3 class="en-US">Constructor method for builder implementation class of basic authentication information</h3>
		 * <h3 class="zh-CN">基本身份认证信息构建器的构造方法</h3>
		 *
		 * @param parentBuilder  <span class="en-US">Parent builder instance object</span>
		 *                       <span class="zh-CN">父构建器实例对象</span>
		 * @param authentication <span class="en-US">Basic authentication information</span>
		 *                       <span class="zh-CN">基本身份认证信息</span>
		 */
		UserAuthenticationBuilder(final SchemaConfigBuilder<?> parentBuilder,
		                          @Nonnull final UserAuthentication authentication) {
			super(parentBuilder, authentication);
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
			if (StringUtils.notBlank(userName)
					&& !ObjectUtils.nullSafeEquals(userName, this.authentication.getUserName())) {
				this.authentication.setUserName(userName);
				this.modified = Boolean.TRUE;
			}
			if (StringUtils.notBlank(passWord)
					&& !ObjectUtils.nullSafeEquals(passWord, this.authentication.getPassWord())) {
				this.authentication.setPassWord(passWord);
				this.modified = Boolean.TRUE;
			}
			return this;
		}
	}

	/**
	 * <h2 class="en-US">Builder implementation class of X.509 certificate authentication information</h2>
	 * <h2 class="zh-CN">X.509证书认证信息的构建器</h2>
	 *
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision : 1.0.0 $ $Date: Apr 10, 2018 15:48:19 $
	 */
	public static final class X509AuthenticationBuilder extends AuthenticationBuilder<X509Authentication> {

		/**
		 * <h3 class="en-US">Constructor method for X.509 certificate authentication information builder implementation class</h3>
		 * <h3 class="zh-CN">X.509证书认证信息的构建器的构造函数</h3>
		 *
		 * @param parentBuilder  <span class="en-US">Generics Type instance</span>
		 *                       <span class="zh-CN">泛型类实例对象</span>
		 * @param authentication <span class="en-US">向09 certificate authentication information</span>
		 *                       <span class="zh-CN">x509证书认证信息</span>
		 */
		X509AuthenticationBuilder(final ParentBuilder parentBuilder, final X509Authentication authentication) {
			super(parentBuilder, authentication);
		}

		/**
		 * <h3 class="en-US">Set X.509 certificate authentication information</h3>
		 * <h3 class="zh-CN">设置X.509证书身份认证信息</h3>
		 *
		 * @param x509Certificate <span class="en-US">X.509 certificate</span>
		 *                        <span class="zh-CN">X.509证书</span>
		 * @return <span class="en-US">The current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 * @throws CertificateEncodingException <span class="en-US">Error while reading certificate</span>
		 *                                      <span class="zh-CN">读取证书时出错</span>
		 */
		public X509AuthenticationBuilder x509(@Nonnull final X509Certificate x509Certificate)
				throws CertificateEncodingException {
			String certData = StringUtils.base64Encode(x509Certificate.getEncoded());
			if (!ObjectUtils.nullSafeEquals(this.authentication.getCertData(), certData)) {
				this.authentication.setCertData(certData);
				this.authentication.setLastModified(DateTimeUtils.currentUTCTimeMillis());
				this.modified = Boolean.TRUE;
			}
			return this;
		}
	}
}
