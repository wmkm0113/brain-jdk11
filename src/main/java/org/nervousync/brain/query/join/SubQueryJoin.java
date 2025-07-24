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

package org.nervousync.brain.query.join;

import jakarta.xml.bind.annotation.*;
import org.nervousync.brain.query.data.QueryData;

/**
 * <h2 class="en-US">Sub-query join information defines</h2>
 * <h2 class="zh-CN">子查询关联信息定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Jul 30, 2023 15:57:33 $
 */
@XmlType(name = "sub_query_join", namespace = "https://nervousync.org/schemas/brain")
@XmlRootElement(name = "sub_query_join", namespace = "https://nervousync.org/schemas/brain")
@XmlAccessorType(XmlAccessType.NONE)
public final class SubQueryJoin extends QueryJoin {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = -4404097218729878715L;

	/**
	 * <span class="en-US">Sub-query data information</span>
	 * <span class="zh-CN">子查询信息</span>
	 */
	@XmlElement(name = "sub_query_data")
	private QueryData subQuery;

	/**
	 * <h3 class="en-US">Constructor method for sub-query join information define</h3>
	 * <h3 class="zh-CN">子查询关联信息定义的构造方法</h3>
	 */
	public SubQueryJoin() {
	}

	/**
	 * <h3 class="en-US">Getter method for the sub-query data information</h3>
	 * <h3 class="zh-CN">子查询信息的Getter方法</h3>
	 *
	 * @return <span class="en-US">Sub-query data information</span>
	 * <span class="zh-CN">子查询信息</span>
	 */
	public QueryData getSubQuery() {
		return this.subQuery;
	}

	/**
	 * <h3 class="en-US">Setter method for the sub-query data information</h3>
	 * <h3 class="zh-CN">子查询信息的Setter方法</h3>
	 *
	 * @param subQuery <span class="en-US">Sub-query data information</span>
	 *                 <span class="zh-CN">子查询信息</span>
	 */
	public void setSubQuery(final QueryData subQuery) {
		this.subQuery = subQuery;
	}
}
