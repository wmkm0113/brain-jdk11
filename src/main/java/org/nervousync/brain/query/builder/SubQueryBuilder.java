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

package org.nervousync.brain.query.builder;

import jakarta.annotation.Nonnull;
import org.nervousync.brain.query.condition.Condition;
import org.nervousync.brain.query.core.QueryItem;
import org.nervousync.brain.query.data.QueryData;
import org.nervousync.brain.query.join.QueryJoin;
import org.nervousync.brain.query.sort.GroupBy;
import org.nervousync.builder.AbstractBuilder;
import org.nervousync.builder.ParentBuilder;
import org.nervousync.exceptions.builder.BuilderException;

import java.util.ArrayList;
import java.util.List;

/**
 * <h2 class="en-US">Sub-query information list builder</h2>
 * <h2 class="zh-CN">子查询信息构建器</h2>
 *
 * @param <P> <span class="en-US">Parent builder generic type class</span>
 *            <span class="zh-CN">父构建器泛型类</span>
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 28, 2020 11:46:08 $
 */
public final class SubQueryBuilder<P extends ParentBuilder> extends AbstractBuilder<P, QueryData> {

	/**
	 * <span class="en-US">Data table name</span>
	 * <span class="zh-CN">数据表名</span>
	 */
	private final String tableName;
	/**
	 * <span class="en-US">Query item instance list</span>
	 * <span class="zh-CN">查询项目实例对象列表</span>
	 */
	private final List<QueryItem> itemList;
	/**
	 * <span class="en-US">Related query information list</span>
	 * <span class="zh-CN">关联查询信息列表</span>
	 */
	private final List<QueryJoin> queryJoins;
	/**
	 * <span class="en-US">Query condition instance list</span>
	 * <span class="zh-CN">查询条件实例对象列表</span>
	 */
	private final List<Condition> conditionList;
	/**
	 * <span class="en-US">Identify key</span>
	 * <span class="zh-CN">分组识别代码列表</span>
	 */
	private final List<GroupBy> groupByList;
	/**
	 * <span class="en-US">Group having condition instance list</span>
	 * <span class="zh-CN">分组筛选条件实例对象列表</span>
	 */
	private final List<Condition> havingList;

	/**
	 * <h3 class="en-US">Constructor method for the sub-query information list builder</h3>
	 * <h3 class="zh-CN">子查询信息构建器的构造函数</h3>
	 *
	 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
	 *                      <span class="zh-CN">父构建器实例对象</span>
	 * @param tableName     <span class="en-US">Data table name</span>
	 *                      <span class="zh-CN">数据表名</span>
	 */
	SubQueryBuilder(final P parentBuilder, @Nonnull final String tableName) {
		super(parentBuilder);
		this.tableName = tableName;
		this.itemList = new ArrayList<>();
		this.queryJoins = new ArrayList<>();
		this.conditionList = new ArrayList<>();
		this.groupByList = new ArrayList<>();
		this.havingList = new ArrayList<>();
	}

	/**
	 * <h3 class="en-US">Query items information list builder</h3>
	 * <h3 class="zh-CN">查询项目列表构建器</h3>
	 *
	 * @return <span class="en-US">Query items information list builder instance object</span>
	 * <span class="zh-CN">查询项目列表构建器实例对象</span>
	 */
	public ItemsBuilder<SubQueryBuilder<P>> items() {
		return new ItemsBuilder<>(this);
	}

	/**
	 * <h3 class="en-US">Query joins information lists builder</h3>
	 * <h3 class="zh-CN">查询关联信息列表构建器构建器</h3>
	 *
	 * @return <span class="en-US">Query joins information lists builder instance object</span>
	 * <span class="zh-CN">查询关联信息列表构建器构建器实例对象</span>
	 */
	public JoinsBuilder<SubQueryBuilder<P>> joins() {
		return new JoinsBuilder<>(this);
	}

	/**
	 * <h3 class="en-US">Query conditions information builder</h3>
	 * <h3 class="zh-CN">查询条件组构建器</h3>
	 *
	 * @return <span class="en-US">Query conditions information builder instance object</span>
	 * <span class="zh-CN">查询条件组构建器实例对象</span>
	 */
	public ConditionsBuilder<SubQueryBuilder<P>> where() {
		return new ConditionsBuilder<>(this, Boolean.FALSE);
	}

	/**
	 * <h3 class="en-US">Group by data list builder</h3>
	 * <h3 class="zh-CN">分组数据列构建器</h3>
	 *
	 * @return <span class="en-US">Group by data list builder instance object</span>
	 * <span class="zh-CN">分组数据列构建器实例对象</span>
	 */
	public SortsBuilder.GroupItemsBuilder<SubQueryBuilder<P>> groups() {
		return new SortsBuilder.GroupItemsBuilder<>(this);
	}

	/**
	 * <h3 class="en-US">Having conditions information builder</h3>
	 * <h3 class="zh-CN">Having条件组构建器</h3>
	 *
	 * @return <span class="en-US">Having conditions information builder instance object</span>
	 * <span class="zh-CN">Having条件组构建器实例对象</span>
	 */
	public ConditionsBuilder<SubQueryBuilder<P>> having() {
		return new ConditionsBuilder<>(this, Boolean.TRUE);
	}

	@Override
	public void confirm(final Object object) {
		if (object instanceof ConditionsBuilder.Conditions) {
			ConditionsBuilder.Conditions conditions = (ConditionsBuilder.Conditions) object;
			if (conditions.isHaving()) {
				this.havingList.addAll(conditions.getConditions());
			} else {
				this.conditionList.addAll(conditions.getConditions());
			}
		} else if (object instanceof ItemsBuilder.Items) {
			this.itemList.clear();
			this.itemList.addAll(((ItemsBuilder.Items) object).getItemList());
		} else if (object instanceof JoinsBuilder.Joins) {
			this.queryJoins.clear();
			this.queryJoins.addAll(((JoinsBuilder.Joins) object).getJoinList());
		} else if (object instanceof SortsBuilder.GroupByItems) {
			this.groupByList.clear();
			this.groupByList.addAll(((SortsBuilder.GroupByItems) object).getItemList());
		}
	}

	@Override
	public QueryData build() throws BuilderException {
		QueryData queryData = new QueryData();
		queryData.setTableName(this.tableName);
		queryData.setItemList(this.itemList);
		queryData.setQueryJoins(this.queryJoins);
		queryData.setConditionList(this.conditionList);
		queryData.setGroupByList(this.groupByList);
		queryData.setHavingList(this.havingList);
		return queryData;
	}
}
