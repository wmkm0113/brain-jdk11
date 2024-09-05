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

package org.nervousync.brain.source;

import org.nervousync.brain.enumerations.ddl.DDLType;

/**
 * <h2 class="en-US">MBean define class for Nervousync brain data source</h2>
 * <h2 class="zh-CN">Nervousync大脑数据源的MBean定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Nov 12, 2020 11:47:07 $
 */
public interface BrainDataSourceMBean {

	/**
	 * <h3 class="en-US">Obtains default data source name</h3>
	 * <h3 class="zh-CN">获取默认数据源名称</h3>
	 *
	 * @return <span class="en-US">Default data source name</span>
	 * <span class="zh-CN">默认数据源名称</span>
	 */
	String getDefaultSchema();

	/**
	 * <h3 class="en-US">Configure default data source name</h3>
	 * <h3 class="zh-CN">设置默认数据源名称</h3>
	 *
	 * @param schema <span class="en-US">Default data source name</span>
	 *               <span class="zh-CN">默认数据源名称</span>
	 */
	void defaultSchema(final String schema);

	/**
	 * <h3 class="en-US">Get JMX monitoring enabled status</h3>
	 * <h3 class="zh-CN">获取JMX监控开启状态</h3>
	 *
	 * @return <span class="en-US">JMX monitoring enabled status</span>
	 * <span class="zh-CN">JMX监控开启状态</span>
	 */
	boolean isJmxEnabled();

	/**
	 * <h3 class="en-US">Configure JMX monitoring enabled status</h3>
	 * <h3 class="zh-CN">设置JMX监控开启状态</h3>
	 *
	 * @param enabled <span class="en-US">JMX monitoring enabled status</span>
	 *                <span class="zh-CN">JMX监控开启状态</span>
	 */
	void jmxEnabled(final boolean enabled);

	/**
	 * <h3 class="en-US">Get DDL type enumeration value</h3>
	 * <h3 class="zh-CN">获取DDL类型枚举值</h3>
	 *
	 * @return <span class="en-US">DDL type enumeration value</span>
	 * <span class="zh-CN">DDL类型枚举值</span>
	 */
	DDLType getDDLType();

	/**
	 * <h3 class="en-US">Configure DDL type enumeration value</h3>
	 * <h3 class="zh-CN">设置DDL类型枚举值</h3>
	 *
	 * @param ddlType <span class="en-US">DDL type enumeration value</span>
	 *                <span class="zh-CN">DDL类型枚举值</span>
	 */
	void ddlType(final DDLType ddlType);

	/**
	 * <h3 class="en-US">Data source was initialized</h3>
	 * <h3 class="zh-CN">数据源已经初始化完毕</h3>
	 *
	 * @return <span class="en-US">Initialize status of data source</span>
	 * <span class="zh-CN">数据源初始化状态</span>
	 */
	boolean isInitialized();
}
