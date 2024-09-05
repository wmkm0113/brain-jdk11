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

import org.nervousync.commons.Globals;
import org.nervousync.utils.*;

import java.sql.*;
import java.util.*;
import java.util.concurrent.Executor;

/**
 * <h2 class="en-US">Data source creates a wrapper class for the connection</h2>
 * <h2 class="zh-CN">数据源创建连接的包装类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Nov 12, 2020 18:51:19 $
 */
public class JdbcConnection implements Connection {

	/**
	 * <span class="en-US">Logger instance</span>
	 * <span class="zh-CN">日志实例</span>
	 */
	private static final LoggerUtils.Logger LOGGER = LoggerUtils.getLogger(JdbcConnection.class);

	/**
	 * <span class="en-US">Database connection pool</span>
	 * <span class="zh-CN">数据库连接池</span>
	 */
	private final JdbcConnectionPool connectionPool;
	/**
	 * <span class="en-US">Maximum size of prepared statement</span>
	 * <span class="zh-CN">查询分析器的最大缓存结果</span>
	 */
	private int cachedLimitSize;
	/**
	 * <span class="en-US">Database connection instance object</span>
	 * <span class="zh-CN">数据库连接实例对象</span>
	 */
	private final Connection connection;
	/**
	 * <span class="en-US">Low query timeout (Unit: milliseconds)</span>
	 * <span class="zh-CN">慢查询的临界时间（单位：毫秒）</span>
	 */
	private final long lowQueryTimeout;
	/**
	 * <span class="en-US">Cached prepared statement mapping</span>
	 * <span class="zh-CN">缓存的查询分析器映射表</span>
	 */
	private final List<StatementWrapper<?>> cachedStatements;

	/**
	 * <h3 class="en-US">Constructor method for data source creates a wrapper class for the connection</h3>
	 * <h3 class="zh-CN">数据源创建连接的包装类的构造方法</h3>
	 *
	 * @param connectionPool  <span class="en-US">Database connection pool</span>
	 *                        <span class="zh-CN">数据库连接池</span>
	 * @param connection      <span class="en-US">Database connection instance object</span>
	 *                        <span class="zh-CN">数据库连接实例对象</span>
	 * @param lowQueryTimeout <span class="en-US">Low query timeout (Unit: milliseconds)</span>
	 *                        <span class="zh-CN">慢查询的临界时间（单位：毫秒）</span>
	 * @param cachedLimitSize <span class="en-US">Maximum size of prepared statement</span>
	 *                        <span class="zh-CN">查询分析器的最大缓存结果</span>
	 */
	JdbcConnection(final JdbcConnectionPool connectionPool, final Connection connection,
	               final long lowQueryTimeout, final int cachedLimitSize) {
		this.connectionPool = connectionPool;
		this.connection = connection;
		this.lowQueryTimeout = lowQueryTimeout;
		this.cachedLimitSize = cachedLimitSize;
		this.cachedStatements = new ArrayList<>();
	}

	/**
	 * <h3 class="en-US">Getter method for connection pool identify code</h3>
	 * <h3 class="zh-CN">连接池识别代码的Getter方法</h3>
	 *
	 * @return <span class="en-US">Connection pool identify code</span>
	 * <span class="zh-CN">连接池识别代码</span>
	 */
	public int identifyCode() {
		return this.connectionPool.getIdentifyCode();
	}

	/**
	 * <h3 class="en-US">Setter method for maximum size of prepared statement</h3>
	 * <h3 class="zh-CN">查询分析器的最大缓存结果的Setter方法</h3>
	 *
	 * @param cachedLimitSize <span class="en-US">Maximum size of prepared statement</span>
	 *                        <span class="zh-CN">查询分析器的最大缓存结果</span>
	 */
	public void setCachedLimitSize(final int cachedLimitSize) {
		this.cachedLimitSize = cachedLimitSize;
	}

	@Override
	public void close() throws SQLException {
		if (this.connection.getTransactionIsolation() == Connection.TRANSACTION_NONE) {
			this.forceClose();
		}
	}

	/**
	 * <h3 class="en-US">Close current connection</h3>
	 * <h3 class="zh-CN">关闭当前连接</h3>
	 *
	 * @throws SQLException <span class="en-US">Error closing connection</span>
	 *                      <span class="zh-CN">关闭连接时出错</span>
	 */
	public void forceClose() throws SQLException {
		this.connectionPool.closeConnection(this);
	}

	/**
	 * <h3 class="en-US">Destroy current connection</h3>
	 * <h3 class="zh-CN">销毁当前连接</h3>
	 *
	 * @throws SQLException <span class="en-US">Error closing connection</span>
	 *                      <span class="zh-CN">关闭连接时出错</span>
	 */
	public void destroy() throws SQLException {
		this.cachedStatements.clear();
		if (!this.connection.isClosed()) {
			this.connection.close();
		}
	}

	@Override
	public <T> T unwrap(final Class<T> clazz) throws SQLException {
		try {
			return clazz.cast(this);
		} catch (ClassCastException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public boolean isWrapperFor(final Class<?> clazz) {
		return ClassUtils.isAssignable(clazz, this.getClass());
	}

	@Override
	public Statement createStatement() throws SQLException {
		return this.connection.createStatement();
	}

	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return this.obtainStatement(KeyType.SQL_ONLY, sql, Globals.DEFAULT_VALUE_INT,
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT,
				new int[0], new String[0]).unwrap(CallableStatement.class);
	}

	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		return this.obtainStatement(KeyType.CALL_ONLY, sql, Globals.DEFAULT_VALUE_INT,
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT,
				new int[0], new String[0]).unwrap(CallableStatement.class);
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		return this.connection.createStatement(resultSetType, resultSetConcurrency);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
			throws SQLException {
		return this.obtainStatement(KeyType.SQL_CONCURRENCY, sql, resultSetType, resultSetConcurrency,
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT,
				new int[0], new String[0]).unwrap(PreparedStatement.class);
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
			throws SQLException {
		return this.obtainStatement(KeyType.CALL_CONCURRENCY, sql, resultSetType, resultSetConcurrency,
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT,
				new int[0], new String[0]).unwrap(CallableStatement.class);
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return this.connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType,
	                                          int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return this.obtainStatement(KeyType.SQL_HOLDABILITY, sql, resultSetType,
				resultSetConcurrency, resultSetHoldability, Globals.DEFAULT_VALUE_INT,
				new int[0], new String[0]).unwrap(PreparedStatement.class);
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType,
	                                     int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return this.obtainStatement(KeyType.CALL_HOLDABILITY, sql, resultSetType,
				resultSetConcurrency, resultSetHoldability, Globals.DEFAULT_VALUE_INT,
				new int[0], new String[0]).unwrap(CallableStatement.class);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		return this.obtainStatement(KeyType.SQL_AUTO_GENERATED_KEYS, sql, Globals.DEFAULT_VALUE_INT,
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, autoGeneratedKeys,
				new int[0], new String[0]).unwrap(PreparedStatement.class);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		return this.obtainStatement(KeyType.SQL_COLUMN_INDEXES, sql, Globals.DEFAULT_VALUE_INT,
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT,
				columnIndexes, new String[0]).unwrap(PreparedStatement.class);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		return this.obtainStatement(KeyType.SQL_COLUMN_NAMES, sql, Globals.DEFAULT_VALUE_INT,
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT,
				new int[0], columnNames).unwrap(PreparedStatement.class);
	}

	@Override
	public String nativeSQL(String sql) throws SQLException {
		return this.connection.nativeSQL(sql);
	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		this.connection.setAutoCommit(autoCommit);
	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		return this.connection.getAutoCommit();
	}

	@Override
	public void commit() throws SQLException {
		this.connection.commit();
	}

	@Override
	public void rollback() throws SQLException {
		this.connection.rollback();
	}

	@Override
	public boolean isClosed() throws SQLException {
		return this.connection.isClosed();
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		return this.connection.getMetaData();
	}

	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		this.connection.setReadOnly(readOnly);
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		return this.connection.isReadOnly();
	}

	@Override
	public void setCatalog(String catalog) throws SQLException {
		this.connection.setCatalog(catalog);
	}

	@Override
	public String getCatalog() throws SQLException {
		return this.connection.getCatalog();
	}

	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		this.connection.setTransactionIsolation(level);
	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		return this.connection.getTransactionIsolation();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return this.connection.getWarnings();
	}

	@Override
	public void clearWarnings() throws SQLException {
		this.connection.clearWarnings();
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return this.connection.getTypeMap();
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		this.connection.setTypeMap(map);
	}

	@Override
	public void setHoldability(int holdability) throws SQLException {
		this.connection.setHoldability(holdability);
	}

	@Override
	public int getHoldability() throws SQLException {
		return this.connection.getHoldability();
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		return this.connection.setSavepoint();
	}

	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		return this.connection.setSavepoint(name);
	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		this.connection.rollback(savepoint);
	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		this.connection.releaseSavepoint(savepoint);
	}

	@Override
	public Clob createClob() throws SQLException {
		return this.connection.createClob();
	}

	@Override
	public Blob createBlob() throws SQLException {
		return this.connection.createBlob();
	}

	@Override
	public NClob createNClob() throws SQLException {
		return this.connection.createNClob();
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		return this.connection.createSQLXML();
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		return this.connection.isValid(timeout);
	}

	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		try {
			this.connection.setClientInfo(name, value);
		} catch (SQLException e) {
			throw new SQLClientInfoException(Map.of(), e);
		}
	}

	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		try {
			this.connection.setClientInfo(properties);
		} catch (SQLException e) {
			throw new SQLClientInfoException(Map.of(), e);
		}
	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		return this.connection.getClientInfo(name);
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		return this.connection.getClientInfo();
	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		return this.connection.createArrayOf(typeName, elements);
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		return this.connection.createStruct(typeName, attributes);
	}

	@Override
	public void setSchema(String schema) throws SQLException {
		this.connection.setSchema(schema);
	}

	@Override
	public String getSchema() throws SQLException {
		return this.connection.getSchema();
	}

	@Override
	public void abort(Executor executor) throws SQLException {
		this.connection.abort(executor);
	}

	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		this.connection.setNetworkTimeout(executor, milliseconds);
	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		return this.connection.getNetworkTimeout();
	}

	/**
	 * <h3 class="en-US">Obtain query statement instance object</h3>
	 * <h3 class="zh-CN">获取查询执行器</h3>
	 *
	 * @param keyType              <span class="en-US">Enumeration value of SQL command type</span>
	 *                             <span class="zh-CN">SQL语句类型枚举值</span>
	 * @param sql                  <span class="en-US">SQL command</span>
	 *                             <span class="zh-CN">SQL语句</span>
	 * @param resultSetType        <span class="en-US">One of the following ResultSet constants: ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE, or ResultSet.TYPE_SCROLL_SENSITIVE</span>
	 *                             <span class="zh-CN">以下 ResultSet 常量之一：ResultSet.TYPE_FORWARD_ONLY、ResultSet.TYPE_SCROLL_INSENSITIVE 或 ResultSet.TYPE_SCROLL_SENSITIVE</span>
	 * @param resultSetConcurrency <span class="en-US">One of the following ResultSet constants: ResultSet.CONCUR_READ_ONLY or ResultSet.CONCUR_UPDATABLE</span>
	 *                             <span class="zh-CN">以下 ResultSet 常量之一：ResultSet.CONCUR_READ_ONLY 或 ResultSet.CONCUR_UPDATABLE</span>
	 * @param resultSetHoldability <span class="en-US">One of the following ResultSet constants: ResultSet.HOLD_CURSORS_OVER_COMMIT or ResultSet.CLOSE_CURSORS_AT_COMMIT</span>
	 *                             <span class="zh-CN">以下 ResultSet 常量之一：ResultSet.HOLD_CURSORS_OVER_COMMIT 或 ResultSet.CLOSE_CURSORS_AT_COMMIT</span>
	 * @param autoGeneratedKeys    <span class="en-US">Constant indicating whether the getGeneratedKeys method should be used to make automatically generated keys available for retrieval; one of the following constants: Statement.RETURN_GENERATED_KEYS or Statement.NO_GENERATED_KEYS</span>
	 *                             <span class="zh-CN">指示是否应该使用 getGeneratedKeys 方法使自动生成的键可用于获取的常量；以下常量之一：Statement.RETURN_GENERATED_KEYS 或 Statement.NO_GENERATED_KEYS</span>
	 * @param columnIndexes        <span class="en-US">By calling the method getGeneratedKeys it should be possible to get the array of column indices in the inserted row</span>
	 *                             <span class="zh-CN">通过调用方法 getGeneratedKeys 应该可用于获取的插入行中的列索引数组</span>
	 * @param columnNames          <span class="en-US">By calling the method getGeneratedKeys it should be possible to get an array of column names in the inserted row</span>
	 *                             <span class="zh-CN">通过调用方法 getGeneratedKeys 应该可用于获取的插入行中的列名称数组</span>
	 * @return <span class="en-US">Query statement instance object</span>
	 * <span class="zh-CN">查询执行器实例对象</span>
	 * @throws SQLException <span class="en-US">This method is called if a database access error occurs or on a closed connection</span>
	 *                      <span class="zh-CN">如果发生数据库访问错误，或者在关闭的连接上调用此方法</span>
	 */
	private StatementWrapper<?> obtainStatement(
			final KeyType keyType, final String sql, final int resultSetType, final int resultSetConcurrency,
			final int resultSetHoldability, final int autoGeneratedKeys, final int[] columnIndexes,
			final String[] columnNames) throws SQLException {
		String cacheKey = cacheKey(keyType, sql, resultSetType, resultSetConcurrency,
				resultSetHoldability, autoGeneratedKeys, columnIndexes, columnNames);
		StatementWrapper<?> statementWrapper = null;
		if (this.cachedLimitSize > 0) {
			statementWrapper = this.cachedStatements.stream()
					.filter(existStatement -> ObjectUtils.nullSafeEquals(cacheKey, existStatement.getIdentifyKey()))
					.findFirst()
					.orElse(null);
		}
		if (statementWrapper == null) {
			switch (keyType) {
				case CALL_ONLY:
					statementWrapper = new CallableStatementWrapper(cacheKey, this.lowQueryTimeout, connection, sql);
					break;
				case CALL_CONCURRENCY:
					statementWrapper = new CallableStatementWrapper(cacheKey, this.lowQueryTimeout, connection, sql,
							resultSetType, resultSetConcurrency);
					break;
				case CALL_HOLDABILITY:
					statementWrapper = new CallableStatementWrapper(cacheKey, this.lowQueryTimeout, connection, sql,
							resultSetType, resultSetConcurrency, resultSetHoldability);
					break;
				case SQL_ONLY:
					statementWrapper = new PreparedStatementWrapper(cacheKey, this.lowQueryTimeout, connection, sql);
					break;
				case SQL_CONCURRENCY:
					statementWrapper = new PreparedStatementWrapper(cacheKey, this.lowQueryTimeout, connection,
							sql, resultSetType, resultSetConcurrency);
					break;
				case SQL_HOLDABILITY:
					statementWrapper = new PreparedStatementWrapper(cacheKey, this.lowQueryTimeout, connection,
							sql, resultSetType, resultSetConcurrency, resultSetHoldability);
					break;
				case SQL_AUTO_GENERATED_KEYS:
					statementWrapper = new PreparedStatementWrapper(cacheKey, this.lowQueryTimeout, connection, sql,
							autoGeneratedKeys);
					break;
				case SQL_COLUMN_INDEXES:
					statementWrapper = new PreparedStatementWrapper(cacheKey, this.lowQueryTimeout, connection, sql,
							columnIndexes);
					break;
				case SQL_COLUMN_NAMES:
					statementWrapper = new PreparedStatementWrapper(cacheKey, this.lowQueryTimeout, connection, sql,
							columnNames);
					break;
				default:
					throw new SQLException("Unknown key type! ");
			}
			if (this.cachedLimitSize > 0) {
				this.cachedStatements.add(statementWrapper);
			}
		}
		if (this.cachedLimitSize > 0) {
			statementWrapper.incrementHitCount();
			this.cachedStatements.sort((o1, o2) -> Integer.compare(o2.getHitCount(), o1.getHitCount()));
			while (this.cachedLimitSize < this.cachedStatements.size()) {
				StatementWrapper<?> removeWrapper = this.cachedStatements.get(this.cachedStatements.size() - 1);
				removeWrapper.close();
				this.cachedStatements.remove(removeWrapper);
			}
		}

		return statementWrapper;
	}

	/**
	 * <h3 class="en-US">Generate identification code of statement instance</h3>
	 * <h3 class="zh-CN">生成查询分析器缓存唯一识别代码</h3>
	 *
	 * @param keyType              <span class="en-US">Enumeration value of SQL command type</span>
	 *                             <span class="zh-CN">SQL语句类型枚举值</span>
	 * @param sql                  <span class="en-US">SQL command</span>
	 *                             <span class="zh-CN">SQL语句</span>
	 * @param resultSetType        <span class="en-US">Result set type code</span>
	 *                             <span class="zh-CN">结果集类型代码</span>
	 * @param resultSetConcurrency <span class="en-US">Result set data type code</span>
	 *                             <span class="zh-CN">结果集数据类型代码</span>
	 * @param resultSetHoldability <span class="en-US">Result set transactional type code</span>
	 *                             <span class="zh-CN">结果集事务类型代码</span>
	 * @param autoGeneratedKeys    <span class="en-US">Returns the flag for automatically generated key values</span>
	 *                             <span class="zh-CN">返回自动生成键值的标志</span>
	 * @param columnIndexes        <span class="en-US">Array of column indices that can be used to get the inserted row</span>
	 *                             <span class="zh-CN">可用于获取的插入行中的列索引数组</span>
	 * @param columnNames          <span class="en-US">Can be used to get an array of column names in the inserted row</span>
	 *                             <span class="zh-CN">可用于获取的插入行中的列名称数组</span>
	 * @return <span class="en-US">Generated identification code</span>
	 * <span class="zh-CN">生成的唯一识别代码</span>
	 */
	private static String cacheKey(KeyType keyType, String sql, int resultSetType,
	                               int resultSetConcurrency, int resultSetHoldability, int autoGeneratedKeys,
	                               int[] columnIndexes, String[] columnNames) {
		TreeMap<String, Object> cacheMap = new TreeMap<>();
		cacheMap.put("KeyType", keyType.toString().toUpperCase());
		cacheMap.put("SQLCmd", sql);
		switch (keyType) {
//			case SQL_CONCURRENCY:
			case CALL_CONCURRENCY:
			case SQL_CONCURRENCY:
				cacheMap.put("ResultSetType", resultSetType);
				cacheMap.put("ResultSetConcurrency", resultSetConcurrency);
				break;
//			case SQL_HOLDABILITY:
			case CALL_HOLDABILITY:
			case SQL_HOLDABILITY:
				cacheMap.put("ResultSetType", resultSetType);
				cacheMap.put("ResultSetConcurrency", resultSetConcurrency);
				cacheMap.put("ResultSetHoldability", resultSetHoldability);
				break;
			case SQL_AUTO_GENERATED_KEYS:
				cacheMap.put("AutoGeneratedKeys", autoGeneratedKeys);
				break;
			case SQL_COLUMN_INDEXES:
				cacheMap.put("ColumnIndexes", columnIndexes);
				break;
			case SQL_COLUMN_NAMES:
				cacheMap.put("ColumnNames", columnNames);
				break;
		}
		String jsonData = StringUtils.objectToString(cacheMap, StringUtils.StringType.JSON, Boolean.FALSE);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Cache_Statement_Map", jsonData);
		}
		return ConvertUtils.toHex(SecurityUtils.SHA256(jsonData));
	}

	/**
	 * <h2 class="en-US">Enumeration value of SQL command type</h2>
	 * <h2 class="zh-CN">SQL语句类型枚举值</h2>
	 */
	private enum KeyType {
		/**
		 * <span class="en-US">Parameterized SQL query statement</span>
		 * <span class="zh-CN">参数化SQL查询</span>
		 */
		SQL_ONLY,
		SQL_CONCURRENCY,
		SQL_HOLDABILITY,
		SQL_AUTO_GENERATED_KEYS,
		SQL_COLUMN_INDEXES,
		SQL_COLUMN_NAMES,
		/**
		 * <span class="en-US">Stored procedure call</span>
		 * <span class="zh-CN">存储过程调用</span>
		 */
		CALL_ONLY,
		CALL_CONCURRENCY,
		CALL_HOLDABILITY
	}
}
