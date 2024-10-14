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

package org.nervousync.brain.source;

import org.nervousync.brain.commons.BrainCommons;
import org.nervousync.brain.commons.DataUtils;
import org.nervousync.brain.configs.Configure;
import org.nervousync.brain.exceptions.data.DataParseException;
import org.nervousync.commons.Globals;
import org.nervousync.utils.StringUtils;

import javax.naming.*;
import javax.naming.spi.ObjectFactory;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * <h2 class="en-US">Data source factory implementation class</h2>
 * <h2 class="zh-CN">数据源工厂实现类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Nov 12, 2020 13:08:12 $
 */
public final class DataSourceFactory implements ObjectFactory {

	/**
	 * <span class="en-US">Initialized data source instance object</span>
	 * <span class="zh-CN">初始化的数据源实例对象</span>
	 */
	private static BrainDataSource DATA_SOURCE = null;

	@Override
	public Object getObjectInstance(final Object obj, final Name name, final Context nameCtx,
	                                final Hashtable<?, ?> environment) throws Exception {
		if (DATA_SOURCE == null) {
			String jndiName = Globals.DEFAULT_VALUE_STRING, filePath = Globals.DEFAULT_VALUE_STRING;

			Enumeration<RefAddr> enumeration = ((Reference) obj).getAll();
			while (enumeration.hasMoreElements()) {
				RefAddr refAddr = enumeration.nextElement();
				switch (refAddr.getType().toLowerCase()) {
					case BrainCommons.PROPERTY_JNDI_NAME_KEY:
						jndiName = (String) refAddr.getContent();
						break;
					case BrainCommons.PROPERTY_PATH_KEY:
						filePath = (String) refAddr.getContent();
						break;
				}
			}
			String lookupName =
					"java:comp/env/" + (StringUtils.isEmpty(jndiName) ? BrainCommons.DEFAULT_JNDI_NAME : jndiName);

			Context context = new InitialContext(environment);
			try {
				DATA_SOURCE = (BrainDataSource) context.lookup(lookupName);
			} catch (Exception e) {
				Configure configure = null;

				if (StringUtils.notBlank(filePath)) {
					configure = StringUtils.fileToObject(filePath, Configure.class);
				}
				if (configure == null) {
					throw new DataParseException(0x00DB00000036L);
				}
				DATA_SOURCE = new BrainDataSource(configure);
				context.bind(lookupName, DATA_SOURCE);
				if (configure.getStorageConfig() != null) {
					DataUtils.initialize(DATA_SOURCE, configure.getStorageConfig());
				}
				Runtime.getRuntime().addShutdownHook(new Thread(() -> {
					try {
						context.unbind(lookupName);
					} catch (NamingException ignore) {
					}
					DATA_SOURCE.close();
				}));
			}
		}
		return DATA_SOURCE;
	}

	/**
	 * <h3 class="en-US">Initialize data source</h3>
	 * <h3 class="zh-CN">初始化数据源</h3>
	 *
	 * @param filePath <span class="en-US">Configure information file path</span>
	 *                 <span class="zh-CN">配置信息地址</span>
	 * @return <span class="en-US">Initialized data source instance object</span>
	 * <span class="zh-CN">初始化的数据源实例对象</span>
	 * @throws DataParseException <span class="en-US">If an error occurs while reading configure information</span>
	 *                            <span class="zh-CN">如果读取配置文件时出错</span>
	 */
	public static BrainDataSource initialize(final String filePath) throws DataParseException {
		if (DATA_SOURCE == null) {
			if (StringUtils.isEmpty(filePath)) {
				return null;
			}
			Configure configure = StringUtils.fileToObject(filePath, Configure.class);
			if (configure == null) {
				throw new DataParseException(0x00DB00000036L);
			}
			initialize(configure);
		}
		return DATA_SOURCE;
	}

	/**
	 * <h3 class="en-US">Initialize data source</h3>
	 * <h3 class="zh-CN">初始化数据源</h3>
	 *
	 * @param configure <span class="en-US">Data source configure information instance object</span>
	 *                  <span class="zh-CN">数据源配置信息实例对象</span>
	 * @throws DataParseException <span class="en-US">If configure information is <code>null</code></span>
	 *                            <span class="zh-CN">如果配置信息为<code>null</code></span>
	 */
	public static void initialize(final Configure configure) throws DataParseException {
		if (DATA_SOURCE == null) {
			if (configure == null) {
				throw new DataParseException(0x00DB00000036L);
			}
			DATA_SOURCE = new BrainDataSource(configure);
		}
	}

	/**
	 * <h3 class="en-US">Get the data source in the environment variable</h3>
	 * <h3 class="zh-CN">获取环境变量中的数据源</h3>
	 *
	 * @return <span class="en-US">Data source instance object</span>
	 * <span class="zh-CN">数据源实例对象</span>
	 */
	public static BrainDataSource getInstance() {
		return DATA_SOURCE;
	}
}
