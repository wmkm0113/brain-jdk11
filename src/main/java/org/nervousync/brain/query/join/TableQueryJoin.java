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

package org.nervousync.brain.query.join;

import jakarta.xml.bind.annotation.*;

/**
 * <h2 class="en-US">Query join information defines</h2>
 * <h2 class="zh-CN">查询关联信息定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Jul 30, 2023 15:57:33 $
 */
@XmlType(name = "table_query_join", namespace = "https://nervousync.org/schemas/brain")
@XmlRootElement(name = "table_query_join", namespace = "https://nervousync.org/schemas/brain")
@XmlAccessorType(XmlAccessType.NONE)
public final class TableQueryJoin extends QueryJoin {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = 8868119078098035574L;

	/**
	 * <span class="en-US">Join table name</span>
	 * <span class="zh-CN">关联表名</span>
	 */
	@XmlElement(name = "join_table")
	private String joinTable;

	/**
	 * <h3 class="en-US">Private constructor method for query join information define</h3>
	 * <h3 class="zh-CN">查询关联信息定义的私有构造方法</h3>
	 */
	public TableQueryJoin() {
	}

	/**
	 * <h3 class="en-US">Getter method for the join table name</h3>
	 * <h3 class="zh-CN">关联表名的Getter方法</h3>
	 *
	 * @return <span class="en-US">Join table name</span>
	 * <span class="zh-CN">关联表名</span>
	 */
	public String getJoinTable() {
		return this.joinTable;
	}

	/**
	 * <h3 class="en-US">Setter method for the join table name</h3>
	 * <h3 class="zh-CN">关联表名的Setter方法</h3>
	 *
	 * @param joinTable <span class="en-US">Join table name</span>
	 *                  <span class="zh-CN">关联表名</span>
	 */
	public void setJoinTable(final String joinTable) {
		this.joinTable = joinTable;
	}
}
