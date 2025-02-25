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
import org.nervousync.brain.enumerations.query.ConditionCode;
import org.nervousync.brain.enumerations.query.ItemType;
import org.nervousync.brain.enumerations.query.JoinType;
import org.nervousync.brain.enumerations.query.OrderType;
import org.nervousync.brain.exceptions.sql.MultilingualSQLException;
import org.nervousync.brain.query.QueryInfo;
import org.nervousync.brain.query.condition.Condition;
import org.nervousync.brain.query.core.AbstractItem;
import org.nervousync.brain.query.data.QueryData;
import org.nervousync.brain.query.filter.GroupBy;
import org.nervousync.brain.query.filter.OrderBy;
import org.nervousync.brain.query.item.ColumnItem;
import org.nervousync.brain.query.join.JoinInfo;
import org.nervousync.brain.query.join.QueryJoin;
import org.nervousync.brain.query.param.AbstractParameter;
import org.nervousync.brain.source.BrainDataSource;
import org.nervousync.builder.Builder;
import org.nervousync.commons.Globals;
import org.nervousync.enumerations.core.ConnectionCode;
import org.nervousync.utils.ObjectUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * <h2 class="en-US">Query information builder</h2>
 * <h2 class="zh-CN">查询信息构建器</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 28, 2020 11:46:08 $
 */
public final class BrainQueryBuilder implements Builder<QueryInfo> {

	/**
	 * <span class="en-US">Data table name</span>
	 * <span class="zh-CN">数据表名</span>
	 */
	private final String tableName;
	/**
	 * <span class="en-US">Data table alias name</span>
	 * <span class="zh-CN">数据表别名</span>
	 */
	private final String aliasName;
	/**
	 * <span class="en-US">Related query information list</span>
	 * <span class="zh-CN">关联查询信息列表</span>
	 */
	private final List<QueryJoin> queryJoins;
	/**
	 * <span class="en-US">Query item instance list</span>
	 * <span class="zh-CN">查询项目实例对象列表</span>
	 */
	private final List<AbstractItem> itemList;
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
	 * <h3 class="en-US">Private constructor method for query plan builder</h3>
	 * <h3 class="zh-CN">查询计划构建器的私有构造方法</h3>
	 *
	 * @param tableName <span class="en-US">Data table name</span>
	 *                  <span class="zh-CN">数据表名</span>
	 * @param aliasName <span class="en-US">Data table alias name</span>
	 *                  <span class="zh-CN">数据表别名</span>
	 */
	private BrainQueryBuilder(final String tableName, final String aliasName) {
		this.tableName = tableName;
		this.aliasName = aliasName;
		this.queryJoins = new ArrayList<>();
		this.itemList = new ArrayList<>();
		this.conditionList = new ArrayList<>();
		this.orderByList = new ArrayList<>();
		this.groupByList = new ArrayList<>();
		this.havingList = new ArrayList<>();
	}

	/**
	 * <h3 class="en-US">Static method used to initialize query information builder</h3>
	 * <h3 class="zh-CN">静态方法用于初始化查询信息构建器</h3>
	 *
	 * @param tableName <span class="en-US">Data table name</span>
	 *                  <span class="zh-CN">数据表名</span>
	 * @return <span class="en-US">Query builder instance object</span>
	 * <span class="zh-CN">查询构建器实例对象</span>
	 */
	public static BrainQueryBuilder newBuilder(final String tableName) {
		return newBuilder(tableName, Globals.DEFAULT_VALUE_STRING);
	}

	/**
	 * <h3 class="en-US">Static method used to initialize query information builder</h3>
	 * <h3 class="zh-CN">静态方法用于初始化查询信息构建器</h3>
	 *
	 * @param tableName <span class="en-US">Data table name</span>
	 *                  <span class="zh-CN">数据表名</span>
	 * @param aliasName <span class="en-US">Data table alias name</span>
	 *                  <span class="zh-CN">数据表别名</span>
	 * @return <span class="en-US">Query builder instance object</span>
	 * <span class="zh-CN">查询构建器实例对象</span>
	 */
	public static BrainQueryBuilder newBuilder(final String tableName, final String aliasName) {
		return new BrainQueryBuilder(tableName, aliasName);
	}

	/**
	 * <h3 class="en-US">Add data table join information</h3>
	 * <h3 class="zh-CN">添加数据表关联信息</h3>
	 *
	 * @param joinType    <span class="en-US">Join type</span>
	 *                    <span class="zh-CN">关联类型</span>
	 * @param joinTable   <span class="en-US">Join table name</span>
	 *                    <span class="zh-CN">关联数据表名</span>
	 * @param joinInfos   <span class="en-US">Join column information list</span>
	 *                    <span class="zh-CN">关联列信息列表</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws SQLException <span class="en-US">If the query join information already exists</span>
	 *                      <span class="zh-CN">如果关联信息已存在</span>
	 */
	public BrainQueryBuilder joinTable(final JoinType joinType, final String joinTable, final List<JoinInfo> joinInfos)
			throws SQLException {
		return this.joinTable(joinType, joinTable, Globals.DEFAULT_VALUE_STRING, joinInfos);
	}

	/**
	 * <h3 class="en-US">Add data table join information</h3>
	 * <h3 class="zh-CN">添加数据表关联信息</h3>
	 *
	 * @param joinType    <span class="en-US">Join type</span>
	 *                    <span class="zh-CN">关联类型</span>
	 * @param joinTable   <span class="en-US">Join table name</span>
	 *                    <span class="zh-CN">关联数据表名</span>
	 * @param aliasName   <span class="en-US">Join table alias name</span>
	 *                    <span class="zh-CN">关联数据表别名</span>
	 * @param joinInfos   <span class="en-US">Join column information list</span>
	 *                    <span class="zh-CN">关联列信息列表</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws SQLException <span class="en-US">If the query join information already exists</span>
	 *                      <span class="zh-CN">如果关联信息已存在</span>
	 */
	public BrainQueryBuilder joinTable(final JoinType joinType, final String joinTable, final String aliasName,
	                                   final List<JoinInfo> joinInfos)
			throws SQLException {
		if (this.queryJoins.stream().anyMatch(queryJoin ->
				ObjectUtils.nullSafeEquals(queryJoin.getRightTable(), joinTable))) {
			throw new MultilingualSQLException(0x00DB00010014L, joinTable);
		}
		this.queryJoins.add(new QueryJoin(joinTable, aliasName, joinType, joinInfos));
		return this;
	}

	/**
	 * <h3 class="en-US">Add query data column</h3>
	 * <h3 class="zh-CN">添加查询数据列</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws SQLException <span class="en-US">If the query data column already exists</span>
	 *                      <span class="zh-CN">如果查询数据列已存在</span>
	 */
	public BrainQueryBuilder queryColumn(final String tableName, final String columnName) throws SQLException {
		return this.queryColumn(tableName, columnName, Boolean.FALSE);
	}

	/**
	 * <h3 class="en-US">Add query data column</h3>
	 * <h3 class="zh-CN">添加查询数据列</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @param aliasName  <span class="en-US">Item alias name</span>
	 *                   <span class="zh-CN">查询项别名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws SQLException <span class="en-US">If the query data column already exists</span>
	 *                      <span class="zh-CN">如果查询数据列已存在</span>
	 */
	public BrainQueryBuilder queryColumn(final String tableName, final String columnName, final String aliasName)
			throws SQLException {
		return this.queryColumn(tableName, columnName, aliasName, Globals.DEFAULT_VALUE_INT);
	}

	/**
	 * <h3 class="en-US">Add query data column</h3>
	 * <h3 class="zh-CN">添加查询数据列</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @param aliasName  <span class="en-US">Item alias name</span>
	 *                   <span class="zh-CN">查询项别名</span>
	 * @param sortCode   <span class="en-US">Sort code</span>
	 *                   <span class="zh-CN">排序代码</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws SQLException <span class="en-US">If the query data column already exists</span>
	 *                      <span class="zh-CN">如果查询数据列已存在</span>
	 */
	public BrainQueryBuilder queryColumn(final String tableName, final String columnName,
	                                     final String aliasName, final int sortCode) throws SQLException {
		return this.queryColumn(tableName, columnName, Boolean.FALSE, aliasName, sortCode);
	}

	/**
	 * <h3 class="en-US">Add query data column</h3>
	 * <h3 class="zh-CN">添加查询数据列</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @param distinct   <span class="en-US">Column distinct</span>
	 *                   <span class="zh-CN">数据列去重</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws SQLException <span class="en-US">If the query data column already exists</span>
	 *                      <span class="zh-CN">如果查询数据列已存在</span>
	 */
	public BrainQueryBuilder queryColumn(final String tableName, final String columnName, final boolean distinct)
			throws SQLException {
		return this.queryColumn(tableName, columnName, distinct, Globals.DEFAULT_VALUE_INT);
	}

	/**
	 * <h3 class="en-US">Add query data column</h3>
	 * <h3 class="zh-CN">添加查询数据列</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @param distinct   <span class="en-US">Column distinct</span>
	 *                   <span class="zh-CN">数据列去重</span>
	 * @param sortCode   <span class="en-US">Sort code</span>
	 *                   <span class="zh-CN">排序代码</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws SQLException <span class="en-US">If the query data column already exists</span>
	 *                      <span class="zh-CN">如果查询数据列已存在</span>
	 */
	public BrainQueryBuilder queryColumn(final String tableName, final String columnName,
	                                     final boolean distinct, final int sortCode) throws SQLException {
		return this.queryColumn(tableName, columnName, distinct, Globals.DEFAULT_VALUE_STRING, sortCode);
	}

	/**
	 * <h3 class="en-US">Add query data column</h3>
	 * <h3 class="zh-CN">添加查询数据列</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @param distinct   <span class="en-US">Column distinct</span>
	 *                   <span class="zh-CN">数据列去重</span>
	 * @param aliasName  <span class="en-US">Item alias name</span>
	 *                   <span class="zh-CN">查询项别名</span>
	 * @param sortCode   <span class="en-US">Sort code</span>
	 *                   <span class="zh-CN">排序代码</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws SQLException <span class="en-US">If the query data column already exists</span>
	 *                      <span class="zh-CN">如果查询数据列已存在</span>
	 */
	public BrainQueryBuilder queryColumn(final String tableName, final String columnName,
	                                     final boolean distinct, final String aliasName, final int sortCode)
			throws SQLException {
		for (AbstractItem item : this.itemList) {
			if (ItemType.COLUMN.equals(item.getItemType())) {
				ColumnItem columnItem = item.unwrap(ColumnItem.class);
				if (ObjectUtils.nullSafeEquals(columnItem.getTableName(), tableName)
						&& ObjectUtils.nullSafeEquals(columnItem.getColumnName(), columnName)) {
					throw new MultilingualSQLException(0x00DB00010016L, tableName, columnName);
				}
			}
		}
		this.itemList.add(AbstractItem.column(tableName, columnName, distinct, aliasName, sortCode));
		return this;
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @param matchValue <span class="en-US">Match value</span>
	 *                   <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greater(final String tableName, final String columnName, final Object matchValue) {
		return this.greater(Boolean.FALSE, tableName, columnName, matchValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param tableName   <span class="en-US">Data table name</span>
	 *                    <span class="zh-CN">数据表名</span>
	 * @param columnName  <span class="en-US">Data column name</span>
	 *                    <span class="zh-CN">数据列名</span>
	 * @param matchTable  <span class="en-US">Target data table name</span>
	 *                    <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn <span class="en-US">Target data column name</span>
	 *                    <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greater(final String tableName, final String columnName,
	                                 final String matchTable, final String matchColumn) {
		return this.greater(Boolean.FALSE, tableName, columnName,
				matchTable, matchColumn);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param functionName   <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greater(final String tableName, final String columnName, final String functionName,
	                                 final AbstractParameter<?>... functionParams) {
		return this.greater(Boolean.FALSE, tableName, columnName,
				functionName, functionParams);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @param subQuery   <span class="en-US">Sub-query instance object</span>
	 *                   <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greater(final String tableName, final String columnName, final QueryData subQuery) {
		return this.greater(Boolean.FALSE, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param tableName   <span class="en-US">Data table name</span>
	 *                    <span class="zh-CN">数据表名</span>
	 * @param columnName  <span class="en-US">Data column name</span>
	 *                    <span class="zh-CN">数据列名</span>
	 * @param matchTable  <span class="en-US">Target data table name</span>
	 *                    <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn <span class="en-US">Target data column name</span>
	 *                    <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greaterEqual(final String tableName, final String columnName,
	                                      final String matchTable, final String matchColumn) {
		return this.greaterEqual(Boolean.FALSE, tableName, columnName, matchTable, matchColumn);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param functionName   <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greaterEqual(final String tableName, final String columnName,
	                                      final String functionName, final AbstractParameter<?>... functionParams) {
		return this.greaterEqual(Boolean.FALSE, tableName, columnName, functionName, functionParams);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @param subQuery   <span class="en-US">Sub-query instance object</span>
	 *                   <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greaterEqual(final String tableName, final String columnName, final QueryData subQuery) {
		return this.greaterEqual(Boolean.FALSE, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @param matchValue <span class="en-US">Match value</span>
	 *                   <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greaterEqual(final String tableName, final String columnName, final Object matchValue) {
		return this.greaterEqual(Boolean.FALSE, tableName, columnName, matchValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param tableName   <span class="en-US">Data table name</span>
	 *                    <span class="zh-CN">数据表名</span>
	 * @param columnName  <span class="en-US">Data column name</span>
	 *                    <span class="zh-CN">数据列名</span>
	 * @param matchTable  <span class="en-US">Target data table name</span>
	 *                    <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn <span class="en-US">Target data column name</span>
	 *                    <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder less(final String tableName, final String columnName,
	                              final String matchTable, final String matchColumn) {
		return this.less(Boolean.FALSE, tableName, columnName, matchTable, matchColumn);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param functionName   <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder less(final String tableName, final String columnName,
	                              final String functionName, final AbstractParameter<?>... functionParams) {
		return this.less(Boolean.FALSE, tableName, columnName, functionName, functionParams);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @param subQuery   <span class="en-US">Sub-query instance object</span>
	 *                   <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder less(final String tableName, final String columnName, final QueryData subQuery) {
		return this.less(Boolean.FALSE, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @param matchValue <span class="en-US">Match value</span>
	 *                   <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder less(final String tableName, final String columnName, final Object matchValue) {
		return this.less(Boolean.FALSE, tableName, columnName, matchValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param tableName   <span class="en-US">Data table name</span>
	 *                    <span class="zh-CN">数据表名</span>
	 * @param columnName  <span class="en-US">Data column name</span>
	 *                    <span class="zh-CN">数据列名</span>
	 * @param matchTable  <span class="en-US">Target data table name</span>
	 *                    <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn <span class="en-US">Target data column name</span>
	 *                    <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder lessEqual(final String tableName, final String columnName,
	                                   final String matchTable, final String matchColumn) {
		return this.lessEqual(Boolean.FALSE, tableName, columnName, matchTable, matchColumn);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param functionName   <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder lessEqual(final String tableName, final String columnName, final String functionName,
	                                   final AbstractParameter<?>... functionParams) {
		return this.lessEqual(Boolean.FALSE, tableName, columnName, functionName, functionParams);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @param subQuery   <span class="en-US">Sub-query instance object</span>
	 *                   <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder lessEqual(final String tableName, final String columnName, final QueryData subQuery) {
		return this.lessEqual(Boolean.FALSE, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @param matchValue <span class="en-US">Match value</span>
	 *                   <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder lessEqual(final String tableName, final String columnName,
	                                   final Object matchValue) {
		return this.lessEqual(Boolean.FALSE, tableName, columnName, matchValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition equal to a certain value</h3>
	 * <h3 class="zh-CN">添加等于某值的查询条件</h3>
	 *
	 * @param tableName   <span class="en-US">Data table name</span>
	 *                    <span class="zh-CN">数据表名</span>
	 * @param columnName  <span class="en-US">Data column name</span>
	 *                    <span class="zh-CN">数据列名</span>
	 * @param matchTable  <span class="en-US">Target data table name</span>
	 *                    <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn <span class="en-US">Target data column name</span>
	 *                    <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder equalTo(final String tableName, final String columnName,
	                                 final String matchTable, final String matchColumn) {
		return this.equalTo(Boolean.FALSE, tableName, columnName, matchTable, matchColumn);
	}

	/**
	 * <h3 class="en-US">Add a query condition equal to a certain value</h3>
	 * <h3 class="zh-CN">添加等于某值的查询条件</h3>
	 *
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param functionName   <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder equalTo(final String tableName, final String columnName, final String functionName,
	                                 final AbstractParameter<?>... functionParams) {
		return this.equalTo(Boolean.FALSE, tableName, columnName, functionName, functionParams);
	}

	/**
	 * <h3 class="en-US">Add a query condition equal to a certain value</h3>
	 * <h3 class="zh-CN">添加等于某值的查询条件</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @param subQuery   <span class="en-US">Sub-query instance object</span>
	 *                   <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder equalTo(final String tableName, final String columnName, final QueryData subQuery) {
		return this.equalTo(Boolean.FALSE, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Add a query condition equal to a certain value</h3>
	 * <h3 class="zh-CN">添加等于某值的查询条件</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @param matchValue <span class="en-US">Match value</span>
	 *                   <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder equalTo(final String tableName, final String columnName, final Object matchValue) {
		return this.equalTo(Boolean.FALSE, tableName, columnName, matchValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition not equal to a certain value</h3>
	 * <h3 class="zh-CN">添加不等于某值的查询条件</h3>
	 *
	 * @param tableName   <span class="en-US">Data table name</span>
	 *                    <span class="zh-CN">数据表名</span>
	 * @param columnName  <span class="en-US">Data column name</span>
	 *                    <span class="zh-CN">数据列名</span>
	 * @param matchTable  <span class="en-US">Target data table name</span>
	 *                    <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn <span class="en-US">Target data column name</span>
	 *                    <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notEqual(final String tableName, final String columnName,
	                                  final String matchTable, final String matchColumn) {
		return this.notEqual(Boolean.FALSE, tableName, columnName, matchTable, matchColumn);
	}

	/**
	 * <h3 class="en-US">Add a query condition not equal to a certain value</h3>
	 * <h3 class="zh-CN">添加不等于某值的查询条件</h3>
	 *
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param functionName   <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notEqual(final String tableName, final String columnName, final String functionName,
	                                  final AbstractParameter<?>... functionParams) {
		return this.notEqual(Boolean.FALSE, tableName, columnName, functionName, functionParams);
	}

	/**
	 * <h3 class="en-US">Add a query condition not equal to a certain value</h3>
	 * <h3 class="zh-CN">添加不等于某值的查询条件</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @param subQuery   <span class="en-US">Sub-query instance object</span>
	 *                   <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notEqual(final String tableName, final String columnName, final QueryData subQuery) {
		return this.notEqual(Boolean.FALSE, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Add a query condition not equal to a certain value</h3>
	 * <h3 class="zh-CN">添加不等于某值的查询条件</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @param matchValue <span class="en-US">Match value</span>
	 *                   <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notEqual(final String tableName, final String columnName, final Object matchValue) {
		return this.notEqual(Boolean.FALSE, tableName, columnName, matchValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition between certain two values</h3>
	 * <h3 class="zh-CN">添加介于某两个值之间的查询条件</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @param beginValue <span class="en-US">Begin value</span>
	 *                   <span class="zh-CN">起始值</span>
	 * @param endValue   <span class="en-US">End value</span>
	 *                   <span class="zh-CN">终止值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder inRanges(final String tableName, final String columnName,
	                                  final Object beginValue, final Object endValue) {
		return this.inRanges(Boolean.FALSE, tableName, columnName, beginValue, endValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition not between certain two values</h3>
	 * <h3 class="zh-CN">添加不介于某两个值之间的查询条件</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @param beginValue <span class="en-US">Begin value</span>
	 *                   <span class="zh-CN">起始值</span>
	 * @param endValue   <span class="en-US">End value</span>
	 *                   <span class="zh-CN">终止值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notInRanges(final String tableName, final String columnName,
	                                     final Object beginValue, final Object endValue) {
		return this.notInRanges(Boolean.FALSE, tableName, columnName, beginValue, endValue);
	}

	/**
	 * <h3 class="en-US">Add query conditions for fuzzy matching values</h3>
	 * <h3 class="zh-CN">添加模糊匹配值的查询条件</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @param matchRule  <span class="en-US">match rule string</span>
	 *                   <span class="zh-CN">匹配规则字符串</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder like(final String tableName, final String columnName, final String matchRule) {
		return this.like(Boolean.FALSE, tableName, columnName, matchRule);
	}

	/**
	 * <h3 class="en-US">Add query conditions for not fuzzy matching values</h3>
	 * <h3 class="zh-CN">添加非模糊匹配值的查询条件</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @param matchRule  <span class="en-US">match rule string</span>
	 *                   <span class="zh-CN">匹配规则字符串</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notLike(final String tableName, final String columnName, final String matchRule) {
		return this.notLike(Boolean.FALSE, tableName, columnName, matchRule);
	}

	/**
	 * <h3 class="en-US">Add query condition with null value</h3>
	 * <h3 class="zh-CN">添加空值的查询条件</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder matchNull(final String tableName, final String columnName) {
		return this.matchNull(Boolean.FALSE, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Add query condition with not null value</h3>
	 * <h3 class="zh-CN">添加非空值的查询条件</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notNull(final String tableName, final String columnName) {
		return this.notNull(Boolean.FALSE, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Adds a query condition where the value is contained in the given data</h3>
	 * <h3 class="zh-CN">添加值包含在给定数据中的查询条件</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @param subQuery   <span class="en-US">Sub-query instance object</span>
	 *                   <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder in(final String tableName, final String columnName, final QueryData subQuery) {
		return this.in(Boolean.FALSE, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Adds a query condition where the value is contained in the given data</h3>
	 * <h3 class="zh-CN">添加值包含在给定数据中的查询条件</h3>
	 *
	 * @param tableName   <span class="en-US">Data table name</span>
	 *                    <span class="zh-CN">数据表名</span>
	 * @param columnName  <span class="en-US">Data column name</span>
	 *                    <span class="zh-CN">数据列名</span>
	 * @param matchValues <span class="en-US">Condition data array</span>
	 *                    <span class="zh-CN">匹配值数组</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder in(final String tableName, final String columnName, final Object... matchValues) {
		return this.in(Boolean.FALSE, tableName, columnName, matchValues);
	}

	/**
	 * <h3 class="en-US">Adds a query condition where the value is not contained in the given data</h3>
	 * <h3 class="zh-CN">添加值非包含在给定数据中的查询条件</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @param subQuery   <span class="en-US">Sub-query instance object</span>
	 *                   <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notIn(final String tableName, final String columnName, final QueryData subQuery) {
		return this.notIn(Boolean.FALSE, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Adds a query condition where the value is not contained in the given data</h3>
	 * <h3 class="zh-CN">添加值非包含在给定数据中的查询条件</h3>
	 *
	 * @param tableName   <span class="en-US">Data table name</span>
	 *                    <span class="zh-CN">数据表名</span>
	 * @param columnName  <span class="en-US">Data column name</span>
	 *                    <span class="zh-CN">数据列名</span>
	 * @param matchValues <span class="en-US">Condition data array</span>
	 *                    <span class="zh-CN">匹配值数组</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notIn(final String tableName, final String columnName, final Object... matchValues) {
		return this.notIn(Boolean.FALSE, tableName, columnName, matchValues);
	}

	/**
	 * <h3 class="en-US">Adds a sub-query condition</h3>
	 * <h3 class="zh-CN">添加子查询条件</h3>
	 *
	 * @param tableName    <span class="en-US">Data table name</span>
	 *                     <span class="zh-CN">数据表名</span>
	 * @param subQuery     <span class="en-US">Sub-query instance object</span>
	 *                     <span class="zh-CN">子查询实例对象</span>
	 * @param functionName <span class="en-US">Function name of sub-query</span>
	 *                     <span class="zh-CN">子查询函数名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder exists(final String tableName, final QueryData subQuery, final String functionName) {
		return this.exists(Boolean.FALSE, tableName, subQuery, functionName);
	}

	/**
	 * <h3 class="en-US">Adds a sub-query condition</h3>
	 * <h3 class="zh-CN">添加子查询条件</h3>
	 *
	 * @param tableName    <span class="en-US">Data table name</span>
	 *                     <span class="zh-CN">数据表名</span>
	 * @param subQuery     <span class="en-US">Sub-query instance object</span>
	 *                     <span class="zh-CN">子查询实例对象</span>
	 * @param functionName <span class="en-US">Function name of sub-query</span>
	 *                     <span class="zh-CN">子查询函数名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notExists(final String tableName, final QueryData subQuery, final String functionName) {
		return this.notExists(Boolean.FALSE, tableName, subQuery, functionName);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param matchValue     <span class="en-US">Match value</span>
	 *                       <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greater(final ConnectionCode connectionCode, final String tableName,
	                                 final String columnName, final Object matchValue) {
		return this.greater(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName, matchValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param matchTable     <span class="en-US">Target data table name</span>
	 *                       <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn    <span class="en-US">Target data column name</span>
	 *                       <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greater(final ConnectionCode connectionCode,
	                                 final String tableName, final String columnName,
	                                 final String matchTable, final String matchColumn) {
		return this.greater(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName,
				matchTable, matchColumn);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param functionName   <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greater(final ConnectionCode connectionCode, final String tableName,
	                                 final String columnName, final String functionName,
	                                 final AbstractParameter<?>... functionParams) {
		return this.greater(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName,
				functionName, functionParams);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greater(final ConnectionCode connectionCode, final String tableName,
	                                 final String columnName, final QueryData subQuery) {
		return this.greater(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param matchTable     <span class="en-US">Target data table name</span>
	 *                       <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn    <span class="en-US">Target data column name</span>
	 *                       <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greaterEqual(final ConnectionCode connectionCode,
	                                      final String tableName, final String columnName,
	                                      final String matchTable, final String matchColumn) {
		return this.greaterEqual(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName, matchTable, matchColumn);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param functionName   <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greaterEqual(final ConnectionCode connectionCode,
	                                      final String tableName, final String columnName,
	                                      final String functionName, final AbstractParameter<?>... functionParams) {
		return this.greaterEqual(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName, functionName, functionParams);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greaterEqual(final ConnectionCode connectionCode, final String tableName,
	                                      final String columnName, final QueryData subQuery) {
		return this.greaterEqual(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param matchValue     <span class="en-US">Match value</span>
	 *                       <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greaterEqual(final ConnectionCode connectionCode, final String tableName,
	                                      final String columnName, final Object matchValue) {
		return this.greaterEqual(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName, matchValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param matchTable     <span class="en-US">Target data table name</span>
	 *                       <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn    <span class="en-US">Target data column name</span>
	 *                       <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder less(final ConnectionCode connectionCode,
	                              final String tableName, final String columnName,
	                              final String matchTable, final String matchColumn) {
		return this.less(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName, matchTable, matchColumn);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param functionName   <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder less(final ConnectionCode connectionCode,
	                              final String tableName, final String columnName,
	                              final String functionName, final AbstractParameter<?>... functionParams) {
		return this.less(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName, functionName, functionParams);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder less(final ConnectionCode connectionCode, final String tableName,
	                              final String columnName, final QueryData subQuery) {
		return this.less(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param matchValue     <span class="en-US">Match value</span>
	 *                       <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder less(final ConnectionCode connectionCode, final String tableName,
	                              final String columnName, final Object matchValue) {
		return this.less(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName, matchValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param matchTable     <span class="en-US">Target data table name</span>
	 *                       <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn    <span class="en-US">Target data column name</span>
	 *                       <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder lessEqual(final ConnectionCode connectionCode,
	                                   final String tableName, final String columnName,
	                                   final String matchTable, final String matchColumn) {
		return this.lessEqual(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName, matchTable, matchColumn);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param functionName   <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder lessEqual(final ConnectionCode connectionCode, final String tableName,
	                                   final String columnName, final String functionName,
	                                   final AbstractParameter<?>... functionParams) {
		return this.lessEqual(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName, functionName, functionParams);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder lessEqual(final ConnectionCode connectionCode, final String tableName,
	                                   final String columnName, final QueryData subQuery) {
		return this.lessEqual(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Entity class where the data column is located</span>
	 *                       <span class="zh-CN">数据列所在实体类</span>
	 * @param columnName     <span class="en-US">Data column identification code</span>
	 *                       <span class="zh-CN">数据列识别代码</span>
	 * @param matchValue     <span class="en-US">Match value</span>
	 *                       <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder lessEqual(final ConnectionCode connectionCode, final String tableName,
	                                   final String columnName, final Object matchValue) {
		return this.lessEqual(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName, matchValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition equal to a certain value</h3>
	 * <h3 class="zh-CN">添加等于某值的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param matchTable     <span class="en-US">Target data table name</span>
	 *                       <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn    <span class="en-US">Target data column name</span>
	 *                       <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder equalTo(final ConnectionCode connectionCode,
	                                 final String tableName, final String columnName,
	                                 final String matchTable, final String matchColumn) {
		return this.equalTo(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName, matchTable, matchColumn);
	}

	/**
	 * <h3 class="en-US">Add a query condition equal to a certain value</h3>
	 * <h3 class="zh-CN">添加等于某值的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param functionName   <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder equalTo(final ConnectionCode connectionCode, final String tableName,
	                                 final String columnName, final String functionName,
	                                 final AbstractParameter<?>... functionParams) {
		return this.equalTo(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName, functionName, functionParams);
	}

	/**
	 * <h3 class="en-US">Add a query condition equal to a certain value</h3>
	 * <h3 class="zh-CN">添加等于某值的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder equalTo(final ConnectionCode connectionCode, final String tableName,
	                                 final String columnName, final QueryData subQuery) {
		return this.equalTo(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Add a query condition equal to a certain value</h3>
	 * <h3 class="zh-CN">添加等于某值的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param matchValue     <span class="en-US">Match value</span>
	 *                       <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder equalTo(final ConnectionCode connectionCode,
	                                 final String tableName, final String columnName,
	                                 final Object matchValue) {
		return this.equalTo(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName, matchValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition not equal to a certain value</h3>
	 * <h3 class="zh-CN">添加不等于某值的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param matchTable     <span class="en-US">Target data table name</span>
	 *                       <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn    <span class="en-US">Target data column name</span>
	 *                       <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notEqual(final ConnectionCode connectionCode, final String tableName, final String columnName,
	                                  final String matchTable, final String matchColumn) {
		return this.notEqual(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName, matchTable, matchColumn);
	}

	/**
	 * <h3 class="en-US">Add a query condition not equal to a certain value</h3>
	 * <h3 class="zh-CN">添加不等于某值的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param functionName   <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notEqual(final ConnectionCode connectionCode, final String tableName,
	                                  final String columnName, final String functionName,
	                                  final AbstractParameter<?>... functionParams) {
		return this.notEqual(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName, functionName, functionParams);
	}

	/**
	 * <h3 class="en-US">Add a query condition not equal to a certain value</h3>
	 * <h3 class="zh-CN">添加不等于某值的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notEqual(final ConnectionCode connectionCode, final String tableName,
	                                  final String columnName, final QueryData subQuery) {
		return this.notEqual(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Add a query condition not equal to a certain value</h3>
	 * <h3 class="zh-CN">添加不等于某值的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param matchValue     <span class="en-US">Match value</span>
	 *                       <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notEqual(final ConnectionCode connectionCode, final String tableName,
	                                  final String columnName, final Object matchValue) {
		return this.notEqual(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName, matchValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition between certain two values</h3>
	 * <h3 class="zh-CN">添加介于某两个值之间的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param beginValue     <span class="en-US">Begin value</span>
	 *                       <span class="zh-CN">起始值</span>
	 * @param endValue       <span class="en-US">End value</span>
	 *                       <span class="zh-CN">终止值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder inRanges(final ConnectionCode connectionCode,
	                                  final String tableName, final String columnName,
	                                  final Object beginValue, final Object endValue) {
		return this.inRanges(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName, beginValue, endValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition not between certain two values</h3>
	 * <h3 class="zh-CN">添加不介于某两个值之间的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param beginValue     <span class="en-US">Begin value</span>
	 *                       <span class="zh-CN">起始值</span>
	 * @param endValue       <span class="en-US">End value</span>
	 *                       <span class="zh-CN">终止值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notInRanges(final ConnectionCode connectionCode,
	                                     final String tableName, final String columnName,
	                                     final Object beginValue, final Object endValue) {
		return this.notInRanges(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName, beginValue, endValue);
	}

	/**
	 * <h3 class="en-US">Add query conditions for fuzzy matching values</h3>
	 * <h3 class="zh-CN">添加模糊匹配值的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param matchRule      <span class="en-US">match rule string</span>
	 *                       <span class="zh-CN">匹配规则字符串</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder like(final ConnectionCode connectionCode, final String tableName,
	                              final String columnName, final String matchRule) {
		return this.like(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName, matchRule);
	}

	/**
	 * <h3 class="en-US">Add query conditions for not fuzzy matching values</h3>
	 * <h3 class="zh-CN">添加非模糊匹配值的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param matchRule      <span class="en-US">match rule string</span>
	 *                       <span class="zh-CN">匹配规则字符串</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notLike(final ConnectionCode connectionCode, final String tableName,
	                                 final String columnName, final String matchRule) {
		return this.notLike(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName, matchRule);
	}

	/**
	 * <h3 class="en-US">Add query condition with null value</h3>
	 * <h3 class="zh-CN">添加空值的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder matchNull(final ConnectionCode connectionCode, final String tableName,
	                                   final String columnName) {
		return this.matchNull(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Add query condition with not null value</h3>
	 * <h3 class="zh-CN">添加非空值的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notNull(final ConnectionCode connectionCode, final String tableName,
	                                 final String columnName) {
		return this.notNull(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Adds a query condition where the value is contained in the given data</h3>
	 * <h3 class="zh-CN">添加值包含在给定数据中的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder in(final ConnectionCode connectionCode, final String tableName,
	                            final String columnName, final QueryData subQuery) {
		return this.in(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Adds a query condition where the value is contained in the given data</h3>
	 * <h3 class="zh-CN">添加值包含在给定数据中的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param matchValues    <span class="en-US">Condition data array</span>
	 *                       <span class="zh-CN">匹配值数组</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder in(final ConnectionCode connectionCode, final String tableName,
	                            final String columnName, final Object... matchValues) {
		return this.in(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName, matchValues);
	}

	/**
	 * <h3 class="en-US">Adds a query condition where the value is not contained in the given data</h3>
	 * <h3 class="zh-CN">添加值非包含在给定数据中的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notIn(final ConnectionCode connectionCode, final String tableName,
	                               final String columnName, final QueryData subQuery) {
		return this.notIn(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Adds a query condition where the value is not contained in the given data</h3>
	 * <h3 class="zh-CN">添加值非包含在给定数据中的查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param matchValues    <span class="en-US">Condition data array</span>
	 *                       <span class="zh-CN">匹配值数组</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notIn(final ConnectionCode connectionCode, final String tableName,
	                               final String columnName, final Object... matchValues) {
		return this.notIn(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, columnName, matchValues);
	}

	/**
	 * <h3 class="en-US">Adds a sub-query condition</h3>
	 * <h3 class="zh-CN">添加子查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @param functionName   <span class="en-US">Function name of sub-query</span>
	 *                       <span class="zh-CN">子查询函数名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder exists(final ConnectionCode connectionCode, final String tableName,
	                                final QueryData subQuery, final String functionName) {
		return this.exists(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, subQuery, functionName);
	}

	/**
	 * <h3 class="en-US">Adds a sub-query condition</h3>
	 * <h3 class="zh-CN">添加子查询条件</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @param functionName   <span class="en-US">Function name of sub-query</span>
	 *                       <span class="zh-CN">子查询函数名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notExists(final ConnectionCode connectionCode, final String tableName,
	                                   final QueryData subQuery, final String functionName) {
		return this.notExists(Globals.DEFAULT_VALUE_INT, connectionCode, tableName, subQuery, functionName);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchValue      <span class="en-US">Match value</span>
	 *                        <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greater(final boolean havingCondition, final String tableName, final String columnName,
	                                 final Object matchValue) {
		return this.greater(ConnectionCode.AND, havingCondition, tableName, columnName, matchValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchTable      <span class="en-US">Target data table name</span>
	 *                        <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn     <span class="en-US">Target data column name</span>
	 *                        <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greater(final boolean havingCondition, final String tableName, final String columnName,
	                                 final String matchTable, final String matchColumn) {
		return this.greater(ConnectionCode.AND, havingCondition, tableName, columnName,
				matchTable, matchColumn);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param functionName    <span class="en-US">Function name</span>
	 *                        <span class="zh-CN">函数名称</span>
	 * @param functionParams  <span class="en-US">Function parameter values</span>
	 *                        <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greater(final boolean havingCondition, final String tableName, final String columnName,
	                                 final String functionName, final AbstractParameter<?>... functionParams) {
		return this.greater(ConnectionCode.AND, havingCondition, tableName, columnName,
				functionName, functionParams);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param subQuery        <span class="en-US">Sub-query instance object</span>
	 *                        <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greater(final boolean havingCondition, final String tableName, final String columnName,
	                                 final QueryData subQuery) {
		return this.greater(ConnectionCode.AND, havingCondition, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchTable      <span class="en-US">Target data table name</span>
	 *                        <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn     <span class="en-US">Target data column name</span>
	 *                        <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greaterEqual(final boolean havingCondition, final String tableName, final String columnName,
	                                      final String matchTable, final String matchColumn) {
		return this.greaterEqual(ConnectionCode.AND, havingCondition, tableName, columnName, matchTable, matchColumn);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param functionName    <span class="en-US">Function name</span>
	 *                        <span class="zh-CN">函数名称</span>
	 * @param functionParams  <span class="en-US">Function parameter values</span>
	 *                        <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greaterEqual(final boolean havingCondition, final String tableName, final String columnName,
	                                      final String functionName, final AbstractParameter<?>... functionParams) {
		return this.greaterEqual(ConnectionCode.AND, havingCondition, tableName, columnName, functionName, functionParams);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param subQuery        <span class="en-US">Sub-query instance object</span>
	 *                        <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greaterEqual(final boolean havingCondition, final String tableName, final String columnName,
	                                      final QueryData subQuery) {
		return this.greaterEqual(ConnectionCode.AND, havingCondition, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchValue      <span class="en-US">Match value</span>
	 *                        <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greaterEqual(final boolean havingCondition, final String tableName, final String columnName,
	                                      final Object matchValue) {
		return this.greaterEqual(ConnectionCode.AND, havingCondition, tableName, columnName, matchValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchTable      <span class="en-US">Target data table name</span>
	 *                        <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn     <span class="en-US">Target data column name</span>
	 *                        <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder less(final boolean havingCondition, final String tableName, final String columnName,
	                              final String matchTable, final String matchColumn) {
		return this.less(ConnectionCode.AND, havingCondition, tableName, columnName, matchTable, matchColumn);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param functionName    <span class="en-US">Function name</span>
	 *                        <span class="zh-CN">函数名称</span>
	 * @param functionParams  <span class="en-US">Function parameter values</span>
	 *                        <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder less(final boolean havingCondition, final String tableName, final String columnName,
	                              final String functionName, final AbstractParameter<?>... functionParams) {
		return this.less(ConnectionCode.AND, havingCondition, tableName, columnName, functionName, functionParams);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param subQuery        <span class="en-US">Sub-query instance object</span>
	 *                        <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder less(final boolean havingCondition, final String tableName, final String columnName,
	                              final QueryData subQuery) {
		return this.less(ConnectionCode.AND, havingCondition, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchValue      <span class="en-US">Match value</span>
	 *                        <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder less(final boolean havingCondition, final String tableName, final String columnName,
	                              final Object matchValue) {
		return this.less(ConnectionCode.AND, havingCondition, tableName, columnName, matchValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchTable      <span class="en-US">Target data table name</span>
	 *                        <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn     <span class="en-US">Target data column name</span>
	 *                        <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder lessEqual(final boolean havingCondition, final String tableName, final String columnName,
	                                   final String matchTable, final String matchColumn) {
		return this.lessEqual(ConnectionCode.AND, havingCondition, tableName, columnName, matchTable, matchColumn);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param functionName    <span class="en-US">Function name</span>
	 *                        <span class="zh-CN">函数名称</span>
	 * @param functionParams  <span class="en-US">Function parameter values</span>
	 *                        <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder lessEqual(final boolean havingCondition, final String tableName, final String columnName,
	                                   final String functionName, final AbstractParameter<?>... functionParams) {
		return this.lessEqual(ConnectionCode.AND, havingCondition, tableName, columnName, functionName, functionParams);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param subQuery        <span class="en-US">Sub-query instance object</span>
	 *                        <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder lessEqual(final boolean havingCondition, final String tableName, final String columnName,
	                                   final QueryData subQuery) {
		return this.lessEqual(ConnectionCode.AND, havingCondition, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchValue      <span class="en-US">Match value</span>
	 *                        <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder lessEqual(final boolean havingCondition, final String tableName, final String columnName,
	                                   final Object matchValue) {
		return this.lessEqual(ConnectionCode.AND, havingCondition, tableName, columnName, matchValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition equal to a certain value</h3>
	 * <h3 class="zh-CN">添加等于某值的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchTable      <span class="en-US">Target data table name</span>
	 *                        <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn     <span class="en-US">Target data column name</span>
	 *                        <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder equalTo(final boolean havingCondition, final String tableName, final String columnName,
	                                 final String matchTable, final String matchColumn) {
		return this.equalTo(ConnectionCode.AND, havingCondition, tableName, columnName, matchTable, matchColumn);
	}

	/**
	 * <h3 class="en-US">Add a query condition equal to a certain value</h3>
	 * <h3 class="zh-CN">添加等于某值的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param functionName    <span class="en-US">Function name</span>
	 *                        <span class="zh-CN">函数名称</span>
	 * @param functionParams  <span class="en-US">Function parameter values</span>
	 *                        <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder equalTo(final boolean havingCondition, final String tableName, final String columnName,
	                                 final String functionName, final AbstractParameter<?>... functionParams) {
		return this.equalTo(ConnectionCode.AND, havingCondition, tableName, columnName, functionName, functionParams);
	}

	/**
	 * <h3 class="en-US">Add a query condition equal to a certain value</h3>
	 * <h3 class="zh-CN">添加等于某值的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param subQuery        <span class="en-US">Sub-query instance object</span>
	 *                        <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder equalTo(final boolean havingCondition, final String tableName, final String columnName,
	                                 final QueryData subQuery) {
		return this.equalTo(ConnectionCode.AND, havingCondition, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Add a query condition equal to a certain value</h3>
	 * <h3 class="zh-CN">添加等于某值的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchValue      <span class="en-US">Match value</span>
	 *                        <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder equalTo(final boolean havingCondition, final String tableName, final String columnName,
	                                 final Object matchValue) {
		return this.equalTo(ConnectionCode.AND, havingCondition, tableName, columnName, matchValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition not equal to a certain value</h3>
	 * <h3 class="zh-CN">添加不等于某值的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchTable      <span class="en-US">Target data table name</span>
	 *                        <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn     <span class="en-US">Target data column name</span>
	 *                        <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notEqual(final boolean havingCondition, final String tableName, final String columnName,
	                                  final String matchTable, final String matchColumn) {
		return this.notEqual(ConnectionCode.AND, havingCondition, tableName, columnName, matchTable, matchColumn);
	}

	/**
	 * <h3 class="en-US">Add a query condition not equal to a certain value</h3>
	 * <h3 class="zh-CN">添加不等于某值的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param functionName    <span class="en-US">Function name</span>
	 *                        <span class="zh-CN">函数名称</span>
	 * @param functionParams  <span class="en-US">Function parameter values</span>
	 *                        <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notEqual(final boolean havingCondition, final String tableName, final String columnName,
	                                  final String functionName, final AbstractParameter<?>... functionParams) {
		return this.notEqual(ConnectionCode.AND, havingCondition, tableName, columnName, functionName, functionParams);
	}

	/**
	 * <h3 class="en-US">Add a query condition not equal to a certain value</h3>
	 * <h3 class="zh-CN">添加不等于某值的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param subQuery        <span class="en-US">Sub-query instance object</span>
	 *                        <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notEqual(final boolean havingCondition, final String tableName, final String columnName,
	                                  final QueryData subQuery) {
		return this.notEqual(ConnectionCode.AND, havingCondition, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Add a query condition not equal to a certain value</h3>
	 * <h3 class="zh-CN">添加不等于某值的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchValue      <span class="en-US">Match value</span>
	 *                        <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notEqual(final boolean havingCondition, final String tableName,
	                                  final String columnName, final Object matchValue) {
		return this.notEqual(ConnectionCode.AND, havingCondition, tableName, columnName, matchValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition between certain two values</h3>
	 * <h3 class="zh-CN">添加介于某两个值之间的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param beginValue      <span class="en-US">Begin value</span>
	 *                        <span class="zh-CN">起始值</span>
	 * @param endValue        <span class="en-US">End value</span>
	 *                        <span class="zh-CN">终止值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder inRanges(final boolean havingCondition, final String tableName, final String columnName,
	                                  final Object beginValue, final Object endValue) {
		return this.inRanges(ConnectionCode.AND, havingCondition, tableName, columnName, beginValue, endValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition not between certain two values</h3>
	 * <h3 class="zh-CN">添加不介于某两个值之间的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param beginValue      <span class="en-US">Begin value</span>
	 *                        <span class="zh-CN">起始值</span>
	 * @param endValue        <span class="en-US">End value</span>
	 *                        <span class="zh-CN">终止值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notInRanges(final boolean havingCondition, final String tableName, final String columnName,
	                                     final Object beginValue, final Object endValue) {
		return this.notInRanges(ConnectionCode.AND, havingCondition, tableName, columnName, beginValue, endValue);
	}

	/**
	 * <h3 class="en-US">Add query conditions for fuzzy matching values</h3>
	 * <h3 class="zh-CN">添加模糊匹配值的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchRule       <span class="en-US">match rule string</span>
	 *                        <span class="zh-CN">匹配规则字符串</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder like(final boolean havingCondition, final String tableName, final String columnName,
	                              final String matchRule) {
		return this.like(ConnectionCode.AND, havingCondition, tableName, columnName, matchRule);
	}

	/**
	 * <h3 class="en-US">Add query conditions for not fuzzy matching values</h3>
	 * <h3 class="zh-CN">添加非模糊匹配值的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchRule       <span class="en-US">match rule string</span>
	 *                        <span class="zh-CN">匹配规则字符串</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notLike(final boolean havingCondition, final String tableName, final String columnName,
	                                 final String matchRule) {
		return this.notLike(ConnectionCode.AND, havingCondition, tableName, columnName, matchRule);
	}

	/**
	 * <h3 class="en-US">Add query condition with null value</h3>
	 * <h3 class="zh-CN">添加空值的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder matchNull(final boolean havingCondition, final String tableName, final String columnName) {
		return this.matchNull(ConnectionCode.AND, havingCondition, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Add query condition with not null value</h3>
	 * <h3 class="zh-CN">添加非空值的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notNull(final boolean havingCondition, final String tableName, final String columnName) {
		return this.notNull(ConnectionCode.AND, havingCondition, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Adds a query condition where the value is contained in the given data</h3>
	 * <h3 class="zh-CN">添加值包含在给定数据中的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param subQuery        <span class="en-US">Sub-query instance object</span>
	 *                        <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder in(final boolean havingCondition, final String tableName, final String columnName,
	                            final QueryData subQuery) {
		return this.in(ConnectionCode.AND, havingCondition, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Adds a query condition where the value is contained in the given data</h3>
	 * <h3 class="zh-CN">添加值包含在给定数据中的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchValues     <span class="en-US">Condition data array</span>
	 *                        <span class="zh-CN">匹配值数组</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder in(final boolean havingCondition, final String tableName, final String columnName,
	                            final Object... matchValues) {
		return this.in(ConnectionCode.AND, havingCondition, tableName, columnName, matchValues);
	}

	/**
	 * <h3 class="en-US">Adds a query condition where the value is not contained in the given data</h3>
	 * <h3 class="zh-CN">添加值非包含在给定数据中的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param subQuery        <span class="en-US">Sub-query instance object</span>
	 *                        <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notIn(final boolean havingCondition, final String tableName, final String columnName,
	                               final QueryData subQuery) {
		return this.notIn(ConnectionCode.AND, havingCondition, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Adds a query condition where the value is not contained in the given data</h3>
	 * <h3 class="zh-CN">添加值非包含在给定数据中的查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchValues     <span class="en-US">Condition data array</span>
	 *                        <span class="zh-CN">匹配值数组</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notIn(final boolean havingCondition, final String tableName, final String columnName,
	                               final Object... matchValues) {
		return this.notIn(ConnectionCode.AND, havingCondition, tableName, columnName, matchValues);
	}

	/**
	 * <h3 class="en-US">Adds a sub-query condition</h3>
	 * <h3 class="zh-CN">添加子查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param subQuery        <span class="en-US">Sub-query instance object</span>
	 *                        <span class="zh-CN">子查询实例对象</span>
	 * @param functionName    <span class="en-US">Function name of sub-query</span>
	 *                        <span class="zh-CN">子查询函数名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder exists(final boolean havingCondition, final String tableName, final QueryData subQuery,
	                                final String functionName) {
		return this.exists(ConnectionCode.AND, havingCondition, tableName, subQuery, functionName);
	}

	/**
	 * <h3 class="en-US">Adds a sub-query condition</h3>
	 * <h3 class="zh-CN">添加子查询条件</h3>
	 *
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param subQuery        <span class="en-US">Sub-query instance object</span>
	 *                        <span class="zh-CN">子查询实例对象</span>
	 * @param functionName    <span class="en-US">Function name of sub-query</span>
	 *                        <span class="zh-CN">子查询函数名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notExists(final boolean havingCondition,
	                                   final String tableName, final QueryData subQuery, final String functionName) {
		return this.notExists(ConnectionCode.AND, havingCondition, tableName, subQuery, functionName);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchValue      <span class="en-US">Match value</span>
	 *                        <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greater(final ConnectionCode connectionCode, final boolean havingCondition,
	                                 final String tableName, final String columnName, final Object matchValue) {
		return this.greater(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName, matchValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchTable      <span class="en-US">Target data table name</span>
	 *                        <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn     <span class="en-US">Target data column name</span>
	 *                        <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greater(final ConnectionCode connectionCode, final boolean havingCondition,
	                                 final String tableName, final String columnName,
	                                 final String matchTable, final String matchColumn) {
		return this.greater(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName,
				matchTable, matchColumn);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param functionName    <span class="en-US">Function name</span>
	 *                        <span class="zh-CN">函数名称</span>
	 * @param functionParams  <span class="en-US">Function parameter values</span>
	 *                        <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greater(final ConnectionCode connectionCode, final boolean havingCondition,
	                                 final String tableName, final String columnName, final String functionName,
	                                 final AbstractParameter<?>... functionParams) {
		return this.greater(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName,
				functionName, functionParams);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param subQuery        <span class="en-US">Sub-query instance object</span>
	 *                        <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greater(final ConnectionCode connectionCode, final boolean havingCondition,
	                                 final String tableName, final String columnName, final QueryData subQuery) {
		return this.greater(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchTable      <span class="en-US">Target data table name</span>
	 *                        <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn     <span class="en-US">Target data column name</span>
	 *                        <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greaterEqual(final ConnectionCode connectionCode, final boolean havingCondition,
	                                      final String tableName, final String columnName,
	                                      final String matchTable, final String matchColumn) {
		return this.greaterEqual(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName, matchTable, matchColumn);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param functionName    <span class="en-US">Function name</span>
	 *                        <span class="zh-CN">函数名称</span>
	 * @param functionParams  <span class="en-US">Function parameter values</span>
	 *                        <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greaterEqual(final ConnectionCode connectionCode, final boolean havingCondition,
	                                      final String tableName, final String columnName,
	                                      final String functionName, final AbstractParameter<?>... functionParams) {
		return this.greaterEqual(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName, functionName, functionParams);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param subQuery        <span class="en-US">Sub-query instance object</span>
	 *                        <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greaterEqual(final ConnectionCode connectionCode, final boolean havingCondition,
	                                      final String tableName, final String columnName, final QueryData subQuery) {
		return this.greaterEqual(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchValue      <span class="en-US">Match value</span>
	 *                        <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greaterEqual(final ConnectionCode connectionCode, final boolean havingCondition,
	                                      final String tableName, final String columnName,
	                                      final Object matchValue) {
		return this.greaterEqual(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName, matchValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchTable      <span class="en-US">Target data table name</span>
	 *                        <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn     <span class="en-US">Target data column name</span>
	 *                        <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder less(final ConnectionCode connectionCode, final boolean havingCondition,
	                              final String tableName, final String columnName,
	                              final String matchTable, final String matchColumn) {
		return this.less(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName, matchTable, matchColumn);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param functionName    <span class="en-US">Function name</span>
	 *                        <span class="zh-CN">函数名称</span>
	 * @param functionParams  <span class="en-US">Function parameter values</span>
	 *                        <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder less(final ConnectionCode connectionCode, final boolean havingCondition,
	                              final String tableName, final String columnName,
	                              final String functionName, final AbstractParameter<?>... functionParams) {
		return this.less(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName, functionName, functionParams);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param subQuery        <span class="en-US">Sub-query instance object</span>
	 *                        <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder less(final ConnectionCode connectionCode, final boolean havingCondition,
	                              final String tableName, final String columnName, final QueryData subQuery) {
		return this.less(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchValue      <span class="en-US">Match value</span>
	 *                        <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder less(final ConnectionCode connectionCode, final boolean havingCondition,
	                              final String tableName, final String columnName, final Object matchValue) {
		return this.less(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName, matchValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchTable      <span class="en-US">Target data table name</span>
	 *                        <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn     <span class="en-US">Target data column name</span>
	 *                        <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder lessEqual(final ConnectionCode connectionCode, final boolean havingCondition,
	                                   final String tableName, final String columnName,
	                                   final String matchTable, final String matchColumn) {
		return this.lessEqual(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName, matchTable, matchColumn);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param functionName    <span class="en-US">Function name</span>
	 *                        <span class="zh-CN">函数名称</span>
	 * @param functionParams  <span class="en-US">Function parameter values</span>
	 *                        <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder lessEqual(final ConnectionCode connectionCode, final boolean havingCondition,
	                                   final String tableName, final String columnName, final String functionName,
	                                   final AbstractParameter<?>... functionParams) {
		return this.lessEqual(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName, functionName, functionParams);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param subQuery        <span class="en-US">Sub-query instance object</span>
	 *                        <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder lessEqual(final ConnectionCode connectionCode, final boolean havingCondition,
	                                   final String tableName, final String columnName, final QueryData subQuery) {
		return this.lessEqual(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchValue      <span class="en-US">Match value</span>
	 *                        <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder lessEqual(final ConnectionCode connectionCode, final boolean havingCondition,
	                                   final String tableName, final String columnName, final Object matchValue) {
		return this.lessEqual(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName, matchValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition equal to a certain value</h3>
	 * <h3 class="zh-CN">添加等于某值的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchTable      <span class="en-US">Target data table name</span>
	 *                        <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn     <span class="en-US">Target data column name</span>
	 *                        <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder equalTo(final ConnectionCode connectionCode, final boolean havingCondition,
	                                 final String tableName, final String columnName,
	                                 final String matchTable, final String matchColumn) {
		return this.equalTo(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName, matchTable, matchColumn);
	}

	/**
	 * <h3 class="en-US">Add a query condition equal to a certain value</h3>
	 * <h3 class="zh-CN">添加等于某值的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param functionName    <span class="en-US">Function name</span>
	 *                        <span class="zh-CN">函数名称</span>
	 * @param functionParams  <span class="en-US">Function parameter values</span>
	 *                        <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder equalTo(final ConnectionCode connectionCode, final boolean havingCondition,
	                                 final String tableName, final String columnName, final String functionName,
	                                 final AbstractParameter<?>... functionParams) {
		return this.equalTo(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName, functionName, functionParams);
	}

	/**
	 * <h3 class="en-US">Add a query condition equal to a certain value</h3>
	 * <h3 class="zh-CN">添加等于某值的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param subQuery        <span class="en-US">Sub-query instance object</span>
	 *                        <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder equalTo(final ConnectionCode connectionCode, final boolean havingCondition,
	                                 final String tableName, final String columnName, final QueryData subQuery) {
		return this.equalTo(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Add a query condition equal to a certain value</h3>
	 * <h3 class="zh-CN">添加等于某值的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchValue      <span class="en-US">Match value</span>
	 *                        <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder equalTo(final ConnectionCode connectionCode, final boolean havingCondition,
	                                 final String tableName, final String columnName,
	                                 final Object matchValue) {
		return this.equalTo(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName, matchValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition not equal to a certain value</h3>
	 * <h3 class="zh-CN">添加不等于某值的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchTable      <span class="en-US">Target data table name</span>
	 *                        <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn     <span class="en-US">Target data column name</span>
	 *                        <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notEqual(final ConnectionCode connectionCode, final boolean havingCondition,
	                                  final String tableName, final String columnName,
	                                  final String matchTable, final String matchColumn) {
		return this.notEqual(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName, matchTable, matchColumn);
	}

	/**
	 * <h3 class="en-US">Add a query condition not equal to a certain value</h3>
	 * <h3 class="zh-CN">添加不等于某值的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param functionName    <span class="en-US">Function name</span>
	 *                        <span class="zh-CN">函数名称</span>
	 * @param functionParams  <span class="en-US">Function parameter values</span>
	 *                        <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notEqual(final ConnectionCode connectionCode, final boolean havingCondition,
	                                  final String tableName, final String columnName, final String functionName,
	                                  final AbstractParameter<?>... functionParams) {
		return this.notEqual(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName, functionName, functionParams);
	}

	/**
	 * <h3 class="en-US">Add a query condition not equal to a certain value</h3>
	 * <h3 class="zh-CN">添加不等于某值的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param subQuery        <span class="en-US">Sub-query instance object</span>
	 *                        <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notEqual(final ConnectionCode connectionCode, final boolean havingCondition,
	                                  final String tableName, final String columnName, final QueryData subQuery) {
		return this.notEqual(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Add a query condition not equal to a certain value</h3>
	 * <h3 class="zh-CN">添加不等于某值的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchValue      <span class="en-US">Match value</span>
	 *                        <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notEqual(final ConnectionCode connectionCode, final boolean havingCondition,
	                                  final String tableName, final String columnName, final Object matchValue) {
		return this.notEqual(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName, matchValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition between certain two values</h3>
	 * <h3 class="zh-CN">添加介于某两个值之间的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param beginValue      <span class="en-US">Begin value</span>
	 *                        <span class="zh-CN">起始值</span>
	 * @param endValue        <span class="en-US">End value</span>
	 *                        <span class="zh-CN">终止值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder inRanges(final ConnectionCode connectionCode, final boolean havingCondition,
	                                  final String tableName, final String columnName,
	                                  final Object beginValue, final Object endValue) {
		return this.inRanges(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName, beginValue, endValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition not between certain two values</h3>
	 * <h3 class="zh-CN">添加不介于某两个值之间的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param beginValue      <span class="en-US">Begin value</span>
	 *                        <span class="zh-CN">起始值</span>
	 * @param endValue        <span class="en-US">End value</span>
	 *                        <span class="zh-CN">终止值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notInRanges(final ConnectionCode connectionCode, final boolean havingCondition,
	                                     final String tableName, final String columnName,
	                                     final Object beginValue, final Object endValue) {
		return this.notInRanges(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName, beginValue, endValue);
	}

	/**
	 * <h3 class="en-US">Add query conditions for fuzzy matching values</h3>
	 * <h3 class="zh-CN">添加模糊匹配值的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchRule       <span class="en-US">match rule string</span>
	 *                        <span class="zh-CN">匹配规则字符串</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder like(final ConnectionCode connectionCode, final boolean havingCondition,
	                              final String tableName, final String columnName, final String matchRule) {
		return this.like(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName, matchRule);
	}

	/**
	 * <h3 class="en-US">Add query conditions for not fuzzy matching values</h3>
	 * <h3 class="zh-CN">添加非模糊匹配值的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchRule       <span class="en-US">match rule string</span>
	 *                        <span class="zh-CN">匹配规则字符串</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notLike(final ConnectionCode connectionCode, final boolean havingCondition,
	                                 final String tableName, final String columnName, final String matchRule) {
		return this.notLike(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName, matchRule);
	}

	/**
	 * <h3 class="en-US">Add query condition with null value</h3>
	 * <h3 class="zh-CN">添加空值的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder matchNull(final ConnectionCode connectionCode, final boolean havingCondition,
	                                   final String tableName, final String columnName) {
		return this.matchNull(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Add query condition with not null value</h3>
	 * <h3 class="zh-CN">添加非空值的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notNull(final ConnectionCode connectionCode, final boolean havingCondition,
	                                 final String tableName, final String columnName) {
		return this.notNull(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Adds a query condition where the value is contained in the given data</h3>
	 * <h3 class="zh-CN">添加值包含在给定数据中的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param subQuery        <span class="en-US">Sub-query instance object</span>
	 *                        <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder in(final ConnectionCode connectionCode, final boolean havingCondition,
	                            final String tableName, final String columnName, final QueryData subQuery) {
		return this.in(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Adds a query condition where the value is contained in the given data</h3>
	 * <h3 class="zh-CN">添加值包含在给定数据中的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchValues     <span class="en-US">Condition data array</span>
	 *                        <span class="zh-CN">匹配值数组</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder in(final ConnectionCode connectionCode, final boolean havingCondition,
	                            final String tableName, final String columnName, final Object... matchValues) {
		return this.in(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName, matchValues);
	}

	/**
	 * <h3 class="en-US">Adds a query condition where the value is not contained in the given data</h3>
	 * <h3 class="zh-CN">添加值非包含在给定数据中的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param subQuery        <span class="en-US">Sub-query instance object</span>
	 *                        <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notIn(final ConnectionCode connectionCode, final boolean havingCondition,
	                               final String tableName, final String columnName, final QueryData subQuery) {
		return this.notIn(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Adds a query condition where the value is not contained in the given data</h3>
	 * <h3 class="zh-CN">添加值非包含在给定数据中的查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchValues     <span class="en-US">Condition data array</span>
	 *                        <span class="zh-CN">匹配值数组</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notIn(final ConnectionCode connectionCode, final boolean havingCondition,
	                               final String tableName, final String columnName, final Object... matchValues) {
		return this.notIn(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, columnName, matchValues);
	}

	/**
	 * <h3 class="en-US">Adds a sub-query condition</h3>
	 * <h3 class="zh-CN">添加子查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param subQuery        <span class="en-US">Sub-query instance object</span>
	 *                        <span class="zh-CN">子查询实例对象</span>
	 * @param functionName    <span class="en-US">Function name of sub-query</span>
	 *                        <span class="zh-CN">子查询函数名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder exists(final ConnectionCode connectionCode, final boolean havingCondition,
	                                final String tableName, final QueryData subQuery, final String functionName) {
		return this.exists(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, subQuery, functionName);
	}

	/**
	 * <h3 class="en-US">Adds a sub-query condition</h3>
	 * <h3 class="zh-CN">添加子查询条件</h3>
	 *
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param subQuery        <span class="en-US">Sub-query instance object</span>
	 *                        <span class="zh-CN">子查询实例对象</span>
	 * @param functionName    <span class="en-US">Function name of sub-query</span>
	 *                        <span class="zh-CN">子查询函数名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notExists(final ConnectionCode connectionCode, final boolean havingCondition,
	                                   final String tableName, final QueryData subQuery, final String functionName) {
		return this.notExists(Globals.DEFAULT_VALUE_INT, connectionCode, havingCondition, tableName, subQuery, functionName);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param matchValue     <span class="en-US">Match value</span>
	 *                       <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greater(final int sortCode, final ConnectionCode connectionCode,
	                                 final String tableName, final String columnName,
	                                 final Object matchValue) {
		return this.greater(sortCode, connectionCode, Boolean.FALSE, tableName, columnName, matchValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param matchTable     <span class="en-US">Target data table name</span>
	 *                       <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn    <span class="en-US">Target data column name</span>
	 *                       <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greater(final int sortCode, final ConnectionCode connectionCode,
	                                 final String tableName, final String columnName,
	                                 final String matchTable, final String matchColumn) {
		return this.greater(sortCode, connectionCode, Boolean.FALSE, tableName, columnName,
				matchTable, matchColumn);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param functionName   <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greater(final int sortCode, final ConnectionCode connectionCode,
	                                 final String tableName, final String columnName,
	                                 final String functionName, final AbstractParameter<?>... functionParams) {
		return this.greater(sortCode, connectionCode, Boolean.FALSE, tableName, columnName,
				functionName, functionParams);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greater(final int sortCode, final ConnectionCode connectionCode,
	                                 final String tableName, final String columnName, final QueryData subQuery) {
		return this.greater(sortCode, connectionCode, Boolean.FALSE, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param matchTable     <span class="en-US">Target data table name</span>
	 *                       <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn    <span class="en-US">Target data column name</span>
	 *                       <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greaterEqual(final int sortCode, final ConnectionCode connectionCode,
	                                      final String tableName, final String columnName,
	                                      final String matchTable, final String matchColumn) {
		return this.greaterEqual(sortCode, connectionCode, Boolean.FALSE, tableName, columnName,
				matchTable, matchColumn);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param functionName   <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greaterEqual(final int sortCode, final ConnectionCode connectionCode,
	                                      final String tableName, final String columnName,
	                                      final String functionName, final AbstractParameter<?>... functionParams) {
		return this.greaterEqual(sortCode, connectionCode, Boolean.FALSE, tableName, columnName,
				functionName, functionParams);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greaterEqual(final int sortCode, final ConnectionCode connectionCode,
	                                      final String tableName, final String columnName, final QueryData subQuery) {
		return this.greaterEqual(sortCode, connectionCode, Boolean.FALSE, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param matchValue     <span class="en-US">Match value</span>
	 *                       <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greaterEqual(final int sortCode, final ConnectionCode connectionCode,
	                                      final String tableName, final String columnName, final Object matchValue) {
		return this.greaterEqual(sortCode, connectionCode, Boolean.FALSE, tableName, columnName, matchValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param matchTable     <span class="en-US">Target data table name</span>
	 *                       <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn    <span class="en-US">Target data column name</span>
	 *                       <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder less(final int sortCode, final ConnectionCode connectionCode,
	                              final String tableName, final String columnName,
	                              final String matchTable, final String matchColumn) {
		return this.less(sortCode, connectionCode, Boolean.FALSE, tableName, columnName, matchTable, matchColumn);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param functionName   <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder less(final int sortCode, final ConnectionCode connectionCode,
	                              final String tableName, final String columnName,
	                              final String functionName, final AbstractParameter<?>... functionParams) {
		return this.less(sortCode, connectionCode, Boolean.FALSE, tableName, columnName,
				functionName, functionParams);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder less(final int sortCode, final ConnectionCode connectionCode,
	                              final String tableName, final String columnName, final QueryData subQuery) {
		return this.less(sortCode, connectionCode, Boolean.FALSE, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param matchValue     <span class="en-US">Match value</span>
	 *                       <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder less(final int sortCode, final ConnectionCode connectionCode,
	                              final String tableName, final String columnName, final Object matchValue) {
		return this.less(sortCode, connectionCode, Boolean.FALSE, tableName, columnName, matchValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param matchTable     <span class="en-US">Target data table name</span>
	 *                       <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn    <span class="en-US">Target data column name</span>
	 *                       <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder lessEqual(final int sortCode, final ConnectionCode connectionCode,
	                                   final String tableName, final String columnName,
	                                   final String matchTable, final String matchColumn) {
		return this.lessEqual(sortCode, connectionCode, Boolean.FALSE, tableName, columnName,
				matchTable, matchColumn);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param functionName   <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder lessEqual(final int sortCode, final ConnectionCode connectionCode,
	                                   final String tableName, final String columnName,
	                                   final String functionName, final AbstractParameter<?>... functionParams) {
		return this.lessEqual(sortCode, connectionCode, Boolean.FALSE, tableName, columnName,
				functionName, functionParams);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder lessEqual(final int sortCode, final ConnectionCode connectionCode,
	                                   final String tableName, final String columnName, final QueryData subQuery) {
		return this.lessEqual(sortCode, connectionCode, Boolean.FALSE, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param matchValue     <span class="en-US">Match value</span>
	 *                       <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder lessEqual(final int sortCode, final ConnectionCode connectionCode,
	                                   final String tableName, final String columnName,
	                                   final Object matchValue) {
		return this.lessEqual(sortCode, connectionCode, Boolean.FALSE, tableName, columnName, matchValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition equal to a certain value</h3>
	 * <h3 class="zh-CN">添加等于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param matchTable     <span class="en-US">Target data table name</span>
	 *                       <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn    <span class="en-US">Target data column name</span>
	 *                       <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder equalTo(final int sortCode, final ConnectionCode connectionCode,
	                                 final String tableName, final String columnName,
	                                 final String matchTable, final String matchColumn) {
		return this.equalTo(sortCode, connectionCode, Boolean.FALSE, tableName, columnName,
				matchTable, matchColumn);
	}

	/**
	 * <h3 class="en-US">Add a query condition equal to a certain value</h3>
	 * <h3 class="zh-CN">添加等于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param functionName   <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder equalTo(final int sortCode, final ConnectionCode connectionCode,
	                                 final String tableName, final String columnName,
	                                 final String functionName, final AbstractParameter<?>... functionParams) {
		return this.equalTo(sortCode, connectionCode, Boolean.FALSE, tableName, columnName,
				functionName, functionParams);
	}

	/**
	 * <h3 class="en-US">Add a query condition equal to a certain value</h3>
	 * <h3 class="zh-CN">添加等于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder equalTo(final int sortCode, final ConnectionCode connectionCode,
	                                 final String tableName, final String columnName, final QueryData subQuery) {
		return this.equalTo(sortCode, connectionCode, Boolean.FALSE, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Add a query condition equal to a certain value</h3>
	 * <h3 class="zh-CN">添加等于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param matchValue     <span class="en-US">Match value</span>
	 *                       <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder equalTo(final int sortCode, final ConnectionCode connectionCode,
	                                 final String tableName, final String columnName,
	                                 final Object matchValue) {
		return this.equalTo(sortCode, connectionCode, Boolean.FALSE, tableName, columnName, matchValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition not equal to a certain value</h3>
	 * <h3 class="zh-CN">添加不等于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param matchTable     <span class="en-US">Target data table name</span>
	 *                       <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn    <span class="en-US">Target data column name</span>
	 *                       <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notEqual(final int sortCode, final ConnectionCode connectionCode,
	                                  final String tableName, final String columnName,
	                                  final String matchTable, final String matchColumn) {
		return this.notEqual(sortCode, connectionCode, Boolean.FALSE, tableName, columnName,
				matchTable, matchColumn);
	}

	/**
	 * <h3 class="en-US">Add a query condition not equal to a certain value</h3>
	 * <h3 class="zh-CN">添加不等于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param functionName   <span class="en-US">Function name</span>
	 *                       <span class="zh-CN">函数名称</span>
	 * @param functionParams <span class="en-US">Function parameter values</span>
	 *                       <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notEqual(final int sortCode, final ConnectionCode connectionCode,
	                                  final String tableName, final String columnName,
	                                  final String functionName, final AbstractParameter<?>... functionParams) {
		return this.notEqual(sortCode, connectionCode, Boolean.FALSE, tableName, columnName,
				functionName, functionParams);
	}

	/**
	 * <h3 class="en-US">Add a query condition not equal to a certain value</h3>
	 * <h3 class="zh-CN">添加不等于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notEqual(final int sortCode, final ConnectionCode connectionCode,
	                                  final String tableName, final String columnName, final QueryData subQuery) {
		return this.notEqual(sortCode, connectionCode, Boolean.FALSE, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Add a query condition not equal to a certain value</h3>
	 * <h3 class="zh-CN">添加不等于某值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param matchValue     <span class="en-US">Match value</span>
	 *                       <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notEqual(final int sortCode, final ConnectionCode connectionCode,
	                                  final String tableName, final String columnName,
	                                  final Object matchValue) {
		return this.notEqual(sortCode, connectionCode, Boolean.FALSE, tableName, columnName, matchValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition between certain two values</h3>
	 * <h3 class="zh-CN">添加介于某两个值之间的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param beginValue     <span class="en-US">Begin value</span>
	 *                       <span class="zh-CN">起始值</span>
	 * @param endValue       <span class="en-US">End value</span>
	 *                       <span class="zh-CN">终止值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder inRanges(final int sortCode, final ConnectionCode connectionCode,
	                                  final String tableName, final String columnName,
	                                  final Object beginValue, final Object endValue) {
		return this.inRanges(sortCode, connectionCode, Boolean.FALSE, tableName, columnName,
				beginValue, endValue);
	}

	/**
	 * <h3 class="en-US">Add a query condition not between certain two values</h3>
	 * <h3 class="zh-CN">添加不介于某两个值之间的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param beginValue     <span class="en-US">Begin value</span>
	 *                       <span class="zh-CN">起始值</span>
	 * @param endValue       <span class="en-US">End value</span>
	 *                       <span class="zh-CN">终止值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notInRanges(final int sortCode, final ConnectionCode connectionCode,
	                                     final String tableName, final String columnName,
	                                     final Object beginValue, final Object endValue) {
		return this.notInRanges(sortCode, connectionCode, Boolean.FALSE, tableName, columnName,
				beginValue, endValue);
	}

	/**
	 * <h3 class="en-US">Add query conditions for fuzzy matching values</h3>
	 * <h3 class="zh-CN">添加模糊匹配值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param matchRule      <span class="en-US">match rule string</span>
	 *                       <span class="zh-CN">匹配规则字符串</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder like(final int sortCode, final ConnectionCode connectionCode,
	                              final String tableName, final String columnName, final String matchRule) {
		return this.like(sortCode, connectionCode, Boolean.FALSE, tableName, columnName, matchRule);
	}

	/**
	 * <h3 class="en-US">Add query conditions for not fuzzy matching values</h3>
	 * <h3 class="zh-CN">添加非模糊匹配值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param matchRule      <span class="en-US">match rule string</span>
	 *                       <span class="zh-CN">匹配规则字符串</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notLike(final int sortCode, final ConnectionCode connectionCode,
	                                 final String tableName, final String columnName, final String matchRule) {
		return this.notLike(sortCode, connectionCode, Boolean.FALSE, tableName, columnName, matchRule);
	}

	/**
	 * <h3 class="en-US">Add query condition with null value</h3>
	 * <h3 class="zh-CN">添加空值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder matchNull(final int sortCode, final ConnectionCode connectionCode,
	                                   final String tableName, final String columnName) {
		return this.matchNull(sortCode, connectionCode, Boolean.FALSE, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Add query condition with not null value</h3>
	 * <h3 class="zh-CN">添加非空值的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notNull(final int sortCode, final ConnectionCode connectionCode,
	                                 final String tableName, final String columnName) {
		return this.notNull(sortCode, connectionCode, Boolean.FALSE, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Adds a query condition where the value is contained in the given data</h3>
	 * <h3 class="zh-CN">添加值包含在给定数据中的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder in(final int sortCode, final ConnectionCode connectionCode, final String tableName,
	                            final String columnName, final QueryData subQuery) {
		return this.in(sortCode, connectionCode, Boolean.FALSE, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Adds a query condition where the value is contained in the given data</h3>
	 * <h3 class="zh-CN">添加值包含在给定数据中的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param matchValues    <span class="en-US">Condition data array</span>
	 *                       <span class="zh-CN">匹配值数组</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder in(final int sortCode, final ConnectionCode connectionCode,
	                            final String tableName, final String columnName, final Object... matchValues) {
		return this.in(sortCode, connectionCode, Boolean.FALSE, tableName, columnName, matchValues);
	}

	/**
	 * <h3 class="en-US">Adds a query condition where the value is not contained in the given data</h3>
	 * <h3 class="zh-CN">添加值非包含在给定数据中的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notIn(final int sortCode, final ConnectionCode connectionCode, final String tableName,
	                               final String columnName, final QueryData subQuery) {
		return this.notIn(sortCode, connectionCode, Boolean.FALSE, tableName, columnName, subQuery);
	}

	/**
	 * <h3 class="en-US">Adds a query condition where the value is not contained in the given data</h3>
	 * <h3 class="zh-CN">添加值非包含在给定数据中的查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @param matchValues    <span class="en-US">Condition data array</span>
	 *                       <span class="zh-CN">匹配值数组</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notIn(final int sortCode, final ConnectionCode connectionCode,
	                               final String tableName, final String columnName, final Object... matchValues) {
		return this.notIn(sortCode, connectionCode, Boolean.FALSE, tableName, columnName, matchValues);
	}

	/**
	 * <h3 class="en-US">Adds a sub-query condition</h3>
	 * <h3 class="zh-CN">添加子查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @param functionName   <span class="en-US">Function name of sub-query</span>
	 *                       <span class="zh-CN">子查询函数名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder exists(final int sortCode, final ConnectionCode connectionCode, final String tableName,
	                                final QueryData subQuery, final String functionName) {
		return this.exists(sortCode, connectionCode, Boolean.FALSE, tableName, subQuery, functionName);
	}

	/**
	 * <h3 class="en-US">Adds a sub-query condition</h3>
	 * <h3 class="zh-CN">添加子查询条件</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param subQuery       <span class="en-US">Sub-query instance object</span>
	 *                       <span class="zh-CN">子查询实例对象</span>
	 * @param functionName   <span class="en-US">Function name of sub-query</span>
	 *                       <span class="zh-CN">子查询函数名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notExists(final int sortCode, final ConnectionCode connectionCode, final String tableName,
	                                   final QueryData subQuery, final String functionName) {
		return this.notExists(sortCode, connectionCode, Boolean.FALSE, tableName, subQuery, functionName);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchValue      <span class="en-US">Match value</span>
	 *                        <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greater(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                                 final String tableName, final String columnName,
	                                 final Object matchValue) {
		return this.addCondition(
				Condition.column(sortCode, connectionCode, ConditionCode.GREATER, tableName, columnName,
						AbstractParameter.constant(matchValue)),
				havingCondition);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchTable      <span class="en-US">Target data table name</span>
	 *                        <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn     <span class="en-US">Target data column name</span>
	 *                        <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greater(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                                 final String tableName, final String columnName,
	                                 final String matchTable, final String matchColumn) {
		return this.addCondition(
				Condition.column(sortCode, connectionCode, ConditionCode.GREATER, tableName, columnName,
						AbstractParameter.column(matchTable, matchColumn, Globals.DEFAULT_VALUE_STRING)),
				havingCondition);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param functionName    <span class="en-US">Function name</span>
	 *                        <span class="zh-CN">函数名称</span>
	 * @param functionParams  <span class="en-US">Function parameter values</span>
	 *                        <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greater(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                                 final String tableName, final String columnName,
	                                 final String functionName, final AbstractParameter<?>... functionParams) {
		return this.addCondition(
				Condition.column(sortCode, connectionCode, ConditionCode.GREATER, tableName, columnName,
						AbstractParameter.function(functionName, functionParams)),
				havingCondition);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param subQuery        <span class="en-US">Sub-query instance object</span>
	 *                        <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greater(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                                 final String tableName, final String columnName, final QueryData subQuery) {
		return this.addCondition(
				Condition.column(sortCode, connectionCode, ConditionCode.GREATER, tableName, columnName,
						AbstractParameter.subQuery(subQuery)),
				havingCondition);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchTable      <span class="en-US">Target data table name</span>
	 *                        <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn     <span class="en-US">Target data column name</span>
	 *                        <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greaterEqual(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                                      final String tableName, final String columnName,
	                                      final String matchTable, final String matchColumn) {
		return this.addCondition(
				Condition.column(sortCode, connectionCode, ConditionCode.GREATER_EQUAL, tableName, columnName,
						AbstractParameter.column(matchTable, matchColumn, Globals.DEFAULT_VALUE_STRING)),
				havingCondition);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param functionName    <span class="en-US">Function name</span>
	 *                        <span class="zh-CN">函数名称</span>
	 * @param functionParams  <span class="en-US">Function parameter values</span>
	 *                        <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greaterEqual(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                                      final String tableName, final String columnName,
	                                      final String functionName, final AbstractParameter<?>... functionParams) {
		return this.addCondition(
				Condition.column(sortCode, connectionCode, ConditionCode.GREATER_EQUAL, tableName, columnName,
						AbstractParameter.function(functionName, functionParams)),
				havingCondition);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param subQuery        <span class="en-US">Sub-query instance object</span>
	 *                        <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greaterEqual(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                                      final String tableName, final String columnName,
	                                      final QueryData subQuery) {
		return this.addCondition(
				Condition.column(sortCode, connectionCode, ConditionCode.GREATER_EQUAL, tableName, columnName,
						AbstractParameter.subQuery(subQuery)),
				havingCondition);
	}

	/**
	 * <h3 class="en-US">Add a query condition greater than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchValue      <span class="en-US">Match value</span>
	 *                        <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder greaterEqual(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                                      final String tableName, final String columnName,
	                                      final Object matchValue) {
		return this.addCondition(
				Condition.column(sortCode, connectionCode, ConditionCode.GREATER_EQUAL, tableName, columnName,
						AbstractParameter.constant(matchValue)),
				havingCondition);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchTable      <span class="en-US">Target data table name</span>
	 *                        <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn     <span class="en-US">Target data column name</span>
	 *                        <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder less(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                              final String tableName, final String columnName,
	                              final String matchTable, final String matchColumn) {
		return this.addCondition(
				Condition.column(sortCode, connectionCode, ConditionCode.LESS, tableName, columnName,
						AbstractParameter.column(matchTable, matchColumn, Globals.DEFAULT_VALUE_STRING)),
				havingCondition);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param functionName    <span class="en-US">Function name</span>
	 *                        <span class="zh-CN">函数名称</span>
	 * @param functionParams  <span class="en-US">Function parameter values</span>
	 *                        <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder less(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                              final String tableName, final String columnName,
	                              final String functionName, final AbstractParameter<?>... functionParams) {
		return this.addCondition(
				Condition.column(sortCode, connectionCode, ConditionCode.LESS, tableName, columnName,
						AbstractParameter.function(functionName, functionParams)),
				havingCondition);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param subQuery        <span class="en-US">Sub-query instance object</span>
	 *                        <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder less(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                              final String tableName, final String columnName, final QueryData subQuery) {
		return this.addCondition(
				Condition.column(sortCode, connectionCode, ConditionCode.LESS, tableName, columnName,
						AbstractParameter.subQuery(subQuery)),
				havingCondition);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than a certain value</h3>
	 * <h3 class="zh-CN">添加大于某值的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchValue      <span class="en-US">Match value</span>
	 *                        <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder less(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                              final String tableName, final String columnName, final Object matchValue) {
		return this.addCondition(
				Condition.column(sortCode, connectionCode, ConditionCode.LESS, tableName, columnName,
						AbstractParameter.constant(matchValue)),
				havingCondition);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchTable      <span class="en-US">Target data table name</span>
	 *                        <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn     <span class="en-US">Target data column name</span>
	 *                        <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder lessEqual(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                                   final String tableName, final String columnName,
	                                   final String matchTable, final String matchColumn) {
		return this.addCondition(
				Condition.column(sortCode, connectionCode, ConditionCode.LESS_EQUAL, tableName, columnName,
						AbstractParameter.column(matchTable, matchColumn, Globals.DEFAULT_VALUE_STRING)),
				havingCondition);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param functionName    <span class="en-US">Function name</span>
	 *                        <span class="zh-CN">函数名称</span>
	 * @param functionParams  <span class="en-US">Function parameter values</span>
	 *                        <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder lessEqual(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                                   final String tableName, final String columnName,
	                                   final String functionName, final AbstractParameter<?>... functionParams) {
		return this.addCondition(
				Condition.column(sortCode, connectionCode, ConditionCode.LESS_EQUAL, tableName, columnName,
						AbstractParameter.function(functionName, functionParams)),
				havingCondition);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param subQuery        <span class="en-US">Sub-query instance object</span>
	 *                        <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder lessEqual(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                                   final String tableName, final String columnName, final QueryData subQuery) {
		return this.addCondition(
				Condition.column(sortCode, connectionCode, ConditionCode.LESS_EQUAL, tableName, columnName,
						AbstractParameter.subQuery(subQuery)),
				havingCondition);
	}

	/**
	 * <h3 class="en-US">Add a query condition less than or equal to a certain value</h3>
	 * <h3 class="zh-CN">添加大于等于某值的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchValue      <span class="en-US">Match value</span>
	 *                        <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder lessEqual(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                                   final String tableName, final String columnName,
	                                   final Object matchValue) {
		return this.addCondition(
				Condition.column(sortCode, connectionCode, ConditionCode.LESS_EQUAL, tableName, columnName,
						AbstractParameter.constant(matchValue)),
				havingCondition);
	}

	/**
	 * <h3 class="en-US">Add a query condition equal to a certain value</h3>
	 * <h3 class="zh-CN">添加等于某值的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchTable      <span class="en-US">Target data table name</span>
	 *                        <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn     <span class="en-US">Target data column name</span>
	 *                        <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder equalTo(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                                 final String tableName, final String columnName,
	                                 final String matchTable, final String matchColumn) {
		return this.addCondition(
				Condition.column(sortCode, connectionCode, ConditionCode.EQUAL, tableName, columnName,
						AbstractParameter.column(matchTable, matchColumn, Globals.DEFAULT_VALUE_STRING)),
				havingCondition);
	}

	/**
	 * <h3 class="en-US">Add a query condition equal to a certain value</h3>
	 * <h3 class="zh-CN">添加等于某值的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param functionName    <span class="en-US">Function name</span>
	 *                        <span class="zh-CN">函数名称</span>
	 * @param functionParams  <span class="en-US">Function parameter values</span>
	 *                        <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder equalTo(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                                 final String tableName, final String columnName,
	                                 final String functionName, final AbstractParameter<?>... functionParams) {
		return this.addCondition(
				Condition.column(sortCode, connectionCode, ConditionCode.EQUAL, tableName, columnName,
						AbstractParameter.function(functionName, functionParams)),
				havingCondition);
	}

	/**
	 * <h3 class="en-US">Add a query condition equal to a certain value</h3>
	 * <h3 class="zh-CN">添加等于某值的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param subQuery        <span class="en-US">Sub-query instance object</span>
	 *                        <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder equalTo(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                                 final String tableName, final String columnName, final QueryData subQuery) {
		return this.addCondition(
				Condition.column(sortCode, connectionCode, ConditionCode.EQUAL, tableName, columnName,
						AbstractParameter.subQuery(subQuery)),
				havingCondition);
	}

	/**
	 * <h3 class="en-US">Add a query condition equal to a certain value</h3>
	 * <h3 class="zh-CN">添加等于某值的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchValue      <span class="en-US">Match value</span>
	 *                        <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder equalTo(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                                 final String tableName, final String columnName,
	                                 final Object matchValue) {
		return this.addCondition(
				Condition.column(sortCode, connectionCode, ConditionCode.EQUAL, tableName, columnName,
						AbstractParameter.constant(matchValue)),
				havingCondition);
	}

	/**
	 * <h3 class="en-US">Add a query condition not equal to a certain value</h3>
	 * <h3 class="zh-CN">添加不等于某值的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchTable      <span class="en-US">Target data table name</span>
	 *                        <span class="zh-CN">目标数据表名</span>
	 * @param matchColumn     <span class="en-US">Target data column name</span>
	 *                        <span class="zh-CN">目标数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notEqual(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                                  final String tableName, final String columnName,
	                                  final String matchTable, final String matchColumn) {
		return this.addCondition(
				Condition.column(sortCode, connectionCode, ConditionCode.NOT_EQUAL,
						tableName, columnName,
						AbstractParameter.column(matchTable, matchColumn, Globals.DEFAULT_VALUE_STRING)),
				havingCondition);
	}

	/**
	 * <h3 class="en-US">Add a query condition not equal to a certain value</h3>
	 * <h3 class="zh-CN">添加不等于某值的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param functionName    <span class="en-US">Function name</span>
	 *                        <span class="zh-CN">函数名称</span>
	 * @param functionParams  <span class="en-US">Function parameter values</span>
	 *                        <span class="zh-CN">函数参数值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notEqual(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                                  final String tableName, final String columnName,
	                                  final String functionName, final AbstractParameter<?>... functionParams) {
		return this.addCondition(
				Condition.column(sortCode, connectionCode, ConditionCode.NOT_EQUAL, tableName, columnName,
						AbstractParameter.function(functionName, functionParams)),
				havingCondition);
	}

	/**
	 * <h3 class="en-US">Add a query condition not equal to a certain value</h3>
	 * <h3 class="zh-CN">添加不等于某值的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param subQuery        <span class="en-US">Sub-query instance object</span>
	 *                        <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notEqual(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                                  final String tableName, final String columnName, final QueryData subQuery) {
		return this.addCondition(
				Condition.column(sortCode, connectionCode, ConditionCode.NOT_EQUAL, tableName, columnName,
						AbstractParameter.subQuery(subQuery)),
				havingCondition);
	}

	/**
	 * <h3 class="en-US">Add a query condition not equal to a certain value</h3>
	 * <h3 class="zh-CN">添加不等于某值的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchValue      <span class="en-US">Match value</span>
	 *                        <span class="zh-CN">匹配值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notEqual(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                                  final String tableName, final String columnName,
	                                  final Object matchValue) {
		return this.addCondition(
				Condition.column(sortCode, connectionCode, ConditionCode.NOT_EQUAL, tableName, columnName,
						AbstractParameter.constant(matchValue)),
				havingCondition);
	}

	/**
	 * <h3 class="en-US">Add a query condition between certain two values</h3>
	 * <h3 class="zh-CN">添加介于某两个值之间的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param beginValue      <span class="en-US">Begin value</span>
	 *                        <span class="zh-CN">起始值</span>
	 * @param endValue        <span class="en-US">End value</span>
	 *                        <span class="zh-CN">终止值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder inRanges(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                                  final String tableName, final String columnName,
	                                  final Object beginValue, final Object endValue) {
		return this.addCondition(
				Condition.column(sortCode, connectionCode, ConditionCode.BETWEEN_AND,
						tableName, columnName, AbstractParameter.ranges(beginValue, endValue)),
				havingCondition);
	}

	/**
	 * <h3 class="en-US">Add a query condition not between certain two values</h3>
	 * <h3 class="zh-CN">添加不介于某两个值之间的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param beginValue      <span class="en-US">Begin value</span>
	 *                        <span class="zh-CN">起始值</span>
	 * @param endValue        <span class="en-US">End value</span>
	 *                        <span class="zh-CN">终止值</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notInRanges(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                                     final String tableName, final String columnName,
	                                     final Object beginValue, final Object endValue) {
		return this.addCondition(Condition.column(sortCode, connectionCode, ConditionCode.NOT_BETWEEN_AND,
				tableName, columnName, AbstractParameter.ranges(beginValue, endValue)), havingCondition);
	}

	/**
	 * <h3 class="en-US">Add query conditions for fuzzy matching values</h3>
	 * <h3 class="zh-CN">添加模糊匹配值的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchRule       <span class="en-US">match rule string</span>
	 *                        <span class="zh-CN">匹配规则字符串</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder like(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                              final String tableName, final String columnName, final String matchRule) {
		return this.addCondition(Condition.column(sortCode, connectionCode, ConditionCode.LIKE,
				tableName, columnName, AbstractParameter.constant(matchRule)), havingCondition);
	}

	/**
	 * <h3 class="en-US">Add query conditions for not fuzzy matching values</h3>
	 * <h3 class="zh-CN">添加非模糊匹配值的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchRule       <span class="en-US">match rule string</span>
	 *                        <span class="zh-CN">匹配规则字符串</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notLike(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                                 final String tableName, final String columnName, final String matchRule) {
		return this.addCondition(Condition.column(sortCode, connectionCode, ConditionCode.NOT_LIKE,
				tableName, columnName, AbstractParameter.constant(matchRule)), havingCondition);
	}

	/**
	 * <h3 class="en-US">Add query condition with null value</h3>
	 * <h3 class="zh-CN">添加空值的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder matchNull(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                                   final String tableName, final String columnName) {
		return this.addCondition(
				Condition.column(sortCode, connectionCode, ConditionCode.IS_NULL,
						tableName, columnName, null),
				havingCondition);
	}

	/**
	 * <h3 class="en-US">Add query condition with not null value</h3>
	 * <h3 class="zh-CN">添加非空值的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notNull(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                                 final String tableName, final String columnName) {
		return this.addCondition(
				Condition.column(sortCode, connectionCode, ConditionCode.NOT_NULL, tableName,
						columnName, null),
				havingCondition);
	}

	/**
	 * <h3 class="en-US">Adds a query condition where the value is contained in the given data</h3>
	 * <h3 class="zh-CN">添加值包含在给定数据中的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param subQuery        <span class="en-US">Sub-query instance object</span>
	 *                        <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder in(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                            final String tableName, final String columnName, final QueryData subQuery) {
		return this.addCondition(Condition.column(sortCode, connectionCode, ConditionCode.IN,
				tableName, columnName, AbstractParameter.subQuery(subQuery)), havingCondition);
	}

	/**
	 * <h3 class="en-US">Adds a query condition where the value is contained in the given data</h3>
	 * <h3 class="zh-CN">添加值包含在给定数据中的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchValues     <span class="en-US">Condition data array</span>
	 *                        <span class="zh-CN">匹配值数组</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder in(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                            final String tableName, final String columnName, final Object... matchValues) {
		return this.addCondition(Condition.column(sortCode, connectionCode, ConditionCode.IN,
				tableName, columnName, AbstractParameter.arrays(matchValues)), havingCondition);
	}

	/**
	 * <h3 class="en-US">Adds a query condition where the value is not contained in the given data</h3>
	 * <h3 class="zh-CN">添加值非包含在给定数据中的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param subQuery        <span class="en-US">Sub-query instance object</span>
	 *                        <span class="zh-CN">子查询实例对象</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notIn(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                               final String tableName, final String columnName, final QueryData subQuery) {
		return this.addCondition(Condition.column(sortCode, connectionCode, ConditionCode.NOT_IN,
				tableName, columnName, AbstractParameter.subQuery(subQuery)), havingCondition);
	}

	/**
	 * <h3 class="en-US">Adds a query condition where the value is not contained in the given data</h3>
	 * <h3 class="zh-CN">添加值非包含在给定数据中的查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param columnName      <span class="en-US">Data column name</span>
	 *                        <span class="zh-CN">数据列名</span>
	 * @param matchValues     <span class="en-US">Condition data array</span>
	 *                        <span class="zh-CN">匹配值数组</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notIn(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                               final String tableName, final String columnName, final Object... matchValues) {
		return this.addCondition(Condition.column(sortCode, connectionCode, ConditionCode.NOT_IN,
				tableName, columnName, AbstractParameter.arrays(matchValues)), havingCondition);
	}

	/**
	 * <h3 class="en-US">Adds a sub-query condition</h3>
	 * <h3 class="zh-CN">添加子查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param subQuery        <span class="en-US">Sub-query instance object</span>
	 *                        <span class="zh-CN">子查询实例对象</span>
	 * @param functionName    <span class="en-US">Function name of sub-query</span>
	 *                        <span class="zh-CN">子查询函数名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder exists(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                                final String tableName, final QueryData subQuery, final String functionName) {
		return this.addCondition(Condition.column(sortCode, connectionCode, ConditionCode.EXISTS,
				tableName, Globals.DEFAULT_VALUE_STRING,
				AbstractParameter.subQuery(subQuery, functionName)), havingCondition);
	}

	/**
	 * <h3 class="en-US">Adds a sub-query condition</h3>
	 * <h3 class="zh-CN">添加子查询条件</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param tableName       <span class="en-US">Data table name</span>
	 *                        <span class="zh-CN">数据表名</span>
	 * @param subQuery        <span class="en-US">Sub-query instance object</span>
	 *                        <span class="zh-CN">子查询实例对象</span>
	 * @param functionName    <span class="en-US">Function name of sub-query</span>
	 *                        <span class="zh-CN">子查询函数名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder notExists(final int sortCode, final ConnectionCode connectionCode, final boolean havingCondition,
	                                   final String tableName, final QueryData subQuery, final String functionName) {
		return this.addCondition(Condition.column(sortCode, connectionCode, ConditionCode.NOT_EXISTS,
				tableName, Globals.DEFAULT_VALUE_STRING,
				AbstractParameter.subQuery(subQuery, functionName)), havingCondition);
	}

	/**
	 * <h3 class="en-US">Add data group condition</h3>
	 * <h3 class="zh-CN">添加数据筛选条件组</h3>
	 *
	 * @param sortCode       <span class="en-US">Sort code</span>
	 *                       <span class="zh-CN">排序代码</span>
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param conditions     <span class="en-US">Condition information array</span>
	 *                       <span class="zh-CN">条件信息数组</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder group(final int sortCode, final ConnectionCode connectionCode,
	                               @Nonnull final Condition... conditions) {
		return this.addCondition(Condition.group(sortCode, connectionCode, conditions), Boolean.FALSE);
	}

	/**
	 * <h3 class="en-US">Add data group condition</h3>
	 * <h3 class="zh-CN">添加数据筛选条件组</h3>
	 *
	 * @param sortCode        <span class="en-US">Sort code</span>
	 *                        <span class="zh-CN">排序代码</span>
	 * @param connectionCode  <span class="en-US">Query connection code</span>
	 *                        <span class="zh-CN">查询条件连接代码</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @param conditions      <span class="en-US">Condition information array</span>
	 *                        <span class="zh-CN">条件信息数组</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder group(final int sortCode, final ConnectionCode connectionCode,
	                               final boolean havingCondition, @Nonnull final Condition... conditions) {
		return this.addCondition(Condition.group(sortCode, connectionCode, conditions), havingCondition);
	}

	/**
	 * <h3 class="en-US">Add order by data column</h3>
	 * <h3 class="zh-CN">添加排序数据列</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws SQLException <span class="en-US">If the sorting data column already exists</span>
	 *                      <span class="zh-CN">如果排序数据列已经存在</span>
	 */
	public BrainQueryBuilder addOrderBy(final String tableName, final String columnName) throws SQLException {
		return this.addOrderBy(tableName, columnName, OrderType.DESC);
	}

	/**
	 * <h3 class="en-US">Add order by data column</h3>
	 * <h3 class="zh-CN">添加排序数据列</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @param orderType  <span class="en-US">Query order type</span>
	 *                   <span class="zh-CN">查询结果集排序类型</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws SQLException <span class="en-US">If the sorting data column already exists</span>
	 *                      <span class="zh-CN">如果排序数据列已经存在</span>
	 */
	public BrainQueryBuilder addOrderBy(final String tableName, final String columnName, final OrderType orderType)
			throws SQLException {
		return this.addOrderBy(tableName, columnName, orderType, Globals.DEFAULT_VALUE_INT);
	}

	/**
	 * <h3 class="en-US">Add order by data column</h3>
	 * <h3 class="zh-CN">添加排序数据列</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @param orderType  <span class="en-US">Query order type</span>
	 *                   <span class="zh-CN">查询结果集排序类型</span>
	 * @param sortCode   <span class="en-US">Sort code</span>
	 *                   <span class="zh-CN">排序代码</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws SQLException <span class="en-US">If the sorting data column already exists</span>
	 *                      <span class="zh-CN">如果排序数据列已经存在</span>
	 */
	public BrainQueryBuilder addOrderBy(final String tableName, final String columnName,
	                                    final OrderType orderType, final int sortCode) throws SQLException {
		if (this.orderByList.stream().anyMatch(orderBy -> orderBy.match(tableName, columnName))) {
			throw new MultilingualSQLException(0x00DB00010017L, tableName, columnName);
		}
		this.orderByList.add(new OrderBy(tableName, columnName, orderType, sortCode));
		return this;
	}

	/**
	 * <h3 class="en-US">Add group by data column</h3>
	 * <h3 class="zh-CN">添加分组数据列</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws SQLException <span class="en-US">If the grouping data column already exists</span>
	 *                      <span class="zh-CN">如果分组数据列已经存在</span>
	 */
	public BrainQueryBuilder addGroupBy(final String tableName, final String columnName) throws SQLException {
		return this.addGroupBy(tableName, columnName, Globals.DEFAULT_VALUE_INT);
	}

	/**
	 * <h3 class="en-US">Add group by data column</h3>
	 * <h3 class="zh-CN">添加分组数据列</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @param sortCode   <span class="en-US">Sort code</span>
	 *                   <span class="zh-CN">排序代码</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 * @throws SQLException <span class="en-US">If the grouping data column already exists</span>
	 *                      <span class="zh-CN">如果分组数据列已经存在</span>
	 */
	public BrainQueryBuilder addGroupBy(final String tableName, final String columnName, final int sortCode)
			throws SQLException {
		if (this.groupByList.stream().anyMatch(groupBy -> groupBy.match(tableName, columnName))) {
			throw new MultilingualSQLException(0x00DB00010018L, tableName, columnName);
		}
		this.groupByList.add(new GroupBy(tableName, columnName, sortCode));
		return this;
	}

	/**
	 * <h3 class="en-US">Setting for query result can cacheable</h3>
	 * <h3 class="zh-CN">设置查询结果可以缓存</h3>
	 *
	 * @param cacheables <span class="en-US">Query result can cacheable</span>
	 *                   <span class="zh-CN">查询结果可以缓存</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainQueryBuilder useCache(final boolean cacheables) {
		this.cacheables = cacheables;
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
	public BrainQueryBuilder configPager(final int pageNo, final int pageLimit) throws SQLException {
		if (pageNo <= Globals.INITIALIZE_INT_VALUE || pageLimit <= Globals.INITIALIZE_INT_VALUE) {
			throw new MultilingualSQLException(0x00DB00010013L, pageNo, pageLimit);
		}
		this.pageNo = pageNo;
		this.pageLimit = pageLimit;
		return this;
	}

	/**
	 * <h3 class="en-US">Fill query data column information</h3>
	 * <h3 class="zh-CN">填充查询数据列信息</h3>
	 *
	 * @throws SQLException <span class="en-US">If the query data column already exists</span>
	 *                      <span class="zh-CN">如果查询数据列已存在</span>
	 */
	public void itemList() throws SQLException {
		if (this.itemListIsEmpty()) {
			BrainDataSource dataSource = BrainDataSource.getInstance();
			if (dataSource.isInitialized()) {
				for (String columnName : dataSource.queryColumns(this.tableName)) {
					this.queryColumn(this.tableName, columnName);
				}
			}
		}
	}

	/**
	 * <h3 class="en-US">Check if the current query item list is empty</h3>
	 * <h3 class="zh-CN">检查当前的查询项目列表是否为空</h3>
	 *
	 * @return <span class="en-US">Check result</span>
	 * <span class="zh-CN">检查结果</span>
	 */
	public boolean itemListIsEmpty() {
		return this.itemList.isEmpty();
	}

	@Override
	public QueryInfo confirm() {
		QueryInfo queryInfo = new QueryInfo();
		queryInfo.setTableName(this.tableName);
		queryInfo.setAliasName(this.aliasName);
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
		return queryInfo;
	}

	/**
	 * <h3 class="en-US">Add data filters condition</h3>
	 * <h3 class="zh-CN">添加数据筛选条件</h3>
	 *
	 * @param condition       <span class="en-US">Condition information</span>
	 *                        <span class="zh-CN">条件信息</span>
	 * @param havingCondition <span class="en-US">Condition information is having condition</span>
	 *                        <span class="zh-CN">Having字句的条件信息</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	private BrainQueryBuilder addCondition(@Nonnull final Condition condition, final boolean havingCondition) {
		if (havingCondition) {
			this.havingList.add(condition);
		} else {
			this.conditionList.add(condition);
		}
		return this;
	}
}
