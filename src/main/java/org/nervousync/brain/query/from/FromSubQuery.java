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

package org.nervousync.brain.query.from;

import jakarta.xml.bind.annotation.*;
import org.nervousync.brain.enumerations.query.FromType;
import org.nervousync.brain.query.core.QueryFrom;
import org.nervousync.brain.query.data.QueryData;

/**
 * <h2 class="en-US">Query from sub-query definition</h2>
 * <h2 class="zh-CN">查询来源子查询定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 9, 2020 11:42:19 $
 */
@XmlType(name = "from_sub_query", namespace = "https://nervousync.org/schemas/brain")
@XmlRootElement(name = "from_sub_query", namespace = "https://nervousync.org/schemas/brain")
@XmlAccessorType(XmlAccessType.NONE)
public final class FromSubQuery extends QueryFrom {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = -6504876902805709275L;

	/**
	 * <span class="en-US">Sub-query information</span>
	 * <span class="zh-CN">子查询信息</span>
	 */
	@XmlElement(name = "query_data", namespace = "https://nervousync.org/schemas/brain")
	private QueryData queryData;

	/**
	 * <h3 class="en-US">Constructor method for the query from sub-query definition</h3>
	 * <h3 class="zh-CN">查询来源子查询定义的构造方法</h3>
	 */
	public FromSubQuery() {
		super(FromType.SubQuery);
	}

	/**
	 * <h3 class="en-US">Getter method for query information</h3>
	 * <h3 class="zh-CN">查询信息的Getter方法</h3>
	 *
	 * @return <span class="en-US">Query information</span>
	 * <span class="zh-CN">查询信息</span>
	 */
	public QueryData getQueryData() {
		return queryData;
	}

	/**
	 * <h3 class="en-US">Setter method for query information</h3>
	 * <h3 class="zh-CN">查询信息的Setter方法</h3>
	 *
	 * @param queryData <span class="en-US">Query information</span>
	 *                  <span class="zh-CN">查询信息</span>
	 */
	public void setQueryData(QueryData queryData) {
		this.queryData = queryData;
	}
}
