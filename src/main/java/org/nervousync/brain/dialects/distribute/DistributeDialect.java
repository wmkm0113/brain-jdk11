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

package org.nervousync.brain.dialects.distribute;

import org.nervousync.brain.configs.schema.impl.DistributeSchemaConfig;
import org.nervousync.brain.dialects.core.BaseDialect;
import org.nervousync.brain.enumerations.dialect.DialectType;
import org.nervousync.brain.exceptions.dialects.DialectException;
import org.nervousync.brain.query.param.AbstractParameter;
import org.nervousync.commons.Globals;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * <h2 class="en-US">Distribute database dialect abstract class</h2>
 * <h2 class="zh-CN">分布式数据库方言抽象类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Feb 18, 2019 10:38:52 $
 */
public abstract class DistributeDialect extends BaseDialect {

	/**
	 * <h3 class="en-US">Constructor method for distribute database dialect abstract class</h3>
	 * <h3 class="zh-CN">分布式数据库方言抽象类的构造方法</h3>
	 *
	 * @throws DialectException <span class="en-US">If the implementation class does not find the org. nervousync. brain. annotations. dialect.SchemaDialect annotation</span>
	 *                          <span class="zh-CN">如果实现类未找到org. nervousync. brain. annotations. dialect.SchemaDialect注解</span>
	 */
	protected DistributeDialect() throws DialectException {
		super(DialectType.Distribute);
	}

	@Override
	protected String parameterValue(final Map<String, String> aliasMap, final AbstractParameter<?> abstractParameter,
	                                final List<Object> values) throws SQLException {
		return Globals.DEFAULT_VALUE_STRING;
	}

	/**
	 * <h3 class="en-US">Generate database operate client</h3>
	 * <h3 class="zh-CN">生成数据库操作客户端</h3>
	 *
	 * @param schemaConfig <span class="en-US">Distribute data source configure information</span>
	 *                     <span class="zh-CN">分布式数据源配置信息</span>
	 * @return <span class="en-US">Generated database operate client</span>
	 * <span class="zh-CN">生成的数据库操作客户端</span>
	 * @throws Exception <span class="en-US">An error occurred during initialization</span>
	 *                   <span class="zh-CN">初始化过程中出错</span>
	 */
	public abstract DistributeClient newClient(final DistributeSchemaConfig schemaConfig) throws Exception;
}
