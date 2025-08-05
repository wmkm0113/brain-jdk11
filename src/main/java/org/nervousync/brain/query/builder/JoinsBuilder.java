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
import org.nervousync.brain.enumerations.query.JoinType;
import org.nervousync.brain.query.join.JoinInfo;
import org.nervousync.brain.query.join.QueryJoin;
import org.nervousync.brain.query.join.SubQueryJoin;
import org.nervousync.brain.query.join.TableQueryJoin;
import org.nervousync.brain.query.subqueries.NestedTableSubQuery;
import org.nervousync.builder.AbstractBuilder;
import org.nervousync.builder.ParentBuilder;
import org.nervousync.exceptions.builder.BuilderException;
import org.nervousync.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <h2 class="en-US">Query join information list builder</h2>
 * <h2 class="zh-CN">查询关联信息列表构建器</h2>
 *
 * @param <P> <span class="en-US">Parent builder generic type class</span>
 *            <span class="zh-CN">父构建器泛型类</span>
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 28, 2020 11:46:08 $
 */
public final class JoinsBuilder<P extends ParentBuilder> extends AbstractBuilder<P, JoinsBuilder.Joins> {

	/**
	 * <span class="en-US">Related query joins information lists</span>
	 * <span class="zh-CN">关联查询信息列表</span>
	 */
	@Nonnull
	private final List<QueryJoin> joinList = new ArrayList<>();

	/**
	 * <h3 class="en-US">Constructor method for the query join information list builder</h3>
	 * <h3 class="zh-CN">查询关联信息列表构建器的构造方法</h3>
	 *
	 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
	 *                      <span class="zh-CN">父构建器实例对象</span>
	 * @param joinList      <span class="en-US">Related query joins information lists</span>
	 *                      <span class="zh-CN">关联查询信息列表</span>
	 */
	public JoinsBuilder(final P parentBuilder, final List<QueryJoin> joinList) {
		super(parentBuilder);
		if (joinList != null) {
			this.joinList.addAll(joinList);
		}
	}

	/**
	 * <h3 class="en-US">Data table query joins information builder</h3>
	 * <h3 class="zh-CN">数据表关联构建器</h3>
	 *
	 * @param joinType       <span class="en-US">Table join type</span>
	 *                       <span class="zh-CN">数据表关联类型</span>
	 * @param drivenIdentify <span class="en-US">Driven table identify code</span>
	 *                       <span class="zh-CN">驱动表识别代码</span>
	 * @param tableName      <span class="en-US">Join table name</span>
	 *                       <span class="zh-CN">关联表名</span>
	 * @return <span class="en-US">Data table query joins information builder instance object</span>
	 * <span class="zh-CN">数据表关联构建器实例对象</span>
	 */
	public TableQueryJoinBuilder<JoinsBuilder<P>> joinTable(@Nonnull final JoinType joinType,
	                                                        @Nonnull final String drivenIdentify,
	                                                        @Nonnull final String tableName) {
		return new TableQueryJoinBuilder<>(this, joinType, drivenIdentify, tableName);
	}

	/**
	 * <h3 class="en-US">Sub-query joins information builder</h3>
	 * <h3 class="zh-CN">子查询关联构建器</h3>
	 *
	 * @param joinType       <span class="en-US">Table join type</span>
	 *                       <span class="zh-CN">数据表关联类型</span>
	 * @param drivenIdentify <span class="en-US">Driven table identify code</span>
	 *                       <span class="zh-CN">驱动表识别代码</span>
	 * @return <span class="en-US">Sub-query joins information builder instance object</span>
	 * <span class="zh-CN">子查询关联构建器实例对象</span>
	 */
	public SubQueryJoinBuilder<JoinsBuilder<P>> joinQuery(@Nonnull final JoinType joinType,
	                                                      @Nonnull final String drivenIdentify) {
		return new SubQueryJoinBuilder<>(this, joinType, drivenIdentify);
	}

	@Override
	public void confirm(final Object object) {
		if (object instanceof QueryJoin) {
			this.joinList.add((QueryJoin) object);
		}
	}

	@Override
	@Nonnull
	public Joins build() throws BuilderException {
		return new Joins(this.joinList);
	}

	/**
	 * <h2 class="en-US">Query join information list</h2>
	 * <h2 class="zh-CN">查询关联信息列表</h2>
	 *
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision: 1.0.0 $ $Date: Oct 28, 2020 11:46:08 $
	 */
	public static final class Joins {

		/**
		 * <span class="en-US">Related query joins information lists</span>
		 * <span class="zh-CN">关联查询信息列表</span>
		 */
		@Nonnull
		private final List<QueryJoin> joinList;

		/**
		 * <h3 class="en-US">Constructor method for the query join information list</h3>
		 * <h3 class="zh-CN">查询关联信息列表的构造方法</h3>
		 */
		private Joins(@Nonnull final List<QueryJoin> joinList) {
			this.joinList = joinList;
		}

		/**
		 * <h3 class="en-US">Getter method for the related query joins information lists</h3>
		 * <h3 class="zh-CN">关联查询信息列表的Getter方法</h3>
		 *
		 * @return <span class="en-US">Related query joins information list</span>
		 * <span class="zh-CN">关联查询信息列表</span>
		 */
		@Nonnull
		public List<QueryJoin> getJoinList() {
			return this.joinList;
		}
	}

	/**
	 * <h2 class="en-US">Abstract class of query join information builder</h2>
	 * <h2 class="zh-CN">关联信息构建器抽象类</h2>
	 *
	 * @param <P> <span class="en-US">Parent builder instance object</span>
	 *            <span class="zh-CN">父构建器实例对象</span>
	 * @param <T> <span class="en-US">Query join information generic type</span>
	 *            <span class="zh-CN">关联查询信息泛型类型</span>
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision: 1.0.0 $ $Date: Oct 28, 2020 11:46:08 $
	 */
	public static abstract class QueryJoinBuilder<P extends ParentBuilder, T extends QueryJoin>
			extends AbstractBuilder<P, T> {

		/**
		 * <span class="en-US">Query join information</span>
		 * <span class="zh-CN">关联查询信息</span>
		 */
		protected final T queryJoin;
		/**
		 * <span class="en-US">Join columns list</span>
		 * <span class="zh-CN">关联列信息列表</span>
		 */
		private final List<JoinInfo> joinInfos = new ArrayList<>();

		/**
		 * <h3 class="en-US">Constructor method for the abstract class of query join information builder</h3>
		 * <h3 class="zh-CN">关联信息构建器抽象类的构造函数</h3>
		 *
		 * @param parentBuilder  <span class="en-US">Parent builder instance object</span>
		 *                       <span class="zh-CN">父构建器实例对象</span>
		 * @param queryJoin      <span class="en-US">Query join information</span>
		 *                       <span class="zh-CN">关联查询信息</span>
		 * @param drivenIdentify <span class="en-US">Driven table identify code</span>
		 *                       <span class="zh-CN">驱动表识别代码</span>
		 */
		protected QueryJoinBuilder(final P parentBuilder, @Nonnull final T queryJoin,
		                           @Nonnull final String drivenIdentify) {
			super(parentBuilder);
			this.queryJoin = queryJoin;
			this.queryJoin.setDrivenIdentify(drivenIdentify);
		}

		/**
		 * <h3 class="en-US">Add join information</h3>
		 * <h3 class="zh-CN">添加关联信息</h3>
		 *
		 * @param connectionCode <span class="en-US">Query connection code</span>
		 *                       <span class="zh-CN">查询条件连接代码</span>
		 * @param conditionCode  <span class="en-US">Query condition code</span>
		 *                       <span class="zh-CN">查询条件运算代码</span>
		 * @param leftKey        <span class="en-US">Left table data column identify code</span>
		 *                       <span class="zh-CN">左表数据列识别代码</span>
		 * @param rightKey       <span class="en-US">Right table data column identify code</span>
		 *                       <span class="zh-CN">右表数据列识别代码</span>
		 */
		protected void joinOn(@Nonnull final ConnectionCode connectionCode, @Nonnull final ConditionCode conditionCode,
		                      @Nonnull final String leftKey, @Nonnull final String rightKey) {
			if (this.joinInfos.stream()
					.noneMatch(existJoin ->
							existJoin.match(conditionCode, leftKey, rightKey))) {
				JoinInfo joinInfo = new JoinInfo();
				joinInfo.setConnectionCode(connectionCode);
				joinInfo.setConditionCode(conditionCode);
				joinInfo.setLeftKey(leftKey);
				joinInfo.setRightKey(rightKey);
				this.joinInfos.add(joinInfo);
			}
		}

		@Override
		public T build() throws BuilderException {
			this.queryJoin.setJoinInfos(this.joinInfos);
			return this.queryJoin;
		}
	}

	/**
	 * <h2 class="en-US">Data table query joins information builder</h2>
	 * <h2 class="zh-CN">数据表关联信息构建器</h2>
	 *
	 * @param <P> <span class="en-US">Parent builder instance object</span>
	 *            <span class="zh-CN">父构建器实例对象</span>
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision: 1.0.0 $ $Date: Oct 28, 2020 11:46:08 $
	 */
	public static final class TableQueryJoinBuilder<P extends ParentBuilder> extends QueryJoinBuilder<P, TableQueryJoin> {

		/**
		 * <h3 class="en-US">Private constructor method for the data table query joins information builder</h3>
		 * <h3 class="zh-CN">数据表关联信息构建器的私有构造函数</h3>
		 *
		 * @param parentBuilder  <span class="en-US">Parent builder instance object</span>
		 *                       <span class="zh-CN">父构建器实例对象</span>
		 * @param joinType       <span class="en-US">Table join type</span>
		 *                       <span class="zh-CN">数据表关联类型</span>
		 * @param drivenIdentify <span class="en-US">Driven table identify code</span>
		 *                       <span class="zh-CN">驱动表识别代码</span>
		 * @param tableName      <span class="en-US">Join table name</span>
		 *                       <span class="zh-CN">关联表名</span>
		 */
		public TableQueryJoinBuilder(final P parentBuilder, @Nonnull final JoinType joinType,
		                             @Nonnull final String drivenIdentify, @Nonnull final String tableName) {
			super(parentBuilder, new TableQueryJoin(), drivenIdentify);
			this.queryJoin.setJoinType(joinType);
			this.queryJoin.setJoinTable(tableName);
		}

		/**
		 * <h3 class="en-US">Set join table alias name</h3>
		 * <h3 class="zh-CN">设置关联表别名</h3>
		 *
		 * @param aliasName <span class="en-US">Data table alias name</span>
		 *                  <span class="zh-CN">数据表别名</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public TableQueryJoinBuilder<P> aliasName(final String aliasName) {
			if (StringUtils.notBlank(aliasName)) {
				this.queryJoin.setAliasName(aliasName);
			}
			return this;
		}

		/**
		 * <h3 class="en-US">Set join information</h3>
		 * <h3 class="zh-CN">设置关联信息</h3>
		 *
		 * @param leftKey  <span class="en-US">Left table data column identify code</span>
		 *                 <span class="zh-CN">左表数据列识别代码</span>
		 * @param rightKey <span class="en-US">Right table data column identify code</span>
		 *                 <span class="zh-CN">右表数据列识别代码</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public TableQueryJoinBuilder<P> on(@Nonnull final String leftKey, @Nonnull final String rightKey) {
			return this.on(ConnectionCode.AND, ConditionCode.EQUAL, leftKey, rightKey);
		}

		/**
		 * <h3 class="en-US">Set join information</h3>
		 * <h3 class="zh-CN">设置关联信息</h3>
		 *
		 * @param connectionCode <span class="en-US">Query connection code</span>
		 *                       <span class="zh-CN">查询条件连接代码</span>
		 * @param leftKey        <span class="en-US">Left table data column identify code</span>
		 *                       <span class="zh-CN">左表数据列识别代码</span>
		 * @param rightKey       <span class="en-US">Right table data column identify code</span>
		 *                       <span class="zh-CN">右表数据列识别代码</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public TableQueryJoinBuilder<P> on(@Nonnull final ConnectionCode connectionCode,
		                                   @Nonnull final String leftKey, @Nonnull final String rightKey) {
			return this.on(connectionCode, ConditionCode.EQUAL, leftKey, rightKey);
		}

		/**
		 * <h3 class="en-US">Set join information</h3>
		 * <h3 class="zh-CN">设置关联信息</h3>
		 *
		 * @param conditionCode <span class="en-US">Query condition code</span>
		 *                      <span class="zh-CN">查询条件运算代码</span>
		 * @param leftKey       <span class="en-US">Left table data column identify code</span>
		 *                      <span class="zh-CN">左表数据列识别代码</span>
		 * @param rightKey      <span class="en-US">Right table data column identify code</span>
		 *                      <span class="zh-CN">右表数据列识别代码</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public TableQueryJoinBuilder<P> on(@Nonnull final ConditionCode conditionCode,
		                                   @Nonnull final String leftKey, @Nonnull final String rightKey) {
			return this.on(ConnectionCode.AND, conditionCode, leftKey, rightKey);
		}

		/**
		 * <h3 class="en-US">Set join information</h3>
		 * <h3 class="zh-CN">设置关联信息</h3>
		 *
		 * @param connectionCode <span class="en-US">Query connection code</span>
		 *                       <span class="zh-CN">查询条件连接代码</span>
		 * @param conditionCode  <span class="en-US">Query condition code</span>
		 *                       <span class="zh-CN">查询条件运算代码</span>
		 * @param leftKey        <span class="en-US">Left table data column identify code</span>
		 *                       <span class="zh-CN">左表数据列识别代码</span>
		 * @param rightKey       <span class="en-US">Right table data column identify code</span>
		 *                       <span class="zh-CN">右表数据列识别代码</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public TableQueryJoinBuilder<P> on(@Nonnull final ConnectionCode connectionCode,
		                                   @Nonnull final ConditionCode conditionCode,
		                                   @Nonnull final String leftKey, @Nonnull final String rightKey) {
			super.joinOn(connectionCode, conditionCode, leftKey, rightKey);
			return this;
		}
	}

	/**
	 * <h2 class="en-US">Sub-query joins information builder</h2>
	 * <h2 class="zh-CN">子查询关联信息构建器</h2>
	 *
	 * @param <P> <span class="en-US">Parent builder instance object</span>
	 *            <span class="zh-CN">父构建器实例对象</span>
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision: 1.0.0 $ $Date: Oct 28, 2020 11:46:08 $
	 */
	public static final class SubQueryJoinBuilder<P extends ParentBuilder> extends QueryJoinBuilder<P, SubQueryJoin> {

		/**
		 * <h3 class="en-US">Private constructor method for the sub-query joins information builder</h3>
		 * <h3 class="zh-CN">子查询关联信息构建器的私有构造函数</h3>
		 *
		 * @param parentBuilder  <span class="en-US">Parent builder instance object</span>
		 *                       <span class="zh-CN">父构建器实例对象</span>
		 * @param joinType       <span class="en-US">Table join type</span>
		 *                       <span class="zh-CN">数据表关联类型</span>
		 * @param drivenIdentify <span class="en-US">Driven table identify code</span>
		 *                       <span class="zh-CN">驱动表识别代码</span>
		 */
		public SubQueryJoinBuilder(final P parentBuilder, @Nonnull final JoinType joinType,
		                           @Nonnull final String drivenIdentify) {
			super(parentBuilder, new SubQueryJoin(), drivenIdentify);
			this.queryJoin.setJoinType(joinType);
		}

		/**
		 * <h3 class="en-US">Sub-query builder instance object</h3>
		 * <h3 class="zh-CN">子查询构建器实例对象</h3>
		 *
		 * @return <span class="en-US">Sub-query builder instance object</span>
		 * <span class="zh-CN">子查询构建器实例对象</span>
		 */
		public SubQueryBuilder.NestedTableSubQueryBuilder<SubQueryJoinBuilder<P>> subQueryBuilder() {
			return new SubQueryBuilder.NestedTableSubQueryBuilder<>(this);
		}

		@Override
		public void confirm(final Object object) {
			if (object instanceof NestedTableSubQuery) {
				this.queryJoin.setSubQuery((NestedTableSubQuery) object);
			} else {
				super.confirm(object);
			}
		}

		/**
		 * <h3 class="en-US">Set sub-query alias name</h3>
		 * <h3 class="zh-CN">设置子查询别名</h3>
		 *
		 * @param aliasName <span class="en-US">Data table alias name</span>
		 *                  <span class="zh-CN">数据表别名</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public SubQueryJoinBuilder<P> aliasName(final String aliasName) {
			if (StringUtils.notBlank(aliasName)) {
				this.queryJoin.setAliasName(aliasName);
			}
			return this;
		}

		/**
		 * <h3 class="en-US">Set join information</h3>
		 * <h3 class="zh-CN">设置关联信息</h3>
		 *
		 * @param leftKey  <span class="en-US">Left table data column identify code</span>
		 *                 <span class="zh-CN">左表数据列识别代码</span>
		 * @param rightKey <span class="en-US">Right table data column identify code</span>
		 *                 <span class="zh-CN">右表数据列识别代码</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public SubQueryJoinBuilder<P> on(@Nonnull final String leftKey, @Nonnull final String rightKey) {
			return this.on(ConnectionCode.AND, ConditionCode.EQUAL, leftKey, rightKey);
		}

		/**
		 * <h3 class="en-US">Set join information</h3>
		 * <h3 class="zh-CN">设置关联信息</h3>
		 *
		 * @param connectionCode <span class="en-US">Query connection code</span>
		 *                       <span class="zh-CN">查询条件连接代码</span>
		 * @param leftKey        <span class="en-US">Left table data column identify code</span>
		 *                       <span class="zh-CN">左表数据列识别代码</span>
		 * @param rightKey       <span class="en-US">Right table data column identify code</span>
		 *                       <span class="zh-CN">右表数据列识别代码</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public SubQueryJoinBuilder<P> on(@Nonnull final ConnectionCode connectionCode,
		                                 @Nonnull final String leftKey, @Nonnull final String rightKey) {
			return this.on(connectionCode, ConditionCode.EQUAL, leftKey, rightKey);
		}

		/**
		 * <h3 class="en-US">Set join information</h3>
		 * <h3 class="zh-CN">设置关联信息</h3>
		 *
		 * @param conditionCode <span class="en-US">Query condition code</span>
		 *                      <span class="zh-CN">查询条件运算代码</span>
		 * @param leftKey       <span class="en-US">Left table data column identify code</span>
		 *                      <span class="zh-CN">左表数据列识别代码</span>
		 * @param rightKey      <span class="en-US">Right table data column identify code</span>
		 *                      <span class="zh-CN">右表数据列识别代码</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public SubQueryJoinBuilder<P> on(@Nonnull final ConditionCode conditionCode,
		                                 @Nonnull final String leftKey, @Nonnull final String rightKey) {
			return this.on(ConnectionCode.AND, conditionCode, leftKey, rightKey);
		}

		/**
		 * <h3 class="en-US">Set join information</h3>
		 * <h3 class="zh-CN">设置关联信息</h3>
		 *
		 * @param connectionCode <span class="en-US">Query connection code</span>
		 *                       <span class="zh-CN">查询条件连接代码</span>
		 * @param conditionCode  <span class="en-US">Query condition code</span>
		 *                       <span class="zh-CN">查询条件运算代码</span>
		 * @param leftKey        <span class="en-US">Left table data column identify code</span>
		 *                       <span class="zh-CN">左表数据列识别代码</span>
		 * @param rightKey       <span class="en-US">Right table data column identify code</span>
		 *                       <span class="zh-CN">右表数据列识别代码</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public SubQueryJoinBuilder<P> on(@Nonnull final ConnectionCode connectionCode,
		                                 @Nonnull final ConditionCode conditionCode,
		                                 @Nonnull final String leftKey, @Nonnull final String rightKey) {
			super.joinOn(connectionCode, conditionCode, leftKey, rightKey);
			return this;
		}
	}
}
