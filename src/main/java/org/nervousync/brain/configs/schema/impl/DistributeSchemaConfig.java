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

package org.nervousync.brain.configs.schema.impl;

import jakarta.xml.bind.annotation.*;
import org.nervousync.brain.configs.schema.SchemaConfig;
import org.nervousync.brain.configs.server.ServerInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * <h2 class="en-US">Distribute data source configuration information</h2>
 * <h2 class="zh-CN">分布式数据源配置信息</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision : 1.0.0 $ $Date: Jul 12, 2020 16:55:07 $
 */
@XmlType(name = "distribute_schema", namespace = "https://nervousync.org/schemas/database")
@XmlRootElement(name = "distribute_schema", namespace = "https://nervousync.org/schemas/database")
@XmlAccessorType(XmlAccessType.NONE)
public final class DistributeSchemaConfig extends SchemaConfig {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = -4563321804255865973L;

	/**
	 * <span class="en-US">Database server info list</span>
	 * <span class="zh-CN">数据库服务器列表</span>
	 */
	@XmlElementWrapper(name = "server_list")
	@XmlElement(name = "server_info")
	private List<ServerInfo> serverList = new ArrayList<>();
	/**
	 * <span class="en-US">Database name</span>
	 * <span class="zh-CN">数据库名称</span>
	 */
	@XmlElement(name = "database_name")
	private String databaseName;
	/**
	 * <span class="en-US">Using SSL when connect to server</span>
	 * <span class="zh-CN">使用SSL连接</span>
	 */
	@XmlElement(name = "use_ssl")
	private boolean useSsl;

	/**
	 * <h3 class="en-US">Constructor method for distribute data source configuration information</h3>
	 * <h3 class="zh-CN">分布式数据源配置信息的构造方法</h3>
	 */
	public DistributeSchemaConfig() {
	}

	/**
	 * <h3 class="en-US">Getter method for database server info list</h3>
	 * <h3 class="zh-CN">数据库服务器列表的Getter方法</h3>
	 *
	 * @return <span class="en-US">Database server info list</span>
	 * <span class="zh-CN">数据库服务器列表</span>
	 */
	public List<ServerInfo> getServerList() {
		return this.serverList;
	}

	/**
	 * <h3 class="en-US">Setter method for database server info list</h3>
	 * <h3 class="zh-CN">数据库服务器列表的Setter方法</h3>
	 *
	 * @param serverList <span class="en-US">Database server info list</span>
	 *                   <span class="zh-CN">数据库服务器列表</span>
	 */
	public void setServerList(final List<ServerInfo> serverList) {
		this.serverList = serverList;
	}

	/**
	 * <h3 class="en-US">Getter method for database name</h3>
	 * <h3 class="zh-CN">数据库名称的Getter方法</h3>
	 *
	 * @return <span class="en-US">Database name</span>
	 * <span class="zh-CN">数据库名称</span>
	 */
	public String getDatabaseName() {
		return this.databaseName;
	}

	/**
	 * <h3 class="en-US">Setter method for database name</h3>
	 * <h3 class="zh-CN">数据库名称的Setter方法</h3>
	 *
	 * @param databaseName <span class="en-US">Database name</span>
	 *                     <span class="zh-CN">数据库名称</span>
	 */
	public void setDatabaseName(final String databaseName) {
		this.databaseName = databaseName;
	}

	/**
	 * <h3 class="en-US">Getter method for using SSL when connect to server</h3>
	 * <h3 class="zh-CN">使用SSL连接的Getter方法</h3>
	 *
	 * @return <span class="en-US">Using SSL when connect to server</span>
	 * <span class="zh-CN">使用SSL连接</span>
	 */
	public boolean isUseSsl() {
		return this.useSsl;
	}

	/**
	 * <h3 class="en-US">Setter method for using SSL when connect to server</h3>
	 * <h3 class="zh-CN">使用SSL连接的Setter方法</h3>
	 *
	 * @param useSsl <span class="en-US">Using SSL when connect to server</span>
	 *               <span class="zh-CN">使用SSL连接</span>
	 */
	public void setUseSsl(final boolean useSsl) {
		this.useSsl = useSsl;
	}
}
