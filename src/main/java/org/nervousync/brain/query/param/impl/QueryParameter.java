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

package org.nervousync.brain.query.param.impl;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.nervousync.brain.enumerations.query.ItemType;
import org.nervousync.brain.query.data.QueryData;
import org.nervousync.brain.query.param.AbstractParameter;
import org.nervousync.commons.Globals;

/**
 * <h2 class="en-US">Sub-query parameter information define</h2>
 * <h2 class="zh-CN">子查询参数定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 9, 2020 11:44:57 $
 */
@XmlType(name = "query_parameter", namespace = "https://nervousync.org/schemas/brain")
@XmlRootElement(name = "query_parameter", namespace = "https://nervousync.org/schemas/brain")
public final class QueryParameter extends AbstractParameter<QueryData> {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = 8967884285195344549L;
	/**
	 * <span class="en-US">Function name of sub-query</span>
	 * <span class="zh-CN">子查询函数名</span>
	 */
	@XmlElement(name = "function_name")
	private String functionName = Globals.DEFAULT_VALUE_STRING;
	/**
	 * <span class="en-US">Parameter value</span>
	 * <span class="zh-CN">参数值</span>
	 */
	@XmlElement(name = "query_info", namespace = "https://nervousync.org/schemas/brain")
	private QueryData itemValue;

	/**
	 * <h3 class="en-US">Constructor method for sub-query parameter information define</h3>
	 * <h3 class="zh-CN">子查询参数定义的构造方法</h3>
	 */
	public QueryParameter() {
		super(ItemType.QUERY);
	}

	/**
	 * <h3 class="en-US">Getter method for function name of sub-query</h3>
	 * <h3 class="zh-CN">的Getter方法</h3>
	 *
	 * @return <span class="en-US">Function name of sub-query</span>
	 * <span class="zh-CN">子查询函数名子查询函数名</span>
	 */
	public String getFunctionName() {
		return this.functionName;
	}

	/**
	 * <h3 class="en-US">Setter method for function name of sub-query</h3>
	 * <h3 class="zh-CN">子查询函数名的Setter方法</h3>
	 *
	 * @param functionName <span class="en-US">Function name of sub-query</span>
	 *                     <span class="zh-CN">子查询函数名</span>
	 */
	public void setFunctionName(final String functionName) {
		this.functionName = functionName;
	}

	@Override
	public QueryData getItemValue() {
		return this.itemValue;
	}

	@Override
	public void setItemValue(final QueryData itemValue) {
		this.itemValue = itemValue;
	}
}
