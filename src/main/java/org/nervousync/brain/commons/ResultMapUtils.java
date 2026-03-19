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

package org.nervousync.brain.commons;

import jakarta.annotation.Nonnull;
import org.nervousync.commons.Globals;
import org.nervousync.enumerations.beans.StringType;
import org.nervousync.utils.core.BeanUtils;
import org.nervousync.utils.core.ConvertUtils;
import org.nervousync.utils.core.StringUtils;

import java.util.*;

/**
 * <h2 class="en-US">ResultSet data map utilities</h2>
 * <h2 class="zh-CN">结果集映射表工具</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Feb 27, 2018 10:21:28 $
 */
public final class ResultMapUtils {

	/**
	 * <span class="en-US">The list split character</span>
	 * <span class="zh-CN">列表分隔符</span>
	 */
	private static final String RECORD_SEPARATOR = "|";
	/**
	 * <span class="en-US">The result set split character</span>
	 * <span class="zh-CN">结果集分隔符</span>
	 */
	private static final String ITEM_SEPARATOR = ",";
	/**
	 * <span class="en-US">The result item split character</span>
	 * <span class="zh-CN">结果项分隔符</span>
	 */
	private static final String DATA_SEPARATOR = ":";

	/**
	 * <h3 class="en-US">Convert the result set mapping table to a string.</h3>
	 * <h3 class="zh-CN">将结果集映射表转换为字符串</h3>
	 *
	 * @param resultMap <span class="en-US">The result set mapping</span>
	 *                  <span class="zh-CN">结果集映射表</span>
	 * @return <span class="en-US">Convert result string</span>
	 * <span class="zh-CN">转换后的字符串</span>
	 */
	public static String mapToString(@Nonnull final Map<String, Object> resultMap) {
		if (resultMap.isEmpty()) {
			return Globals.DEFAULT_VALUE_STRING;
		}
		StringBuilder stringBuilder = new StringBuilder();
		for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
			stringBuilder.append(ITEM_SEPARATOR).append(entry.getKey())
					.append(DATA_SEPARATOR)
					.append(BeanUtils.objectToString(entry.getValue(), StringType.SERIALIZABLE));
		}
		return stringBuilder.substring(ITEM_SEPARATOR.length());
	}

	/**
	 * <h3 class="en-US">Convert the string to a result set mapping table.</h3>
	 * <h3 class="zh-CN">将字符串转换为结果集映射表</h3>
	 *
	 * @param string <span class="en-US">String to be converted</span>
	 *               <span class="zh-CN">待转换的字符串</span>
	 * @return <span class="en-US">The result set mapping</span>
	 * <span class="zh-CN">结果集映射表</span>
	 */
	public static Map<String, Object> stringToResultMap(@Nonnull final String string) {
		if (StringUtils.isEmpty(string)) {
			return Map.of();
		}
		Map<String, Object> resultMap = new HashMap<>();
		Arrays.stream(StringUtils.tokenizeToStringArray(string, ITEM_SEPARATOR))
				.filter(StringUtils::notBlank)
				.forEach(itemData ->
						Optional.of(StringUtils.tokenizeToStringArray(itemData, DATA_SEPARATOR))
								.filter(dataArray -> dataArray.length == 2)
								.ifPresent(dataArray -> resultMap.put(dataArray[0],
										ConvertUtils.toObject(StringUtils.base64Decode(dataArray[1])))));
		return resultMap;
	}

	/**
	 * <h3 class="en-US">Convert the result set mapping table list to a string.</h3>
	 * <h3 class="zh-CN">将结果集映射表列表转换为字符串</h3>
	 *
	 * @param resultList <span class="en-US">The result set mapping table list</span>
	 *                   <span class="zh-CN">结果集映射表列表</span>
	 * @return <span class="en-US">Convert result string</span>
	 * <span class="zh-CN">转换后的字符串</span>
	 */
	public static String listToString(@Nonnull final List<Map<String, Object>> resultList) {
		if (resultList.isEmpty()) {
			return Globals.DEFAULT_VALUE_STRING;
		}
		StringBuilder stringBuilder = new StringBuilder();
		for (Map<String, Object> resultMap : resultList) {
			stringBuilder.append(RECORD_SEPARATOR).append(mapToString(resultMap));
		}
		return stringBuilder.substring(RECORD_SEPARATOR.length());
	}

	/**
	 * <h3 class="en-US">Convert the string to a result set mapping table list.</h3>
	 * <h3 class="zh-CN">将字符串转换为结果集映射表列表</h3>
	 *
	 * @param string <span class="en-US">String to be converted</span>
	 *               <span class="zh-CN">待转换的字符串</span>
	 * @return <span class="en-US">The result set mapping table list</span>
	 * <span class="zh-CN">结果集映射表列表</span>
	 */
	public static List<Map<String, Object>> stringToResultList(@Nonnull final String string) {
		if (StringUtils.isEmpty(string)) {
			return Collections.emptyList();
		}

		List<Map<String, Object>> resultList = new ArrayList<>();
		Arrays.stream(StringUtils.tokenizeToStringArray(string, RECORD_SEPARATOR))
				.filter(StringUtils::notBlank)
				.forEach(recordData -> resultList.add(stringToResultMap(recordData)));
		return resultList;
	}
}
