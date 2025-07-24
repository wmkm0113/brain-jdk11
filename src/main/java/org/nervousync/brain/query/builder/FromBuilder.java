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
import org.nervousync.brain.query.core.QueryFrom;
import org.nervousync.brain.query.data.QueryData;
import org.nervousync.brain.query.from.FromSubQuery;
import org.nervousync.brain.query.from.FromTable;
import org.nervousync.builder.AbstractBuilder;
import org.nervousync.builder.ParentBuilder;
import org.nervousync.exceptions.builder.BuilderException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <h2 class="en-US">Query from information lists builder</h2>
 * <h2 class="zh-CN">查询来源信息列表构建器</h2>
 *
 * @param <P> <span class="en-US">Parent builder generic type class</span>
 *            <span class="zh-CN">父构建器泛型类</span>
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 28, 2020 11:46:08 $
 */
public final class FromBuilder<P extends ParentBuilder> extends AbstractBuilder<P, FromBuilder.QueriesFrom> {

	/**
	 * <span class="en-US">Query from instance lists</span>
	 * <span class="zh-CN">查询来源实例对象列表</span>
	 */
	@Nonnull
	private final List<QueryFrom> fromList;

	/**
	 * <h3 class="en-US">Constructor method for the query from information lists builder</h3>
	 * <h3 class="zh-CN">查询来源信息列表构建器的构造方法</h3>
	 *
	 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
	 *                      <span class="zh-CN">父构建器实例对象</span>
	 */
	public FromBuilder(final P parentBuilder) {
		super(parentBuilder);
		this.fromList = new ArrayList<>();
	}

	/**
	 * <h3 class="en-US">Query from data table builder</h3>
	 * <h3 class="zh-CN">查询来源数据表构建器</h3>
	 *
	 * @param tableName <span class="en-US">Data table name</span>
	 *                  <span class="zh-CN">数据表名</span>
	 * @return <span class="en-US">Query from data table builder instance object</span>
	 * <span class="zh-CN">查询来源数据表构建器实例对象</span>
	 */
	public FromTableBuilder table(@Nonnull final String tableName) {
		return new FromTableBuilder(this, tableName);
	}

	/**
	 * <h3 class="en-US">Query from sub-query builder</h3>
	 * <h3 class="zh-CN">查询来源子查询构建器</h3>
	 *
	 * @param aliasName <span class="en-US">Alias name</span>
	 *                  <span class="zh-CN">别名</span>
	 * @return <span class="en-US">Query from sub-query builder instance object</span>
	 * <span class="zh-CN">查询来源子查询构建器实例对象</span>
	 */
	public FromSubQueryBuilder subQuery(@Nonnull final String aliasName) {
		return new FromSubQueryBuilder(this, aliasName);
	}

	@Override
	public void confirm(final Object object) {
		if (object instanceof QueryFrom) {
			this.fromList.add((QueryFrom) object);
		}
	}

	@Override
	public QueriesFrom build() throws BuilderException {
		return new QueriesFrom(this.fromList);
	}

	/**
	 * <h2 class="en-US">Query from data table builder</h2>
	 * <h2 class="zh-CN">查询来源数据表构建器</h2>
	 *
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision: 1.0.0 $ $Date: Oct 28, 2020 11:46:08 $
	 */
	public final class FromTableBuilder extends AbstractBuilder<FromBuilder<P>, FromTable> {

		/**
		 * <span class="en-US">Query item instance list</span>
		 * <span class="zh-CN">查询项目实例对象列表</span>
		 */
		@Nonnull
		private final FromTable fromTable;

		/**
		 * <h3 class="en-US">Private constructor method for the query from data table builder</h3>
		 * <h3 class="zh-CN">查询来源数据表构建器的私有构造方法</h3>
		 *
		 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
		 *                      <span class="zh-CN">父构建器实例对象</span>
		 * @param tableName     <span class="en-US">Data table name</span>
		 *                      <span class="zh-CN">数据表名</span>
		 */
		private FromTableBuilder(final FromBuilder<P> parentBuilder, @Nonnull final String tableName) {
			super(parentBuilder);
			this.fromTable = new FromTable();
			this.fromTable.setTableName(tableName);
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
		public FromTableBuilder sortCode(final int sortCode) {
			this.fromTable.setSortCode(sortCode);
			return this;
		}

		/**
		 * <h3 class="en-US">Set alias name</h3>
		 * <h3 class="zh-CN">设置别名</h3>
		 *
		 * @param aliasName <span class="en-US">Alias name</span>
		 *                  <span class="zh-CN">别名</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public FromTableBuilder aliasName(final String aliasName) {
			this.fromTable.setAliasName(aliasName);
			return this;
		}

		@Override
		public FromTable build() throws BuilderException {
			return this.fromTable;
		}
	}

	/**
	 * <h2 class="en-US">Query from sub-query builder</h2>
	 * <h2 class="zh-CN">查询来源子查询构建器</h2>
	 *
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision: 1.0.0 $ $Date: Oct 28, 2020 11:46:08 $
	 */
	public final class FromSubQueryBuilder extends AbstractBuilder<FromBuilder<P>, FromSubQuery> {

		/**
		 * <span class="en-US">Query item instance list</span>
		 * <span class="zh-CN">查询项目实例对象列表</span>
		 */
		@Nonnull
		private final FromSubQuery fromSubQuery;

		/**
		 * <h3 class="en-US">Private constructor method for the query from data table builder</h3>
		 * <h3 class="zh-CN">查询来源数据表构建器的私有构造方法</h3>
		 *
		 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
		 *                      <span class="zh-CN">父构建器实例对象</span>
		 * @param aliasName     <span class="en-US">Alias name</span>
		 *                      <span class="zh-CN">别名</span>
		 */
		private FromSubQueryBuilder(final FromBuilder<P> parentBuilder, final String aliasName) {
			super(parentBuilder);
			this.fromSubQuery = new FromSubQuery();
			this.fromSubQuery.setAliasName(aliasName);
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
		public FromSubQueryBuilder sortCode(final int sortCode) {
			this.fromSubQuery.setSortCode(sortCode);
			return this;
		}

		/**
		 * <h3 class="en-US">Sub-query information builder</h3>
		 * <h3 class="zh-CN">子查询构建器</h3>
		 *
		 * @param tableName <span class="en-US">Data table name</span>
		 *                  <span class="zh-CN">数据表名</span>
		 * @return <span class="en-US">Sub-query builder instance object</span>
		 * <span class="zh-CN">子查询构建器实例对象</span>
		 */
		public SubQueryBuilder<FromSubQueryBuilder> queryBuilder(@Nonnull final String tableName) {
			return new SubQueryBuilder<>(this, tableName);
		}

		@Override
		public void confirm(final Object object) {
			if (object instanceof QueryData) {
				this.fromSubQuery.setQueryData((QueryData) object);
			}
		}

		@Override
		public FromSubQuery build() throws BuilderException {
			return this.fromSubQuery;
		}
	}

	/**
	 * <h2 class="en-US">Query from information lists</h2>
	 * <h2 class="zh-CN">查询来源信息列表</h2>
	 *
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision: 1.0.0 $ $Date: Oct 28, 2020 11:46:08 $
	 */
	public static final class QueriesFrom {

		/**
		 * <span class="en-US">Query item instance list</span>
		 * <span class="zh-CN">查询项目实例对象列表</span>
		 */
		@Nonnull
		private final List<QueryFrom> fromList;

		/**
		 * <h3 class="en-US">Private constructor method for the query from information lists</h3>
		 * <h3 class="zh-CN">查询来源信息列表的私有构造方法</h3>
		 *
		 * @param fromList <span class="en-US">Query item instance list</span>
		 *                 <span class="zh-CN">查询项目实例对象列表</span>
		 */
		private QueriesFrom(@Nonnull final List<QueryFrom> fromList) {
			this.fromList = fromList.isEmpty() ? Collections.emptyList() : fromList;
		}

		/**
		 * <h3 class="en-US">Getter method for the query item instance list</h3>
		 * <h3 class="zh-CN">查询项目实例对象列表的Getter方法</h3>
		 *
		 * @return <span class="en-US">Query item instance list</span>
		 * <span class="zh-CN">查询项目实例对象列表</span>
		 */
		@Nonnull
		public List<QueryFrom> getFromList() {
			return this.fromList;
		}
	}
}
