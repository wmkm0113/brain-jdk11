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

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nonnull;
import jakarta.persistence.LockModeType;
import jakarta.xml.bind.annotation.*;
import org.nervousync.annotations.beans.OutputConfig;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.brain.query.condition.Condition;
import org.nervousync.brain.query.condition.impl.ColumnCondition;
import org.nervousync.brain.query.condition.impl.GroupCondition;
import org.nervousync.brain.query.core.QueryFrom;
import org.nervousync.brain.query.core.QueryItem;
import org.nervousync.brain.query.core.SortedItem;
import org.nervousync.brain.query.from.FromSubQuery;
import org.nervousync.brain.query.from.FromTable;
import org.nervousync.brain.query.sort.GroupBy;
import org.nervousync.brain.query.sort.OrderBy;
import org.nervousync.brain.query.item.*;
import org.nervousync.brain.query.join.QueryJoin;
import org.nervousync.commons.Globals;
import org.nervousync.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <h2 class="en-US">Query information defines</h2>
 * <h2 class="zh-CN">查询信息定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 28, 2020 11:46:08 $
 */
@XmlType(name = "query_info", namespace = "https://nervousync.org/schemas/brain")
@XmlRootElement(name = "query_info", namespace = "https://nervousync.org/schemas/brain")
@XmlAccessorType(XmlAccessType.NONE)
@OutputConfig(formatted = true, defaultType = StringUtils.StringType.XML, types = {StringUtils.StringType.JSON, StringUtils.StringType.YAML})
public final class QueryInfo extends BeanObject {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = 549973159743148887L;

	/**
	 * <span class="en-US">Sheet name</span>
	 * <span class="zh-CN">工作表名称</span>
	 */
	@JsonIgnore
	private String sheetName;
	/**
	 * <span class="en-US">Query from information list</span>
	 * <span class="zh-CN">查询来源信息列表</span>
	 */
	@Nonnull
	@XmlElements({
			@XmlElement(name = "from_sub_query", type = FromSubQuery.class),
			@XmlElement(name = "from_table", type = FromTable.class)
	})
	@XmlElementWrapper(name = "from_list")
	private List<QueryFrom> queryFrom;
	/**
	 * <span class="en-US">Related query information list</span>
	 * <span class="zh-CN">关联查询信息列表</span>
	 */
	@Nonnull
	@XmlElement(name = "query_join")
	@XmlElementWrapper(name = "join_list")
	private List<QueryJoin> queryJoins;
	/**
	 * <span class="en-US">Query item instance list</span>
	 * <span class="zh-CN">查询项目实例对象列表</span>
	 */
	@Nonnull
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
	 * <span class="en-US">Query condition instance list</span>
	 * <span class="zh-CN">查询条件实例对象列表</span>
	 */
	@Nonnull
	@XmlElements({
			@XmlElement(name = "column_condition", type = ColumnCondition.class),
			@XmlElement(name = "group_condition", type = GroupCondition.class)
	})
	@XmlElementWrapper(name = "condition_list")
	private List<Condition> conditionList;
	/**
	 * <span class="en-US">Query order by columns' list</span>
	 * <span class="zh-CN">查询排序数据列列表</span>
	 */
	@Nonnull
	@XmlElement(name = "order_by")
	@XmlElementWrapper(name = "order_list")
	private List<OrderBy> orderByList;
	/**
	 * <span class="en-US">Query group by columns list</span>
	 * <span class="zh-CN">查询分组数据列列表</span>
	 */
	@Nonnull
	@XmlElement(name = "group_by")
	@XmlElementWrapper(name = "group_list")
	private List<GroupBy> groupByList;
	/**
	 * <span class="en-US">Group having condition instance list</span>
	 * <span class="zh-CN">分组筛选条件实例对象列表</span>
	 */
	@Nonnull
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
	 * <span class="en-US">Retrieve result using for update record</span>
	 * <span class="zh-CN">检索结果用于更新记录</span>
	 */
	@XmlElement(name = "for_update")
	private boolean forUpdate = Boolean.FALSE;
	/**
	 * <span class="en-US">Query record lock option</span>
	 * <span class="zh-CN">查询记录锁定选项</span>
	 */
	@XmlElement(name = "lock_option")
	private LockModeType lockOption = LockModeType.NONE;

	/**
	 * <h3 class="en-US">Constructor method for query information define</h3>
	 * <h3 class="zh-CN">查询条件信息的构造方法</h3>
	 */
	public QueryInfo() {
		this.queryFrom = new ArrayList<>();
		this.queryJoins = new ArrayList<>();
		this.itemList = new ArrayList<>();
		this.conditionList = new ArrayList<>();
		this.orderByList = new ArrayList<>();
		this.groupByList = new ArrayList<>();
		this.havingList = new ArrayList<>();
	}

	/**
	 * <h3 class="en-US">Getter method for the sheet name</h3>
	 * <h3 class="zh-CN">工作表名称的Getter方法</h3>
	 *
	 * @return <span class="en-US">Sheet name</span>
	 * <span class="zh-CN">工作表名称</span>
	 */
	public String getSheetName() {
		return this.sheetName;
	}

	/**
	 * <h3 class="en-US">Setter method for the sheet name</h3>
	 * <h3 class="zh-CN">工作表名称的Setter方法</h3>
	 *
	 * @param sheetName <span class="en-US">Sheet name</span>
	 *                  <span class="zh-CN">工作表名称</span>
	 */
	public void setSheetName(final String sheetName) {
		this.sheetName = sheetName;
	}

	/**
	 * <h3 class="en-US">Getter method for the query from information list</h3>
	 * <h3 class="zh-CN">查询来源信息列表的Getter方法</h3>
	 *
	 * @return <span class="en-US">Query from information list</span>
	 * <span class="zh-CN">查询来源信息列表</span>
	 */
	@Nonnull
	public List<QueryFrom> getQueryFrom() {
		return this.queryFrom;
	}

	/**
	 * <h3 class="en-US">Setter method for the query from information list</h3>
	 * <h3 class="zh-CN">查询来源信息列表的Setter方法</h3>
	 *
	 * @param queryFrom <span class="en-US">Query from information list</span>
	 *                  <span class="zh-CN">查询来源信息列表</span>
	 */
	public void setQueryFrom(final List<QueryFrom> queryFrom) {
		this.queryFrom = (queryFrom == null) ? new ArrayList<>() : queryFrom;
	}

	/**
	 * <h3 class="en-US">Getter method for the related query information list</h3>
	 * <h3 class="zh-CN">关联查询信息列表的Getter方法</h3>
	 *
	 * @return <span class="en-US">Related query information list</span>
	 * <span class="zh-CN">关联查询信息列表</span>
	 */
	@Nonnull
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
		this.queryJoins = (queryJoins == null) ? new ArrayList<>() : queryJoins;
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

	/**
	 * <h3 class="en-US">Getter method for the query condition instance list</h3>
	 * <h3 class="zh-CN">查询条件实例对象列表的Getter方法</h3>
	 *
	 * @return <span class="en-US">Query condition instance list</span>
	 * <span class="zh-CN">查询条件实例对象列表</span>
	 */
	@Nonnull
	public List<Condition> getConditionList() {
		return this.conditionList;
	}

	/**
	 * <h3 class="en-US">Setter method for the query condition instance list</h3>
	 * <h3 class="zh-CN">查询条件实例对象列表的Setter方法</h3>
	 *
	 * @param conditionList <span class="en-US">Query condition instance list</span>
	 *                      <span class="zh-CN">查询条件实例对象列表</span>
	 */
	public void setConditionList(final List<Condition> conditionList) {
		this.conditionList = (conditionList == null) ? new ArrayList<>() : conditionList;
		this.conditionList.sort(SortedItem.desc());
	}

	/**
	 * <h3 class="en-US">Getter method for query order by column list</h3>
	 * <h3 class="zh-CN">查询排序数据列列表的Getter方法</h3>
	 *
	 * @return <span class="en-US">Query order by column list</span>
	 * <span class="zh-CN">查询排序数据列列表</span>
	 */
	@Nonnull
	public List<OrderBy> getOrderByList() {
		return this.orderByList;
	}

	/**
	 * <h3 class="en-US">Setter method for query order by column list</h3>
	 * <h3 class="zh-CN">查询排序数据列列表的Setter方法</h3>
	 *
	 * @param orderByList <span class="en-US">Query order by column list</span>
	 *                    <span class="zh-CN">查询排序数据列列表</span>
	 */
	public void setOrderByList(final List<OrderBy> orderByList) {
		this.orderByList = (orderByList == null) ? new ArrayList<>() : orderByList;
		this.orderByList.sort(SortedItem.desc());
	}

	/**
	 * <h3 class="en-US">Getter method for the query group by column list</h3>
	 * <h3 class="zh-CN">查询分组数据列列表的Getter方法</h3>
	 *
	 * @return <span class="en-US">Query group by columns list</span>
	 * <span class="zh-CN">查询分组数据列列表</span>
	 */
	@Nonnull
	public List<GroupBy> getGroupByList() {
		return this.groupByList;
	}

	/**
	 * <h3 class="en-US">Setter method for the query group by column list</h3>
	 * <h3 class="zh-CN">查询分组数据列列表的Setter方法</h3>
	 *
	 * @param groupByList <span class="en-US">Query group by columns list</span>
	 *                    <span class="zh-CN">查询分组数据列列表</span>
	 */
	public void setGroupByList(final List<GroupBy> groupByList) {
		this.groupByList = (groupByList == null) ? new ArrayList<>() : groupByList;
		this.groupByList.sort(SortedItem.desc());
	}

	/**
	 * <h3 class="en-US">Getter method for the group having condition instance list</h3>
	 * <h3 class="zh-CN">分组筛选条件实例对象列表的Getter方法</h3>
	 *
	 * @return <span class="en-US">Group having condition instance list</span>
	 * <span class="zh-CN">分组筛选条件实例对象列表</span>
	 */
	@Nonnull
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
		this.havingList = (havingList == null) ? new ArrayList<>() : havingList;
	}

	/**
	 * <h3 class="en-US">Getter method for the query result can cacheable</h3>
	 * <h3 class="zh-CN">查询结果可以缓存的Getter方法</h3>
	 *
	 * @return <span class="en-US">Query result can cacheable</span>
	 * <span class="zh-CN">查询结果可以缓存</span>
	 */
	public boolean isCacheables() {
		return this.cacheables;
	}

	/**
	 * <h3 class="en-US">Setter method for the query result can cacheable</h3>
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

	/**
	 * <h3 class="en-US">Getter method for the query result using for update record</h3>
	 * <h3 class="zh-CN">查询结果用于更新记录的Getter方法</h3>
	 *
	 * @return <span class="en-US">Query result using for update record</span>
	 * <span class="zh-CN">查询结果用于更新记录</span>
	 */
	public boolean isForUpdate() {
		return this.forUpdate;
	}

	/**
	 * <h3 class="en-US">Setter method for the query result using for update record</h3>
	 * <h3 class="zh-CN">查询结果用于更新记录的Setter方法</h3>
	 *
	 * @param forUpdate <span class="en-US">Query result using for update record</span>
	 *                  <span class="zh-CN">查询结果用于更新记录</span>
	 */
	public void setForUpdate(final boolean forUpdate) {
		this.forUpdate = forUpdate;
	}

	/**
	 * <h3 class="en-US">Getter method for the query record lock option</h3>
	 * <h3 class="zh-CN">查询记录锁定选项的Getter方法</h3>
	 *
	 * @return <span class="en-US">Query record lock option</span>
	 * <span class="zh-CN">查询记录锁定选项</span>
	 */
	public LockModeType getLockOption() {
		return this.lockOption;
	}

	/**
	 * <h3 class="en-US">Setter method for the query record lock option</h3>
	 * <h3 class="zh-CN">查询记录锁定选项的Setter方法</h3>
	 *
	 * @param lockOption <span class="en-US">Query record lock option</span>
	 *                   <span class="zh-CN">查询记录锁定选项</span>
	 */
	public void setLockOption(final LockModeType lockOption) {
		this.lockOption = lockOption;
	}
}
