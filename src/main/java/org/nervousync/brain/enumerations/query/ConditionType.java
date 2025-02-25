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
 * <h2 class="en-US">Enumeration value of query condition type</h2>
 * <h2 class="zh-CN">查询中运算类型的枚举值</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 6, 2020 18:47:52 $
 */
public enum ConditionType {
    /**
     * <span class="en-US">Data column condition</span>
     * <span class="zh-CN">数据列条件</span>
     */
	COLUMN,
    /**
     * <span class="en-US">Condition group</span>
     * <span class="zh-CN">条件组</span>
     */
	GROUP,
    /**
     * <span class="en-US">Condition constant</span>
     * <span class="zh-CN">固定值</span>
     */
	CONSTANT
}
