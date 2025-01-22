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

package org.nervousync.brain.dialects;

import jakarta.annotation.Nonnull;
import org.nervousync.annotations.provider.Provider;
import org.nervousync.brain.annotations.dialect.SchemaDialect;
import org.nervousync.brain.enumerations.dialect.DialectType;
import org.nervousync.brain.exceptions.sql.MultilingualSQLException;
import org.nervousync.commons.Globals;
import org.nervousync.utils.LoggerUtils;
import org.nervousync.utils.MultilingualUtils;
import org.nervousync.utils.ObjectUtils;
import org.nervousync.utils.StringUtils;

import java.sql.SQLException;
import java.util.*;

/**
 * <h2 class="en-US">Database dialect factory, running in singleton mode</h2>
 * <h2 class="zh-CN">数据库方言工厂，使用单例模式运行</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Nov 12, 2020 13:08:12 $
 */
public final class DialectFactory {

	/**
	 * <span class="en-US">Logger instance</span>
	 * <span class="zh-CN">日志实例</span>
	 */
	private static final LoggerUtils.Logger LOGGER = LoggerUtils.getLogger(DialectFactory.class);

	/**
	 * <span class="en-US">Registered dialect implementation class instance object</span>
	 * <span class="zh-CN">已注册的方言实现类实例对象</span>
	 */
	private static final Hashtable<String, Dialect> REGISTERED_DIALECTS = new Hashtable<>();

	static {
		//  Load database dialect implementation class by Java SPI
		ServiceLoader.load(Dialect.class).forEach(DialectFactory::register);
	}

	/**
	 * <h3 class="en-US">Register the database dialect implementation class</h3>
	 * <h3 class="zh-CN">注册数据库方言实现类</h3>
	 *
	 * @param dialect <span class="en-US">Database dialect implementation class instance object</span>
	 *                <span class="zh-CN">数据库方言实现类实例对象</span>
	 */
	public static void register(@Nonnull final Dialect dialect) {
		Provider provider = dialect.getClass().getAnnotation(Provider.class);
		if (provider == null) {
			return;
		}
		Optional.of(dialect.getClass().getAnnotation(SchemaDialect.class))
				.ifPresent(schemaDialect -> {
					if (registered(provider.name())) {
						LOGGER.warn("Override_Registered_Dialect", provider.name(),
								REGISTERED_DIALECTS.get(provider.name()).getClass().getName());
					}
					REGISTERED_DIALECTS.put(provider.name(), dialect);
				});
	}

	/**
	 * <h3 class="en-US">Get a list of registered database dialect names</h3>
	 * <h3 class="zh-CN">获取已注册的数据库方言名称列表</h3>
	 *
	 * @param dialectType <span class="en-US">Database dialect type</span>
	 *                    <span class="zh-CN">数据库方言类型</span>
	 * @return <span class="en-US">Database dialect name list</span>
	 * <span class="zh-CN">数据库方言名称列表</span>
	 */
	public static List<String> dialectNames(@Nonnull final DialectType dialectType) {
		List<String> dialectNames = new ArrayList<>();
		REGISTERED_DIALECTS.entrySet()
				.stream()
				.filter(entry -> ObjectUtils.nullSafeEquals(entry.getValue().type(), dialectType))
				.forEach(entry -> dialectNames.add(entry.getKey()));
		return dialectNames;
	}

	/**
	 * <h3 class="en-US">Get the title of the given database dialect name</h3>
	 * <h3 class="zh-CN">获取给定数据库方言名称的标题</h3>
	 *
	 * @param dialectName  <span class="en-US">Database dialect name</span>
	 *                     <span class="zh-CN">数据库方言名称</span>
	 * @param languageCode <span class="en-US">Language code</span>
	 *                     <span class="zh-CN">语言代码</span>
	 * @return <span class="en-US">Display title</span>
	 * <span class="zh-CN">显示的标题</span>
	 */
	public static String displayName(final String dialectName, final String languageCode) {
		if (StringUtils.isEmpty(dialectName)) {
			return Globals.DEFAULT_VALUE_STRING;
		}
		return Optional.ofNullable(REGISTERED_DIALECTS.get(dialectName))
				.map(Dialect::getClass)
				.map(dialectClass -> MultilingualUtils.providerName(dialectClass, languageCode))
				.orElse(Globals.DEFAULT_VALUE_STRING);
	}

	/**
	 * <h3 class="en-US">Get the description of the given database dialect name</h3>
	 * <h3 class="zh-CN">获取给定数据库方言名称的介绍信息</h3>
	 *
	 * @param dialectName  <span class="en-US">Database dialect name</span>
	 *                     <span class="zh-CN">数据库方言名称</span>
	 * @param languageCode <span class="en-US">Language code</span>
	 *                     <span class="zh-CN">语言代码</span>
	 * @return <span class="en-US">Display description information</span>
	 * <span class="zh-CN">显示的介绍信息</span>
	 */
	public static String displayDescription(final String dialectName, final String languageCode) {
		if (StringUtils.isEmpty(dialectName)) {
			return Globals.DEFAULT_VALUE_STRING;
		}
		return Optional.ofNullable(REGISTERED_DIALECTS.get(dialectName))
				.map(Dialect::getClass)
				.map(dialectClass -> MultilingualUtils.providerDescription(dialectClass, languageCode))
				.orElse(Globals.DEFAULT_VALUE_STRING);
	}

	/**
	 * <h3 class="en-US">Check the registered status of given database dialect implementation name</h3>
	 * <h3 class="zh-CN">检查给定的方言名称是否注册</h3>
	 *
	 * @param dialectName <span class="en-US">Database dialect name</span>
	 *                    <span class="zh-CN">数据库方言名称</span>
	 * @return <span class="en-US">Registered status</span>
	 * <span class="zh-CN">已注册状态</span>
	 */
	public static boolean registered(final String dialectName) {
		if (StringUtils.isEmpty(dialectName)) {
			return Boolean.FALSE;
		}
		return REGISTERED_DIALECTS.containsKey(dialectName);
	}

	/**
	 * <h3 class="en-US">Obtain database dialect implementation class instance object</h3>
	 * <h3 class="zh-CN">获得数据库方言实例对象</h3>
	 *
	 * @param dialectName <span class="en-US">Database dialect name</span>
	 *                    <span class="zh-CN">数据库方言名称</span>
	 * @return <span class="en-US">Initialized instance object</span>
	 * <span class="zh-CN">初始化的实例对象</span>
	 * @throws SQLException <span class="en-US">If the given dialect name is not registered</span>
	 *                      <span class="zh-CN">如果给定的方言名称未注册</span>
	 */
	public static Dialect retrieve(final String dialectName) throws SQLException {
		return Optional.ofNullable(REGISTERED_DIALECTS.get(dialectName))
				.orElseThrow(() -> new MultilingualSQLException(0x00DB00000001L, dialectName));
	}
}
