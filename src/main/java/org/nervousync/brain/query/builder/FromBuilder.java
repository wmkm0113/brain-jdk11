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
import org.nervousync.brain.query.from.FromSubQuery;
import org.nervousync.brain.query.from.FromTable;
import org.nervousync.brain.query.subqueries.NestedTableSubQuery;
import org.nervousync.builder.AbstractBuilder;
import org.nervousync.builder.ParentBuilder;
import org.nervousync.exceptions.builder.BuilderException;

/**
 * <h2 class="en-US">Query from information lists builder</h2>
 * <h2 class="zh-CN">查询来源信息列表构建器</h2>
 *
 * @param <P> <span class="en-US">Parent builder generic type class</span>
 *            <span class="zh-CN">父构建器泛型类</span>
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 28, 2020 11:46:08 $
 */
public abstract class FromBuilder<P extends ParentBuilder, T extends QueryFrom> extends AbstractBuilder<P, T> {

	/**
	 * <span class="en-US">Query from instance lists</span>
	 * <span class="zh-CN">查询来源实例对象列表</span>
	 */
	@Nonnull
	protected final T queryFrom;

	/**
	 * <h3 class="en-US">Constructor method for the query from information lists builder</h3>
	 * <h3 class="zh-CN">查询来源信息列表构建器的构造方法</h3>
	 *
	 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
	 *                      <span class="zh-CN">父构建器实例对象</span>
	 * @param queryFrom     <span class="en-US">Query from instance</span>
	 *                      <span class="zh-CN">查询来源实例对象</span>
	 */
	protected FromBuilder(final P parentBuilder, @Nonnull final T queryFrom) {
		super(parentBuilder);
		this.queryFrom = queryFrom;
	}

	@Override
	public T build() throws BuilderException {
		return this.queryFrom;
	}

	/**
	 * <h2 class="en-US">Query from data table builder</h2>
	 * <h2 class="zh-CN">查询来源数据表构建器</h2>
	 *
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision: 1.0.0 $ $Date: Oct 28, 2020 11:46:08 $
	 */
	public static final class FromTableBuilder<P extends ParentBuilder> extends FromBuilder<P, FromTable> {

		/**
		 * <h3 class="en-US">Private constructor method for the query from data table builder</h3>
		 * <h3 class="zh-CN">查询来源数据表构建器的私有构造方法</h3>
		 *
		 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
		 *                      <span class="zh-CN">父构建器实例对象</span>
		 * @param tableName     <span class="en-US">Data table name</span>
		 *                      <span class="zh-CN">数据表名</span>
		 */
		public FromTableBuilder(final P parentBuilder, @Nonnull final String tableName) {
			super(parentBuilder, new FromTable());
			this.queryFrom.setTableName(tableName);
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
		public FromTableBuilder<P> sortCode(final int sortCode) {
			this.queryFrom.setSortCode(sortCode);
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
		public FromTableBuilder<P> aliasName(final String aliasName) {
			this.queryFrom.setAliasName(aliasName);
			return this;
		}
	}

	/**
	 * <h2 class="en-US">Query from sub-query builder</h2>
	 * <h2 class="zh-CN">查询来源子查询构建器</h2>
	 *
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision: 1.0.0 $ $Date: Oct 28, 2020 11:46:08 $
	 */
	public static final class FromSubQueryBuilder<P extends ParentBuilder> extends FromBuilder<P, FromSubQuery> {

		/**
		 * <h3 class="en-US">Private constructor method for the query from data table builder</h3>
		 * <h3 class="zh-CN">查询来源数据表构建器的私有构造方法</h3>
		 *
		 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
		 *                      <span class="zh-CN">父构建器实例对象</span>
		 * @param aliasName     <span class="en-US">Alias name</span>
		 *                      <span class="zh-CN">别名</span>
		 */
		public FromSubQueryBuilder(final P parentBuilder, final String aliasName) {
			super(parentBuilder, new FromSubQuery());
			this.queryFrom.setAliasName(aliasName);
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
		public FromSubQueryBuilder<P> sortCode(final int sortCode) {
			this.queryFrom.setSortCode(sortCode);
			return this;
		}

		/**
		 * <h3 class="en-US">Query from table information builder</h3>
		 * <h3 class="zh-CN">查询来源数据表信息构建器</h3>
		 *
		 * @return <span class="en-US">Sub-query builder instance object</span>
		 * <span class="zh-CN">子查询构建器实例对象</span>
		 */
		@Nonnull
		public SubQueryBuilder.NestedTableSubQueryBuilder<FromSubQueryBuilder<P>> builder() {
			return new SubQueryBuilder.NestedTableSubQueryBuilder<>(this);
		}

		@Override
		public void confirm(final Object object) {
			if (object instanceof NestedTableSubQuery) {
				this.queryFrom.setQueryData((NestedTableSubQuery) object);
			}
		}
	}
}
