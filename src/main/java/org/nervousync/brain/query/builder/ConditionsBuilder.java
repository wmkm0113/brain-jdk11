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
import org.nervousync.brain.enumerations.query.ConnectionCode;
import org.nervousync.brain.query.condition.Condition;
import org.nervousync.brain.query.condition.impl.ColumnCondition;
import org.nervousync.brain.query.condition.impl.GroupCondition;
import org.nervousync.brain.query.core.AbstractQuery;
import org.nervousync.brain.query.param.AbstractParameter;
import org.nervousync.brain.query.param.impl.FunctionParameter;
import org.nervousync.builder.AbstractBuilder;
import org.nervousync.builder.ParentBuilder;
import org.nervousync.exceptions.builder.BuilderException;

import java.util.ArrayList;
import java.util.List;

/**
 * <h2 class="en-US">Query conditions information list builder</h2>
 * <h2 class="zh-CN">查询条件信息列表构建器</h2>
 *
 * @param <P> <span class="en-US">Parent builder generic type class</span>
 *            <span class="zh-CN">父构建器泛型类</span>
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 28, 2020 11:46:08 $
 */
public final class ConditionsBuilder<P extends ParentBuilder> extends AbstractBuilder<P, ConditionsBuilder.Conditions> {

	/**
	 * <span class="en-US">Query condition instance list</span>
	 * <span class="zh-CN">查询条件实例对象列表</span>
	 */
	@Nonnull
	private final List<Condition> conditions = new ArrayList<>();
	/**
	 * <span class="en-US">Having condition flag</span>
	 * <span class="zh-CN">Having字句条件标记</span>
	 */
	private final boolean having;

	/**
	 * <h3 class="en-US">Protected constructor for AbstractBuilder</h3>
	 * <h3 class="zh-CN">AbstractBuilder的构造函数</h3>
	 *
	 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
	 *                      <span class="zh-CN">父构建器实例对象</span>
	 * @param having        <span class="en-US">Having condition flag</span>
	 *                      <span class="zh-CN">Having字句条件标记</span>
	 * @param conditions    <span class="en-US">Query condition instance list</span>
	 *                      <span class="zh-CN">查询条件实例对象列表</span>
	 */
	public ConditionsBuilder(final P parentBuilder, final boolean having, final List<Condition> conditions) {
		super(parentBuilder);
		this.having = having;
		if (conditions != null) {
			this.conditions.addAll(conditions);
		}
	}

	/**
	 * <h3 class="en-US">Data column less condition information builder</h3>
	 * <h3 class="zh-CN">数据列小于条件信息构建器</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column query condition information builder instance object</span>
	 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
	 */
	public ColumnConditionBuilder<ConditionsBuilder<P>> less(final String tableName, final String columnName) {
		return this.less(ConnectionCode.AND, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Data column less or equal condition information builder</h3>
	 * <h3 class="zh-CN">数据列小于等于条件信息构建器</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column query condition information builder instance object</span>
	 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
	 */
	public ColumnConditionBuilder<ConditionsBuilder<P>> lessEqual(final String tableName, final String columnName) {
		return this.lessEqual(ConnectionCode.AND, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Data column greater condition information builder</h3>
	 * <h3 class="zh-CN">数据列大于条件信息构建器</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column query condition information builder instance object</span>
	 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
	 */
	public ColumnConditionBuilder<ConditionsBuilder<P>> greater(final String tableName, final String columnName) {
		return this.greater(ConnectionCode.AND, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Data column greater or equal condition information builder</h3>
	 * <h3 class="zh-CN">数据列大于等于条件信息构建器</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column query condition information builder instance object</span>
	 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
	 */
	public ColumnConditionBuilder<ConditionsBuilder<P>> greaterEqual(final String tableName, final String columnName) {
		return this.greaterEqual(ConnectionCode.AND, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Data column equal condition information builder</h3>
	 * <h3 class="zh-CN">数据列等于条件信息构建器</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column query condition information builder instance object</span>
	 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
	 */
	public ColumnConditionBuilder<ConditionsBuilder<P>> equalTo(final String tableName, final String columnName) {
		return this.equalTo(ConnectionCode.AND, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Data column not equal condition information builder</h3>
	 * <h3 class="zh-CN">数据列不等于条件信息构建器</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column query condition information builder instance object</span>
	 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
	 */
	public ColumnConditionBuilder<ConditionsBuilder<P>> notEqual(final String tableName, final String columnName) {
		return this.notEqual(ConnectionCode.AND, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Data column in arrays condition information builder</h3>
	 * <h3 class="zh-CN">数据列在数组中条件信息构建器</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column query condition information builder instance object</span>
	 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
	 */
	public ColumnConditionBuilder<ConditionsBuilder<P>> in(final String tableName, final String columnName) {
		return this.in(ConnectionCode.AND, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Data column not in arrays condition information builder</h3>
	 * <h3 class="zh-CN">数据列不在数组中条件信息构建器</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column query condition information builder instance object</span>
	 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
	 */
	public ColumnConditionBuilder<ConditionsBuilder<P>> notIn(final String tableName, final String columnName) {
		return this.notIn(ConnectionCode.AND, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Data column in ranges condition information builder</h3>
	 * <h3 class="zh-CN">数据列在指定区间条件信息构建器</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column query condition information builder instance object</span>
	 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
	 */
	public ColumnConditionBuilder<ConditionsBuilder<P>> betweenAnd(final String tableName, final String columnName) {
		return this.betweenAnd(ConnectionCode.AND, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Data column in ranges condition information builder</h3>
	 * <h3 class="zh-CN">数据列不在指定区间条件信息构建器</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column query condition information builder instance object</span>
	 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
	 */
	public ColumnConditionBuilder<ConditionsBuilder<P>> notBetweenAnd(final String tableName, final String columnName) {
		return this.notBetweenAnd(ConnectionCode.AND, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Data column is null condition information builder</h3>
	 * <h3 class="zh-CN">数据列为空条件信息构建器</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column query condition information builder instance object</span>
	 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
	 */
	public ConditionsBuilder<P> isNull(final String tableName, final String columnName) {
		return this.isNull(ConnectionCode.AND, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Data column not null condition information builder</h3>
	 * <h3 class="zh-CN">数据列不为空条件信息构建器</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column query condition information builder instance object</span>
	 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
	 */
	public ConditionsBuilder<P> notNull(final String tableName, final String columnName) {
		return this.notNull(ConnectionCode.AND, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Data column like condition information builder</h3>
	 * <h3 class="zh-CN">数据列模糊匹配条件信息构建器</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column query condition information builder instance object</span>
	 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
	 */
	public ColumnConditionBuilder<ConditionsBuilder<P>> like(final String tableName, final String columnName) {
		return this.like(ConnectionCode.AND, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Data column is not like condition information builder</h3>
	 * <h3 class="zh-CN">数据列非模糊匹配条件信息构建器</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column query condition information builder instance object</span>
	 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
	 */
	public ColumnConditionBuilder<ConditionsBuilder<P>> notLike(final String tableName, final String columnName) {
		return this.notLike(ConnectionCode.AND, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Data exists condition information builder</h3>
	 * <h3 class="zh-CN">数据存在条件信息构建器</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column query condition information builder instance object</span>
	 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
	 */
	public ColumnConditionBuilder<ConditionsBuilder<P>> exists(final String tableName, final String columnName) {
		return this.exists(ConnectionCode.AND, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Data don't exist condition information builder</h3>
	 * <h3 class="zh-CN">数据不存在条件信息构建器</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column query condition information builder instance object</span>
	 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
	 */
	public ColumnConditionBuilder<ConditionsBuilder<P>> notExists(final String tableName, final String columnName) {
		return this.notExists(ConnectionCode.AND, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Data column less condition information builder</h3>
	 * <h3 class="zh-CN">数据列小于条件信息构建器</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column query condition information builder instance object</span>
	 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
	 */
	public ColumnConditionBuilder<ConditionsBuilder<P>> less(final ConnectionCode connectionCode,
	                                                         final String tableName, final String columnName) {
		return this.column(connectionCode, ConditionCode.LESS, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Data column less or equal condition information builder</h3>
	 * <h3 class="zh-CN">数据列小于等于条件信息构建器</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column query condition information builder instance object</span>
	 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
	 */
	public ColumnConditionBuilder<ConditionsBuilder<P>> lessEqual(final ConnectionCode connectionCode,
	                                                              final String tableName, final String columnName) {
		return this.column(connectionCode, ConditionCode.LESS_EQUAL, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Data column greater condition information builder</h3>
	 * <h3 class="zh-CN">数据列大于条件信息构建器</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column query condition information builder instance object</span>
	 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
	 */
	public ColumnConditionBuilder<ConditionsBuilder<P>> greater(final ConnectionCode connectionCode,
	                                                            final String tableName, final String columnName) {
		return this.column(connectionCode, ConditionCode.GREATER, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Data column greater or equal condition information builder</h3>
	 * <h3 class="zh-CN">数据列大于等于条件信息构建器</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column query condition information builder instance object</span>
	 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
	 */
	public ColumnConditionBuilder<ConditionsBuilder<P>> greaterEqual(final ConnectionCode connectionCode,
	                                                                 final String tableName, final String columnName) {
		return this.column(connectionCode, ConditionCode.GREATER_EQUAL, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Data column equal condition information builder</h3>
	 * <h3 class="zh-CN">数据列等于条件信息构建器</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column query condition information builder instance object</span>
	 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
	 */
	public ColumnConditionBuilder<ConditionsBuilder<P>> equalTo(final ConnectionCode connectionCode,
	                                                            final String tableName, final String columnName) {
		return this.column(connectionCode, ConditionCode.EQUAL, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Data column not equal condition information builder</h3>
	 * <h3 class="zh-CN">数据列不等于条件信息构建器</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column query condition information builder instance object</span>
	 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
	 */
	public ColumnConditionBuilder<ConditionsBuilder<P>> notEqual(final ConnectionCode connectionCode,
	                                                             final String tableName, final String columnName) {
		return this.column(connectionCode, ConditionCode.NOT_EQUAL, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Data column in arrays condition information builder</h3>
	 * <h3 class="zh-CN">数据列在数组中条件信息构建器</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column query condition information builder instance object</span>
	 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
	 */
	public ColumnConditionBuilder<ConditionsBuilder<P>> in(final ConnectionCode connectionCode,
	                                                       final String tableName, final String columnName) {
		return this.column(connectionCode, ConditionCode.IN, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Data column not in arrays condition information builder</h3>
	 * <h3 class="zh-CN">数据列不在数组中条件信息构建器</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column query condition information builder instance object</span>
	 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
	 */
	public ColumnConditionBuilder<ConditionsBuilder<P>> notIn(final ConnectionCode connectionCode,
	                                                          final String tableName, final String columnName) {
		return this.column(connectionCode, ConditionCode.NOT_IN, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Data column in ranges condition information builder</h3>
	 * <h3 class="zh-CN">数据列在指定区间条件信息构建器</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column query condition information builder instance object</span>
	 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
	 */
	public ColumnConditionBuilder<ConditionsBuilder<P>> betweenAnd(final ConnectionCode connectionCode,
	                                                               final String tableName, final String columnName) {
		return this.column(connectionCode, ConditionCode.BETWEEN_AND, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Data column in ranges condition information builder</h3>
	 * <h3 class="zh-CN">数据列不在指定区间条件信息构建器</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column query condition information builder instance object</span>
	 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
	 */
	public ColumnConditionBuilder<ConditionsBuilder<P>> notBetweenAnd(final ConnectionCode connectionCode,
	                                                                  final String tableName, final String columnName) {
		return this.column(connectionCode, ConditionCode.NOT_BETWEEN_AND, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Data column is null condition information builder</h3>
	 * <h3 class="zh-CN">数据列为空条件信息构建器</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column query condition information builder instance object</span>
	 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
	 */
	public ConditionsBuilder<P> isNull(final ConnectionCode connectionCode,
	                                   final String tableName, final String columnName) {
		return this.column(connectionCode, ConditionCode.IS_NULL, tableName, columnName).confirm();
	}

	/**
	 * <h3 class="en-US">Data column not null condition information builder</h3>
	 * <h3 class="zh-CN">数据列不为空条件信息构建器</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column query condition information builder instance object</span>
	 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
	 */
	public ConditionsBuilder<P> notNull(final ConnectionCode connectionCode,
	                                    final String tableName, final String columnName) {
		return this.column(connectionCode, ConditionCode.NOT_NULL, tableName, columnName).confirm();
	}

	/**
	 * <h3 class="en-US">Data column like condition information builder</h3>
	 * <h3 class="zh-CN">数据列模糊匹配条件信息构建器</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column query condition information builder instance object</span>
	 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
	 */
	public ColumnConditionBuilder<ConditionsBuilder<P>> like(final ConnectionCode connectionCode,
	                                                         final String tableName, final String columnName) {
		return this.column(connectionCode, ConditionCode.LIKE, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Data column is not like condition information builder</h3>
	 * <h3 class="zh-CN">数据列非模糊匹配条件信息构建器</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column query condition information builder instance object</span>
	 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
	 */
	public ColumnConditionBuilder<ConditionsBuilder<P>> notLike(final ConnectionCode connectionCode,
	                                                            final String tableName, final String columnName) {
		return this.column(connectionCode, ConditionCode.NOT_LIKE, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Data exists condition information builder</h3>
	 * <h3 class="zh-CN">数据存在条件信息构建器</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column query condition information builder instance object</span>
	 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
	 */
	public ColumnConditionBuilder<ConditionsBuilder<P>> exists(final ConnectionCode connectionCode,
	                                                           final String tableName, final String columnName) {
		return this.column(connectionCode, ConditionCode.EXISTS, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Data don't exist condition information builder</h3>
	 * <h3 class="zh-CN">数据不存在条件信息构建器</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column query condition information builder instance object</span>
	 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
	 */
	public ColumnConditionBuilder<ConditionsBuilder<P>> notExists(final ConnectionCode connectionCode,
	                                                              final String tableName, final String columnName) {
		return this.column(connectionCode, ConditionCode.NOT_EXISTS, tableName, columnName);
	}

	/**
	 * <h3 class="en-US">Group query condition information builder</h3>
	 * <h3 class="zh-CN">查询信息组构建器</h3>
	 *
	 * @return <span class="en-US">Group query condition information builder instance object</span>
	 * <span class="zh-CN">查询信息组构建器实例对象</span>
	 */
	public GroupConditionBuilder<ConditionsBuilder<P>> group() {
		return this.group(ConnectionCode.AND);
	}

	/**
	 * <h3 class="en-US">Group query condition information builder</h3>
	 * <h3 class="zh-CN">查询信息组构建器</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @return <span class="en-US">Group query condition information builder instance object</span>
	 * <span class="zh-CN">查询信息组构建器实例对象</span>
	 */
	public GroupConditionBuilder<ConditionsBuilder<P>> group(final ConnectionCode connectionCode) {
		return new GroupConditionBuilder<>(this, connectionCode);
	}

	@Override
	public void confirm(final Object object) {
		if (object instanceof Condition) {
			this.conditions.add((Condition) object);
		}
	}

	@Override
	public Conditions build() throws BuilderException {
		return new Conditions(this.conditions, this.having);
	}

	/**
	 * <h3 class="en-US">Data column query condition information builder</h3>
	 * <h3 class="zh-CN">数据列查询信息构建器</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param conditionCode  <span class="en-US">Query condition code</span>
	 *                       <span class="zh-CN">查询条件运算代码</span>
	 * @param tableName      <span class="en-US">Data table name</span>
	 *                       <span class="zh-CN">数据表名</span>
	 * @param columnName     <span class="en-US">Data column name</span>
	 *                       <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column query condition information builder instance object</span>
	 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
	 */
	private ColumnConditionBuilder<ConditionsBuilder<P>> column(final ConnectionCode connectionCode,
	                                                            final ConditionCode conditionCode,
	                                                            final String tableName, final String columnName) {
		return new ColumnConditionBuilder<>(this, connectionCode, conditionCode, tableName, columnName);
	}

	/**
	 * <h2 class="en-US">Query conditions information</h2>
	 * <h2 class="zh-CN">查询条件信息列表</h2>
	 *
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision: 1.0.0 $ $Date: Oct 28, 2020 11:46:08 $
	 */
	public static final class Conditions {

		/**
		 * <span class="en-US">Query condition instance list</span>
		 * <span class="zh-CN">查询条件实例对象列表</span>
		 */
		@Nonnull
		private final List<Condition> conditions;
		/**
		 * <span class="en-US">Having condition flag</span>
		 * <span class="zh-CN">Having字句条件标记</span>
		 */
		private final boolean having;

		/**
		 * <h3 class="en-US">Constructor method for the query conditions information</h3>
		 * <h3 class="zh-CN">查询条件信息列表的构造方法</h3>
		 *
		 * @param conditions <span class="en-US">Query condition instance list</span>
		 *                   <span class="zh-CN">查询条件实例对象列表</span>
		 * @param having     <span class="en-US">Having condition flag</span>
		 *                   <span class="zh-CN">Having字句条件标记</span>
		 */
		private Conditions(@Nonnull final List<Condition> conditions, final boolean having) {
			this.conditions = conditions;
			this.having = having;
		}

		/**
		 * <h3 class="en-US">Getter method for the query condition instance list</h3>
		 * <h3 class="zh-CN">查询条件实例对象列表的Getter方法</h3>
		 *
		 * @return <span class="en-US">Query condition instance list</span>
		 * <span class="zh-CN">查询条件实例对象列表</span>
		 */
		@Nonnull
		public List<Condition> getConditions() {
			return this.conditions;
		}

		/**
		 * <h3 class="en-US">Getter method for the having condition flag</h3>
		 * <h3 class="zh-CN">Having字句条件标记的Getter方法</h3>
		 *
		 * @return <span class="en-US">Having condition flag</span>
		 * <span class="zh-CN">Having字句条件标记</span>
		 */
		public boolean isHaving() {
			return this.having;
		}
	}

	/**
	 * <h2 class="en-US">Abstract class for query conditions information builder</h2>
	 * <h2 class="zh-CN">查询条件信息构建器抽象类</h2>
	 *
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision: 1.0.0 $ $Date: Oct 28, 2020 11:46:08 $
	 */
	private static abstract class ConditionBuilder<P extends ParentBuilder, T extends Condition>
			extends AbstractBuilder<P, T> {

		/**
		 * <span class="en-US">Query condition instance object</span>
		 * <span class="zh-CN">查询条件实例对象</span>
		 */
		protected final T condition;

		/**
		 * <h3 class="en-US">Protected constructor for AbstractBuilder</h3>
		 * <h3 class="zh-CN">AbstractBuilder的构造函数</h3>
		 *
		 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
		 *                      <span class="zh-CN">父构建器实例对象</span>
		 */
		protected ConditionBuilder(final P parentBuilder, final T condition) {
			super(parentBuilder);
			this.condition = condition;
		}
	}

	/**
	 * <h2 class="en-US">Data column conditions information builder</h2>
	 * <h2 class="zh-CN">数据列查询条件信息构建器</h2>
	 *
	 * @param <P> <span class="en-US">Parent builder generic type class</span>
	 *            <span class="zh-CN">父构建器泛型类</span>
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision: 1.0.0 $ $Date: Oct 28, 2020 11:46:08 $
	 */
	public static final class ColumnConditionBuilder<P extends ParentBuilder>
			extends ConditionBuilder<P, ColumnCondition> {

		/**
		 * <h3 class="en-US">Constructor method for the data column conditions information builder</h3>
		 * <h3 class="zh-CN">数据列查询条件信息构建器的构造函数</h3>
		 *
		 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
		 *                      <span class="zh-CN">父构建器实例对象</span>
		 * @param connection    <span class="en-US">Query connection code</span>
		 *                      <span class="zh-CN">查询条件连接代码</span>
		 * @param condition     <span class="en-US">Query condition code</span>
		 *                      <span class="zh-CN">查询条件运算代码</span>
		 * @param tableName     <span class="en-US">Data table name</span>
		 *                      <span class="zh-CN">数据表名</span>
		 * @param columnName    <span class="en-US">Data column name</span>
		 *                      <span class="zh-CN">数据列名</span>
		 */
		public ColumnConditionBuilder(final P parentBuilder, final ConnectionCode connection,
		                              final ConditionCode condition, final String tableName, final String columnName) {
			super(parentBuilder, new ColumnCondition());
			this.condition.setConnectionCode(connection);
			this.condition.setConditionCode(condition);
			this.condition.setTableName(tableName);
			this.condition.setColumnName(columnName);
		}

		/**
		 * <h3 class="en-US">Set sort code</h3>
		 * <h3 class="zh-CN">设置排序代码</h3>
		 *
		 * @param sortCode <span class="en-US">Sort code</span>
		 *                 <span class="zh-CN">排序代码</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public ColumnConditionBuilder<P> sortCode(final int sortCode) {
			this.condition.setSortCode(sortCode);
			return this;
		}

		/**
		 * <h3 class="en-US">Set the execute function name</h3>
		 * <h3 class="zh-CN">设置查询匹配值运算函数名</h3>
		 *
		 * @param functionName <span class="en-US">Execute function name</span>
		 *                     <span class="zh-CN">运算函数名</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public ColumnConditionBuilder<P> function(final String functionName) {
			this.condition.setFunctionName(functionName);
			return this;
		}

		/**
		 * <h3 class="en-US">Set condition match value</h3>
		 * <h3 class="zh-CN">设置查询匹配值</h3>
		 *
		 * @param matchValue <span class="en-US">Condition match value</span>
		 *                   <span class="zh-CN">查询匹配值</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public ColumnConditionBuilder<P> matchValue(final Object matchValue) {
			this.condition.setConditionParameter(AbstractParameter.constant(matchValue));
			return this;
		}

		/**
		 * <h3 class="en-US">Set condition match column information</h3>
		 * <h3 class="zh-CN">设置查询匹配数据列</h3>
		 *
		 * @param matchTable  <span class="en-US">Data table name</span>
		 *                    <span class="zh-CN">数据表名</span>
		 * @param matchColumn <span class="en-US">Data column name</span>
		 *                    <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public ColumnConditionBuilder<P> matchColumn(final String matchTable, final String matchColumn) {
			this.condition.setConditionParameter(AbstractParameter.column(matchTable, matchColumn));
			return this;
		}

		/**
		 * <h3 class="en-US">Calculate function parameter item builder</h3>
		 * <h3 class="zh-CN">计算参数信息构建器</h3>
		 *
		 * @return <span class="en-US">Calculate function parameter item builder instance object</span>
		 * <span class="zh-CN">计算参数信息构建器实例对象</span>
		 */
		public ParametersBuilder.CalculateParameterBuilder<ColumnConditionBuilder<P>> matchCalculate() {
			return new ParametersBuilder.CalculateParameterBuilder<>(this);
		}

		/**
		 * <h3 class="en-US">Query condition match function information builder</h3>
		 * <h3 class="zh-CN">查询匹配函数构建器</h3>
		 *
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public ParametersBuilder.FunctionParameterBuilder<ColumnConditionBuilder<P>> matchFunction() {
			return new ParametersBuilder.FunctionParameterBuilder<>(this);
		}

		/**
		 * <h3 class="en-US">Set condition ranges begin value and end value</h3>
		 * <h3 class="zh-CN">设置查询区间起始值和终止值</h3>
		 *
		 * @param beginValue <span class="en-US">Ranges begin value</span>
		 *                   <span class="zh-CN">区间起始值</span>
		 * @param endValue   <span class="en-US">Ranges end value</span>
		 *                   <span class="zh-CN">区间终止值</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public ColumnConditionBuilder<P> inRanges(@Nonnull final Object beginValue, @Nonnull final Object endValue) {
			this.condition.setConditionParameter(AbstractParameter.ranges(beginValue, endValue));
			return this;
		}

		/**
		 * <h3 class="en-US">Set condition value array</h3>
		 * <h3 class="zh-CN">设置查询匹配值数组</h3>
		 *
		 * @param matchValues <span class="en-US">Condition value array</span>
		 *                    <span class="zh-CN">查询匹配值数组</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public ColumnConditionBuilder<P> inArray(@Nonnull final Object... matchValues) {
			this.condition.setConditionParameter(AbstractParameter.arrays(matchValues));
			return this;
		}

		/**
		 * <h3 class="en-US">Condition matches the scalar sub-query information builder</h3>
		 * <h3 class="zh-CN">匹配标量子查询构建器</h3>
		 *
		 * @return <span class="en-US">Sub-query builder instance object</span>
		 * <span class="zh-CN">子查询构建器实例对象</span>
		 */
		public SubQueryBuilder.ScalarSubQueryBuilder<ColumnConditionBuilder<P>> matchQuery() {
			return new SubQueryBuilder.ScalarSubQueryBuilder<>(this);
		}

		@Override
		public void confirm(final Object object) {
			if (object instanceof AbstractQuery) {
				this.condition.setConditionParameter(AbstractParameter.subQuery((AbstractQuery) object));
			} else if (object instanceof FunctionParameter) {
				this.condition.setConditionParameter((FunctionParameter) object);
			}
		}

		@Override
		public ColumnCondition build() throws BuilderException {
			return this.condition;
		}
	}

	/**
	 * <h2 class="en-US">Group conditions information builder</h2>
	 * <h2 class="zh-CN">查询条件组信息构建器</h2>
	 *
	 * @param <P> <span class="en-US">Parent builder generic type class</span>
	 *            <span class="zh-CN">父构建器泛型类</span>
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision: 1.0.0 $ $Date: Oct 28, 2020 11:46:08 $
	 */
	public static final class GroupConditionBuilder<P extends ParentBuilder>
			extends ConditionBuilder<P, GroupCondition> {

		/**
		 * <span class="en-US">Match condition list</span>
		 * <span class="zh-CN">匹配条件列表</span>
		 */
		private final List<Condition> conditionList;

		/**
		 * <h3 class="en-US">Constructor method for the group conditions information builder</h3>
		 * <h3 class="zh-CN">查询条件组信息构建器的构造函数</h3>
		 *
		 * @param parentBuilder  <span class="en-US">Parent builder instance object</span>
		 *                       <span class="zh-CN">父构建器实例对象</span>
		 * @param connectionCode <span class="en-US">Query connection code</span>
		 *                       <span class="zh-CN">查询条件连接代码</span>
		 */
		public GroupConditionBuilder(final P parentBuilder, final ConnectionCode connectionCode) {
			super(parentBuilder, new GroupCondition());
			this.condition.setConnectionCode(connectionCode);
			this.conditionList = new ArrayList<>();
		}

		/**
		 * <h3 class="en-US">Set sort code</h3>
		 * <h3 class="zh-CN">设置排序代码</h3>
		 *
		 * @param sortCode <span class="en-US">Sort code</span>
		 *                 <span class="zh-CN">排序代码</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public GroupConditionBuilder<P> sortCode(final int sortCode) {
			this.condition.setSortCode(sortCode);
			return this;
		}

		/**
		 * <h3 class="en-US">Data column less condition information builder</h3>
		 * <h3 class="zh-CN">数据列小于条件信息构建器</h3>
		 *
		 * @param tableName  <span class="en-US">Data table name</span>
		 *                   <span class="zh-CN">数据表名</span>
		 * @param columnName <span class="en-US">Data column name</span>
		 *                   <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Data column query condition information builder instance object</span>
		 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
		 */
		public ColumnConditionBuilder<GroupConditionBuilder<P>> less(final String tableName, final String columnName) {
			return this.less(ConnectionCode.AND, tableName, columnName);
		}

		/**
		 * <h3 class="en-US">Data column less or equal condition information builder</h3>
		 * <h3 class="zh-CN">数据列小于等于条件信息构建器</h3>
		 *
		 * @param tableName  <span class="en-US">Data table name</span>
		 *                   <span class="zh-CN">数据表名</span>
		 * @param columnName <span class="en-US">Data column name</span>
		 *                   <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Data column query condition information builder instance object</span>
		 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
		 */
		public ColumnConditionBuilder<GroupConditionBuilder<P>> lessEqual(final String tableName, final String columnName) {
			return this.lessEqual(ConnectionCode.AND, tableName, columnName);
		}

		/**
		 * <h3 class="en-US">Data column greater condition information builder</h3>
		 * <h3 class="zh-CN">数据列大于条件信息构建器</h3>
		 *
		 * @param tableName  <span class="en-US">Data table name</span>
		 *                   <span class="zh-CN">数据表名</span>
		 * @param columnName <span class="en-US">Data column name</span>
		 *                   <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Data column query condition information builder instance object</span>
		 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
		 */
		public ColumnConditionBuilder<GroupConditionBuilder<P>> greater(final String tableName, final String columnName) {
			return this.greater(ConnectionCode.AND, tableName, columnName);
		}

		/**
		 * <h3 class="en-US">Data column greater or equal condition information builder</h3>
		 * <h3 class="zh-CN">数据列大于等于条件信息构建器</h3>
		 *
		 * @param tableName  <span class="en-US">Data table name</span>
		 *                   <span class="zh-CN">数据表名</span>
		 * @param columnName <span class="en-US">Data column name</span>
		 *                   <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Data column query condition information builder instance object</span>
		 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
		 */
		public ColumnConditionBuilder<GroupConditionBuilder<P>> greaterEqual(final String tableName, final String columnName) {
			return this.greaterEqual(ConnectionCode.AND, tableName, columnName);
		}

		/**
		 * <h3 class="en-US">Data column equal condition information builder</h3>
		 * <h3 class="zh-CN">数据列等于条件信息构建器</h3>
		 *
		 * @param tableName  <span class="en-US">Data table name</span>
		 *                   <span class="zh-CN">数据表名</span>
		 * @param columnName <span class="en-US">Data column name</span>
		 *                   <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Data column query condition information builder instance object</span>
		 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
		 */
		public ColumnConditionBuilder<GroupConditionBuilder<P>> equalTo(final String tableName, final String columnName) {
			return this.equalTo(ConnectionCode.AND, tableName, columnName);
		}

		/**
		 * <h3 class="en-US">Data column not equal condition information builder</h3>
		 * <h3 class="zh-CN">数据列不等于条件信息构建器</h3>
		 *
		 * @param tableName  <span class="en-US">Data table name</span>
		 *                   <span class="zh-CN">数据表名</span>
		 * @param columnName <span class="en-US">Data column name</span>
		 *                   <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Data column query condition information builder instance object</span>
		 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
		 */
		public ColumnConditionBuilder<GroupConditionBuilder<P>> notEqual(final String tableName, final String columnName) {
			return this.notEqual(ConnectionCode.AND, tableName, columnName);
		}

		/**
		 * <h3 class="en-US">Data column in arrays condition information builder</h3>
		 * <h3 class="zh-CN">数据列在数组中条件信息构建器</h3>
		 *
		 * @param tableName  <span class="en-US">Data table name</span>
		 *                   <span class="zh-CN">数据表名</span>
		 * @param columnName <span class="en-US">Data column name</span>
		 *                   <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Data column query condition information builder instance object</span>
		 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
		 */
		public ColumnConditionBuilder<GroupConditionBuilder<P>> in(final String tableName, final String columnName) {
			return this.in(ConnectionCode.AND, tableName, columnName);
		}

		/**
		 * <h3 class="en-US">Data column not in arrays condition information builder</h3>
		 * <h3 class="zh-CN">数据列不在数组中条件信息构建器</h3>
		 *
		 * @param tableName  <span class="en-US">Data table name</span>
		 *                   <span class="zh-CN">数据表名</span>
		 * @param columnName <span class="en-US">Data column name</span>
		 *                   <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Data column query condition information builder instance object</span>
		 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
		 */
		public ColumnConditionBuilder<GroupConditionBuilder<P>> notIn(final String tableName, final String columnName) {
			return this.notIn(ConnectionCode.AND, tableName, columnName);
		}

		/**
		 * <h3 class="en-US">Data column in ranges condition information builder</h3>
		 * <h3 class="zh-CN">数据列在指定区间条件信息构建器</h3>
		 *
		 * @param tableName  <span class="en-US">Data table name</span>
		 *                   <span class="zh-CN">数据表名</span>
		 * @param columnName <span class="en-US">Data column name</span>
		 *                   <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Data column query condition information builder instance object</span>
		 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
		 */
		public ColumnConditionBuilder<GroupConditionBuilder<P>> betweenAnd(final String tableName, final String columnName) {
			return this.betweenAnd(ConnectionCode.AND, tableName, columnName);
		}

		/**
		 * <h3 class="en-US">Data column in ranges condition information builder</h3>
		 * <h3 class="zh-CN">数据列不在指定区间条件信息构建器</h3>
		 *
		 * @param tableName  <span class="en-US">Data table name</span>
		 *                   <span class="zh-CN">数据表名</span>
		 * @param columnName <span class="en-US">Data column name</span>
		 *                   <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Data column query condition information builder instance object</span>
		 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
		 */
		public ColumnConditionBuilder<GroupConditionBuilder<P>> notBetweenAnd(final String tableName, final String columnName) {
			return this.notBetweenAnd(ConnectionCode.AND, tableName, columnName);
		}

		/**
		 * <h3 class="en-US">Data column is null condition information builder</h3>
		 * <h3 class="zh-CN">数据列为空条件信息构建器</h3>
		 *
		 * @param tableName  <span class="en-US">Data table name</span>
		 *                   <span class="zh-CN">数据表名</span>
		 * @param columnName <span class="en-US">Data column name</span>
		 *                   <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Data column query condition information builder instance object</span>
		 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
		 */
		public GroupConditionBuilder<P> isNull(final String tableName, final String columnName) {
			return this.isNull(ConnectionCode.AND, tableName, columnName);
		}

		/**
		 * <h3 class="en-US">Data column not null condition information builder</h3>
		 * <h3 class="zh-CN">数据列不为空条件信息构建器</h3>
		 *
		 * @param tableName  <span class="en-US">Data table name</span>
		 *                   <span class="zh-CN">数据表名</span>
		 * @param columnName <span class="en-US">Data column name</span>
		 *                   <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Data column query condition information builder instance object</span>
		 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
		 */
		public GroupConditionBuilder<P> notNull(final String tableName, final String columnName) {
			return this.notNull(ConnectionCode.AND, tableName, columnName);
		}

		/**
		 * <h3 class="en-US">Data column like condition information builder</h3>
		 * <h3 class="zh-CN">数据列模糊匹配条件信息构建器</h3>
		 *
		 * @param tableName  <span class="en-US">Data table name</span>
		 *                   <span class="zh-CN">数据表名</span>
		 * @param columnName <span class="en-US">Data column name</span>
		 *                   <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Data column query condition information builder instance object</span>
		 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
		 */
		public ColumnConditionBuilder<GroupConditionBuilder<P>> like(final String tableName, final String columnName) {
			return this.like(ConnectionCode.AND, tableName, columnName);
		}

		/**
		 * <h3 class="en-US">Data column is not like condition information builder</h3>
		 * <h3 class="zh-CN">数据列非模糊匹配条件信息构建器</h3>
		 *
		 * @param tableName  <span class="en-US">Data table name</span>
		 *                   <span class="zh-CN">数据表名</span>
		 * @param columnName <span class="en-US">Data column name</span>
		 *                   <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Data column query condition information builder instance object</span>
		 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
		 */
		public ColumnConditionBuilder<GroupConditionBuilder<P>> notLike(final String tableName, final String columnName) {
			return this.notLike(ConnectionCode.AND, tableName, columnName);
		}

		/**
		 * <h3 class="en-US">Data exists condition information builder</h3>
		 * <h3 class="zh-CN">数据存在条件信息构建器</h3>
		 *
		 * @param tableName  <span class="en-US">Data table name</span>
		 *                   <span class="zh-CN">数据表名</span>
		 * @param columnName <span class="en-US">Data column name</span>
		 *                   <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Data column query condition information builder instance object</span>
		 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
		 */
		public ColumnConditionBuilder<GroupConditionBuilder<P>> exists(final String tableName, final String columnName) {
			return this.exists(ConnectionCode.AND, tableName, columnName);
		}

		/**
		 * <h3 class="en-US">Data don't exist condition information builder</h3>
		 * <h3 class="zh-CN">数据不存在条件信息构建器</h3>
		 *
		 * @param tableName  <span class="en-US">Data table name</span>
		 *                   <span class="zh-CN">数据表名</span>
		 * @param columnName <span class="en-US">Data column name</span>
		 *                   <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Data column query condition information builder instance object</span>
		 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
		 */
		public ColumnConditionBuilder<GroupConditionBuilder<P>> notExists(final String tableName, final String columnName) {
			return this.notExists(ConnectionCode.AND, tableName, columnName);
		}

		/**
		 * <h3 class="en-US">Data column less condition information builder</h3>
		 * <h3 class="zh-CN">数据列小于条件信息构建器</h3>
		 *
		 * @param connectionCode <span class="en-US">Query connection code</span>
		 *                       <span class="zh-CN">查询条件连接代码</span>
		 * @param tableName      <span class="en-US">Data table name</span>
		 *                       <span class="zh-CN">数据表名</span>
		 * @param columnName     <span class="en-US">Data column name</span>
		 *                       <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Data column query condition information builder instance object</span>
		 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
		 */
		public ColumnConditionBuilder<GroupConditionBuilder<P>> less(final ConnectionCode connectionCode,
		                                                             final String tableName, final String columnName) {
			return this.column(connectionCode, ConditionCode.LESS, tableName, columnName);
		}

		/**
		 * <h3 class="en-US">Data column less or equal condition information builder</h3>
		 * <h3 class="zh-CN">数据列小于等于条件信息构建器</h3>
		 *
		 * @param connectionCode <span class="en-US">Query connection code</span>
		 *                       <span class="zh-CN">查询条件连接代码</span>
		 * @param tableName      <span class="en-US">Data table name</span>
		 *                       <span class="zh-CN">数据表名</span>
		 * @param columnName     <span class="en-US">Data column name</span>
		 *                       <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Data column query condition information builder instance object</span>
		 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
		 */
		public ColumnConditionBuilder<GroupConditionBuilder<P>> lessEqual(final ConnectionCode connectionCode,
		                                                                  final String tableName, final String columnName) {
			return this.column(connectionCode, ConditionCode.LESS_EQUAL, tableName, columnName);
		}

		/**
		 * <h3 class="en-US">Data column greater condition information builder</h3>
		 * <h3 class="zh-CN">数据列大于条件信息构建器</h3>
		 *
		 * @param connectionCode <span class="en-US">Query connection code</span>
		 *                       <span class="zh-CN">查询条件连接代码</span>
		 * @param tableName      <span class="en-US">Data table name</span>
		 *                       <span class="zh-CN">数据表名</span>
		 * @param columnName     <span class="en-US">Data column name</span>
		 *                       <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Data column query condition information builder instance object</span>
		 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
		 */
		public ColumnConditionBuilder<GroupConditionBuilder<P>> greater(final ConnectionCode connectionCode,
		                                                                final String tableName, final String columnName) {
			return this.column(connectionCode, ConditionCode.GREATER, tableName, columnName);
		}

		/**
		 * <h3 class="en-US">Data column greater or equal condition information builder</h3>
		 * <h3 class="zh-CN">数据列大于等于条件信息构建器</h3>
		 *
		 * @param connectionCode <span class="en-US">Query connection code</span>
		 *                       <span class="zh-CN">查询条件连接代码</span>
		 * @param tableName      <span class="en-US">Data table name</span>
		 *                       <span class="zh-CN">数据表名</span>
		 * @param columnName     <span class="en-US">Data column name</span>
		 *                       <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Data column query condition information builder instance object</span>
		 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
		 */
		public ColumnConditionBuilder<GroupConditionBuilder<P>> greaterEqual(final ConnectionCode connectionCode,
		                                                                     final String tableName, final String columnName) {
			return this.column(connectionCode, ConditionCode.GREATER_EQUAL, tableName, columnName);
		}

		/**
		 * <h3 class="en-US">Data column equal condition information builder</h3>
		 * <h3 class="zh-CN">数据列等于条件信息构建器</h3>
		 *
		 * @param connectionCode <span class="en-US">Query connection code</span>
		 *                       <span class="zh-CN">查询条件连接代码</span>
		 * @param tableName      <span class="en-US">Data table name</span>
		 *                       <span class="zh-CN">数据表名</span>
		 * @param columnName     <span class="en-US">Data column name</span>
		 *                       <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Data column query condition information builder instance object</span>
		 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
		 */
		public ColumnConditionBuilder<GroupConditionBuilder<P>> equalTo(final ConnectionCode connectionCode,
		                                                                final String tableName, final String columnName) {
			return this.column(connectionCode, ConditionCode.EQUAL, tableName, columnName);
		}

		/**
		 * <h3 class="en-US">Data column not equal condition information builder</h3>
		 * <h3 class="zh-CN">数据列不等于条件信息构建器</h3>
		 *
		 * @param connectionCode <span class="en-US">Query connection code</span>
		 *                       <span class="zh-CN">查询条件连接代码</span>
		 * @param tableName      <span class="en-US">Data table name</span>
		 *                       <span class="zh-CN">数据表名</span>
		 * @param columnName     <span class="en-US">Data column name</span>
		 *                       <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Data column query condition information builder instance object</span>
		 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
		 */
		public ColumnConditionBuilder<GroupConditionBuilder<P>> notEqual(final ConnectionCode connectionCode,
		                                                                 final String tableName, final String columnName) {
			return this.column(connectionCode, ConditionCode.NOT_EQUAL, tableName, columnName);
		}

		/**
		 * <h3 class="en-US">Data column in arrays condition information builder</h3>
		 * <h3 class="zh-CN">数据列在数组中条件信息构建器</h3>
		 *
		 * @param connectionCode <span class="en-US">Query connection code</span>
		 *                       <span class="zh-CN">查询条件连接代码</span>
		 * @param tableName      <span class="en-US">Data table name</span>
		 *                       <span class="zh-CN">数据表名</span>
		 * @param columnName     <span class="en-US">Data column name</span>
		 *                       <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Data column query condition information builder instance object</span>
		 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
		 */
		public ColumnConditionBuilder<GroupConditionBuilder<P>> in(final ConnectionCode connectionCode,
		                                                           final String tableName, final String columnName) {
			return this.column(connectionCode, ConditionCode.IN, tableName, columnName);
		}

		/**
		 * <h3 class="en-US">Data column not in arrays condition information builder</h3>
		 * <h3 class="zh-CN">数据列不在数组中条件信息构建器</h3>
		 *
		 * @param connectionCode <span class="en-US">Query connection code</span>
		 *                       <span class="zh-CN">查询条件连接代码</span>
		 * @param tableName      <span class="en-US">Data table name</span>
		 *                       <span class="zh-CN">数据表名</span>
		 * @param columnName     <span class="en-US">Data column name</span>
		 *                       <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Data column query condition information builder instance object</span>
		 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
		 */
		public ColumnConditionBuilder<GroupConditionBuilder<P>> notIn(final ConnectionCode connectionCode,
		                                                              final String tableName, final String columnName) {
			return this.column(connectionCode, ConditionCode.NOT_IN, tableName, columnName);
		}

		/**
		 * <h3 class="en-US">Data column in ranges condition information builder</h3>
		 * <h3 class="zh-CN">数据列在指定区间条件信息构建器</h3>
		 *
		 * @param connectionCode <span class="en-US">Query connection code</span>
		 *                       <span class="zh-CN">查询条件连接代码</span>
		 * @param tableName      <span class="en-US">Data table name</span>
		 *                       <span class="zh-CN">数据表名</span>
		 * @param columnName     <span class="en-US">Data column name</span>
		 *                       <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Data column query condition information builder instance object</span>
		 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
		 */
		public ColumnConditionBuilder<GroupConditionBuilder<P>> betweenAnd(final ConnectionCode connectionCode,
		                                                                   final String tableName, final String columnName) {
			return this.column(connectionCode, ConditionCode.BETWEEN_AND, tableName, columnName);
		}

		/**
		 * <h3 class="en-US">Data column in ranges condition information builder</h3>
		 * <h3 class="zh-CN">数据列不在指定区间条件信息构建器</h3>
		 *
		 * @param connectionCode <span class="en-US">Query connection code</span>
		 *                       <span class="zh-CN">查询条件连接代码</span>
		 * @param tableName      <span class="en-US">Data table name</span>
		 *                       <span class="zh-CN">数据表名</span>
		 * @param columnName     <span class="en-US">Data column name</span>
		 *                       <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Data column query condition information builder instance object</span>
		 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
		 */
		public ColumnConditionBuilder<GroupConditionBuilder<P>> notBetweenAnd(final ConnectionCode connectionCode,
		                                                                      final String tableName, final String columnName) {
			return this.column(connectionCode, ConditionCode.NOT_BETWEEN_AND, tableName, columnName);
		}

		/**
		 * <h3 class="en-US">Data column is null condition information builder</h3>
		 * <h3 class="zh-CN">数据列为空条件信息构建器</h3>
		 *
		 * @param connectionCode <span class="en-US">Query connection code</span>
		 *                       <span class="zh-CN">查询条件连接代码</span>
		 * @param tableName      <span class="en-US">Data table name</span>
		 *                       <span class="zh-CN">数据表名</span>
		 * @param columnName     <span class="en-US">Data column name</span>
		 *                       <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Data column query condition information builder instance object</span>
		 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
		 */
		public GroupConditionBuilder<P> isNull(final ConnectionCode connectionCode,
		                                       final String tableName, final String columnName) {
			return this.column(connectionCode, ConditionCode.IS_NULL, tableName, columnName).confirm();
		}

		/**
		 * <h3 class="en-US">Data column not null condition information builder</h3>
		 * <h3 class="zh-CN">数据列不为空条件信息构建器</h3>
		 *
		 * @param connectionCode <span class="en-US">Query connection code</span>
		 *                       <span class="zh-CN">查询条件连接代码</span>
		 * @param tableName      <span class="en-US">Data table name</span>
		 *                       <span class="zh-CN">数据表名</span>
		 * @param columnName     <span class="en-US">Data column name</span>
		 *                       <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Data column query condition information builder instance object</span>
		 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
		 */
		public GroupConditionBuilder<P> notNull(final ConnectionCode connectionCode,
		                                        final String tableName, final String columnName) {
			return this.column(connectionCode, ConditionCode.NOT_NULL, tableName, columnName).confirm();
		}

		/**
		 * <h3 class="en-US">Data column like condition information builder</h3>
		 * <h3 class="zh-CN">数据列模糊匹配条件信息构建器</h3>
		 *
		 * @param connectionCode <span class="en-US">Query connection code</span>
		 *                       <span class="zh-CN">查询条件连接代码</span>
		 * @param tableName      <span class="en-US">Data table name</span>
		 *                       <span class="zh-CN">数据表名</span>
		 * @param columnName     <span class="en-US">Data column name</span>
		 *                       <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Data column query condition information builder instance object</span>
		 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
		 */
		public ColumnConditionBuilder<GroupConditionBuilder<P>> like(final ConnectionCode connectionCode,
		                                                             final String tableName, final String columnName) {
			return this.column(connectionCode, ConditionCode.LIKE, tableName, columnName);
		}

		/**
		 * <h3 class="en-US">Data column is not like condition information builder</h3>
		 * <h3 class="zh-CN">数据列非模糊匹配条件信息构建器</h3>
		 *
		 * @param connectionCode <span class="en-US">Query connection code</span>
		 *                       <span class="zh-CN">查询条件连接代码</span>
		 * @param tableName      <span class="en-US">Data table name</span>
		 *                       <span class="zh-CN">数据表名</span>
		 * @param columnName     <span class="en-US">Data column name</span>
		 *                       <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Data column query condition information builder instance object</span>
		 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
		 */
		public ColumnConditionBuilder<GroupConditionBuilder<P>> notLike(final ConnectionCode connectionCode,
		                                                                final String tableName, final String columnName) {
			return this.column(connectionCode, ConditionCode.NOT_LIKE, tableName, columnName);
		}

		/**
		 * <h3 class="en-US">Data exists condition information builder</h3>
		 * <h3 class="zh-CN">数据存在条件信息构建器</h3>
		 *
		 * @param connectionCode <span class="en-US">Query connection code</span>
		 *                       <span class="zh-CN">查询条件连接代码</span>
		 * @param tableName      <span class="en-US">Data table name</span>
		 *                       <span class="zh-CN">数据表名</span>
		 * @param columnName     <span class="en-US">Data column name</span>
		 *                       <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Data column query condition information builder instance object</span>
		 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
		 */
		public ColumnConditionBuilder<GroupConditionBuilder<P>> exists(final ConnectionCode connectionCode,
		                                                               final String tableName, final String columnName) {
			return this.column(connectionCode, ConditionCode.EXISTS, tableName, columnName);
		}

		/**
		 * <h3 class="en-US">Data don't exist condition information builder</h3>
		 * <h3 class="zh-CN">数据不存在条件信息构建器</h3>
		 *
		 * @param connectionCode <span class="en-US">Query connection code</span>
		 *                       <span class="zh-CN">查询条件连接代码</span>
		 * @param tableName      <span class="en-US">Data table name</span>
		 *                       <span class="zh-CN">数据表名</span>
		 * @param columnName     <span class="en-US">Data column name</span>
		 *                       <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Data column query condition information builder instance object</span>
		 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
		 */
		public ColumnConditionBuilder<GroupConditionBuilder<P>> notExists(final ConnectionCode connectionCode,
		                                                                  final String tableName, final String columnName) {
			return this.column(connectionCode, ConditionCode.NOT_EXISTS, tableName, columnName);
		}

		/**
		 * <h3 class="en-US">Data column query condition information builder</h3>
		 * <h3 class="zh-CN">数据列查询信息构建器</h3>
		 *
		 * @param connectionCode <span class="en-US">Query connection code</span>
		 *                       <span class="zh-CN">查询条件连接代码</span>
		 * @param conditionCode  <span class="en-US">Query condition code</span>
		 *                       <span class="zh-CN">查询条件运算代码</span>
		 * @param tableName      <span class="en-US">Data table name</span>
		 *                       <span class="zh-CN">数据表名</span>
		 * @param columnName     <span class="en-US">Data column name</span>
		 *                       <span class="zh-CN">数据列名</span>
		 * @return <span class="en-US">Data column query condition information builder instance object</span>
		 * <span class="zh-CN">数据列查询信息构建器实例对象</span>
		 */
		private ColumnConditionBuilder<GroupConditionBuilder<P>> column(final ConnectionCode connectionCode,
		                                                                final ConditionCode conditionCode,
		                                                                final String tableName, final String columnName) {
			return new ColumnConditionBuilder<>(this, connectionCode, conditionCode, tableName, columnName);
		}

		/**
		 * <h3 class="en-US">Group query condition information builder</h3>
		 * <h3 class="zh-CN">查询信息组构建器</h3>
		 *
		 * @return <span class="en-US">Group query condition information builder instance object</span>
		 * <span class="zh-CN">查询信息组构建器实例对象</span>
		 */
		public GroupConditionBuilder<GroupConditionBuilder<P>> group() {
			return this.group(ConnectionCode.AND);
		}

		/**
		 * <h3 class="en-US">Group query condition information builder</h3>
		 * <h3 class="zh-CN">查询信息组构建器</h3>
		 *
		 * @param connectionCode <span class="en-US">Query connection code</span>
		 *                       <span class="zh-CN">查询条件连接代码</span>
		 * @return <span class="en-US">Group query condition information builder instance object</span>
		 * <span class="zh-CN">查询信息组构建器实例对象</span>
		 */
		public GroupConditionBuilder<GroupConditionBuilder<P>> group(final ConnectionCode connectionCode) {
			return new GroupConditionBuilder<>(this, connectionCode);
		}

		@Override
		public void confirm(final Object object) throws BuilderException {
			if (object instanceof Condition) {
				this.conditionList.add((Condition) object);
			}
		}

		@Override
		public GroupCondition build() {
			this.condition.setConditionList(this.conditionList);
			return this.condition;
		}
	}
}
