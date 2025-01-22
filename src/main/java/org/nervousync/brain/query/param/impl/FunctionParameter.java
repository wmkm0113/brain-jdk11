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
import org.nervousync.brain.query.item.FunctionItem;
import org.nervousync.brain.query.param.AbstractParameter;

/**
 * <h2 class="en-US">Function parameter information define</h2>
 * <h2 class="zh-CN">函数参数定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 9, 2020 11:44:57 $
 */
@XmlType(name = "function_parameter", namespace = "https://nervousync.org/schemas/brain")
@XmlRootElement(name = "function_parameter", namespace = "https://nervousync.org/schemas/brain")
public final class FunctionParameter extends AbstractParameter<FunctionItem> {

    /**
     * <span class="en-US">Serial version UID</span>
     * <span class="zh-CN">序列化UID</span>
     */
    private static final long serialVersionUID = -2293421714573118396L;
    /**
     * <span class="en-US">Parameter value</span>
     * <span class="zh-CN">参数值</span>
     */
    @XmlElement(name = "function_item", namespace = "https://nervousync.org/schemas/brain")
    private FunctionItem itemValue;

    /**
     * <h3 class="en-US">Constructor method for function parameter information define</h3>
     * <h3 class="zh-CN">函数参数定义的构造方法</h3>
     */
    public FunctionParameter() {
        super(ItemType.FUNCTION);
    }

    @Override
    public FunctionItem getItemValue() {
        return this.itemValue;
    }

    @Override
    public void setItemValue(final FunctionItem itemValue) {
        this.itemValue = itemValue;
    }
}
