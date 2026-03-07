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
import org.nervousync.brain.configs.transactional.TransactionalConfig;
import org.nervousync.brain.exceptions.transactional.TransactionalException;
import org.nervousync.brain.transactional.TransactionalManager;

import java.util.Optional;
import java.util.Stack;

/**
 * <h2 class="en-US">Transactional manager implement class by ThreadLocal</h2>
 * <h2 class="zh-CN">使用 ThreadLocal 实现的事务管理器实现类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Nov 12, 2020 12:20:49 $
 */
public final class ThreadLocalTransactionalManagerImpl implements TransactionalManager {

	/**
	 * <span class="en-US">Hang-up transactional context stack</span>
	 * <span class="zh-CN">挂起的事务上下文</span>
	 */
	private static final ThreadLocal<Stack<TransactionalContext>> HANG_UP = new ThreadLocal<>();
	/**
	 * <span class="en-US">Current transactional context</span>
	 * <span class="zh-CN">当前事务上下文</span>
	 */
	private static final ThreadLocal<TransactionalContext> CURRENT = new ThreadLocal<>();
	/**
	 * <span class="en-US">Current thread read-only flag</span>
	 * <span class="zh-CN">当前线程只读标记</span>
	 */
	private static final ThreadLocal<Boolean> READ_ONLY = new ThreadLocal<>();

	@Override
	public boolean begin(@Nonnull final TransactionalConfig transactionalConfig) throws TransactionalException {
		boolean create = true;
		switch (transactionalConfig.getPropagation()) {
			case REQUIRED:
				create = CURRENT.get() == null;
				break;
			case SUPPORTS:
				create = false;
				break;
			case MANDATORY:
				if (CURRENT.get() == null) {
					throw new TransactionalException(0x00DB00000049L);
				}
				create = false;
				break;
			case REQUIRES_NEW:
				hangUp();
				break;
			case NOT_SUPPORTED:
				hangUp();
				create = false;
				break;
			case NEVER:
				if (CURRENT.get() != null) {
					throw new TransactionalException(0x00DB00000049L);
				}
				create = false;
				break;
		}

		if (create) {
			CURRENT.set(new TransactionalContext(transactionalConfig));
		}
		READ_ONLY.set(transactionalConfig.isReadOnly());
		return create;
	}

	@Override
	public boolean readOnly() {
		if (READ_ONLY.get() == null) {
			return Boolean.FALSE;
		}
		return READ_ONLY.get();
	}

	@Override
	public void readOnly(final boolean readOnly) {
		READ_ONLY.set(readOnly);
	}

	@Override
	public void reset() {
		READ_ONLY.remove();
	}

	@Override
	public void rollback(final Exception e) throws Exception {
		TransactionalContext txContext = CURRENT.get();
		if (txContext != null && txContext.getTransactionalConfig().rollback(e)) {
			txContext.rollback();
		}
	}

	@Override
	public void commit(final boolean createTransactional) throws Exception {
		if (createTransactional) {
			TransactionalContext txContext = CURRENT.get();
			if (txContext != null) {
				txContext.commit();
			}
		}
	}

	@Override
	public void end(final boolean createTransactional) throws Exception {
		if (createTransactional) {
			TransactionalContext txContext = CURRENT.get();
			if (txContext != null) {
				txContext.end();
			}
			//  Resume the hang-up transactional
			resume();
		}
	}

	@Override
	public void clear() {
		CURRENT.remove();
		HANG_UP.remove();
	}

	@Override
	public TransactionalContext get() {
		return CURRENT.get();
	}

	@Override
	public boolean inTransactional() {
		return CURRENT.get() != null;
	}

	/**
	 * <h3 class="en-US">Hang-up current transactional</h3>
	 * <h3 class="zh-CN">挂起当前事务</h3>
	 */
	private void hangUp() {
		if (CURRENT.get() != null) {
			Stack<TransactionalContext> hangUp = HANG_UP.get();
			if (hangUp == null) {
				hangUp = new Stack<>();
			}
			hangUp.push(CURRENT.get());
			HANG_UP.set(hangUp);
			CURRENT.remove();
		}
	}

	/**
	 * <h3 class="en-US">Resume the hang-up transactional</h3>
	 * <h3 class="zh-CN">继续挂起的事务</h3>
	 */
	private void resume() {
		Optional.ofNullable(HANG_UP.get()).map(Stack::pop).ifPresent(CURRENT::set);
	}
}
