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

package org.nervousync.brain.dialects.distribute;

import org.nervousync.brain.configs.auth.Authentication;
import org.nervousync.brain.configs.secure.TrustStore;
import org.nervousync.brain.configs.server.ServerInfo;
import org.nervousync.brain.dialects.core.BaseDialect;
import org.nervousync.brain.exceptions.dialects.DialectException;

import java.util.List;

/**
 * <h2 class="en-US">Distribute database dialect abstract class</h2>
 * <h2 class="zh-CN">分布式数据库方言抽象类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Feb 18, 2019 10:38:52 $
 */
public abstract class DistributeDialect extends BaseDialect {

	/**
	 * <h3 class="en-US">Constructor method for distribute database dialect abstract class</h3>
	 * <h3 class="zh-CN">分布式数据库方言抽象类的构造方法</h3>
	 *
	 * @throws DialectException <span class="en-US">If the implementation class does not find the org. nervousync. brain. annotations. dialect.SchemaDialect annotation</span>
	 *                          <span class="zh-CN">如果实现类未找到org. nervousync. brain. annotations. dialect.SchemaDialect注解</span>
	 */
	protected DistributeDialect() throws DialectException {
		super();
	}

	/**
	 * <h3 class="en-US">Generate database operate client</h3>
	 * <h3 class="zh-CN">生成数据库操作客户端</h3>
	 *
	 * @param serverList      <span class="en-US">Database server info list</span>
	 *                        <span class="zh-CN">数据库从服务器列表</span>
	 * @param authentication  <span class="en-US">Identity authentication configuration information</span>
	 *                        <span class="zh-CN">身份认证配置信息</span>
	 * @param trustStore      <span class="en-US">Trust certificate store configuration information</span>
	 *                        <span class="zh-CN">信任证书库配置信息</span>
	 * @param lowQueryTimeout <span class="en-US">Low query timeout (Unit: milliseconds)</span>
	 *                        <span class="zh-CN">慢查询的临界时间（单位：毫秒）</span>
	 * @param useSsl          <span class="en-US">Using SSL when connect to server</span>
	 *                        <span class="zh-CN">使用SSL连接</span>
	 * @param validateTimeout <span class="en-US">Timeout value of connection validate</span>
	 *                        <span class="zh-CN">连接检查超时时间</span>
	 * @param connectTimeout  <span class="en-US">Timeout value of create connection</span>
	 *                        <span class="zh-CN">建立连接超时时间</span>
	 * @return <span class="en-US">Generated database operate client</span>
	 * <span class="zh-CN">生成的数据库操作客户端</span>
	 * @throws Exception <span class="en-US">An error occurred during initialization</span>
	 *                   <span class="zh-CN">初始化过程中出错</span>
	 */
	public abstract DistributeClient newClient(final List<ServerInfo> serverList, final Authentication authentication,
	                                           final TrustStore trustStore, final long lowQueryTimeout,
	                                           final boolean useSsl, final int validateTimeout,
	                                           final int connectTimeout) throws Exception;
}
