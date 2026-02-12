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
import org.nervousync.brain.configs.BrainConfigure;
import org.nervousync.brain.configs.schema.SchemaConfig;
import org.nervousync.brain.configs.schema.impl.DistributeSchemaConfig;
import org.nervousync.brain.configs.schema.impl.JdbcSchemaConfig;
import org.nervousync.brain.configs.schema.impl.RemoteSchemaConfig;
import org.nervousync.brain.enumerations.ddl.DDLType;
import org.nervousync.builder.AbstractBuilder;
import org.nervousync.builder.ParentBuilder;
import org.nervousync.exceptions.builder.BuilderException;
import org.nervousync.utils.core.ClassUtils;
import org.nervousync.utils.core.DateTimeUtils;
import org.nervousync.utils.core.ObjectUtils;

import java.util.List;

/**
 * <h2 class="en-US">Implementation class of configuring information builder</h2>
 * <h2 class="zh-CN">配置信息构建器的实现类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Apr 10, 2018 15:48:19 $
 */
@SuppressWarnings("unused")
public final class BrainConfigureBuilder<P extends ParentBuilder> extends AbstractBuilder<P, BrainConfigure> {

	/**
	 * <span class="en-US">Data source configuration information instance object</span>
	 * <span class="zh-CN">数据源配置信息实例对象</span>
	 */
	private final BrainConfigure configure;
	/**
	 * <span class="en-US">Configure information modified flag</span>
	 * <span class="zh-CN">配置信息修改标记</span>
	 */
	private boolean modified = Boolean.FALSE;

	/**
	 * <h3 class="en-US">Private constructor method for configure information builder implementation class</h3>
	 * <h3 class="zh-CN">配置信息构建器实现类的构造函数</h3>
	 *
	 * @param configure <span class="en-US">Data source configuration information instance object</span>
	 *                  <span class="zh-CN">数据源配置信息实例对象</span>
	 */
	private BrainConfigureBuilder(final P parentBuilder, final BrainConfigure configure) {
		super(parentBuilder);
		this.configure = (configure == null) ? new BrainConfigure() : configure;
	}

	/**
	 * <h3 class="en-US">Static method is used to initialize the current class of configuration information builder</h3>
	 * <h3 class="zh-CN">静态方法用于初始化配置信息构建器实现类</h3>
	 *
	 * @param <P>       <span class="en-US">Generics Type instance</span>
	 *                  <span class="zh-CN">泛型类实例对象</span>
	 * @param configure <span class="en-US">Data source configuration information instance object</span>
	 *                  <span class="zh-CN">数据源配置信息实例对象</span>
	 * @return <span class="en-US">Configuration information instance object</span>
	 * <span class="zh-CN">配置信息构建器实现类实例对象</span>
	 */
	public static <P extends ParentBuilder> BrainConfigureBuilder<P> newBuilder(final BrainConfigure configure) {
		return newBuilder(null, configure);
	}

	/**
	 * <h3 class="en-US">Static method is used to initialize the current class of configuration information builder</h3>
	 * <h3 class="zh-CN">静态方法用于初始化配置信息构建器实现类</h3>
	 *
	 * @param <P>           <span class="en-US">Generics Type instance</span>
	 *                      <span class="zh-CN">泛型类实例对象</span>
	 * @param parentBuilder <span class="en-US">Generics Type instance</span>
	 *                      <span class="zh-CN">泛型类实例对象</span>
	 * @param configure     <span class="en-US">Data source configuration information instance object</span>
	 *                      <span class="zh-CN">数据源配置信息实例对象</span>
	 * @return <span class="en-US">Configuration information instance object</span>
	 * <span class="zh-CN">配置信息构建器实现类实例对象</span>
	 */
	public static <P extends ParentBuilder> BrainConfigureBuilder<P> newBuilder(final P parentBuilder,
	                                                                            final BrainConfigure configure) {
		return new BrainConfigureBuilder<>(parentBuilder, configure);
	}

	/**
	 * <h3 class="en-US">Setup data source lazy load</h3>
	 * <h3 class="zh-CN">设置数据源懒加载</h3>
	 *
	 * @param lazyInit <span class="en-US">Lazy load flag</span>
	 *                 <span class="zh-CN">懒加载标记</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainConfigureBuilder<P> lazyInit(final boolean lazyInit) {
		return lazyInit ? this.enableLazyInit() : this.disableLazyInit();
	}

	/**
	 * <h3 class="en-US">Enable data source lazy load</h3>
	 * <h3 class="zh-CN">开启数据源懒加载</h3>
	 *
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainConfigureBuilder<P> enableLazyInit() {
		if (!this.configure.isLazyInitialize()) {
			this.configure.setLazyInitialize(Boolean.TRUE);
			this.modified = Boolean.TRUE;
		}
		return this;
	}

	/**
	 * <h3 class="en-US">Disable data source lazy load</h3>
	 * <h3 class="zh-CN">关闭数据源懒加载</h3>
	 *
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainConfigureBuilder<P> disableLazyInit() {
		if (this.configure.isLazyInitialize()) {
			this.configure.setLazyInitialize(Boolean.FALSE);
			this.modified = Boolean.TRUE;
		}
		return this;
	}

	/**
	 * <h3 class="en-US">Setup data source JMX monitor status</h3>
	 * <h3 class="zh-CN">设置数据源JMX监控状态</h3>
	 *
	 * @param monitor <span class="en-US">Enable monitor flag</span>
	 *                 <span class="zh-CN">监控启用标记</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainConfigureBuilder<P> jmxMonitor(final boolean monitor) {
		return monitor ? this.enableJmxMonitor() : this.disableJmxMonitor();
	}

	/**
	 * <h3 class="en-US">Enable data source JMX monitor</h3>
	 * <h3 class="zh-CN">开启数据源JMX监控</h3>
	 *
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainConfigureBuilder<P> enableJmxMonitor() {
		if (!this.configure.isJmxMonitor()) {
			this.configure.setJmxMonitor(Boolean.TRUE);
			this.modified = Boolean.TRUE;
		}
		return this;
	}

	/**
	 * <h3 class="en-US">Disable data source JMX monitor</h3>
	 * <h3 class="zh-CN">关闭数据源JMX监控</h3>
	 *
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public BrainConfigureBuilder<P> disableJmxMonitor() {
		if (this.configure.isJmxMonitor()) {
			this.configure.setJmxMonitor(Boolean.FALSE);
			this.modified = Boolean.TRUE;
		}
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
	public BrainConfigureBuilder<P> ddlMode(final DDLType ddlType) {
		if (!ObjectUtils.nullSafeEquals(this.configure.getDdlType(), ddlType)) {
			this.configure.setDdlType(ddlType);
			this.modified = Boolean.TRUE;
		}
		return this;
	}

	/**
	 * <h3 class="en-US">Configure query optimizer information</h3>
	 * <h3 class="zh-CN">设置查询优化器相关信息</h3>
	 *
	 * @param optimizerName <span class="en-US">Used identification code of query optimizer implementation class</span>
	 *                      <span class="zh-CN">使用的查询优化器实现类识别代码</span>
	 * @param poolSize      <span class="en-US">Query optimizer pool size</span>
	 *                      <span class="zh-CN">查询优化器对象池大小</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实现类实例对象</span>
	 */
	public BrainConfigureBuilder<P> queryOptimizer(final String optimizerName, final Integer poolSize) {
		if (!ObjectUtils.nullSafeEquals(this.configure.getOptimizerName(), optimizerName)
				|| !ObjectUtils.nullSafeEquals(this.configure.getPoolSize(), poolSize)) {
			this.configure.setOptimizerName(optimizerName);
			this.configure.setPoolSize(poolSize);
			this.modified = Boolean.TRUE;
		}
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
	public BrainConfigureBuilder<P> defaultSchema(@Nonnull final String schemaName) {
		if (this.configure.getSchemaConfigs().stream()
				.noneMatch(schemaConfig -> schemaName.equalsIgnoreCase(schemaConfig.getSchemaName()))) {
			return this;
		}
		this.configure.getSchemaConfigs().replaceAll(schemaConfig -> {
			boolean defaultSchema = schemaConfig.getSchemaName().equalsIgnoreCase(schemaName);
			if (!ObjectUtils.nullSafeEquals(defaultSchema, schemaConfig.isDefaultSchema())) {
				this.modified = Boolean.TRUE;
			}
			schemaConfig.setDefaultSchema(defaultSchema);
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
	 * @throws BuilderException <span class="en-US">If the type of existing configuration information is inconsistent with the distribute database configure information</span>
	 *                          <span class="zh-CN">如果现有配置信息的类型不是分布式数据源配置信息</span>
	 */
	public SchemaConfigBuilder.DistributeConfigBuilder<BrainConfigureBuilder<P>> distributeConfig(final String schemaName)
			throws BuilderException {
		return new SchemaConfigBuilder.DistributeConfigBuilder<>(this,
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
	public SchemaConfigBuilder.JdbcConfigBuilder<BrainConfigureBuilder<P>> jdbcConfig(final String schemaName)
			throws BuilderException {
		return new SchemaConfigBuilder.JdbcConfigBuilder<>(this,
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
	 * @throws BuilderException <span class="en-US">If the type of existing configuration information is inconsistent with the remote configuring information</span>
	 *                          <span class="zh-CN">如果现有配置信息的类型不是远程数据源配置信息</span>
	 */
	public SchemaConfigBuilder.RemoteConfigBuilder<BrainConfigureBuilder<P>> remoteConfig(final String schemaName)
			throws BuilderException {
		return new SchemaConfigBuilder.RemoteConfigBuilder<>(this,
				this.readConfig(schemaName, RemoteSchemaConfig.class));
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
	public BrainConfigure build() {
		if (this.modified) {
			this.configure.setLastModified(DateTimeUtils.currentUTCTimeMillis());
		}
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
						if (existConfig.getLastModified() != schemaConfig.getLastModified()) {
							this.modified = Boolean.TRUE;
						}
						return schemaConfig;
					}
					return existConfig;
				});
			} else {
				schemaList.add(schemaConfig);
				this.modified = Boolean.TRUE;
			}
			this.configure.setSchemaConfigs(schemaList);
		}
	}
}
