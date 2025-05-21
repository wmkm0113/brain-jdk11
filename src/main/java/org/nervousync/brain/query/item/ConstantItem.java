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

package org.nervousync.brain.query.item;

import jakarta.xml.bind.annotation.*;
import org.nervousync.brain.enumerations.query.ItemType;
import org.nervousync.brain.query.core.AbstractItem;

/**
 * <h2 class="en-US">Query constant information define</h2>
 * <h2 class="zh-CN">查询常量值信息定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 9, 2020 11:55:28 $
 */
@XmlType(name = "constant_item", namespace = "https://nervousync.org/schemas/brain")
@XmlRootElement(name = "constant_item", namespace = "https://nervousync.org/schemas/brain")
@XmlAccessorType(XmlAccessType.NONE)
public final class ConstantItem extends AbstractItem {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = 3321192808086705066L;

	/**
	 * <span class="en-US">Constant value</span>
	 * <span class="zh-CN">常量值</span>
	 */
	@XmlElement(name = "constant_value")
	private String constantValue;

	/**
	 * <h3 class="en-US">Constructor method for constant column information defines</h3>
	 * <h3 class="zh-CN">常量数据列信息定义的构造方法</h3>
	 */
	public ConstantItem() {
		super(ItemType.CONSTANT);
	}

	/**
	 * <h3 class="en-US">Getter method for the constant value</h3>
	 * <h3 class="zh-CN">常量值的Getter方法</h3>
	 *
	 * @return <span class="en-US">Constant value</span>
	 * <span class="zh-CN">常量值</span>
	 */
	public String getConstantValue() {
		return this.constantValue;
	}

	/**
	 * <h3 class="en-US">Setter method for the constant value</h3>
	 * <h3 class="zh-CN">常量值的Setter方法</h3>
	 *
	 * @param constantValue <span class="en-US">Constant value</span>
	 *                      <span class="zh-CN">常量值</span>
	 */
	public void setConstantValue(final String constantValue) {
		this.constantValue = constantValue;
	}
}
