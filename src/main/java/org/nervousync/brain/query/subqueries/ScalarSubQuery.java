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

package org.nervousync.brain.query.subqueries;

import jakarta.xml.bind.annotation.*;
import org.nervousync.brain.enumerations.query.QueryType;
import org.nervousync.brain.query.core.AbstractQuery;
import org.nervousync.brain.query.core.QueryItem;
import org.nervousync.brain.query.item.*;

/**
 * <h2 class="en-US">Scalar sub-query define</h2>
 * <h2 class="zh-CN">标量子查询定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 9, 2020 18:19:42 $
 */
@XmlType(name = "scalar_sub_query", namespace = "https://nervousync.org/schemas/brain")
@XmlRootElement(name = "scalar_sub_query", namespace = "https://nervousync.org/schemas/brain")
@XmlAccessorType(XmlAccessType.NONE)
public final class ScalarSubQuery extends AbstractQuery {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = -1669671159812582632L;

	/**
	 * <span class="en-US">Query item instance object</span>
	 * <span class="zh-CN">查询项目实例对象</span>
	 */
	@XmlElements({
			@XmlElement(name = "calculate_item", type = CalculateItem.class, namespace = "https://nervousync.org/schemas/brain"),
			@XmlElement(name = "column_item", type = ColumnItem.class, namespace = "https://nervousync.org/schemas/brain"),
			@XmlElement(name = "constant_item", type = ConstantItem.class, namespace = "https://nervousync.org/schemas/brain"),
			@XmlElement(name = "function_item", type = FunctionItem.class, namespace = "https://nervousync.org/schemas/brain"),
			@XmlElement(name = "sub_query_item", type = SubQueryItem.class, namespace = "https://nervousync.org/schemas/brain")
	})
	private QueryItem queryItem;

	/**
	 * <h3 class="en-US">Constructor method for the scalar sub-query define</h3>
	 * <h3 class="zh-CN">标量子查询定义的构造方法</h3>
	 */
	public ScalarSubQuery() {
		super(QueryType.SCALAR);
	}

	/**
	 * <h3 class="en-US">Getter method for the query item instance object</h3>
	 * <h3 class="zh-CN">查询项目实例对象的Getter方法</h3>
	 *
	 * @return <span class="en-US">Query item instance object</span>
	 * <span class="zh-CN">查询项目实例对象</span>
	 */
	public QueryItem getQueryItem() {
		return this.queryItem;
	}

	/**
	 * <h3 class="en-US">Setter method for the query item instance object</h3>
	 * <h3 class="zh-CN">查询项目实例对象的Setter方法</h3>
	 *
	 * @param queryItem <span class="en-US">Query item instance object</span>
	 *                  <span class="zh-CN">查询项目实例对象</span>
	 */
	public void setQueryItem(final QueryItem queryItem) {
		this.queryItem = queryItem;
	}
}
