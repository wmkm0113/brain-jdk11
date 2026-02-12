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

package org.nervousync.brain.query.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;
import org.nervousync.brain.enumerations.query.FromType;
import org.nervousync.utils.core.ClassUtils;

import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Wrapper;

/**
 * <h2 class="en-US">Abstract query from definition</h2>
 * <h2 class="zh-CN">抽象查询来源信息定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 9, 2020 11:30:54 $
 */
@XmlTransient
@XmlAccessorType(XmlAccessType.NONE)
public abstract class QueryFrom implements Wrapper, Serializable {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = -3942284033392495679L;

	/**
	 * <span class="en-US">Enumeration value of the query from type</span>
	 * <span class="zh-CN">查询来源类型的枚举值</span>
	 */
	@JsonIgnore
	private final FromType fromType;
	/**
	 * <span class="en-US">Item alias name</span>
	 * <span class="zh-CN">查询项别名</span>
	 */
	@XmlElement(name = "alias_name")
	private String aliasName;

	/**
	 * <h3 class="en-US">Protect constructor method for the abstract query from definition</h3>
	 * <h3 class="zh-CN">抽象查询来源信息定义的构造方法</h3>
	 *
	 * @param fromType <span class="en-US">Enumeration value of the query from type</span>
	 *                 <span class="zh-CN">查询来源类型的枚举值</span>
	 */
	protected QueryFrom(final FromType fromType) {
		this.fromType = fromType;
	}

	/**
	 * <h3 class="en-US">Getter method for the enumeration value of the query from type</h3>
	 * <h3 class="zh-CN">查询来源类型枚举值的Getter方法</h3>
	 *
	 * @return <span class="en-US">Enumeration value of the query from type</span>
	 * <span class="zh-CN">查询来源类型的枚举值</span>
	 */
	public FromType getFromType() {
		return this.fromType;
	}

	/**
	 * <h3 class="en-US">Getter method for alias name</h3>
	 * <h3 class="zh-CN">别名的Getter方法</h3>
	 *
	 * @return <span class="en-US">Alias name</span>
	 * <span class="zh-CN">别名</span>
	 */
	public String getAliasName() {
		return aliasName;
	}

	/**
	 * <h3 class="en-US">Setter method for alias name</h3>
	 * <h3 class="zh-CN">别名的Setter方法</h3>
	 *
	 * @param aliasName <span class="en-US">Alias name</span>
	 *                  <span class="zh-CN">别名</span>
	 */
	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
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
