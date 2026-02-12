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
import org.intellij.lang.annotations.MagicConstant;
import org.nervousync.brain.enumerations.query.CalculateCode;
import org.nervousync.brain.query.item.CalculateItem;
import org.nervousync.brain.query.item.ColumnItem;
import org.nervousync.brain.query.item.FunctionItem;
import org.nervousync.brain.query.param.AbstractParameter;
import org.nervousync.brain.query.param.impl.CalculateParameter;
import org.nervousync.brain.query.param.impl.ColumnParameter;
import org.nervousync.brain.query.param.impl.ConstantParameter;
import org.nervousync.brain.query.param.impl.FunctionParameter;
import org.nervousync.builder.AbstractBuilder;
import org.nervousync.builder.ParentBuilder;
import org.nervousync.exceptions.builder.BuilderException;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * <h2 class="en-US">Function parameters information list builder</h2>
 * <h2 class="zh-CN">函数参数信息列表构建器</h2>
 *
 * @param <P> <span class="en-US">Parent builder generic type class</span>
 *            <span class="zh-CN">父构建器泛型类</span>
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 28, 2020 11:46:08 $
 */
@SuppressWarnings("unused")
public final class ParametersBuilder<P extends ParentBuilder> extends AbstractBuilder<P, ParametersBuilder.Parameters> {

	/**
	 * <span class="en-US">Function arguments list</span>
	 * <span class="zh-CN">函数参数列表</span>
	 */
	@Nonnull
	private final List<AbstractParameter<?>> parameterList = new ArrayList<>();

	/**
	 * <h3 class="en-US">Constructor method for the function parameters information list builder</h3>
	 * <h3 class="zh-CN">函数参数信息列表构建器的构造方法</h3>
	 *
	 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
	 *                      <span class="zh-CN">父构建器实例对象</span>
	 * @param parameterList <span class="en-US">Function arguments list</span>
	 *                      <span class="zh-CN">函数参数列表</span>
	 */
	public ParametersBuilder(final P parentBuilder, final List<AbstractParameter<?>> parameterList) {
		super(parentBuilder);
		if (parameterList != null) {
			this.parameterList.addAll(parameterList);
		}
	}

	/**
	 * <h3 class="en-US">Calculate function parameter item builder</h3>
	 * <h3 class="zh-CN">计算参数信息构建器</h3>
	 *
	 * @return <span class="en-US">Calculate function parameter item builder instance object</span>
	 * <span class="zh-CN">计算参数信息构建器实例对象</span>
	 */
	public CalculateParameterBuilder<ParametersBuilder<P>> calculate() {
		return new CalculateParameterBuilder<>(this);
	}

	/**
	 * <h3 class="en-US">Data column function parameter item builder</h3>
	 * <h3 class="zh-CN">数据列参数信息构建器</h3>
	 *
	 * @return <span class="en-US">Data column function parameter item builder instance object</span>
	 * <span class="zh-CN">数据列参数信息构建器实例对象</span>
	 */
	public ColumnParameterBuilder<ParametersBuilder<P>> column() {
		return new ColumnParameterBuilder<>(this);
	}

	/**
	 * <h3 class="en-US">Constant value function parameter item builder</h3>
	 * <h3 class="zh-CN">常量值参数信息构建器</h3>
	 *
	 * @return <span class="en-US">Constant value function parameter item builder instance object</span>
	 * <span class="zh-CN">常量值参数信息构建器实例对象</span>
	 */
	public ConstantParameterBuilder<ParametersBuilder<P>> constant() {
		return new ConstantParameterBuilder<>(this);
	}

	/**
	 * <h3 class="en-US">Function parameter item builder</h3>
	 * <h3 class="zh-CN">函数参数信息构建器</h3>
	 *
	 * @return <span class="en-US">Function parameter item builder instance object</span>
	 * <span class="zh-CN">函数参数信息构建器实例对象</span>
	 */
	public FunctionParameterBuilder<ParametersBuilder<P>> function() {
		return new FunctionParameterBuilder<>(this);
	}

	@Override
	public void confirm(final Object object) {
		if (object instanceof AbstractParameter<?>) {
			this.parameterList.add((AbstractParameter<?>) object);
		}
	}

	@Override
	public Parameters build() throws BuilderException {
		return new Parameters(this.parameterList);
	}

	/**
	 * <h2 class="en-US">Function parameters information list</h2>
	 * <h2 class="zh-CN">函数参数信息列表</h2>
	 *
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision: 1.0.0 $ $Date: Oct 28, 2020 11:46:08 $
	 */
	public static final class Parameters {

		/**
		 * <span class="en-US">Function arguments list</span>
		 * <span class="zh-CN">函数参数列表</span>
		 */
		@Nonnull
		private final List<AbstractParameter<?>> functionParams;

		/**
		 * <h3 class="en-US">Constructor method for the function parameters information list</h3>
		 * <h3 class="zh-CN">函数参数信息列表的构造方法</h3>
		 *
		 * @param functionParams <span class="en-US">Function arguments list</span>
		 *                       <span class="zh-CN">函数参数列表</span>
		 */
		public Parameters(@Nonnull final List<AbstractParameter<?>> functionParams) {
			this.functionParams = functionParams;
		}

		/**
		 * <h3 class="en-US">Getter method for the function arguments list</h3>
		 * <h3 class="zh-CN">函数参数列表的Getter方法</h3>
		 *
		 * @return <span class="en-US">Function arguments list</span>
		 * <span class="zh-CN">函数参数列表</span>
		 */
		@Nonnull
		public List<AbstractParameter<?>> getFunctionParams() {
			return this.functionParams;
		}
	}

	/**
	 * <h2 class="en-US">Abstract class for parameter information builder</h2>
	 * <h2 class="zh-CN">参数信息构建器抽象类</h2>
	 *
	 * @param <P> <span class="en-US">Parent builder generic type class</span>
	 *            <span class="zh-CN">父构建器泛型类</span>
	 * @param <T> <span class="en-US">Parameter information generic type class</span>
	 *            <span class="zh-CN">参数信息泛型类</span>
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision: 1.0.0 $ $Date: Oct 9, 2020 11:42:46 $
	 */
	public static abstract class ParameterBuilder<P extends ParentBuilder, T extends AbstractParameter<?>>
			extends AbstractBuilder<P, T> {

		/**
		 * <span class="en-US">Parameter information instance object</span>
		 * <span class="zh-CN">参数信息实例对象</span>
		 */
		protected final T parameter;

		/**
		 * <h3 class="en-US">Protected constructor for AbstractBuilder</h3>
		 * <h3 class="zh-CN">AbstractBuilder的构造函数</h3>
		 *
		 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
		 *                      <span class="zh-CN">父构建器实例对象</span>
		 * @param parameter     <span class="en-US">Parameter information instance object</span>
		 *                      <span class="zh-CN">参数信息实例对象</span>
		 */
		protected ParameterBuilder(final P parentBuilder, final T parameter) {
			super(parentBuilder);
			this.parameter = parameter;
		}

		@Override
		public final T build() throws BuilderException {
			return this.parameter;
		}
	}

	/**
	 * <h2 class="en-US">Calculate parameter information builder</h2>
	 * <h2 class="zh-CN">计算参数信息构建器</h2>
	 *
	 * @param <P> <span class="en-US">Parent builder generic type class</span>
	 *            <span class="zh-CN">父构建器泛型类</span>
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision: 1.0.0 $ $Date: Oct 9, 2020 11:42:46 $
	 */
	public static final class CalculateParameterBuilder<P extends ParentBuilder>
			extends ParameterBuilder<P, CalculateParameter> {

		/**
		 * <h3 class="en-US">Constructor method for calculating parameter information builder</h3>
		 * <h3 class="zh-CN">计算参数信息构建器的构造函数</h3>
		 *
		 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
		 *                      <span class="zh-CN">父构建器实例对象</span>
		 */
		CalculateParameterBuilder(final P parentBuilder) {
			super(parentBuilder, new CalculateParameter());
		}

		/**
		 * <h3 class="en-US">Calculate item builder instance object</h3>
		 * <h3 class="zh-CN">计算项目构建器实例对象</h3>
		 *
		 * @param calculateCode <span class="en-US">Enumeration value of calculate code</span>
		 *                      <span class="zh-CN">计算代码的枚举值</span>
		 * @param jdbcType      <span class="en-US">Jdbc type code</span>
		 *                      <span class="zh-CN">JDBC类型代码</span>
		 * @return <span class="en-US">Calculate item builder instance object</span>
		 * <span class="zh-CN">计算项目构建器实例对象</span>
		 */
		public ItemsBuilder.CalculateItemBuilder<CalculateParameterBuilder<P>> calculate(final CalculateCode calculateCode,
		                                                                                 @MagicConstant(valuesFromClass = Types.class) final int jdbcType) {
			return new ItemsBuilder.CalculateItemBuilder<>(this, calculateCode, jdbcType);
		}

		@Override
		public void confirm(final Object object) {
			if (object instanceof CalculateItem) {
				this.parameter.setItemValue((CalculateItem) object);
			}
		}
	}

	/**
	 * <h2 class="en-US">Data column parameter information builder</h2>
	 * <h2 class="zh-CN">数据列参数信息构建器</h2>
	 *
	 * @param <P> <span class="en-US">Parent builder generic type class</span>
	 *            <span class="zh-CN">父构建器泛型类</span>
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision: 1.0.0 $ $Date: Oct 9, 2020 11:42:46 $
	 */
	public static final class ColumnParameterBuilder<P extends ParentBuilder>
			extends ParameterBuilder<P, ColumnParameter> {

		/**
		 * <h3 class="en-US">Constructor method for the data column parameter information builder</h3>
		 * <h3 class="zh-CN">数据列参数信息构建器的构造函数</h3>
		 *
		 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
		 *                      <span class="zh-CN">父构建器实例对象</span>
		 */
		ColumnParameterBuilder(final P parentBuilder) {
			super(parentBuilder, new ColumnParameter());
		}

		/**
		 * <h3 class="en-US">Configure data column information</h3>
		 * <h3 class="zh-CN">设置数据列信息</h3>
		 *
		 * @param tableName  <span class="en-US">Data table name</span>
		 *                   <span class="zh-CN">数据表名</span>
		 * @param columnName <span class="en-US">Data column name</span>
		 *                   <span class="zh-CN">数据列名</span>
		 * @param distinct   <span class="en-US">Column distinct</span>
		 *                   <span class="zh-CN">数据列去重</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public ColumnParameterBuilder<P> value(final String tableName, final String columnName, final boolean distinct) {
			ColumnItem columnItem = new ColumnItem();
			columnItem.setTableName(tableName);
			columnItem.setColumnName(columnName);
			columnItem.setDistinct(distinct);
			this.parameter.setItemValue(columnItem);
			return this;
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
		public ColumnParameterBuilder<P> sortCode(final int sortCode) {
			this.parameter.setSortCode(sortCode);
			return this;
		}
	}

	/**
	 * <h2 class="en-US">Constant value parameter information builder</h2>
	 * <h2 class="zh-CN">常量值参数信息构建器</h2>
	 *
	 * @param <P> <span class="en-US">Parent builder generic type class</span>
	 *            <span class="zh-CN">父构建器泛型类</span>
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision: 1.0.0 $ $Date: Oct 9, 2020 11:42:46 $
	 */
	public static final class ConstantParameterBuilder<P extends ParentBuilder>
			extends ParameterBuilder<P, ConstantParameter> {

		/**
		 * <h3 class="en-US">Constructor method for the constant value parameter information builder</h3>
		 * <h3 class="zh-CN">常量值参数信息构建器的构造函数</h3>
		 *
		 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
		 *                      <span class="zh-CN">父构建器实例对象</span>
		 */
		ConstantParameterBuilder(final P parentBuilder) {
			super(parentBuilder, new ConstantParameter());
		}

		/**
		 * <h3 class="en-US">Configure constant value information</h3>
		 * <h3 class="zh-CN">设置常量值信息</h3>
		 *
		 * @param itemValue <span class="en-US">Constant value</span>
		 *                  <span class="zh-CN">常量值</span>
		 * @return <span class="en-US">Current builder instance object</span>
		 * <span class="zh-CN">当前构建器实例对象</span>
		 */
		public ConstantParameterBuilder<P> value(final Object itemValue) {
			this.parameter.setItemValue(itemValue);
			return this;
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
		public ConstantParameterBuilder<P> sortCode(final int sortCode) {
			this.parameter.setSortCode(sortCode);
			return this;
		}
	}

	/**
	 * <h2 class="en-US">Function type parameter information builder</h2>
	 * <h2 class="zh-CN">函数型参数信息构建器</h2>
	 *
	 * @param <P> <span class="en-US">Parent builder generic type class</span>
	 *            <span class="zh-CN">父构建器泛型类</span>
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision: 1.0.0 $ $Date: Oct 9, 2020 11:42:46 $
	 */
	public static final class FunctionParameterBuilder<P extends ParentBuilder>
			extends ParameterBuilder<P, FunctionParameter> {

		/**
		 * <h3 class="en-US">Constructor method for the function type parameter information builder</h3>
		 * <h3 class="zh-CN">函数型参数信息构建器的构造函数</h3>
		 *
		 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
		 *                      <span class="zh-CN">父构建器实例对象</span>
		 */
		FunctionParameterBuilder(final P parentBuilder) {
			super(parentBuilder, new FunctionParameter());
		}

		/**
		 * <h3 class="en-US">Function item builder instance object</h3>
		 * <h3 class="zh-CN">函数信息构建器实例对象</h3>
		 *
		 * @param functionName <span class="en-US">Function name</span>
		 *                     <span class="zh-CN">函数名</span>
		 * @param jdbcType     <span class="en-US">Jdbc type code</span>
		 *                     <span class="zh-CN">JDBC类型代码</span>
		 * @return <span class="en-US">Calculate item builder instance object</span>
		 * <span class="zh-CN">计算项目构建器实例对象</span>
		 */
		public ItemsBuilder.FunctionItemBuilder<FunctionParameterBuilder<P>> functionName(final String functionName,
		                                                                                  @MagicConstant(valuesFromClass = Types.class) final int jdbcType) {
			return new ItemsBuilder.FunctionItemBuilder<>(this, functionName, jdbcType);
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
		public FunctionParameterBuilder<P> sortCode(final int sortCode) {
			this.parameter.setSortCode(sortCode);
			return this;
		}

		@Override
		public void confirm(final Object object) {
			if (object instanceof FunctionItem) {
				this.parameter.setItemValue((FunctionItem) object);
			}
		}
	}
}
