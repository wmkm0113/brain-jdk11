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

package org.nervousync.brain.query.condition;

import jakarta.xml.bind.annotation.*;
import org.nervousync.brain.enumerations.query.ConditionType;
import org.nervousync.brain.enumerations.query.ConnectionCode;
import org.nervousync.brain.query.condition.impl.ColumnCondition;
import org.nervousync.brain.query.condition.impl.GroupCondition;
import org.nervousync.brain.query.core.SortedItem;
import org.nervousync.utils.core.ClassUtils;

import java.sql.SQLException;
import java.sql.Wrapper;

/**
 * <h2 class="en-US">Abstract class for query condition information define</h2>
 * <h2 class="zh-CN">查询匹配条件定义抽象类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 9, 2020 19:10:21 $
 */
@XmlTransient
@XmlSeeAlso({ColumnCondition.class, GroupCondition.class})
@XmlAccessorType(XmlAccessType.NONE)
public abstract class Condition extends SortedItem implements Wrapper {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = -1483012369723138515L;

	/**
	 * <span class="en-US">Query condition type enumeration value</span>
	 * <span class="zh-CN">查询条件类型枚举值</span>
	 */
	@XmlElement(name = "condition_type")
	private final ConditionType conditionType;
	/**
	 * <span class="en-US">Query connection code</span>
	 * <span class="zh-CN">查询条件连接代码</span>
	 */
	@XmlElement(name = "connection_code")
	private ConnectionCode connectionCode = ConnectionCode.AND;

	/**
	 * <h3 class="en-US">Constructor method for query condition information define</h3>
	 * <h3 class="zh-CN">查询匹配条件定义的构造方法</h3>
	 *
	 * @param conditionType <span class="en-US">Query condition type enumeration value</span>
	 *                      <span class="zh-CN">查询条件类型枚举值</span>
	 */
	protected Condition(final ConditionType conditionType) {
		this.conditionType = conditionType;
	}

	/**
	 * <h3 class="en-US">Getter method for query condition type enumeration value</h3>
	 * <h3 class="zh-CN">查询条件类型枚举值的Getter方法</h3>
	 *
	 * @return <span class="en-US">Query condition type enumeration value</span>
	 * <span class="zh-CN">查询条件类型枚举值</span>
	 */
	public ConditionType getConditionType() {
		return this.conditionType;
	}

	/**
	 * <h3 class="en-US">Getter method for query connection code</h3>
	 * <h3 class="zh-CN">查询条件连接代码的Getter方法</h3>
	 *
	 * @return <span class="en-US">Query connection code</span>
	 * <span class="zh-CN">查询条件连接代码</span>
	 */
	public final ConnectionCode getConnectionCode() {
		return connectionCode;
	}

	/**
	 * <h3 class="en-US">Setter method for query connection code</h3>
	 * <h3 class="zh-CN">查询条件连接代码的Setter方法</h3>
	 *
	 * @param connectionCode <span class="en-US">Query connection code</span>
	 *                       <span class="zh-CN">查询条件连接代码</span>
	 */
	public final void setConnectionCode(ConnectionCode connectionCode) {
		this.connectionCode = connectionCode;
	}

	@Override
	public final <T> T unwrap(final Class<T> clazz) throws SQLException {
		try {
			return clazz.cast(this);
		} catch (ClassCastException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public final boolean isWrapperFor(final Class<?> clazz) {
		return ClassUtils.isAssignable(clazz, this.getClass());
	}
}
