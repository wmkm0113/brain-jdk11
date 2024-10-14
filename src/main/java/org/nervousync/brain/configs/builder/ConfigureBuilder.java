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

package org.nervousync.brain.configs.builder;

import jakarta.annotation.Nonnull;
import org.nervousync.brain.commons.BrainCommons;
import org.nervousync.brain.configs.Configure;
import org.nervousync.brain.configs.schema.SchemaConfig;
import org.nervousync.brain.configs.schema.impl.DistributeSchemaConfig;
import org.nervousync.brain.configs.schema.impl.JdbcSchemaConfig;
import org.nervousync.brain.configs.schema.impl.RemoteSchemaConfig;
import org.nervousync.brain.configs.storage.StorageConfig;
import org.nervousync.brain.enumerations.ddl.DDLType;
import org.nervousync.builder.AbstractBuilder;
import org.nervousync.builder.ParentBuilder;
import org.nervousync.exceptions.builder.BuilderException;
import org.nervousync.utils.ClassUtils;
import org.nervousync.utils.ObjectUtils;

import java.util.List;

/**
 * <h2 class="en-US">Implementation class of configure information builder</h2>
 * <h2 class="zh-CN">配置信息构建器的实现类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision : 1.0.0 $ $Date: Apr 10, 2018 15:48:19 $
 */
public final class ConfigureBuilder extends AbstractBuilder<Configure> implements ParentBuilder {

	/**
	 * <span class="en-US">Data source configuration information instance object</span>
	 * <span class="zh-CN">数据源配置信息实例对象</span>
	 */
	private final Configure configure;

	/**
	 * <h3 class="en-US">Private constructor method for configure information builder implementation class</h3>
	 * <h3 class="zh-CN">配置信息构建器实现类的构造函数</h3>
	 *
	 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
	 *                      <span class="zh-CN">父构建器实例对象</span>
	 * @param configure     <span class="en-US">Data source configuration information instance object</span>
	 *                      <span class="zh-CN">数据源配置信息实例对象</span>
	 */
	private ConfigureBuilder(final ParentBuilder parentBuilder, @Nonnull final Configure configure) {
		super(parentBuilder);
		this.configure = configure;
	}

	/**
	 * <h3 class="en-US">Static method is used to initialize the current class of configuration information builder</h3>
	 * <h3 class="zh-CN">静态方法用于初始化配置信息构建器实现类</h3>
	 *
	 * @return <span class="en-US">Configuration information instance object</span>
	 * <span class="zh-CN">配置信息构建器实现类实例对象</span>
	 */
	public static ConfigureBuilder newBuilder() {
		return newBuilder(new Configure());
	}

	/**
	 * <h3 class="en-US">Static method is used to initialize the current class of configuration information builder</h3>
	 * <h3 class="zh-CN">静态方法用于初始化配置信息构建器实现类</h3>
	 *
	 * @param configure <span class="en-US">Data source configuration information instance object</span>
	 *                  <span class="zh-CN">数据源配置信息实例对象</span>
	 * @return <span class="en-US">Configuration information instance object</span>
	 * <span class="zh-CN">配置信息构建器实现类实例对象</span>
	 */
	public static ConfigureBuilder newBuilder(@Nonnull final Configure configure) {
		return newBuilder(null, configure);
	}

	/**
	 * <h3 class="en-US">Static method is used to initialize the current class of configuration information builder</h3>
	 * <h3 class="zh-CN">静态方法用于初始化配置信息构建器实现类</h3>
	 *
	 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
	 *                      <span class="zh-CN">父构建器实例对象</span>
	 * @return <span class="en-US">Configuration information instance object</span>
	 * <span class="zh-CN">配置信息构建器实现类实例对象</span>
	 */
	public static ConfigureBuilder newBuilder(final ParentBuilder parentBuilder) {
		return newBuilder(parentBuilder, new Configure());
	}

	/**
	 * <h3 class="en-US">Static method is used to initialize the current class of configuration information builder</h3>
	 * <h3 class="zh-CN">静态方法用于初始化配置信息构建器实现类</h3>
	 *
	 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
	 *                      <span class="zh-CN">父构建器实例对象</span>
	 * @param configure     <span class="en-US">Data source configuration information instance object</span>
	 *                      <span class="zh-CN">数据源配置信息实例对象</span>
	 * @return <span class="en-US">Configuration information instance object</span>
	 * <span class="zh-CN">配置信息构建器实现类实例对象</span>
	 */
	public static ConfigureBuilder newBuilder(final ParentBuilder parentBuilder, @Nonnull final Configure configure) {
		return new ConfigureBuilder(parentBuilder, configure);
	}

	/**
	 * <h3 class="en-US">Enable data source lazy load</h3>
	 * <h3 class="zh-CN">开启数据源懒加载</h3>
	 *
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public ConfigureBuilder enableLazyInit() {
		this.configure.setLazyInitialize(Boolean.TRUE);
		return this;
	}

	/**
	 * <h3 class="en-US">Disable data source lazy load</h3>
	 * <h3 class="zh-CN">关闭数据源懒加载</h3>
	 *
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public ConfigureBuilder disableLazyInit() {
		this.configure.setLazyInitialize(Boolean.FALSE);
		return this;
	}

	/**
	 * <h3 class="en-US">Enable data source JMX monitor</h3>
	 * <h3 class="zh-CN">开启数据源JMX监控</h3>
	 *
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public ConfigureBuilder enableJmxMonitor() {
		this.configure.setJmxMonitor(Boolean.TRUE);
		return this;
	}

	/**
	 * <h3 class="en-US">Disable data source JMX monitor</h3>
	 * <h3 class="zh-CN">关闭数据源JMX监控</h3>
	 *
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public ConfigureBuilder disableJmxMonitor() {
		this.configure.setJmxMonitor(Boolean.FALSE);
		return this;
	}

	/**
	 * <h3 class="en-US">Configure type code of entity class and database table</h3>
	 * <h3 class="zh-CN">设置实体类与数据表的类型代码</h3>
	 *
	 * @param ddlType <span class="en-US">Type code of entity class and database table</span>
	 *                <span class="zh-CN">实体类与数据表的类型代码</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public ConfigureBuilder configDDL(final DDLType ddlType) {
		this.configure.setDdlType(ddlType);
		return this;
	}

	/**
	 * <h3 class="en-US">Set default data schema</h3>
	 * <h3 class="zh-CN">设置默认数据源</h3>
	 *
	 * @param schemaName <span class="en-US">Data source name</span>
	 *                   <span class="zh-CN">数据源名称</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public ConfigureBuilder defaultSchema(final String schemaName) {
		this.configure.getSchemaConfigs().replaceAll(schemaConfig -> {
			schemaConfig.setDefaultSchema(schemaConfig.getSchemaName().equalsIgnoreCase(schemaName));
			return schemaConfig;
		});
		return this;
	}

	/**
	 * <h3 class="en-US">Initialize the distributed database configuration information builder instance object</h3>
	 * <h3 class="zh-CN">初始化分布式数据库配置信息构建器实例对象</h3>
	 *
	 * @param schemaName <span class="en-US">Data source name</span>
	 *                   <span class="zh-CN">数据源名称</span>
	 * @return <span class="en-US">Configure information builder instance object</span>
	 * <span class="zh-CN">配置信息构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the type of existing configuration information is inconsistent with the distribute configure information</span>
	 *                          <span class="zh-CN">如果现有配置信息的类型不是分布式数据源配置信息</span>
	 */
	public SchemaConfigBuilder.DistributeConfigBuilder distributeConfigBuilder(final String schemaName)
			throws BuilderException {
		return new SchemaConfigBuilder.DistributeConfigBuilder(this,
				this.readConfig(schemaName, DistributeSchemaConfig.class));
	}

	/**
	 * <h3 class="en-US">Initialize the JDBC database configuration information builder instance object</h3>
	 * <h3 class="zh-CN">初始化JDBC数据库配置信息构建器实例对象</h3>
	 *
	 * @param schemaName <span class="en-US">Data source name</span>
	 *                   <span class="zh-CN">数据源名称</span>
	 * @return <span class="en-US">Configure information builder instance object</span>
	 * <span class="zh-CN">配置信息构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the type of existing configuration information is inconsistent with the JDBC configure information</span>
	 *                          <span class="zh-CN">如果现有配置信息的类型不是JDBC数据源配置信息</span>
	 */
	public SchemaConfigBuilder.JdbcConfigBuilder jdbcConfigBuilder(final String schemaName) throws BuilderException {
		return new SchemaConfigBuilder.JdbcConfigBuilder(this,
				this.readConfig(schemaName, JdbcSchemaConfig.class));
	}

	/**
	 * <h3 class="en-US">Initialize the remote database configuration information builder instance object</h3>
	 * <h3 class="zh-CN">初始化远程数据库配置信息构建器实例对象</h3>
	 *
	 * @param schemaName <span class="en-US">Data source name</span>
	 *                   <span class="zh-CN">数据源名称</span>
	 * @return <span class="en-US">Configure information builder instance object</span>
	 * <span class="zh-CN">配置信息构建器实例对象</span>
	 * @throws BuilderException <span class="en-US">If the type of existing configuration information is inconsistent with the remote configure information</span>
	 *                          <span class="zh-CN">如果现有配置信息的类型不是远程数据源配置信息</span>
	 */
	public SchemaConfigBuilder.RemoteConfigBuilder remoteConfigBuilder(final String schemaName)
			throws BuilderException {
		return new SchemaConfigBuilder.RemoteConfigBuilder(this,
				this.readConfig(schemaName, RemoteSchemaConfig.class));
	}

	/**
	 * <h3 class="en-US">Initialize the data import and export tool configuration information builder instance object</h3>
	 * <h3 class="zh-CN">初始化数据导入导出工具配置信息构建器实例对象</h3>
	 *
	 * @return <span class="en-US">Configure information builder instance object</span>
	 * <span class="zh-CN">配置信息构建器实例对象</span>
	 */
	public StorageConfigBuilder storageConfigBuilder() {
		StorageConfig storageConfig = this.configure.getStorageConfig();
		if (storageConfig == null) {
			storageConfig = new StorageConfig();
		}
		return new StorageConfigBuilder(this, storageConfig);
	}

	/**
	 * <h3 class="en-US">Read configuration information for the given data source name</h3>
	 * <h3 class="zh-CN">读取给定数据源名称的配置信息</h3>
	 *
	 * @param schemaName  <span class="en-US">Data source name</span>
	 *                    <span class="zh-CN">数据源名称</span>
	 * @param configClass <span class="en-US">Schema configure type class</span>
	 *                    <span class="zh-CN">数据源配置信息类型</span>
	 * @param <T>         <span class="en-US">Schema configure generic class</span>
	 *                    <span class="zh-CN">配置信息泛型类</span>
	 * @return <span class="en-US">Configure information instance object</span>
	 * <span class="zh-CN">配置信息实例对象</span>
	 * @throws BuilderException <span class="en-US">If the type of existing configuration information is inconsistent with the specified type</span>
	 *                          <span class="zh-CN">如果现有配置信息的类型与指定类型不一致</span>
	 */
	private <T> T readConfig(final String schemaName, Class<T> configClass) throws BuilderException {
		SchemaConfig schemaConfig =
				this.configure.getSchemaConfigs().stream()
						.filter(existConfig -> ObjectUtils.nullSafeEquals(existConfig.getSchemaName(), schemaName))
						.findFirst()
						.orElse(null);

		if (schemaConfig == null) {
			T config = ObjectUtils.newInstance(configClass);
			((SchemaConfig) config).setSchemaName(schemaName);
			if (RemoteSchemaConfig.class.equals(configClass)) {
				((RemoteSchemaConfig) config).setDialectName(BrainCommons.DEFAULT_REMOTE_DIALECT_NAME);
			}
			return config;
		}
		if (ClassUtils.isAssignable(configClass, schemaConfig.getClass())) {
			return configClass.cast(schemaConfig);
		}
		throw new BuilderException(0x00DB00000037L,
				schemaName, configClass.getName(), schemaConfig.getClass().getName());
	}

	@Override
	public Configure confirm() {
		return this.configure;
	}

	@Override
	public void confirm(@Nonnull final Object object) {
		if (object instanceof SchemaConfig) {
			SchemaConfig schemaConfig = (SchemaConfig) object;
			List<SchemaConfig> schemaList = this.configure.getSchemaConfigs();
			if (schemaList.stream().anyMatch(existConfig ->
					ObjectUtils.nullSafeEquals(existConfig.getSchemaName(), schemaConfig.getSchemaName()))) {
				schemaList.replaceAll(existConfig -> {
					if (ObjectUtils.nullSafeEquals(existConfig.getSchemaName(), schemaConfig.getSchemaName())) {
						return schemaConfig;
					}
					return existConfig;
				});
			} else {
				schemaList.add(schemaConfig);
			}
			this.configure.setSchemaConfigs(schemaList);
		} else if (object instanceof StorageConfig) {
			this.configure.setStorageConfig((StorageConfig) object);
		}
	}
}
