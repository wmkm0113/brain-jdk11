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
 * <h2 class="en-US">Enumeration value of calculate code</h2>
 * <h2 class="zh-CN">计算代码的枚举值</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 6, 2020 18:47:52 $
 */
public enum CalculateCode {

    /**
     * <span class="en-US">Addition</span>
     * <span class="zh-CN">加法</span>
     */
	ADD,
    /**
     * <span class="en-US">Subtraction</span>
     * <span class="zh-CN">减法</span>
     */
	SUBTRACT,
    /**
     * <span class="en-US">Multiplication</span>
     * <span class="zh-CN">乘法</span>
     */
	MULTIPLY,
    /**
     * <span class="en-US">Division</span>
     * <span class="zh-CN">除法</span>
     */
	DIVIDE,
    /**
     * <span class="en-US">Take the remainder</span>
     * <span class="zh-CN">取余数</span>
     */
	REMAINDER,
    /**
     * <span class="en-US">Bitwise AND</span>
     * <span class="zh-CN">按位与</span>
     */
	AND,
    /**
     * <span class="en-US">Bitwise OR</span>
     * <span class="zh-CN">按位或</span>
     */
	OR,
    /**
     * <span class="en-US">Bitwise XOR</span>
     * <span class="zh-CN">按位异或</span>
     */
	XOR
}
