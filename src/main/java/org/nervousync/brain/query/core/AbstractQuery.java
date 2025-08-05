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

package org.nervousync.brain.query.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nonnull;
import jakarta.xml.bind.annotation.*;
import org.nervousync.annotations.beans.OutputConfig;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.brain.enumerations.query.QueryType;
import org.nervousync.brain.query.QueryInfo;
import org.nervousync.brain.query.condition.Condition;
import org.nervousync.brain.query.condition.impl.ColumnCondition;
import org.nervousync.brain.query.condition.impl.GroupCondition;
import org.nervousync.brain.query.from.FromSubQuery;
import org.nervousync.brain.query.from.FromTable;
import org.nervousync.brain.query.join.QueryJoin;
import org.nervousync.brain.query.join.SubQueryJoin;
import org.nervousync.brain.query.join.TableQueryJoin;
import org.nervousync.brain.query.sort.GroupBy;
import org.nervousync.brain.query.subqueries.NestedTableSubQuery;
import org.nervousync.brain.query.subqueries.ScalarSubQuery;
import org.nervousync.brain.query.subqueries.TableSubQuery;
import org.nervousync.utils.StringUtils;

import java.util.List;

/**
 * <h2 class="en-US">Abstract class of the query defines</h2>
 * <h2 class="zh-CN">查询抽象类定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 9, 2020 18:19:42 $
 */
@XmlSeeAlso({QueryInfo.class, NestedTableSubQuery.class, ScalarSubQuery.class, TableSubQuery.class})
@XmlTransient
@XmlAccessorType(XmlAccessType.NONE)
@OutputConfig(formatted = true, defaultType = StringUtils.StringType.XML, types = {StringUtils.StringType.JSON, StringUtils.StringType.YAML})
public abstract class AbstractQuery extends BeanObject {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = -3561111209756721789L;

	/**
	 * <span class="en-US">Sub-query type enumeration value</span>
	 * <span class="zh-CN">子查询类型枚举值</span>
	 */
	@JsonIgnore
	private final QueryType queryType;
	/**
	 * <span class="en-US">Query from information</span>
	 * <span class="zh-CN">查询来源信息</span>
	 */
	@XmlElements({
			@XmlElement(name = "from_sub_query", type = FromSubQuery.class, namespace = "https://nervousync.org/schemas/brain"),
			@XmlElement(name = "from_table", type = FromTable.class, namespace = "https://nervousync.org/schemas/brain")
	})
	private QueryFrom queryFrom;
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
	@XmlElement(name = "group_by", type = GroupBy.class, namespace = "https://nervousync.org/schemas/brain")
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
	 * <h3 class="en-US">Constructor method for the abstract class of sub-query defines</h3>
	 * <h3 class="zh-CN">子查询抽象类定义的构造方法</h3>
	 *
	 * @param queryType <span class="en-US">Sub-query type enumeration value</span>
	 *                  <span class="zh-CN">子查询类型枚举值</span>
	 */
	protected AbstractQuery(@Nonnull final QueryType queryType) {
		this.queryType = queryType;
	}

	/**
	 * <h3 class="en-US">Getter method for the sub-query type enumeration value</h3>
	 * <h3 class="zh-CN">子查询类型枚举值的Getter方法</h3>
	 *
	 * @return <span class="en-US">Sub-query type enumeration value</span>
	 * <span class="zh-CN">子查询类型枚举值</span>
	 */
	public QueryType getQueryType() {
		return this.queryType;
	}

	/**
	 * <h3 class="en-US">Getter method for the query from information</h3>
	 * <h3 class="zh-CN">查询来源信息的Getter方法</h3>
	 *
	 * @return <span class="en-US">Query from information</span>
	 * <span class="zh-CN">查询来源信息</span>
	 */
	@Nonnull
	public QueryFrom getQueryFrom() {
		return this.queryFrom;
	}

	/**
	 * <h3 class="en-US">Setter method for the query from information</h3>
	 * <h3 class="zh-CN">查询来源信息的Setter方法</h3>
	 *
	 * @param queryFrom <span class="en-US">Query from information</span>
	 *                  <span class="zh-CN">查询来源信息</span>
	 */
	public void setQueryFrom(final QueryFrom queryFrom) {
		this.queryFrom = queryFrom;
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
	 *                      <span class="zh-CN">查询条件实例对象列表</span>
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
	 *                    <span class="zh-CN">分组识别代码列表</span>
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
