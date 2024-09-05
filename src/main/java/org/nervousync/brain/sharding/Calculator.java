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

package org.nervousync.brain.sharding;

/**
 * <h2 class="en-US">Sharding calculator interface</h2>
 * <h2 class="zh-CN">分片计算器接口</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Feb 27, 2021 10:21:16 $
 */
public interface Calculator<T> {

	/**
	 * <h3 class="en-US">Calculate sharding result</h3>
	 * <h3 class="zh-CN">计算分片值</h3>
	 *
	 * @param value <span class="en-US">Data column value</span>
	 *              <span class="zh-CN">数据列的值</span>
	 * @return <span class="en-US">Calculate result</span>
	 * <span class="zh-CN">计算结果</span>
	 */
	String result(final T value);

	/**
	 * <h3 class="en-US">Matches sharding result</h3>
	 * <h3 class="zh-CN">匹配分片值</h3>
	 *
	 * @param value <span class="en-US">Sharding result</span>
	 *              <span class="zh-CN">分片值</span>
	 * @return <span class="en-US">Matches result</span>
	 * <span class="zh-CN">匹配结果</span>
	 */
	boolean matches(final String value);
}
