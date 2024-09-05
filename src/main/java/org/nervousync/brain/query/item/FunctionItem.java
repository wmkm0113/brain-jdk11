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
import org.nervousync.brain.query.param.AbstractParameter;
import org.nervousync.brain.query.param.impl.ColumnParameter;
import org.nervousync.brain.query.param.impl.ConstantParameter;
import org.nervousync.brain.query.param.impl.FunctionParameter;

import java.util.*;

/**
 * <h2 class="en-US">Query function information define</h2>
 * <h2 class="zh-CN">查询数据函数信息定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 9, 2020 11:42:19 $
 */
@XmlType(name = "function_item", namespace = "https://nervousync.org/schemas/database")
@XmlRootElement(name = "function_item", namespace = "https://nervousync.org/schemas/database")
@XmlAccessorType(XmlAccessType.NONE)
public final class FunctionItem extends AbstractItem {

    /**
     * <span class="en-US">Serial version UID</span>
     * <span class="zh-CN">序列化UID</span>
     */
    private static final long serialVersionUID = 4463684389449026498L;

    /**
     * <span class="en-US">Function name</span>
     * <span class="zh-CN">函数名</span>
     */
    @XmlElement(name = "sql_function")
    private String sqlFunction = null;
    /**
     * <span class="en-US">Function arguments array</span>
     * <span class="zh-CN">函数参数数组</span>
     */
    @XmlElements({
            @XmlElement(name = "column_parameter", type = ColumnParameter.class, namespace = "https://nervousync.org/schemas/database"),
            @XmlElement(name = "constant_parameter", type = ConstantParameter.class, namespace = "https://nervousync.org/schemas/database"),
            @XmlElement(name = "function_parameter", type = FunctionParameter.class, namespace = "https://nervousync.org/schemas/database")
    })
    @XmlElementWrapper(name = "function_parameter_list")
    private List<AbstractParameter<?>> functionParams;

    /**
     * <h3 class="en-US">Constructor method for query function information define</h3>
     * <h3 class="zh-CN">查询数据函数信息定义的构造方法</h3>
     */
    public FunctionItem() {
        super(ItemType.FUNCTION);
        this.functionParams = new ArrayList<>();
    }

    /**
     * <h3 class="en-US">Getter method for function name</h3>
     * <h3 class="zh-CN">函数名的Getter方法</h3>
     *
     * @return <span class="en-US">Function name</span>
     * <span class="zh-CN">函数名</span>
     */
    public String getSqlFunction() {
        return this.sqlFunction;
    }

    /**
     * <h3 class="en-US">Setter method for function name</h3>
     * <h3 class="zh-CN">函数名的Setter方法</h3>
     *
     * @param sqlFunction <span class="en-US">Function name</span>
     *                    <span class="zh-CN">函数名</span>
     */
    public void setSqlFunction(final String sqlFunction) {
        this.sqlFunction = sqlFunction;
    }

    /**
     * <h3 class="en-US">Getter method for function arguments array</h3>
     * <h3 class="zh-CN">函数参数数组的Getter方法</h3>
     *
     * @return <span class="en-US">Function arguments array</span>
     * <span class="zh-CN">函数参数数组</span>
     */
    public List<AbstractParameter<?>> getFunctionParams() {
        return this.functionParams;
    }

    /**
     * <h3 class="en-US">Setter method for function arguments array</h3>
     * <h3 class="zh-CN">函数参数数组的Setter方法</h3>
     *
     * @param functionParams <span class="en-US">Function arguments array</span>
     *                       <span class="zh-CN">函数参数数组</span>
     */
    public void setFunctionParams(final List<AbstractParameter<?>> functionParams) {
        this.functionParams = functionParams;
    }
}
