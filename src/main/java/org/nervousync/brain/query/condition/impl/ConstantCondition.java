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

package org.nervousync.brain.query.condition.impl;

import org.nervousync.brain.enumerations.query.ConditionType;
import org.nervousync.brain.query.condition.Condition;

/**
 * <h2 class="en-US">Constant condition information</h2>
 * <h2 class="zh-CN">固定条件定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 9, 2020 19:12:02 $
 */
public final class ConstantCondition extends Condition {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = -5093482759842827131L;

	/**
	 * <span class="en-US">Match result</span>
	 * <span class="zh-CN">匹配结果</span>
	 */
	private final boolean matchResult;

	/**
	 * <h3 class="en-US">Constructor method for query condition information define</h3>
	 * <h3 class="zh-CN">查询匹配条件定义的构造方法</h3>
	 *
	 * @param matchResult <span class="en-US">Match result</span>
	 *                    <span class="zh-CN">匹配结果</span>
	 */
	public ConstantCondition(final boolean matchResult) {
		super(ConditionType.CONSTANT);
		this.matchResult = matchResult;
	}

	/**
	 * <h3 class="en-US">Getter method for match result</h3>
	 * <h3 class="zh-CN">匹配结果的Getter方法</h3>
	 *
	 * @return <span class="en-US">Match result</span>
	 * <span class="zh-CN">匹配结果</span>
	 */
	public boolean isMatchResult() {
		return this.matchResult;
	}
}
