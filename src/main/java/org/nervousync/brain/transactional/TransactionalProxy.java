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

import org.nervousync.brain.exceptions.transactional.TransactionalException;
import org.nervousync.brain.transactional.impl.ThreadLocalTransactionalManagerImpl;
import org.nervousync.utils.core.ClassUtils;
import org.nervousync.utils.core.ObjectUtils;

/**
 * <h2 class="en-US">Transactional manager proxy</h2>
 * <h2 class="zh-CN">事务管理器代理</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Nov 12, 2020 12:20:49 $
 */
public final class TransactionalProxy {

	/**
	 * <span class="en-US">Transactional manager instance object</span>
	 * <span class="zh-CN">事务管理器实例对象</span>
	 */
	private static volatile TransactionalManager TRANSACTIONAL_MANAGER = new ThreadLocalTransactionalManagerImpl();

	/**
	 * <h3 class="en-US">Get the transactional manager instance object</h3>
	 * <h3 class="zh-CN">获取事务管理器实例对象</h3>
	 *
	 * @return <span class="en-US">Transactional manager instance object</span>
	 * <span class="zh-CN">事务管理器实例对象</span>
	 */
	public static TransactionalManager getTransactionalManager() {
		return TRANSACTIONAL_MANAGER;
	}

	/**
	 * <h3 class="en-US">Check the current thread was read-only</h3>
	 * <h3 class="zh-CN">检查当前线程是否为只读</h3>
	 *
	 * @return <span class="en-US">Check result</span>
	 * <span class="zh-CN">检查结果</span>
	 */
	public static boolean isReadOnly() {
		return TRANSACTIONAL_MANAGER.readOnly();
	}

	/**
	 * <h3 class="en-US">Initialize the transactional manager instance object</h3>
	 * <h3 class="zh-CN">初始化事务管理器实例对象</h3>
	 *
	 * @param implementClass <span class="en-US">Transactional manager implement class</span>
	 *                       <span class="zh-CN">事务管理器实现类</span>
	 * @throws TransactionalException <span class="en-US">Transactional manager implement class not implement the interface</span>
	 *                                <span class="zh-CN">事务管理器实现类没有实现接口</span>
	 */
	public static synchronized void initialize(final Class<?> implementClass) throws TransactionalException {
		if (ClassUtils.isAssignable(TransactionalManager.class, implementClass)) {
			TRANSACTIONAL_MANAGER = (TransactionalManager) ObjectUtils.newInstance(implementClass);
			return;
		}
		throw new TransactionalException(0x0L);
	}
}
