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

package org.nervousync.brain.command;

import java.util.List;

/**
 * <h2 class="en-US">Generated command information</h2>
 * <h2 class="zh-CN">生成的命令信息</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Feb 18, 2019 10:38:52 $
 */
public final class GeneratedCommand {
	/**
	 * <span class="en-US">Generated SQL command</span>
	 * <span class="zh-CN">生成的SQL命令</span>
	 */
	private final String command;
	/**
	 * <span class="en-US">Parameter value list</span>
	 * <span class="zh-CN">参数值列表</span>
	 */
	private final List<Object> parameters;

	/**
	 * <h3 class="en-US">Private constructor method for SQL command information</h3>
	 * <h3 class="zh-CN">SQL命令信息的私有构造方法</h3>
	 *
	 * @param command    <span class="en-US">Generated SQL command</span>
	 *                   <span class="zh-CN">生成的SQL命令</span>
	 * @param parameters <span class="en-US">Parameter value list</span>
	 *                   <span class="zh-CN">参数值列表</span>
	 */
	public GeneratedCommand(final String command, final List<Object> parameters) {
		this.command = command;
		this.parameters = parameters;
	}

	/**
	 * <h3 class="en-US">Getter method for generated SQL command</h3>
	 * <h3 class="zh-CN">生成的SQL命令的Getter方法</h3>
	 *
	 * @return <span class="en-US">Generated SQL command</span>
	 * <span class="zh-CN">生成的SQL命令</span>
	 */
	public String getCommand() {
		return this.command;
	}

	/**
	 * <h3 class="en-US">Getter method for parameter value list</h3>
	 * <h3 class="zh-CN">参数值列表的Getter方法</h3>
	 *
	 * @return <span class="en-US">Parameter value list</span>
	 * <span class="zh-CN">参数值列表</span>
	 */
	public List<Object> getParameters() {
		return this.parameters;
	}
}
