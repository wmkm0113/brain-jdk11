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

import jakarta.xml.bind.annotation.*;
import org.nervousync.brain.enumerations.query.ItemType;
import org.nervousync.brain.query.item.*;
import org.nervousync.utils.core.ClassUtils;

import java.sql.SQLException;
import java.sql.Types;
import java.sql.Wrapper;

/**
 * <h2 class="en-US">Abstract query item define</h2>
 * <h2 class="zh-CN">抽象查询项信息定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 9, 2020 11:30:54 $
 */
@XmlSeeAlso({CalculateItem.class, ColumnItem.class, ConstantItem.class, FunctionItem.class, SubQueryItem.class})
@XmlTransient
@XmlAccessorType(XmlAccessType.NONE)
public abstract class QueryItem extends SortedItem implements Wrapper {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = -6138695356160544999L;

	/**
	 * <span class="en-US">Query item type</span>
	 * <span class="zh-CN">查询项类型</span>
	 */
	@XmlElement(name = "item_type")
	private final ItemType itemType;
	/**
	 * <span class="en-US">Item alias name</span>
	 * <span class="zh-CN">查询项别名</span>
	 */
	@XmlElement(name = "alias_name")
	private String aliasName;
	/**
	 * <span class="en-US">Jdbc type code</span>
	 * <span class="zh-CN">JDBC类型代码</span>
	 */
	@XmlElement(name = "jdbc_type")
	private int jdbcType = Types.NULL;

	/**
	 * <h3 class="en-US">Protect constructor method for abstract query item define</h3>
	 * <h3 class="zh-CN">抽象查询项信息定义的构造方法</h3>
	 *
	 * @param itemType <span class="en-US">Query item type</span>
	 *                 <span class="zh-CN">查询项类型</span>
	 */
	protected QueryItem(final ItemType itemType) {
		this.itemType = itemType;
	}

	/**
	 * <h3 class="en-US">Getter method for the query item type</h3>
	 * <h3 class="zh-CN">查询项类型的Getter方法</h3>
	 *
	 * @return <span class="en-US">Query item type</span>
	 * <span class="zh-CN">查询项类型</span>
	 */
	public ItemType getItemType() {
		return itemType;
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

	/**
	 * <h3 class="en-US">Getter method for the JDBC type code</h3>
	 * <h3 class="zh-CN">JDBC类型代码的Getter方法</h3>
	 *
	 * @return <span class="en-US">JDBC type code</span>
	 * <span class="zh-CN">JDBC类型代码</span>
	 */
	public int getJdbcType() {
		return this.jdbcType;
	}

	/**
	 * <h3 class="en-US">Setter method for the JDBC type code</h3>
	 * <h3 class="zh-CN">JDBC类型代码的Setter方法</h3>
	 *
	 * @param jdbcType <span class="en-US">Jdbc type code</span>
	 *                 <span class="zh-CN">JDBC类型代码</span>
	 */
	public void setJdbcType(final int jdbcType) {
		this.jdbcType = jdbcType;
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
