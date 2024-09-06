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

package org.nervousync.brain.exceptions.sql;

import org.nervousync.commons.Globals;
import org.nervousync.utils.MultilingualUtils;

import java.sql.SQLException;

/**
 * <h2 class="en-US">SQL exceptions implemented in multiple languages</h2>
 * <h2 class="zh-CN">多语言实现的SQL异常</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Jul 24, 2018 13:08:12 $
 */
public final class MultilingualSQLException extends SQLException {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = 7216968895143298449L;

	/**
	 * <span class="en-US">Internationalization information agent</span>
	 * <span class="zh-CN">国际化信息代理</span>
	 */
	private final MultilingualUtils.Agent multiAgent = MultilingualUtils.newAgent(this.getClass());
	/**
	 * <span class="en-US">Details message of error</span>
	 * <span class="zh-CN">错误详细信息</span>
	 */
	private final String detailMessage;

	/**
	 * <h3 class="en-US">Constructor method for MultilingualSQLException</h3>
	 * <span class="en-US">Create a new MultilingualSQLException with the specified message.</span>
	 * <h3 class="zh-CN">MultilingualSQLException构造方法</h3>
	 * <span class="zh-CN">使用特定的信息创建MultilingualSQLException实例对象。</span>
	 *
	 * @param errorCode   <span class="en-US">Error identified code</span>
	 *                    <span class="zh-CN">错误识别代码</span>
	 * @param collections <span class="en-US">given parameters of information formatter</span>
	 *                    <span class="zh-CN">用于资源信息格式化的参数</span>
	 */
	public MultilingualSQLException(final long errorCode, final Object... collections) {
		super(Globals.DEFAULT_VALUE_STRING);
		this.detailMessage = this.multiAgent.errorMessage(errorCode, collections);
	}

	/**
	 * <h3 class="en-US">Constructor method for MultilingualSQLException</h3>
	 * <span class="en-US">Create a new MultilingualSQLException with the specified message and root cause.</span>
	 * <h3 class="zh-CN">MultilingualSQLException构造方法</h3>
	 * <span class="zh-CN">使用特定的信息以及异常信息对象实例创建MultilingualSQLException实例对象。</span>
	 *
	 * @param errorCode   <span class="en-US">Error identified code</span>
	 *                    <span class="zh-CN">错误识别代码</span>
	 * @param cause       <span class="en-US">The root cause</span>
	 *                    <span class="zh-CN">异常信息对象实例</span>
	 * @param collections <span class="en-US">given parameters of information formatter</span>
	 *                    <span class="zh-CN">用于资源信息格式化的参数</span>
	 */
	public MultilingualSQLException(final long errorCode, final Throwable cause, final Object... collections) {
		super(Globals.DEFAULT_VALUE_STRING, cause);
		this.detailMessage = this.multiAgent.errorMessage(errorCode, collections);
	}

	/**
	 * <h3 class="en-US">Constructor method for MultilingualSQLException</h3>
	 * <span class="en-US">Create a new MultilingualSQLException with the specified message.</span>
	 * <h3 class="zh-CN">MultilingualSQLException构造方法</h3>
	 * <span class="zh-CN">使用特定的信息创建MultilingualSQLException实例对象。</span>
	 *
	 * @param errorCode   <span class="en-US">Error identified code</span>
	 *                    <span class="zh-CN">错误识别代码</span>
	 * @param SQLState    <span class="en-US">an XOPEN or SQL:2003 code identifying the exception</span>
	 *                    <span class="zh-CN">标识异常的 XOPEN 或 SQL:2003 代码</span>
	 * @param cause       <span class="en-US">The root cause</span>
	 *                    <span class="zh-CN">异常信息对象实例</span>
	 * @param collections <span class="en-US">given parameters of information formatter</span>
	 *                    <span class="zh-CN">用于资源信息格式化的参数</span>
	 */
	public MultilingualSQLException(final long errorCode, String SQLState,
	                                final Throwable cause, final Object... collections) {
		super(Globals.DEFAULT_VALUE_STRING, SQLState, cause);
		this.detailMessage = this.multiAgent.errorMessage(errorCode, collections);
	}

	/**
	 * <h3 class="en-US">Constructor method for MultilingualSQLException</h3>
	 * <span class="en-US">Create a new MultilingualSQLException with the specified message.</span>
	 * <h3 class="zh-CN">MultilingualSQLException构造方法</h3>
	 * <span class="zh-CN">使用特定的信息创建MultilingualSQLException实例对象。</span>
	 *
	 * @param errorCode   <span class="en-US">Error identified code</span>
	 *                    <span class="zh-CN">错误识别代码</span>
	 * @param SQLState    <span class="en-US">an XOPEN or SQL:2003 code identifying the exception</span>
	 *                    <span class="zh-CN">标识异常的 XOPEN 或 SQL:2003 代码</span>
	 * @param vendorCode  <span class="en-US">数据库供应商特定的异常代码</span>
	 *                    <span class="zh-CN">错误识别代码</span>
	 * @param cause       <span class="en-US">The root cause</span>
	 *                    <span class="zh-CN">异常信息对象实例</span>
	 * @param collections <span class="en-US">given parameters of information formatter</span>
	 *                    <span class="zh-CN">用于资源信息格式化的参数</span>
	 */
	public MultilingualSQLException(final long errorCode, String SQLState, int vendorCode,
	                                final Throwable cause, final Object... collections) {
		super(Globals.DEFAULT_VALUE_STRING, SQLState, vendorCode, cause);
		this.detailMessage = this.multiAgent.errorMessage(errorCode, collections);
	}

	@Override
	public String getMessage() {
		return detailMessage;
	}
}
