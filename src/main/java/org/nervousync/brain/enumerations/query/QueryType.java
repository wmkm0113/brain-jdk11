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

package org.nervousync.brain.enumerations.query;

import jakarta.xml.bind.annotation.XmlEnum;

/**
 * <h2 class="en-US">Enumeration value of query types</h2>
 * <h2 class="zh-CN">查询类型的枚举值</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 6, 2020 19:05:28 $
 */
@XmlEnum
public enum QueryType {
	/**
	 * <span class="en-US">Normal query</span>
	 * <span class="zh-CN">标准查询</span>
	 */
	NORMAL,
	/**
	 * <span class="en-US">Scalar sub-query</span>
	 * <span class="zh-CN">标量子查询</span>
	 */
	SCALAR,
	/**
	 * <span class="en-US">Table sub-query</span>
	 * <span class="zh-CN">表子查询</span>
	 */
	TABLE,
	/**
	 * <span class="en-US">Nested table sub-query</span>
	 * <span class="zh-CN">嵌套表子查询</span>
	 */
	NESTED_TABLE
}
