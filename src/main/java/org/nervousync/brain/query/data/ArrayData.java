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

package org.nervousync.brain.query.data;

import jakarta.xml.bind.annotation.*;
import org.nervousync.beans.core.BeanObject;

/**
 * <h2 class="en-US">Array objects define</h2>
 * <h2 class="zh-CN">数组值定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 9, 2020 18:10:21 $
 */
@XmlType(name = "array_data", namespace = "https://nervousync.org/schemas/brain")
@XmlRootElement(name = "array_data", namespace = "https://nervousync.org/schemas/brain")
public final class ArrayData extends BeanObject {

    /**
     * <span class="en-US">Serial version UID</span>
     * <span class="zh-CN">序列化UID</span>
     */
    private static final long serialVersionUID = 5864161740076618428L;

    /**
     * <span class="en-US">Array objects</span>
     * <span class="zh-CN">数组值</span>
     */
    @XmlElement(name = "data_value")
    @XmlElementWrapper(name = "array_objects")
    private Object[] arrayObject;

    /**
     * <h3 class="en-US">Constructor method for array objects define</h3>
     * <h3 class="zh-CN">数组值定义的构造方法</h3>
     */
    public ArrayData() {
    }

    /**
     * <h3 class="en-US">Constructor method for array objects define</h3>
     * <h3 class="zh-CN">数组值定义的构造方法</h3>
     *
     * @param arrayObject <span class="en-US">Array objects</span>
     *                    <span class="zh-CN">数组值</span>
     */
    public ArrayData(final Object[] arrayObject) {
        this.arrayObject = arrayObject;
    }

    /**
     * <h3 class="en-US">Getter method for array objects</h3>
     * <h3 class="zh-CN">数组值的Getter方法</h3>
     *
     * @return <span class="en-US">Array objects</span>
     * <span class="zh-CN">数组值</span>
     */
    public Object[] getArrayObject() {
        return arrayObject;
    }

    /**
     * <h3 class="en-US">Setter method for array objects</h3>
     * <h3 class="zh-CN">数组值的Setter方法</h3>
     *
     * @param arrayObject <span class="en-US">Array objects</span>
     *                    <span class="zh-CN">数组值</span>
     */
    public void setArrayObject(final Object[] arrayObject) {
        this.arrayObject = arrayObject;
    }
}
