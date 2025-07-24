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
import org.nervousync.brain.query.data.QueryData;
import org.nervousync.brain.query.join.JoinInfo;
import org.nervousync.brain.query.join.QueryJoin;
import org.nervousync.brain.query.join.SubQueryJoin;
import org.nervousync.brain.query.join.TableQueryJoin;
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
	 * <span class="en-US">Query join information list</span>
	 * <span class="zh-CN">查询关联信息列表</span>
	 */
	@Nonnull
	private final Joins joins;

	/**
	 * <h3 class="en-US">Constructor method for the query join information list builder</h3>
	 * <h3 class="zh-CN">查询关联信息列表构建器的构造方法</h3>
	 *
	 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
	 *                      <span class="zh-CN">父构建器实例对象</span>
	 */
	public JoinsBuilder(final P parentBuilder) {
		super(parentBuilder);
		this.joins = new Joins();
	}

	/**
	 * <h3 class="en-US">Data table query joins information builder</h3>
	 * <h3 class="zh-CN">数据表关联构建器</h3>
	 *
	 * @param joinType  <span class="en-US">Table join type</span>
	 *                  <span class="zh-CN">数据表关联类型</span>
	 * @param tableName <span class="en-US">Join table name</span>
	 *                  <span class="zh-CN">关联表名</span>
	 * @return <span class="en-US">Data table query joins information builder instance object</span>
	 * <span class="zh-CN">数据表关联构建器实例对象</span>
	 */
	public TableQueryJoinBuilder<JoinsBuilder<P>> joinTable(@Nonnull final JoinType joinType, @Nonnull final String tableName) {
		return new TableQueryJoinBuilder<>(this, joinType, tableName);
	}

	/**
	 * <h3 class="en-US">Sub-query joins information builder</h3>
	 * <h3 class="zh-CN">子查询关联构建器</h3>
	 *
	 * @param joinType <span class="en-US">Table join type</span>
	 *                 <span class="zh-CN">数据表关联类型</span>
	 * @return <span class="en-US">Sub-query joins information builder instance object</span>
	 * <span class="zh-CN">子查询关联构建器实例对象</span>
	 */
	public SubQueryJoinBuilder<JoinsBuilder<P>> joinQuery(@Nonnull final JoinType joinType) {
		return new SubQueryJoinBuilder<>(this, joinType);
	}

	@Override
	public void confirm(final Object object) {
		if (object instanceof QueryJoin) {
			this.joins.addJoin((QueryJoin) object);
		}
	}

	@Override
	@Nonnull
	public Joins build() throws BuilderException {
		return this.joins;
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
		private Joins() {
			this.joinList = new ArrayList<>();
		}

		/**
		 * <h3 class="en-US">Add query join information</h3>
		 * <h3 class="zh-CN">添加关联查询信息</h3>
		 *
		 * @param join <span class="en-US">Query join information instance object</span>
		 *             <span class="zh-CN">关联查询信息实例对象</span>
		 */
		private void addJoin(@Nonnull final QueryJoin join) {
			this.joinList.add(join);
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
		 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
		 *                      <span class="zh-CN">父构建器实例对象</span>
		 * @param queryJoin     <span class="en-US">Query join information</span>
		 *                      <span class="zh-CN">关联查询信息</span>
		 */
		protected QueryJoinBuilder(final P parentBuilder, final T queryJoin) {
			super(parentBuilder);
			this.queryJoin = queryJoin;
		}

		/**
		 * <h3 class="en-US">Add join information</h3>
		 * <h3 class="zh-CN">添加关联信息</h3>
		 *
		 * @param connectionCode <span class="en-US">Query connection code</span>
		 *                       <span class="zh-CN">查询条件连接代码</span>
		 * @param conditionCode  <span class="en-US">Query condition code</span>
		 *                       <span class="zh-CN">查询条件运算代码</span>
		 * @param leftIdentify   <span class="en-US">Left table identify code</span>
		 *                       <span class="zh-CN">左表识别代码</span>
		 * @param leftKey        <span class="en-US">Left table data column identify code</span>
		 *                       <span class="zh-CN">左表数据列识别代码</span>
		 * @param rightIdentify  <span class="en-US">Right table identify code</span>
		 *                       <span class="zh-CN">右表识别代码</span>
		 * @param rightKey       <span class="en-US">Right table data column identify code</span>
		 *                       <span class="zh-CN">右表数据列识别代码</span>
		 */
		protected void joinOn(@Nonnull final ConnectionCode connectionCode, @Nonnull final ConditionCode conditionCode,
		                      @Nonnull final String leftIdentify, @Nonnull final String leftKey,
		                      @Nonnull final String rightIdentify, @Nonnull final String rightKey) {
			if (this.joinInfos.stream()
					.noneMatch(existJoin ->
							existJoin.match(conditionCode, leftIdentify, leftKey, rightIdentify, rightKey))) {
				JoinInfo joinInfo = new JoinInfo();
				joinInfo.setConnectionCode(connectionCode);
				joinInfo.setConditionCode(conditionCode);
				joinInfo.setLeftIdentify(leftIdentify);
				joinInfo.setLeftKey(leftKey);
				joinInfo.setRightIdentify(rightIdentify);
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
		 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
		 *                      <span class="zh-CN">父构建器实例对象</span>
		 * @param joinType      <span class="en-US">Table join type</span>
		 *                      <span class="zh-CN">数据表关联类型</span>
		 * @param tableName     <span class="en-US">Join table name</span>
		 *                      <span class="zh-CN">关联表名</span>
		 */
		public TableQueryJoinBuilder(final P parentBuilder, @Nonnull final JoinType joinType,
		                             @Nonnull final String tableName) {
			super(parentBuilder, new TableQueryJoin());
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
		 * @param leftIdentify  <span class="en-US">Left table identify code</span>
		 *                      <span class="zh-CN">左表识别代码</span>
		 * @param leftKey       <span class="en-US">Left table data column identify code</span>
		 *                      <span class="zh-CN">左表数据列识别代码</span>
		 * @param rightIdentify <span class="en-US">Right table identify code</span>
		 *                      <span class="zh-CN">右表识别代码</span>
		 * @param rightKey      <span class="en-US">Right table data column identify code</span>
		 *                      <span class="zh-CN">右表数据列识别代码</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public TableQueryJoinBuilder<P> on(@Nonnull final String leftIdentify, @Nonnull final String leftKey,
		                                   @Nonnull final String rightIdentify, @Nonnull final String rightKey) {
			return this.on(ConnectionCode.AND, ConditionCode.EQUAL, leftIdentify, leftKey, rightIdentify, rightKey);
		}

		/**
		 * <h3 class="en-US">Set join information</h3>
		 * <h3 class="zh-CN">设置关联信息</h3>
		 *
		 * @param connectionCode <span class="en-US">Query connection code</span>
		 *                       <span class="zh-CN">查询条件连接代码</span>
		 * @param leftIdentify   <span class="en-US">Left table identify code</span>
		 *                       <span class="zh-CN">左表识别代码</span>
		 * @param leftKey        <span class="en-US">Left table data column identify code</span>
		 *                       <span class="zh-CN">左表数据列识别代码</span>
		 * @param rightIdentify  <span class="en-US">Right table identify code</span>
		 *                       <span class="zh-CN">右表识别代码</span>
		 * @param rightKey       <span class="en-US">Right table data column identify code</span>
		 *                       <span class="zh-CN">右表数据列识别代码</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public TableQueryJoinBuilder<P> on(@Nonnull final ConnectionCode connectionCode,
		                                   @Nonnull final String leftIdentify, @Nonnull final String leftKey,
		                                   @Nonnull final String rightIdentify, @Nonnull final String rightKey) {
			return this.on(connectionCode, ConditionCode.EQUAL, leftIdentify, leftKey, rightIdentify, rightKey);
		}

		/**
		 * <h3 class="en-US">Set join information</h3>
		 * <h3 class="zh-CN">设置关联信息</h3>
		 *
		 * @param conditionCode <span class="en-US">Query condition code</span>
		 *                      <span class="zh-CN">查询条件运算代码</span>
		 * @param leftIdentify  <span class="en-US">Left table identify code</span>
		 *                      <span class="zh-CN">左表识别代码</span>
		 * @param leftKey       <span class="en-US">Left table data column identify code</span>
		 *                      <span class="zh-CN">左表数据列识别代码</span>
		 * @param rightIdentify <span class="en-US">Right table identify code</span>
		 *                      <span class="zh-CN">右表识别代码</span>
		 * @param rightKey      <span class="en-US">Right table data column identify code</span>
		 *                      <span class="zh-CN">右表数据列识别代码</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public TableQueryJoinBuilder<P> on(@Nonnull final ConditionCode conditionCode,
		                                   @Nonnull final String leftIdentify, @Nonnull final String leftKey,
		                                   @Nonnull final String rightIdentify, @Nonnull final String rightKey) {
			return this.on(ConnectionCode.AND, conditionCode, leftIdentify, leftKey, rightIdentify, rightKey);
		}

		/**
		 * <h3 class="en-US">Set join information</h3>
		 * <h3 class="zh-CN">设置关联信息</h3>
		 *
		 * @param connectionCode <span class="en-US">Query connection code</span>
		 *                       <span class="zh-CN">查询条件连接代码</span>
		 * @param conditionCode  <span class="en-US">Query condition code</span>
		 *                       <span class="zh-CN">查询条件运算代码</span>
		 * @param leftIdentify   <span class="en-US">Left table identify code</span>
		 *                       <span class="zh-CN">左表识别代码</span>
		 * @param leftKey        <span class="en-US">Left table data column identify code</span>
		 *                       <span class="zh-CN">左表数据列识别代码</span>
		 * @param rightIdentify  <span class="en-US">Right table identify code</span>
		 *                       <span class="zh-CN">右表识别代码</span>
		 * @param rightKey       <span class="en-US">Right table data column identify code</span>
		 *                       <span class="zh-CN">右表数据列识别代码</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public TableQueryJoinBuilder<P> on(@Nonnull final ConnectionCode connectionCode,
		                                   @Nonnull final ConditionCode conditionCode,
		                                   @Nonnull final String leftIdentify, @Nonnull final String leftKey,
		                                   @Nonnull final String rightIdentify, @Nonnull final String rightKey) {
			super.joinOn(connectionCode, conditionCode, leftIdentify, leftKey, rightIdentify, rightKey);
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
		 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
		 *                      <span class="zh-CN">父构建器实例对象</span>
		 * @param joinType      <span class="en-US">Table join type</span>
		 *                      <span class="zh-CN">数据表关联类型</span>
		 */
		public SubQueryJoinBuilder(final P parentBuilder, @Nonnull final JoinType joinType) {
			super(parentBuilder, new SubQueryJoin());
			this.queryJoin.setJoinType(joinType);
		}

		/**
		 * <h3 class="en-US">Sub-query builder instance object</h3>
		 * <h3 class="zh-CN">子查询构建器实例对象</h3>
		 *
		 * @param tableName <span class="en-US">Data table name</span>
		 *                  <span class="zh-CN">数据表名</span>
		 * @return <span class="en-US">Sub-query builder instance object</span>
		 * <span class="zh-CN">子查询构建器实例对象</span>
		 */
		public SubQueryBuilder<SubQueryJoinBuilder<P>> subQueryBuilder(@Nonnull final String tableName) {
			return new SubQueryBuilder<>(this, tableName);
		}

		@Override
		public void confirm(final Object object) {
			if (object instanceof QueryData) {
				this.queryJoin.setSubQuery((QueryData) object);
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
		 * @param leftIdentify  <span class="en-US">Left table identify code</span>
		 *                      <span class="zh-CN">左表识别代码</span>
		 * @param leftKey       <span class="en-US">Left table data column identify code</span>
		 *                      <span class="zh-CN">左表数据列识别代码</span>
		 * @param rightIdentify <span class="en-US">Right table identify code</span>
		 *                      <span class="zh-CN">右表识别代码</span>
		 * @param rightKey      <span class="en-US">Right table data column identify code</span>
		 *                      <span class="zh-CN">右表数据列识别代码</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public SubQueryJoinBuilder<P> on(@Nonnull final String leftIdentify, @Nonnull final String leftKey,
		                                 @Nonnull final String rightIdentify, @Nonnull final String rightKey) {
			return this.on(ConnectionCode.AND, ConditionCode.EQUAL, leftIdentify, leftKey, rightIdentify, rightKey);
		}

		/**
		 * <h3 class="en-US">Set join information</h3>
		 * <h3 class="zh-CN">设置关联信息</h3>
		 *
		 * @param connectionCode <span class="en-US">Query connection code</span>
		 *                       <span class="zh-CN">查询条件连接代码</span>
		 * @param leftIdentify   <span class="en-US">Left table identify code</span>
		 *                       <span class="zh-CN">左表识别代码</span>
		 * @param leftKey        <span class="en-US">Left table data column identify code</span>
		 *                       <span class="zh-CN">左表数据列识别代码</span>
		 * @param rightIdentify  <span class="en-US">Right table identify code</span>
		 *                       <span class="zh-CN">右表识别代码</span>
		 * @param rightKey       <span class="en-US">Right table data column identify code</span>
		 *                       <span class="zh-CN">右表数据列识别代码</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public SubQueryJoinBuilder<P> on(@Nonnull final ConnectionCode connectionCode,
		                                 @Nonnull final String leftIdentify, @Nonnull final String leftKey,
		                                 @Nonnull final String rightIdentify, @Nonnull final String rightKey) {
			return this.on(connectionCode, ConditionCode.EQUAL, leftIdentify, leftKey, rightIdentify, rightKey);
		}

		/**
		 * <h3 class="en-US">Set join information</h3>
		 * <h3 class="zh-CN">设置关联信息</h3>
		 *
		 * @param conditionCode <span class="en-US">Query condition code</span>
		 *                      <span class="zh-CN">查询条件运算代码</span>
		 * @param leftIdentify  <span class="en-US">Left table identify code</span>
		 *                      <span class="zh-CN">左表识别代码</span>
		 * @param leftKey       <span class="en-US">Left table data column identify code</span>
		 *                      <span class="zh-CN">左表数据列识别代码</span>
		 * @param rightIdentify <span class="en-US">Right table identify code</span>
		 *                      <span class="zh-CN">右表识别代码</span>
		 * @param rightKey      <span class="en-US">Right table data column identify code</span>
		 *                      <span class="zh-CN">右表数据列识别代码</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public SubQueryJoinBuilder<P> on(@Nonnull final ConditionCode conditionCode,
		                                 @Nonnull final String leftIdentify, @Nonnull final String leftKey,
		                                 @Nonnull final String rightIdentify, @Nonnull final String rightKey) {
			return this.on(ConnectionCode.AND, conditionCode, leftIdentify, leftKey, rightIdentify, rightKey);
		}

		/**
		 * <h3 class="en-US">Set join information</h3>
		 * <h3 class="zh-CN">设置关联信息</h3>
		 *
		 * @param connectionCode <span class="en-US">Query connection code</span>
		 *                       <span class="zh-CN">查询条件连接代码</span>
		 * @param conditionCode  <span class="en-US">Query condition code</span>
		 *                       <span class="zh-CN">查询条件运算代码</span>
		 * @param leftIdentify   <span class="en-US">Left table identify code</span>
		 *                       <span class="zh-CN">左表识别代码</span>
		 * @param leftKey        <span class="en-US">Left table data column identify code</span>
		 *                       <span class="zh-CN">左表数据列识别代码</span>
		 * @param rightIdentify  <span class="en-US">Right table identify code</span>
		 *                       <span class="zh-CN">右表识别代码</span>
		 * @param rightKey       <span class="en-US">Right table data column identify code</span>
		 *                       <span class="zh-CN">右表数据列识别代码</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public SubQueryJoinBuilder<P> on(@Nonnull final ConnectionCode connectionCode,
		                                 @Nonnull final ConditionCode conditionCode,
		                                 @Nonnull final String leftIdentify, @Nonnull final String leftKey,
		                                 @Nonnull final String rightIdentify, @Nonnull final String rightKey) {
			super.joinOn(connectionCode, conditionCode, leftIdentify, leftKey, rightIdentify, rightKey);
			return this;
		}
	}
}
