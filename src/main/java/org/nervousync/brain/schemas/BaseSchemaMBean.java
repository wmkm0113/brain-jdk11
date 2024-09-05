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

package org.nervousync.brain.schemas;

/**
 * <h2 class="en-US">MBean define class for data source implement class</h2>
 * <h2 class="zh-CN">数据源实现类的MBean定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Nov 12, 2020 11:47:07 $
 */
public interface BaseSchemaMBean {

	/**
	 * <h3 class="en-US">Read timeout value of low query</h3>
	 * <h3 class="zh-CN">获取慢查询的临界时间</h3>
	 *
	 * @return <span class="en-US">Low query timeout (Unit: milliseconds)</span>
	 * <span class="zh-CN">慢查询的临界时间（单位：毫秒）</span>
	 */
	long getLowQueryTimeout();

	/**
	 * <h3 class="en-US">Setup timeout value of low query</h3>
	 * <h3 class="zh-CN">设置慢查询的临界时间</h3>
	 *
	 * @param timeout <span class="en-US">Low query timeout (Unit: milliseconds)</span>
	 *                <span class="zh-CN">慢查询的临界时间（单位：毫秒）</span>
	 */
	void lowQueryTimeout(long timeout);

	/**
	 * <h3 class="en-US">Read timeout value of connection validate</h3>
	 * <h3 class="zh-CN">获取连接检查超时时间</h3>
	 *
	 * @return <span class="en-US">Timeout value of connection validate</span>
	 * <span class="zh-CN">连接检查超时时间</span>
	 */
	int getValidateTimeout();

	/**
	 * <h3 class="en-US">Setup timeout value of connection validate</h3>
	 * <h3 class="zh-CN">设置连接检查超时时间</h3>
	 *
	 * @param timeout <span class="en-US">Timeout value of connection validate</span>
	 *                <span class="zh-CN">连接检查超时时间</span>
	 */
	void validateTimeout(int timeout);

	/**
	 * <h3 class="en-US">Read timeout value of create connection</h3>
	 * <h3 class="zh-CN">获取建立连接超时时间</h3>
	 *
	 * @return <span class="en-US">Timeout value of create connection</span>
	 * <span class="zh-CN">建立连接超时时间</span>
	 */
	int getConnectTimeout();

	/**
	 * <h3 class="en-US">Setup timeout value of create connection</h3>
	 * <h3 class="zh-CN">设置建立连接超时时间</h3>
	 *
	 * @param timeout <span class="en-US">Timeout value of create connection</span>
	 *                <span class="zh-CN">建立连接超时时间</span>
	 */
	void connectTimeout(int timeout);

	/**
	 * <h3 class="en-US">Data source was initialized</h3>
	 * <h3 class="zh-CN">数据源已经初始化完毕</h3>
	 *
	 * @return <span class="en-US">Initialize status of data source</span>
	 * <span class="zh-CN">数据源初始化状态</span>
	 */
	boolean isInitialized();
}
