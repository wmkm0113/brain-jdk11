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

package org.nervousync.brain.transactional;

import jakarta.annotation.Nonnull;
import org.nervousync.brain.configs.transactional.TransactionalConfig;
import org.nervousync.brain.exceptions.transactional.TransactionalException;
import org.nervousync.brain.transactional.impl.TransactionalContext;

/**
 * <h2 class="en-US">Transactional manager interface class</h2>
 * <h2 class="zh-CN">事务管理器接口</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Nov 12, 2020 12:20:49 $
 */
@SuppressWarnings("unused")
public interface TransactionalManager {

	/**
	 * <h3 class="en-US">Begin transactional</h3>
	 * <h3 class="zh-CN">开始事务</h3>
	 *
	 * @param transactionalConfig <span class="en-US">Transactional configure information</span>
	 *                            <span class="zh-CN">事务配置信息</span>
	 * @return <span class="en-US">New transactional flag</span>
	 * <span class="zh-CN">新事务标记</span>
	 * @throws TransactionalException <span class="en-US">If an error occurs when processing</span>
	 *                                <span class="zh-CN">执行过程中出错</span>
	 */
	boolean begin(@Nonnull final TransactionalConfig transactionalConfig) throws TransactionalException;

	/**
	 * <h3 class="en-US">Rollback transactional</h3>
	 * <h3 class="zh-CN">回滚事务</h3>
	 *
	 * @param e <span class="en-US">Exception instance object</span>
	 *          <span class="zh-CN">异常信息</span>
	 * @throws Exception <span class="en-US">If an error occurs when processing</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	void rollback(final Exception e) throws Exception;

	/**
	 * <h3 class="en-US">Commit transactional</h3>
	 * <h3 class="zh-CN">提交事务</h3>
	 *
	 * @param newTransactional <span class="en-US">New transactional flag</span>
	 *                         <span class="zh-CN">新事务标记</span>
	 * @throws Exception <span class="en-US">If an error occurs when processing</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	void commit(final boolean newTransactional) throws Exception;

	/**
	 * <h3 class="en-US">End transactional</h3>
	 * <h3 class="zh-CN">结束事务</h3>
	 *
	 * @param newTransactional <span class="en-US">New transactional flag</span>
	 *                         <span class="zh-CN">新事务标记</span>
	 * @throws Exception <span class="en-US">If an error occurs when processing</span>
	 *                   <span class="zh-CN">执行过程中出错</span>
	 */
	void end(final boolean newTransactional) throws Exception;

	/**
	 * <h3 class="en-US">Get the context of the current transactional</h3>
	 * <h3 class="zh-CN">获取当前事务上下文</h3>
	 *
	 * @return <span class="en-US">Transactional context instance object</span>
	 * <span class="zh-CN">事务上下文实例对象</span>
	 */
	TransactionalContext get();

	/**
	 * <h3 class="en-US">Check the current thread was in transactional</h3>
	 * <h3 class="zh-CN">检查当前线程在事务中</h3>
	 *
	 * @return <span class="en-US">Check result</span>
	 * <span class="zh-CN">检查结果</span>
	 */
	boolean inTransactional();

	/**
	 * <h3 class="en-US">Check the current thread was read-only</h3>
	 * <h3 class="zh-CN">检查当前线程是否为只读</h3>
	 *
	 * @return <span class="en-US">Check result</span>
	 * <span class="zh-CN">检查结果</span>
	 */
	boolean readOnly();

	/**
	 * <h3 class="en-US">Setting the current thread read-only flag</h3>
	 * <h3 class="zh-CN">设置当前线程的只读状态</h3>
	 *
	 * @param readOnly <span class="en-US">Read-only flag</span>
	 *                 <span class="zh-CN">只读标记</span>
	 */
	void readOnly(final boolean readOnly);

	/**
	 * <h3 class="en-US">Reset the current thread read-only flag</h3>
	 * <h3 class="zh-CN">重置当前线程的只读状态</h3>
	 */
	void reset();
}
