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

package org.nervousync.brain.dialects;

import org.nervousync.brain.configs.auth.Authentication;
import org.nervousync.brain.configs.secure.TrustStore;

import java.sql.Wrapper;
import java.util.Properties;

/**
 * <h2 class="en-US">Database dialect interface, used to load implementation classes in SPI mode</h2>
 * <h2 class="zh-CN">数据库方言接口，用于SPI方式加载实现类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Feb 18, 2019 10:15:08 $
 */
public interface Dialect extends Wrapper {

	/**
	 * <h3 class="en-US">Handle case of names</h3>
	 * <h3 class="zh-CN">处理名称的大小写</h3>
	 *
	 * @param name <span class="en-US">The name string to be processed</span>
	 *             <span class="zh-CN">需要处理的名称字符串</span>
	 * @return <span class="en-US">Processed name string</span>
	 * <span class="zh-CN">处理后的名称字符串</span>
	 */
	String nameCase(final String name);

	/**
	 * <h3 class="en-US">Generate parameter information required for connection</h3>
	 * <h3 class="zh-CN">生成连接需要使用的参数信息</h3>
	 *
	 * @param trustStore     <span class="en-US">Trust certificate store configure information</span>
	 *                       <span class="zh-CN">信任证书库配置信息</span>
	 * @param authentication <span class="en-US">Authentication information</span>
	 *                       <span class="zh-CN">身份认证信息</span>
	 * @return <span class="en-US">Connect properties instance object</span>
	 * <span class="zh-CN">连接属性值</span>
	 */
	Properties properties(final TrustStore trustStore, final Authentication authentication);
}
