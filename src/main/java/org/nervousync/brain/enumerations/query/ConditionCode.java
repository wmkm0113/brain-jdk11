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
package org.nervousync.brain.enumerations.query;

/**
 * <h2 class="en-US">Enumeration value of query condition code</h2>
 * <h2 class="zh-CN">查询中运算代码的枚举值</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 6, 2020 18:47:52 $
 */
public enum ConditionCode {
    /**
     * <span class="en-US">Greater</span>
     * <span class="zh-CN">大于</span>
     */
	GREATER,
    /**
     * <span class="en-US">Greater equal</span>
     * <span class="zh-CN">大于等于</span>
     */
	GREATER_EQUAL,
    /**
     * <span class="en-US">Less</span>
     * <span class="zh-CN">小于</span>
     */
	LESS,
    /**
     * <span class="en-US">Less equal</span>
     * <span class="zh-CN">小于等于</span>
     */
	LESS_EQUAL,
    /**
     * <span class="en-US">Equal</span>
     * <span class="zh-CN">等于</span>
     */
	EQUAL,
    /**
     * <span class="en-US">Not equal</span>
     * <span class="zh-CN">不等于</span>
     */
	NOT_EQUAL,
    /**
     * <span class="en-US">Between value1 and value2</span>
     * <span class="zh-CN">在 ... 和 ... 之间</span>
     */
	BETWEEN_AND,
    /**
     * <span class="en-US">Not between value1 and value2</span>
     * <span class="zh-CN">没有在 ... 和 ... 之间</span>
     */
	NOT_BETWEEN_AND,
    /**
     * <span class="en-US">Like</span>
     * <span class="zh-CN">匹配查询</span>
     */
	LIKE,
    /**
     * <span class="en-US">Not like</span>
     * <span class="zh-CN">不匹配查询</span>
     */
	NOT_LIKE,
    /**
     * <span class="en-US">Is null</span>
     * <span class="zh-CN">为空值</span>
     */
	IS_NULL,
    /**
     * <span class="en-US">Not null</span>
     * <span class="zh-CN">不为空值</span>
     */
	NOT_NULL,
    /**
     * <span class="en-US">Contains in</span>
     * <span class="zh-CN">包含</span>
     */
	IN,
    /**
     * <span class="en-US">Not contains in</span>
     * <span class="zh-CN">不包含</span>
     */
	NOT_IN,
    /**
     * <span class="en-US">Sub-query results contains record</span>
     * <span class="zh-CN">子查询有记录</span>
     */
	EXISTS,
    /**
     * <span class="en-US">Sub-query results not contains record</span>
     * <span class="zh-CN">子查询没有记录</span>
     */
	NOT_EXISTS
}
