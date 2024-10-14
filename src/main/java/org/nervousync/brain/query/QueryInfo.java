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

package org.nervousync.brain.query;

import jakarta.xml.bind.annotation.*;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.brain.query.condition.Condition;
import org.nervousync.brain.query.condition.impl.ColumnCondition;
import org.nervousync.brain.query.condition.impl.GroupCondition;
import org.nervousync.brain.query.core.AbstractItem;
import org.nervousync.brain.query.core.SortedItem;
import org.nervousync.brain.query.filter.GroupBy;
import org.nervousync.brain.query.filter.OrderBy;
import org.nervousync.brain.query.item.ColumnItem;
import org.nervousync.brain.query.item.FunctionItem;
import org.nervousync.brain.query.item.QueryItem;
import org.nervousync.brain.query.join.QueryJoin;
import org.nervousync.commons.Globals;

import java.util.ArrayList;
import java.util.List;

/**
 * <h2 class="en-US">Query information define</h2>
 * <h2 class="zh-CN">查询信息定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 28, 2020 11:46:08 $
 */
@XmlType(name = "query_info", namespace = "https://nervousync.org/schemas/database")
@XmlRootElement(name = "query_info", namespace = "https://nervousync.org/schemas/database")
@XmlAccessorType(XmlAccessType.NONE)
public final class QueryInfo extends BeanObject {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = 549973159743148887L;

	/**
	 * <span class="en-US">Data table name</span>
	 * <span class="zh-CN">数据表名</span>
	 */
	@XmlElement(name = "table_name")
	private String tableName;
	/**
	 * <span class="en-US">Data table alias name</span>
	 * <span class="zh-CN">数据表别名</span>
	 */
	@XmlElement(name = "alias_name")
	private String aliasName;
	/**
	 * <span class="en-US">Related query information list</span>
	 * <span class="zh-CN">关联查询信息列表</span>
	 */
	@XmlElement(name = "query_join")
	@XmlElementWrapper(name = "join_list")
	private List<QueryJoin> queryJoins;
	/**
	 * <span class="en-US">Query item instance list</span>
	 * <span class="zh-CN">查询项目实例对象列表</span>
	 */
	@XmlElements({
			@XmlElement(name = "column_item", type = ColumnItem.class),
			@XmlElement(name = "function_item", type = FunctionItem.class),
			@XmlElement(name = "query_item", type = QueryItem.class)
	})
	@XmlElementWrapper(name = "item_list")
	private List<AbstractItem> itemList;
	/**
	 * <span class="en-US">Query condition instance list</span>
	 * <span class="zh-CN">查询条件实例对象列表</span>
	 */
	@XmlElements({
			@XmlElement(name = "column_condition", type = ColumnCondition.class),
			@XmlElement(name = "group_condition", type = GroupCondition.class)
	})
	@XmlElementWrapper(name = "condition_list")
	private List<Condition> conditionList;
	/**
	 * <span class="en-US">Query order by columns list</span>
	 * <span class="zh-CN">查询排序数据列列表</span>
	 */
	@XmlElement(name = "order_by")
	@XmlElementWrapper(name = "order_list")
	private List<OrderBy> orderByList;
	/**
	 * <span class="en-US">Query group by columns list</span>
	 * <span class="zh-CN">查询分组数据列列表</span>
	 */
	@XmlElement(name = "group_by")
	@XmlElementWrapper(name = "group_list")
	private List<GroupBy> groupByList;
	/**
	 * <span class="en-US">Group having condition instance list</span>
	 * <span class="zh-CN">分组筛选条件实例对象列表</span>
	 */
	@XmlElements({
			@XmlElement(name = "column_condition", type = ColumnCondition.class),
			@XmlElement(name = "group_condition", type = GroupCondition.class)
	})
	@XmlElementWrapper(name = "having_list")
	private List<Condition> havingList;
	/**
	 * <span class="en-US">Query result can cacheable</span>
	 * <span class="zh-CN">查询结果可以缓存</span>
	 */
	@XmlElement
	private boolean cacheables = Boolean.FALSE;
	/**
	 * <span class="en-US">Current page number</span>
	 * <span class="zh-CN">当前页数</span>
	 */
	@XmlElement(name = "page_number")
	private int pageNo = Globals.DEFAULT_VALUE_INT;
	/**
	 * <span class="en-US">Page limit records count</span>
	 * <span class="zh-CN">每页的记录数</span>
	 */
	@XmlElement(name = "page_limit")
	private int pageLimit = Globals.DEFAULT_VALUE_INT;

	/**
	 * <h3 class="en-US">Constructor method for query information define</h3>
	 * <h3 class="zh-CN">查询条件信息的构造方法</h3>
	 */
	public QueryInfo() {
		this.queryJoins = new ArrayList<>();
		this.itemList = new ArrayList<>();
		this.conditionList = new ArrayList<>();
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
	 * <h3 class="en-US">Getter method for data table alias name</h3>
	 * <h3 class="zh-CN">数据表别名的Getter方法</h3>
	 *
	 * @return <span class="en-US">Data table alias name</span>
	 * <span class="zh-CN">数据表别名</span>
	 */
	public String getAliasName() {
		return this.aliasName;
	}

	/**
	 * <h3 class="en-US">Setter method for data table alias name</h3>
	 * <h3 class="zh-CN">数据表别名的Setter方法</h3>
	 *
	 * @param aliasName <span class="en-US">Data table alias name</span>
	 *                  <span class="zh-CN">数据表别名</span>
	 */
	public void setAliasName(final String aliasName) {
		this.aliasName = aliasName;
	}

	/**
	 * <h3 class="en-US">Getter method for related query information list</h3>
	 * <h3 class="zh-CN">关联查询信息列表的Getter方法</h3>
	 *
	 * @return <span class="en-US">Related query information list</span>
	 * <span class="zh-CN">关联查询信息列表</span>
	 */
	public List<QueryJoin> getQueryJoins() {
		return this.queryJoins;
	}

	/**
	 * <h3 class="en-US">Setter method for related query information list</h3>
	 * <h3 class="zh-CN">关联查询信息列表的Setter方法</h3>
	 *
	 * @param queryJoins <span class="en-US">Related query information list</span>
	 *                   <span class="zh-CN">关联查询信息列表</span>
	 */
	public void setQueryJoins(final List<QueryJoin> queryJoins) {
		this.queryJoins = queryJoins;
	}

	/**
	 * <h3 class="en-US">Getter method for query item instance list</h3>
	 * <h3 class="zh-CN">查询项目实例对象列表的Getter方法</h3>
	 *
	 * @return <span class="en-US">Query item instance list</span>
	 * <span class="zh-CN">查询项目实例对象列表</span>
	 */
	public List<AbstractItem> getItemList() {
		return this.itemList;
	}

	/**
	 * <h3 class="en-US">Setter method for query item instance list</h3>
	 * <h3 class="zh-CN">查询项目实例对象列表的Setter方法</h3>
	 *
	 * @param itemList <span class="en-US">Query item instance list</span>
	 *                 <span class="zh-CN">查询项目实例对象列表</span>
	 */
	public void setItemList(final List<AbstractItem> itemList) {
		this.itemList = itemList;
		this.itemList.sort(SortedItem.desc());

	}

	/**
	 * <h3 class="en-US">Getter method for query condition instance list</h3>
	 * <h3 class="zh-CN">查询条件实例对象列表的Getter方法</h3>
	 *
	 * @return <span class="en-US">Query condition instance list</span>
	 * <span class="zh-CN">查询条件实例对象列表</span>
	 */
	public List<Condition> getConditionList() {
		return this.conditionList;
	}

	/**
	 * <h3 class="en-US">Setter method for query condition instance list</h3>
	 * <h3 class="zh-CN">查询条件实例对象列表的Setter方法</h3>
	 *
	 * @param conditionList <span class="en-US">Query condition instance list</span>
	 *                      <span class="zh-CN">查询条件实例对象列表</span>
	 */
	public void setConditionList(final List<Condition> conditionList) {
		this.conditionList = conditionList;
		this.conditionList.sort(SortedItem.desc());
	}

	/**
	 * <h3 class="en-US">Getter method for query order by columns list</h3>
	 * <h3 class="zh-CN">查询排序数据列列表的Getter方法</h3>
	 *
	 * @return <span class="en-US">Query order by columns list</span>
	 * <span class="zh-CN">查询排序数据列列表</span>
	 */
	public List<OrderBy> getOrderByList() {
		return this.orderByList;
	}

	/**
	 * <h3 class="en-US">Setter method for query order by columns list</h3>
	 * <h3 class="zh-CN">查询排序数据列列表的Setter方法</h3>
	 *
	 * @param orderByList <span class="en-US">Query order by columns list</span>
	 *                    <span class="zh-CN">查询排序数据列列表</span>
	 */
	public void setOrderByList(final List<OrderBy> orderByList) {
		this.orderByList = orderByList;
		this.orderByList.sort(SortedItem.desc());
	}

	/**
	 * <h3 class="en-US">Getter method for query group by columns list</h3>
	 * <h3 class="zh-CN">查询分组数据列列表的Getter方法</h3>
	 *
	 * @return <span class="en-US">Query group by columns list</span>
	 * <span class="zh-CN">查询分组数据列列表</span>
	 */
	public List<GroupBy> getGroupByList() {
		return this.groupByList;
	}

	/**
	 * <h3 class="en-US">Setter method for query group by columns list</h3>
	 * <h3 class="zh-CN">查询分组数据列列表的Setter方法</h3>
	 *
	 * @param groupByList <span class="en-US">Query group by columns list</span>
	 *                    <span class="zh-CN">查询分组数据列列表</span>
	 */
	public void setGroupByList(final List<GroupBy> groupByList) {
		this.groupByList = groupByList;
		this.groupByList.sort(SortedItem.desc());
	}

	/**
	 * <h3 class="en-US">Getter method for group having condition instance list</h3>
	 * <h3 class="zh-CN">分组筛选条件实例对象列表的Getter方法</h3>
	 *
	 * @return <span class="en-US">Group having condition instance list</span>
	 * <span class="zh-CN">分组筛选条件实例对象列表</span>
	 */
	public List<Condition> getHavingList() {
		return this.havingList;
	}

	/**
	 * <h3 class="en-US">Setter method for group having condition instance list</h3>
	 * <h3 class="zh-CN">分组筛选条件实例对象列表的Setter方法</h3>
	 *
	 * @param havingList <span class="en-US">Group having condition instance list</span>
	 *                   <span class="zh-CN">分组筛选条件实例对象列表</span>
	 */
	public void setHavingList(final List<Condition> havingList) {
		this.havingList = havingList;
	}

	/**
	 * <h3 class="en-US">Getter method for query result can cacheable</h3>
	 * <h3 class="zh-CN">查询结果可以缓存的Getter方法</h3>
	 *
	 * @return <span class="en-US">Query result can cacheable</span>
	 * <span class="zh-CN">查询结果可以缓存</span>
	 */
	public boolean isCacheables() {
		return this.cacheables;
	}

	/**
	 * <h3 class="en-US">Setter method for query result can cacheable</h3>
	 * <h3 class="zh-CN">查询结果可以缓存的Setter方法</h3>
	 *
	 * @param cacheables <span class="en-US">Query result can cacheable</span>
	 *                   <span class="zh-CN">查询结果可以缓存</span>
	 */
	public void setCacheables(final boolean cacheables) {
		this.cacheables = cacheables;
	}

	/**
	 * <h3 class="en-US">Getter method for current page number</h3>
	 * <h3 class="zh-CN">当前页数的Getter方法</h3>
	 *
	 * @return <span class="en-US">Current page number</span>
	 * <span class="zh-CN">当前页数</span>
	 */
	public int getPageNo() {
		return this.pageNo;
	}

	/**
	 * <h3 class="en-US">Setter method for current page number</h3>
	 * <h3 class="zh-CN">当前页数的Setter方法</h3>
	 *
	 * @param pageNo <span class="en-US">Current page number</span>
	 *               <span class="zh-CN">当前页数</span>
	 */
	public void setPageNo(final int pageNo) {
		this.pageNo = pageNo;
	}

	/**
	 * <h3 class="en-US">Getter method for query page limit</h3>
	 * <h3 class="zh-CN">查询分页记录数的Getter方法</h3>
	 *
	 * @return <span class="en-US">Query page limit</span>
	 * <span class="zh-CN">查询分页记录数</span>
	 */
	public int getPageLimit() {
		return this.pageLimit;
	}

	/**
	 * <h3 class="en-US">Setter method for query page limit</h3>
	 * <h3 class="zh-CN">查询分页记录数的Setter方法</h3>
	 *
	 * @param pageLimit <span class="en-US">Query page limit</span>
	 *                  <span class="zh-CN">查询分页记录数</span>
	 */
	public void setPageLimit(final int pageLimit) {
		this.pageLimit = pageLimit;
	}
}
