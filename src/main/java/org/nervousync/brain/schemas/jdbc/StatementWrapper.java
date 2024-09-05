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
import org.nervousync.utils.ClassUtils;
import org.nervousync.utils.DateTimeUtils;
import org.nervousync.utils.LoggerUtils;
import org.nervousync.utils.StringUtils;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <h2 class="en-US">Abstract class for cached statement</h2>
 * <h2 class="zh-CN">可缓存的查询执行器抽象类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 21, 2019 14:27:33 $
 */
public class StatementWrapper<S extends PreparedStatement> implements PreparedStatement {

	/**
	 * <span class="en-US">Logger instance</span>
	 * <span class="zh-CN">日志实例</span>
	 */
	protected static final LoggerUtils.Logger LOGGER = LoggerUtils.getLogger(StatementWrapper.class);

	/**
	 * <span class="en-US">Low query timeout (Unit: milliseconds)</span>
	 * <span class="zh-CN">慢查询的临界时间（单位：毫秒）</span>
	 */
	protected final long lowQueryTimeout;
	/**
	 * <span class="en-US">Wrapper connection instance object</span>
	 * <span class="zh-CN">数据源连接包装类实例对象</span>
	 */
	protected final Connection connection;
	/**
	 * <span class="en-US">Identification code</span>
	 * <span class="zh-CN">唯一识别代码</span>
	 */
	private final String identifyKey;
	/**
	 * <span class="en-US">Hit count</span>
	 * <span class="zh-CN">命中的次数</span>
	 */
	private final AtomicInteger hitCount;
	/**
	 * <span class="en-US">Close status</span>
	 * <span class="zh-CN">关闭状态</span>
	 */
	protected boolean closed = Boolean.FALSE;
	/**
	 * <span class="en-US">PreparedStatement instance object</span>
	 * <span class="zh-CN">参数化查询执行器实例对象</span>
	 */
	protected final S statement;
	/**
	 * <span class="en-US">SQL command to execute</span>
	 * <span class="zh-CN">要执行的SQL命令</span>
	 */
	private final String sql;
	/**
	 * <span class="en-US">Batch operation parameter value list</span>
	 * <span class="zh-CN">批量操作参数值列表</span>
	 */
	private final List<Map<Object, Object>> batchParameters = new ArrayList<>();
	/**
	 * <span class="en-US">Parameter value mapping</span>
	 * <span class="zh-CN">参数值映射表</span>
	 */
	private final Map<Object, Object> parameterMap = new HashMap<>();
	/**
	 * <span class="en-US">Execution start timestamp</span>
	 * <span class="zh-CN">执行起始时间戳</span>
	 */
	private long beginTime = Globals.DEFAULT_VALUE_LONG;

	/**
	 * <h3 class="en-US">Constructor method for abstract class for cached statement</h3>
	 * <h3 class="zh-CN">可缓存的查询执行器抽象类的构造方法</h3>
	 *
	 * @param identifyKey     <span class="en-US">Identification code</span>
	 *                        <span class="zh-CN">唯一识别代码</span>
	 * @param lowQueryTimeout <span class="en-US">Low query timeout (Unit: milliseconds)</span>
	 *                        <span class="zh-CN">慢查询的临界时间（单位：毫秒）</span>
	 * @param connection      <span class="en-US">Wrapper connection instance object</span>
	 *                        <span class="zh-CN">数据源连接包装类实例对象</span>
	 * @param statement       <span class="en-US">PreparedStatement instance object</span>
	 *                        <span class="zh-CN">参数化查询执行器实例对象</span>
	 * @param sql             <span class="en-US">SQL command to execute</span>
	 *                        <span class="zh-CN">要执行的SQL命令</span>
	 */
	protected StatementWrapper(final String identifyKey, final long lowQueryTimeout,
	                           final Connection connection, final S statement, final String sql) {
		this.identifyKey = identifyKey;
		this.lowQueryTimeout = lowQueryTimeout;
		this.connection = connection;
		this.hitCount = new AtomicInteger(Globals.INITIALIZE_INT_VALUE);
		this.statement = statement;
		this.sql = sql;
	}

	/**
	 * <h3 class="en-US">Increment hit count</h3>
	 * <h3 class="zh-CN">增加命中次数</h3>
	 */
	public final void incrementHitCount() {
		this.reset();
		this.hitCount.incrementAndGet();
	}

	/**
	 * <h3 class="en-US">Getter method for hit count</h3>
	 * <h3 class="zh-CN">命中的次数的Getter方法</h3>
	 *
	 * @return <span class="en-US">Hit count</span>
	 * <span class="zh-CN">命中的次数</span>
	 */
	public final int getHitCount() {
		return this.hitCount.get();
	}

	/**
	 * <h3 class="en-US">Getter method for identification code</h3>
	 * <h3 class="zh-CN">唯一识别代码的Getter方法</h3>
	 *
	 * @return <span class="en-US">Identification code</span>
	 * <span class="zh-CN">唯一识别代码</span>
	 */
	public final String getIdentifyKey() {
		return this.identifyKey;
	}

	@Override
	public final Connection getConnection() {
		return this.connection;
	}

	@Override
	public final <T> T unwrap(final Class<T> clazz) throws SQLException {
		try {
			return clazz.cast(this);
		} catch (ClassCastException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public final boolean isWrapperFor(final Class<?> clazz) {
		return ClassUtils.isAssignable(clazz, this.getClass());
	}

	/**
	 * <h3 class="en-US">Add current parameter information to the batch operation list</h3>
	 * <h3 class="zh-CN">添加当前参数信息到批量操作列表</h3>
	 */
	protected final void batchParameter() {
		if (this.lowQueryTimeout > 0) {
			this.batchParameters.add(Map.copyOf(this.parameterMap));
			this.parameterMap.clear();
		}
	}

	/**
	 * <h3 class="en-US">Record parameter configure</h3>
	 * <h3 class="zh-CN">记录参数值</h3>
	 *
	 * @param key   <span class="en-US">Parameter name</span>
	 *              <span class="zh-CN">参数名</span>
	 * @param value <span class="en-US">Parameter value</span>
	 *              <span class="zh-CN">参数值</span>
	 */
	protected final void parameter(final Object key, Object value) {
		if (this.lowQueryTimeout > 0) {
			this.parameterMap.put(key, value);
		}
	}

	/**
	 * <h3 class="en-US">Record operate begin timestamp</h3>
	 * <h3 class="zh-CN">记录操作的起始时间</h3>
	 */
	protected final void begin() {
		if (this.lowQueryTimeout > 0) {
			this.beginTime = DateTimeUtils.currentUTCTimeMillis();
		}
	}

	/**
	 * <h3 class="en-US">Record operate end timestamp</h3>
	 * <h3 class="zh-CN">记录操作的终止时间</h3>
	 *
	 * @param sql <span class="en-US">SQL command</span>
	 *            <span class="zh-CN">执行的SQL命令</span>
	 */
	protected final void end(final String sql) {
		if (this.lowQueryTimeout > 0) {
			long usedTime = DateTimeUtils.currentUTCTimeMillis() - this.beginTime;
			if (this.lowQueryTimeout < usedTime) {
				String parameters;
				if (this.batchParameters.isEmpty()) {
					parameters = StringUtils.objectToString(this.parameterMap, StringUtils.StringType.JSON, Boolean.TRUE);
				} else {
					parameters = StringUtils.objectToString(this.batchParameters, StringUtils.StringType.JSON, Boolean.TRUE);
				}
				LOGGER.warn("Low_Query_Log", sql, parameters, usedTime, this.lowQueryTimeout);
			}
		}
	}

	/**
	 * <h3 class="en-US">Clear parameters</h3>
	 * <h3 class="zh-CN">清理参数信息</h3>
	 */
	protected final void clearParameterMap() {
		this.parameterMap.clear();
	}

	/**
	 * <h3 class="en-US">Clear batch parameters</h3>
	 * <h3 class="zh-CN">清理批量操作参数信息</h3>
	 */
	protected final void clearBatchParameters() {
		this.batchParameters.clear();
	}

	/**
	 * <h3 class="en-US">Reset current query</h3>
	 * <h3 class="zh-CN">重置当前查询器</h3>
	 */
	protected final void reset() {
		this.beginTime = Globals.DEFAULT_VALUE_LONG;
		this.clearParameterMap();
		this.clearBatchParameters();
	}

	@Override
	public final void addBatch(final String sql) throws UnsupportedOperationException {
		//  Current operate cannot be called on PreparedStatement
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean execute(final String sql) throws UnsupportedOperationException {
		//  Current operate cannot be called on PreparedStatement
		throw new UnsupportedOperationException();
	}

	@Override
	public final int executeUpdate(final String sql, final int autoGeneratedKeys) throws UnsupportedOperationException {
		//  Current operate cannot be called on PreparedStatement
		throw new UnsupportedOperationException();
	}

	@Override
	public final int executeUpdate(final String sql, final int[] columnIndexes) throws UnsupportedOperationException {
		//  Current operate cannot be called on PreparedStatement
		throw new UnsupportedOperationException();
	}

	@Override
	public final int executeUpdate(final String sql, final String[] columnNames) throws UnsupportedOperationException {
		//  Current operate cannot be called on PreparedStatement
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean execute(final String sql, final int autoGeneratedKeys) throws UnsupportedOperationException {
		//  Current operate cannot be called on PreparedStatement
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean execute(final String sql, final int[] columnIndexes) throws UnsupportedOperationException {
		//  Current operate cannot be called on PreparedStatement
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean execute(final String sql, final String[] columnNames) throws UnsupportedOperationException {
		//  Current operate cannot be called on PreparedStatement
		throw new UnsupportedOperationException();
	}

	@Override
	public final long executeLargeUpdate(String sql) throws UnsupportedOperationException {
		//  Current operate cannot be called on PreparedStatement
		throw new UnsupportedOperationException();
	}

	@Override
	public final long executeLargeUpdate(String sql, int autoGeneratedKeys) throws UnsupportedOperationException {
		//  Current operate cannot be called on PreparedStatement
		throw new UnsupportedOperationException();
	}

	@Override
	public final long executeLargeUpdate(String sql, int[] columnIndexes) throws UnsupportedOperationException {
		//  Current operate cannot be called on PreparedStatement
		throw new UnsupportedOperationException();
	}

	@Override
	public final long executeLargeUpdate(String sql, String[] columnNames) throws UnsupportedOperationException {
		//  Current operate cannot be called on PreparedStatement
		throw new UnsupportedOperationException();
	}

	@Override
	public final ResultSet executeQuery(final String sql) throws UnsupportedOperationException {
		//  Current operate cannot be called on PreparedStatement and CallableStatement
		throw new UnsupportedOperationException();
	}

	@Override
	public final int executeUpdate(final String sql) throws UnsupportedOperationException {
		//  Current operate cannot be called on PreparedStatement and CallableStatement
		throw new UnsupportedOperationException();
	}

	@Override
	public final int getMaxFieldSize() throws SQLException {
		return this.statement.getMaxFieldSize();
	}

	@Override
	public final void setMaxFieldSize(int max) throws SQLException {
		this.statement.setMaxFieldSize(max);
	}

	@Override
	public final int getMaxRows() throws SQLException {
		return this.statement.getMaxRows();
	}

	@Override
	public final void setMaxRows(int max) throws SQLException {
		this.statement.setMaxRows(max);
	}

	@Override
	public final void setEscapeProcessing(boolean enable) throws SQLException {
		this.statement.setEscapeProcessing(enable);
	}

	@Override
	public final int getQueryTimeout() throws SQLException {
		return this.statement.getQueryTimeout();
	}

	@Override
	public final void setQueryTimeout(int seconds) throws SQLException {
		this.statement.setQueryTimeout(seconds);
	}

	@Override
	public final void cancel() throws SQLException {
		this.statement.cancel();
	}

	@Override
	public final SQLWarning getWarnings() throws SQLException {
		return this.statement.getWarnings();
	}

	@Override
	public final void clearWarnings() throws SQLException {
		this.statement.clearWarnings();
	}

	@Override
	public final void setCursorName(String name) throws SQLException {
		this.statement.setCursorName(name);
	}

	@Override
	public final ResultSet getResultSet() throws SQLException {
		return this.statement.getResultSet();
	}

	@Override
	public final int getUpdateCount() throws SQLException {
		return this.statement.getUpdateCount();
	}

	@Override
	public final boolean getMoreResults() throws SQLException {
		return this.statement.getMoreResults();
	}

	@Override
	public final void setFetchDirection(int direction) throws SQLException {
		this.statement.setFetchDirection(direction);
	}

	@Override
	public final int getFetchDirection() throws SQLException {
		return this.statement.getFetchDirection();
	}

	@Override
	public final void setFetchSize(int rows) throws SQLException {
		this.statement.setFetchSize(rows);
	}

	@Override
	public final int getFetchSize() throws SQLException {
		return this.statement.getFetchSize();
	}

	@Override
	public final int getResultSetConcurrency() throws SQLException {
		return this.statement.getResultSetConcurrency();
	}

	@Override
	public final int getResultSetType() throws SQLException {
		return this.statement.getResultSetType();
	}

	@Override
	public final void clearBatch() throws SQLException {
		this.clearBatchParameters();
		this.statement.clearBatch();
	}

	@Override
	public final int[] executeBatch() throws SQLException {
		try {
			this.begin();
			return this.statement.executeBatch();
		} finally {
			this.end(this.sql);
		}
	}

	@Override
	public final boolean getMoreResults(final int current) throws SQLException {
		return this.statement.getMoreResults(current);
	}

	@Override
	public final ResultSet getGeneratedKeys() throws SQLException {
		return this.statement.getGeneratedKeys();
	}

	@Override
	public final int getResultSetHoldability() throws SQLException {
		return this.statement.getResultSetHoldability();
	}

	@Override
	public final boolean isClosed() throws SQLException {
		return this.statement.isClosed();
	}

	@Override
	public final void setPoolable(boolean poolable) throws SQLException {
		this.statement.setPoolable(poolable);
	}

	@Override
	public final boolean isPoolable() throws SQLException {
		return this.statement.isPoolable();
	}

	@Override
	public final void closeOnCompletion() throws SQLException {
		this.statement.closeOnCompletion();
	}

	@Override
	public final boolean isCloseOnCompletion() throws SQLException {
		return this.statement.isCloseOnCompletion();
	}

	@Override
	public final long getLargeUpdateCount() throws SQLException {
		try {
			this.begin();
			return this.statement.getLargeUpdateCount();
		} finally {
			this.end(this.sql);
		}
	}

	@Override
	public final long[] executeLargeBatch() throws SQLException {
		try {
			this.begin();
			return this.statement.executeLargeBatch();
		} finally {
			this.end(this.sql);
		}
	}

	@Override
	public final ResultSet executeQuery() throws SQLException {
		try {
			this.begin();
			return this.statement.executeQuery();
		} finally {
			this.end(this.sql);
		}
	}

	@Override
	public final int executeUpdate() throws SQLException {
		try {
			this.begin();
			return this.statement.executeUpdate();
		} finally {
			this.end(this.sql);
		}
	}

	@Override
	public final void setNull(int parameterIndex, int sqlType) throws SQLException {
		this.parameter(parameterIndex, null);
		this.statement.setNull(parameterIndex, sqlType);
	}

	@Override
	public final void setBoolean(int parameterIndex, boolean x) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setBoolean(parameterIndex, x);
	}

	@Override
	public final void setByte(int parameterIndex, byte x) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setByte(parameterIndex, x);
	}

	@Override
	public final void setShort(int parameterIndex, short x) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setShort(parameterIndex, x);
	}

	@Override
	public final void setInt(int parameterIndex, int x) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setInt(parameterIndex, x);
	}

	@Override
	public final void setLong(int parameterIndex, long x) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setLong(parameterIndex, x);
	}

	@Override
	public final void setFloat(int parameterIndex, float x) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setFloat(parameterIndex, x);
	}

	@Override
	public final void setDouble(int parameterIndex, double x) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setDouble(parameterIndex, x);
	}

	@Override
	public final void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setBigDecimal(parameterIndex, x);
	}

	@Override
	public final void setString(int parameterIndex, String x) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setString(parameterIndex, x);
	}

	@Override
	public final void setBytes(int parameterIndex, byte[] x) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setBytes(parameterIndex, x);
	}

	@Override
	public final void setDate(int parameterIndex, Date x) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setDate(parameterIndex, x);
	}

	@Override
	public final void setTime(int parameterIndex, Time x) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setTime(parameterIndex, x);
	}

	@Override
	public final void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setTimestamp(parameterIndex, x);
	}

	@Override
	public final void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setAsciiStream(parameterIndex, x, length);
	}

	@Override
	@Deprecated(since = "1.2")
	public final void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setUnicodeStream(parameterIndex, x, length);
	}

	@Override
	public final void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setBinaryStream(parameterIndex, x, length);
	}

	@Override
	public final void clearParameters() throws SQLException {
		this.clearParameterMap();
		this.statement.clearParameters();
	}

	@Override
	public final void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setObject(parameterIndex, x, targetSqlType);
	}

	@Override
	public final void setObject(int parameterIndex, Object x) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setObject(parameterIndex, x);
	}

	@Override
	public final boolean execute() throws SQLException {
		try {
			this.begin();
			return this.statement.execute();
		} finally {
			this.end(this.sql);
		}
	}

	@Override
	public final void addBatch() throws SQLException {
		this.batchParameter();
		this.statement.addBatch();
	}

	@Override
	public final void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
		this.parameter(parameterIndex, reader);
		this.statement.setCharacterStream(parameterIndex, reader, length);
	}

	@Override
	public final void setRef(int parameterIndex, Ref x) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setRef(parameterIndex, x);
	}

	@Override
	public final void setBlob(int parameterIndex, Blob x) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setBlob(parameterIndex, x);
	}

	@Override
	public final void setClob(int parameterIndex, Clob x) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setClob(parameterIndex, x);
	}

	@Override
	public final void setArray(int parameterIndex, Array x) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setArray(parameterIndex, x);
	}

	@Override
	public final ResultSetMetaData getMetaData() throws SQLException {
		return this.statement.getMetaData();
	}

	@Override
	public final void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setDate(parameterIndex, x, cal);
	}

	@Override
	public final void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setTime(parameterIndex, x, cal);
	}

	@Override
	public final void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setTimestamp(parameterIndex, x, cal);
	}

	@Override
	public final void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
		this.parameter(parameterIndex, null);
		this.statement.setNull(parameterIndex, sqlType, typeName);
	}

	@Override
	public final void setURL(int parameterIndex, URL x) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setURL(parameterIndex, x);
	}

	@Override
	public final ParameterMetaData getParameterMetaData() throws SQLException {
		return this.statement.getParameterMetaData();
	}

	@Override
	public final void setRowId(int parameterIndex, RowId x) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setRowId(parameterIndex, x);
	}

	@Override
	public final void setNString(int parameterIndex, String value) throws SQLException {
		this.parameter(parameterIndex, value);
		this.statement.setNString(parameterIndex, value);
	}

	@Override
	public final void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
		this.parameter(parameterIndex, value);
		this.statement.setNCharacterStream(parameterIndex, value, length);
	}

	@Override
	public final void setNClob(int parameterIndex, NClob value) throws SQLException {
		this.parameter(parameterIndex, value);
		this.statement.setNClob(parameterIndex, value);
	}

	@Override
	public final void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
		this.parameter(parameterIndex, reader);
		this.statement.setClob(parameterIndex, reader, length);
	}

	@Override
	public final void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
		this.parameter(parameterIndex, inputStream);
		this.statement.setBlob(parameterIndex, inputStream, length);
	}

	@Override
	public final void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
		this.parameter(parameterIndex, reader);
		this.statement.setNClob(parameterIndex, reader, length);
	}

	@Override
	public final void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
		this.parameter(parameterIndex, xmlObject);
		this.statement.setSQLXML(parameterIndex, xmlObject);
	}

	@Override
	public final void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
	}

	@Override
	public final void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setAsciiStream(parameterIndex, x, length);
	}

	@Override
	public final void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setBinaryStream(parameterIndex, x, length);
	}

	@Override
	public final void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
		this.parameter(parameterIndex, reader);
		this.statement.setCharacterStream(parameterIndex, reader, length);
	}

	@Override
	public final void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setAsciiStream(parameterIndex, x);
	}

	@Override
	public final void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setBinaryStream(parameterIndex, x);
	}

	@Override
	public final void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
		this.parameter(parameterIndex, reader);
		this.statement.setCharacterStream(parameterIndex, reader);
	}

	@Override
	public final void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
		this.parameter(parameterIndex, value);
		this.statement.setNCharacterStream(parameterIndex, value);
	}

	@Override
	public final void setClob(int parameterIndex, Reader reader) throws SQLException {
		this.parameter(parameterIndex, reader);
		this.statement.setClob(parameterIndex, reader);
	}

	@Override
	public final void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
		this.parameter(parameterIndex, inputStream);
		this.statement.setBlob(parameterIndex, inputStream);
	}

	@Override
	public final void setNClob(int parameterIndex, Reader reader) throws SQLException {
		this.parameter(parameterIndex, reader);
		this.statement.setNClob(parameterIndex, reader);
	}

	@Override
	public final void setObject(int parameterIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
	}

	@Override
	public final void setObject(int parameterIndex, Object x, SQLType targetSqlType) throws SQLException {
		this.parameter(parameterIndex, x);
		this.statement.setObject(parameterIndex, x, targetSqlType);
	}

	@Override
	public final void close() throws SQLException {
		if (this.closed) {
			return;
		}
		this.clearBatch();
		this.clearWarnings();
		this.reset();
		this.statement.close();
		this.closed = Boolean.TRUE;
	}
}
