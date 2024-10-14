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

import org.nervousync.brain.configs.storage.StorageConfig;
import org.nervousync.builder.AbstractBuilder;

/**
 * <h2 class="en-US">Implementation class of data import and export tool configuration information builder</h2>
 * <h2 class="zh-CN">数据导入导出工具配置信息构建器的实现类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision : 1.0.0 $ $Date: Apr 10, 2018 15:48:19 $
 */
public final class StorageConfigBuilder extends AbstractBuilder<StorageConfig> {

	/**
	 * <span class="en-US">Configure information instance object</span>
	 * <span class="zh-CN">配置信息实例对象</span>
	 */
	private final StorageConfig storageConfig;

	/**
	 * <h3 class="en-US">Constructor method for implementation class of data import and export tool configuration information builder</h3>
	 * <h3 class="zh-CN">数据导入导出工具配置信息构建器的实现类的构造函数</h3>
	 *
	 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
	 *                      <span class="zh-CN">父构建器实例对象</span>
	 * @param storageConfig <span class="en-US">Configure information instance object</span>
	 *                      <span class="zh-CN">配置信息实例对象</span>
	 */
	StorageConfigBuilder(final ConfigureBuilder parentBuilder, final StorageConfig storageConfig) {
		super(parentBuilder);
		this.storageConfig = storageConfig;
	}

	/**
	 * <h3 class="en-US">Set the base path of the data import and export tool</h3>
	 * <h3 class="zh-CN">设置数据导入导出工具的基础路径</h3>
	 *
	 * @param basePath <span class="en-US">The base path for system execution</span>
	 *                 <span class="zh-CN">系统执行的基础路径</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public StorageConfigBuilder basePath(final String basePath) {
		this.storageConfig.setBasePath(basePath);
		return this;
	}

	/**
	 * <h3 class="en-US">Set the task store provider name to use</h3>
	 * <h3 class="zh-CN">设置使用的任务存储适配器名称</h3>
	 *
	 * @param providerName <span class="en-US">Task store provider name to use</span>
	 *                     <span class="zh-CN">使用的任务存储适配器名称</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public StorageConfigBuilder provider(final String providerName) {
		this.storageConfig.setStorageProvider(providerName);
		return this;
	}

	/**
	 * <h3 class="en-US">Set the number of task concurrency and task cleanup time</h3>
	 * <h3 class="zh-CN">设置任务并发数和任务清理时间</h3>
	 *
	 * @param threadLimit <span class="en-US">Number of tasks allowed to be executed simultaneously</span>
	 *                    <span class="zh-CN">允许同时执行的任务数</span>
	 * @param expireTime  <span class="en-US">Expiration time for automatic deletion of completed tasks</span>
	 *                    <span class="zh-CN">已完成任务自动删除的过期时间</span>
	 * @return <span class="en-US">Current builder instance object</span>
	 * <span class="zh-CN">当前构建器实例对象</span>
	 */
	public StorageConfigBuilder config(final int threadLimit, final long expireTime) {
		this.storageConfig.setThreadLimit(threadLimit);
		this.storageConfig.setExpireTime(expireTime);
		return this;
	}

	@Override
	public StorageConfig confirm() {
		return this.storageConfig;
	}
}
