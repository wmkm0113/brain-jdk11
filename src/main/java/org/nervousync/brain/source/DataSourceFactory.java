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
import org.nervousync.brain.configs.BrainConfigure;
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

	@Override
	public Object getObjectInstance(final Object obj, final Name name, final Context nameCtx,
	                                final Hashtable<?, ?> environment) throws Exception {
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
		BrainDataSource dataSource;
		try {
			dataSource = (BrainDataSource) context.lookup(lookupName);
		} catch (Exception e) {
			dataSource = null;
		}

		if (dataSource == null) {
			BrainConfigure configure = null;

			if (StringUtils.notBlank(filePath)) {
				configure = StringUtils.fileToObject(filePath, BrainConfigure.class);
			}
			if (configure == null) {
				throw new DataParseException(0x00DB00000036L);
			}
			dataSource = BrainDataSource.getInstance();
			context.bind(lookupName, dataSource);
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				try {
					context.unbind(lookupName);
				} catch (NamingException ignore) {
				}
				BrainDataSource.destroy();
			}));
			dataSource.initialize(configure);
		}
		return dataSource;
	}
}
