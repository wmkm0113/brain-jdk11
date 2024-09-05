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

package org.nervousync.brain.annotations.dialect;

import java.lang.annotation.*;

/**
 * <h2 class="en-US">Data type annotation define</h2>
 * <h2 class="zh-CN">数据类型注解定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision : 1.0.0 $ $Date: Jun 25, 2018 08:42:51 $
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface DataType {

	/**
	 * <h3 class="en-US">Obtain JDBC data type code</h3>
	 * <h3 class="zh-CN">获取JDBC数据类型代码</h3>
	 *
	 * @return <span class="en-US">JDBC data type code</span>
	 * <span class="zh-CN">JDBC数据类型代码</span>
	 */
	int code();

	/**
	 * <h3 class="en-US">Obtain data column define string</h3>
	 * <h3 class="zh-CN">获取数据列类型字符串</h3>
	 *
	 * @return <span class="en-US">Data column define string</span>
	 * <span class="zh-CN">数据列类型字符串</span>
	 */
	String type();

}
