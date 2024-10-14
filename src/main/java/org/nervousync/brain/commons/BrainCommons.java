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

import org.nervousync.commons.Globals;
import org.nervousync.utils.ConvertUtils;
import org.nervousync.utils.SecurityUtils;
import org.nervousync.utils.StringUtils;
import org.nervousync.utils.SystemUtils;

/**
 * <h2 class="en-US">Constant value define</h2>
 * <h2 class="zh-CN">常量定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Feb 27, 2018 10:21:28 $
 */
public final class BrainCommons {

	/**
	 * <span class="en-US">Default page number</span>
	 * <span class="zh-CN">默认起始页</span>
	 */
	public static final int DEFAULT_PAGE_NO = 1;
	/**
	 * <span class="en-US">Default page limit</span>
	 * <span class="zh-CN">默认每页记录数</span>
	 */
	public static final int DEFAULT_PAGE_LIMIT = 20;
	/**
	 * <span class="en-US">The number of threads executed simultaneously by the default data import and export task</span>
	 * <span class="zh-CN">默认数据导入导出任务同时执行的线程数</span>
	 */
	public static final int DEFAULT_PROCESS_THREAD_LIMIT = 20;
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
	 * <span class="en-US">The default expiration time after the data import and export task is completed</span>
	 * <span class="zh-CN">默认数据导入导出任务完成后的过期时间</span>
	 */
	public static final long DEFAULT_STORAGE_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;
	/**
	 * <span class="en-US">Data import and export task status: Create</span>
	 * <span class="zh-CN">数据导入导出任务状态：创建</span>
	 */
	public static final int DATA_TASK_STATUS_CREATE = 0;
	/**
	 * <span class="en-US">Data import and export task status: Processing</span>
	 * <span class="zh-CN">数据导入导出任务状态：处理中</span>
	 */
	public static final int DATA_TASK_STATUS_PROCESS = 1;
	/**
	 * <span class="en-US">Data import and export task status: Finished</span>
	 * <span class="zh-CN">数据导入导出任务状态：已完成</span>
	 */
	public static final int DATA_TASK_STATUS_FINISH = 2;
	/**
	 * <span class="en-US">Data file extension</span>
	 * <span class="zh-CN">数据文件的扩展名</span>
	 */
	public static final String DATA_FILE_EXTENSION_NAME = ".dat";
	/**
	 * <span class="en-US">Data file extension</span>
	 * <span class="zh-CN">数据文件的扩展名</span>
	 */
	public static final String DATA_TMP_FILE_EXTENSION_NAME = ".tmp";

	/**
	 * <span class="en-US">Default remote database dialect name</span>
	 * <span class="zh-CN">默认的远程数据库方言名称</span>
	 */
	public static final String DEFAULT_REMOTE_DIALECT_NAME = "Remote";
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
	public static final String PROPERTY_PATH_KEY = "path";

	/**
	 * <span class="en-US">Default JNDI name</span>
	 * <span class="zh-CN">默认的JNDI名称</span>
	 */
	public static final String DEFAULT_JNDI_NAME = "jndi/brain";
	/**
	 * <span class="en-US">Default data import/export work path</span>
	 * <span class="zh-CN">默认的导入导出工作目录</span>
	 */
	public static final String DEFAULT_TMP_PATH = SystemUtils.JAVA_TMP_DIR + Globals.DEFAULT_PAGE_SEPARATOR + "brain";

	/**
	 * <h3 class="en-US">Data table identification code</h3>
	 * <h3 class="zh-CN">数据表识别代码</h3>
	 *
	 * @param string <span class="en-US">Data table name or entity class name</span>
	 *               <span class="zh-CN">数据表名或实体类名</span>
	 * @return <span class="en-US">Identification code</span>
	 * <span class="zh-CN">识别代码</span>
	 */
	public static String identifyCode(final String string) {
		if (StringUtils.isEmpty(string)) {
			return Globals.DEFAULT_VALUE_STRING;
		}
		return ConvertUtils.toHex(SecurityUtils.SHA256(string));
	}
}
