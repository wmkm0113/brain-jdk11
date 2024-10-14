/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */

package org.nervousync.brain.configs.storage;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.brain.commons.BrainCommons;
import org.nervousync.commons.Globals;

/**
 * <h2 class="en-US">Configure information of data import/export utilities</h2>
 * <h2 class="zh-CN">数据导入导出工具配置信息</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Mar 26, 2020 14:49:15 $
 */
@XmlType(name = "storage_config", namespace = "https://nervousync.org/schemas/database")
@XmlRootElement(name = "storage_config", namespace = "https://nervousync.org/schemas/database")
public final class StorageConfig extends BeanObject {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = -6185059618701956238L;

	/**
	 * <span class="en-US">The base path for system execution</span>
	 * <span class="zh-CN">系统执行的基础路径</span>
	 */
	@XmlElement(name = "base_path")
	private String basePath = Globals.DEFAULT_VALUE_STRING;
	/**
	 * <span class="en-US">Task store provider name to use</span>
	 * <span class="zh-CN">使用的任务存储适配器名称</span>
	 */
	@XmlElement(name = "storage_provider")
	private String storageProvider = Globals.DEFAULT_VALUE_STRING;
	/**
	 * <span class="en-US">Number of tasks allowed to be executed simultaneously</span>
	 * <span class="zh-CN">允许同时执行的任务数</span>
	 */
	@XmlElement(name = "thread_limit")
	private int threadLimit = BrainCommons.DEFAULT_PROCESS_THREAD_LIMIT;
	/**
	 * <span class="en-US">Expiration time for automatic deletion of completed tasks</span>
	 * <span class="zh-CN">已完成任务自动删除的过期时间</span>
	 */
	@XmlElement(name = "expire_time")
	private long expireTime = BrainCommons.DEFAULT_STORAGE_EXPIRE_TIME;

	/**
	 * <h3 class="en-US">Constructor method for configure information of data import/export utilities</h3>
	 * <h3 class="zh-CN">数据导入导出工具配置信息的构造方法</h3>
	 */
	public StorageConfig() {
	}

	/**
	 * <h3 class="en-US">Getter method for the base path for system execution</h3>
	 * <h3 class="zh-CN">系统执行的基础路径的Getter方法</h3>
	 *
	 * @return <span class="en-US">The base path for system execution</span>
	 * <span class="zh-CN">系统执行的基础路径</span>
	 */
	public String getBasePath() {
		return this.basePath;
	}

	/**
	 * <h3 class="en-US">Setter method for the base path for system execution</h3>
	 * <h3 class="zh-CN">系统执行的基础路径的Setter方法</h3>
	 *
	 * @param basePath <span class="en-US">The base path for system execution</span>
	 *                 <span class="zh-CN">系统执行的基础路径</span>
	 */
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	/**
	 * <h3 class="en-US">Getter method for task store provider name to use</h3>
	 * <h3 class="zh-CN">使用的任务存储适配器名称的Getter方法</h3>
	 *
	 * @return <span class="en-US">Task store provider name to use</span>
	 * <span class="zh-CN">使用的任务存储适配器名称</span>
	 */
	public String getStorageProvider() {
		return this.storageProvider;
	}

	/**
	 * <h3 class="en-US">Setter method for task store provider name to use</h3>
	 * <h3 class="zh-CN">使用的任务存储适配器名称的Setter方法</h3>
	 *
	 * @param storageProvider <span class="en-US">Task store provider name to use</span>
	 *                        <span class="zh-CN">使用的任务存储适配器名称</span>
	 */
	public void setStorageProvider(String storageProvider) {
		this.storageProvider = storageProvider;
	}

	/**
	 * <h3 class="en-US">Getter method for number of tasks allowed to be executed simultaneously</h3>
	 * <h3 class="zh-CN">允许同时执行的任务数的Getter方法</h3>
	 *
	 * @return <span class="en-US">Number of tasks allowed to be executed simultaneously</span>
	 * <span class="zh-CN">允许同时执行的任务数</span>
	 */
	public int getThreadLimit() {
		return this.threadLimit;
	}

	/**
	 * <h3 class="en-US">Setter method for number of tasks allowed to be executed simultaneously</h3>
	 * <h3 class="zh-CN">允许同时执行的任务数的Setter方法</h3>
	 *
	 * @param threadLimit <span class="en-US">Number of tasks allowed to be executed simultaneously</span>
	 *                    <span class="zh-CN">允许同时执行的任务数</span>
	 */
	public void setThreadLimit(final int threadLimit) {
		this.threadLimit = threadLimit;
	}

	/**
	 * <h3 class="en-US">Getter method for expiration time for automatic deletion of completed tasks</h3>
	 * <h3 class="zh-CN">已完成任务自动删除的过期时间的Getter方法</h3>
	 *
	 * @return <span class="en-US">Expiration time for automatic deletion of completed tasks</span>
	 * <span class="zh-CN">已完成任务自动删除的过期时间</span>
	 */
	public long getExpireTime() {
		return this.expireTime;
	}

	/**
	 * <h3 class="en-US">Setter method for expiration time for automatic deletion of completed tasks</h3>
	 * <h3 class="zh-CN">已完成任务自动删除的过期时间的Setter方法</h3>
	 *
	 * @param expireTime <span class="en-US">Expiration time for automatic deletion of completed tasks</span>
	 *                   <span class="zh-CN">已完成任务自动删除的过期时间</span>
	 */
	public void setExpireTime(final long expireTime) {
		this.expireTime = expireTime;
	}
}
