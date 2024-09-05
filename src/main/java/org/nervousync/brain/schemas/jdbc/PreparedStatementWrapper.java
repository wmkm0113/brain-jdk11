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

package org.nervousync.brain.schemas.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * <h2 class="en-US">Cached prepared statement</h2>
 * <h2 class="zh-CN">可缓存的参数化查询执行器</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 21, 2019 14:27:33 $
 */
public final class PreparedStatementWrapper extends StatementWrapper<PreparedStatement> {

	/**
	 * <h3 class="en-US">Constructor method for implementation class for cached prepared statement</h3>
	 * <h3 class="zh-CN">可缓存的参数化查询执行器实现类的构造方法</h3>
	 *
	 * @param identifyKey     <span class="en-US">Identification code</span>
	 *                        <span class="zh-CN">唯一识别代码</span>
	 * @param lowQueryTimeout <span class="en-US">Low query timeout (Unit: milliseconds)</span>
	 *                        <span class="zh-CN">慢查询的临界时间（单位：毫秒）</span>
	 * @param connection      <span class="en-US">Database connection instance object</span>
	 *                        <span class="zh-CN">数据库连接实例对象</span>
	 * @param sql             <span class="en-US">SQL command to execute</span>
	 *                        <span class="zh-CN">要执行的SQL命令</span>
	 * @throws SQLException <span class="en-US">If an error occurs during parsing</span>
	 *                      <span class="zh-CN">如果解析过程出错</span>
	 */
	public PreparedStatementWrapper(final String identifyKey, final long lowQueryTimeout,
	                                final Connection connection, final String sql) throws SQLException {
		super(identifyKey, lowQueryTimeout, connection, connection.prepareStatement(sql), sql);
	}

	/**
	 * <h3 class="en-US">Constructor method for abstract class for Neurons statement</h3>
	 * <h3 class="zh-CN">可缓存的查询执行器抽象类的构造方法</h3>
	 *
	 * @param identifyKey          <span class="en-US">Identification code</span>
	 *                             <span class="zh-CN">唯一识别代码</span>
	 * @param lowQueryTimeout      <span class="en-US">Low query timeout (Unit: milliseconds)</span>
	 *                             <span class="zh-CN">慢查询的临界时间（单位：毫秒）</span>
	 * @param connection           <span class="en-US">Database connection instance object</span>
	 *                             <span class="zh-CN">数据库连接实例对象</span>
	 * @param sql                  <span class="en-US">SQL command to execute</span>
	 *                             <span class="zh-CN">要执行的SQL命令</span>
	 * @param resultSetType        <span class="en-US">One of the following ResultSet constants: ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE, or ResultSet.TYPE_SCROLL_SENSITIVE</span>
	 *                             <span class="zh-CN">以下 ResultSet 常量之一：ResultSet.TYPE_FORWARD_ONLY、ResultSet.TYPE_SCROLL_INSENSITIVE 或 ResultSet.TYPE_SCROLL_SENSITIVE</span>
	 * @param resultSetConcurrency <span class="en-US">One of the following ResultSet constants: ResultSet.CONCUR_READ_ONLY or ResultSet.CONCUR_UPDATABLE</span>
	 *                             <span class="zh-CN">以下 ResultSet 常量之一：ResultSet.CONCUR_READ_ONLY 或 ResultSet.CONCUR_UPDATABLE</span>
	 * @throws SQLException <span class="en-US">If an error occurs during parsing</span>
	 *                      <span class="zh-CN">如果解析过程出错</span>
	 */
	public PreparedStatementWrapper(final String identifyKey, final long lowQueryTimeout,
	                                final Connection connection, final String sql,
	                                final int resultSetType, final int resultSetConcurrency)
			throws SQLException {
		super(identifyKey, lowQueryTimeout, connection,
				connection.prepareStatement(sql, resultSetType, resultSetConcurrency), sql);
	}

	/**
	 * <h3 class="en-US">Constructor method for abstract class for Neurons statement</h3>
	 * <h3 class="zh-CN">可缓存的查询执行器抽象类的构造方法</h3>
	 *
	 * @param identifyKey          <span class="en-US">Identification code</span>
	 *                             <span class="zh-CN">唯一识别代码</span>
	 * @param lowQueryTimeout      <span class="en-US">Low query timeout (Unit: milliseconds)</span>
	 *                             <span class="zh-CN">慢查询的临界时间（单位：毫秒）</span>
	 * @param connection           <span class="en-US">Database connection instance object</span>
	 *                             <span class="zh-CN">数据库连接实例对象</span>
	 * @param sql                  <span class="en-US">SQL command to execute</span>
	 *                             <span class="zh-CN">要执行的SQL命令</span>
	 * @param resultSetType        <span class="en-US">One of the following ResultSet constants: ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE, or ResultSet.TYPE_SCROLL_SENSITIVE</span>
	 *                             <span class="zh-CN">以下 ResultSet 常量之一：ResultSet.TYPE_FORWARD_ONLY、ResultSet.TYPE_SCROLL_INSENSITIVE 或 ResultSet.TYPE_SCROLL_SENSITIVE</span>
	 * @param resultSetConcurrency <span class="en-US">One of the following ResultSet constants: ResultSet.CONCUR_READ_ONLY or ResultSet.CONCUR_UPDATABLE</span>
	 *                             <span class="zh-CN">以下 ResultSet 常量之一：ResultSet.CONCUR_READ_ONLY 或 ResultSet.CONCUR_UPDATABLE</span>
	 * @param resultSetHoldability <span class="en-US">One of the following ResultSet constants: ResultSet.HOLD_CURSORS_OVER_COMMIT or ResultSet.CLOSE_CURSORS_AT_COMMIT</span>
	 *                             <span class="zh-CN">以下 ResultSet 常量之一：ResultSet.HOLD_CURSORS_OVER_COMMIT 或 ResultSet.CLOSE_CURSORS_AT_COMMIT</span>
	 * @throws SQLException <span class="en-US">If an error occurs during parsing</span>
	 *                      <span class="zh-CN">如果解析过程出错</span>
	 */
	public PreparedStatementWrapper(final String identifyKey, final long lowQueryTimeout,
	                                final Connection connection, final String sql,
	                                final int resultSetType, final int resultSetConcurrency,
	                                final int resultSetHoldability) throws SQLException {
		super(identifyKey, lowQueryTimeout, connection,
				connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability), sql);
	}

	/**
	 * <h3 class="en-US">Constructor method for abstract class for Neurons statement</h3>
	 * <h3 class="zh-CN">可缓存的查询执行器抽象类的构造方法</h3>
	 *
	 * @param identifyKey       <span class="en-US">Identification code</span>
	 *                          <span class="zh-CN">唯一识别代码</span>
	 * @param lowQueryTimeout   <span class="en-US">Low query timeout (Unit: milliseconds)</span>
	 *                          <span class="zh-CN">慢查询的临界时间（单位：毫秒）</span>
	 * @param connection        <span class="en-US">Database connection instance object</span>
	 *                          <span class="zh-CN">数据库连接实例对象</span>
	 * @param sql               <span class="en-US">SQL command to execute</span>
	 *                          <span class="zh-CN">要执行的SQL命令</span>
	 * @param autoGeneratedKeys <span class="en-US">Constant indicating whether the getGeneratedKeys method should be used to make automatically generated keys available for retrieval; one of the following constants: Statement.RETURN_GENERATED_KEYS or Statement.NO_GENERATED_KEYS</span>
	 *                          <span class="zh-CN">指示是否应该使用 getGeneratedKeys 方法使自动生成的键可用于获取的常量；以下常量之一：Statement.RETURN_GENERATED_KEYS 或 Statement.NO_GENERATED_KEYS</span>
	 * @throws SQLException <span class="en-US">If an error occurs during parsing</span>
	 *                      <span class="zh-CN">如果解析过程出错</span>
	 */
	public PreparedStatementWrapper(final String identifyKey, final long lowQueryTimeout,
	                                final Connection connection, final String sql,
	                                final int autoGeneratedKeys) throws SQLException {
		super(identifyKey, lowQueryTimeout, connection, connection.prepareStatement(sql, autoGeneratedKeys), sql);
	}

	/**
	 * <h3 class="en-US">Constructor method for abstract class for Neurons statement</h3>
	 * <h3 class="zh-CN">可缓存的查询执行器抽象类的构造方法</h3>
	 *
	 * @param identifyKey     <span class="en-US">Identification code</span>
	 *                        <span class="zh-CN">唯一识别代码</span>
	 * @param lowQueryTimeout <span class="en-US">Low query timeout (Unit: milliseconds)</span>
	 *                        <span class="zh-CN">慢查询的临界时间（单位：毫秒）</span>
	 * @param connection      <span class="en-US">Database connection instance object</span>
	 *                        <span class="zh-CN">数据库连接实例对象</span>
	 * @param sql             <span class="en-US">SQL command to execute</span>
	 *                        <span class="zh-CN">要执行的SQL命令</span>
	 * @param columnIndexes   <span class="en-US">By calling the method getGeneratedKeys it should be possible to get the array of column indices in the inserted row</span>
	 *                        <span class="zh-CN">通过调用方法 getGeneratedKeys 应该可用于获取的插入行中的列索引数组</span>
	 * @throws SQLException <span class="en-US">If an error occurs during parsing</span>
	 *                      <span class="zh-CN">如果解析过程出错</span>
	 */
	public PreparedStatementWrapper(final String identifyKey, final long lowQueryTimeout,
	                                final Connection connection, final String sql,
	                                final int[] columnIndexes) throws SQLException {
		super(identifyKey, lowQueryTimeout, connection, connection.prepareStatement(sql, columnIndexes), sql);
	}

	/**
	 * <h3 class="en-US">Constructor method for abstract class for Neurons statement</h3>
	 * <h3 class="zh-CN">可缓存的查询执行器抽象类的构造方法</h3>
	 *
	 * @param identifyKey     <span class="en-US">Identification code</span>
	 *                        <span class="zh-CN">唯一识别代码</span>
	 * @param lowQueryTimeout <span class="en-US">Low query timeout (Unit: milliseconds)</span>
	 *                        <span class="zh-CN">慢查询的临界时间（单位：毫秒）</span>
	 * @param connection      <span class="en-US">Database connection instance object</span>
	 *                        <span class="zh-CN">数据库连接实例对象</span>
	 * @param sql             <span class="en-US">SQL command to execute</span>
	 *                        <span class="zh-CN">要执行的SQL命令</span>
	 * @param columnNames     <span class="en-US">By calling the method getGeneratedKeys it should be possible to get an array of column names in the inserted row</span>
	 *                        <span class="zh-CN">通过调用方法 getGeneratedKeys 应该可用于获取的插入行中的列名称数组</span>
	 * @throws SQLException <span class="en-US">If an error occurs during parsing</span>
	 *                      <span class="zh-CN">如果解析过程出错</span>
	 */
	public PreparedStatementWrapper(final String identifyKey, final long lowQueryTimeout,
	                                final Connection connection, final String sql,
	                                final String[] columnNames) throws SQLException {
		super(identifyKey, lowQueryTimeout, connection, connection.prepareStatement(sql, columnNames), sql);
	}
}
