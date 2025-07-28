/*
 * Licensed to the Nervousync Studio (NSYC) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
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

package org.nervousync.brain.query.data;

import jakarta.xml.bind.annotation.*;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.brain.query.condition.Condition;
import org.nervousync.brain.query.condition.impl.ColumnCondition;
import org.nervousync.brain.query.condition.impl.GroupCondition;
import org.nervousync.brain.query.core.QueryItem;
import org.nervousync.brain.query.core.SortedItem;
import org.nervousync.brain.query.item.*;
import org.nervousync.brain.query.join.QueryJoin;
import org.nervousync.brain.query.join.SubQueryJoin;
import org.nervousync.brain.query.join.TableQueryJoin;
import org.nervousync.brain.query.sort.GroupBy;

import java.util.ArrayList;
import java.util.List;

/**
 * <h2 class="en-US">Sub-query define</h2>
 * <h2 class="zh-CN">子查询定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 9, 2020 18:19:42 $
 */
@XmlType(name = "sub_query_data", namespace = "https://nervousync.org/schemas/brain")
@XmlRootElement(name = "sub_query_data", namespace = "https://nervousync.org/schemas/brain")
public final class QueryData extends BeanObject {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = 904613011408758201L;

	/**
	 * <span class="en-US">Data table name</span>
	 * <span class="zh-CN">数据表名</span>
	 */
	@XmlElement(name = "table_name")
	private String tableName;
	/**
	 * <span class="en-US">Query item instance list</span>
	 * <span class="zh-CN">查询项目实例对象列表</span>
	 */
	@XmlElements({
			@XmlElement(name = "calculate_item", type = CalculateItem.class),
			@XmlElement(name = "column_item", type = ColumnItem.class),
			@XmlElement(name = "constant_item", type = ConstantItem.class),
			@XmlElement(name = "function_item", type = FunctionItem.class),
			@XmlElement(name = "sub_query_item", type = SubQueryItem.class)
	})
	@XmlElementWrapper(name = "item_list")
	private List<QueryItem> itemList;
	/**
	 * <span class="en-US">Related query information list</span>
	 * <span class="zh-CN">关联查询信息列表</span>
	 */
	@XmlElements({
			@XmlElement(name = "table_query_join", type = TableQueryJoin.class),
			@XmlElement(name = "sub_query_join", type = SubQueryJoin.class)
	})
	@XmlElementWrapper(name = "join_list")
	private List<QueryJoin> queryJoins;
	/**
	 * <span class="en-US">Query condition instance list</span>
	 * <span class="zh-CN">查询条件实例对象列表</span>
	 */
	@XmlElements({
			@XmlElement(name = "column_condition", type = ColumnCondition.class, namespace = "https://nervousync.org/schemas/brain"),
			@XmlElement(name = "group_condition", type = GroupCondition.class, namespace = "https://nervousync.org/schemas/brain")
	})
	@XmlElementWrapper(name = "condition_list")
	private List<Condition> conditionList;
	/**
	 * <span class="en-US">Identify key</span>
	 * <span class="zh-CN">分组识别代码列表</span>
	 */
	@XmlElement(name = "group_by")
	@XmlElementWrapper(name = "group_list")
	private List<GroupBy> groupByList;
	/**
	 * <span class="en-US">Group having condition instance list</span>
	 * <span class="zh-CN">分组筛选条件实例对象列表</span>
	 */
	@XmlElements({
			@XmlElement(name = "column_condition", type = ColumnCondition.class, namespace = "https://nervousync.org/schemas/brain"),
			@XmlElement(name = "group_condition", type = GroupCondition.class, namespace = "https://nervousync.org/schemas/brain")
	})
	@XmlElementWrapper(name = "having_list")
	private List<Condition> havingList;

	/**
	 * <h3 class="en-US">Constructor method for sub-query define</h3>
	 * <h3 class="zh-CN">子查询定义的构造方法</h3>
	 */
	public QueryData() {
		this.conditionList = new ArrayList<>();
		this.groupByList = new ArrayList<>();
		this.havingList = new ArrayList<>();
	}

	/**
	 * <h3 class="en-US">Getter method for data table name</h3>
	 * <h3 class="zh-CN">数据表名的Getter方法</h3>
	 *
	 * @return <span class="en-US">Data table name</span>
	 * <span class="zh-CN">数据表名</span>
	 */
	public String getTableName() {
		return this.tableName;
	}

	/**
	 * <h3 class="en-US">Setter method for data table name</h3>
	 * <h3 class="zh-CN">数据表名的Setter方法</h3>
	 *
	 * @param tableName <span class="en-US">Data table name</span>
	 *                  <span class="zh-CN">数据表名</span>
	 */
	public void setTableName(final String tableName) {
		this.tableName = tableName;
	}

	/**
	 * <h3 class="en-US">Getter method for the query item instance list</h3>
	 * <h3 class="zh-CN">查询项目实例对象列表的Getter方法</h3>
	 *
	 * @return <span class="en-US">Query item instance list</span>
	 * <span class="zh-CN">查询项目实例对象列表</span>
	 */
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

	/**
	 * <h3 class="en-US">Getter method for the related query information list</h3>
	 * <h3 class="zh-CN">关联查询信息列表的Getter方法</h3>
	 *
	 * @return <span class="en-US">Related query information list</span>
	 * <span class="zh-CN">关联查询信息列表</span>
	 */
	public List<QueryJoin> getQueryJoins() {
		return this.queryJoins;
	}

	/**
	 * <h3 class="en-US">Setter method for the related query information list</h3>
	 * <h3 class="zh-CN">关联查询信息列表的Setter方法</h3>
	 *
	 * @param queryJoins <span class="en-US">Related query information list</span>
	 *                   <span class="zh-CN">关联查询信息列表</span>
	 */
	public void setQueryJoins(final List<QueryJoin> queryJoins) {
		this.queryJoins = queryJoins;
	}

	/**
	 * <h3 class="en-US">Getter method for the query condition instance list</h3>
	 * <h3 class="zh-CN">查询条件实例对象列表的Getter方法</h3>
	 *
	 * @return <span class="en-US">Query condition instance list</span>
	 * <span class="zh-CN">查询条件实例对象列表</span>
	 */
	public List<Condition> getConditionList() {
		return this.conditionList;
	}

	/**
	 * <h3 class="en-US">Setter method for the query condition instance list</h3>
	 * <h3 class="zh-CN">查询条件实例对象列表的Setter方法</h3>
	 *
	 * @param conditionList <span class="en-US">Query condition instance list</span>
	 *                   <span class="zh-CN">查询条件实例对象列表</span>
	 */
	public void setConditionList(final List<Condition> conditionList) {
		this.conditionList = conditionList;
	}

	/**
	 * <h3 class="en-US">Getter method for group identify key</h3>
	 * <h3 class="zh-CN">分组识别代码列表的Getter方法</h3>
	 *
	 * @return <span class="en-US">Group identify key</span>
	 * <span class="zh-CN">分组识别代码列表</span>
	 */
	public List<GroupBy> getGroupByList() {
		return this.groupByList;
	}

	/**
	 * <h3 class="en-US">Setter method for group identify key</h3>
	 * <h3 class="zh-CN">分组识别代码列表的Setter方法</h3>
	 *
	 * @param groupByList <span class="en-US">Group identify key</span>
	 *                <span class="zh-CN">分组识别代码列表</span>
	 */
	public void setGroupByList(final List<GroupBy> groupByList) {
		this.groupByList = groupByList;
	}

	/**
	 * <h3 class="en-US">Getter method for the group having condition instance list</h3>
	 * <h3 class="zh-CN">分组筛选条件实例对象列表的Getter方法</h3>
	 *
	 * @return <span class="en-US">Group having condition instance list</span>
	 * <span class="zh-CN">分组筛选条件实例对象列表</span>
	 */
	public List<Condition> getHavingList() {
		return this.havingList;
	}

	/**
	 * <h3 class="en-US">Setter method for the group having condition instance list</h3>
	 * <h3 class="zh-CN">分组筛选条件实例对象列表的Setter方法</h3>
	 *
	 * @param havingList <span class="en-US">Group having condition instance list</span>
	 *                   <span class="zh-CN">分组筛选条件实例对象列表</span>
	 */
	public void setHavingList(final List<Condition> havingList) {
		this.havingList = havingList;
	}
}
