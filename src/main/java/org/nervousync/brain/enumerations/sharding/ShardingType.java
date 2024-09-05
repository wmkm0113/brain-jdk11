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

package org.nervousync.brain.enumerations.sharding;

/**
 * <h2 class="en-US">Enumeration of sharding type</h2>
 * <h2 class="zh-CN">分片类型的枚举类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Sep 12, 2023 15:16:08 $
 */
public enum ShardingType {
    /**
     * <span class="en-US">Database sharding</span>
     * <span class="zh-CN">数据库分片</span>
     */
	DATABASE,
    /**
     * <span class="en-US">Table sharding</span>
     * <span class="zh-CN">数据表分片</span>
     */
	TABLE
}
