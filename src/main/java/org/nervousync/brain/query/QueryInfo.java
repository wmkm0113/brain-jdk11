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

package org.nervousync.brain.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nonnull;
import jakarta.xml.bind.annotation.*;
import org.nervousync.brain.enumerations.query.QueryType;
import org.nervousync.brain.query.core.AbstractQuery;
import org.nervousync.brain.query.core.QueryItem;
import org.nervousync.brain.query.core.SortedItem;
import org.nervousync.brain.query.sort.OrderBy;
import org.nervousync.brain.query.item.*;
import org.nervousync.commons.Globals;

import java.util.ArrayList;
import java.util.List;

/**
 * <h2 class="en-US">Query information defines</h2>
 * <h2 class="zh-CN">查询信息定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 28, 2020 11:46:08 $
 */
@SuppressWarnings("unused")
@XmlType(name = "query_info", namespace = "https://nervousync.org/schemas/brain")
@XmlRootElement(name = "query_info", namespace = "https://nervousync.org/schemas/brain")
@XmlAccessorType(XmlAccessType.NONE)
public final class QueryInfo extends AbstractQuery {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = 549973159743148887L;

	/**
	 * <span class="en-US">Cache key value</span>
	 * <span class="zh-CN">缓存键值</span>
	 */
	@XmlElement(name = "cache_key")
	private String cacheKey;
	/**
	 * <span class="en-US">Sheet name</span>
	 * <span class="zh-CN">工作表名称</span>
	 */
	@JsonIgnore
	private String sheetName;
	/**
	 * <span class="en-US">Query item instance list</span>
	 * <span class="zh-CN">查询项目实例对象列表</span>
	 */
	@Nonnull
	@XmlElements({
			@XmlElement(name = "calculate_item", type = CalculateItem.class, namespace = "https://nervousync.org/schemas/brain"),
			@XmlElement(name = "column_item", type = ColumnItem.class, namespace = "https://nervousync.org/schemas/brain"),
			@XmlElement(name = "constant_item", type = ConstantItem.class, namespace = "https://nervousync.org/schemas/brain"),
			@XmlElement(name = "function_item", type = FunctionItem.class, namespace = "https://nervousync.org/schemas/brain"),
			@XmlElement(name = "sub_query_item", type = SubQueryItem.class, namespace = "https://nervousync.org/schemas/brain")
	})
	@XmlElementWrapper(name = "item_list")
	private List<QueryItem> itemList;
	/**
	 * <span class="en-US">Query order by columns' list</span>
	 * <span class="zh-CN">查询排序数据列列表</span>
	 */
	@Nonnull
	@XmlElement(name = "order_by", type = OrderBy.class, namespace = "https://nervousync.org/schemas/brain")
	@XmlElementWrapper(name = "order_list")
	private List<OrderBy> orderByList;
	/**
	 * <span class="en-US">Query result can cacheable</span>
	 * <span class="zh-CN">查询结果可以缓存</span>
	 */
	@XmlElement
	private boolean cacheables = Boolean.FALSE;
	/**
	 * <span class="en-US">Current page number</span>
	 * <span class="zh-CN">当前页数</span>
	 */
	@XmlElement(name = "page_number")
	private int pageNo = Globals.DEFAULT_VALUE_INT;
	/**
	 * <span class="en-US">Page limit records count</span>
	 * <span class="zh-CN">每页的记录数</span>
	 */
	@XmlElement(name = "page_limit")
	private int pageLimit = Globals.DEFAULT_VALUE_INT;

	/**
	 * <h3 class="en-US">Constructor method for query information define</h3>
	 * <h3 class="zh-CN">查询条件信息的构造方法</h3>
	 */
	public QueryInfo() {
		super(QueryType.NORMAL);
		this.itemList = new ArrayList<>();
		this.orderByList = new ArrayList<>();
	}

	/**
	 * <h3 class="en-US">Getter method for the cache key value</h3>
	 * <h3 class="zh-CN">缓存键值的Getter方法</h3>
	 *
	 * @return <span class="en-US">Cache key value</span>
	 * <span class="zh-CN">缓存键值</span>
	 */
	public String getCacheKey() {
		return this.cacheKey;
	}

	/**
	 * <h3 class="en-US">Setter method for the cache key value</h3>
	 * <h3 class="zh-CN">缓存键值的Setter方法</h3>
	 *
	 * @param cacheKey <span class="en-US">Cache key value</span>
	 *                 <span class="zh-CN">缓存键值</span>
	 */
	public void setCacheKey(final String cacheKey) {
		this.cacheKey = cacheKey;
	}

	/**
	 * <h3 class="en-US">Getter method for the sheet name</h3>
	 * <h3 class="zh-CN">工作表名称的Getter方法</h3>
	 *
	 * @return <span class="en-US">Sheet name</span>
	 * <span class="zh-CN">工作表名称</span>
	 */
	public String getSheetName() {
		return this.sheetName;
	}

	/**
	 * <h3 class="en-US">Setter method for the sheet name</h3>
	 * <h3 class="zh-CN">工作表名称的Setter方法</h3>
	 *
	 * @param sheetName <span class="en-US">Sheet name</span>
	 *                  <span class="zh-CN">工作表名称</span>
	 */
	public void setSheetName(final String sheetName) {
		this.sheetName = sheetName;
	}

	/**
	 * <h3 class="en-US">Getter method for the query item instance list</h3>
	 * <h3 class="zh-CN">查询项目实例对象列表的Getter方法</h3>
	 *
	 * @return <span class="en-US">Query item instance list</span>
	 * <span class="zh-CN">查询项目实例对象列表</span>
	 */
	@Nonnull
	public List<QueryItem> getItemList() {
		return this.itemList;
	}

	/**
	 * <h3 class="en-US">Setter method for the query item instance list</h3>
	 * <h3 class="zh-CN">查询项目实例对象列表的Setter方法</h3>
	 *
	 * @param itemList <span class="en-US">Query item instance list</span>
	 *                 <span class="zh-CN">查询项目实例对象列表</span>
	 */
	public void setItemList(@Nonnull final List<QueryItem> itemList) {
		this.itemList = new ArrayList<>(itemList);
		this.itemList.sort(SortedItem.desc());
	}

	/**
	 * <h3 class="en-US">Getter method for query order by column list</h3>
	 * <h3 class="zh-CN">查询排序数据列列表的Getter方法</h3>
	 *
	 * @return <span class="en-US">Query order by column list</span>
	 * <span class="zh-CN">查询排序数据列列表</span>
	 */
	@Nonnull
	public List<OrderBy> getOrderByList() {
		return this.orderByList;
	}

	/**
	 * <h3 class="en-US">Setter method for query order by column list</h3>
	 * <h3 class="zh-CN">查询排序数据列列表的Setter方法</h3>
	 *
	 * @param orderByList <span class="en-US">Query order by column list</span>
	 *                    <span class="zh-CN">查询排序数据列列表</span>
	 */
	public void setOrderByList(@Nonnull final List<OrderBy> orderByList) {
		this.orderByList = new ArrayList<>(orderByList);
		this.orderByList.sort(SortedItem.desc());
	}

	/**
	 * <h3 class="en-US">Getter method for the query result can cacheable</h3>
	 * <h3 class="zh-CN">查询结果可以缓存的Getter方法</h3>
	 *
	 * @return <span class="en-US">Query result can cacheable</span>
	 * <span class="zh-CN">查询结果可以缓存</span>
	 */
	public boolean isCacheables() {
		return this.cacheables;
	}

	/**
	 * <h3 class="en-US">Setter method for the query result can cacheable</h3>
	 * <h3 class="zh-CN">查询结果可以缓存的Setter方法</h3>
	 *
	 * @param cacheables <span class="en-US">Query result can cacheable</span>
	 *                   <span class="zh-CN">查询结果可以缓存</span>
	 */
	public void setCacheables(final boolean cacheables) {
		this.cacheables = cacheables;
	}

	/**
	 * <h3 class="en-US">Getter method for the current page number</h3>
	 * <h3 class="zh-CN">当前页数的Getter方法</h3>
	 *
	 * @return <span class="en-US">Current page number</span>
	 * <span class="zh-CN">当前页数</span>
	 */
	public int getPageNo() {
		return this.pageNo;
	}

	/**
	 * <h3 class="en-US">Setter method for the current page number</h3>
	 * <h3 class="zh-CN">当前页数的Setter方法</h3>
	 *
	 * @param pageNo <span class="en-US">Current page number</span>
	 *               <span class="zh-CN">当前页数</span>
	 */
	public void setPageNo(final int pageNo) {
		this.pageNo = pageNo;
	}

	/**
	 * <h3 class="en-US">Getter method for query page limit</h3>
	 * <h3 class="zh-CN">查询分页记录数的Getter方法</h3>
	 *
	 * @return <span class="en-US">Query page limit</span>
	 * <span class="zh-CN">查询分页记录数</span>
	 */
	public int getPageLimit() {
		return this.pageLimit;
	}

	/**
	 * <h3 class="en-US">Setter method for query page limit</h3>
	 * <h3 class="zh-CN">查询分页记录数的Setter方法</h3>
	 *
	 * @param pageLimit <span class="en-US">Query page limit</span>
	 *                  <span class="zh-CN">查询分页记录数</span>
	 */
	public void setPageLimit(final int pageLimit) {
		this.pageLimit = pageLimit;
	}
}
