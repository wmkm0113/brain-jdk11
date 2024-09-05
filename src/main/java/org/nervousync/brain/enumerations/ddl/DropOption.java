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
package org.nervousync.brain.enumerations.ddl;

import jakarta.xml.bind.annotation.XmlEnum;

/**
 * <h2 class="en-US">Enumeration value of drop option</h2>
 * <h2 class="zh-CN">删除选项的枚举值</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Jun 26, 2015 12:46:42 $
 */
@XmlEnum
public enum DropOption {
	/**
     * <span class="en-US">RESTRICT</span>
     * <span class="zh-CN">约束</span>
	 */
	RESTRICT,
	/**
     * <span class="en-US">CASCADE</span>
     * <span class="zh-CN">级联</span>
	 */
	CASCADE,
	/**
     * <span class="en-US">NONE</span>
     * <span class="zh-CN">不操作</span>
	 */
	NONE
}
