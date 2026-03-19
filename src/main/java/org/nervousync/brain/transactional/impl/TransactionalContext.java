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

package org.nervousync.brain.transactional.impl;

import jakarta.annotation.Nonnull;
import org.nervousync.brain.commons.BrainCommons;
import org.nervousync.brain.configs.transactional.TransactionalConfig;
import org.nervousync.brain.exceptions.transactional.TransactionalException;
import org.nervousync.brain.source.BrainDataSource;
import org.nervousync.utils.core.ClassUtils;
import org.nervousync.utils.core.StringUtils;
import org.nervousync.utils.logger.LoggerUtils;

import java.sql.SQLException;
import java.util.*;

/**
 * <h2 class="en-US">Transactional context information</h2>
 * <h2 class="zh-CN">事务上下文信息</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Nov 12, 2020 12:20:49 $
 */
public final class TransactionalContext {

	/**
	 * <span class="en-US">Logger instance</span>
	 * <span class="zh-CN">日志实例</span>
	 */
	private final LoggerUtils.Logger logger = LoggerUtils.getLogger(this.getClass());

	/**
	 * <span class="en-US">Transactional configure information</span>
	 * <span class="zh-CN">事务配置信息</span>
	 */
	private final TransactionalConfig transactionalConfig;
	/**
	 * <span class="en-US">Bind connections mapping</span>
	 * <span class="zh-CN">绑定的连接映射表</span>
	 */
	private final Map<String, Object> connections;

	/**
	 * <h3 class="en-US">Constructor method for the transactional context information</h3>
	 * <h3 class="zh-CN">事务上下文信息的构造方法</h3>
	 *
	 * @param transactionalConfig <span class="en-US">Transactional configure information</span>
	 *                            <span class="zh-CN">事务配置信息</span>
	 */
	public TransactionalContext(final TransactionalConfig transactionalConfig) {
		this.transactionalConfig = transactionalConfig;
		this.connections = new HashMap<>();
	}

	/**
	 * <h3 class="en-US">Bind transactional object</h3>
	 * <h3 class="zh-CN">绑定事务对象</h3>
	 *
	 * @param identifyKey <span class="en-US">Connection identify key</span>
	 *                    <span class="zh-CN">连接识别码</span>
	 * @param object      <span class="en-US">Transactional object</span>
	 *                    <span class="zh-CN">事务对象</span>
	 * @throws TransactionalException <span class="en-US">Parameters value invalid</span>
	 *                                <span class="zh-CN">参数信息非法</span>
	 */
	public void bind(@Nonnull final String identifyKey, @Nonnull final Object object) throws TransactionalException {
		if (StringUtils.isEmpty(identifyKey)) {
			throw new TransactionalException(0x00DB00000051L);
		}
		if (this.connections.containsKey(identifyKey)) {
			this.logger.error("Bind_IdentifyKey_Exists");
			return;
		}
		this.connections.put(identifyKey, object);
	}

	/**
	 * <h3 class="en-US">Unbind the transactional object</h3>
	 * <h3 class="zh-CN">解除绑定事务对象</h3>
	 *
	 * @param identifyKey <span class="en-US">Connection identify key</span>
	 *                    <span class="zh-CN">连接识别码</span>
	 */
	public void unbind(final String identifyKey) {
		this.connections.remove(identifyKey);
	}

	/**
	 * <h3 class="en-US">Unbind the transactional object</h3>
	 * <h3 class="zh-CN">解除绑定事务对象</h3>
	 *
	 * @param prefix <span class="en-US">Prefix string of identify key</span>
	 *               <span class="zh-CN">识别码前缀</span>
	 */
	public void unbindAll(final String prefix) {
		this.connections.entrySet().removeIf(entry -> entry.getKey().equalsIgnoreCase(prefix) || entry.getKey().startsWith(prefix));
	}

	/**
	 * <h3 class="en-US">Get connection instance object</h3>
	 * <h3 class="zh-CN">获取连接实例对象</h3>
	 *
	 * @param identifyKey <span class="en-US">Connection identify key</span>
	 *                    <span class="zh-CN">连接识别码</span>
	 * @return <span class="en-US">Connection instance object</span>
	 * <span class="zh-CN">连接实例对象</span>
	 */
	public <T> T get(@Nonnull final String identifyKey, @Nonnull final Class<T> targetClass) {
		return Optional.ofNullable(this.connections.get(identifyKey))
				.filter(object -> ClassUtils.isAssignable(targetClass, object.getClass()))
				.map(targetClass::cast)
				.orElse(null);
	}

	/**
	 * <h3 class="en-US">Get connection instance object</h3>
	 * <h3 class="zh-CN">获取连接实例对象</h3>
	 *
	 * @param prefix <span class="en-US">Prefix string of identify key</span>
	 *               <span class="zh-CN">识别码前缀</span>
	 * @return <span class="en-US">Connection instance object</span>
	 * <span class="zh-CN">连接实例对象</span>
	 */
	public <T> List<T> getAll(@Nonnull final String prefix, @Nonnull final Class<T> targetClass) {
		List<T> objects = new ArrayList<>();
		this.connections.entrySet().stream()
				.filter(entry ->
						(entry.getKey().equalsIgnoreCase(prefix) || entry.getKey().startsWith(prefix))
								&& ClassUtils.isAssignable(targetClass, entry.getValue().getClass()))
				.forEach(entry -> objects.add(targetClass.cast(entry.getValue())));
		return objects;
	}

	/**
	 * <h3 class="en-US">Rollback transactional</h3>
	 * <h3 class="zh-CN">回滚事务</h3>
	 *
	 * @throws Exception <span class="en-US">If an error occurs during execution</span>
	 *                   <span class="zh-CN">如果执行过程中出错</span>
	 */
	public void rollback() throws Exception {
		BrainDataSource dataSource = BrainDataSource.getInstance();
		if (dataSource == null) {
			return;
		}
		for (String identifyKey : new ArrayList<>(this.connections.keySet())) {
			String schemaName = Optional.of(identifyKey.indexOf(BrainCommons.DEFAULT_NAME_SPLIT))
					.filter(index -> index > 0)
					.map(index -> identifyKey.substring(0, index))
					.orElse(identifyKey);
			dataSource.rollback(schemaName);
		}
	}

	/**
	 * <h3 class="en-US">Submit transactional execute</h3>
	 * <h3 class="zh-CN">提交事务执行</h3>
	 *
	 * @throws Exception <span class="en-US">If an error occurs during execution</span>
	 *                   <span class="zh-CN">如果执行过程中出错</span>
	 */
	public void commit() throws Exception {
		BrainDataSource dataSource = BrainDataSource.getInstance();
		if (dataSource == null) {
			return;
		}
		for (String identifyKey : new ArrayList<>(this.connections.keySet())) {
			String schemaName = Optional.of(identifyKey.indexOf(BrainCommons.DEFAULT_NAME_SPLIT))
					.filter(index -> index > 0)
					.map(index -> identifyKey.substring(0, index))
					.orElse(identifyKey);
			dataSource.commit(schemaName);
		}
	}

	/**
	 * <h3 class="en-US">Close all bind connection instance object</h3>
	 * <h3 class="zh-CN">关闭所有绑定的连接实例对象</h3>
	 *
	 * @throws SQLException <span class="en-US">If an error occurs during execution</span>
	 *                      <span class="zh-CN">如果执行过程中出错</span>
	 */
	public void end() throws Exception {
		BrainDataSource dataSource = BrainDataSource.getInstance();
		if (dataSource == null) {
			return;
		}
		for (String identifyKey : new ArrayList<>(this.connections.keySet())) {
			String schemaName = Optional.of(identifyKey.indexOf(BrainCommons.DEFAULT_NAME_SPLIT))
					.filter(index -> index > 0)
					.map(index -> identifyKey.substring(0, index))
					.orElse(identifyKey);
			dataSource.endTransactional(schemaName);
		}
	}

	/**
	 * <h3 class="en-US">Getter method for the transactional configuring information</h3>
	 * <h3 class="zh-CN">事务配置信息的 Getter 方法</h3>
	 *
	 * @return <span class="en-US">Transactional configure information</span>
	 * <span class="zh-CN">事务配置信息</span>
	 */
	public TransactionalConfig getTransactionalConfig() {
		return this.transactionalConfig;
	}
}
