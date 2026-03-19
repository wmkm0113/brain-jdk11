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

package org.nervousync.brain.manager;

import jakarta.annotation.Nonnull;
import org.intellij.lang.annotations.MagicConstant;
import org.nervousync.brain.commons.BrainCommons;
import org.nervousync.brain.defines.ColumnDefine;
import org.nervousync.brain.defines.TableDefine;
import org.nervousync.brain.exceptions.sql.MultilingualSQLException;
import org.nervousync.utils.core.StringUtils;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;

/**
 * <h2 class="en-US">Data table manager</h2>
 * <span class="en-US">Running in singleton mode</span>
 * <h2 class="zh-CN">数据表管理器</h2>
 * <span class="zh-CN">使用单例模式运行</span>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Feb 18, 2019 10:15:08 $
 */
@SuppressWarnings("unused")
public final class TableManager {

	/**
	 * <span class="en-US">Data table manager instance object</span>
	 * <span class="zh-CN">数据表管理器的实例对象</span>
	 */
	private static TableManager INSTANCE = null;

	/**
	 * <span class="en-US">Registered data table define information mapping table</span>
	 * <span class="zh-CN">注册的数据表定义信息</span>
	 */
	private final Hashtable<String, TableDefine> registeredTables;
	/**
	 * <span class="en-US">Mapping table of data table identification codes and table names</span>
	 * <span class="zh-CN">数据表识别代码与表名的映射表</span>
	 */
	private final Hashtable<String, String> identifyCodeMapping;

	/**
	 * <h3 class="en-US">Private constructor method for the data table manager</h3>
	 * <h3 class="zh-CN">数据表管理器的私有构造方法</h3>
	 */
	private TableManager() {
		this.registeredTables = new Hashtable<>();
		this.identifyCodeMapping = new Hashtable<>();
	}

	/**
	 * <h3 class="en-US">Static method for get the data table manager instance object</h3>
	 * <h3 class="zh-CN">静态方法用于获取数据表管理器的实例对象</h3>
	 *
	 * @return <span class="en-US">Data table manager instance object</span>
	 * <span class="zh-CN">数据表管理器的实例对象</span>
	 */
	public static TableManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new TableManager();
		}
		return INSTANCE;
	}

	/**
	 * <h3 class="en-US">Register the data table define information</h3>
	 * <h3 class="zh-CN">注册数据表定义信息</h3>
	 *
	 * @param tableDefine <span class="en-US">Table defines information</span>
	 *                    <span class="zh-CN">数据表定义信息</span>
	 */
	public void register(final TableDefine tableDefine) {
		if (this.registeredTables.containsKey(tableDefine.getTableName())) {
			return;
		}
		this.registeredTables.put(tableDefine.getTableName(), tableDefine);
		this.identifyCodeMapping.put(BrainCommons.identifyCode(tableDefine.getTableName()), tableDefine.getTableName());
	}

	/**
	 * <h3 class="en-US">Get the registered table define information</h3>
	 * <h3 class="zh-CN">获取注册的数据表定义信息</h3>
	 *
	 * @param identifyCode <span class="en-US">Data table identify code</span>
	 *                     <span class="zh-CN">数据表识别代码</span>
	 * @return <span class="en-US">Data table define information</span>
	 * <span class="zh-CN">数据表定义信息</span>
	 * @throws SQLException <span class="en-US">The data table is not registered</span>
	 *                      <span class="zh-CN">数据表未注册</span>
	 */
	@Nonnull
	public TableDefine define(@Nonnull final String identifyCode) throws SQLException {
		String tableName = this.identifyCodeMapping.getOrDefault(identifyCode, identifyCode);
		if (StringUtils.isEmpty(tableName)) {
			throw new MultilingualSQLException(0x00DB00000034L, tableName);
		}
		TableDefine tableDefine = this.registeredTables.get(tableName);
		if (tableDefine == null) {
			throw new MultilingualSQLException(0x00DB00000034L, tableName);
		}
		return tableDefine;
	}

	/**
	 * <h3 class="en-US">Checks if the given array of table identification codes is in the same data source</h3>
	 * <h3 class="zh-CN">检查给定的数据表识别代码数组是否在同一数据源中</h3>
	 *
	 * @param identifyCodes <span class="en-US">Data table identify codes array</span>
	 *                      <span class="zh-CN">数据表识别代码数组</span>
	 * @return <span class="en-US">Check result</span>
	 * <span class="zh-CN">检查结果</span>
	 */
	public boolean sameSchema(@Nonnull final String... identifyCodes) {
		if (identifyCodes.length == 0) {
			return Boolean.TRUE;
		}
		List<String> schemaList = new ArrayList<>();
		List.of(identifyCodes).forEach(identifyCode -> {
			String tableName = this.identifyCodeMapping.getOrDefault(identifyCode, identifyCode);
			if (StringUtils.isEmpty(tableName)) {
				return;
			}
			TableDefine tableDefine = this.registeredTables.get(tableName);
			if (tableDefine == null) {
				return;
			}
			if (!schemaList.contains(tableDefine.getSchemaName())) {
				schemaList.add(tableDefine.getSchemaName());
			}
		});
		return schemaList.size() == 1;
	}

	/**
	 * <h3 class="en-US">Get the column name of the data column in the registration data table</h3>
	 * <h3 class="zh-CN">获取注册数据表中数据列的列名</h3>
	 *
	 * @param tableIdentify  <span class="en-US">Data table identify code</span>
	 *                       <span class="zh-CN">数据表识别代码</span>
	 * @param columnIdentify <span class="en-US">Data column identify code</span>
	 *                       <span class="zh-CN">数据列识别代码</span>
	 * @return <span class="en-US">Column name of the data column</span>
	 * <span class="zh-CN">数据列的列名</span>
	 * @throws SQLException <span class="en-US">The data table is not registered or data column not exists</span>
	 *                      <span class="zh-CN">数据表未注册或数据列不存在</span>
	 */
	public String columnName(@Nonnull final String tableIdentify, @Nonnull final String columnIdentify)
			throws SQLException {
		return Optional.ofNullable(this.define(tableIdentify).column(columnIdentify))
				.map(ColumnDefine::getColumnName)
				.orElseThrow(() -> new MultilingualSQLException(0x00DB00000011L));
	}

	/**
	 * <h3 class="en-US">Get the JDBC type code of the data column in the registration data table</h3>
	 * <h3 class="zh-CN">获取注册数据表中数据列的JDBC类型代码</h3>
	 *
	 * @param tableIdentify  <span class="en-US">Data table identify code</span>
	 *                       <span class="zh-CN">数据表识别代码</span>
	 * @param columnIdentify <span class="en-US">Data column identify code</span>
	 *                       <span class="zh-CN">数据列识别代码</span>
	 * @return <span class="en-US">JDBC type code</span>
	 * <span class="zh-CN">JDBC类型代码</span>
	 * @throws SQLException <span class="en-US">The data table is not registered or data column not exists</span>
	 *                      <span class="zh-CN">数据表未注册或数据列不存在</span>
	 */
	@MagicConstant(valuesFromClass = Types.class)
	public int jdbcType(@Nonnull final String tableIdentify, @Nonnull final String columnIdentify) throws SQLException {
		return Optional.ofNullable(this.define(tableIdentify).column(columnIdentify))
				.map(ColumnDefine::getJdbcType)
				.orElseThrow(() -> new MultilingualSQLException(0x00DB00000011L));
	}

	/**
	 * <h3 class="en-US">Static method for destroy the data table manager instance object</h3>
	 * <h3 class="zh-CN">静态方法用于销毁数据表管理器的实例对象</h3>
	 */
	public static void destroy() {
		if (INSTANCE != null) {
			INSTANCE = null;
		}
	}
}
