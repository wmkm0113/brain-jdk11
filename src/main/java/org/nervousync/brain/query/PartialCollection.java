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

import jakarta.annotation.Nonnull;
import org.nervousync.brain.commons.ResultMapUtils;
import org.nervousync.commons.Globals;
import org.nervousync.enumerations.beans.StringType;
import org.nervousync.utils.core.BeanUtils;
import org.nervousync.utils.core.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * <h2 class="en-US">Query result partial collection defines</h2>
 * <h2 class="zh-CN">查询结果集定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jan 13, 2010 4:07:14 PM $
 */
@SuppressWarnings("unused")
public final class PartialCollection implements Serializable {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = 2086690645677391624L;

	/**
	 * <span class="en-US">The constant TOTAL_COUNT_KEY.</span>
	 * <span class="zh-CN">总记录数键值常量</span>
	 */
	private static final String TOTAL_COUNT_KEY = "TOTAL_COUNT";
	/**
	 * <span class="en-US">The constant RESULT_LIST_KEY.</span>
	 * <span class="zh-CN">结果集数键值常量</span>
	 */
	private static final String RESULT_LIST_KEY = "RESULT_LIST";

	/**
	 * <span class="en-US">Collection of entities (part of some another collection)</span>
	 * <span class="zh-CN">结果集列表</span>
	 */
	@Nonnull
	private final List<Map<String, Object>> resultList;

	/**
	 * <span class="en-US">Total number of elements in the query</span>
	 * <span class="zh-CN">查询总记录数</span>
	 */
	private final long totalCount;

	/**
	 * <h3 class="en-US">Constructor method for query result partial collection defines</h3>
	 * <h3 class="zh-CN">查询结果集定义的构造方法</h3>
	 *
	 * @param resultList <span class="en-US">Collection of entities (part of some another collection)</span>
	 *                   <span class="zh-CN">结果集列表</span>
	 * @param totalCount <span class="en-US">Total number of elements in the query</span>
	 *                   <span class="zh-CN">查询总记录数</span>
	 */
	public PartialCollection(@Nonnull final List<Map<String, Object>> resultList, final long totalCount) {
		this.resultList = resultList;
		this.totalCount = totalCount;
	}

	/**
	 * <h3 class="en-US">Get the result size of the current partial collection</h3>
	 * <h3 class="zh-CN">获取当前结果集中的记录数</h3>
	 *
	 * @return <span class="en-US">Number of elements in partial collection</span>
	 * <span class="zh-CN">部分集合中的元素数量</span>
	 */
	public int size() {
		return this.resultList.size();
	}

	/**
	 * <h3 class="en-US">Check if the current result set is empty</h3>
	 * <h3 class="zh-CN">检查当前结果集是否为空</h3>
	 *
	 * @return <span class="en-US"><code>true</code> if this collection is empty</span>
	 * <span class="zh-CN">如果为空返回<code>true</code></span>
	 */
	public boolean isEmpty() {
		return this.resultList.isEmpty();
	}

	/**
	 * <h3 class="en-US">Getter method for the total number of elements in the query</h3>
	 * <h3 class="zh-CN">查询总记录数的Getter方法</h3>
	 *
	 * @return <span class="en-US">Total number of elements in the query</span>
	 * <span class="zh-CN">查询总记录数</span>
	 */
	public long getTotalCount() {
		return this.totalCount;
	}

	/**
	 * <h3 class="en-US">Get the collection of entities (part of some another collection)</h3>
	 * <h3 class="zh-CN">获取结果集列表</h3>
	 *
	 * @return <span class="en-US">Collection of entities (part of some another collection)</span>
	 * <span class="zh-CN">结果集列表</span>
	 */
	@Nonnull
	public List<Map<String, Object>> asList() {
		return this.resultList;
	}

	/**
	 * <h3 class="en-US">Parse the serializable string data</h3>
	 * <h3 class="zh-CN">解析序列化字符串数据</h3>
	 *
	 * @param string <span class="en-US">Serializable string</span>
	 *               <span class="zh-CN">序列化字符串</span>
	 * @return <span class="en-US">Query result partial collection instance object</span>
	 * <span class="zh-CN">查询结果集实例对象</span>
	 */
	public static PartialCollection parse(final String string) {
		if (StringUtils.isEmpty(string)) {
			return null;
		}

		Map<String, Object> convertMap = BeanUtils.stringToMap(string, StringType.JSON, Globals.DEFAULT_ENCODING);
		if (convertMap.isEmpty()) {
			return null;
		}
		String totalCount = (String) convertMap.getOrDefault(TOTAL_COUNT_KEY, Long.toHexString(Globals.DEFAULT_VALUE_LONG));
		String resultData = (String) convertMap.getOrDefault(RESULT_LIST_KEY, Globals.DEFAULT_VALUE_STRING);
		if (StringUtils.isEmpty(resultData)) {
			return new PartialCollection(Collections.emptyList(), Long.parseLong(totalCount, 16));
		}
		List<Map<String, Object>> resultList = ResultMapUtils.stringToResultList(resultData);
		return new PartialCollection(resultList, Long.parseLong(totalCount, 16));
	}

	@Override
	public String toString() {
		Map<String, Object> convertMap = new HashMap<>();
		convertMap.put(TOTAL_COUNT_KEY, Long.toHexString(this.totalCount));
		convertMap.put(RESULT_LIST_KEY, ResultMapUtils.listToString(this.resultList));
		return BeanUtils.objectToString(convertMap, StringType.JSON);
	}
}
