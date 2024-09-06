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
import org.nervousync.brain.defines.ColumnDefine;
import org.nervousync.brain.dialects.Dialect;
import org.nervousync.brain.exceptions.dialects.DialectException;
import org.nervousync.commons.Globals;
import org.nervousync.utils.LoggerUtils;
import org.nervousync.utils.StringUtils;

import java.util.Hashtable;
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
	 * <span class="en-US">Dialect name</span>
	 * <span class="zh-CN">方言名称</span>
	 */
	private final String dialectName;
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
	 * <h3 class="en-US">Constructor method for Database dialect abstract class</h3>
	 * <h3 class="zh-CN">数据库方言抽象类的构造方法</h3>
	 *
	 * @throws DialectException <span class="en-US">If the implementation class does not find the org. nervousync. brain. annotations. dialect.SchemaDialect annotation</span>
	 *                          <span class="zh-CN">如果实现类未找到org. nervousync. brain. annotations. dialect.SchemaDialect注解</span>
	 */
	protected BaseDialect() throws DialectException {
		SchemaDialect schemaDialect = Optional.ofNullable(this.getClass().getAnnotation(SchemaDialect.class))
				.orElseThrow(() -> new DialectException(0x00DB00000005L, this.getClass().getName()));
		this.dialectName = schemaDialect.name();
		this.supportJoin = schemaDialect.supportJoin();
		this.connectionPool = schemaDialect.connectionPool();
		this.validationQuery = schemaDialect.validationQuery();
		if (schemaDialect.types().length == 0) {
			this.logger.warn("Dialect_Type_None", this.dialectName);
		}
		for (DataType dataType : schemaDialect.types()) {
			if (this.dataTypes.containsKey(dataType.code())) {
				this.logger.warn("Dialect_Type_Override", this.dialectName, dataType.code());
			}
			this.dataTypes.put(dataType.code(), dataType.type());
		}
	}

	/**
	 * <h3 class="en-US">Getter method for dialect name</h3>
	 * <h3 class="zh-CN">方言名称的Getter方法</h3>
	 *
	 * @return <span class="en-US">Dialect name</span>
	 * <span class="zh-CN">方言名称</span>
	 */
	public final String getDialectName() {
		return this.dialectName;
	}

	/**
	 * <h3 class="en-US">Getter method for support join query</h3>
	 * <h3 class="zh-CN">支持关联查询的Getter方法</h3>
	 *
	 * @return <span class="en-US">Support join query</span>
	 * <span class="zh-CN">支持关联查询</span>
	 */
	public final boolean isSupportJoin() {
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
	 * @param columnDefine <span class="en-US">Column configure information</span>
	 *                     <span class="zh-CN">数据列配置信息</span>
	 * @return <span class="en-US">The type definition of the data column. If it is not defined, an empty string of zero length is returned.</span>
	 * <span class="zh-CN">数据列的类型定义，如果未定义则返回长度为零的空字符串</span>
	 */
	public final String columnType(@Nonnull final ColumnDefine columnDefine) {
		String columnType = this.dataTypes.getOrDefault(columnDefine.getJdbcType(), Globals.DEFAULT_VALUE_STRING);
		if (StringUtils.notBlank(columnType)) {
			columnType = StringUtils.replace(columnType, "{length}", Integer.toString(columnDefine.getLength()));
			columnType = StringUtils.replace(columnType, "{precision}", Integer.toString(columnDefine.getPrecision()));
			columnType = StringUtils.replace(columnType, "{scale}", Integer.toString(columnDefine.getScale()));
		}
		return columnType;
	}
}
