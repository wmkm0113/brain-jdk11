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
import org.nervousync.brain.query.core.AbstractItem;
import org.nervousync.brain.query.item.ColumnItem;
import org.nervousync.brain.query.item.FunctionItem;
import org.nervousync.brain.query.item.QueryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * <h2 class="en-US">Sub-query define</h2>
 * <h2 class="zh-CN">子查询定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 9, 2020 18:19:42 $
 */
@XmlType(name = "query_data", namespace = "https://nervousync.org/schemas/database")
@XmlRootElement(name = "query_data", namespace = "https://nervousync.org/schemas/database")
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
	 * <span class="en-US">Query item instance</span>
	 * <span class="zh-CN">查询项目实例对象</span>
	 */
	@XmlElementRefs({
			@XmlElementRef(name = "column_item", type = ColumnItem.class, namespace = "https://nervousync.org/schemas/database"),
			@XmlElementRef(name = "function_item", type = FunctionItem.class, namespace = "https://nervousync.org/schemas/database"),
			@XmlElementRef(name = "query_item", type = QueryItem.class, namespace = "https://nervousync.org/schemas/database"),
	})
	private AbstractItem queryItem;
	/**
	 * <span class="en-US">Query condition instance list</span>
	 * <span class="zh-CN">查询条件实例对象列表</span>
	 */
    @XmlElements({
            @XmlElement(name = "column_condition", type = ColumnCondition.class, namespace = "https://nervousync.org/schemas/database"),
            @XmlElement(name = "group_condition", type = GroupCondition.class, namespace = "https://nervousync.org/schemas/database")
    })
	@XmlElementWrapper(name = "conditions")
	private List<Condition> conditions;
	/**
	 * <span class="en-US">Identify key</span>
	 * <span class="zh-CN">分组识别代码列表</span>
	 */
	@XmlElement(name = "identify_key")
	@XmlElementWrapper(name = "group_by")
	private List<String> groupBy;
	/**
	 * <span class="en-US">Group having condition instance list</span>
	 * <span class="zh-CN">分组筛选条件实例对象列表</span>
	 */
    @XmlElements({
            @XmlElement(name = "column_condition", type = ColumnCondition.class, namespace = "https://nervousync.org/schemas/database"),
            @XmlElement(name = "group_condition", type = GroupCondition.class, namespace = "https://nervousync.org/schemas/database")
    })
	@XmlElementWrapper(name = "having_list")
	private List<Condition> havingList;

	/**
	 * <h3 class="en-US">Constructor method for sub-query define</h3>
	 * <h3 class="zh-CN">子查询定义的构造方法</h3>
	 */
	public QueryData() {
		this.conditions = new ArrayList<>();
		this.groupBy = new ArrayList<>();
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
	 * <h3 class="en-US">Getter method for query item instance</h3>
	 * <h3 class="zh-CN">查询项目实例对象的Getter方法</h3>
	 *
	 * @return <span class="en-US">Query item instance</span>
	 * <span class="zh-CN">查询项目实例对象</span>
	 */
	public AbstractItem getQueryItem() {
		return this.queryItem;
	}

	/**
	 * <h3 class="en-US">Setter method for query item instance</h3>
	 * <h3 class="zh-CN">查询项目实例对象的Setter方法</h3>
	 *
	 * @param queryItem <span class="en-US">Query item instance</span>
	 *                  <span class="zh-CN">查询项目实例对象</span>
	 */
	public void setQueryItem(final AbstractItem queryItem) {
		this.queryItem = queryItem;
	}

	/**
	 * <h3 class="en-US">Getter method for query condition instance list</h3>
	 * <h3 class="zh-CN">查询条件实例对象列表的Getter方法</h3>
	 *
	 * @return <span class="en-US">Query condition instance list</span>
	 * <span class="zh-CN">查询条件实例对象列表</span>
	 */
	public List<Condition> getConditions() {
		return this.conditions;
	}

	/**
	 * <h3 class="en-US">Setter method for query condition instance list</h3>
	 * <h3 class="zh-CN">查询条件实例对象列表的Setter方法</h3>
	 *
	 * @param conditions <span class="en-US">Query condition instance list</span>
	 *                   <span class="zh-CN">查询条件实例对象列表</span>
	 */
	public void setConditions(final List<Condition> conditions) {
		this.conditions = conditions;
	}

	/**
	 * <h3 class="en-US">Getter method for group identify key</h3>
	 * <h3 class="zh-CN">分组识别代码列表的Getter方法</h3>
	 *
	 * @return <span class="en-US">Group identify key</span>
	 * <span class="zh-CN">分组识别代码列表</span>
	 */
	public List<String> getGroupBy() {
		return this.groupBy;
	}

	/**
	 * <h3 class="en-US">Setter method for group identify key</h3>
	 * <h3 class="zh-CN">分组识别代码列表的Setter方法</h3>
	 *
	 * @param groupBy <span class="en-US">Group identify key</span>
	 *                <span class="zh-CN">分组识别代码列表</span>
	 */
	public void setGroupBy(final List<String> groupBy) {
		this.groupBy = groupBy;
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
}
