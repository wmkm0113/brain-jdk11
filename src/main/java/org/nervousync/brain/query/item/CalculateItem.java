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
import org.nervousync.brain.enumerations.query.CalculateCode;
import org.nervousync.brain.enumerations.query.ItemType;
import org.nervousync.brain.query.core.QueryItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <h2 class="en-US">Calculate result value information define</h2>
 * <h2 class="zh-CN">计算结果值信息定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 9, 2020 11:42:19 $
 */
@XmlType(name = "calculate_item", namespace = "https://nervousync.org/schemas/brain")
@XmlRootElement(name = "calculate_item", namespace = "https://nervousync.org/schemas/brain")
@XmlAccessorType(XmlAccessType.NONE)
public final class CalculateItem extends QueryItem {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = 8971893317009935658L;

	private static final List<ItemType> CALCULATE_ITEM_TYPES =
			Arrays.asList(ItemType.CALCULATE, ItemType.CONSTANT, ItemType.COLUMN, ItemType.FUNCTION);

	/**
	 * <span class="en-US">Enumeration value of calculate code</span>
	 * <span class="zh-CN">计算代码的枚举值</span>
	 */
	@XmlElement(name = "calculate_code")
	private CalculateCode calculateCode;
	/**
	 * <span class="en-US">List of query items participating in the calculation</span>
	 * <span class="zh-CN">参与计算的查询项信息列表</span>
	 */
	@XmlElementWrapper(name = "calculate_item_list")
	@XmlElementRefs({
			@XmlElementRef(name = "calculate_item", type = CalculateItem.class),
			@XmlElementRef(name = "constant_item", type = ConstantItem.class),
			@XmlElementRef(name = "column_item", type = ColumnItem.class),
			@XmlElementRef(name = "function_item", type = FunctionItem.class)
	})
	private List<QueryItem> calculateItems;

	/**
	 * <h3 class="en-US">Constructor method for calculate result value information define</h3>
	 * <h3 class="zh-CN">计算结果值信息定义的构造方法</h3>
	 */
	public CalculateItem() {
		super(ItemType.CALCULATE);
		this.calculateItems = new ArrayList<>();
	}

	/**
	 * <h3 class="en-US">Getter method for the enumeration value of calculate code</h3>
	 * <h3 class="zh-CN">计算代码的枚举值的Getter方法</h3>
	 *
	 * @return <span class="en-US">Enumeration value of calculate code</span>
	 * <span class="zh-CN">计算代码的枚举值</span>
	 */
	public CalculateCode getCalculateCode() {
		return this.calculateCode;
	}

	/**
	 * <h3 class="en-US">Setter method for the enumeration value of calculate code</h3>
	 * <h3 class="zh-CN">计算代码的枚举值的Setter方法</h3>
	 *
	 * @param calculateCode <span class="en-US">Enumeration value of calculate code</span>
	 *                      <span class="zh-CN">计算代码的枚举值</span>
	 */
	public void setCalculateCode(final CalculateCode calculateCode) {
		this.calculateCode = calculateCode;
	}

	/**
	 * <h3 class="en-US">Getter method for the list of query items participating in the calculation</h3>
	 * <h3 class="zh-CN">参与计算的查询项信息列表的Getter方法</h3>
	 *
	 * @return <span class="en-US">List of query items participating in the calculation</span>
	 * <span class="zh-CN">参与计算的查询项信息列表</span>
	 */
	public List<QueryItem> getCalculateItems() {
		return this.calculateItems;
	}

	/**
	 * <h3 class="en-US">Setter method for the list of query items participating in the calculation</h3>
	 * <h3 class="zh-CN">参与计算的查询项信息列表的Setter方法</h3>
	 *
	 * @param calculateItems <span class="en-US">List of query items participating in the calculation</span>
	 *                       <span class="zh-CN">参与计算的查询项信息列表</span>
	 */
	public void setCalculateItems(final List<QueryItem> calculateItems) {
		if (calculateItems != null && calculateItems.stream()
				.allMatch(abstractItem -> CALCULATE_ITEM_TYPES.contains(abstractItem.getItemType()))) {
			this.calculateItems = calculateItems;
		}
	}
}
