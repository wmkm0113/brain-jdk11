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

/**
 * <h2 class="en-US">Sharding configure information</h2>
 * <h2 class="zh-CN">分片配置信息</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Mar 23, 2010 10:13:02 $
 */
public final class ShardingDefine<T> {

	/**
	 * <span class="en-US">Sharding default value</span>
	 * <span class="zh-CN">分片默认值</span>
	 */
	private final String defaultValue;
	/**
	 * <span class="en-US">Data column name</span>
	 * <span class="zh-CN">数据列名称</span>
	 */
	private final String columnName;
	/**
	 * <span class="en-US">Sharding result template</span>
	 * <span class="zh-CN">分片结果模板</span>
	 */
	private final String shardingTemplate;
	/**
	 * <span class="en-US">Sharding calculator implementation class</span>
	 * <span class="zh-CN">分片计算器实现类</span>
	 */
	private final Class<?> calculatorClass;
	/**
	 * <span class="en-US">Column data type</span>
	 * <span class="zh-CN">数据列类型</span>
	 */
	private final Class<T> fieldType;

	/**
	 * <h3 class="en-US">Constructor method for sharding configure information</h3>
	 * <h3 class="zh-CN">分片配置信息的构造方法</h3>
	 *
	 * @param defaultValue     <span class="en-US">Sharding default value</span>
	 *                         <span class="zh-CN">分片默认值</span>
	 * @param columnName       <span class="en-US">Data column name</span>
	 *                         <span class="zh-CN">数据列名称</span>
	 * @param shardingTemplate <span class="en-US">Sharding result template</span>
	 *                         <span class="zh-CN">分片结果模板</span>
	 * @param calculatorClass  <span class="en-US">Sharding calculator implementation class</span>
	 *                         <span class="zh-CN">分片计算器实现类</span>
	 * @param fieldType        <span class="en-US">Column data type</span>
	 *                         <span class="zh-CN">数据列类型</span>
	 */
	public ShardingDefine(final String defaultValue, final String columnName, final String shardingTemplate,
	                      final Class<?> calculatorClass, final Class<T> fieldType) {
		this.defaultValue = defaultValue;
		this.columnName = columnName;
		this.shardingTemplate = shardingTemplate;
		this.calculatorClass = calculatorClass;
		this.fieldType = fieldType;
	}

	/**
	 * <h3 class="en-US">Getter method for sharding default value</h3>
	 * <h3 class="zh-CN">分片默认值的Getter方法</h3>
	 *
	 * @return <span class="en-US">Sharding default value</span>
	 * <span class="zh-CN">分片默认值</span>
	 */
	public String getDefaultValue() {
		return this.defaultValue;
	}

	/**
	 * <h3 class="en-US">Getter method for data column name</h3>
	 * <h3 class="zh-CN">数据列名称的Getter方法</h3>
	 *
	 * @return <span class="en-US">Data column name</span>
	 * <span class="zh-CN">数据列名称</span>
	 */
	public String getColumnName() {
		return this.columnName;
	}

	/**
	 * <h3 class="en-US">Getter method for sharding result template</h3>
	 * <h3 class="zh-CN">分片结果模板的Getter方法</h3>
	 *
	 * @return <span class="en-US">Sharding result template</span>
	 * <span class="zh-CN">分片结果模板</span>
	 */
	public String getShardingTemplate() {
		return this.shardingTemplate;
	}

	/**
	 * <h3 class="en-US">Getter method for sharding calculator implementation class</h3>
	 * <h3 class="zh-CN">分片计算器实现类的Getter方法</h3>
	 *
	 * @return <span class="en-US">Sharding calculator implementation class</span>
	 * <span class="zh-CN">分片计算器实现类</span>
	 */
	public Class<?> getCalculatorClass() {
		return this.calculatorClass;
	}

	/**
	 * <h3 class="en-US">Getter method for column data type</h3>
	 * <h3 class="zh-CN">数据列类型的Getter方法</h3>
	 *
	 * @return <span class="en-US">Column data type</span>
	 * <span class="zh-CN">数据列类型</span>
	 */
	public Class<T> getFieldType() {
		return this.fieldType;
	}
}
