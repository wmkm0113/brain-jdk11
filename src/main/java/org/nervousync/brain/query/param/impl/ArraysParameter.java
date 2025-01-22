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

import jakarta.xml.bind.annotation.*;
import org.nervousync.brain.enumerations.query.ItemType;
import org.nervousync.brain.query.data.ArrayData;
import org.nervousync.brain.query.param.AbstractParameter;

/**
 * <h2 class="en-US">Condition data array parameter information define</h2>
 * <h2 class="zh-CN">匹配值数组参数定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 9, 2020 18:05:28 $
 */
@XmlType(name = "arrays_parameter", namespace = "https://nervousync.org/schemas/brain")
@XmlRootElement(name = "arrays_parameter", namespace = "https://nervousync.org/schemas/brain")
public final class ArraysParameter extends AbstractParameter<ArrayData> {

    /**
     * <span class="en-US">Serial version UID</span>
     * <span class="zh-CN">序列化UID</span>
     */
    private static final long serialVersionUID = -2063549662005019056L;
    /**
     * <span class="en-US">Parameter value</span>
     * <span class="zh-CN">参数值</span>
     */
    @XmlElement(name = "array_data", namespace = "https://nervousync.org/schemas/brain")
    private ArrayData itemValue;

    /**
     * <h3 class="en-US">Constructor method for condition data array parameter information define</h3>
     * <h3 class="zh-CN">匹配值数组参数定义的构造方法</h3>
     */
    public ArraysParameter() {
        super(ItemType.ARRAY);
    }

    @Override
    public ArrayData getItemValue() {
        return this.itemValue;
    }

    @Override
    public void setItemValue(final ArrayData itemValue) {
        this.itemValue = itemValue;
    }
}
