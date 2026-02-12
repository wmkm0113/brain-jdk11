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

package org.nervousync.brain.dialects.core;

import jakarta.annotation.Nonnull;
import org.nervousync.brain.annotations.dialect.DataType;
import org.nervousync.brain.annotations.dialect.SchemaDialect;
import org.nervousync.brain.commons.BrainCommons;
import org.nervousync.brain.defines.ColumnDefine;
import org.nervousync.brain.dialects.Dialect;
import org.nervousync.brain.enumerations.dialect.DialectType;
import org.nervousync.brain.exceptions.dialects.DialectException;
import org.nervousync.brain.manager.TableManager;
import org.nervousync.brain.query.condition.impl.ColumnCondition;
import org.nervousync.brain.query.item.ColumnItem;
import org.nervousync.brain.query.param.AbstractParameter;
import org.nervousync.commons.Globals;
import org.nervousync.utils.core.ClassUtils;
import org.nervousync.utils.core.StringUtils;
import org.nervousync.utils.logger.LoggerUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * <h2 class="en-US">Database dialect abstract class</h2>
 * <h2 class="zh-CN">数据库方言抽象类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Feb 18, 2019 10:38:52 $
 */
public abstract class BaseDialect implements Dialect {

	/**
	 * <span class="en-US">Logger instance</span>
	 * <span class="zh-CN">日志实例</span>
	 */
	protected final LoggerUtils.Logger logger = LoggerUtils.getLogger(this.getClass());

	/**
	 * <span class="en-US">Data source dialect type enumeration value</span>
	 * <span class="zh-CN">数据源方言类型枚举值</span>
	 */
	private final DialectType dialectType;
	/**
	 * <span class="en-US">Support join query</span>
	 * <span class="zh-CN">支持关联查询</span>
	 */
	private final boolean supportJoin;
	/**
	 * <span class="en-US">Support connection pool</span>
	 * <span class="zh-CN">支持数据库连接池</span>
	 */
	private final boolean connectionPool;
	/**
	 * <span class="en-US">Support dynamic database switching</span>
	 * <span class="zh-CN">支持数据库动态切换</span>
	 */
	private final boolean databaseSharding;
	/**
	 * <span class="en-US">Connection verification query command</span>
	 * <span class="zh-CN">连接验证查询命令</span>
	 */
	private final String validationQuery;
	/**
	 * <span class="en-US">Data type definition mapping table</span>
	 * <span class="zh-CN">数据类型定义映射表</span>
	 */
	private final Hashtable<Integer, String> dataTypes = new Hashtable<>();
	/**
	 * <span class="en-US">Data table manager instance object</span>
	 * <span class="zh-CN">数据表管理器实例对象</span>
	 */
	protected final TableManager tableManager;

	/**
	 * <h3 class="en-US">Constructor method for Database dialect abstract class</h3>
	 * <h3 class="zh-CN">数据库方言抽象类的构造方法</h3>
	 *
	 * @param dialectType <span class="en-US">Data source dialect type enumeration value</span>
	 *                    <span class="zh-CN">数据源方言类型枚举值</span>
	 * @throws DialectException <span class="en-US">If the implementation class does not find the org.nervousync.brain.annotations.dialect.SchemaDialect annotation</span>
	 *                          <span class="zh-CN">如果实现类未找到org.nervousync.brain.annotations.dialect.SchemaDialect注解</span>
	 */
	protected BaseDialect(@Nonnull final DialectType dialectType) throws DialectException {
		SchemaDialect schemaDialect = Optional.ofNullable(this.getClass().getAnnotation(SchemaDialect.class))
				.orElseThrow(() -> new DialectException(0x00DB00000005L, this.getClass().getName()));
		this.dialectType = dialectType;
		String className = this.getClass().getName();
		this.supportJoin = schemaDialect.supportJoin();
		this.connectionPool = schemaDialect.connectionPool();
		if (DialectType.Relational.equals(this.dialectType)) {
			this.databaseSharding = schemaDialect.sharding();
		} else {
			this.databaseSharding = Boolean.FALSE;
		}
		this.validationQuery = schemaDialect.validationQuery();
		if (schemaDialect.types().length == 0) {
			this.logger.warn("Dialect_Type_None", className);
		}
		for (DataType dataType : schemaDialect.types()) {
			if (this.dataTypes.containsKey(dataType.code())) {
				this.logger.warn("Dialect_Type_Override", className, dataType.code());
			}
			this.dataTypes.put(dataType.code(), dataType.type());
		}
		this.tableManager = TableManager.getInstance();
	}

	@Override
	@Nonnull
	public final DialectType type() {
		return this.dialectType;
	}

	/**
	 * <h3 class="en-US">Convert default value to string</h3>
	 * <h3 class="zh-CN">转换默认值为字符串</h3>
	 *
	 * @param columnDefine <span class="en-US">Column define information</span>
	 *                     <span class="zh-CN">数据列定义信息</span>
	 * @param object       <span class="en-US">Default value instance object</span>
	 *                     <span class="zh-CN">默认值实例对象</span>
	 * @return <span class="en-US">Default value string</span>
	 * <span class="zh-CN">默认值字符串</span>
	 */
	public String defaultValue(final ColumnDefine columnDefine, final Object object) {
		return Globals.DEFAULT_VALUE_STRING;
	}

	/**
	 * <h3 class="en-US">Parse database-defined default values</h3>
	 * <h3 class="zh-CN">解析数据库定义的默认值</h3>
	 *
	 * @param jdbcType     <span class="en-US">JDBC type code</span>
	 *                     <span class="zh-CN">JDBC类型代码</span>
	 * @param length       <span class="en-US">Data column length</span>
	 *                     <span class="zh-CN">数据列长度</span>
	 * @param precision    <span class="en-US">The precision for a decimal (exact numeric) column</span>
	 *                     <span class="zh-CN">小数（精确数字）列的精度</span>
	 * @param scale        <span class="en-US">The scale for a decimal (exact numeric) column</span>
	 *                     <span class="zh-CN">小数（精确数字）列的比例</span>
	 * @param defaultValue <span class="en-US">Database definition default value string</span>
	 *                     <span class="zh-CN">数据库定义默认值字符串</span>
	 * @return <span class="en-US">Default value string</span>
	 * <span class="zh-CN">默认值字符串</span>
	 */
	public String parseDefault(final int jdbcType, final int length, final int precision, final int scale,
	                           final String defaultValue) {
		switch (jdbcType) {
			case Types.BOOLEAN:
				return Boolean.valueOf(defaultValue).toString();
			case Types.BIT:
				return Boolean.parseBoolean(defaultValue) ? "1" : "0";
			case Types.TINYINT:
				return Byte.valueOf(defaultValue).toString();
			case Types.SMALLINT:
				return Short.valueOf(defaultValue).toString();
			case Types.INTEGER:
				return Integer.valueOf(defaultValue).toString();
			case Types.BIGINT:
				return Long.valueOf(defaultValue).toString();
			case Types.FLOAT:
				return Float.valueOf(defaultValue).toString();
			case Types.DOUBLE:
				return Double.valueOf(defaultValue).toString();
			case Types.DECIMAL:
			case Types.NUMERIC:
				return new BigDecimal(defaultValue)
						.round(new MathContext(precision, RoundingMode.FLOOR))
						.setScale(scale, RoundingMode.HALF_UP).toString();
			case Types.DATE:
			case Types.TIME:
			case Types.TIMESTAMP:
				return Globals.DEFAULT_VALUE_STRING;
			default:
				String returnValue = StringUtils.isEmpty(defaultValue) ? Globals.DEFAULT_VALUE_STRING : defaultValue.trim();
				if (returnValue.length() > length) {
					returnValue = returnValue.substring(returnValue.length() - length);
				}
				return returnValue;
		}
	}

	/**
	 * <h3 class="en-US">Checks whether the given dialect type is consistent with the current dialect type</h3>
	 * <h3 class="zh-CN">检查给定的方言类型与当前方言类型是否一致</h3>
	 *
	 * @param dialectType <span class="en-US">Data source dialect type enumeration value</span>
	 *                    <span class="zh-CN">数据源方言类型枚举值</span>
	 * @return <span class="en-US">Check result</span>
	 * <span class="zh-CN">检查结果</span>
	 */
	public final boolean match(final DialectType dialectType) {
		return DialectType.Default.equals(dialectType) || this.dialectType.equals(dialectType);
	}

	/**
	 * <h3 class="en-US">Getter method for the support join query</h3>
	 * <h3 class="zh-CN">支持关联查询的Getter方法</h3>
	 *
	 * @return <span class="en-US">Support join query</span>
	 * <span class="zh-CN">支持关联查询</span>
	 */
	public final boolean supportJoin() {
		return this.supportJoin;
	}

	/**
	 * <h3 class="en-US">Getter method for support connection pool</h3>
	 * <h3 class="zh-CN">支持数据库连接池的Getter方法</h3>
	 *
	 * @return <span class="en-US">Support connection pool</span>
	 * <span class="zh-CN">支持数据库连接池</span>
	 */
	public final boolean isConnectionPool() {
		return this.connectionPool;
	}

	/**
	 * <h3 class="en-US">Getter method for support dynamic database switching</h3>
	 * <h3 class="zh-CN">支持数据库动态切换的Getter方法</h3>
	 *
	 * @return <span class="en-US">Support dynamic database switching</span>
	 * <span class="zh-CN">支持数据库动态切换</span>
	 */
	public boolean isDatabaseSharding() {
		return this.databaseSharding;
	}

	/**
	 * <h3 class="en-US">Getter method for connection verification query command</h3>
	 * <h3 class="zh-CN">连接验证查询命令的Getter方法</h3>
	 *
	 * @return <span class="en-US">Connection verification query command</span>
	 * <span class="zh-CN">连接验证查询命令</span>
	 */
	public final String getValidationQuery() {
		return this.validationQuery;
	}

	/**
	 * <h3 class="en-US">Get the type definition of the data column based on the JDBC type value</h3>
	 * <h3 class="zh-CN">根据JDBC类型值获取数据列的类型定义</h3>
	 *
	 * @param columnDefine <span class="en-US">Column define information</span>
	 *                     <span class="zh-CN">数据列定义信息</span>
	 * @return <span class="en-US">
	 * The type definition of the data column
	 * If it is not defined, an empty string of zero lengths is returned.
	 * </span>
	 * <span class="zh-CN">数据列的类型定义，如果未定义则返回长度为零的空字符串</span>
	 */
	public final String columnType(final ColumnDefine columnDefine) {
		String columnType = this.dataTypes.getOrDefault(columnDefine.getJdbcType(), Globals.DEFAULT_VALUE_STRING);
		if (StringUtils.notBlank(columnType)) {
			columnType = StringUtils.replace(columnType, "{length}",
					Integer.toString(Integer.max(columnDefine.getLength(), Globals.INITIALIZE_INT_VALUE)));
			columnType = StringUtils.replace(columnType, "{precision}",
					Integer.toString(Integer.max(columnDefine.getPrecision(), Globals.INITIALIZE_INT_VALUE)));
			columnType = StringUtils.replace(columnType, "{scale}",
					Integer.toString(Integer.max(columnDefine.getScale(), Globals.INITIALIZE_INT_VALUE)));
		}
		return columnType;
	}

	@Override
	public final <T> T unwrap(final Class<T> clazz) throws SQLException {
		try {
			return clazz.cast(this);
		} catch (ClassCastException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public final boolean isWrapperFor(final Class<?> clazz) {
		return ClassUtils.isAssignable(clazz, this.getClass());
	}

	/**
	 * <h3 class="en-US">Generate query parameter commands</h3>
	 * <h3 class="zh-CN">生成查询参数命令</h3>
	 *
	 * @param aliasMap        <span class="en-US">Data table alias mapping table</span>
	 *                        <span class="zh-CN">数据表别名映射表</span>
	 * @param columnCondition <span class="en-US">Query condition</span>
	 *                        <span class="zh-CN">查询信息</span>
	 * @param values          <span class="en-US">Parameter value list</span>
	 *                        <span class="zh-CN">参数值列表</span>
	 * @return <span class="en-US">Generated SQL command</span>
	 * <span class="zh-CN">生成的SQL命令</span>
	 * @throws SQLException <span class="en-US">An error occurred while generating the SQL command</span>
	 *                      <span class="zh-CN">生成的SQL命令时出现错误</span>
	 */
	protected final String columnCondition(final Map<String, String> aliasMap, final ColumnCondition columnCondition,
	                                       final List<Object> values) throws SQLException {
		StringBuilder sqlBuilder = new StringBuilder();
		if (StringUtils.notBlank(columnCondition.getFunctionName())) {
			sqlBuilder.append(columnCondition.getFunctionName()).append(BrainCommons.BRACKETS_BEGIN);
		}
		sqlBuilder.append(this.columnName(aliasMap, columnCondition.getTableName(), columnCondition.getColumnName()));
		if (StringUtils.notBlank(columnCondition.getFunctionName())) {
			sqlBuilder.append(BrainCommons.BRACKETS_END);
		}
		switch (columnCondition.getConditionCode()) {
			case IN:
				sqlBuilder.append(BrainCommons.OPERATOR_IN);
				break;
			case LESS:
				sqlBuilder.append(BrainCommons.OPERATOR_LESS);
				break;
			case LIKE:
				sqlBuilder.append(BrainCommons.OPERATOR_LIKE);
				break;
			case EQUAL:
				sqlBuilder.append(BrainCommons.OPERATOR_EQUAL);
				break;
			case EXISTS:
				sqlBuilder.append(BrainCommons.OPERATOR_EXISTS);
				break;
			case NOT_IN:
				sqlBuilder.append(BrainCommons.OPERATOR_NOT_IN);
				break;
			case GREATER:
				sqlBuilder.append(BrainCommons.OPERATOR_GREATER);
				break;
			case IS_NULL:
				sqlBuilder.append(BrainCommons.OPERATOR_IS_NULL);
				break;
			case NOT_LIKE:
				sqlBuilder.append(BrainCommons.OPERATOR_NOT_LIKE);
				break;
			case NOT_NULL:
				sqlBuilder.append(BrainCommons.OPERATOR_NOT_NULL);
				break;
			case NOT_EQUAL:
				sqlBuilder.append(BrainCommons.OPERATOR_NOT_EQUAL);
				break;
			case LESS_EQUAL:
				sqlBuilder.append(BrainCommons.OPERATOR_LESS_EQUAL);
				break;
			case NOT_EXISTS:
				sqlBuilder.append(BrainCommons.OPERATOR_NOT_EXISTS);
				break;
			case BETWEEN_AND:
				sqlBuilder.append(BrainCommons.OPERATOR_BETWEEN_AND);
				break;
			case GREATER_EQUAL:
				sqlBuilder.append(BrainCommons.OPERATOR_GREATER_EQUAL);
				break;
			case NOT_BETWEEN_AND:
				sqlBuilder.append(BrainCommons.OPERATOR_NOT_BETWEEN_AND);
				break;
		}

		if (columnCondition.getConditionParameter() != null) {
			sqlBuilder.append(this.parameterValue(aliasMap, columnCondition.getConditionParameter(), values));
		}

		return sqlBuilder.toString();
	}

	/**
	 * <h3 class="en-US">Generate query column name commands</h3>
	 * <h3 class="zh-CN">生成查询列名称命令</h3>
	 *
	 * @param aliasMap   <span class="en-US">Data table alias mapping table</span>
	 *                   <span class="zh-CN">数据表别名映射表</span>
	 * @param columnItem <span class="en-US">Query data column define information</span>
	 *                   <span class="zh-CN">查询数据列信息定义</span>
	 * @return <span class="en-US">Generated SQL command</span>
	 * <span class="zh-CN">生成的SQL命令</span>
	 */
	protected final String columnName(final Map<String, String> aliasMap, final ColumnItem columnItem) {
		return this.columnName(aliasMap, columnItem.getTableName(), columnItem.getColumnName());
	}

	/**
	 * <h3 class="en-US">Generate query column name commands</h3>
	 * <h3 class="zh-CN">生成查询列名称命令</h3>
	 *
	 * @param aliasMap     <span class="en-US">Data table alias mapping table</span>
	 *                     <span class="zh-CN">数据表别名映射表</span>
	 * @param identifyCode <span class="en-US">Identify code</span>
	 *                     <span class="zh-CN">识别代码</span>
	 * @param columnName   <span class="en-US">Data column name</span>
	 *                     <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Generated SQL command</span>
	 * <span class="zh-CN">生成的SQL命令</span>
	 */
	protected final String columnName(final Map<String, String> aliasMap, final String identifyCode,
	                                  final String columnName) {
		StringBuilder stringBuilder = new StringBuilder();
		if (StringUtils.notBlank(identifyCode)) {
			if (aliasMap.containsKey(identifyCode)) {
				stringBuilder.append(aliasMap.get(identifyCode)).append(BrainCommons.DEFAULT_NAME_SPLIT);
			} else if (aliasMap.containsValue(identifyCode)) {
				stringBuilder.append(identifyCode).append(BrainCommons.DEFAULT_NAME_SPLIT);
			}
		}
		stringBuilder.append(columnName);
		return this.nameCase(stringBuilder.toString());
	}

	/**
	 * <h3 class="en-US">Generate query parameter value commands</h3>
	 * <h3 class="zh-CN">生成查询参数值命令</h3>
	 *
	 * @param aliasMap          <span class="en-US">Data table alias mapping table</span>
	 *                          <span class="zh-CN">数据表别名映射表</span>
	 * @param abstractParameter <span class="en-US">Query parameter value</span>
	 *                          <span class="zh-CN">查询参数信息</span>
	 * @param values            <span class="en-US">Parameter value list</span>
	 *                          <span class="zh-CN">参数值列表</span>
	 * @return <span class="en-US">Generated SQL command</span>
	 * <span class="zh-CN">生成的SQL命令</span>
	 * @throws SQLException <span class="en-US">An error occurred while generating the SQL command</span>
	 *                      <span class="zh-CN">生成的SQL命令时出现错误</span>
	 */
	protected abstract String parameterValue(final Map<String, String> aliasMap,
	                                         final AbstractParameter<?> abstractParameter,
	                                         final List<Object> values) throws SQLException;
}
