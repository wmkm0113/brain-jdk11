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

package org.nervousync.brain.dialects.jdbc;

import jakarta.annotation.Nonnull;
import org.jetbrains.annotations.NotNull;
import org.nervousync.brain.commons.BrainCommons;
import org.nervousync.brain.defines.ColumnDefine;
import org.nervousync.brain.defines.GeneratorDefine;
import org.nervousync.brain.defines.IndexDefine;
import org.nervousync.brain.defines.TableDefine;
import org.nervousync.brain.dialects.core.BaseDialect;
import org.nervousync.brain.enumerations.ddl.DropOption;
import org.nervousync.brain.enumerations.ddl.GenerationType;
import org.nervousync.brain.enumerations.query.LockOption;
import org.nervousync.brain.exceptions.dialects.DialectException;
import org.nervousync.brain.query.QueryInfo;
import org.nervousync.brain.query.condition.Condition;
import org.nervousync.brain.query.condition.impl.ColumnCondition;
import org.nervousync.brain.query.condition.impl.GroupCondition;
import org.nervousync.brain.query.core.AbstractItem;
import org.nervousync.brain.query.core.SortedItem;
import org.nervousync.brain.query.data.ArrayData;
import org.nervousync.brain.query.data.QueryData;
import org.nervousync.brain.query.data.RangesData;
import org.nervousync.brain.query.filter.GroupBy;
import org.nervousync.brain.query.filter.OrderBy;
import org.nervousync.brain.query.item.ColumnItem;
import org.nervousync.brain.query.item.FunctionItem;
import org.nervousync.brain.query.item.QueryItem;
import org.nervousync.brain.query.join.JoinInfo;
import org.nervousync.brain.query.join.QueryJoin;
import org.nervousync.brain.query.param.AbstractParameter;
import org.nervousync.brain.query.param.impl.*;
import org.nervousync.commons.Globals;
import org.nervousync.enumerations.core.ConnectionCode;
import org.nervousync.utils.IDUtils;
import org.nervousync.utils.ObjectUtils;
import org.nervousync.utils.StringUtils;

import java.io.Serializable;
import java.sql.*;
import java.util.*;

/**
 * <h2 class="en-US">JDBC database dialect abstract class</h2>
 * <h2 class="zh-CN">JDBC数据库方言抽象类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Feb 18, 2019 10:38:52 $
 */
public abstract class JdbcDialect extends BaseDialect {

	/**
	 * <span class="en-US">Create database command</span>
	 * <span class="zh-CN">创建数据库命令</span>
	 */
	private static final String CREATE_DATABASE = "CREATE DATABASE ";
	/**
	 * <span class="en-US">Create table command</span>
	 * <span class="zh-CN">创建数据表命令</span>
	 */
	private static final String CREATE_TABLE = "CREATE TABLE ";
	/**
	 * <span class="en-US">Create view command</span>
	 * <span class="zh-CN">创建视图命令</span>
	 */
	private static final String CREATE_VIEW = "CREATE OR REPLACE VIEW ";
	/**
	 * <span class="en-US">Constraint command</span>
	 * <span class="zh-CN">约束命令</span>
	 */
	private static final String CREATE_CONSTRAINT = "CONSTRAINT ";
	/**
	 * <span class="en-US">Primary key command</span>
	 * <span class="zh-CN">主键命令</span>
	 */
	private static final String CREATE_PRIMARY_KEY = " PRIMARY KEY ";
	/**
	 * <span class="en-US">Prefix string of primary key name</span>
	 * <span class="zh-CN">主键名称前缀</span>
	 */
	private static final String PRIMARY_KEY_PREFIX = "PK_";
	/**
	 * <span class="en-US">SQL command ON</span>
	 * <span class="zh-CN">SQL命令ON</span>
	 */
	private static final String DEFAULT_COMMAND_ON = " ON ";

	/**
	 * <span class="en-US">Drop table command</span>
	 * <span class="zh-CN">删除数据表命令</span>
	 */
	private static final String DROP_TABLE = "DROP TABLE ";

	/**
	 * <span class="en-US">Drop index command</span>
	 * <span class="zh-CN">删除索引命令</span>
	 */
	private static final String DROP_INDEX = "DROP INDEX ";
	/**
	 * <span class="en-US">Restrict drop command</span>
	 * <span class="zh-CN">有限制条件删除命令</span>
	 */
	private static final String DROP_RESTRICT = " RESTRICT";
	/**
	 * <span class="en-US">Cascade drop command</span>
	 * <span class="zh-CN">无条件删除命令</span>
	 */
	private static final String DROP_CASCADE = " CASCADE";

	/**
	 * <span class="en-US">Alter table command</span>
	 * <span class="zh-CN">修改数据表结构命令</span>
	 */
	protected static final String ALTER_TABLE = "ALTER TABLE ";
	/**
	 * <span class="en-US">Column default value define command</span>
	 * <span class="zh-CN">数据列默认值定义命令</span>
	 */
	private static final String COLUMN_DEFAULT_VALUE = " DEFAULT ";
	/**
	 * <span class="en-US">Column not null command</span>
	 * <span class="zh-CN">数据列不允许为空命令</span>
	 */
	private static final String COLUMN_NOT_NULL = " NOT NULL ";
	/**
	 * <span class="en-US">Column unique command</span>
	 * <span class="zh-CN">数据列唯一约束命令</span>
	 */
	private static final String COLUMN_UNIQUE = " UNIQUE ";
	/**
	 * <span class="en-US">Column rename command</span>
	 * <span class="zh-CN">数据列重命名命令</span>
	 */
	private static final String COLUMN_RENAME = " RENAME COLUMN ";
	/**
	 * <span class="en-US">TO command</span>
	 * <span class="zh-CN">TO命令</span>
	 */
	private static final String TO_COMMAND = " TO ";
	/**
	 * <span class="en-US">Create index command</span>
	 * <span class="zh-CN">创建索引命令</span>
	 */
	private static final String CREATE_INDEX = "CREATE INDEX ";
	/**
	 * <span class="en-US">Create unique index command</span>
	 * <span class="zh-CN">创建唯一索引命令</span>
	 */
	private static final String CREATE_UNIQUE_INDEX = "CREATE UNIQUE INDEX ";
	/**
	 * <span class="en-US">Query data command</span>
	 * <span class="zh-CN">数据查询命令</span>
	 */
	private static final String SELECT_COMMAND = "SELECT ";
	/**
	 * <span class="en-US">Query from command</span>
	 * <span class="zh-CN">数据来自命令</span>
	 */
	private static final String FROM_COMMAND = " FROM ";
	/**
	 * <span class="en-US">WHERE command of SQL query</span>
	 * <span class="zh-CN">SQL查询的WHERE命令</span>
	 */
	protected static final String WHERE_COMMAND = " WHERE ";
	/**
	 * <span class="en-US">Order by command of SQL query</span>
	 * <span class="zh-CN">SQL查询的Order by命令</span>
	 */
	private static final String ORDER_BY_COMMAND = " ORDER BY ";
	/**
	 * <span class="en-US">Group by command of SQL query</span>
	 * <span class="zh-CN">SQL查询的Group by命令</span>
	 */
	private static final String GROUP_BY_COMMAND = " GROUP BY ";
	/**
	 * <span class="en-US">Having command of SQL query</span>
	 * <span class="zh-CN">SQL查询的HAVING命令</span>
	 */
	private static final String HAVING_COMMAND = " HAVING ";
	/**
	 * <span class="en-US">Insert record command</span>
	 * <span class="zh-CN">插入记录命令</span>
	 */
	private static final String COMMAND_INSERT = "INSERT INTO ";
	/**
	 * <span class="en-US">Insert record command</span>
	 * <span class="zh-CN">插入记录命令</span>
	 */
	private static final String COMMAND_VALUES = " VALUES ";
	/**
	 * <span class="en-US">Update record command</span>
	 * <span class="zh-CN">更新记录命令</span>
	 */
	private static final String COMMAND_UPDATE = "UPDATE ";
	/**
	 * <span class="en-US">Set column data command</span>
	 * <span class="zh-CN">设置记录值命令</span>
	 */
	private static final String COMMAND_SET = " SET ";
	/**
	 * <span class="en-US">Delete record command</span>
	 * <span class="zh-CN">删除记录命令</span>
	 */
	private static final String COMMAND_DELETE = "DELETE FROM ";
	/**
	 * <span class="en-US">Full join</span>
	 * <span class="zh-CN">完全连接</span>
	 */
	private static final String COMMAND_JOIN_FULL = " FULL JOIN ";
	/**
	 * <span class="en-US">Inner join</span>
	 * <span class="zh-CN">内连接</span>
	 */
	private static final String COMMAND_JOIN_INNER = " INNER JOIN ";
	/**
	 * <span class="en-US">Cross join</span>
	 * <span class="zh-CN">交叉连接</span>
	 */
	private static final String COMMAND_JOIN_CROSS = " CROSS JOIN ";
	/**
	 * <span class="en-US">Left join</span>
	 * <span class="zh-CN">左连接</span>
	 */
	private static final String COMMAND_JOIN_LEFT = " LEFT JOIN ";
	/**
	 * <span class="en-US">Right join</span>
	 * <span class="zh-CN">右连接</span>
	 */
	private static final String COMMAND_JOIN_RIGHT = " RIGHT JOIN ";

	protected JdbcDialect() throws DialectException {
		super();
	}

	/**
	 * <h3 class="en-US">Commands to modify data types</h3>
	 * <h3 class="zh-CN">修改数据类型的命令</h3>
	 *
	 * @return <span class="en-US">Command string</span>
	 * <span class="zh-CN">命令字符串</span>
	 */
	public String alterType() {
		return Globals.DEFAULT_VALUE_STRING;
	}

	/**
	 * <h3 class="en-US">Commands to add column</h3>
	 * <h3 class="zh-CN">添加数据列的命令</h3>
	 *
	 * @return <span class="en-US">Command string</span>
	 * <span class="zh-CN">命令字符串</span>
	 */
	public String addColumn() {
		return " ADD ";
	}

	/**
	 * <h3 class="en-US">Commands to modify column</h3>
	 * <h3 class="zh-CN">修改数据列的命令</h3>
	 *
	 * @return <span class="en-US">Command string</span>
	 * <span class="zh-CN">命令字符串</span>
	 */
	public String alterColumn() {
		return " ALTER COLUMN ";
	}

	/**
	 * <h3 class="en-US">Commands to drop column</h3>
	 * <h3 class="zh-CN">删除数据列的命令</h3>
	 *
	 * @return <span class="en-US">Command string</span>
	 * <span class="zh-CN">命令字符串</span>
	 */
	public String dropColumn() {
		return " DROP COLUMN ";
	}

	/**
	 * <h3 class="en-US">Commands to rename column</h3>
	 * <h3 class="zh-CN">重命名数据列的命令</h3>
	 *
	 * @param tableName <span class="en-US">Database table name</span>
	 *                  <span class="zh-CN">数据表名</span>
	 * @param oldName   <span class="en-US">Old column name</span>
	 *                  <span class="zh-CN">旧列名</span>
	 * @param newName   <span class="en-US">New column name</span>
	 *                  <span class="zh-CN">新列名</span>
	 * @return <span class="en-US">Command string</span>
	 * <span class="zh-CN">命令字符串</span>
	 */
	public String renameColumn(final String tableName, String oldName, String newName) {
		StringBuilder stringBuilder = new StringBuilder();
		if (StringUtils.notBlank(tableName) && StringUtils.notBlank(oldName) && StringUtils.notBlank(newName)) {
			stringBuilder.append(ALTER_TABLE)
					.append(this.nameCase(tableName))
					.append(COLUMN_RENAME)
					.append(this.nameCase(oldName))
					.append(TO_COMMAND)
					.append(this.nameCase(newName));
		}
		return stringBuilder.toString();
	}

	/**
	 * <h3 class="en-US">Commands to set column default value</h3>
	 * <h3 class="zh-CN">设置数据列默认值的命令</h3>
	 *
	 * @param defaultValue <span class="en-US">Default value</span>
	 *                     <span class="zh-CN">默认值</span>
	 * @return <span class="en-US">Command string</span>
	 * <span class="zh-CN">命令字符串</span>
	 */
	public String columnSetDefault(String defaultValue) {
		if (StringUtils.isEmpty(defaultValue)) {
			return Globals.DEFAULT_VALUE_STRING;
		}
		return " DEFAULT " + defaultValue;
	}

	/**
	 * <h3 class="en-US">Commands to remove column default value</h3>
	 * <h3 class="zh-CN">移除数据列默认值的命令</h3>
	 *
	 * @return <span class="en-US">Command string</span>
	 * <span class="zh-CN">命令字符串</span>
	 */
	public String columnRemoveDefault() {
		return " DROP DEFAULT ";
	}


	/**
	 * <h3 class="en-US">Read data of type Blob</h3>
	 * <h3 class="zh-CN">读取Blob类型的数据</h3>
	 *
	 * @param resultSet   <span class="en-US">Query results to parse</span>
	 *                    <span class="zh-CN">要解析的查询结果</span>
	 * @param columnIndex <span class="en-US">Data column index value</span>
	 *                    <span class="zh-CN">数据列索引值</span>
	 * @return <span class="en-US">Read binary data</span>
	 * <span class="zh-CN">读取的二进制数据</span>
	 */
	public byte[] readBlob(final ResultSet resultSet, final int columnIndex) {
		try {
			Blob blob = resultSet.getBlob(columnIndex);
			if (blob != null) {
				byte[] buffer = new byte[(int) blob.length()];
				int readLength = blob.getBinaryStream().read(buffer, 0, buffer.length);
				if (readLength == buffer.length) {
					return buffer;
				}
			}
		} catch (Exception e) {
			this.logger.warn("Read BLOB data error! ");
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Stack message: ", e);
			}
		}
		return new byte[0];
	}

	/**
	 * <h3 class="en-US">Read data of type Clob</h3>
	 * <h3 class="zh-CN">读取Clob类型的数据</h3>
	 *
	 * @param resultSet   <span class="en-US">Query results to parse</span>
	 *                    <span class="zh-CN">要解析的查询结果</span>
	 * @param columnIndex <span class="en-US">Data column index value</span>
	 *                    <span class="zh-CN">数据列索引值</span>
	 * @return <span class="en-US">Read character array</span>
	 * <span class="zh-CN">读取的字节数据</span>
	 */
	public char[] readClob(ResultSet resultSet, int columnIndex) {
		try {
			Clob clob = resultSet.getClob(columnIndex);
			if (clob != null) {
				char[] buffer = new char[(int) clob.length()];
				int readLength = clob.getCharacterStream().read(buffer, 0, buffer.length);
				if (readLength == buffer.length) {
					return buffer;
				}
			}
		} catch (Exception ignore) {
		}
		return new char[0];
	}

	/**
	 * <h3 class="en-US">Create sharded database command</h3>
	 * <h3 class="zh-CN">创建分片数据库命令</h3>
	 *
	 * @param shardingName <span class="en-US">Sharding database name</span>
	 *                     <span class="zh-CN">分片数据库名</span>
	 * @return <span class="en-US">Generated SQL command</span>
	 * <span class="zh-CN">生成的SQL命令</span>
	 */
	public String createDatabase(final String shardingName) {
		return CREATE_DATABASE + shardingName + this.databaseCommand();
	}

	/**
	 * <h3 class="en-US">Create database parameters command</h3>
	 * <h3 class="zh-CN">创建数据库的参数命令</h3>
	 *
	 * @return <span class="en-US">Generated SQL command</span>
	 * <span class="zh-CN">生成的SQL命令</span>
	 */
	protected abstract String databaseCommand();

	/**
	 * <h3 class="en-US">Create views of sharded data tables for data query</h3>
	 * <h3 class="zh-CN">创建分片数据表的视图，用于数据查询</h3>
	 *
	 * @param tableDefine    <span class="en-US">Database table define information</span>
	 *                       <span class="zh-CN">数据表配置信息</span>
	 * @param shardingTables <span class="en-US">Sharding data table name list</span>
	 *                       <span class="zh-CN">分片数据表名列表</span>
	 * @return <span class="en-US">Generated SQL command</span>
	 * <span class="zh-CN">生成的SQL命令</span>
	 */
	public final String createShardingView(@Nonnull TableDefine tableDefine, final List<String> shardingTables) {
		if (shardingTables.isEmpty()) {
			return Globals.DEFAULT_VALUE_STRING;
		}
		String tableName = tableDefine.getTableName();
		StringBuilder stringBuilder =
				new StringBuilder(CREATE_VIEW).append(BrainCommons.WHITE_SPACE)
						.append("[").append(this.nameCase(tableName)).append("]")
						.append(BrainCommons.WHITE_SPACE)
						.append(" AS ");
		String joinCharacter = Globals.DEFAULT_VALUE_STRING;
		for (String shardingTable : shardingTables) {
			stringBuilder.append(joinCharacter)
					.append(SELECT_COMMAND).append(" * ").append(FROM_COMMAND)
					.append(this.nameCase(shardingTable));
			if (StringUtils.isEmpty(joinCharacter)) {
				joinCharacter = " UNION ALL ";
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * <h3 class="en-US">Generate SQL commands to create data tables</h3>
	 * <h3 class="zh-CN">生成创建数据表的SQL命令</h3>
	 *
	 * @param tableDefine   <span class="en-US">Database table define information</span>
	 *                      <span class="zh-CN">数据表配置信息</span>
	 * @param shardingTable <span class="en-US">Database table sharding value</span>
	 *                      <span class="zh-CN">数据表分片值</span>
	 * @return <span class="en-US">Generated SQL command</span>
	 * <span class="zh-CN">生成的SQL命令</span>
	 * @throws SQLException <span class="en-US">An error occurred while generating the SQL command</span>
	 *                      <span class="zh-CN">生成的SQL命令时出现错误</span>
	 */
	public final String createTableCommand(@Nonnull final TableDefine tableDefine, final String shardingTable)
			throws SQLException {
		String tableName = StringUtils.isEmpty(shardingTable) ? tableDefine.getTableName() : shardingTable;
		StringBuilder sqlBuilder = new StringBuilder(CREATE_TABLE)
				.append(this.nameCase(tableName))
				.append(BrainCommons.BRACKETS_BEGIN);

		StringBuilder columnBuilder = new StringBuilder();
		StringBuilder primaryKeyBuilder = new StringBuilder();
		for (ColumnDefine columnDefine : tableDefine.getColumnDefines()) {
			Optional.of(this.columnCommand(columnDefine, Boolean.FALSE))
					.filter(StringUtils::notBlank)
					.ifPresent(columnCmd -> {
						columnBuilder.append(BrainCommons.DEFAULT_SPLIT_CHARACTER).append(columnCmd);
						if (columnDefine.isPrimaryKey()) {
							primaryKeyBuilder.append(BrainCommons.DEFAULT_SPLIT_CHARACTER)
									.append(this.nameCase(columnDefine.getColumnName()));
						}
					});
		}
		if (columnBuilder.length() > 0) {
			sqlBuilder.append(columnBuilder.substring(BrainCommons.DEFAULT_SPLIT_CHARACTER.length()));
		}
		if (primaryKeyBuilder.length() > 0) {
			sqlBuilder.append(BrainCommons.DEFAULT_SPLIT_CHARACTER)
					.append(CREATE_CONSTRAINT)
					.append(PRIMARY_KEY_PREFIX)
					.append(this.nameCase(tableName))
					.append(CREATE_PRIMARY_KEY)
					.append(BrainCommons.BRACKETS_BEGIN)
					.append(primaryKeyBuilder.substring(BrainCommons.DEFAULT_SPLIT_CHARACTER.length()))
					.append(BrainCommons.BRACKETS_END);
		}
		sqlBuilder.append(BrainCommons.BRACKETS_END);
		return sqlBuilder.toString();
	}

	/**
	 * <h3 class="en-US">Generate SQL commands to create index</h3>
	 * <h3 class="zh-CN">生成创建索引的SQL命令</h3>
	 *
	 * @param tableDefine   <span class="en-US">Database table define information</span>
	 *                      <span class="zh-CN">数据表配置信息</span>
	 * @param shardingTable <span class="en-US">Database table sharding value</span>
	 *                      <span class="zh-CN">数据表分片值</span>
	 * @return <span class="en-US">Generated SQL command</span>
	 * <span class="zh-CN">生成的SQL命令</span>
	 */
	public final List<String> createIndexCommand(@Nonnull final TableDefine tableDefine, final String shardingTable) {
		String tableName = StringUtils.isEmpty(shardingTable) ? tableDefine.getTableName() : shardingTable;
		List<String> sqlCmdList = new ArrayList<>();
		StringBuilder stringBuilder;
		for (IndexDefine indexDefine : tableDefine.getIndexDefines()) {
			if (indexDefine.getColumnList().isEmpty()) {
				continue;
			}
			if (indexDefine.isUnique()) {
				stringBuilder = new StringBuilder(CREATE_UNIQUE_INDEX);
			} else {
				stringBuilder = new StringBuilder(CREATE_INDEX);
			}
			stringBuilder.append(this.nameCase(indexDefine.getIndexName()))
					.append(DEFAULT_COMMAND_ON)
					.append(this.nameCase(tableName));

			StringBuilder columnBuilder = new StringBuilder();
			for (String columnName : indexDefine.getColumnList()) {
				columnBuilder.append(BrainCommons.DEFAULT_SPLIT_CHARACTER).append(this.nameCase(columnName));
			}
			stringBuilder.append(BrainCommons.BRACKETS_BEGIN)
					.append(columnBuilder.substring(BrainCommons.DEFAULT_SPLIT_CHARACTER.length()))
					.append(BrainCommons.BRACKETS_END);
			if (stringBuilder.length() > 0) {
				sqlCmdList.add(stringBuilder.toString());
			}
		}
		return sqlCmdList;
	}

	/**
	 * <h3 class="en-US">Generate SQL commands to alter data tables</h3>
	 * <h3 class="zh-CN">生成修改数据表的SQL命令</h3>
	 *
	 * @param tableDefine  <span class="en-US">Database table define information</span>
	 *                     <span class="zh-CN">数据表配置信息</span>
	 * @param shardingName <span class="en-US">Sharding data table name</span>
	 *                     <span class="zh-CN">分片数据表名</span>
	 * @param existColumns <span class="en-US">List of currently existing data column information</span>
	 *                     <span class="zh-CN">当前存在的数据列信息列表</span>
	 * @return <span class="en-US">Generated SQL command</span>
	 * <span class="zh-CN">生成的SQL命令</span>
	 * @throws SQLException <span class="en-US">An error occurred while generating the SQL command</span>
	 *                      <span class="zh-CN">生成的SQL命令时出现错误</span>
	 */
	public final List<String> alterTableCommand(@Nonnull final TableDefine tableDefine, final String shardingName,
	                                            @Nonnull final List<ColumnDefine> existColumns) throws SQLException {
		if (StringUtils.isEmpty(this.alterColumn())) {
			return Collections.emptyList();
		}
		String tableName = this.nameCase(StringUtils.isEmpty(shardingName) ? tableDefine.getTableName() : shardingName);
		List<ColumnDefine> matchedColumns = new ArrayList<>();
		List<String> sqlCmdList = new ArrayList<>();
		for (ColumnDefine columnDefine : tableDefine.getColumnDefines()) {
			ColumnDefine existColumn =
					existColumns.stream()
							.filter(columnInfo ->
									ObjectUtils.nullSafeEquals(this.nameCase(columnDefine.getColumnName()),
											this.nameCase(columnInfo.getColumnName())))
							.findFirst()
							.orElse(null);
			if (existColumn == null) {
				//  Find column name histories
				List<String> nameHistories = new ArrayList<>();
				columnDefine.getNameHistories().forEach(nameHistory -> nameHistories.add(this.nameCase(nameHistory)));
				if (!nameHistories.isEmpty()) {
					existColumn = existColumns.stream()
							.filter(columnInfo -> nameHistories.contains(this.nameCase(columnInfo.getColumnName())))
							.findFirst()
							.map(columnInfo -> {
								String renameCommand =
										this.renameColumn(tableName, columnInfo.getColumnName(), columnDefine.getColumnName());
								if (StringUtils.isEmpty(renameCommand)) {
									String sqlBuilder = ALTER_TABLE + tableName + COLUMN_RENAME
											+ columnInfo.getColumnName() + TO_COMMAND + columnDefine.getColumnName();
									sqlCmdList.add(sqlBuilder);
								} else {
									sqlCmdList.add(renameCommand);
								}
								return columnInfo;
							})
							.orElse(null);
				}
			}
			if (existColumn == null) {
				Optional.of(this.columnCommand(columnDefine, Boolean.TRUE))
						.filter(StringUtils::notBlank)
						.ifPresent(columnCmd -> sqlCmdList.add(ALTER_TABLE + tableName + this.addColumn() + columnCmd));
			} else {
				if (existColumn.modified(columnDefine)) {
					if (existColumn.getJdbcType() != columnDefine.getJdbcType()
							|| existColumn.getLength() != columnDefine.getLength()
							|| existColumn.getPrecision() != columnDefine.getPrecision()
							|| existColumn.getScale() != columnDefine.getScale()) {
						String columnType = this.columnType(columnDefine);
						if (StringUtils.isEmpty(columnType)) {
							throw new SQLException("Unknown column type! Column name: " + columnDefine.getColumnName()
									+ " JDBC type: " + columnDefine.getJdbcType());
						}
						String sqlCmd = ALTER_TABLE + tableName + this.alterColumn()
								+ this.nameCase(columnDefine.getColumnName()) + BrainCommons.WHITE_SPACE
								+ this.alterType() + BrainCommons.WHITE_SPACE + columnType;
						sqlCmdList.add(sqlCmd);
					}

					if ((StringUtils.isEmpty(existColumn.getDefaultValue()) && StringUtils.notBlank(columnDefine.getDefaultValue()))
							|| !ObjectUtils.nullSafeEquals(existColumn.getDefaultValue(), columnDefine.getDefaultValue())) {
						String sqlCmd = ALTER_TABLE + tableName + this.alterColumn()
								+ this.nameCase(columnDefine.getColumnName()) + BrainCommons.WHITE_SPACE
								+ this.columnSetDefault(columnDefine.getDefaultValue());
						sqlCmdList.add(sqlCmd);
					}

					if (StringUtils.notBlank(existColumn.getDefaultValue())
							&& StringUtils.isEmpty(columnDefine.getDefaultValue())) {
						String sqlCmd = ALTER_TABLE + tableName + this.alterColumn()
								+ this.nameCase(columnDefine.getColumnName()) + BrainCommons.WHITE_SPACE
								+ this.columnRemoveDefault();
						sqlCmdList.add(sqlCmd);
					}
				}
				matchedColumns.add(existColumn);
			}
		}

		existColumns.stream()
				.filter(columnInfo -> !matchedColumns.contains(columnInfo))
				.forEach(columnInfo ->
						sqlCmdList.add(ALTER_TABLE + tableName + this.dropColumn()
								+ this.nameCase(columnInfo.getColumnName())));
		return sqlCmdList;
	}

	/**
	 * <h3 class="en-US">Generate SQL commands to truncate data tables</h3>
	 * <h3 class="zh-CN">生成清空数据表的SQL命令</h3>
	 *
	 * @param tableName <span class="en-US">Database table name</span>
	 *                  <span class="zh-CN">数据表名</span>
	 * @return <span class="en-US">Generated SQL command</span>
	 * <span class="zh-CN">生成的SQL命令</span>
	 */
	public String truncateTable(@NotNull String tableName) {
		if (StringUtils.isEmpty(tableName)) {
			return Globals.DEFAULT_VALUE_STRING;
		}
		return "TRUNCATE TABLE " + tableName;
	}

	/**
	 * <h3 class="en-US">Generate SQL commands to drop data tables</h3>
	 * <h3 class="zh-CN">生成删除数据表的SQL命令</h3>
	 *
	 * @param tableName  <span class="en-US">Database table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param dropOption <span class="en-US">Cascading delete options</span>
	 *                   <span class="zh-CN">级联删除选项</span>
	 * @return <span class="en-US">Generated SQL command</span>
	 * <span class="zh-CN">生成的SQL命令</span>
	 */
	public final String dropTableCommand(@Nonnull final String tableName, @Nonnull final DropOption dropOption) {
		if (StringUtils.isEmpty(tableName)) {
			return Globals.DEFAULT_VALUE_STRING;
		}
		StringBuilder sqlBuilder = new StringBuilder(DROP_TABLE).append(this.nameCase(tableName));
		switch (dropOption) {
			case CASCADE:
				sqlBuilder.append(DROP_CASCADE);
				break;
			case RESTRICT:
				sqlBuilder.append(DROP_RESTRICT);
				break;
		}
		return sqlBuilder.toString();
	}

	/**
	 * <h3 class="en-US">Generate SQL commands to drop index</h3>
	 * <h3 class="zh-CN">生成删除索引的SQL命令</h3>
	 *
	 * @param indexName <span class="en-US">Index name</span>
	 *                  <span class="zh-CN">索引名</span>
	 * @param tableName <span class="en-US">Database table name</span>
	 *                  <span class="zh-CN">数据表名</span>
	 * @return <span class="en-US">Generated SQL command</span>
	 * <span class="zh-CN">生成的SQL命令</span>
	 */
	public final String dropIndexCommand(final String indexName, final String tableName) {
		return DROP_INDEX + this.nameCase(indexName) + DEFAULT_COMMAND_ON + this.nameCase(tableName);
	}

	/**
	 * <h3 class="en-US">SQL command to get sequence generator values</h3>
	 * <h3 class="zh-CN">获取序列生成器值的SQL命令</h3>
	 *
	 * @param sequenceName <span class="en-US">Sequence generator name</span>
	 *                     <span class="zh-CN">序列生成器名称</span>
	 * @return <span class="en-US">Command string</span>
	 * <span class="zh-CN">命令字符串</span>
	 */
	protected String nextVal(final String sequenceName) {
		if (StringUtils.isEmpty(sequenceName)) {
			return Globals.DEFAULT_VALUE_STRING;
		}
		return " NEXT VALUE FOR " + this.nameCase(sequenceName);
	}

	/**
	 * <h3 class="en-US">Generate SQL commands to lock record</h3>
	 * <h3 class="zh-CN">生成数据锁定的SQL命令</h3>
	 *
	 * @param whereClause <span class="en-US">Generated where sentences</span>
	 *                    <span class="zh-CN">生成的Where字句</span>
	 * @param lockOption  <span class="en-US">Query record lock option</span>
	 *                    <span class="zh-CN">查询记录锁定选项</span>
	 * @return <span class="en-US">Generated SQL command</span>
	 * <span class="zh-CN">生成的SQL命令</span>
	 */
	protected String lockWhereClause(final String whereClause, final LockOption lockOption) {
		if (StringUtils.isEmpty(whereClause)) {
			return Globals.DEFAULT_VALUE_STRING;
		}
		switch (lockOption) {
			case PESSIMISTIC_UPGRADE:
				return WHERE_COMMAND + whereClause + "FOR UPDATE ";
			case PESSIMISTIC_UPGRADE_NOWAIT:
				return WHERE_COMMAND + whereClause + "FOR UPDATE NOWAIT ";
			default:
				return WHERE_COMMAND + whereClause;
		}
	}

	protected String aliasCommand() {
		return " AS ";
	}

	/**
	 * <h3 class="en-US">SQL command to get current date</h3>
	 * <h3 class="zh-CN">获取当前日期的SQL命令</h3>
	 *
	 * @return <span class="en-US">Command string</span>
	 * <span class="zh-CN">命令字符串</span>
	 */
	public abstract String currentDate();

	/**
	 * <h3 class="en-US">SQL command to get current time</h3>
	 * <h3 class="zh-CN">获取当前时间的SQL命令</h3>
	 *
	 * @return <span class="en-US">Command string</span>
	 * <span class="zh-CN">命令字符串</span>
	 */
	public abstract String currentTime();

	/**
	 * <h3 class="en-US">SQL command to get current timestamp</h3>
	 * <h3 class="zh-CN">获取当前时间戳的SQL命令</h3>
	 *
	 * @return <span class="en-US">Command string</span>
	 * <span class="zh-CN">命令字符串</span>
	 */
	public abstract String currentTimestamp();

	/**
	 * <h3 class="en-US">Generate SQL commands to insert record</h3>
	 * <h3 class="zh-CN">生成插入记录的SQL命令</h3>
	 *
	 * @param tableDefine <span class="en-US">Table define information</span>
	 *                    <span class="zh-CN">数据表定义信息</span>
	 * @param tableName   <span class="en-US">Query table name</span>
	 *                    <span class="zh-CN">查询数据表名</span>
	 * @param dataMap     <span class="en-US">Insert data mapping</span>
	 *                    <span class="zh-CN">写入数据映射表</span>
	 * @return <span class="en-US">Generated SQL command</span>
	 * <span class="zh-CN">生成的SQL命令</span>
	 * @throws SQLException <span class="en-US">An error occurred while generating the SQL command</span>
	 *                      <span class="zh-CN">生成的SQL命令时出现错误</span>
	 */
	public final SQLCommand insertCommand(@Nonnull final TableDefine tableDefine, final String tableName,
	                                      @Nonnull final Map<String, Serializable> dataMap) throws SQLException {
		if (dataMap.isEmpty()) {
			throw new SQLException("Insert parameter map is empty!");
		}
		StringBuilder columnBuilder = new StringBuilder();
		StringBuilder valueBuilder = new StringBuilder();
		List<Object> values = new ArrayList<>();
		for (Map.Entry<String, Serializable> entry : dataMap.entrySet()) {
			String columnName = entry.getKey();
			GeneratorDefine generatorDefine = tableDefine.generatorDefine(columnName);
			if (generatorDefine == null) {
				columnBuilder.append(BrainCommons.DEFAULT_SPLIT_CHARACTER).append(this.nameCase(columnName));
				valueBuilder.append(BrainCommons.DEFAULT_SPLIT_CHARACTER)
						.append(BrainCommons.DEFAULT_PLACE_HOLDER);
				values.add(entry.getValue());
			} else {
				switch (generatorDefine.getGenerationType()) {
					case ASSIGNED:
						valueBuilder.append(BrainCommons.DEFAULT_SPLIT_CHARACTER)
								.append(BrainCommons.DEFAULT_PLACE_HOLDER);
						values.add(entry.getValue());
						break;
					case GENERATE:
						valueBuilder.append(BrainCommons.DEFAULT_SPLIT_CHARACTER)
								.append(BrainCommons.DEFAULT_PLACE_HOLDER);
						values.add(IDUtils.generate(generatorDefine.getGeneratorName(), new byte[0]));
						break;
					case SEQUENCE:
						valueBuilder.append(BrainCommons.DEFAULT_SPLIT_CHARACTER)
								.append(this.nextVal(generatorDefine.getGeneratorName()));
						break;
					case CURRENT_DATE:
						valueBuilder.append(BrainCommons.DEFAULT_SPLIT_CHARACTER).append(this.currentDate());
						break;
					case CURRENT_TIME:
						valueBuilder.append(BrainCommons.DEFAULT_SPLIT_CHARACTER).append(this.currentTime());
						break;
					case CURRENT_TIMESTAMP:
						valueBuilder.append(BrainCommons.DEFAULT_SPLIT_CHARACTER).append(this.currentTimestamp());
						break;
				}
				if (!GenerationType.AUTO_INCREMENT.equals(generatorDefine.getGenerationType())) {
					columnBuilder.append(BrainCommons.DEFAULT_SPLIT_CHARACTER).append(this.nameCase(columnName));
				}
			}
		}
		StringBuilder sqlBuilder = new StringBuilder(COMMAND_INSERT).append(this.nameCase(tableName));
		if (columnBuilder.length() == 0) {
			throw new SQLException("Insert columns cannot be empty!");
		}
		sqlBuilder.append(columnBuilder).append(COMMAND_VALUES).append(valueBuilder);
		return new SQLCommand(sqlBuilder.toString(), values);
	}

	/**
	 * <h3 class="en-US">Generate SQL commands to update record</h3>
	 * <h3 class="zh-CN">生成更新记录的SQL命令</h3>
	 *
	 * @param tableDefine <span class="en-US">Table define information</span>
	 *                    <span class="zh-CN">数据表定义信息</span>
	 * @param tableName   <span class="en-US">Query table name</span>
	 *                    <span class="zh-CN">查询数据表名</span>
	 * @param dataMap     <span class="en-US">Insert data mapping</span>
	 *                    <span class="zh-CN">写入数据映射表</span>
	 * @param filterMap   <span class="en-US">Retrieve filter mapping</span>
	 *                    <span class="zh-CN">查询条件映射表</span>
	 * @return <span class="en-US">Generated SQL command</span>
	 * <span class="zh-CN">生成的SQL命令</span>
	 * @throws SQLException <span class="en-US">An error occurred while generating the SQL command</span>
	 *                      <span class="zh-CN">生成的SQL命令时出现错误</span>
	 */
	public final SQLCommand updateCommand(@Nonnull final TableDefine tableDefine, final String tableName,
	                                      @Nonnull final Map<String, Serializable> dataMap,
	                                      @Nonnull final Map<String, Serializable> filterMap) throws SQLException {
		if (dataMap.isEmpty()) {
			throw new SQLException("Update parameter map is empty!");
		}
		StringBuilder columnBuilder = new StringBuilder();
		List<Object> values = new ArrayList<>();
		for (Map.Entry<String, Serializable> entry : dataMap.entrySet()) {
			String columnName = entry.getKey();
			columnBuilder.append(BrainCommons.DEFAULT_SPLIT_CHARACTER)
					.append(this.nameCase(columnName))
					.append(BrainCommons.OPERATOR_EQUAL);

			GeneratorDefine generatorDefine = tableDefine.generatorDefine(columnName);
			if (generatorDefine == null) {
				columnBuilder.append(BrainCommons.DEFAULT_PLACE_HOLDER);
				values.add(entry.getValue());
			} else {
				switch (generatorDefine.getGenerationType()) {
					case GENERATE:
						columnBuilder.append(BrainCommons.DEFAULT_PLACE_HOLDER);
						values.add(IDUtils.generate(generatorDefine.getGeneratorName(), new byte[0]));
						break;
					case CURRENT_DATE:
						columnBuilder.append(this.currentDate());
						break;
					case CURRENT_TIME:
						columnBuilder.append(this.currentTime());
						break;
					case CURRENT_TIMESTAMP:
						columnBuilder.append(this.currentTimestamp());
						break;
					default:
						columnBuilder.append(BrainCommons.DEFAULT_PLACE_HOLDER);
						values.add(entry.getValue());
						break;
				}
			}
		}

		StringBuilder sqlBuilder =
				new StringBuilder(COMMAND_UPDATE).append(this.nameCase(tableName));
		if (columnBuilder.length() == 0) {
			throw new SQLException("Update columns cannot be empty!");
		}
		sqlBuilder.append(COMMAND_SET).append(columnBuilder).append(WHERE_COMMAND)
				.append(BrainCommons.DEFAULT_WHERE_CLAUSE).append(this.whereClause(filterMap, values));
		return new SQLCommand(sqlBuilder.toString(), values);
	}

	/**
	 * <h3 class="en-US">Generate SQL commands to delete record</h3>
	 * <h3 class="zh-CN">生成删除记录的SQL命令</h3>
	 *
	 * @param tableName <span class="en-US">Query table name</span>
	 *                  <span class="zh-CN">查询数据表名</span>
	 * @param filterMap <span class="en-US">Retrieve filter mapping</span>
	 *                  <span class="zh-CN">查询条件映射表</span>
	 * @return <span class="en-US">Generated SQL command</span>
	 * <span class="zh-CN">生成的SQL命令</span>
	 * @throws SQLException <span class="en-US">An error occurred while generating the SQL command</span>
	 *                      <span class="zh-CN">生成的SQL命令时出现错误</span>
	 */
	public final SQLCommand deleteCommand(final String tableName,
	                                      @Nonnull final Map<String, Serializable> filterMap) throws SQLException {
		StringBuilder sqlBuilder = new StringBuilder(COMMAND_DELETE).append(this.nameCase(tableName));
		if (filterMap.isEmpty()) {
			throw new SQLException("Conditions cannot be empty!");
		}
		List<Object> values = new ArrayList<>();
		sqlBuilder.append(WHERE_COMMAND)
				.append(BrainCommons.DEFAULT_WHERE_CLAUSE).append(this.whereClause(filterMap, values));
		return new SQLCommand(sqlBuilder.toString(), values);
	}

	/**
	 * <h3 class="en-US">Generate SQL commands to retrieve record</h3>
	 * <h3 class="zh-CN">生成唯一检索记录的SQL命令</h3>
	 *
	 * @param tableName  <span class="en-US">Query table name</span>
	 *                   <span class="zh-CN">查询数据表名</span>
	 * @param columns    <span class="en-US">Query column names</span>
	 *                   <span class="zh-CN">查询数据列名</span>
	 * @param filterMap  <span class="en-US">Retrieve filter mapping</span>
	 *                   <span class="zh-CN">查询条件映射表</span>
	 * @param forUpdate  <span class="en-US">Retrieve result using for update record</span>
	 *                   <span class="zh-CN">检索结果用于更新记录</span>
	 * @param lockOption <span class="en-US">Query record lock option</span>
	 *                   <span class="zh-CN">查询记录锁定选项</span>
	 * @return <span class="en-US">Generated SQL command</span>
	 * <span class="zh-CN">生成的SQL命令</span>
	 */
	public final SQLCommand retrieveCommand(@Nonnull final String tableName, final String columns,
	                                        @Nonnull final Map<String, Serializable> filterMap,
	                                        final boolean forUpdate, final LockOption lockOption) {
		List<Object> values = new ArrayList<>();
		StringBuilder sqlBuilder = new StringBuilder(SELECT_COMMAND)
				.append(StringUtils.isEmpty(columns) ? " * " : columns)
				.append(FROM_COMMAND)
				.append(this.nameCase(tableName));
		String whereClause = this.whereClause(filterMap, values);
		if (forUpdate) {
			sqlBuilder.append(this.lockWhereClause(whereClause, lockOption));
		} else {
			sqlBuilder.append(WHERE_COMMAND).append(whereClause);
		}
		return new SQLCommand(sqlBuilder.toString(), values);
	}

	/**
	 * <h3 class="en-US">Generate SQL commands to query record</h3>
	 * <h3 class="zh-CN">生成数据查询记录的SQL命令</h3>
	 *
	 * @param tableName     <span class="en-US">Query table name</span>
	 *                      <span class="zh-CN">查询数据表名</span>
	 * @param columns       <span class="en-US">Query column names</span>
	 *                      <span class="zh-CN">查询数据列名</span>
	 * @param conditionList <span class="en-US">Query condition instance list</span>
	 *                      <span class="zh-CN">查询条件实例对象列表</span>
	 * @param lockOption    <span class="en-US">Query record lock option</span>
	 *                      <span class="zh-CN">查询记录锁定选项</span>
	 * @return <span class="en-US">Generated SQL command</span>
	 * <span class="zh-CN">生成的SQL命令</span>
	 * @throws SQLException <span class="en-US">An error occurred while generating the SQL command</span>
	 *                      <span class="zh-CN">生成的SQL命令时出现错误</span>
	 */
	public final SQLCommand queryCommand(@Nonnull final String tableName, final String columns,
	                                     final List<Condition> conditionList, final LockOption lockOption)
			throws SQLException {
		StringBuilder sqlBuilder =
				new StringBuilder(SELECT_COMMAND)
						.append(this.nameCase(columns))
						.append(FROM_COMMAND)
						.append(this.nameCase(tableName));
		List<Object> values = new ArrayList<>();
		sqlBuilder.append(this.lockWhereClause(this.whereClause(Map.of(), conditionList, values), lockOption));
		return new SQLCommand(sqlBuilder.toString(), values);
	}

	/**
	 * <h3 class="en-US">Generate SQL commands to query record</h3>
	 * <h3 class="zh-CN">生成数据查询记录的SQL命令</h3>
	 *
	 * @param queryInfo <span class="en-US">Query record information</span>
	 *                  <span class="zh-CN">数据检索信息</span>
	 * @return <span class="en-US">Generated SQL command</span>
	 * <span class="zh-CN">生成的SQL命令</span>
	 * @throws SQLException <span class="en-US">An error occurred while generating the SQL command</span>
	 *                      <span class="zh-CN">生成的SQL命令时出现错误</span>
	 */
	public final SQLCommand queryCommand(final QueryInfo queryInfo) throws SQLException {
		if (!queryInfo.getQueryJoins().isEmpty() && !this.isSupportJoin()) {
			throw new SQLException("Current dialect can't support query joins!");
		}
		final Map<String, String> aliasMap = new HashMap<>();
		aliasMap.put(queryInfo.getTableName(), "t_0");
		for (QueryJoin queryJoin : queryInfo.getQueryJoins()) {
			if (!aliasMap.containsKey(queryJoin.getJoinTable())) {
				aliasMap.put(queryJoin.getJoinTable(), "t_" + aliasMap.size());
			}
		}

		List<Object> values = new ArrayList<>();
		StringBuilder itemBuilder = new StringBuilder();
		for (AbstractItem abstractItem : queryInfo.getItemList()) {
			Optional.of(this.queryItem(aliasMap, abstractItem, values))
					.filter(StringUtils::notBlank)
					.ifPresent(item -> {
						if (itemBuilder.length() > 0) {
							itemBuilder.append(BrainCommons.DEFAULT_SPLIT_CHARACTER);
						}
						itemBuilder.append(item);
					});
		}

		if (itemBuilder.length() == 0) {
			throw new SQLException("No query column found! ");
		}

		String aliasCommand = this.aliasCommand();
		StringBuilder sqlBuilder =
				new StringBuilder(SELECT_COMMAND)
						.append(itemBuilder)
						.append(FROM_COMMAND)
						.append(this.nameCase(queryInfo.getTableName()))
						.append(aliasCommand)
						.append(BrainCommons.WHITE_SPACE)
						.append(aliasMap.get(queryInfo.getTableName()));

		for (QueryJoin queryJoin : queryInfo.getQueryJoins()) {
			sqlBuilder.append(this.joinCommand(aliasMap, queryJoin, aliasCommand));
		}
		String whereClause = this.whereClause(aliasMap, queryInfo.getConditionList(), values);
		if (StringUtils.isEmpty(whereClause)) {
			if (this.logger.isDebugEnabled()) {
				this.logger.warn("Query_Condition_Empty");
			}
		} else {
			sqlBuilder.append(WHERE_COMMAND).append(whereClause);
		}

		String orderBy = this.orderBy(aliasMap, queryInfo.getOrderByList());
		if (StringUtils.notBlank(orderBy)) {
			sqlBuilder.append(ORDER_BY_COMMAND).append(orderBy);
		}

		String groupBy = this.groupBy(aliasMap, queryInfo.getGroupByList(), queryInfo.getHavingList(), values);
		if (StringUtils.notBlank(groupBy)) {
			sqlBuilder.append(GROUP_BY_COMMAND).append(groupBy);
		}
		return new SQLCommand(sqlBuilder.toString(), values);
	}

	/**
	 * <h3 class="en-US">Generate SQL commands to data column</h3>
	 * <h3 class="zh-CN">生成数据列的SQL命令</h3>
	 *
	 * @param columnDefine <span class="en-US">Column define information</span>
	 *                     <span class="zh-CN">数据列定义信息</span>
	 * @param alterTable   <span class="en-US">Update database table struct</span>
	 *                     <span class="zh-CN">更新数据表结构</span>
	 * @return <span class="en-US">Generated SQL command</span>
	 * <span class="zh-CN">生成的SQL命令</span>
	 * @throws SQLException <span class="en-US">An error occurred while generating the SQL command</span>
	 *                      <span class="zh-CN">生成的SQL命令时出现错误</span>
	 */
	private String columnCommand(@Nonnull final ColumnDefine columnDefine, boolean alterTable) throws SQLException {
		StringBuilder sqlBuilder = new StringBuilder();
		String columnType = this.columnType(columnDefine);
		if (StringUtils.notBlank(columnType)) {
			sqlBuilder.append(this.nameCase(columnDefine.getColumnName()))
					.append(BrainCommons.WHITE_SPACE)
					.append(columnType)
					.append(BrainCommons.WHITE_SPACE);

			if (StringUtils.notBlank(columnDefine.getDefaultValue())) {
				sqlBuilder.append(COLUMN_DEFAULT_VALUE).append(BrainCommons.WHITE_SPACE).append(columnDefine.getDefaultValue());
			}
			if (!columnDefine.isNullable()) {
				sqlBuilder.append(COLUMN_NOT_NULL);
				if (alterTable && StringUtils.isEmpty(columnDefine.getDefaultValue())) {
					//  Throw exception if not configure the default value when alter table
					throw new SQLException("Add not null column must setting default value");
				}
			}
			if (columnDefine.isUnique()) {
				sqlBuilder.append(COLUMN_UNIQUE);
			}
		}
		return sqlBuilder.toString();
	}

	/**
	 * <h3 class="en-US">Generate SQL commands to join table</h3>
	 * <h3 class="zh-CN">生成关联数据表命令</h3>
	 *
	 * @param aliasMap     <span class="en-US">Data table alias mapping table</span>
	 *                     <span class="zh-CN">数据表别名映射表</span>
	 * @param queryJoin    <span class="en-US">Query join define information</span>
	 *                     <span class="zh-CN">查询关联信息定义</span>
	 * @param aliasCommand <span class="en-US">Command to set alias</span>
	 *                     <span class="zh-CN">设置别名的命令</span>
	 * @return <span class="en-US">Generated SQL command</span>
	 * <span class="zh-CN">生成的SQL命令</span>
	 * @throws SQLException <span class="en-US">An error occurred while generating the SQL command</span>
	 *                      <span class="zh-CN">生成的SQL命令时出现错误</span>
	 */
	private String joinCommand(final Map<String, String> aliasMap, final QueryJoin queryJoin,
	                           final String aliasCommand) throws SQLException {
		StringBuilder sqlBuilder = new StringBuilder();
		switch (queryJoin.getJoinType()) {
			case FULL:
				sqlBuilder.append(COMMAND_JOIN_FULL);
				break;
			case LEFT:
				sqlBuilder.append(COMMAND_JOIN_LEFT);
				break;
			case CROSS:
				sqlBuilder.append(COMMAND_JOIN_CROSS);
				break;
			case INNER:
				sqlBuilder.append(COMMAND_JOIN_INNER);
				break;
			case RIGHT:
				sqlBuilder.append(COMMAND_JOIN_RIGHT);
				break;
			default:
				throw new SQLException("Unknown join type: " + queryJoin.getJoinType());
		}
		sqlBuilder.append(this.nameCase(queryJoin.getJoinTable()))
				.append(aliasCommand)
				.append(BrainCommons.WHITE_SPACE)
				.append(DEFAULT_COMMAND_ON);
		StringBuilder columnBuilder = new StringBuilder();
		for (JoinInfo joinInfo : queryJoin.getJoinInfos()) {
			if (columnBuilder.length() > 0) {
				columnBuilder.append(BrainCommons.WHITE_SPACE)
						.append(ConnectionCode.AND)
						.append(BrainCommons.WHITE_SPACE);
			}
			columnBuilder.append(this.columnName(aliasMap, queryJoin.getDriverTable(), joinInfo.getJoinKey()))
					.append(BrainCommons.OPERATOR_EQUAL)
					.append(this.columnName(aliasMap, queryJoin.getJoinTable(), joinInfo.getReferenceKey()));
		}
		sqlBuilder.append(BrainCommons.BRACKETS_BEGIN).append(columnBuilder).append(BrainCommons.BRACKETS_END);
		return sqlBuilder.toString();
	}

	/**
	 * <h3 class="en-US">Generate where commands to data filter</h3>
	 * <h3 class="zh-CN">生成数据的WHERE命令</h3>
	 *
	 * @param aliasMap      <span class="en-US">Data table alias mapping table</span>
	 *                      <span class="zh-CN">数据表别名映射表</span>
	 * @param conditionList <span class="en-US">Query matching condition list</span>
	 *                      <span class="zh-CN">查询匹配条件列表</span>
	 * @param values        <span class="en-US">Parameter value list</span>
	 *                      <span class="zh-CN">参数值列表</span>
	 * @return <span class="en-US">Generated SQL command</span>
	 * <span class="zh-CN">生成的SQL命令</span>
	 * @throws SQLException <span class="en-US">An error occurred while generating the SQL command</span>
	 *                      <span class="zh-CN">生成的SQL命令时出现错误</span>
	 */
	private String whereClause(final Map<String, String> aliasMap, final List<Condition> conditionList,
	                           final List<Object> values) throws SQLException {
		StringBuilder sqlBuilder = new StringBuilder();
		conditionList.sort(SortedItem.desc());
		for (Condition condition : conditionList) {
			if (sqlBuilder.length() > 0) {
				sqlBuilder.append(BrainCommons.WHITE_SPACE)
						.append(condition.getConnectionCode().toString())
						.append(BrainCommons.WHITE_SPACE);
			}
			switch (condition.getConditionType()) {
				case COLUMN:
					sqlBuilder.append(this.columnCondition(aliasMap, condition.unwrap(ColumnCondition.class), values));
					break;
				case GROUP:
					GroupCondition groupCondition = condition.unwrap(GroupCondition.class);
					String groupWhereClause = this.whereClause(aliasMap, groupCondition.getConditionList(), values);
					if (StringUtils.notBlank(groupWhereClause)) {
						sqlBuilder.append(BrainCommons.BRACKETS_BEGIN)
								.append(groupWhereClause)
								.append(BrainCommons.BRACKETS_END);
					}
					break;
				default:
					throw new SQLException("Unknown condition type: " + condition.getConditionType());
			}
		}
		return sqlBuilder.toString();
	}

	/**
	 * <h3 class="en-US">Generate sub-query commands</h3>
	 * <h3 class="zh-CN">生成子查询命令</h3>
	 *
	 * @param aliasMap  <span class="en-US">Data table alias mapping table</span>
	 *                  <span class="zh-CN">数据表别名映射表</span>
	 * @param queryData <span class="en-US">Sub-query define information</span>
	 *                  <span class="zh-CN">子查询信息</span>
	 * @param values    <span class="en-US">Parameter value list</span>
	 *                  <span class="zh-CN">参数值列表</span>
	 * @return <span class="en-US">Generated SQL command</span>
	 * <span class="zh-CN">生成的SQL命令</span>
	 * @throws SQLException <span class="en-US">An error occurred while generating the SQL command</span>
	 *                      <span class="zh-CN">生成的SQL命令时出现错误</span>
	 */
	private String subQuery(final Map<String, String> aliasMap, @Nonnull final QueryData queryData,
	                        final List<Object> values) throws SQLException {
		Map<String, String> subAliasMap = Map.of(queryData.getTableName(), "t_" + aliasMap.size());
		StringBuilder sqlBuilder = new StringBuilder(SELECT_COMMAND)
				.append(this.queryItem(subAliasMap, queryData.getQueryItem(), values))
				.append(FROM_COMMAND)
				.append(this.nameCase(queryData.getTableName()));
		String whereClause = this.whereClause(subAliasMap, queryData.getConditions(), values);
		if (StringUtils.notBlank(whereClause)) {
			sqlBuilder.append(WHERE_COMMAND).append(whereClause);
		}
		if (!queryData.getGroupBy().isEmpty()) {
			StringBuilder groupByClause = new StringBuilder();
			for (String groupBy : queryData.getGroupBy()) {
				if (groupByClause.length() > 0) {
					groupByClause.append(BrainCommons.DEFAULT_SPLIT_CHARACTER);
				}
				groupByClause.append(this.columnName(subAliasMap, queryData.getTableName(), groupBy));
			}
			if (groupByClause.indexOf(BrainCommons.DEFAULT_SPLIT_CHARACTER) > 0) {
				groupByClause.append(BrainCommons.BRACKETS_END);
				groupByClause.insert(Globals.INITIALIZE_INT_VALUE, BrainCommons.BRACKETS_BEGIN);
			}
			sqlBuilder.append(GROUP_BY_COMMAND);
			sqlBuilder.append(groupByClause);

			String havingClause = this.whereClause(subAliasMap, queryData.getConditions(), values);
			if (StringUtils.notBlank(havingClause)) {
				sqlBuilder.append(HAVING_COMMAND).append(havingClause);
			}
		}
		return sqlBuilder.toString();
	}

	/**
	 * <h3 class="en-US">Generate order by commands</h3>
	 * <h3 class="zh-CN">生成排序命令</h3>
	 *
	 * @param aliasMap    <span class="en-US">Data table alias mapping table</span>
	 *                    <span class="zh-CN">数据表别名映射表</span>
	 * @param orderByList <span class="en-US">Sort data column definition list</span>
	 *                    <span class="zh-CN">排序数据列定义列表</span>
	 * @return <span class="en-US">Generated SQL command</span>
	 * <span class="zh-CN">生成的SQL命令</span>
	 */
	private String orderBy(final Map<String, String> aliasMap, final List<OrderBy> orderByList) {
		StringBuilder sqlBuilder = new StringBuilder();
		if (!orderByList.isEmpty()) {
			for (OrderBy orderBy : orderByList) {
				if (sqlBuilder.length() > 0) {
					sqlBuilder.append(BrainCommons.DEFAULT_SPLIT_CHARACTER);
				}
				sqlBuilder.append(this.columnName(aliasMap, orderBy.getTableName(), orderBy.getColumnName()))
						.append(BrainCommons.WHITE_SPACE)
						.append(orderBy.getOrderType().toString());
			}
			sqlBuilder.append(BrainCommons.BRACKETS_END);
			sqlBuilder.insert(Globals.INITIALIZE_INT_VALUE, BrainCommons.BRACKETS_BEGIN);
		}
		return sqlBuilder.toString();
	}

	/**
	 * <h3 class="en-US">Generate group by commands</h3>
	 * <h3 class="zh-CN">生成分组查询命令</h3>
	 *
	 * @param aliasMap         <span class="en-US">Data table alias mapping table</span>
	 *                         <span class="zh-CN">数据表别名映射表</span>
	 * @param groupByList      <span class="en-US">Group data column definition list</span>
	 *                         <span class="zh-CN">分组数据列定义列表</span>
	 * @param havingConditions <span class="en-US">Group data column filter list</span>
	 *                         <span class="zh-CN">分组数据列筛选列表</span>
	 * @param values           <span class="en-US">Parameter value list</span>
	 *                         <span class="zh-CN">参数值列表</span>
	 * @return <span class="en-US">Generated SQL command</span>
	 * <span class="zh-CN">生成的SQL命令</span>
	 * @throws SQLException <span class="en-US">An error occurred while generating the SQL command</span>
	 *                      <span class="zh-CN">生成的SQL命令时出现错误</span>
	 */
	private String groupBy(final Map<String, String> aliasMap, final List<GroupBy> groupByList,
	                       final List<Condition> havingConditions, final List<Object> values) throws SQLException {
		StringBuilder sqlBuilder = new StringBuilder();
		if (!groupByList.isEmpty()) {
			for (GroupBy groupBy : groupByList) {
				if (sqlBuilder.length() > 0) {
					sqlBuilder.append(BrainCommons.DEFAULT_SPLIT_CHARACTER);
				}
				sqlBuilder.append(this.columnName(aliasMap, groupBy.getTableName(), groupBy.getColumnName()));
			}
			sqlBuilder.append(BrainCommons.BRACKETS_END);
			sqlBuilder.insert(Globals.INITIALIZE_INT_VALUE, BrainCommons.BRACKETS_BEGIN);

			if (!havingConditions.isEmpty()) {
				String havingClause = this.whereClause(aliasMap, havingConditions, values);
				if (StringUtils.notBlank(havingClause)) {
					sqlBuilder.append(HAVING_COMMAND).append(havingClause);
				}
			}
		}
		return sqlBuilder.toString();
	}

	/**
	 * <h3 class="en-US">Generate query item commands</h3>
	 * <h3 class="zh-CN">生成查询项命令</h3>
	 *
	 * @param aliasMap     <span class="en-US">Data table alias mapping table</span>
	 *                     <span class="zh-CN">数据表别名映射表</span>
	 * @param abstractItem <span class="en-US">Query item define information</span>
	 *                     <span class="zh-CN">查询项定义信息</span>
	 * @param values       <span class="en-US">Parameter value list</span>
	 *                     <span class="zh-CN">参数值列表</span>
	 * @return <span class="en-US">Generated SQL command</span>
	 * <span class="zh-CN">生成的SQL命令</span>
	 * @throws SQLException <span class="en-US">An error occurred while generating the SQL command</span>
	 *                      <span class="zh-CN">生成的SQL命令时出现错误</span>
	 */
	private String queryItem(final Map<String, String> aliasMap, final AbstractItem abstractItem,
	                         final List<Object> values) throws SQLException {
		StringBuilder sqlBuilder = new StringBuilder();
		switch (abstractItem.getItemType()) {
			case FUNCTION:
				FunctionItem functionItem = abstractItem.unwrap(FunctionItem.class);
				for (AbstractParameter<?> functionParameter : functionItem.getFunctionParams()) {
					String parameter = this.parameterValue(aliasMap, functionParameter, values);
					if (StringUtils.notBlank(parameter)) {
						if (sqlBuilder.length() > 0) {
							sqlBuilder.append(BrainCommons.DEFAULT_SPLIT_CHARACTER);
						}
						sqlBuilder.append(parameter);
					}
				}
				sqlBuilder.append(BrainCommons.BRACKETS_END);
				sqlBuilder.insert(Globals.INITIALIZE_INT_VALUE, BrainCommons.BRACKETS_BEGIN);
				sqlBuilder.insert(Globals.INITIALIZE_INT_VALUE, functionItem.getSqlFunction());
				break;
			case QUERY:
				if (StringUtils.isEmpty(abstractItem.getAliasName())) {
					throw new SQLException("Sub-query must specify an alias");
				}
				QueryItem queryItem = abstractItem.unwrap(QueryItem.class);
				sqlBuilder.append(BrainCommons.BRACKETS_BEGIN)
						.append(this.subQuery(aliasMap, queryItem.getQueryData(), values))
						.append(BrainCommons.BRACKETS_END)
						.append(this.aliasCommand())
						.append(BrainCommons.WHITE_SPACE)
						.append(abstractItem.getAliasName());
				break;
			case COLUMN:
				ColumnItem columnItem = abstractItem.unwrap(ColumnItem.class);
				sqlBuilder.append(this.columnName(aliasMap, columnItem.getTableName(), columnItem.getColumnName()));
				if (StringUtils.notBlank(abstractItem.getAliasName())) {
					sqlBuilder.append(this.aliasCommand())
							.append(BrainCommons.WHITE_SPACE)
							.append(abstractItem.getAliasName())
							.append(BrainCommons.WHITE_SPACE);
				}
				break;
			default:
				throw new SQLException("Unknown item type: " + abstractItem.getItemType());
		}
		return sqlBuilder.toString();
	}

	/**
	 * <h3 class="en-US">Generate query column name commands</h3>
	 * <h3 class="zh-CN">生成查询列名称命令</h3>
	 *
	 * @param aliasMap   <span class="en-US">Data table alias mapping table</span>
	 *                   <span class="zh-CN">数据表别名映射表</span>
	 * @param tableName  <span class="en-US">Database table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Generated SQL command</span>
	 * <span class="zh-CN">生成的SQL命令</span>
	 */
	private String columnName(final Map<String, String> aliasMap, final String tableName, final String columnName) {
		if (aliasMap.containsKey(tableName)) {
			return aliasMap.get(tableName) + BrainCommons.DEFAULT_NAME_SPLIT + this.nameCase(columnName);
		}
		return this.nameCase(columnName);
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
	private String parameterValue(final Map<String, String> aliasMap, final AbstractParameter<?> abstractParameter,
	                              final List<Object> values) throws SQLException {
		StringBuilder sqlBuilder = new StringBuilder();
		switch (abstractParameter.getItemType()) {
			case COLUMN:
				ColumnItem columnItem = abstractParameter.unwrap(ColumnParameter.class).getItemValue();
				sqlBuilder.append(this.queryItem(aliasMap, columnItem, values));
				break;
			case ARRAY:
				ArrayData arrayData = abstractParameter.unwrap(ArraysParameter.class).getItemValue();
				if (arrayData.getArrayObject().length == 0) {
					throw new SQLException("Array is empty");
				}
				if (arrayData.getArrayObject().length == 1) {
					throw new SQLException("Array length is 1, using EQUAL condition instead");
				}
				for (Object object : arrayData.getArrayObject()) {
					if (sqlBuilder.length() > 0) {
						sqlBuilder.append(BrainCommons.DEFAULT_SPLIT_CHARACTER);
					}
					sqlBuilder.append(BrainCommons.DEFAULT_PLACE_HOLDER);
					values.add(object);
				}
				sqlBuilder.insert(Globals.INITIALIZE_INT_VALUE, BrainCommons.BRACKETS_BEGIN);
				sqlBuilder.append(BrainCommons.BRACKETS_END);
				break;
			case QUERY:
				QueryParameter queryParameter = abstractParameter.unwrap(QueryParameter.class);
				if (StringUtils.notBlank(queryParameter.getFunctionName())) {
					sqlBuilder.append(queryParameter.getFunctionName());
				}
				sqlBuilder.append(BrainCommons.BRACKETS_BEGIN)
						.append(this.subQuery(aliasMap, queryParameter.getItemValue(), values))
						.append(BrainCommons.BRACKETS_END);
				break;
			case RANGE:
				RangesData rangesData = abstractParameter.unwrap(RangesParameter.class).getItemValue();
				if (rangesData == null) {
					throw new SQLException("Can't found range value");
				}
				values.add(rangesData.getBeginValue());
				values.add(rangesData.getEndValue());
				break;
			case CONSTANT:
				ConstantParameter constantParameter = abstractParameter.unwrap(ConstantParameter.class);
				sqlBuilder.append(BrainCommons.DEFAULT_PLACE_HOLDER);
				values.add(constantParameter.getItemValue());
				break;
			case FUNCTION:
				FunctionItem functionItem = abstractParameter.unwrap(FunctionParameter.class).getItemValue();
				sqlBuilder.append(this.queryItem(aliasMap, functionItem, values));
				break;
		}
		return sqlBuilder.toString();
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
	private String columnCondition(final Map<String, String> aliasMap, final ColumnCondition columnCondition,
	                               final List<Object> values) throws SQLException {
		StringBuilder sqlBuilder = new StringBuilder();
		if (StringUtils.notBlank(columnCondition.getFunctionName())) {
			sqlBuilder.append(columnCondition.getFunctionName()).append(BrainCommons.BRACKETS_BEGIN);
		}
		sqlBuilder.append(this.columnName(aliasMap, columnCondition.getTableName(), columnCondition.getColumnName()));
		if (StringUtils.notBlank(columnCondition.getFunctionName())) {
			sqlBuilder.append(BrainCommons.BRACKETS_BEGIN);
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
	 * <h3 class="en-US">Generate Where sentences based on the given filtering information mapping table</h3>
	 * <h3 class="zh-CN">根据给定的过滤信息映射表生成Where字句</h3>
	 *
	 * @param filterMap <span class="en-US">Retrieve filter mapping</span>
	 *                  <span class="zh-CN">查询条件映射表</span>
	 * @param values    <span class="en-US">Query condition value list</span>
	 *                  <span class="zh-CN">查询条件值列表</span>
	 * @return <span class="en-US">Generated where sentences</span>
	 * <span class="zh-CN">生成的Where字句</span>
	 */
	private String whereClause(final Map<String, Serializable> filterMap, final List<Object> values) {
		StringBuilder whereClause = new StringBuilder();
		if (!filterMap.isEmpty()) {
			for (Map.Entry<String, Serializable> entry : filterMap.entrySet()) {
				whereClause.append(BrainCommons.WHITE_SPACE)
						.append(ConnectionCode.AND)
						.append(BrainCommons.WHITE_SPACE)
						.append(this.nameCase(entry.getKey()))
						.append(BrainCommons.OPERATOR_EQUAL)
						.append(BrainCommons.DEFAULT_PLACE_HOLDER);
				values.add(entry.getValue());
			}
		}
		return whereClause.toString();
	}

	/**
	 * <h2 class="en-US">SQL command information</h2>
	 * <h2 class="zh-CN">SQL命令信息</h2>
	 *
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision: 1.0.0 $ $Date: Feb 18, 2019 10:38:52 $
	 */
	public static final class SQLCommand {
		/**
		 * <span class="en-US">Generated SQL command</span>
		 * <span class="zh-CN">生成的SQL命令</span>
		 */
		private final String sql;
		/**
		 * <span class="en-US">Parameter value list</span>
		 * <span class="zh-CN">参数值列表</span>
		 */
		private final List<Object> values;

		/**
		 * <h3 class="en-US">Private constructor method for SQL command information</h3>
		 * <h3 class="zh-CN">SQL命令信息的私有构造方法</h3>
		 *
		 * @param sql    <span class="en-US">Generated SQL command</span>
		 *               <span class="zh-CN">生成的SQL命令</span>
		 * @param values <span class="en-US">Parameter value list</span>
		 *               <span class="zh-CN">参数值列表</span>
		 */
		private SQLCommand(final String sql, final List<Object> values) {
			this.sql = sql;
			this.values = values;
		}

		/**
		 * <h3 class="en-US">Getter method for generated SQL command</h3>
		 * <h3 class="zh-CN">生成的SQL命令的Getter方法</h3>
		 *
		 * @return <span class="en-US">Generated SQL command</span>
		 * <span class="zh-CN">生成的SQL命令</span>
		 */
		public String getSql() {
			return this.sql;
		}

		/**
		 * <h3 class="en-US">Getter method for parameter value list</h3>
		 * <h3 class="zh-CN">参数值列表的Getter方法</h3>
		 *
		 * @return <span class="en-US">Parameter value list</span>
		 * <span class="zh-CN">参数值列表</span>
		 */
		public List<Object> getValues() {
			return this.values;
		}
	}
}
