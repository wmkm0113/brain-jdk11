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
package org.nervousync.brain.query.join;

import jakarta.xml.bind.annotation.*;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.enumerations.core.ConnectionCode;
import org.nervousync.utils.StringUtils;

/**
 * <h2 class="en-US">Join column define</h2>
 * <h2 class="zh-CN">关联列信息定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Feb 17, 2021 16:35:51 $
 */
@XmlType(name = "join_info", namespace = "https://nervousync.org/schemas/brain")
@XmlRootElement(name = "join_info", namespace = "https://nervousync.org/schemas/brain")
@XmlAccessorType(XmlAccessType.NONE)
public final class JoinInfo extends BeanObject {
	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = 8163690027798389179L;

	/**
	 * <span class="en-US">Query connection code</span>
	 * <span class="zh-CN">查询条件连接代码</span>
	 */
	@XmlElement(name = "connection_code")
	private ConnectionCode connectionCode;
	/**
	 * <span class="en-US">Left table name</span>
	 * <span class="zh-CN">左表名</span>
	 */
	@XmlElement(name = "left_table")
	private String leftTable;
	/**
	 * <span class="en-US">Left table data column identify code</span>
	 * <span class="zh-CN">左表数据列识别代码</span>
	 */
	@XmlElement(name = "left_key")
	private String leftKey;
	/**
	 * <span class="en-US">Right table data column identify code</span>
	 * <span class="zh-CN">右表数据列识别代码</span>
	 */
	@XmlElement(name = "right_key")
	private String rightKey;

	/**
	 * <h3 class="en-US">Constructor method for join column define</h3>
	 * <h3 class="zh-CN">关联列信息定义的构造方法</h3>
	 */
	public JoinInfo() {
	}

	/**
	 * <h3 class="en-US">Static method is used to generate join column information instance objects</h3>
	 * <h3 class="zh-CN">静态方法用于生成关联列信息实例对象</h3>
	 *
	 * @param leftTable <span class="en-US">Left table name</span>
	 *                  <span class="zh-CN">左表名</span>
	 * @param leftKey   <span class="en-US">Left table data column identify code</span>
	 *                  <span class="zh-CN">左表数据列识别代码</span>
	 * @param rightKey  <span class="en-US">Right table data column identify code</span>
	 *                  <span class="zh-CN">右表数据列识别代码</span>
	 * @return <span class="en-US">
	 * The generated associated column information instance object. If the data column
	 * identification code cannot find the corresponding data column definition,
	 * <code>null</code> will be returned.
	 * </span>
	 * <span class="zh-CN">生成的关联列信息实例对象，如果数据列识别代码不能找到对应的数据列定义则返回<code>null</code></span>
	 */
	public static JoinInfo newInstance(final String leftTable, final String leftKey, final String rightKey) {
		return newInstance(ConnectionCode.AND, leftTable, leftKey, rightKey);
	}

	/**
	 * <h3 class="en-US">Static method is used to generate join column information instance objects</h3>
	 * <h3 class="zh-CN">静态方法用于生成关联列信息实例对象</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 * @param leftTable      <span class="en-US">Left table name</span>
	 *                       <span class="zh-CN">左表名</span>
	 * @param leftKey        <span class="en-US">Left table data column identify code</span>
	 *                       <span class="zh-CN">左表数据列识别代码</span>
	 * @param rightKey       <span class="en-US">Right table data column identify code</span>
	 *                       <span class="zh-CN">右表数据列识别代码</span>
	 * @return <span class="en-US">
	 * The generated associated column information instance object. If the data column
	 * identification code cannot find the corresponding data column definition,
	 * <code>null</code> will be returned.
	 * </span>
	 * <span class="zh-CN">生成的关联列信息实例对象，如果数据列识别代码不能找到对应的数据列定义则返回<code>null</code></span>
	 */
	public static JoinInfo newInstance(final ConnectionCode connectionCode,
	                                   final String leftTable, final String leftKey, final String rightKey) {
		JoinInfo joinInfo = null;
		if (StringUtils.notBlank(leftTable) && StringUtils.notBlank(leftKey) && StringUtils.notBlank(rightKey)) {
			joinInfo = new JoinInfo();
			joinInfo.setConnectionCode(connectionCode);
			joinInfo.setLeftTable(leftTable);
			joinInfo.setLeftKey(leftKey);
			joinInfo.setRightKey(rightKey);
		}
		return joinInfo;
	}

	/**
	 * <h3 class="en-US">Getter method for query connection code</h3>
	 * <h3 class="zh-CN">查询条件连接代码的Getter方法</h3>
	 *
	 * @return <span class="en-US">Query connection code</span>
	 * <span class="zh-CN">查询条件连接代码</span>
	 */
	public ConnectionCode getConnectionCode() {
		return connectionCode;
	}

	/**
	 * <h3 class="en-US">Setter method for query connection code</h3>
	 * <h3 class="zh-CN">查询条件连接代码的Setter方法</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 */
	public void setConnectionCode(ConnectionCode connectionCode) {
		this.connectionCode = connectionCode;
	}

	/**
	 * <h3 class="en-US">Getter method for the left table name</h3>
	 * <h3 class="zh-CN">左表名的Getter方法</h3>
	 *
	 * @return <span class="en-US">Driven table name</span>
	 * <span class="zh-CN">左表名</span>
	 */
	public String getLeftTable() {
		return this.leftTable;
	}

	/**
	 * <h3 class="en-US">Setter method for the left table name</h3>
	 * <h3 class="zh-CN">左表名的Setter方法</h3>
	 *
	 * @param leftTable <span class="en-US">Left table name</span>
	 *                  <span class="zh-CN">左表名</span>
	 */
	public void setLeftTable(final String leftTable) {
		this.leftTable = leftTable;
	}

	/**
	 * <h3 class="en-US">Getter method for the left table data column identify code</h3>
	 * <h3 class="zh-CN">左表数据列识别代码的Getter方法</h3>
	 *
	 * @return <span class="en-US">Left table data column identify code</span>
	 * <span class="zh-CN">左表数据列识别代码</span>
	 */
	public String getLeftKey() {
		return this.leftKey;
	}

	/**
	 * <h3 class="en-US">Setter method for the left table data column identify code</h3>
	 * <h3 class="zh-CN">左表数据列识别代码的Setter方法</h3>
	 *
	 * @param leftKey <span class="en-US">Left table data column identify code</span>
	 *                <span class="zh-CN">左表数据列识别代码</span>
	 */
	public void setLeftKey(final String leftKey) {
		this.leftKey = leftKey;
	}

	/**
	 * <h3 class="en-US">Getter method for the right table data column identify code</h3>
	 * <h3 class="zh-CN">右表数据列识别代码的Getter方法</h3>
	 *
	 * @return <span class="en-US">Join table data column identify code</span>
	 * <span class="zh-CN">右表数据列识别代码</span>
	 */
	public String getRightKey() {
		return this.rightKey;
	}

	/**
	 * <h3 class="en-US">Setter method for the join table data column identify code</h3>
	 * <h3 class="zh-CN">右表数据列识别代码的Setter方法</h3>
	 *
	 * @param rightKey <span class="en-US">Right table data column identify code</span>
	 *                 <span class="zh-CN">右表数据列识别代码</span>
	 */
	public void setRightKey(final String rightKey) {
		this.rightKey = rightKey;
	}
}
