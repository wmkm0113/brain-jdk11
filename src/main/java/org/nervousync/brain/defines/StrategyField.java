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

package org.nervousync.brain.defines;

import java.io.Serializable;

/**
 * <h2 class="en-US">Sharding strategy fields information</h2>
 * <h2 class="zh-CN">分片属性配置信息</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Nov 4, 2020 16:33:28 $
 */
@SuppressWarnings("unused")
public final class StrategyField implements Serializable {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = -86570373639161969L;

	/**
	 * <span class="en-US">Sort code</span>
	 * <span class="zh-CN">排序代码</span>
	 */
	private int sortCode;
	/**
	 * <span class="en-US">Data field name</span>
	 * <span class="zh-CN">数据列名称</span>
	 */
	private String fieldName;

	/**
	 * <h3 class="en-US">Getter method for the sort code</h3>
	 * <h3 class="zh-CN">排序代码的Getter方法</h3>
	 *
	 * @return <span class="en-US">Sort code</span>
	 * <span class="zh-CN">排序代码</span>
	 */
	public int getSortCode() {
		return this.sortCode;
	}

	/**
	 * <h3 class="en-US">Setter method for the sort code</h3>
	 * <h3 class="zh-CN">排序代码的Setter方法</h3>
	 *
	 * @param sortCode <span class="en-US">Sort code</span>
	 *                 <span class="zh-CN">排序代码</span>
	 */
	public void setSortCode(final int sortCode) {
		this.sortCode = sortCode;
	}

	/**
	 * <h3 class="en-US">Getter method for the data field name</h3>
	 * <h3 class="zh-CN">数据列名称的Getter方法</h3>
	 *
	 * @return <span class="en-US">Data field name</span>
	 * <span class="zh-CN">数据列名称</span>
	 */
	public String getFieldName() {
		return this.fieldName;
	}

	/**
	 * <h3 class="en-US">Setter method for the data field name</h3>
	 * <h3 class="zh-CN">数据列名称的Setter方法</h3>
	 *
	 * @param fieldName <span class="en-US">Data field name</span>
	 *                  <span class="zh-CN">数据列名称</span>
	 */
	public void setFieldName(final String fieldName) {
		this.fieldName = fieldName;
	}
}
