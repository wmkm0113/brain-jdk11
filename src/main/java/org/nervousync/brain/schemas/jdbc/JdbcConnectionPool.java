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

import jakarta.annotation.Nonnull;
import org.nervousync.brain.configs.server.ServerInfo;
import org.nervousync.brain.exceptions.sql.MultilingualSQLException;
import org.nervousync.commons.Globals;
import org.nervousync.utils.DateTimeUtils;
import org.nervousync.utils.LoggerUtils;
import org.nervousync.utils.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <h2 class="en-US">JDBC database connection pool</h2>
 * <h2 class="zh-CN">JDBC数据源创建连接池</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Nov 12, 2019 19:17:06 $
 */
public final class JdbcConnectionPool {

	/**
	 * <span class="en-US">Logger instance</span>
	 * <span class="zh-CN">日志实例</span>
	 */
	private static final LoggerUtils.Logger LOGGER = LoggerUtils.getLogger(JdbcConnectionPool.class);

	/**
	 * <span class="en-US">JDBC dialect instance object</span>
	 * <span class="zh-CN">JDBC方言实例对象</span>
	 */
	private final JdbcSchema jdbcSchema;
	/**
	 * <span class="en-US">Connection pool identify code</span>
	 * <span class="zh-CN">连接池识别代码</span>
	 */
	private final int identifyCode;
	/**
	 * <span class="en-US">Database JDBC connection string</span>
	 * <span class="zh-CN">数据库JDBC连接字符串</span>
	 */
	private final String jdbcUrl;
	/**
	 * <span class="en-US">Database connection queue</span>
	 * <span class="zh-CN">数据库连接队列</span>
	 */
	private final Queue<JdbcConnection> connectionQueue;
	/**
	 * <span class="en-US">Using database connection list</span>
	 * <span class="zh-CN">使用中的数据库连接列表</span>
	 */
	private final List<JdbcConnection> activeConnections;
	/**
	 * <span class="en-US">Waiting to get count of connections</span>
	 * <span class="zh-CN">等待获取连接的计数</span>
	 */
	private final AtomicInteger waitCount;
	/**
	 * <span class="en-US">Using connection pool</span>
	 * <span class="zh-CN">使用连接池</span>
	 */
	private boolean pooled;
	/**
	 * <span class="en-US">Create connection task execution status</span>
	 * <span class="zh-CN">创建连接任务执行状态</span>
	 */
	private boolean createRunning = Boolean.FALSE;


	/**
	 * <h2 class="en-US">Private constructor method for database connection pool</h2>
	 * <h2 class="zh-CN">数据库连接池的私有构造方法</h2>
	 *
	 * @param jdbcSchema  <span class="en-US">JDBC data source instance object</span>
	 *                    <span class="zh-CN">JDBC数据源实例对象</span>
	 * @param pooled      <span class="en-US">Using connection pool</span>
	 *                    <span class="zh-CN">使用连接池</span>
	 * @param serverInfo  <span class="en-US">Server information</span>
	 *                    <span class="zh-CN">服务器信息</span>
	 * @param shardingKey <span class="en-US">Database sharding value</span>
	 *                    <span class="zh-CN">数据库分片值</span>
	 */
	JdbcConnectionPool(final JdbcSchema jdbcSchema, final boolean pooled,
	                   final ServerInfo serverInfo, final String shardingKey)
			throws SQLException {
		this.jdbcSchema = jdbcSchema;
		this.identifyCode = this.jdbcSchema.identifyCode(serverInfo, shardingKey);
		this.jdbcUrl = this.jdbcSchema.shardingUrl(serverInfo, shardingKey);
		this.pooled = pooled;
		this.connectionQueue = new LinkedList<>();
		this.activeConnections = new ArrayList<>();
		this.waitCount = new AtomicInteger(Globals.INITIALIZE_INT_VALUE);
		this.createConnections();
	}

	/**
	 * <h3 class="en-US">Getter method for connection pool identify code</h3>
	 * <h3 class="zh-CN">连接池识别代码的Getter方法</h3>
	 *
	 * @return <span class="en-US">Connection pool identify code</span>
	 * <span class="zh-CN">连接池识别代码</span>
	 */
	public int getIdentifyCode() {
		return this.identifyCode;
	}

	/**
	 * <h3 class="en-US">Establish a real database connection</h3>
	 * <h3 class="zh-CN">建立真实的数据库连接</h3>
	 *
	 * @return <span class="en-US">Database connection instance object</span>
	 * <span class="zh-CN">数据库连接实例对象</span>
	 */
	JdbcConnection createConnection() throws SQLException {
		Properties jdbcProperties = this.jdbcSchema.properties();

		boolean process = Boolean.TRUE;
		int retryCount = Globals.INITIALIZE_INT_VALUE;
		Connection connection = null;
		while (process) {
			try {
				connection = DriverManager.getConnection(jdbcUrl, jdbcProperties);
			} catch (SQLException e) {
				LOGGER.error("Create_Connection_Error");
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Stack_Message_Error", e);
				}
			}
			if (connection == null) {
				if (retryCount < this.jdbcSchema.retryCount) {
					try {
						retryCount++;
						Thread.sleep(this.jdbcSchema.retryPeriod);
					} catch (InterruptedException e) {
						LOGGER.error("Thread_Sleep_Error");
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Stack_Message_Error", e);
						}
						process = Boolean.FALSE;
					}
				} else {
					process = Boolean.FALSE;
				}
			} else {
				process = Boolean.FALSE;
			}
		}

		if (connection == null) {
			throw new MultilingualSQLException(0x00DB00000023L);
		}
		return new JdbcConnection(this, connection,
				this.jdbcSchema.getLowQueryTimeout(), this.jdbcSchema.cachedLimitSize);
	}

	void configPooled(final boolean pooled) {
		boolean original = this.pooled;
		this.pooled = pooled;
		if (original) {
			if (!pooled) {
				this.closePool();
			}
		}
		if (this.pooled) {
			this.createConnections();
		}
	}

	/**
	 * <h3 class="en-US">Destroy the given database connection</h3>
	 * <h3 class="zh-CN">销毁给定的数据库连接</h3>
	 *
	 * @param connection <span class="en-US">Database connection instance object</span>
	 *                   <span class="zh-CN">数据库连接实例对象</span>
	 */
	void destroyConnection(final JdbcConnection connection) {
		try {
			if (connection == null || connection.isClosed()) {
				return;
			}
			connection.destroy();
		} catch (SQLException e) {
			LOGGER.error("Close_Connection_Error");
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Stack_Message_Error", e);
			}
		}
	}

	/**
	 * <h3 class="en-US">Checks whether the given connection has expired</h3>
	 * <h3 class="zh-CN">检查给定的连接是否已经失效</h3>
	 *
	 * @param connection <span class="en-US">Obtained connection</span>
	 *                   <span class="zh-CN">获得的连接</span>
	 * @return <span class="en-US">Check result</span>
	 * <span class="zh-CN">检查结果</span>
	 */
	boolean invalidConnection(@Nonnull final JdbcConnection connection) {
		boolean validate;
		Statement statement = null;
		try {
			String validateQuery = this.jdbcSchema.validationQuery();
			if (StringUtils.isEmpty(validateQuery)) {
				validate = connection.isValid(this.jdbcSchema.getValidateTimeout());
			} else {
				statement = connection.createStatement();
				statement.setQueryTimeout(this.jdbcSchema.getValidateTimeout());
				validate = statement.execute(validateQuery);
			}
		} catch (SQLException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Check_Connection_Error", e);
			}
			validate = Boolean.FALSE;
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException ignore) {
				}
			}
		}

		if (!validate) {
			this.destroyConnection(connection);
		}
		return !validate;
	}

	/**
	 * <h3 class="en-US">Number of connections in the connection queue</h3>
	 * <h3 class="zh-CN">连接队列中的连接数</h3>
	 *
	 * @return <span class="en-US">Number of connections</span>
	 * <span class="zh-CN">连接数</span>
	 */
	int poolCount() {
		return this.connectionQueue.size();
	}

	/**
	 * <h3 class="en-US">Number of the using connections</h3>
	 * <h3 class="zh-CN">使用中的连接数</h3>
	 *
	 * @return <span class="en-US">Number of connections</span>
	 * <span class="zh-CN">连接数</span>
	 */
	int activeCount() {
		return this.connectionQueue.size();
	}

	/**
	 * <h3 class="en-US">Number of clients waiting to get a connection</h3>
	 * <h3 class="zh-CN">等待获得连接的客户端数量</h3>
	 *
	 * @return <span class="en-US">Number of clients waiting</span>
	 * <span class="zh-CN">等待的客户端数量</span>
	 */
	int waitCount() {
		return this.waitCount.get();
	}

	/**
	 * <h3 class="en-US">Obtain a connection without transactional configure</h3>
	 * <h3 class="zh-CN">获得无事务连接</h3>
	 *
	 * @throws SQLException <span class="en-US">An error occurred while obtaining the connection</span>
	 *                      <span class="zh-CN">获得连接过程中出错</span>
	 */
	JdbcConnection obtainConnection() throws SQLException {
		return this.obtainConnection(Connection.TRANSACTION_NONE);
	}

	/**
	 * <h3 class="en-US">Obtain a connection</h3>
	 * <h3 class="zh-CN">获得连接</h3>
	 *
	 * @return <span class="en-US">Obtained connection</span>
	 * <span class="zh-CN">获得的连接</span>
	 * @throws SQLException <span class="en-US">An error occurred while obtaining the connection</span>
	 *                      <span class="zh-CN">获得连接过程中出错</span>
	 */
	JdbcConnection obtainConnection(final int isolation) throws SQLException {

		JdbcConnection connection = null;
		if (!this.pooled) {
			connection = this.createConnection();
		} else {
			long beginTime = DateTimeUtils.currentUTCTimeMillis();
			long timeOutTime = this.jdbcSchema.getConnectTimeout() * 1000L;

			boolean waitCount = Boolean.FALSE;

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Connection_Wait_Count", this.waitCount.get());
			}
			while (connection == null) {
				connection = this.connectionQueue.poll();
				if (connection == null) {
					try {
						connection = this.createConnection();
					} catch (SQLException e) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Create_Connection_Error", e);
						}
					}
				}

				if (connection != null && this.jdbcSchema.testOnBorrow && this.invalidConnection(connection)) {
					this.destroyConnection(connection);
					connection = null;
				}

				if (connection == null) {
					if (!waitCount) {
						this.waitCount.incrementAndGet();
						waitCount = Boolean.TRUE;
					}

					if (timeOutTime < (DateTimeUtils.currentUTCTimeMillis() - beginTime)) {
						break;
					}
				}
			}

			if (waitCount) {
				this.waitCount.decrementAndGet();
			}

			if (LOGGER.isDebugEnabled()) {
				if (waitCount) {
					LOGGER.debug("Connection_From_Create");
				} else {
					LOGGER.debug("Connection_From_Pool");
				}
				LOGGER.debug("Connection_Used_Time",
						DateTimeUtils.currentUTCTimeMillis() - beginTime);
				LOGGER.debug("Pool_Connection_Debug", this.activeConnections.size(),
						this.connectionQueue.size());
			}
		}

		if (connection == null) {
			throw new MultilingualSQLException(0x00DB00000024L);
		}

		if (isolation != Connection.TRANSACTION_NONE) {
			connection.setAutoCommit(Boolean.FALSE);
			connection.setTransactionIsolation(isolation);
		}

		this.activeConnections.add(connection);
		connection.setCachedLimitSize(this.jdbcSchema.cachedLimitSize);
		return connection;
	}

	void close() {
		//  Close all activated connection
		Iterator<JdbcConnection> iterator = this.activeConnections.iterator();
		while (iterator.hasNext()) {
			JdbcConnection connection = iterator.next();
			this.destroyConnection(connection);
			iterator.remove();
		}
		this.closePool();
	}

	private void closePool() {
		JdbcConnection connection;
		while ((connection = this.connectionQueue.poll()) != null) {
			this.destroyConnection(connection);
		}
		this.connectionQueue.clear();
	}

	/**
	 * <h3 class="en-US">Close the given connection object</h3>
	 * <h3 class="zh-CN">关闭给定的连接对象</h3>
	 *
	 * @param connection <span class="en-US">Obtained connection</span>
	 *                   <span class="zh-CN">获得的连接</span>
	 * @throws SQLException <span class="en-US">An error occurred while close the connection</span>
	 *                      <span class="zh-CN">关闭连接过程中出错</span>
	 */
	void closeConnection(final JdbcConnection connection) throws SQLException {
		if (connection == null) {
			return;
		}

		this.activeConnections.remove(connection);

		if (!this.pooled || connection.getTransactionIsolation() != Connection.TRANSACTION_NONE) {
			this.destroyConnection(connection);
			return;
		}

		if (connection.isClosed()) {
			return;
		}

		if (this.jdbcSchema.testOnReturn && this.invalidConnection(connection)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Invalid_Destroy_Connection");
			}
			this.destroyConnection(connection);
			return;
		}

		this.addConnection(connection);
	}

	/**
	 * <h3 class="en-US">Check current connections count is greater or equal the maximum connections</h3>
	 * <h3 class="zh-CN">检查当前连接数是否超过最大连接数</h3>
	 *
	 * @return <span class="en-US">Check result</span>
	 * <span class="zh-CN">检查结果</span>
	 */
	private boolean limitConnections() {
		return this.jdbcSchema.maxConnections < (this.activeConnections.size() + this.connectionQueue.size());
	}

	/**
	 * <h3 class="en-US">Add connection to connection pool</h3>
	 * <h3 class="zh-CN">添加连接到连接池</h3>
	 *
	 * @param connection <span class="en-US">Obtained connection</span>
	 *                   <span class="zh-CN">获得的连接</span>
	 */
	private void addConnection(@Nonnull final JdbcConnection connection) {
		if (!this.pooled) {
			this.destroyConnection(connection);
			return;
		}
		boolean destroy = Boolean.TRUE;
		synchronized (this.connectionQueue) {
			if (this.needConnections()) {
				destroy = !this.connectionQueue.offer(connection);
			}
		}
		if (destroy) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Pool_Full_Destroy_Connection");
			}
			this.destroyConnection(connection);
		}
	}

	/**
	 * <h3 class="en-US">Check current connections count in connection pool is less than the pool maximum connections</h3>
	 * <h3 class="zh-CN">检查当前连接池中的连接数是否小于连接池最大连接数</h3>
	 *
	 * @return <span class="en-US">Check result</span>
	 * <span class="zh-CN">检查结果</span>
	 */
	private boolean needConnections() {
		return this.pooled ? (this.connectionQueue.size() < this.jdbcSchema.minConnections) : Boolean.FALSE;
	}

	/**
	 * <h3 class="en-US">Check whether the number of connections in the database connection pool meets the configuration requirements</h3>
	 * <h3 class="zh-CN">检查数据库连接池中的连接数是否满足配置需求</h3>
	 */
	void createConnections() {
		if (this.createRunning || !this.pooled) {
			return;
		}

		this.createRunning = Boolean.TRUE;
		if (this.limitConnections()) {
			LOGGER.debug("Create_Connection_Full");
			return;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Create_Connection_Begin_Debug");
		}
		int poolSize = this.connectionQueue.size(), count = Globals.INITIALIZE_INT_VALUE;
		while (count < poolSize) {
			JdbcConnection connection = this.connectionQueue.poll();
			if (connection == null) {
				break;
			}
			boolean destroy = this.invalidConnection(connection);
			if (!destroy) {
				destroy = !this.connectionQueue.offer(connection);
			}

			if (destroy) {
				this.destroyConnection(connection);
			}
			count++;
		}
		synchronized (this.connectionQueue) {
			while (this.needConnections()) {
				try {
					this.addConnection(this.createConnection());
				} catch (SQLException e) {
					LOGGER.error("Create_Connection_Error");
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("", e);
					}
					break;
				}
			}
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Create_Connection_End_Debug");
		}
		this.createRunning = Boolean.FALSE;
	}
}
