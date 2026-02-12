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
import jakarta.persistence.LockModeType;
import org.intellij.lang.annotations.MagicConstant;
import org.nervousync.brain.exceptions.sql.MultilingualSQLException;
import org.nervousync.brain.query.QueryInfo;
import org.nervousync.brain.query.condition.Condition;
import org.nervousync.brain.query.core.QueryFrom;
import org.nervousync.brain.query.core.QueryItem;
import org.nervousync.brain.query.from.FromSubQuery;
import org.nervousync.brain.query.from.FromTable;
import org.nervousync.brain.query.sort.GroupBy;
import org.nervousync.brain.query.sort.OrderBy;
import org.nervousync.brain.query.join.QueryJoin;
import org.nervousync.brain.query.subqueries.TableSubQuery;
import org.nervousync.builder.Builder;
import org.nervousync.builder.ParentBuilder;
import org.nervousync.commons.Globals;
import org.nervousync.exceptions.builder.BuilderException;
import org.nervousync.utils.core.StringUtils;
import org.nervousync.utils.logger.LoggerUtils;

import java.sql.SQLException;
import java.util.*;

/**
 * <h2 class="en-US">Query information builder</h2>
 * <h2 class="zh-CN">查询信息构建器</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 28, 2020 11:46:08 $
 */
@SuppressWarnings("unused")
public final class BrainQueryBuilder extends ParentBuilder implements Builder<QueryInfo> {

	/**
	 * <span class="en-US">Logger instance</span>
	 * <span class="zh-CN">日志实例</span>
	 */
	private static final LoggerUtils.Logger LOGGER = LoggerUtils.getLogger(BrainQueryBuilder.class);

	/**
	 * <span class="en-US">Sheet name</span>
	 * <span class="zh-CN">工作表名称</span>
	 */
	private String sheetName;
	/**
	 * <span class="en-US">Query from information list</span>
	 * <span class="zh-CN">查询来源信息列表</span>
	 */
	private QueryFrom queryFrom = null;
	/**
	 * <span class="en-US">Related query information list</span>
	 * <span class="zh-CN">关联查询信息列表</span>
	 */
	private final List<QueryJoin> queryJoins;
	/**
	 * <span class="en-US">Query item instance list</span>
	 * <span class="zh-CN">查询项目实例对象列表</span>
	 */
	private final List<QueryItem> itemList;
	/**
	 * <span class="en-US">Query condition instance list</span>
	 * <span class="zh-CN">查询条件实例对象列表</span>
	 */
	private final List<Condition> conditionList;
	/**
	 * <span class="en-US">Query order by column list</span>
	 * <span class="zh-CN">查询排序数据列列表</span>
	 */
	private final List<OrderBy> orderByList;
	/**
	 * <span class="en-US">Query group by columns list</span>
	 * <span class="zh-CN">查询分组数据列列表</span>
	 */
	private final List<GroupBy> groupByList;
	/**
	 * <span class="en-US">Group having condition instance list</span>
	 * <span class="zh-CN">分组筛选条件实例对象列表</span>
	 */
	private final List<Condition> havingList;
	/**
	 * <span class="en-US">Query result can cacheable</span>
	 * <span class="zh-CN">查询结果可以缓存</span>
	 */
	private boolean cacheables = Boolean.FALSE;
	/**
	 * <span class="en-US">Current page number</span>
	 * <span class="zh-CN">当前页数</span>
	 */
	private int pageNo = Globals.DEFAULT_VALUE_INT;
	/**
	 * <span class="en-US">Page limit records count</span>
	 * <span class="zh-CN">每页的记录数</span>
	 */
	private int pageLimit = Globals.DEFAULT_VALUE_INT;
	/**
	 * <span class="en-US">Query result using for update record</span>
	 * <span class="zh-CN">查询结果用于更新记录</span>
	 */
	private boolean forUpdate = Boolean.FALSE;
	/**
	 * <span class="en-US">Query record lock option</span>
	 * <span class="zh-CN">查询记录锁定选项</span>
	 */
	private LockModeType lockOption = LockModeType.NONE;

	/**
	 * <h3 class="en-US">Constructor method for query information builder</h3>
	 * <h3 class="zh-CN">查询计划构建器的构造方法</h3>
	 */
	public BrainQueryBuilder() {
		this.queryJoins = new ArrayList<>();
		this.itemList = new ArrayList<>();
		this.conditionList = new ArrayList<>();
		this.orderByList = new ArrayList<>();
		this.groupByList = new ArrayList<>();
		this.havingList = new ArrayList<>();
	}

	/**
	 * <h3 class="en-US">Setting the sheet name value</h3>
	 * <h3 class="zh-CN">设置工作表名称</h3>
	 *
	 * @param sheetName <span class="en-US">Sheet name</span>
	 *                  <span class="zh-CN">工作表名称</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder sheetName(final String sheetName) {
		this.sheetName = sheetName;
		return this;
	}

	/**
	 * <h3 class="en-US">Query items information list builder</h3>
	 * <h3 class="zh-CN">查询项目列表构建器</h3>
	 *
	 * @return <span class="en-US">Query items information list builder instance object</span>
	 * <span class="zh-CN">查询项目列表构建器实例对象</span>
	 */
	public ItemsBuilder<BrainQueryBuilder> items() {
		return new ItemsBuilder<>(this, this.itemList);
	}

	/**
	 * <h3 class="en-US">Query from table information builder</h3>
	 * <h3 class="zh-CN">查询来源数据表信息构建器</h3>
	 *
	 * @param tableName <span class="en-US">Data table name</span>
	 *                  <span class="zh-CN">数据表名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder fromTable(@Nonnull final String tableName) {
		return this.fromTable(tableName, Globals.DEFAULT_VALUE_STRING);
	}

	/**
	 * <h3 class="en-US">Query from table information builder</h3>
	 * <h3 class="zh-CN">查询来源数据表信息构建器</h3>
	 *
	 * @param tableName <span class="en-US">Data table name</span>
	 *                  <span class="zh-CN">数据表名</span>
	 * @param aliasName <span class="en-US">Alias name</span>
	 *                  <span class="zh-CN">别名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder fromTable(@Nonnull final String tableName, final String aliasName) {
		this.queryFrom = new FromTable();
		((FromTable) this.queryFrom).setTableName(tableName);
		if (StringUtils.notBlank(aliasName)) {
			this.queryFrom.setAliasName(aliasName);
		}
		return this;
	}

	/**
	 * <h3 class="en-US">Query from table sub-query information builder</h3>
	 * <h3 class="zh-CN">查询来源子查询信息构建器</h3>
	 *
	 * @param aliasName <span class="en-US">Alias name</span>
	 *                  <span class="zh-CN">别名</span>
	 * @return <span class="en-US">Query from information list builder instance object</span>
	 * <span class="zh-CN">查询来源信息列表构建器实例对象</span>
	 */
	public SubQueryBuilder.TableSubQueryBuilder<BrainQueryBuilder> fromSubQuery(@Nonnull final String aliasName) {
		this.queryFrom = new FromSubQuery();
		this.queryFrom.setAliasName(aliasName);
		return new SubQueryBuilder.TableSubQueryBuilder<>(this);
	}

	/**
	 * <h3 class="en-US">Query joins information lists builder</h3>
	 * <h3 class="zh-CN">查询关联信息列表构建器构建器</h3>
	 *
	 * @return <span class="en-US">Query joins information lists builder instance object</span>
	 * <span class="zh-CN">查询关联信息列表构建器构建器实例对象</span>
	 */
	public JoinsBuilder<BrainQueryBuilder> joins() {
		return new JoinsBuilder<>(this, this.queryJoins);
	}

	/**
	 * <h3 class="en-US">Query conditions information builder</h3>
	 * <h3 class="zh-CN">查询条件组构建器</h3>
	 *
	 * @return <span class="en-US">Query conditions information builder instance object</span>
	 * <span class="zh-CN">查询条件组构建器实例对象</span>
	 */
	public ConditionsBuilder<BrainQueryBuilder> where() {
		return new ConditionsBuilder<>(this, Boolean.FALSE, this.conditionList);
	}

	/**
	 * <h3 class="en-US">Group by data list builder</h3>
	 * <h3 class="zh-CN">分组数据列构建器</h3>
	 *
	 * @return <span class="en-US">Group by data list builder instance object</span>
	 * <span class="zh-CN">分组数据列构建器实例对象</span>
	 */
	public SortsBuilder.GroupItemsBuilder<BrainQueryBuilder> groups() {
		return new SortsBuilder.GroupItemsBuilder<>(this, this.groupByList);
	}

	/**
	 * <h3 class="en-US">Having conditions information builder</h3>
	 * <h3 class="zh-CN">Having条件组构建器</h3>
	 *
	 * @return <span class="en-US">Having conditions information builder instance object</span>
	 * <span class="zh-CN">Having条件组构建器实例对象</span>
	 */
	public ConditionsBuilder<BrainQueryBuilder> having() {
		return new ConditionsBuilder<>(this, Boolean.TRUE, this.havingList);
	}

	/**
	 * <h3 class="en-US">Order by data list builder</h3>
	 * <h3 class="zh-CN">排序数据列构建器</h3>
	 *
	 * @return <span class="en-US">Order by data list builder instance object</span>
	 * <span class="zh-CN">排序数据列构建器实例对象</span>
	 */
	public SortsBuilder.OrderItemsBuilder<BrainQueryBuilder> orders() {
		return new SortsBuilder.OrderItemsBuilder<>(this, this.orderByList);
	}

	/**
	 * <h3 class="en-US">Setting for the query result can cacheable</h3>
	 * <h3 class="zh-CN">设置查询结果可以缓存</h3>
	 *
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder useCache() {
		this.cacheables = Boolean.TRUE;
		return this;
	}

	/**
	 * <h3 class="en-US">Setting for pager information</h3>
	 * <h3 class="zh-CN">设置分页信息</h3>
	 *
	 * @param pageNo    <span class="en-US">Current page number</span>
	 *                  <span class="zh-CN">当前页数</span>
	 * @param pageLimit <span class="en-US">Page limit records count</span>
	 *                  <span class="zh-CN">每页的记录数</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws SQLException <span class="en-US">If the configuration information is invalid</span>
	 *                      <span class="zh-CN">如果配置信息错误</span>
	 */
	public BrainQueryBuilder pager(final int pageNo, final int pageLimit) throws SQLException {
		if (pageNo <= Globals.INITIALIZE_INT_VALUE || pageLimit <= Globals.INITIALIZE_INT_VALUE) {
			throw new MultilingualSQLException(0x00DB00010013L, pageNo, pageLimit);
		}
		this.pageNo = pageNo;
		this.pageLimit = pageLimit;
		return this;
	}

	/**
	 * <h3 class="en-US">Setting for the query result will use for update records</h3>
	 * <h3 class="zh-CN">设置查询结果用于更新数据</h3>
	 *
	 * @param lockOption <span class="en-US">Query record lock option</span>
	 *                   <span class="zh-CN">查询记录锁定选项</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder forUpdate(@MagicConstant(valuesFromClass = LockModeType.class) final LockModeType lockOption) {
		this.forUpdate = Boolean.TRUE;
		this.lockOption = lockOption;
		return this;
	}

	@Override
	public QueryInfo build() throws BuilderException {
		if (this.itemList.isEmpty()) {
			throw new BuilderException(0x00DB00000042L);
		}
		QueryInfo queryInfo = new QueryInfo();
		queryInfo.setSheetName(this.sheetName);
		queryInfo.setQueryFrom(this.queryFrom);
		queryInfo.setQueryJoins(this.queryJoins);
		queryInfo.setItemList(this.itemList);
		queryInfo.setConditionList(this.conditionList);
		queryInfo.setOrderByList(this.orderByList);
		queryInfo.setGroupByList(this.groupByList);
		queryInfo.setOrderByList(this.orderByList);
		queryInfo.setHavingList(this.havingList);
		queryInfo.setCacheables(this.cacheables);
		queryInfo.setPageNo(this.pageNo);
		queryInfo.setPageLimit(this.pageLimit);
		queryInfo.setForUpdate(this.forUpdate);
		queryInfo.setLockOption(this.lockOption);
		return queryInfo;
	}

	@Override
	public void confirm(final Object object) {
		if (object instanceof TableSubQuery) {
			if (this.queryFrom instanceof FromSubQuery) {
				((FromSubQuery) this.queryFrom).setQueryData((TableSubQuery) object);
			}
		} else if (object instanceof ConditionsBuilder.Conditions) {
			ConditionsBuilder.Conditions conditions = (ConditionsBuilder.Conditions) object;
			if (conditions.isHaving()) {
				this.havingList.clear();
				this.havingList.addAll(conditions.getConditions());
			} else {
				this.conditionList.clear();
				this.conditionList.addAll(conditions.getConditions());
			}
		} else if (object instanceof ItemsBuilder.Items) {
			this.itemList.clear();
			this.itemList.addAll(((ItemsBuilder.Items) object).getItemList());
		} else if (object instanceof QueryFrom) {
			this.queryFrom = (QueryFrom) object;
		} else if (object instanceof JoinsBuilder.Joins) {
			this.queryJoins.clear();
			this.queryJoins.addAll(((JoinsBuilder.Joins) object).getJoinList());
		} else if (object instanceof SortsBuilder.GroupByItems) {
			this.groupByList.clear();
			this.groupByList.addAll(((SortsBuilder.GroupByItems) object).getItemList());
		} else if (object instanceof SortsBuilder.OrderByItems) {
			this.orderByList.clear();
			this.orderByList.addAll(((SortsBuilder.OrderByItems) object).getItemList());
		}
	}
}
