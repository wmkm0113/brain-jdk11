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

import jakarta.xml.bind.annotation.XmlEnum;

/**
 * <h2 class="en-US">Enumeration value of lock option</h2>
 * <h2 class="zh-CN">数据记录锁定选项的枚举值</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: May 15, 2015 18:05:34 $
 */
@XmlEnum
public enum LockOption {
    /**
     * <span class="en-US">None</span>
     * <span class="zh-CN">无</span>
     */
	NONE,
    /**
     * <span class="en-US">Pessimistic upgrade</span>
     * <span class="zh-CN">悲观锁</span>
     */
	PESSIMISTIC_UPGRADE,
    /**
     * <span class="en-US">Pessimistic upgrade nowait (Oracle special implement)</span>
     * <span class="zh-CN">无等待悲观锁（Oracle的特定实现）</span>
     */
	PESSIMISTIC_UPGRADE_NOWAIT,
    /**
     * <span class="en-US">Optimistic upgrade</span>
     * <span class="zh-CN">乐观锁</span>
     */
	OPTIMISTIC_UPGRADE
}
