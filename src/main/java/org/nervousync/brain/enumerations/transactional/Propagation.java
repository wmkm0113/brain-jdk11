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

package org.nervousync.brain.enumerations.transactional;

/**
 * <h2 class="en-US">Enumeration value of transactional types</h2>
 * <h2 class="zh-CN">事务类型的枚举值</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Jun 26, 2018 11:39:22 $
 */
public enum Propagation {
	/**
	 * <span class="en-US">Use the current transactional, if current transaction does not exist, create a new transaction</span>
	 * <span class="zh-CN">使用当前事务，如果不存在当前事务则创建新事务</span>
	 */
	REQUIRED,
	/**
	 * <span class="en-US">Use the current transactional if the current transaction does not exist, process in non-transaction</span>
	 * <span class="zh-CN">使用当前事务，如果不存在当前事务则无事务执行</span>
	 */
	SUPPORTS,
	/**
	 * <span class="en-US">Must process in a transactional, if the transaction does not exist, throw Exception</span>
	 * <span class="zh-CN">必须在事务中执行，如果没有事务则抛出异常</span>
	 */
	MANDATORY,
	/**
	 * <span class="en-US">Hang up the current transactional and create a new transaction</span>
	 * <span class="zh-CN">挂起当前事务并创建新事务</span>
	 */
	REQUIRES_NEW,
	/**
	 * <span class="en-US">Ignore the current transactional and process in non-transaction</span>
	 * <span class="zh-CN">忽略当前事务，以无事务方式运行</span>
	 */
	NOT_SUPPORTED,
	/**
	 * <span class="en-US">Process in non-transaction, if the current transaction exists, throw Exception</span>
	 * <span class="zh-CN">以无事务方式运行，如果当前存在事务则抛出异常</span>
	 */
	NEVER
}
