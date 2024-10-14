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

package org.nervousync.brain.configs;

import jakarta.xml.bind.annotation.*;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.brain.configs.schema.SchemaConfig;
import org.nervousync.brain.configs.schema.impl.DistributeSchemaConfig;
import org.nervousync.brain.configs.schema.impl.JdbcSchemaConfig;
import org.nervousync.brain.configs.schema.impl.RemoteSchemaConfig;
import org.nervousync.brain.configs.storage.StorageConfig;
import org.nervousync.brain.enumerations.ddl.DDLType;

import java.util.ArrayList;
import java.util.List;

/**
 * <h2 class="en-US">Data source configuration information definition</h2>
 * <h2 class="zh-CN">数据源配置信息定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Dec 20, 2018 15:43:52 $
 */
@XmlType(name = "brain_config", namespace = "https://nervousync.org/schemas/database")
@XmlRootElement(name = "brain_config", namespace = "https://nervousync.org/schemas/database")
@XmlAccessorType(XmlAccessType.NONE)
public final class Configure extends BeanObject {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = -7511102852263227774L;

	/**
	 * <span class="en-US">Lazy initialize data source</span>
	 * <span class="zh-CN">懒加载初始化数据源</span>
	 */
	@XmlElement(name = "lazy_initialize")
	private boolean lazyInitialize = Boolean.FALSE;
	/**
	 * <span class="en-US">JMX monitor enabled status</span>
	 * <span class="zh-CN">JMX监控开启状态</span>
	 */
	@XmlElement(name = "jmx_monitor")
	private boolean jmxMonitor = Boolean.FALSE;
	/**
	 * <span class="en-US">Type code of entity class and database table</span>
	 * <span class="zh-CN">实体类与数据表的类型代码</span>
	 */
	@XmlElement(name = "ddl_type")
	private DDLType ddlType = DDLType.NONE;
	/**
	 * <span class="en-US">Data source configure information list</span>
	 * <span class="zh-CN">数据源配置信息列表</span>
	 */
	@XmlElementWrapper(name = "schema_list")
	@XmlElementRefs({
			@XmlElementRef(name = "distribute_schema", type = DistributeSchemaConfig.class),
			@XmlElementRef(name = "jdbc_schema", type = JdbcSchemaConfig.class),
			@XmlElementRef(name = "remote_schema", type = RemoteSchemaConfig.class)
	})
	private List<SchemaConfig> schemaConfigs;
	/**
	 * <span class="en-US">Data import/export configure information</span>
	 * <span class="zh-CN">数据导入导出配置</span>
	 */
	@XmlElement(name = "storage_config", namespace = "https://nervousync.org/schemas/database")
	private StorageConfig storageConfig = null;

	/**
	 * <h3 class="en-US">Constructor method for MAGI configuration information definition</h3>
	 * <h3 class="zh-CN">MAGI配置信息定义的构造方法</h3>
	 */
	public Configure() {
		this.schemaConfigs = new ArrayList<>();
	}

	/**
	 * <h3 class="en-US">Getter method for lazy initialize data source</h3>
	 * <h3 class="zh-CN">懒加载初始化数据源的Getter方法</h3>
	 *
	 * @return <span class="en-US">Lazy initialize data source</span>
	 * <span class="zh-CN">懒加载初始化数据源</span>
	 */
	public boolean isLazyInitialize() {
		return this.lazyInitialize;
	}

	/**
	 * <h3 class="en-US">Setter method for lazy initialize data source</h3>
	 * <h3 class="zh-CN">懒加载初始化数据源的Setter方法</h3>
	 *
	 * @param lazyInitialize <span class="en-US">Lazy initialize data source</span>
	 *                       <span class="zh-CN">懒加载初始化数据源</span>
	 */
	public void setLazyInitialize(boolean lazyInitialize) {
		this.lazyInitialize = lazyInitialize;
	}

	/**
	 * <h3 class="en-US">Getter method for JMX monitor enabled status</h3>
	 * <h3 class="zh-CN">JMX监控开启状态的Getter方法</h3>
	 *
	 * @return <span class="en-US">JMX monitor enabled status</span>
	 * <span class="zh-CN">JMX监控开启状态</span>
	 */
	public boolean isJmxMonitor() {
		return this.jmxMonitor;
	}

	/**
	 * <h3 class="en-US">Setter method for JMX monitor enabled status</h3>
	 * <h3 class="zh-CN">JMX监控开启状态的Setter方法</h3>
	 *
	 * @param jmxMonitor <span class="en-US">JMX monitor enabled status</span>
	 *                   <span class="zh-CN">JMX监控开启状态</span>
	 */
	public void setJmxMonitor(boolean jmxMonitor) {
		this.jmxMonitor = jmxMonitor;
	}

	/**
	 * <h3 class="en-US">Getter method for type code of entity class and database table</h3>
	 * <h3 class="zh-CN">实体类与数据表的类型代码的Getter方法</h3>
	 *
	 * @return <span class="en-US">Type code of entity class and database table</span>
	 * <span class="zh-CN">实体类与数据表的类型代码</span>
	 */
	public DDLType getDdlType() {
		return this.ddlType;
	}

	/**
	 * <h3 class="en-US">Setter method for type code of entity class and database table</h3>
	 * <h3 class="zh-CN">实体类与数据表的类型代码的Setter方法</h3>
	 *
	 * @param ddlType <span class="en-US">Type code of entity class and database table</span>
	 *                <span class="zh-CN">实体类与数据表的类型代码</span>
	 */
	public void setDdlType(DDLType ddlType) {
		this.ddlType = ddlType;
	}

	/**
	 * <h3 class="en-US">Getter method for data source configure information list</h3>
	 * <h3 class="zh-CN">数据源配置信息列表的Getter方法</h3>
	 *
	 * @return <span class="en-US">Data source configure information list</span>
	 * <span class="zh-CN">数据源配置信息列表</span>
	 */
	public List<SchemaConfig> getSchemaConfigs() {
		return this.schemaConfigs;
	}

	/**
	 * <h3 class="en-US">Setter method for data source configure information list</h3>
	 * <h3 class="zh-CN">数据源配置信息列表的Setter方法</h3>
	 *
	 * @param schemaConfigs <span class="en-US">Data source configure information list</span>
	 *                      <span class="zh-CN">数据源配置信息列表</span>
	 */
	public void setSchemaConfigs(final List<SchemaConfig> schemaConfigs) {
		this.schemaConfigs = schemaConfigs;
	}

	/**
	 * <h3 class="en-US">Getter method for data import/export configure information</h3>
	 * <h3 class="zh-CN">数据导入导出配置的Getter方法</h3>
	 *
	 * @return <span class="en-US">Data import/export configure information</span>
	 * <span class="zh-CN">数据导入导出配置</span>
	 */
	public StorageConfig getStorageConfig() {
		return this.storageConfig;
	}

	/**
	 * <h3 class="en-US">Setter method for data import/export configure information</h3>
	 * <h3 class="zh-CN">数据导入导出配置的Setter方法</h3>
	 *
	 * @param storageConfig <span class="en-US">Data import/export configure information</span>
	 *                      <span class="zh-CN">数据导入导出配置</span>
	 */
	public void setStorageConfig(final StorageConfig storageConfig) {
		this.storageConfig = storageConfig;
	}
}
