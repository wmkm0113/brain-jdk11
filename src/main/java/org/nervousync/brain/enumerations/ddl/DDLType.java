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

package org.nervousync.brain.enumerations.ddl;

/**
 * <h2 class="en-US">Type code of entity class and database table</h2>
 * <h2 class="zh-CN">实体类与数据表的类型代码</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Sep 12, 2023 15:16:08 $
 */
public enum DDLType {
    /**
     * <span class="en-US">None operate</span>
     * <span class="zh-CN">无操作</span>
     */
	NONE,
    /**
     * <span class="en-US">Only verification, an exception is thrown when the entity class definition is inconsistent with the data table structure</span>
     * <span class="zh-CN">仅验证，实体类定义与数据表结构不一致时抛出异常</span>
     */
	VALIDATE,
    /**
     * <span class="en-US">Only create data tables that do not exist</span>
     * <span class="zh-CN">仅创建不存在的数据表</span>
     */
	CREATE,
    /**
     * <span class="en-US">Create data tables at startup and truncate all data tables at end</span>
     * <span class="zh-CN">启动时创建数据表，结束时清空所有数据表</span>
     */
	CREATE_TRUNCATE,
    /**
     * <span class="en-US">Create data tables at startup and delete all data tables at end</span>
     * <span class="zh-CN">启动时创建数据表，结束时删除所有数据表</span>
     */
	CREATE_DROP,
    /**
     * <span class="en-US">
     *     Synchronize entity class definition and data table structure.
     *     If the data table does not exist, create the data table;
     *     if the structure is inconsistent, perform an update operation;
     *     if the mapping between the entity class and the data table is not found, delete the data table
     * </span>
     * <span class="zh-CN">
     *     同步实体类定义与数据表结构。
     *     如果数据表不存在，则创建数据表；如果结构不一致，则进行更新操作；如果未找到实体类与数据表的映射，则删除数据表
     * </span>
     */
	SYNCHRONIZE
}
