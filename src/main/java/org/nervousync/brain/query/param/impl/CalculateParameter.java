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

package org.nervousync.brain.query.param.impl;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.nervousync.brain.enumerations.query.ItemType;
import org.nervousync.brain.query.item.CalculateItem;
import org.nervousync.brain.query.param.AbstractParameter;

/**
 * <h2 class="en-US">Calculate data parameter information define</h2>
 * <h2 class="zh-CN">计算值参数定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 9, 2020 18:05:28 $
 */
@XmlType(name = "calculate_parameter", namespace = "https://nervousync.org/schemas/brain")
@XmlRootElement(name = "calculate_parameter", namespace = "https://nervousync.org/schemas/brain")
public final class CalculateParameter extends AbstractParameter<CalculateItem> {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = 7516729127131470411L;

	/**
	 * <h3 class="en-US">Protect constructor method for abstract class for parameter information define</h3>
	 * <h3 class="zh-CN">参数信息定义抽象类的构造方法</h3>
	 */
	public CalculateParameter() {
		super(ItemType.CALCULATE);
	}
}
