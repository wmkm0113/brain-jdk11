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

package org.nervousync.brain.query.core;

import jakarta.annotation.Nonnull;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.brain.enumerations.query.OrderType;
import org.nervousync.commons.Globals;

import java.util.Comparator;

/**
 * <h2 class="en-US">Sort item define</h2>
 * <h2 class="zh-CN">排序项定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 9, 2020 11:30:54 $
 */
@XmlTransient
public abstract class SortedItem extends BeanObject {
    /**
     * <span class="en-US">Serial version UID</span>
     * <span class="zh-CN">序列化UID</span>
     */
    private static final long serialVersionUID = -8808799678096056072L;

    /**
     * <span class="en-US">Sort code</span>
     * <span class="zh-CN">排序代码</span>
     */
    @XmlElement(name = "sort_code")
    private int sortCode;

    /**
     * <h3 class="en-US">Getter method for sort code</h3>
     * <h3 class="zh-CN">排序代码的Getter方法</h3>
     *
     * @return <span class="en-US">Sort code</span>
     * <span class="zh-CN">排序代码</span>
     */
    public int getSortCode() {
        return sortCode;
    }

    /**
     * <h3 class="en-US">Setter method for sort code</h3>
     * <h3 class="zh-CN">排序代码的Setter方法</h3>
     *
     * @param sortCode <span class="en-US">Sort code</span>
     *                 <span class="zh-CN">排序代码</span>
     */
    public void setSortCode(int sortCode) {
        this.sortCode = sortCode;
    }

	/**
	 * <h3 class="en-US">Generate ASC mode comparator instance</h3>
	 * <h3 class="zh-CN">生成升序排序比较器</h3>
	 *
	 * @return <span class="en-US">Generated ASC mode comparator instance</span>
	 * <span class="zh-CN">生成的升序排序比较器</span>
	 */
    public static SortedItemComparator asc() {
        return new SortedItemComparator(OrderType.ASC);
    }

	/**
	 * <h3 class="en-US">Generate DESC mode comparator instance</h3>
	 * <h3 class="zh-CN">生成降序排序比较器</h3>
	 *
	 * @return <span class="en-US">Generated DESC mode comparator instance</span>
	 * <span class="zh-CN">生成的降序排序比较器</span>
	 */
    public static SortedItemComparator desc() {
        return new SortedItemComparator(OrderType.DESC);
    }

	/**
	 * <h2 class="en-US">Sort item comparator</h2>
	 * <h2 class="zh-CN">排序项比较器</h2>
	 *
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision: 1.0.0 $ $Date: Oct 9, 2020 11:38:17 $
	 */
    public static final class SortedItemComparator implements Comparator<SortedItem> {

		/**
		 * <span class="en-US">Order type</span>
		 * <span class="zh-CN">排序类型</span>
		 */
        private final OrderType orderType;

		/**
		 * <h3 class="en-US">Private constructor method for sort item comparator</h3>
		 * <h3 class="zh-CN">排序项比较器的私有构造方法</h3>
		 *
		 * @param orderType <span class="en-US">Order type</span>
		 *                  <span class="zh-CN">排序类型</span>
		 */
        private SortedItemComparator(@Nonnull final OrderType orderType) {
            this.orderType = orderType;
        }

		/**
		 * (Non-javadoc)
		 *
		 * @see Comparator#compare(Object, Object)
		 */
        @Override
        public int compare(final SortedItem o1, final SortedItem o2) {
            int compare = Integer.compare(o1.getSortCode(), o2.getSortCode());
            if (OrderType.DESC.equals(this.orderType)) {
                compare *= Globals.DEFAULT_VALUE_INT;
            }
            return compare;
        }
    }
}
