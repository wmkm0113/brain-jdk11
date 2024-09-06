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

package org.nervousync.brain.configs.sharding;

import jakarta.annotation.Nonnull;
import org.nervousync.brain.defines.ColumnDefine;
import org.nervousync.brain.defines.ShardingDefine;
import org.nervousync.brain.defines.TableDefine;
import org.nervousync.brain.enumerations.sharding.ShardingType;
import org.nervousync.brain.exceptions.defines.TableDefineException;
import org.nervousync.brain.sharding.Calculator;
import org.nervousync.commons.Globals;
import org.nervousync.utils.ObjectUtils;
import org.nervousync.utils.StringUtils;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

/**
 * <h2 class="en-US">Sharding configure information</h2>
 * <h2 class="zh-CN">分片配置信息</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Nov 12, 2020 17:02:27 $
 */
public final class ShardingConfig {

	/**
	 * <span class="en-US">Database sharding configure details</span>
	 * <span class="zh-CN">数据库分片配置详情</span>
	 */
	private final ShardingDetails<?> shardingDatabase;
	/**
	 * <span class="en-US">Table sharding configure details</span>
	 * <span class="zh-CN">数据表分片配置详情</span>
	 */
	private final ShardingDetails<?> shardingTable;

	/**
	 * <h3 class="en-US">Constructor method for sharding configure information</h3>
	 * <h3 class="zh-CN">分片配置信息的构造方法</h3>
	 *
	 * @param tableDefine <span class="en-US">Table define information</span>
	 *                    <span class="zh-CN">数据表定义信息</span>
	 * @param database    <span class="en-US">Database sharding configuration information</span>
	 *                    <span class="zh-CN">数据库分片配置信息</span>
	 * @param table       <span class="en-US">Data table sharding configuration information</span>
	 *                    <span class="zh-CN">数据表分片配置信息</span>
	 * @throws TableDefineException <span class="en-US">If not found sharding column</span>
	 *                              <span class="zh-CN">如果分片数据列未找到</span>
	 */
	public ShardingConfig(@Nonnull final TableDefine tableDefine, final ShardingDefine<?> database,
	                      final ShardingDefine<?> table) throws TableDefineException {
		if (database == null) {
			this.shardingDatabase = null;
		} else {
			this.shardingDatabase = Optional.ofNullable(tableDefine.column(database.getColumnName()))
					.map(columnDefine -> new ShardingDetails<>(database, columnDefine))
					.orElseThrow(() -> new TableDefineException(0x00DB00000002L,
							tableDefine.getTableName(), database.getColumnName()));
		}
		if (table == null) {
			this.shardingTable = null;
		} else {
			this.shardingTable = Optional.ofNullable(tableDefine.column(table.getColumnName()))
					.map(columnDefine -> new ShardingDetails<>(table, columnDefine))
					.orElseThrow(() -> new TableDefineException(0x00DB00000002L,
							tableDefine.getTableName(), table.getColumnName()));
		}
	}

	/**
	 * <h3 class="en-US">Calculate sharding key</h3>
	 * <h3 class="zh-CN">计算分片值</h3>
	 *
	 * @param shardingType <span class="en-US">Enumeration value of sharding type</span>
	 *                     <span class="zh-CN">分片类型枚举值</span>
	 * @param parameterMap <span class="en-US">Columns data mapping</span>
	 *                     <span class="zh-CN">数据列信息映射表</span>
	 * @return <span class="en-US">Calculated sharding key result</span>
	 * <span class="zh-CN">分片计算结果值</span>
	 */
	public String shardingKey(@Nonnull final ShardingType shardingType,
	                          @Nonnull final Map<String, Serializable> parameterMap) {
		switch (shardingType) {
			case DATABASE:
				return (this.shardingDatabase == null)
						? Globals.DEFAULT_VALUE_STRING
						: this.shardingDatabase.shardingKey(parameterMap);
			case TABLE:
				return (this.shardingTable == null)
						? Globals.DEFAULT_VALUE_STRING
						: this.shardingTable.shardingKey(parameterMap);
		}
		return Globals.DEFAULT_VALUE_STRING;
	}

	/**
	 * <h3 class="en-US">Matches sharding result</h3>
	 * <h3 class="zh-CN">匹配分片值</h3>
	 *
	 * @param shardingType <span class="en-US">Enumeration value of sharding type</span>
	 *                     <span class="zh-CN">分片类型枚举值</span>
	 * @param value        <span class="en-US">Sharding result</span>
	 *                     <span class="zh-CN">分片值</span>
	 * @return <span class="en-US">Matches result</span>
	 * <span class="zh-CN">匹配结果</span>
	 */
	public boolean matchKey(@Nonnull final ShardingType shardingType, @Nonnull final String value) {
		switch (shardingType) {
			case DATABASE:
				return (this.shardingDatabase == null)
						? Boolean.FALSE
						: this.shardingDatabase.matchKey(value);
			case TABLE:
				return (this.shardingTable == null)
						? Boolean.FALSE
						: this.shardingTable.matchKey(value);
		}
		return Boolean.FALSE;
	}

	/**
	 * <h2 class="en-US">Sharding configure details</h2>
	 * <h2 class="zh-CN">分片配置详情</h2>
	 *
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision: 1.0.0 $ $Date: Nov 12, 2020 17:02:27 $
	 */
	private static final class ShardingDetails<T> {

		/**
		 * <span class="en-US">Default value of sharding key</span>
		 * <span class="zh-CN">分片默认值</span>
		 */
		private final String defaultValue;
		/**
		 * <span class="en-US">Sharding key from column name</span>
		 * <span class="zh-CN">分片数据列</span>
		 */
		private final String columnName;
		/**
		 * <span class="en-US">Sharding result template</span>
		 * <span class="zh-CN">分片结果模板</span>
		 */
		private final String shardingTemplate;
		/**
		 * <span class="en-US">Column data type</span>
		 * <span class="zh-CN">数据列类型</span>
		 */
		private final Class<T> fieldType;
		/**
		 * <span class="en-US">Sharding value calculation method reflection object</span>
		 * <span class="zh-CN">分片值计算方法反射对象</span>
		 */
		private final Calculator<T> calculator;

		/**
		 * <h3 class="en-US">Constructor method for sharding configure details</h3>
		 * <h3 class="zh-CN">分片配置详情的构造方法</h3>
		 *
		 * @param shardingDefine <span class="en-US">Sharding define information</span>
		 *                       <span class="zh-CN">分片定义信息</span>
		 * @param columnDefine   <span class="en-US">Sharding column define information</span>
		 *                       <span class="zh-CN">分片数据列定义信息</span>
		 */
		@SuppressWarnings("unchecked")
		ShardingDetails(@Nonnull final ShardingDefine<T> shardingDefine, @Nonnull final ColumnDefine columnDefine) {
			this.defaultValue = shardingDefine.getDefaultValue();
			this.columnName = columnDefine.getColumnName();
			this.shardingTemplate = shardingDefine.getShardingTemplate();
			this.fieldType = shardingDefine.getFieldType();
			this.calculator = (Calculator<T>) ObjectUtils.newInstance(shardingDefine.getCalculatorClass());
		}

		/**
		 * <h3 class="en-US">Calculate sharding key</h3>
		 * <h3 class="zh-CN">计算分片值</h3>
		 *
		 * @param parameterMap <span class="en-US">Columns data mapping</span>
		 *                     <span class="zh-CN">数据列信息映射表</span>
		 * @return <span class="en-US">Calculated sharding key result</span>
		 * <span class="zh-CN">分片计算结果值</span>
		 */
		String shardingKey(@Nonnull final Map<String, Serializable> parameterMap) {
			if (this.calculator == null) {
				return this.defaultValue;
			}
			String result = Optional.ofNullable(parameterMap.get(this.columnName))
					.map(this.fieldType::cast)
					.map(this.calculator::result)
					.orElse(this.defaultValue);
			return StringUtils.replace(this.shardingTemplate, "{shardingKey}", result);
		}

		/**
		 * <h3 class="en-US">Matches sharding result</h3>
		 * <h3 class="zh-CN">匹配分片值</h3>
		 *
		 * @param value <span class="en-US">Sharding result</span>
		 *              <span class="zh-CN">分片值</span>
		 * @return <span class="en-US">Matches result</span>
		 * <span class="zh-CN">匹配结果</span>
		 */
		boolean matchKey(@Nonnull final String value) {
			if (this.calculator == null) {
				return ObjectUtils.nullSafeEquals(value, this.defaultValue);
			}
			return this.calculator.matches(value);
		}
	}
}
