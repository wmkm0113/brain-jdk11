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

import org.nervousync.commons.Globals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <h2 class="en-US">Sharding strategy defines information</h2>
 * <h2 class="zh-CN">分片战略配置信息</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Nov 4, 2020 16:33:28 $
 */
public final class StrategyDefine implements Serializable {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = 1007991703640577552L;

	/**
	 * <span class="en-US">Sharding default value</span>
	 * <span class="zh-CN">分片默认值</span>
	 */
	private String defaultValue = Globals.DEFAULT_VALUE_STRING;
	/**
	 * <span class="en-US">List of data column names</span>
	 * <span class="zh-CN">数据列名称列表</span>
	 */
	private List<StrategyField> strategyFields = new ArrayList<>();
	/**
	 * <span class="en-US">Sharding calculator implementation class name</span>
	 * <span class="zh-CN">分片计算器实现类名</span>
	 */
	private String calculatorClass = Globals.DEFAULT_VALUE_STRING;

	/**
	 * <h3 class="en-US">Getter method for the sharding default value</h3>
	 * <h3 class="zh-CN">分片默认值的Getter方法</h3>
	 *
	 * @return
	 * <span class="en-US">Sharding default value</span>
	 * <span class="zh-CN">分片默认值</span>
	 */
	public String getDefaultValue() {
		return this.defaultValue;
	}

	/**
	 * <h3 class="en-US">Setter method for the sharding default value</h3>
	 * <h3 class="zh-CN">分片默认值的Setter方法</h3>
	 *
	 * @param defaultValue
	 * <span class="en-US">Sharding default value</span>
	 * <span class="zh-CN">分片默认值</span>
	 */
	public void setDefaultValue(final String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * <h3 class="en-US">Getter method for the list of data column names</h3>
	 * <h3 class="zh-CN">数据列名称列表的Getter方法</h3>
	 *
	 * @return
	 * <span class="en-US">List of data column names</span>
	 * <span class="zh-CN">数据列名称列表</span>
	 */
	public List<StrategyField> getStrategyFields() {
		return this.strategyFields;
	}

	/**
	 * <h3 class="en-US">Setter method for the list of data column names</h3>
	 * <h3 class="zh-CN">数据列名称列表的Setter方法</h3>
	 *
	 * @param strategyFields
	 * <span class="en-US">List of data column names</span>
	 * <span class="zh-CN">数据列名称列表</span>
	 */
	public void setStrategyFields(final List<StrategyField> strategyFields) {
		this.strategyFields = strategyFields;
	}

	/**
	 * <h3 class="en-US">Getter method for the sharding calculator implementation class name</h3>
	 * <h3 class="zh-CN">分片计算器实现类名的Getter方法</h3>
	 *
	 * @return
	 * <span class="en-US">Sharding calculator implementation class name</span>
	 * <span class="zh-CN">分片计算器实现类名</span>
	 */
	public String getCalculatorClass() {
		return this.calculatorClass;
	}

	/**
	 * <h3 class="en-US">Setter method for the sharding calculator implementation class name</h3>
	 * <h3 class="zh-CN">分片计算器实现类名的Setter方法</h3>
	 *
	 * @param calculatorClass
	 * <span class="en-US">Sharding calculator implementation class name</span>
	 * <span class="zh-CN">分片计算器实现类名</span>
	 */
	public void setCalculatorClass(final String calculatorClass) {
		this.calculatorClass = calculatorClass;
	}
}
