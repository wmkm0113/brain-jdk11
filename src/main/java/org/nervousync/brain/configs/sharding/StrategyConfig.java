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
import org.nervousync.brain.defines.StrategyDefine;
import org.nervousync.brain.query.condition.Condition;
import org.nervousync.brain.sharding.Calculator;
import org.nervousync.commons.Globals;
import org.nervousync.utils.core.ClassUtils;
import org.nervousync.utils.core.ObjectUtils;
import org.nervousync.utils.core.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <h2 class="en-US">Sharding strategies configure information</h2>
 * <h2 class="zh-CN">分片规则配置信息</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Nov 12, 2020 17:02:27 $
 */
@SuppressWarnings("unused")
public final class StrategyConfig {

	/**
	 * <span class="en-US">Data table name</span>
	 * <span class="zh-CN">数据表名</span>
	 */
	private final String tableName;
	/**
	 * <span class="en-US">Default database sharding key</span>
	 * <span class="zh-CN">默认的数据库分片值</span>
	 */
	private final String defaultCatalog;
	/**
	 * <span class="en-US">Database sharding strategy configure details</span>
	 * <span class="zh-CN">数据库分片配置详情</span>
	 */
	private final StrategyDetails databaseStrategy;
	/**
	 * <span class="en-US">Table sharding strategy configure details</span>
	 * <span class="zh-CN">数据表分片配置详情</span>
	 */
	private final StrategyDetails tableStrategy;

	/**
	 * <h3 class="en-US">Constructor method for sharding configure information</h3>
	 * <h3 class="zh-CN">分片配置信息的构造方法</h3>
	 *
	 * @param catalog          <span class="en-US">Database shard value</span>
	 *                         <span class="zh-CN">所属数据库分片</span>
	 * @param defaultCatalog   <span class="en-US">Default database sharding value</span>
	 *                         <span class="zh-CN">默认数据库分片值</span>
	 * @param tableName        <span class="en-US">Data table name</span>
	 *                         <span class="zh-CN">数据表名</span>
	 * @param databaseStrategy <span class="en-US">Database strategy defines information</span>
	 *                         <span class="zh-CN">数据库分片规则定义信息</span>
	 * @param tableStrategy    <span class="en-US">Data table strategy defines information</span>
	 *                         <span class="zh-CN">数据表分片规则定义信息</span>
	 */
	public StrategyConfig(@Nonnull final String catalog, final String defaultCatalog, final String tableName,
	                      final StrategyDefine databaseStrategy, final StrategyDefine tableStrategy) {
		this.tableName = tableName;
		this.defaultCatalog = StringUtils.isEmpty(catalog) ? defaultCatalog : catalog;
		if (databaseStrategy == null) {
			this.databaseStrategy = null;
		} else {
			this.databaseStrategy = new StrategyDetails(databaseStrategy);
		}
		if (tableStrategy != null && StringUtils.containsIgnoreCase(this.tableName, "{sharding}")) {
			this.tableStrategy = new StrategyDetails(tableStrategy);
		} else {
			this.tableStrategy = null;
		}
	}

	/**
	 * <h3 class="en-US">Calculate sharding result</h3>
	 * <h3 class="zh-CN">计算分片值</h3>
	 *
	 * @param dataMap <span class="en-US">Data objects that need to be sharded</span>
	 *                <span class="zh-CN">需要分片的数据对象</span>
	 * @return <span class="en-US">Calculate result</span>
	 * <span class="zh-CN">计算结果</span>
	 */
	public String dbKey(@Nonnull final Map<String, Object> dataMap) {
		return (this.databaseStrategy == null) ? this.defaultCatalog : this.databaseStrategy.result(dataMap);
	}

	/**
	 * <h3 class="en-US">Calculate sharding result</h3>
	 * <h3 class="zh-CN">计算分片值</h3>
	 *
	 * @param dataMap <span class="en-US">Data objects that need to be sharded</span>
	 *                <span class="zh-CN">需要分片的数据对象</span>
	 * @return <span class="en-US">Calculate result</span>
	 * <span class="zh-CN">计算结果</span>
	 */
	public List<String> dbKeys(@Nonnull final Map<String, Object> dataMap) {
		return (this.databaseStrategy == null) ? Collections.singletonList(this.defaultCatalog) : this.databaseStrategy.keys(dataMap);
	}

	/**
	 * <h3 class="en-US">Get the list of the calculated sharding result</h3>
	 * <h3 class="zh-CN">获取分片值列表</h3>
	 *
	 * @param conditionList <span class="en-US">Query condition instance list</span>
	 *                      <span class="zh-CN">查询条件实例对象列表</span>
	 * @return <span class="en-US">List of calculated sharding result</span>
	 * <span class="zh-CN">分片值列表</span>
	 */
	public List<String> dbKeys(@Nonnull final List<Condition> conditionList) {
		return (this.databaseStrategy == null)
				? Collections.singletonList(this.defaultCatalog)
				: this.databaseStrategy.keys(conditionList);
	}

	/**
	 * <h3 class="en-US">Matches sharding result</h3>
	 * <h3 class="zh-CN">匹配分片值</h3>
	 *
	 * @param string <span class="en-US">Sharding result</span>
	 *               <span class="zh-CN">分片值</span>
	 * @return <span class="en-US">Matches result</span>
	 * <span class="zh-CN">匹配结果</span>
	 */
	public boolean dbMatch(final String string) {
		return (this.databaseStrategy == null) ? ObjectUtils.nullSafeEquals(this.defaultCatalog, string) : this.databaseStrategy.match(string);
	}

	/**
	 * <h3 class="en-US">Calculate sharding result</h3>
	 * <h3 class="zh-CN">计算分片值</h3>
	 *
	 * @param dataMap <span class="en-US">Data objects that need to be sharded</span>
	 *                <span class="zh-CN">需要分片的数据对象</span>
	 * @return <span class="en-US">Calculate result</span>
	 * <span class="zh-CN">计算结果</span>
	 */
	public String tableKey(@Nonnull final Map<String, Object> dataMap) {
		if (this.tableStrategy == null) {
			return this.tableName;
		}
		return this.sharding(this.tableStrategy.result(dataMap));
	}

	/**
	 * <h3 class="en-US">Calculate sharding result</h3>
	 * <h3 class="zh-CN">计算分片值</h3>
	 *
	 * @param conditionList <span class="en-US">Query condition instance list</span>
	 *                      <span class="zh-CN">查询条件实例对象列表</span>
	 * @return <span class="en-US">Calculate result</span>
	 * <span class="zh-CN">计算结果</span>
	 */
	public String tableKey(@Nonnull final List<Condition> conditionList) {
		if (this.tableStrategy == null) {
			return this.tableName;
		}
		return this.sharding(this.tableStrategy.result(conditionList));
	}

	/**
	 * <h3 class="en-US">Calculate sharding result</h3>
	 * <h3 class="zh-CN">计算分片值</h3>
	 *
	 * @param dataMap <span class="en-US">Data objects that need to be sharded</span>
	 *                <span class="zh-CN">需要分片的数据对象</span>
	 * @return <span class="en-US">Calculate result</span>
	 * <span class="zh-CN">计算结果</span>
	 */
	public List<String> tableKeys(@Nonnull final Map<String, Object> dataMap) {
		return (this.tableStrategy == null) ? Collections.singletonList(this.sharding(Globals.DEFAULT_VALUE_STRING)) : this.tableStrategy.keys(dataMap);
	}

	/**
	 * <h3 class="en-US">Get the list of the calculated sharding result</h3>
	 * <h3 class="zh-CN">获取分片值列表</h3>
	 *
	 * @param conditionList <span class="en-US">Query condition instance list</span>
	 *                      <span class="zh-CN">查询条件实例对象列表</span>
	 * @return <span class="en-US">List of calculated sharding result</span>
	 * <span class="zh-CN">分片值列表</span>
	 */
	public List<String> tableKeys(@Nonnull final List<Condition> conditionList) {
		return (this.tableStrategy == null)
				? Collections.singletonList(this.sharding(Globals.DEFAULT_VALUE_STRING))
				: this.tableStrategy.keys(conditionList);
	}

	private String sharding(final String shardingKey) {
		if (StringUtils.containsIgnoreCase(this.tableName, "{sharding}")) {
			return Optional.ofNullable(shardingKey)
					.filter(StringUtils::notBlank)
					.map(sharding -> StringUtils.replace(this.tableName, "{sharding}", sharding))
					.orElse(StringUtils.replace(this.tableName, "{sharding}", this.tableStrategy.defaultValue));
		}
		return this.tableName;
	}

	/**
	 * <h3 class="en-US">Matches sharding result</h3>
	 * <h3 class="zh-CN">匹配分片值</h3>
	 *
	 * @param string <span class="en-US">Sharding result</span>
	 *               <span class="zh-CN">分片值</span>
	 * @return <span class="en-US">Matches result</span>
	 * <span class="zh-CN">匹配结果</span>
	 */
	public boolean tableMatch(final String string) {
		return (this.tableStrategy == null) ? ObjectUtils.nullSafeEquals(this.tableName, string) : this.tableStrategy.match(string);
	}

	/**
	 * <h3 class="en-US">Data table sharding flag</h3>
	 * <h3 class="zh-CN">数据表分片标记</h3>
	 *
	 * @return <span class="en-US">Matches result</span>
	 * <span class="zh-CN">匹配结果</span>
	 */
	public boolean shardingTable() {
		return this.tableStrategy != null;
	}

	/**
	 * <h2 class="en-US">Sharding configure details</h2>
	 * <h2 class="zh-CN">分片配置详情</h2>
	 *
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision: 1.0.0 $ $Date: Nov 12, 2020 17:02:27 $
	 */
	private static final class StrategyDetails {

		/**
		 * <span class="en-US">Sharding default value</span>
		 * <span class="zh-CN">分片默认值</span>
		 */
		private final String defaultValue;
		/**
		 * <span class="en-US">Maximum index value</span>
		 * <span class="zh-CN">最大索引值</span>
		 */
		private final int maximumIndex;
		/**
		 * <span class="en-US">Data column name and index mapping</span>
		 * <span class="zh-CN">数据列名称索引映射表</span>
		 */
		private final Map<Integer, String> fieldsMap;
		/**
		 * <span class="en-US">Sharding value calculator instance object</span>
		 * <span class="zh-CN">分片值计算器实例对象</span>
		 */
		private final Class<?> calculatorClass;

		/**
		 * <h3 class="en-US">Constructor method for sharding configure information</h3>
		 * <h3 class="zh-CN">分片配置信息的构造方法</h3>
		 *
		 * @param strategyDefine <span class="en-US">Sharding strategy defines information</span>
		 *                       <span class="zh-CN">分片战略配置信息</span>
		 */
		StrategyDetails(@Nonnull final StrategyDefine strategyDefine) {
			this.defaultValue = strategyDefine.getDefaultValue();
			this.fieldsMap = new HashMap<>();
			AtomicInteger maximumIndex = new AtomicInteger(Globals.INITIALIZE_INT_VALUE);
			strategyDefine.getStrategyFields()
					.forEach(strategyField -> {
						this.fieldsMap.put(strategyField.getSortCode(), strategyField.getFieldName());
						if (maximumIndex.get() < strategyField.getSortCode()) {
							maximumIndex.set(strategyField.getSortCode());
						}
					});
			this.maximumIndex = maximumIndex.get();
			this.calculatorClass = ClassUtils.forName(strategyDefine.getCalculatorClass());
		}

		/**
		 * <h3 class="en-US">Calculate sharding result</h3>
		 * <h3 class="zh-CN">计算分片值</h3>
		 *
		 * @param dataMap <span class="en-US">Data objects that need to be sharded</span>
		 *                <span class="zh-CN">需要分片的数据对象</span>
		 * @return <span class="en-US">Calculate result</span>
		 * <span class="zh-CN">计算结果</span>
		 */
		String result(@Nonnull final Map<String, Object> dataMap) {
			if (dataMap.isEmpty()) {
				return this.defaultValue;
			}
			Object[] arguments = new Object[this.maximumIndex];
			this.fieldsMap.forEach((index, fieldName) -> arguments[index] = dataMap.get(fieldName));
			return Optional.ofNullable(newInstance(this.calculatorClass))
					.map(calculator -> calculator.result(arguments))
					.orElse(this.defaultValue);
		}

		/**
		 * <h3 class="en-US">Calculate sharding result</h3>
		 * <h3 class="zh-CN">计算分片值</h3>
		 *
		 * @param conditionList <span class="en-US">Query condition instance list</span>
		 *                      <span class="zh-CN">查询条件实例对象列表</span>
		 * @return <span class="en-US">Calculate result</span>
		 * <span class="zh-CN">计算结果</span>
		 */
		String result(@Nonnull final List<Condition> conditionList) {
			if (conditionList.isEmpty()) {
				return this.defaultValue;
			}
			return Optional.ofNullable(newInstance(this.calculatorClass))
					.map(calculator -> calculator.result(conditionList))
					.orElse(this.defaultValue);
		}

		/**
		 * <h3 class="en-US">Get the list of the calculated sharding result</h3>
		 * <h3 class="zh-CN">获取分片值列表</h3>
		 *
		 * @param dataMap <span class="en-US">Data objects that need to be sharded</span>
		 *                <span class="zh-CN">需要分片的数据对象</span>
		 * @return <span class="en-US">List of calculated sharding result</span>
		 * <span class="zh-CN">分片值列表</span>
		 */
		List<String> keys(@Nonnull final Map<String, Object> dataMap) {
			if (dataMap.isEmpty()) {
				return Collections.singletonList(this.defaultValue);
			}
			return Optional.ofNullable(newInstance(this.calculatorClass))
					.map(calculator -> calculator.keys(dataMap))
					.orElse(Collections.singletonList(this.defaultValue));
		}

		/**
		 * <h3 class="en-US">Get the list of the calculated sharding result</h3>
		 * <h3 class="zh-CN">获取分片值列表</h3>
		 *
		 * @param conditionList <span class="en-US">Query condition instance list</span>
		 *                      <span class="zh-CN">查询条件实例对象列表</span>
		 * @return <span class="en-US">List of calculated sharding result</span>
		 * <span class="zh-CN">分片值列表</span>
		 */
		List<String> keys(@Nonnull final List<Condition> conditionList) {
			if (conditionList.isEmpty()) {
				return Collections.singletonList(this.defaultValue);
			}
			return Optional.ofNullable(newInstance(this.calculatorClass))
					.map(calculator -> calculator.keys(conditionList))
					.orElse(Collections.singletonList(this.defaultValue));
		}

		/**
		 * <h3 class="en-US">Matches sharding result</h3>
		 * <h3 class="zh-CN">匹配分片值</h3>
		 *
		 * @param string <span class="en-US">Sharding result</span>
		 *               <span class="zh-CN">分片值</span>
		 * @return <span class="en-US">Matches result</span>
		 * <span class="zh-CN">匹配结果</span>
		 */
		boolean match(final String string) {
			return Optional.ofNullable(newInstance(this.calculatorClass))
					.map(calculator -> calculator.matches(string))
					.orElse(Boolean.FALSE);
		}
	}

	/**
	 * <h3 class="en-US">Generate calculator instance object</h3>
	 * <h3 class="zh-CN">生成计算器实例对象</h3>
	 *
	 * @return <span class="en-US">Generated instance object</span>
	 * <span class="zh-CN">生成的实例对象</span>
	 */
	private static Calculator newInstance(final Class<?> calculatorClass) {
		return (Calculator) ObjectUtils.newInstance(calculatorClass);
	}
}
