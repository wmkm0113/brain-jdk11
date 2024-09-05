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
package org.nervousync.brain.query.filter;

import jakarta.xml.bind.annotation.*;
import org.nervousync.brain.query.core.SortedItem;
import org.nervousync.utils.ObjectUtils;

/**
 * <h2 class="en-US">Query group by column define</h2>
 * <h2 class="zh-CN">查询分组列信息定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 7， 2020 13:36：28 $
 */
@XmlType(name = "group_by", namespace = "https://nervousync.org/schemas/database")
@XmlRootElement(name = "group_by", namespace = "https://nervousync.org/schemas/database")
@XmlAccessorType(XmlAccessType.NONE)
public final class GroupBy extends SortedItem {
	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = -7703148998062830489L;

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
	 * <h3 class="en-US">Constructor method for query group by column define</h3>
	 * <h3 class="zh-CN">查询分组列信息定义的构造方法</h3>
	 */
	public GroupBy() {
	}

	/**
	 * <h3 class="en-US">Constructor method for query group by column define</h3>
	 * <h3 class="zh-CN">查询分组列信息定义的构造方法</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @param sortCode   <span class="en-US">Sort code</span>
	 *                   <span class="zh-CN">排序代码</span>
	 */
	public GroupBy(final String tableName, final String columnName, final int sortCode) {
		this();
		this.tableName = tableName;
		this.columnName = columnName;
		super.setSortCode(sortCode);
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
	 * <h3 class="en-US">Checks whether the given parameter value matches the current information</h3>
	 * <h3 class="zh-CN">检查给定的参数值是否与当前信息匹配</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Match result</span>
	 * <span class="zh-CN">匹配结果</span>
	 */
	public boolean match(final String tableName, final String columnName) {
		return ObjectUtils.nullSafeEquals(tableName, this.tableName)
				&& ObjectUtils.nullSafeEquals(columnName, this.columnName);
	}
}
