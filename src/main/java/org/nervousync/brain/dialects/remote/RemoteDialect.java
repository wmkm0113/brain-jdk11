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

package org.nervousync.brain.dialects.remote;

import jakarta.ws.rs.client.ClientBuilder;
import org.nervousync.brain.dialects.core.BaseDialect;
import org.nervousync.brain.enumerations.dialect.DialectType;
import org.nervousync.brain.exceptions.dialects.DialectException;
import org.nervousync.brain.query.param.AbstractParameter;
import org.nervousync.commons.Globals;
import org.nervousync.utils.ServiceUtils;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

/**
 * <h2 class="en-US">Remote database dialect abstract class</h2>
 * <h2 class="zh-CN">远程数据库方言抽象类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Feb 18, 2019 10:38:52 $
 */
public abstract class RemoteDialect extends BaseDialect {

	/**
	 * <h3 class="en-US">Constructor method for remote database dialect implementation class</h3>
	 * <h3 class="zh-CN">远程数据库方言实现类的构造方法</h3>
	 *
	 * @throws DialectException <span class="en-US">If the implementation class does not find the org. nervousync. brain. annotations. dialect.SchemaDialect annotation</span>
	 *                          <span class="zh-CN">如果实现类未找到org. nervousync. brain. annotations. dialect.SchemaDialect注解</span>
	 */
	public RemoteDialect() throws DialectException {
		super(DialectType.Remote);
	}

	@Override
	public final String defaultValue(final int jdbcType, final int length, final int precision, final int scale,
	                                 final Object object) {
		return Globals.DEFAULT_VALUE_STRING;
	}

	@Override
	protected final String parameterValue(final Map<String, String> aliasMap, final AbstractParameter<?> abstractParameter,
	                                      final List<Object> values) {
		return Globals.DEFAULT_VALUE_STRING;
	}

	@Override
	public final String nameCase(final String name) {
		return name;
	}

	/**
	 * <h3 class="en-US">Generate SOAP client instance object</h3>
	 * <h3 class="zh-CN">生成SOAP请求客户端</h3>
	 *
	 * @param targetAddress <span class="en-US">Remote address</span>
	 *                      <span class="zh-CN">远端地址</span>
	 * @param configMap     <span class="en-US">Request configure information map</span>
	 *                      <span class="zh-CN">请求配置信息映射</span>
	 * @return <span class="en-US">Generated client instance</span>
	 * <span class="zh-CN">生成的客户端实例对象</span>
	 * @throws MalformedURLException <span class="en-US">if no protocol is specified, or an unknown protocol is found, or spec is null.</span>
	 *                               <span class="zh-CN">如果没有指定协议，或者发现未知协议，或者spec为空。</span>
	 */
	public final RemoteClient SOAPClient(final String targetAddress, final Map<String, String> configMap)
			throws MalformedURLException {
		return ServiceUtils.SOAPClient(targetAddress, RemoteClient.class, configMap);
	}

	/**
	 * <h3 class="en-US">Generate Restful client instance object</h3>
	 * <h3 class="zh-CN">生成Restful请求客户端</h3>
	 *
	 * @param targetAddress <span class="en-US">Remote address</span>
	 *                      <span class="zh-CN">远端地址</span>
	 * @param clientBuilder <span class="en-US">Configured client generator</span>
	 *                      <span class="zh-CN">配置好的客户端生成器</span>
	 * @param headerMap     <span class="en-US">Request configure information map</span>
	 *                      <span class="zh-CN">请求配置信息映射</span>
	 * @return <span class="en-US">Generated client instance</span>
	 * <span class="zh-CN">生成的客户端实例对象</span>
	 * @throws MalformedURLException <span class="en-US">if no protocol is specified, or an unknown protocol is found, or spec is null.</span>
	 *                               <span class="zh-CN">如果没有指定协议，或者发现未知协议，或者spec为空。</span>
	 */
	public final RemoteClient restfulClient(final String targetAddress, final ClientBuilder clientBuilder,
	                                        final Map<String, String> headerMap) throws MalformedURLException {
		return ServiceUtils.RestfulClient(targetAddress, clientBuilder, RemoteClient.class, headerMap);
	}
}
