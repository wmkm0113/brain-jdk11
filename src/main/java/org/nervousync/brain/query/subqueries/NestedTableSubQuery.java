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

import jakarta.annotation.Nonnull;
import jakarta.xml.bind.annotation.*;
import org.nervousync.brain.enumerations.query.QueryType;
import org.nervousync.brain.query.core.AbstractQuery;
import org.nervousync.brain.query.core.QueryItem;
import org.nervousync.brain.query.core.SortedItem;
import org.nervousync.brain.query.item.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <h2 class="en-US">Nested table sub-query define</h2>
 * <h2 class="zh-CN">嵌套表子查询定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 9, 2020 18:19:42 $
 */
@XmlType(name = "nested_table_sub_query", namespace = "https://nervousync.org/schemas/brain")
@XmlRootElement(name = "nested_table_sub_query", namespace = "https://nervousync.org/schemas/brain")
@XmlAccessorType(XmlAccessType.NONE)
public final class NestedTableSubQuery extends AbstractQuery {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = 2579683840087561629L;

	@XmlElements({
			@XmlElement(name = "calculate_item", type = CalculateItem.class, namespace = "https://nervousync.org/schemas/brain"),
			@XmlElement(name = "column_item", type = ColumnItem.class, namespace = "https://nervousync.org/schemas/brain"),
			@XmlElement(name = "constant_item", type = ConstantItem.class, namespace = "https://nervousync.org/schemas/brain"),
			@XmlElement(name = "function_item", type = FunctionItem.class, namespace = "https://nervousync.org/schemas/brain"),
			@XmlElement(name = "sub_query_item", type = SubQueryItem.class, namespace = "https://nervousync.org/schemas/brain")
	})
	@XmlElementWrapper(name = "item_list")
	private List<QueryItem> itemList;

	/**
	 * <h3 class="en-US">Constructor method for the table sub-query define</h3>
	 * <h3 class="zh-CN">表子查询定义的构造方法</h3>
	 */
	public NestedTableSubQuery() {
		super(QueryType.NESTED_TABLE);
		this.itemList = new ArrayList<>();
	}

	/**
	 * <h3 class="en-US">Getter method for the query item instance list</h3>
	 * <h3 class="zh-CN">查询项目实例对象列表的Getter方法</h3>
	 *
	 * @return <span class="en-US">Query item instance list</span>
	 * <span class="zh-CN">查询项目实例对象列表</span>
	 */
	@Nonnull
	public List<QueryItem> getItemList() {
		return this.itemList;
	}

	/**
	 * <h3 class="en-US">Setter method for the query item instance list</h3>
	 * <h3 class="zh-CN">查询项目实例对象列表的Setter方法</h3>
	 *
	 * @param itemList <span class="en-US">Query item instance list</span>
	 *                 <span class="zh-CN">查询项目实例对象列表</span>
	 */
	public void setItemList(final List<QueryItem> itemList) {
		this.itemList = (itemList == null) ? new ArrayList<>() : itemList;
		this.itemList.sort(SortedItem.desc());
	}
}
