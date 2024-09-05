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

/**
 * <h2 class="en-US">Constant value define</h2>
 * <h2 class="zh-CN">常量定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Feb 27, 2018 10:21:28 $
 */
public final class BrainCommons {

	/**
	 * <span class="en-US">Default minimum connection count</span>
	 * <span class="zh-CN">默认的最小连接数</span>
	 */
	public static final int DEFAULT_MIN_CONNECTIONS = 2;
	/**
	 * <span class="en-US">Default maximum connection count</span>
	 * <span class="zh-CN">默认的最大连接数</span>
	 */
	public static final int DEFAULT_MAX_CONNECTIONS = 2;
	/**
	 * <span class="en-US">Default maximum connection retry count</span>
	 * <span class="zh-CN">默认的最大重试次数</span>
	 */
	public static final int DEFAULT_RETRY_COUNT = 3;
	/**
	 * <span class="en-US">Default retry interval (Unit: milliseconds)</span>
	 * <span class="zh-CN">默认的重试间隔时间（单位：毫秒）</span>
	 */
	public static final long DEFAULT_RETRY_PERIOD = 1000L;

	/**
	 * <span class="en-US">White space string</span>
	 * <span class="zh-CN">空格字符串</span>
	 */
	public static final String WHITE_SPACE = " ";
	/**
	 * <span class="en-US">Default split string</span>
	 * <span class="zh-CN">默认分隔字符串</span>
	 */
	public static final String DEFAULT_SPLIT_CHARACTER = ", ";
	/**
	 * <span class="en-US">Brackets begin string</span>
	 * <span class="zh-CN">括号起始字符串</span>
	 */
	public static final String BRACKETS_BEGIN = "(";
	/**
	 * <span class="en-US">Brackets end string</span>
	 * <span class="zh-CN">括号终止字符串</span>
	 */
	public static final String BRACKETS_END = ")";
	/**
	 * <span class="en-US">SQL placeholder</span>
	 * <span class="zh-CN">SQL占位符</span>
	 */
	public static final String DEFAULT_PLACE_HOLDER = " ? ";
	/**
	 * <span class="en-US">Default name split character</span>
	 * <span class="zh-CN">默认的分隔符</span>
	 */
	public static final String DEFAULT_NAME_SPLIT = ".";

	/**
	 * <span class="en-US">Greater operator</span>
	 * <span class="zh-CN">大于操作符</span>
	 */
	public static final String OPERATOR_GREATER = " > ";
	/**
	 * <span class="en-US">Greater equal operator</span>
	 * <span class="zh-CN">大于等于操作符</span>
	 */
	public static final String OPERATOR_GREATER_EQUAL = " >= ";
	/**
	 * <span class="en-US">Less operator</span>
	 * <span class="zh-CN">小于操作符</span>
	 */
	public static final String OPERATOR_LESS = " < ";
	/**
	 * <span class="en-US">Less equal operator</span>
	 * <span class="zh-CN">小于等于操作符</span>
	 */
	public static final String OPERATOR_LESS_EQUAL = " <= ";
	/**
	 * <span class="en-US">Equal operator</span>
	 * <span class="zh-CN">等于操作符</span>
	 */
	public static final String OPERATOR_EQUAL = " = ";
	/**
	 * <span class="en-US">Not equal operator</span>
	 * <span class="zh-CN">不等于操作符</span>
	 */
	public static final String OPERATOR_NOT_EQUAL = " <> ";
	/**
	 * <span class="en-US">Between ... and ... operator</span>
	 * <span class="zh-CN">在两者之间操作符</span>
	 */
	public static final String OPERATOR_BETWEEN_AND = " BETWEEN ? AND ? ";
	/**
	 * <span class="en-US">Not between ... and ... operator</span>
	 * <span class="zh-CN">未在两者之间操作符</span>
	 */
	public static final String OPERATOR_NOT_BETWEEN_AND = " NOT BETWEEN ? AND ? ";
	/**
	 * <span class="en-US">Like operator</span>
	 * <span class="zh-CN">模糊匹配操作符</span>
	 */
	public static final String OPERATOR_LIKE = " LIKE ? ";
	/**
	 * <span class="en-US">Not like operator</span>
	 * <span class="zh-CN">非模糊匹配操作符</span>
	 */
	public static final String OPERATOR_NOT_LIKE = " NOT LIKE ? ";
	/**
	 * <span class="en-US">Is null operator</span>
	 * <span class="zh-CN">空值操作符</span>
	 */
	public static final String OPERATOR_IS_NULL = " IS NULL ";
	/**
	 * <span class="en-US">Not null operator</span>
	 * <span class="zh-CN">非空值操作符</span>
	 */
	public static final String OPERATOR_NOT_NULL = " NOT NULL ";
	/**
	 * <span class="en-US">IN operator</span>
	 * <span class="zh-CN">包含其中操作符</span>
	 */
	public static final String OPERATOR_IN = " IN ";
	/**
	 * <span class="en-US">Not IN operator</span>
	 * <span class="zh-CN">未包含操作符</span>
	 */
	public static final String OPERATOR_NOT_IN = " NOT IN ";
	/**
	 * <span class="en-US">Exists operator</span>
	 * <span class="zh-CN">是否存在操作符</span>
	 */
	public static final String OPERATOR_EXISTS = " EXISTS ";
	/**
	 * <span class="en-US">Not exists operator</span>
	 * <span class="zh-CN">是否不存在操作符</span>
	 */
	public static final String OPERATOR_NOT_EXISTS = " NOT EXISTS ";

	/**
	 * <span class="en-US">Default where clause</span>
	 * <span class="zh-CN">默认的匹配条件</span>
	 */
	public static final String DEFAULT_WHERE_CLAUSE = " 1 = 1 ";

	/**
	 * <span class="en-US">JNDI name configure</span>
	 * <span class="zh-CN">JNDI名称配置</span>
	 */
	public static final String PROPERTY_JNDI_NAME_KEY = "name";
	/**
	 * <span class="en-US">Lazy load configure</span>
	 * <span class="zh-CN">延迟加载配置</span>
	 */
	public static final String PROPERTY_LAZY_INIT_KEY = "lazy";
	/**
	 * <span class="en-US">JMX monitor configure</span>
	 * <span class="zh-CN">JMX监控配置</span>
	 */
	public static final String PROPERTY_JMX_MONITOR_KEY = "jmx";
	/**
	 * <span class="en-US">DDL configure</span>
	 * <span class="zh-CN">DDL配置</span>
	 */
	public static final String PROPERTY_DDL_TYPE_KEY = "ddl";

	/**
	 * <span class="en-US">Default JNDI name</span>
	 * <span class="zh-CN">默认的JNDI名称</span>
	 */
	public static final String DEFAULT_JNDI_NAME = "jndi/brain";
}
