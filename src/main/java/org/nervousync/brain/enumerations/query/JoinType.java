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
 * <h2 class="en-US">Enumeration value of join type</h2>
 * <h2 class="zh-CN">数据表关联类型的枚举值</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Feb 17, 2021 09:49:16 $
 */
public enum JoinType {
    /**
     * <span class="en-US">Left join</span>
     * <span class="zh-CN">左连接</span>
     */
	LEFT,
    /**
     * <span class="en-US">Right join</span>
     * <span class="zh-CN">右连接</span>
     */
	RIGHT,
    /**
     * <span class="en-US">Full join</span>
     * <span class="zh-CN">完全连接</span>
     */
	FULL,
    /**
     * <span class="en-US">Inner join</span>
     * <span class="zh-CN">内连接</span>
     */
	INNER,
    /**
     * <span class="en-US">Cross join</span>
     * <span class="zh-CN">交叉连接</span>
     */
	CROSS
}
