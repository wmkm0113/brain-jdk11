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

package org.nervousync.brain.defines;

import jakarta.annotation.Nonnull;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.brain.dialects.core.BaseDialect;
import org.nervousync.commons.Globals;
import org.nervousync.utils.ObjectUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * <h2 class="en-US">Data column define</h2>
 * <h2 class="zh-CN">数据列定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Jun 27, 2018 23:02:27 $
 */
@XmlType(name = "column_define", namespace = "https://nervousync.org/schemas/brain")
@XmlRootElement(name = "column_define", namespace = "https://nervousync.org/schemas/brain")
public final class ColumnDefine extends BeanObject {

	/**
	 * <span class="en-US">Data column name</span>
	 * <span class="zh-CN">数据列名</span>
	 */
	@XmlElement(name = "column_name")
	private String columnName;
	/**
	 * <span class="en-US">JDBC data type code</span>
	 * <span class="zh-CN">JDBC数据类型代码</span>
	 */
	@XmlElement(name = "jdbc_type")
	private int jdbcType;
	/**
	 * <span class="en-US">Data column is nullable</span>
	 * <span class="zh-CN">数据列允许为空值</span>
	 */
	@XmlElement
	private boolean nullable;
	/**
	 * <span class="en-US">Data column length</span>
	 * <span class="zh-CN">数据列长度</span>
	 */
	@XmlElement
	private int length;
	/**
	 * <span class="en-US">Data column precision</span>
	 * <span class="zh-CN">数据列精度</span>
	 */
	@XmlElement
	private int precision;
	/**
	 * <span class="en-US">Data column scale</span>
	 * <span class="zh-CN">数据列小数位数</span>
	 */
	@XmlElement
	private int scale;
	/**
	 * <span class="en-US">Data column default value</span>
	 * <span class="zh-CN">数据列默认值</span>
	 */
	@XmlElement(name = "default_value")
	private String defaultValue;
	/**
	 * <span class="en-US">Data column is primary key</span>
	 * <span class="zh-CN">数据列是否为主键</span>
	 */
	@XmlElement(name = "primary_key")
	private boolean primaryKey;
	/**
	 * <span class="en-US">Data column is lazy load</span>
	 * <span class="zh-CN">数据列懒加载</span>
	 */
	@XmlElement(name = "lazy_load")
	private boolean lazyLoad;
	/**
	 * <span class="en-US">Data column is optimistic version column</span>
	 * <span class="zh-CN">数据列为乐观锁版本列</span>
	 */
	@XmlElement(name = "optimistic_version")
	private boolean version;
	/**
	 * <span class="en-US">Column name histories</span>
	 * <span class="zh-CN">历史列名</span>
	 */
	@XmlElement(name = "history_name")
	@XmlElementWrapper(name = "name_histories")
	private List<String> nameHistories = new ArrayList<>();
	/**
	 * <span class="en-US">Data column is unique</span>
	 * <span class="zh-CN">数据列是否唯一约束</span>
	 */
	@XmlElement
	private boolean unique;
	/**
	 * <span class="en-US">Data column can update</span>
	 * <span class="zh-CN">数据列允许更新</span>
	 */
	@XmlElement
	private boolean updatable;
	/**
	 * <span class="en-US">Column value generator configure</span>
	 * <span class="zh-CN">数据生成器配置</span>
	 */
	@XmlElement(name = "generator_define", namespace = "https://nervousync.org/schemas/brain")
	private GeneratorDefine generatorDefine;

	/**
	 * <h3 class="en-US">Constructor method for data column define</h3>
	 * <h3 class="zh-CN">数据列定义的构造方法</h3>
	 */
	public ColumnDefine() {
	}

	/**
	 * <h3 class="en-US">Generate data column define instance by given result set</h3>
	 * <h3 class="zh-CN">根据给定的查询结果集生成数据列定义实例对象</h3>
	 *
	 * @param resultSet   <span class="en-US">result set instance</span>
	 *                    <span class="zh-CN">查询结果集实例对象</span>
	 * @param baseDialect <span class="en-US">Database dialect instance object</span>
	 *                    <span class="zh-CN">数据库方言实例对象</span>
	 * @param primaryKeys <span class="en-US">List of primary key data column names</span>
	 *                    <span class="zh-CN">主键数据列名的列表</span>
	 * @param uniqueKeys  <span class="en-US">List of unique data column names</span>
	 *                    <span class="zh-CN">唯一约束列名的列表</span>
	 * @return <span class="en-US">Data column define instance object</span>
	 * <span class="zh-CN">数据列定义实例对象</span>
	 * @throws SQLException <span class="en-US">If an error occurs when parse result set instance</span>
	 *                      <span class="zh-CN">如果解析查询结果集时出现异常</span>
	 */
	public static ColumnDefine newInstance(@Nonnull final ResultSet resultSet, final BaseDialect baseDialect,
	                                       final List<String> primaryKeys, final List<String> uniqueKeys)
			throws SQLException {
		//  Oracle must read default value first
		String string = resultSet.getString("COLUMN_DEF");
		String columnName = resultSet.getString("COLUMN_NAME");
		int jdbcType = resultSet.getInt("DATA_TYPE");
		boolean nullable = ObjectUtils.nullSafeEquals("YES", resultSet.getString("IS_NULLABLE"));
		int length = resultSet.getInt("COLUMN_SIZE"),
				precision = Globals.INITIALIZE_INT_VALUE,
				scale = Globals.INITIALIZE_INT_VALUE;
		switch (jdbcType) {
			case Types.DECIMAL:
			case Types.FLOAT:
			case Types.NUMERIC:
			case Types.DOUBLE:
				precision = resultSet.getInt("COLUMN_SIZE");
				scale = resultSet.getInt("DECIMAL_DIGITS");
				break;
		}

		String defaultValue = baseDialect.parseDefault(jdbcType, length, precision, scale, string);

		ColumnDefine columnDefine = new ColumnDefine();
		columnDefine.setColumnName(columnName);
		columnDefine.setJdbcType(jdbcType);
		columnDefine.setNullable(nullable);
		columnDefine.setLength(length);
		columnDefine.setPrecision(precision);
		columnDefine.setScale(scale);
		columnDefine.setDefaultValue(defaultValue);
		columnDefine.setPrimaryKey(primaryKeys.contains(columnName));
		columnDefine.setUnique(uniqueKeys.contains(columnName));
		return columnDefine;
	}

	/**
	 * <h3 class="en-US">Getter method for data column name</h3>
	 * <h3 class="zh-CN">数据列名的Getter方法</h3>
	 *
	 * @return <span class="en-US">Data column name</span>
	 * <span class="zh-CN">数据列名</span>
	 */
	public String getColumnName() {
		return this.columnName;
	}

	/**
	 * <h3 class="en-US">Setter method for data column name</h3>
	 * <h3 class="zh-CN">数据列名的Setter方法</h3>
	 *
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 */
	public void setColumnName(final String columnName) {
		this.columnName = columnName;
	}

	/**
	 * <h3 class="en-US">Getter method for JDBC data type code</h3>
	 * <h3 class="zh-CN">JDBC数据类型代码的Getter方法</h3>
	 *
	 * @return <span class="en-US">JDBC data type code</span>
	 * <span class="zh-CN">JDBC数据类型代码</span>
	 */
	public int getJdbcType() {
		return this.jdbcType;
	}

	/**
	 * <h3 class="en-US">Setter method for JDBC data type code</h3>
	 * <h3 class="zh-CN">JDBC数据类型代码的Setter方法</h3>
	 *
	 * @param jdbcType <span class="en-US">JDBC data type code</span>
	 *                 <span class="zh-CN">JDBC数据类型代码</span>
	 */
	public void setJdbcType(final int jdbcType) {
		this.jdbcType = jdbcType;
	}

	/**
	 * <h3 class="en-US">Getter method for data column is nullable</h3>
	 * <h3 class="zh-CN">数据列允许为空值的Getter方法</h3>
	 *
	 * @return <span class="en-US">Data column is nullable</span>
	 * <span class="zh-CN">数据列允许为空值</span>
	 */
	public boolean isNullable() {
		return this.nullable;
	}

	/**
	 * <h3 class="en-US">Setter method for data column is nullable</h3>
	 * <h3 class="zh-CN">数据列允许为空值的Setter方法</h3>
	 *
	 * @param nullable <span class="en-US">Data column is nullable</span>
	 *                 <span class="zh-CN">数据列允许为空值</span>
	 */
	public void setNullable(final boolean nullable) {
		this.nullable = nullable;
	}

	/**
	 * <h3 class="en-US">Getter method for data column length</h3>
	 * <h3 class="zh-CN">数据列长度的Getter方法</h3>
	 *
	 * @return <span class="en-US">Data column length</span>
	 * <span class="zh-CN">数据列长度</span>
	 */
	public int getLength() {
		return this.length;
	}

	/**
	 * <h3 class="en-US">Setter method for data column length</h3>
	 * <h3 class="zh-CN">数据列长度的Setter方法</h3>
	 *
	 * @param length <span class="en-US">Data column length</span>
	 *               <span class="zh-CN">数据列长度</span>
	 */
	public void setLength(final int length) {
		this.length = length;
	}

	/**
	 * <h3 class="en-US">Getter method for data column precision</h3>
	 * <h3 class="zh-CN">数据列精度的Getter方法</h3>
	 *
	 * @return <span class="en-US">Data column precision</span>
	 * <span class="zh-CN">数据列精度</span>
	 */
	public int getPrecision() {
		return this.precision;
	}

	/**
	 * <h3 class="en-US">Setter method for data column precision</h3>
	 * <h3 class="zh-CN">数据列精度的Setter方法</h3>
	 *
	 * @param precision <span class="en-US">Data column precision</span>
	 *                  <span class="zh-CN">数据列精度</span>
	 */
	public void setPrecision(final int precision) {
		this.precision = precision;
	}

	/**
	 * <h3 class="en-US">Getter method for data column scale</h3>
	 * <h3 class="zh-CN">数据列小数位数的Getter方法</h3>
	 *
	 * @return <span class="en-US">Data column scale</span>
	 * <span class="zh-CN">数据列小数位数</span>
	 */
	public int getScale() {
		return this.scale;
	}

	/**
	 * <h3 class="en-US">Setter method for data column scale</h3>
	 * <h3 class="zh-CN">数据列小数位数的Setter方法</h3>
	 *
	 * @param scale <span class="en-US">Data column scale</span>
	 *              <span class="zh-CN">数据列小数位数</span>
	 */
	public void setScale(final int scale) {
		this.scale = scale;
	}

	/**
	 * <h3 class="en-US">Getter method for data column default value</h3>
	 * <h3 class="zh-CN">数据列默认值的Getter方法</h3>
	 *
	 * @return <span class="en-US">Data column default value</span>
	 * <span class="zh-CN">数据列默认值</span>
	 */
	public String getDefaultValue() {
		return this.defaultValue;
	}

	/**
	 * <h3 class="en-US">Setter method for data column default value</h3>
	 * <h3 class="zh-CN">数据列默认值的Setter方法</h3>
	 *
	 * @param defaultValue <span class="en-US">Data column default value</span>
	 *                     <span class="zh-CN">数据列默认值</span>
	 */
	public void setDefaultValue(final String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * <h3 class="en-US">Getter method for data column is primary key</h3>
	 * <h3 class="zh-CN">数据列是否为主键的Getter方法</h3>
	 *
	 * @return <span class="en-US">Data column is primary key</span>
	 * <span class="zh-CN">数据列是否为主键</span>
	 */
	public boolean isPrimaryKey() {
		return this.primaryKey;
	}

	/**
	 * <h3 class="en-US">Setter method for data column is primary key</h3>
	 * <h3 class="zh-CN">数据列是否为主键的Setter方法</h3>
	 *
	 * @param primaryKey <span class="en-US">Data column is primary key</span>
	 *                   <span class="zh-CN">数据列是否为主键</span>
	 */
	public void setPrimaryKey(final boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	/**
	 * <h3 class="en-US">Getter method for data column is lazy load</h3>
	 * <h3 class="zh-CN">数据列懒加载的Getter方法</h3>
	 *
	 * @return <span class="en-US">Data column is lazy load</span>
	 * <span class="zh-CN">数据列懒加载</span>
	 */
	public boolean isLazyLoad() {
		return this.lazyLoad;
	}

	/**
	 * <h3 class="en-US">Setter method for data column is lazy load</h3>
	 * <h3 class="zh-CN">数据列懒加载的Setter方法</h3>
	 *
	 * @param lazyLoad <span class="en-US">Data column is lazy load</span>
	 *                 <span class="zh-CN">数据列懒加载</span>
	 */
	public void setLazyLoad(final boolean lazyLoad) {
		this.lazyLoad = lazyLoad;
	}

	/**
	 * <h3 class="en-US">Getter method for data column is optimistic version column</h3>
	 * <h3 class="zh-CN">数据列为乐观锁版本列的Getter方法</h3>
	 *
	 * @return <span class="en-US">Data column is optimistic version column</span>
	 * <span class="zh-CN">数据列为乐观锁版本列</span>
	 */
	public boolean isVersion() {
		return this.version;
	}

	/**
	 * <h3 class="en-US">Setter method for data column is optimistic version column</h3>
	 * <h3 class="zh-CN">数据列为乐观锁版本列的Setter方法</h3>
	 *
	 * @param version <span class="en-US">Data column is optimistic version column</span>
	 *                <span class="zh-CN">数据列为乐观锁版本列</span>
	 */
	public void setVersion(final boolean version) {
		this.version = version;
	}

	/**
	 * <h3 class="en-US">Getter method for column name histories</h3>
	 * <h3 class="zh-CN">历史列名的Getter方法</h3>
	 *
	 * @return <span class="en-US">Column name histories</span>
	 * <span class="zh-CN">历史列名</span>
	 */
	public List<String> getNameHistories() {
		return this.nameHistories;
	}

	/**
	 * <h3 class="en-US">Setter method for column name histories</h3>
	 * <h3 class="zh-CN">历史列名的Setter方法</h3>
	 *
	 * @param nameHistories <span class="en-US">Column name histories</span>
	 *                      <span class="zh-CN">历史列名</span>
	 */
	public void setNameHistories(final List<String> nameHistories) {
		this.nameHistories = nameHistories;
	}

	/**
	 * <h3 class="en-US">Add column name history</h3>
	 * <h3 class="zh-CN">添加历史列名</h3>
	 *
	 * @param columnName <span class="en-US">History column name</span>
	 *                   <span class="zh-CN">历史列名</span>
	 */
	public void addNameHistory(final String columnName) {
		if (!this.nameHistories.contains(columnName)) {
			this.nameHistories.add(columnName);
		}
	}

	/**
	 * <h3 class="en-US">Getter method for data column is unique</h3>
	 * <h3 class="zh-CN">数据列是否唯一约束的Getter方法</h3>
	 *
	 * @return <span class="en-US">Data column is unique</span>
	 * <span class="zh-CN">数据列是否唯一约束</span>
	 */
	public boolean isUnique() {
		return this.unique;
	}

	/**
	 * <h3 class="en-US">Setter method for data column is unique</h3>
	 * <h3 class="zh-CN">数据列是否唯一约束的Setter方法</h3>
	 *
	 * @param unique <span class="en-US">Data column is unique</span>
	 *               <span class="zh-CN">数据列是否唯一约束</span>
	 */
	public void setUnique(final boolean unique) {
		this.unique = unique;
	}

	/**
	 * <h3 class="en-US">Getter method for data column can update</h3>
	 * <h3 class="zh-CN">数据列允许更新的Getter方法</h3>
	 *
	 * @return <span class="en-US">Data column can update</span>
	 * <span class="zh-CN">数据列允许更新</span>
	 */
	public boolean isUpdatable() {
		return this.updatable;
	}

	/**
	 * <h3 class="en-US">Setter method for data column can update</h3>
	 * <h3 class="zh-CN">数据列允许更新的Setter方法</h3>
	 *
	 * @param updatable <span class="en-US">Data column can update</span>
	 *                  <span class="zh-CN">数据列允许更新</span>
	 */
	public void setUpdatable(final boolean updatable) {
		this.updatable = updatable;
	}

	/**
	 * <h3 class="en-US">Getter method for column value generator configure</h3>
	 * <h3 class="zh-CN">数据生成器配置的Getter方法</h3>
	 *
	 * @return <span class="en-US">Column value generator configure</span>
	 * <span class="zh-CN">数据生成器配置</span>
	 */
	public GeneratorDefine getGeneratorDefine() {
		return this.generatorDefine;
	}

	/**
	 * <h3 class="en-US">Setter method for column value generator configure</h3>
	 * <h3 class="zh-CN">数据生成器配置的Setter方法</h3>
	 *
	 * @param generatorDefine <span class="en-US">Column value generator configure</span>
	 *                        <span class="zh-CN">数据生成器配置</span>
	 */
	public void setGeneratorDefine(final GeneratorDefine generatorDefine) {
		this.generatorDefine = generatorDefine;
	}

	/**
	 * <h3 class="en-US">Check the given column information was modified</h3>
	 * <h3 class="zh-CN">检查给定的列基本信息是否更改</h3>
	 *
	 * @param columnDefine <span class="en-US">Target data column define</span>
	 *                     <span class="zh-CN">目标数据列基本定义</span>
	 * @return <span class="en-US">Check result</span>
	 * <span class="zh-CN">检查结果</span>
	 */
	public boolean modified(@Nonnull final ColumnDefine columnDefine) {
		return !ObjectUtils.nullSafeEquals(this.jdbcType, columnDefine.getJdbcType())
				|| !ObjectUtils.nullSafeEquals(this.nullable, columnDefine.isNullable())
				|| !ObjectUtils.nullSafeEquals(this.length, columnDefine.getLength())
				|| !ObjectUtils.nullSafeEquals(this.precision, columnDefine.getPrecision())
				|| !ObjectUtils.nullSafeEquals(this.scale, columnDefine.getScale())
				|| !ObjectUtils.nullSafeEquals(this.defaultValue, columnDefine.getDefaultValue());
	}
}
