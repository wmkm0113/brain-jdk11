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
package org.nervousync.brain.configs.transactional;

import jakarta.annotation.Nonnull;
import org.intellij.lang.annotations.MagicConstant;
import org.nervousync.utils.ClassUtils;
import org.nervousync.utils.IDUtils;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * <h2 class="en-US">Transactional configure information</h2>
 * <h2 class="zh-CN">事务配置信息</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Mar 30, 2016 16:07:44 $
 */
public final class TransactionalConfig implements Serializable {
    /**
     * <span class="en-US">Serial version UID</span>
     * <span class="zh-CN">序列化UID</span>
     */
    private static final long serialVersionUID = 5195470765056859725L;
    /**
     * <span class="en-US">Transactional identify code</span>
     * <span class="zh-CN">事务识别代码</span>
     */
    private final long transactionalCode;
    /**
     * <span class="en-US">The timeout value of transactional</span>
     * <span class="zh-CN">事务的超时时间</span>
     */
    private final int timeout;
    /**
     * <span class="en-US">The isolation value of transactional</span>
     * <span class="zh-CN">事务的等级代码</span>
     */
    @MagicConstant(valuesFromClass = Connection.class)
    private final int isolation;
    /**
     * <span class="en-US">The rollback exception class of transactional</span>
     * <span class="zh-CN">事务的回滚异常</span>
     */
    private final Stream<Class<?>> rollBackForClasses;

    /**
     * <h3 class="en-US">Private constructor method for transactional configure information</h3>
     * <h3 class="zh-CN">事务配置信息的私有构造方法</h3>
     *
     * @param timeout            <span class="en-US">The timeout value of transactional</span>
     *                           <span class="zh-CN">事务的超时时间</span>
     * @param isolation          <span class="en-US">The isolation value of transactional</span>
     *                           <span class="zh-CN">事务的等级代码</span>
     * @param rollBackForClasses <span class="en-US">The rollback exception class of transactional</span>
     *                           <span class="zh-CN">事务的回滚异常</span>
     */
    private TransactionalConfig(final int timeout,
                                @MagicConstant(valuesFromClass = Connection.class) final int isolation,
                                final Class<?>[] rollBackForClasses) {
        this.transactionalCode = IDUtils.snowflake();
        this.timeout = timeout;
        this.isolation = isolation;
        this.rollBackForClasses = Arrays.stream(rollBackForClasses);
    }

    /**
     * <h3 class="en-US">Generate transactional configure information instance by given annotation instance</h3>
     * <h3 class="zh-CN">根据给定的注解实例对象生成数事务配置信息实例对象</h3>
     *
     * @param timeout            <span class="en-US">The timeout value of transactional</span>
     *                           <span class="zh-CN">事务的超时时间</span>
     * @param isolation          <span class="en-US">The isolation value of transactional</span>
     *                           <span class="zh-CN">事务的等级代码</span>
     * @param rollBackForClasses <span class="en-US">The rollback exception class of transactional</span>
     *                           <span class="zh-CN">事务的回滚异常</span>
     * @return <span class="en-US">Generated transactional configure information instance</span>
     * <span class="zh-CN">生成的事务配置信息实例对象</span>
     */
    public static TransactionalConfig newInstance(final int timeout,
                                                  @MagicConstant(valuesFromClass = Connection.class)final int isolation,
                                                  final Class<?>[] rollBackForClasses) {
        if (timeout < 0 || rollBackForClasses.length == 0) {
            return null;
        }
        return new TransactionalConfig(timeout, isolation, rollBackForClasses);
    }

    /**
     * <h3 class="en-US">Getter method for transactional identify code</h3>
     * <h3 class="zh-CN">事务识别代码的Getter方法</h3>
     *
     * @return <span class="en-US">Transactional identify code</span>
     * <span class="zh-CN">事务识别代码</span>
     */
    public long getTransactionalCode() {
        return transactionalCode;
    }

    /**
     * <h3 class="en-US">Getter method for the timeout value of transactional</h3>
     * <h3 class="zh-CN">事务的超时时间的Getter方法</h3>
     *
     * @return <span class="en-US">The timeout value of transactional</span>
     * <span class="zh-CN">事务的超时时间</span>
     */
    public int getTimeout() {
        return this.timeout;
    }

    /**
     * <h3 class="en-US">Getter method for the isolation value of transactional</h3>
     * <h3 class="zh-CN">事务的等级代码的Getter方法</h3>
     *
     * @return <span class="en-US">The isolation value of transactional</span>
     * <span class="zh-CN">事务的等级代码</span>
     */
    @MagicConstant(valuesFromClass = Connection.class)
    public int getIsolation() {
        return this.isolation;
    }

    /**
     * <h3 class="en-US">Getter method for the rollback exception class of transactional</h3>
     * <h3 class="zh-CN">事务的回滚异常的Getter方法</h3>
     *
	 * @param e <span class="en-US">Cached execution information</span>
	 *          <span class="zh-CN">捕获的异常信息</span>
     * @return <span class="en-US">The rollback exception class of transactional</span>
     * <span class="zh-CN">事务的回滚异常</span>
     */
    public boolean rollback(@Nonnull final Exception e) {
        return this.rollBackForClasses.anyMatch(rollbackClass -> ClassUtils.isAssignable(e.getClass(), rollbackClass));
    }
}
