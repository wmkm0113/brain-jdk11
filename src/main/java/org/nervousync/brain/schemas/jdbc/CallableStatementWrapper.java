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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Map;

/**
 * <h2 class="en-US">Cached callable statement</h2>
 * <h2 class="zh-CN">可缓存的参数化存储过程执行器</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 21, 2019 15:09:22 $
 */
public final class CallableStatementWrapper extends StatementWrapper<CallableStatement>
		implements CallableStatement {

	/**
	 * <h3 class="en-US">Constructor method for implementation class for callable statement</h3>
	 * <h3 class="zh-CN">可缓存的参数化存储过程执行器实现类的构造方法</h3>
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
	public CallableStatementWrapper(final String identifyKey, final long lowQueryTimeout,
	                                final Connection connection, final String sql) throws SQLException {
		super(identifyKey, lowQueryTimeout, connection, connection.prepareCall(sql), sql);
	}

	/**
	 * <h3 class="en-US">Constructor method for implementation class for callable statement</h3>
	 * <h3 class="zh-CN">可缓存的参数化存储过程执行器实现类的构造方法</h3>
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
	public CallableStatementWrapper(final String identifyKey, final long lowQueryTimeout,
	                                final Connection connection, final String sql,
	                                final int resultSetType, final int resultSetConcurrency) throws SQLException {
		super(identifyKey, lowQueryTimeout, connection,
				connection.prepareCall(sql, resultSetType, resultSetConcurrency), sql);
	}

	/**
	 * <h3 class="en-US">Constructor method for implementation class for callable statement</h3>
	 * <h3 class="zh-CN">可缓存的参数化存储过程执行器实现类的构造方法</h3>
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
	public CallableStatementWrapper(final String identifyKey, final long lowQueryTimeout,
	                                final Connection connection, final String sql,
	                                final int resultSetType, final int resultSetConcurrency,
	                                final int resultSetHoldability) throws SQLException {
		super(identifyKey, lowQueryTimeout, connection,
				connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability), sql);
	}

	@Override
	public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
		this.statement.registerOutParameter(parameterIndex, sqlType);
	}

	@Override
	public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
		this.statement.registerOutParameter(parameterIndex, sqlType, scale);
	}

	@Override
	public boolean wasNull() throws SQLException {
		return this.statement.wasNull();
	}

	@Override
	public String getString(int parameterIndex) throws SQLException {
		return this.statement.getString(parameterIndex);
	}

	@Override
	public boolean getBoolean(int parameterIndex) throws SQLException {
		return this.statement.getBoolean(parameterIndex);
	}

	@Override
	public byte getByte(int parameterIndex) throws SQLException {
		return this.statement.getByte(parameterIndex);
	}

	@Override
	public short getShort(int parameterIndex) throws SQLException {
		return this.statement.getShort(parameterIndex);
	}

	@Override
	public int getInt(int parameterIndex) throws SQLException {
		return this.statement.getInt(parameterIndex);
	}

	@Override
	public long getLong(int parameterIndex) throws SQLException {
		return this.statement.getLong(parameterIndex);
	}

	@Override
	public float getFloat(int parameterIndex) throws SQLException {
		return this.statement.getFloat(parameterIndex);
	}

	@Override
	public double getDouble(int parameterIndex) throws SQLException {
		return this.statement.getDouble(parameterIndex);
	}

	@Override
	@Deprecated(since = "2.0")
	public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
		return this.statement.getBigDecimal(parameterIndex, scale);
	}

	@Override
	public byte[] getBytes(int parameterIndex) throws SQLException {
		return this.statement.getBytes(parameterIndex);
	}

	@Override
	public Date getDate(int parameterIndex) throws SQLException {
		return this.statement.getDate(parameterIndex);
	}

	@Override
	public Time getTime(int parameterIndex) throws SQLException {
		return this.statement.getTime(parameterIndex);
	}

	@Override
	public Timestamp getTimestamp(int parameterIndex) throws SQLException {
		return this.statement.getTimestamp(parameterIndex);
	}

	@Override
	public Object getObject(int parameterIndex) throws SQLException {
		return this.statement.getObject(parameterIndex);
	}

	@Override
	public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
		return this.statement.getBigDecimal(parameterIndex);
	}

	@Override
	public Object getObject(int parameterIndex, Map<String, Class<?>> map) throws SQLException {
		return this.statement.getObject(parameterIndex, map);
	}

	@Override
	public Ref getRef(int parameterIndex) throws SQLException {
		return this.statement.getRef(parameterIndex);
	}

	@Override
	public Blob getBlob(int parameterIndex) throws SQLException {
		return this.statement.getBlob(parameterIndex);
	}

	@Override
	public Clob getClob(int parameterIndex) throws SQLException {
		return this.statement.getClob(parameterIndex);
	}

	@Override
	public Array getArray(int parameterIndex) throws SQLException {
		return this.statement.getArray(parameterIndex);
	}

	@Override
	public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
		return this.statement.getDate(parameterIndex, cal);
	}

	@Override
	public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
		return this.statement.getTime(parameterIndex, cal);
	}

	@Override
	public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
		return this.statement.getTimestamp(parameterIndex, cal);
	}

	@Override
	public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
		this.statement.registerOutParameter(parameterIndex, sqlType, typeName);
	}

	@Override
	public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
		this.statement.registerOutParameter(parameterName, sqlType);
	}

	@Override
	public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
		this.statement.registerOutParameter(parameterName, sqlType, scale);
	}

	@Override
	public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
		this.statement.registerOutParameter(parameterName, sqlType, typeName);
	}

	@Override
	public URL getURL(int parameterIndex) throws SQLException {
		return this.statement.getURL(parameterIndex);
	}

	@Override
	public void setURL(String parameterName, URL val) throws SQLException {
		super.parameter(parameterName, val);
		this.statement.setURL(parameterName, val);
	}

	@Override
	public void setNull(String parameterName, int sqlType) throws SQLException {
		super.parameter(parameterName, null);
		this.statement.setNull(parameterName, sqlType);
	}

	@Override
	public void setBoolean(String parameterName, boolean x) throws SQLException {
		super.parameter(parameterName, x);
		this.statement.setBoolean(parameterName, x);
	}

	@Override
	public void setByte(String parameterName, byte x) throws SQLException {
		super.parameter(parameterName, x);
		this.statement.setByte(parameterName, x);
	}

	@Override
	public void setShort(String parameterName, short x) throws SQLException {
		super.parameter(parameterName, x);
		this.statement.setShort(parameterName, x);
	}

	@Override
	public void setInt(String parameterName, int x) throws SQLException {
		super.parameter(parameterName, x);
		this.statement.setInt(parameterName, x);
	}

	@Override
	public void setLong(String parameterName, long x) throws SQLException {
		super.parameter(parameterName, x);
		this.statement.setLong(parameterName, x);
	}

	@Override
	public void setFloat(String parameterName, float x) throws SQLException {
		super.parameter(parameterName, x);
		this.statement.setFloat(parameterName, x);
	}

	@Override
	public void setDouble(String parameterName, double x) throws SQLException {
		super.parameter(parameterName, x);
		this.statement.setDouble(parameterName, x);
	}

	@Override
	public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
		super.parameter(parameterName, x);
		this.statement.setBigDecimal(parameterName, x);
	}

	@Override
	public void setString(String parameterName, String x) throws SQLException {
		super.parameter(parameterName, x);
		this.statement.setString(parameterName, x);
	}

	@Override
	public void setBytes(String parameterName, byte[] x) throws SQLException {
		super.parameter(parameterName, x);
		this.statement.setBytes(parameterName, x);
	}

	@Override
	public void setDate(String parameterName, Date x) throws SQLException {
		super.parameter(parameterName, x);
		this.statement.setDate(parameterName, x);
	}

	@Override
	public void setTime(String parameterName, Time x) throws SQLException {
		super.parameter(parameterName, x);
		this.statement.setTime(parameterName, x);
	}

	@Override
	public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
		super.parameter(parameterName, x);
		this.statement.setTimestamp(parameterName, x);
	}

	@Override
	public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
		super.parameter(parameterName, x);
		this.statement.setAsciiStream(parameterName, x, length);
	}

	@Override
	public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
		super.parameter(parameterName, x);
		this.statement.setBinaryStream(parameterName, x, length);
	}

	@Override
	public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
		super.parameter(parameterName, x);
		this.statement.setObject(parameterName, x, targetSqlType, scale);
	}

	@Override
	public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
		super.parameter(parameterName, x);
		this.statement.setObject(parameterName, x, targetSqlType);
	}

	@Override
	public void setObject(String parameterName, Object x) throws SQLException {
		super.parameter(parameterName, x);
		this.statement.setObject(parameterName, x);
	}

	@Override
	public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
		super.parameter(parameterName, reader);
		this.statement.setCharacterStream(parameterName, reader, length);
	}

	@Override
	public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
		super.parameter(parameterName, x);
		this.statement.setDate(parameterName, x, cal);
	}

	@Override
	public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
		super.parameter(parameterName, x);
		this.statement.setTime(parameterName, x, cal);
	}

	@Override
	public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
		super.parameter(parameterName, x);
		this.statement.setTimestamp(parameterName, x, cal);
	}

	@Override
	public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
		super.parameter(parameterName, null);
		this.statement.setNull(parameterName, sqlType, typeName);
	}

	@Override
	public String getString(String parameterName) throws SQLException {
		return this.statement.getString(parameterName);
	}

	@Override
	public boolean getBoolean(String parameterName) throws SQLException {
		return this.statement.getBoolean(parameterName);
	}

	@Override
	public byte getByte(String parameterName) throws SQLException {
		return this.statement.getByte(parameterName);
	}

	@Override
	public short getShort(String parameterName) throws SQLException {
		return this.statement.getShort(parameterName);
	}

	@Override
	public int getInt(String parameterName) throws SQLException {
		return this.statement.getInt(parameterName);
	}

	@Override
	public long getLong(String parameterName) throws SQLException {
		return this.statement.getLong(parameterName);
	}

	@Override
	public float getFloat(String parameterName) throws SQLException {
		return this.statement.getFloat(parameterName);
	}

	@Override
	public double getDouble(String parameterName) throws SQLException {
		return this.statement.getDouble(parameterName);
	}

	@Override
	public byte[] getBytes(String parameterName) throws SQLException {
		return this.statement.getBytes(parameterName);
	}

	@Override
	public Date getDate(String parameterName) throws SQLException {
		return this.statement.getDate(parameterName);
	}

	@Override
	public Time getTime(String parameterName) throws SQLException {
		return this.statement.getTime(parameterName);
	}

	@Override
	public Timestamp getTimestamp(String parameterName) throws SQLException {
		return this.statement.getTimestamp(parameterName);
	}

	@Override
	public Object getObject(String parameterName) throws SQLException {
		return this.statement.getObject(parameterName);
	}

	@Override
	public BigDecimal getBigDecimal(String parameterName) throws SQLException {
		return this.statement.getBigDecimal(parameterName);
	}

	@Override
	public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
		return this.statement.getObject(parameterName, map);
	}

	@Override
	public Ref getRef(String parameterName) throws SQLException {
		return this.statement.getRef(parameterName);
	}

	@Override
	public Blob getBlob(String parameterName) throws SQLException {
		return this.statement.getBlob(parameterName);
	}

	@Override
	public Clob getClob(String parameterName) throws SQLException {
		return this.statement.getClob(parameterName);
	}

	@Override
	public Array getArray(String parameterName) throws SQLException {
		return this.statement.getArray(parameterName);
	}

	@Override
	public Date getDate(String parameterName, Calendar cal) throws SQLException {
		return this.statement.getDate(parameterName, cal);
	}

	@Override
	public Time getTime(String parameterName, Calendar cal) throws SQLException {
		return this.statement.getTime(parameterName, cal);
	}

	@Override
	public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
		return this.statement.getTimestamp(parameterName, cal);
	}

	@Override
	public URL getURL(String parameterName) throws SQLException {
		return this.statement.getURL(parameterName);
	}

	@Override
	public RowId getRowId(int parameterIndex) throws SQLException {
		return this.statement.getRowId(parameterIndex);
	}

	@Override
	public RowId getRowId(String parameterName) throws SQLException {
		return this.statement.getRowId(parameterName);
	}

	@Override
	public void setRowId(String parameterName, RowId x) throws SQLException {
		super.parameter(parameterName, x);
		this.statement.setRowId(parameterName, x);
	}

	@Override
	public void setNString(String parameterName, String value) throws SQLException {
		super.parameter(parameterName, value);
		this.statement.setNString(parameterName, value);
	}

	@Override
	public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
		super.parameter(parameterName, value);
		this.statement.setNCharacterStream(parameterName, value, length);
	}

	@Override
	public void setNClob(String parameterName, NClob value) throws SQLException {
		super.parameter(parameterName, value);
		this.statement.setNClob(parameterName, value);
	}

	@Override
	public void setClob(String parameterName, Reader reader, long length) throws SQLException {
		super.parameter(parameterName, reader);
		this.statement.setClob(parameterName, reader, length);
	}

	@Override
	public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
		super.parameter(parameterName, inputStream);
		this.statement.setBlob(parameterName, inputStream, length);
	}

	@Override
	public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
		super.parameter(parameterName, reader);
		this.statement.setNClob(parameterName, reader, length);
	}

	@Override
	public NClob getNClob(int parameterIndex) throws SQLException {
		return this.statement.getNClob(parameterIndex);
	}

	@Override
	public NClob getNClob(String parameterName) throws SQLException {
		return this.statement.getNClob(parameterName);
	}

	@Override
	public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
		super.parameter(parameterName, xmlObject);
		this.statement.setSQLXML(parameterName, xmlObject);
	}

	@Override
	public SQLXML getSQLXML(int parameterIndex) throws SQLException {
		return this.statement.getSQLXML(parameterIndex);
	}

	@Override
	public SQLXML getSQLXML(String parameterName) throws SQLException {
		return this.statement.getSQLXML(parameterName);
	}

	@Override
	public String getNString(int parameterIndex) throws SQLException {
		return this.statement.getNString(parameterIndex);
	}

	@Override
	public String getNString(String parameterName) throws SQLException {
		return this.statement.getNString(parameterName);
	}

	@Override
	public Reader getNCharacterStream(int parameterIndex) throws SQLException {
		return this.statement.getNCharacterStream(parameterIndex);
	}

	@Override
	public Reader getNCharacterStream(String parameterName) throws SQLException {
		return this.statement.getNCharacterStream(parameterName);
	}

	@Override
	public Reader getCharacterStream(int parameterIndex) throws SQLException {
		return this.statement.getCharacterStream(parameterIndex);
	}

	@Override
	public Reader getCharacterStream(String parameterName) throws SQLException {
		return this.statement.getCharacterStream(parameterName);
	}

	@Override
	public void setBlob(String parameterName, Blob x) throws SQLException {
		super.parameter(parameterName, x);
		this.statement.setBlob(parameterName, x);
	}

	@Override
	public void setClob(String parameterName, Clob x) throws SQLException {
		super.parameter(parameterName, x);
		this.statement.setClob(parameterName, x);
	}

	@Override
	public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
		super.parameter(parameterName, x);
		this.statement.setAsciiStream(parameterName, x, length);
	}

	@Override
	public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
		super.parameter(parameterName, x);
		this.statement.setBinaryStream(parameterName, x, length);
	}

	@Override
	public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
		super.parameter(parameterName, reader);
		this.statement.setCharacterStream(parameterName, reader, length);
	}

	@Override
	public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
		super.parameter(parameterName, x);
		this.statement.setAsciiStream(parameterName, x);
	}

	@Override
	public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
		super.parameter(parameterName, x);
		this.statement.setBinaryStream(parameterName, x);
	}

	@Override
	public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
		super.parameter(parameterName, reader);
		this.statement.setCharacterStream(parameterName, reader);
	}

	@Override
	public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
		super.parameter(parameterName, value);
		this.statement.setNCharacterStream(parameterName, value);
	}

	@Override
	public void setClob(String parameterName, Reader reader) throws SQLException {
		super.parameter(parameterName, reader);
		this.statement.setClob(parameterName, reader);
	}

	@Override
	public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
		super.parameter(parameterName, inputStream);
		this.statement.setBlob(parameterName, inputStream);
	}

	@Override
	public void setNClob(String parameterName, Reader reader) throws SQLException {
		super.parameter(parameterName, reader);
		this.statement.setNClob(parameterName, reader);
	}

	@Override
	public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
		return this.statement.getObject(parameterIndex, type);
	}

	@Override
	public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
		return this.statement.getObject(parameterName, type);
	}
}
