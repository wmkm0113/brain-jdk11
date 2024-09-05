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

package org.nervousync.brain.schemas.remote;

/**
 * <h2 class="en-US">MBean define class for remote database implement class</h2>
 * <h2 class="zh-CN">远程数据源实现类的MBean定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Nov 12, 2020 11:47:07 $
 */
public interface RemoteSchemaMBean {

	/**
	 * <h3 class="en-US">Get the remote data source target address</h3>
	 * <h3 class="zh-CN">获取远程数据源目标地址</h3>
	 *
	 * @return <span class="en-US">Remote data source target address</span>
	 * <span class="zh-CN">远程数据源目标地址</span>
	 */
	String getRemoteAddress();

	/**
	 * <h3 class="en-US">Activated connections count</h3>
	 * <h3 class="zh-CN">使用中的连接数</h3>
	 *
	 * @return <span class="en-US">Connections count</span>
	 * <span class="zh-CN">连接数</span>
	 */
	int getActiveCount();

	/**
	 * <h3 class="en-US">Data source was initialized</h3>
	 * <h3 class="zh-CN">数据源已经初始化完毕</h3>
	 *
	 * @return <span class="en-US">Initialize status of data source</span>
	 * <span class="zh-CN">数据源初始化状态</span>
	 */
	boolean isInitialized();
}
