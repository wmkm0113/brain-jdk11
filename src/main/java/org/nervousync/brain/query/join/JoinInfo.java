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
import org.nervousync.beans.core.BeanObject;
import org.nervousync.utils.StringUtils;

/**
 * <h2 class="en-US">Join column define</h2>
 * <h2 class="zh-CN">关联列信息定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Feb 17, 2021 16:35:51 $
 */
@XmlType(name = "join_info", namespace = "https://nervousync.org/schemas/brain")
@XmlRootElement(name = "join_info", namespace = "https://nervousync.org/schemas/brain")
@XmlAccessorType(XmlAccessType.NONE)
public final class JoinInfo extends BeanObject {
	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = 8163690027798389179L;

	/**
	 * <span class="en-US">Join column identify code</span>
	 * <span class="zh-CN">关联列识别代码</span>
	 */
	@XmlElement(name = "join_key")
	private String joinKey;
	/**
	 * <span class="en-US">Reference column identify code</span>
	 * <span class="zh-CN">目标列识别代码</span>
	 */
	@XmlElement(name = "reference_key")
	private String referenceKey;

	/**
	 * <h3 class="en-US">Constructor method for join column define</h3>
	 * <h3 class="zh-CN">关联列信息定义的构造方法</h3>
	 */
	public JoinInfo() {
	}

	/**
	 * <h3 class="en-US">Static method is used to generate join column information instance objects</h3>
	 * <h3 class="zh-CN">静态方法用于生成关联列信息实例对象</h3>
	 *
	 * @param mainKey        <span class="en-US">Join column identify code</span>
	 *                       <span class="zh-CN">关联列识别代码</span>
	 * @param referenceKey   <span class="en-US">Reference column identify code</span>
	 *                       <span class="zh-CN">目标列识别代码</span>
	 * @return <span class="en-US">
	 * The generated associated column information instance object. If the data column
	 * identification code cannot find the corresponding data column definition,
	 * <code>null</code> will be returned.
	 * </span>
	 * <span class="zh-CN">生成的关联列信息实例对象，如果数据列识别代码不能找到对应的数据列定义则返回<code>null</code></span>
	 */
	public static JoinInfo newInstance(final String mainKey, final String referenceKey) {
		JoinInfo joinInfo = null;
		if (StringUtils.notBlank(referenceKey)) {
			joinInfo = new JoinInfo();
			joinInfo.setJoinKey(mainKey);
			joinInfo.setReferenceKey(referenceKey);
		}
		return joinInfo;
	}

	/**
	 * <h3 class="en-US">Getter method for join column identify code</h3>
	 * <h3 class="zh-CN">关联列识别代码的Getter方法</h3>
	 *
	 * @return <span class="en-US">Join column identify code</span>
	 * <span class="zh-CN">关联列识别代码</span>
	 */
	public String getJoinKey() {
		return this.joinKey;
	}

	/**
	 * <h3 class="en-US">Setter method for join column identify code</h3>
	 * <h3 class="zh-CN">关联列识别代码的Setter方法</h3>
	 *
	 * @param joinKey <span class="en-US">Join column identify code</span>
	 *                <span class="zh-CN">关联列识别代码</span>
	 */
	public void setJoinKey(final String joinKey) {
		this.joinKey = joinKey;
	}

	/**
	 * <h3 class="en-US">Getter method for reference column identify code</h3>
	 * <h3 class="zh-CN">目标列识别代码的Getter方法</h3>
	 *
	 * @return <span class="en-US">Reference column identify code</span>
	 * <span class="zh-CN">目标列识别代码</span>
	 */
	public String getReferenceKey() {
		return this.referenceKey;
	}

	/**
	 * <h3 class="en-US">Setter method for reference column identify code</h3>
	 * <h3 class="zh-CN">目标列识别代码的Setter方法</h3>
	 *
	 * @param referenceKey <span class="en-US">Reference column identify code</span>
	 *                     <span class="zh-CN">目标列识别代码</span>
	 */
	public void setReferenceKey(final String referenceKey) {
		this.referenceKey = referenceKey;
	}
}
