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

package org.nervousync.brain.query.condition.impl;

import jakarta.xml.bind.annotation.*;
import org.nervousync.brain.enumerations.query.ConditionCode;
import org.nervousync.brain.enumerations.query.ConditionType;
import org.nervousync.brain.query.condition.Condition;
import org.nervousync.brain.query.param.AbstractParameter;
import org.nervousync.brain.query.param.impl.*;

/**
 * <h2 class="en-US">Query column condition information define</h2>
 * <h2 class="zh-CN">数据列查询匹配条件定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 9, 2020 19:12:02 $
 */
@XmlType(name = "column_condition", namespace = "https://nervousync.org/schemas/query")
@XmlRootElement(name = "column_condition", namespace = "https://nervousync.org/schemas/query")
@XmlAccessorType(XmlAccessType.NONE)
public final class ColumnCondition extends Condition {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = 7198216674051216778L;

	/**
	 * <span class="en-US">Query condition code</span>
	 * <span class="zh-CN">查询条件运算代码</span>
	 */
	@XmlElement(name = "condition_code")
	private ConditionCode conditionCode;
	/**
	 * <span class="en-US">Data table name</span>
	 * <span class="zh-CN">数据表名</span>
	 */
	@XmlElement(name = "table_name")
	private String tableName;
	/**
	 * <span class="en-US">Data column name</span>
	 * <span class="zh-CN">数据列名</span>
	 */
	@XmlElement(name = "column_name")
	private String columnName;
	/**
	 * <span class="en-US">Execute function name</span>
	 * <span class="zh-CN">运算函数名</span>
	 */
	@XmlElement(name = "function_name")
	private String functionName;
	/**
	 * <span class="en-US">Match condition</span>
	 * <span class="zh-CN">匹配结果</span>
	 */
	@XmlElements({
			@XmlElement(name = "arrays_parameter", type = ArraysParameter.class),
			@XmlElement(name = "column_parameter", type = ColumnParameter.class),
			@XmlElement(name = "constant_parameter", type = ConstantParameter.class),
			@XmlElement(name = "function_parameter", type = FunctionParameter.class),
			@XmlElement(name = "query_parameter", type = QueryParameter.class),
			@XmlElement(name = "ranges_parameter", type = RangesParameter.class)
	})
	private AbstractParameter<?> conditionParameter;

	/**
	 * <h3 class="en-US">Constructor method for query column condition information define</h3>
	 * <h3 class="zh-CN">数据列查询匹配条件定义的构造方法</h3>
	 */
	public ColumnCondition() {
		super(ConditionType.COLUMN);
	}

	/**
	 * <h3 class="en-US">Getter method for query condition code</h3>
	 * <h3 class="zh-CN">查询条件运算代码的Getter方法</h3>
	 *
	 * @return <span class="en-US">Query condition code</span>
	 * <span class="zh-CN">查询条件运算代码</span>
	 */
	public ConditionCode getConditionCode() {
		return conditionCode;
	}

	/**
	 * <h3 class="en-US">Setter method for query condition code</h3>
	 * <h3 class="zh-CN">查询条件运算代码的Setter方法</h3>
	 *
	 * @param conditionCode <span class="en-US">Query condition code</span>
	 *                      <span class="zh-CN">查询条件运算代码</span>
	 */
	public void setConditionCode(final ConditionCode conditionCode) {
		this.conditionCode = conditionCode;
	}

	/**
	 * <h3 class="en-US">Getter method for data table name</h3>
	 * <h3 class="zh-CN">数据表名的Getter方法</h3>
	 *
	 * @return <span class="en-US">Data table name</span>
	 * <span class="zh-CN">数据表名</span>
	 */
	public String getTableName() {
		return this.tableName;
	}

	/**
	 * <h3 class="en-US">Setter method for data table name</h3>
	 * <h3 class="zh-CN">数据表名的Setter方法</h3>
	 *
	 * @param tableName <span class="en-US">Data table name</span>
	 *                  <span class="zh-CN">数据表名</span>
	 */
	public void setTableName(final String tableName) {
		this.tableName = tableName;
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
	 * <h3 class="en-US">Getter method for execute function name</h3>
	 * <h3 class="zh-CN">运算函数名的Getter方法</h3>
	 *
	 * @return <span class="en-US">Execute function name</span>
	 * <span class="zh-CN">运算函数名</span>
	 */
	public String getFunctionName() {
		return this.functionName;
	}

	/**
	 * <h3 class="en-US">Setter method for execute function name</h3>
	 * <h3 class="zh-CN">运算函数名的Setter方法</h3>
	 *
	 * @param functionName <span class="en-US">Execute function name</span>
	 *                     <span class="zh-CN">运算函数名</span>
	 */
	public void setFunctionName(final String functionName) {
		this.functionName = functionName;
	}

	/**
	 * <h3 class="en-US">Getter method for match condition</h3>
	 * <h3 class="zh-CN">匹配结果的Getter方法</h3>
	 *
	 * @return <span class="en-US">Match condition</span>
	 * <span class="zh-CN">匹配结果</span>
	 */
	public AbstractParameter<?> getConditionParameter() {
		return this.conditionParameter;
	}

	/**
	 * <h3 class="en-US">Setter method for match condition</h3>
	 * <h3 class="zh-CN">匹配结果的Setter方法</h3>
	 *
	 * @param conditionParameter <span class="en-US">Match condition</span>
	 *                           <span class="zh-CN">匹配结果</span>
	 */
	public void setConditionParameter(final AbstractParameter<?> conditionParameter) {
		this.conditionParameter = conditionParameter;
	}
}
