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

import jakarta.annotation.Nonnull;
import jakarta.xml.bind.annotation.*;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.brain.enumerations.query.ConditionCode;
import org.nervousync.brain.enumerations.query.ConnectionCode;
import org.nervousync.utils.ObjectUtils;

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
	 * <span class="en-US">Query condition code</span>
	 * <span class="zh-CN">查询条件运算代码</span>
	 */
	@XmlElement(name = "condition_code")
	private ConditionCode conditionCode;
	/**
	 * <span class="en-US">Left table identify code</span>
	 * <span class="zh-CN">左表识别代码</span>
	 */
	@XmlElement(name = "left_identify")
	private String leftIdentify;
	/**
	 * <span class="en-US">Left table data column identify code</span>
	 * <span class="zh-CN">左表数据列识别代码</span>
	 */
	@XmlElement(name = "left_key")
	private String leftKey;
	/**
	 * <span class="en-US">Right table identify code</span>
	 * <span class="zh-CN">右表识别代码</span>
	 */
	@XmlElement(name = "right_identify")
	private String rightIdentify;
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
	 * <h3 class="en-US">Getter method for the left table identify code</h3>
	 * <h3 class="zh-CN">左表识别代码的Getter方法</h3>
	 *
	 * @return <span class="en-US">Left table identify code</span>
	 * <span class="zh-CN">左表识别代码</span>
	 */
	public String getLeftIdentify() {
		return this.leftIdentify;
	}

	/**
	 * <h3 class="en-US">Setter method for the left table identify code</h3>
	 * <h3 class="zh-CN">左表识别代码的Setter方法</h3>
	 *
	 * @param leftIdentify <span class="en-US">Left table identify code</span>
	 *                     <span class="zh-CN">左表识别代码</span>
	 */
	public void setLeftIdentify(final String leftIdentify) {
		this.leftIdentify = leftIdentify;
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
	 * <h3 class="en-US">Getter method for the right table identify code</h3>
	 * <h3 class="zh-CN">右表识别代码的Getter方法</h3>
	 *
	 * @return <span class="en-US">Right table identify code</span>
	 * <span class="zh-CN">右表识别代码</span>
	 */
	public String getRightIdentify() {
		return this.rightIdentify;
	}

	/**
	 * <h3 class="en-US">Setter method for the right table identify code</h3>
	 * <h3 class="zh-CN">右表识别代码的Setter方法</h3>
	 *
	 * @param rightIdentify <span class="en-US">Right table identify code</span>
	 *                      <span class="zh-CN">右表识别代码</span>
	 */
	public void setRightIdentify(final String rightIdentify) {
		this.rightIdentify = rightIdentify;
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

	/**
	 * <h3 class="en-US">Checks whether the given association information is consistent with the current association information</h3>
	 * <h3 class="zh-CN">检查给定的关联信息是否与当前关联信息一致</h3>
	 *
	 * @param conditionCode <span class="en-US">Query condition code</span>
	 *                      <span class="zh-CN">查询条件运算代码</span>
	 * @param leftIdentify  <span class="en-US">Left table identify code</span>
	 *                      <span class="zh-CN">左表识别代码</span>
	 * @param leftKey       <span class="en-US">Left table data column identify code</span>
	 *                      <span class="zh-CN">左表数据列识别代码</span>
	 * @param rightIdentify <span class="en-US">Right table identify code</span>
	 *                      <span class="zh-CN">右表识别代码</span>
	 * @param rightKey      <span class="en-US">Right table data column identify code</span>
	 *                      <span class="zh-CN">右表数据列识别代码</span>
	 * @return <span class="en-US">Check result</span>
	 * <span class="zh-CN">检查结果</span>
	 */
	public boolean match(final ConditionCode conditionCode,
	                     @Nonnull final String leftIdentify, @Nonnull final String leftKey,
	                     @Nonnull final String rightIdentify, @Nonnull final String rightKey) {
		return ObjectUtils.nullSafeEquals(this.conditionCode, conditionCode)
				&& ObjectUtils.nullSafeEquals(this.leftIdentify, leftIdentify)
				&& ObjectUtils.nullSafeEquals(this.leftKey, leftKey)
				&& ObjectUtils.nullSafeEquals(this.rightIdentify, rightIdentify)
				&& ObjectUtils.nullSafeEquals(this.rightKey, rightKey);
	}
}
