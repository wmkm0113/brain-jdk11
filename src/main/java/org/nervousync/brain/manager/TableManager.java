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
import org.nervousync.brain.commons.BrainCommons;
import org.nervousync.brain.defines.TableDefine;
import org.nervousync.brain.exceptions.sql.MultilingualSQLException;
import org.nervousync.utils.StringUtils;

import java.sql.SQLException;
import java.util.Hashtable;

/**
 * <h2 class="en-US">Data table manager</h2>
 * <span class="en-US">Running in singleton mode</span>
 * <h2 class="zh-CN">数据表管理器</h2>
 * <span class="zh-CN">使用单例模式运行</span>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Feb 18, 2019 10:15:08 $
 */
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
	 * <h3 class="en-US">Static method for obtain the data table manager instance object</h3>
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
	 * <h3 class="en-US">Check whether the data source and data table are registered</h3>
	 * <h3 class="zh-CN">检查数据源和数据表是否注册</h3>
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
	 * <h3 class="en-US">Static method for destroy the data table manager instance object</h3>
	 * <h3 class="zh-CN">静态方法用于销毁数据表管理器的实例对象</h3>
	 */
	public static void destroy() {
		if (INSTANCE != null) {
			INSTANCE = null;
		}
	}
}
