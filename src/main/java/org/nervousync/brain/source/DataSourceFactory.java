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
import org.nervousync.brain.enumerations.ddl.DDLType;
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
		String jndiName = Globals.DEFAULT_VALUE_STRING;
		boolean jmxEnabled = Boolean.FALSE, lazyInit = Boolean.FALSE;
		DDLType ddlType = DDLType.NONE;

		Enumeration<RefAddr> enumeration = ((Reference) obj).getAll();
		while (enumeration.hasMoreElements()) {
			RefAddr refAddr = enumeration.nextElement();
			switch (refAddr.getType().toLowerCase()) {
				case BrainCommons.PROPERTY_JNDI_NAME_KEY:
					jndiName = (String) refAddr.getContent();
					break;
				case BrainCommons.PROPERTY_LAZY_INIT_KEY:
					lazyInit = Boolean.parseBoolean((String) refAddr.getContent());
					break;
				case BrainCommons.PROPERTY_JMX_MONITOR_KEY:
					jmxEnabled = Boolean.parseBoolean((String) refAddr.getContent());
					break;
				case BrainCommons.PROPERTY_DDL_TYPE_KEY:
					ddlType = DDLType.valueOf((String) refAddr.getContent());
					break;
			}
		}
		if (StringUtils.isEmpty(jndiName)) {
			jndiName = BrainCommons.DEFAULT_JNDI_NAME;
		}

		initialize(jndiName, jmxEnabled, lazyInit, ddlType, environment);
		return getInstance(jndiName, environment);
	}

	/**
	 * <h3 class="en-US">Initialize data source</h3>
	 * <h3 class="zh-CN">初始化数据源</h3>
	 *
	 * @param jndiName    <span class="en-US">JNDI name</span>
	 *                    <span class="zh-CN">JNDI名称</span>
	 * @param jmxEnabled  <span class="en-US">Data source initialize status</span>
	 *                    <span class="zh-CN">数据源初始化状态</span>
	 * @param lazyInit    <span class="en-US">Perform initialization operations when using</span>
	 *                    <span class="zh-CN">使用时再执行初始化操作</span>
	 * @param ddlType     <span class="en-US">Enumeration value of DDL operate</span>
	 *                    <span class="zh-CN">操作类型枚举值</span>
	 * @param environment <span class="en-US">Environment parameters</span>
	 *                    <span class="zh-CN">环境变量</span>
	 * @throws NamingException <span class="en-US">If an error occurs while reading or writing environment variables</span>
	 *                         <span class="zh-CN">如果读取或写入环境变量时出错</span>
	 */
	public static void initialize(final String jndiName, final boolean jmxEnabled, final boolean lazyInit,
	                              final DDLType ddlType, final Hashtable<?, ?> environment) throws NamingException {
		if (StringUtils.isEmpty(jndiName)) {
			return;
		}
		Context context = new InitialContext(environment);
		BrainDataSource dataSource = (BrainDataSource) context.lookup("java:comp/env/" + jndiName);
		if (dataSource == null) {
			dataSource = new BrainDataSource(jmxEnabled, lazyInit, ddlType);
			context.bind("java:comp/env/" + jndiName, dataSource);
		}
	}

	/**
	 * <h3 class="en-US">Destroy data source</h3>
	 * <h3 class="zh-CN">销毁数据源</h3>
	 *
	 * @param jndiName <span class="en-US">JNDI name</span>
	 *                 <span class="zh-CN">JNDI名称</span>
	 * @throws NamingException <span class="en-US">If an error occurs while reading or writing environment variables</span>
	 *                         <span class="zh-CN">如果读取或写入环境变量时出错</span>
	 */
	public static void destroy(final String jndiName) throws NamingException {
		Context context = new InitialContext();
		BrainDataSource dataSource = (BrainDataSource) context.lookup("java:comp/env/" + jndiName);
		if (dataSource != null) {
			dataSource.close();
			context.unbind("java:comp/env/" + jndiName);
		}
	}

	/**
	 * <h3 class="en-US">Get the data source in the environment variable</h3>
	 * <h3 class="zh-CN">获取环境变量中的数据源</h3>
	 *
	 * @param jndiName    <span class="en-US">JNDI name</span>
	 *                    <span class="zh-CN">JNDI名称</span>
	 * @param environment <span class="en-US">Environment parameters</span>
	 *                    <span class="zh-CN">环境变量</span>
	 * @return <span class="en-US">Data source instance object</span>
	 * <span class="zh-CN">数据源实例对象</span>
	 * @throws NamingException <span class="en-US">If an error occurs while reading or writing environment variables</span>
	 *                         <span class="zh-CN">如果读取或写入环境变量时出错</span>
	 */
	public static BrainDataSource getInstance(final String jndiName, final Hashtable<?, ?> environment)
			throws NamingException {
		if (StringUtils.isEmpty(jndiName)) {
			return null;
		}
		return (BrainDataSource) new InitialContext(environment).lookup("java:comp/env/" + jndiName);
	}
}
