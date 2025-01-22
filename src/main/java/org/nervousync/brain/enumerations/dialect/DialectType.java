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

package org.nervousync.brain.enumerations.dialect;

/**
 * <h2 class="en-US">Data source dialect type enumeration value</h2>
 * <h2 class="zh-CN">数据源方言类型枚举值</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Sep 12, 2023 15:16:08 $
 */
public enum DialectType {
	/**
	 * <span class="en-US">Relational database</span>
	 * <span class="zh-CN">关系型数据库</span>
	 */
	Relational,
	/**
	 * <span class="en-US">Distribute database</span>
	 * <span class="zh-CN">分布式数据库</span>
	 */
	Distribute,
	/**
	 * <span class="en-US">Graph database</span>
	 * <span class="zh-CN">图数据库</span>
	 */
	Graph,
	/**
	 * <span class="en-US">Timescale database</span>
	 * <span class="zh-CN">时序数据库</span>
	 */
	Timescale,
	/**
	 * <span class="en-US">Remote database</span>
	 * <span class="zh-CN">远程数据库</span>
	 */
	Remote,
	/**
	 * <span class="en-US">Default type</span>
	 * <span class="zh-CN">默认类型</span>
	 */
	Default
}
