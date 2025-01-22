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

import org.nervousync.commons.Globals;

import java.lang.annotation.*;

/**
 * <h2 class="en-US">Database dialect annotation define</h2>
 * <h2 class="zh-CN">数据库方言注解定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision : 1.0.0 $ $Date: Jun 25, 2018 08:42:51 $
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface SchemaDialect {

	/**
	 * <h3 class="en-US">Support join query</h3>
	 * <h3 class="zh-CN">支持关联查询</h3>
	 *
	 * @return <span class="en-US">Support join query</span>
	 * <span class="zh-CN">支持关联查询</span>
	 */
	boolean supportJoin();

	/**
	 * <h3 class="en-US">Support database connection pool</h3>
	 * <h3 class="zh-CN">支持数据库连接池</h3>
	 *
	 * @return <span class="en-US">Support database connection pool</span>
	 * <span class="zh-CN">支持数据库连接池</span>
	 */
	boolean connectionPool() default true;

	/**
	 * <h3 class="en-US">Query command to test connection validity</h3>
	 * <h3 class="zh-CN">测试连接有效性的查询命令</h3>
	 *
	 * @return <span class="en-US">Query command</span>
	 * <span class="zh-CN">查询命令</span>
	 */
	String validationQuery() default Globals.DEFAULT_VALUE_STRING;

	/**
	 * <h3 class="en-US">Data type definition annotation array</h3>
	 * <h3 class="zh-CN">数据类型定义注解数组</h3>
	 *
	 * @return <span class="en-US">Annotation array</span>
	 * <span class="zh-CN">注解数组</span>
	 */
	DataType[] types();
}
