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

package org.nervousync.brain.enumerations.remote;

import jakarta.xml.bind.annotation.XmlEnum;

/**
 * <h2 class="en-US">Enumeration value of remote schema type</h2>
 * <h2 class="zh-CN">远程数据源类型的枚举值</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Jun 26, 2018 11:39:22 $
 */
@XmlEnum
public enum RemoteType {
	/**
	 * <span class="en-US">SOAP request</span>
	 * <span class="zh-CN">SOAP请求</span>
	 */
	SOAP,
	/**
	 * <span class="en-US">Restful request</span>
	 * <span class="zh-CN">Restful请求</span>
	 */
	Restful
}
