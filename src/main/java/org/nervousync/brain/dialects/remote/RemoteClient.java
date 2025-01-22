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

package org.nervousync.brain.dialects.remote;

import jakarta.annotation.Nonnull;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.persistence.LockModeType;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.xml.ws.WebServiceClient;
import org.nervousync.brain.enumerations.ddl.DropOption;

import java.sql.SQLException;

/**
 * <h2 class="en-US">Data source operator interface</h2>
 * <h2 class="zh-CN">数据源操作器接口</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Nov 12, 2020 12:16:28 $
 */
@WebServiceClient
public interface RemoteClient {


	/**
	 * <h3 class="en-US">Begin transactional</h3>
	 * <h3 class="zh-CN">开启事务</h3>
	 *
	 * @param txCode    <span class="en-US">Transactional identify code</span>
	 *                  <span class="zh-CN">事务识别代码</span>
	 * @param isolation <span class="en-US">Isolation level</span>
	 *                  <span class="zh-CN">事务等级</span>
	 * @param timeout   <span class="en-US">Transactional timeout</span>
	 *                  <span class="zh-CN">事务超时时间</span>
	 * @return <span class="en-US">Response data</span>
	 * <span class="zh-CN">响应数据</span>
	 */
	@GET
	@Path("/transactional/begin/{txCode}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	@WebMethod
	String beginTransactional(@WebParam @PathParam("txCode") final long txCode,
	                          @WebParam @QueryParam("isolation") final int isolation,
	                          @WebParam @QueryParam("timeout") final int timeout);

	/**
	 * <h3 class="en-US">Rollback transactional</h3>
	 * <h3 class="zh-CN">回滚事务</h3>
	 *
	 * @param txCode <span class="en-US">Transactional identify code</span>
	 *               <span class="zh-CN">事务识别代码</span>
	 * @return <span class="en-US">Response data</span>
	 * <span class="zh-CN">响应数据</span>
	 */
	@GET
	@Path("/transactional/rollback/{txCode}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	@WebMethod
	String rollback(@WebParam @PathParam("txCode") final long txCode);

	/**
	 * <h3 class="en-US">Submit transactional execute</h3>
	 * <h3 class="zh-CN">提交事务执行</h3>
	 *
	 * @param txCode <span class="en-US">Transactional identify code</span>
	 *               <span class="zh-CN">事务识别代码</span>
	 * @return <span class="en-US">Response data</span>
	 * <span class="zh-CN">响应数据</span>
	 */
	@GET
	@Path("/transactional/commit/{txCode}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	@WebMethod
	String commit(@WebParam @PathParam("txCode") final long txCode);

	/**
	 * <h3 class="en-US">Truncate all data table</h3>
	 * <h3 class="zh-CN">清空所有数据表</h3>
	 *
	 * @return <span class="en-US">Response data</span>
	 * <span class="zh-CN">响应数据</span>
	 */
	@DELETE
	@Path("/truncate/tables")
	@Produces(MediaType.APPLICATION_JSON)
	@WebMethod
	String truncateTables();

	/**
	 * <h3 class="en-US">Truncate data table</h3>
	 * <h3 class="zh-CN">清空数据表</h3>
	 *
	 * @param tableName <span class="en-US">Data table name</span>
	 *                  <span class="zh-CN">数据表名</span>
	 * @return <span class="en-US">Response data</span>
	 * <span class="zh-CN">响应数据</span>
	 */
	@DELETE
	@Path("/truncate/table/{tableName}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	@WebMethod
	String truncateTable(@Nonnull @WebParam @PathParam("tableName") final String tableName);

	/**
	 * <h3 class="en-US">Drop all data table</h3>
	 * <h3 class="zh-CN">删除所有数据表</h3>
	 *
	 * @param dropOption <span class="en-US">Cascading delete options</span>
	 *                   <span class="zh-CN">级联删除选项</span>
	 * @return <span class="en-US">Response data</span>
	 * <span class="zh-CN">响应数据</span>
	 */
	@DELETE
	@Path("/drop/tables")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	@WebMethod
	String dropTables(final @QueryParam("option") DropOption dropOption);

	/**
	 * <h3 class="en-US">Drop data table</h3>
	 * <h3 class="zh-CN">删除数据表</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param indexNames <span class="en-US">Data index name list</span>
	 *                   <span class="zh-CN">数据索引名列表</span>
	 * @param dropOption <span class="en-US">Cascading delete options</span>
	 *                   <span class="zh-CN">级联删除选项</span>
	 * @return <span class="en-US">Response data</span>
	 * <span class="zh-CN">响应数据</span>
	 */
	@DELETE
	@Path("/drop/table/{tableName}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	@WebMethod
	String dropTable(@Nonnull @WebParam @PathParam("tableName") final String tableName,
	                 @Nonnull @WebParam @QueryParam("indexNames") final String indexNames,
	                 @Nonnull @WebParam @QueryParam("dropOption") final DropOption dropOption);

	/**
	 * <h3 class="en-US">Execute lock record command</h3>
	 * <h3 class="zh-CN">执行数据锁定命令</h3>
	 *
	 * @param tableName     <span class="en-US">Data table name</span>
	 *                      <span class="zh-CN">数据表名</span>
	 * @param filterContent <span class="en-US">Retrieve filter information</span>
	 *                      <span class="zh-CN">查询条件信息</span>
	 * @return <span class="en-US">Process result</span>
	 * <span class="zh-CN">操作结果</span>
	 */
	@POST
	@Path("/table/{tableName}/lock")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@WebMethod
	Boolean lockRecord(@Nonnull @WebParam @PathParam("tableName") final String tableName,
	                   @Nonnull @WebParam @QueryParam("filter") final String filterContent);

	/**
	 * <h3 class="en-US">Execute insert record command</h3>
	 * <h3 class="zh-CN">执行插入数据命令</h3>
	 *
	 * @param tableName   <span class="en-US">Data table name</span>
	 *                    <span class="zh-CN">数据表名</span>
	 * @param dataContent <span class="en-US">Insert data content</span>
	 *                    <span class="zh-CN">写入数据信息</span>
	 * @return <span class="en-US">Primary key values generated by database</span>
	 * <span class="zh-CN">数据库生成的主键值</span>
	 */
	@POST
	@Path("/table/{tableName}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@WebMethod
	String insert(@Nonnull @WebParam @PathParam("tableName") final String tableName,
	              @Nonnull @WebParam @QueryParam("dataContent") final String dataContent);

	/**
	 * <h3 class="en-US">Execute retrieve record command</h3>
	 * <h3 class="zh-CN">执行数据唯一检索命令</h3>
	 *
	 * @param tableName     <span class="en-US">Data table name</span>
	 *                      <span class="zh-CN">数据表名</span>
	 * @param columns       <span class="en-US">Query column names</span>
	 *                      <span class="zh-CN">查询数据列名</span>
	 * @param filterContent <span class="en-US">Retrieve filter information</span>
	 *                      <span class="zh-CN">查询条件信息</span>
	 * @param forUpdate     <span class="en-US">Retrieve result using for update record</span>
	 *                      <span class="zh-CN">检索结果用于更新记录</span>
	 * @param lockOption    <span class="en-US">Query record lock option</span>
	 *                      <span class="zh-CN">查询记录锁定选项</span>
	 * @return <span class="en-US">Data content of retrieved records</span>
	 * <span class="zh-CN">检索到记录的数据</span>
	 */
	@GET
	@Path("/table/{tableName}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@WebMethod
	String retrieve(@Nonnull @WebParam @PathParam("tableName") final String tableName,
	                @WebParam @QueryParam("columns") final String columns,
	                @Nonnull @WebParam @QueryParam("filter") final String filterContent,
	                @WebParam @QueryParam("forUpdate") final boolean forUpdate,
	                @WebParam @QueryParam("lockOption") final LockModeType lockOption);

	/**
	 * <h3 class="en-US">Execute update record command</h3>
	 * <h3 class="zh-CN">执行更新记录命令</h3>
	 *
	 * @param tableName     <span class="en-US">Data table name</span>
	 *                      <span class="zh-CN">数据表名</span>
	 * @param dataContent   <span class="en-US">Insert data content</span>
	 *                      <span class="zh-CN">写入数据信息</span>
	 * @param filterContent <span class="en-US">Retrieve filter information</span>
	 *                      <span class="zh-CN">查询条件信息</span>
	 * @return <span class="en-US">Updated records count</span>
	 * <span class="zh-CN">更新记录条数</span>
	 */
	@PUT
	@Path("/table/{tableName}")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	@WebMethod
	Integer update(@Nonnull @WebParam @PathParam("tableName") final String tableName,
	               @Nonnull @WebParam @QueryParam("dataContent") final String dataContent,
	               @Nonnull @WebParam @QueryParam("filter") final String filterContent);

	/**
	 * <h3 class="en-US">Execute delete record command</h3>
	 * <h3 class="zh-CN">执行删除记录命令</h3>
	 *
	 * @param tableName     <span class="en-US">Data table name</span>
	 *                      <span class="zh-CN">数据表名</span>
	 * @param filterContent <span class="en-US">Retrieve filter information</span>
	 *                      <span class="zh-CN">查询条件信息</span>
	 * @return <span class="en-US">Deleted records count</span>
	 * <span class="zh-CN">删除记录条数</span>
	 */
	@DELETE
	@Path("/table/{tableName}")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	@WebMethod
	Integer delete(@Nonnull @WebParam @PathParam("tableName") final String tableName,
	               @Nonnull @WebParam @QueryParam("filter") final String filterContent);

	/**
	 * <h3 class="en-US">Execute query record command</h3>
	 * <h3 class="zh-CN">执行数据检索命令</h3>
	 *
	 * @param queryInfo <span class="en-US">Query record information</span>
	 *                  <span class="zh-CN">数据检索信息</span>
	 * @return <span class="en-US">List of data mapping tables for retrieved records</span>
	 * <span class="zh-CN">检索到记录的数据映射表列表</span>
	 */
	@POST
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@WebMethod
	String query(@Nonnull @WebParam @QueryParam("query") final String queryInfo);

	/**
	 * <h3 class="en-US">Execute query commands for data updates</h3>
	 * <h3 class="zh-CN">执行用于数据更新的查询命令</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param columns    <span class="en-US">Query column names</span>
	 *                   <span class="zh-CN">查询数据列名</span>
	 * @param conditions <span class="en-US">Query condition instance list</span>
	 *                   <span class="zh-CN">查询条件实例对象列表</span>
	 * @param lockOption <span class="en-US">Query record lock option</span>
	 *                   <span class="zh-CN">查询记录锁定选项</span>
	 * @return <span class="en-US">List of data mapping tables for retrieved records</span>
	 * <span class="zh-CN">检索到记录的数据映射表列表</span>
	 */
	@GET
	@Path("/search/{tableName}/forUpdate")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@WebMethod
	String queryForUpdate(@Nonnull @WebParam @PathParam("tableName") final String tableName,
	                      @WebParam @QueryParam("columns") final String columns,
	                      @Nonnull @WebParam @QueryParam("conditions") final String conditions,
	                      @Nonnull @WebParam @QueryParam("lock") final LockModeType lockOption);

	/**
	 * <h3 class="en-US">Query total record count</h3>
	 * <h3 class="zh-CN">查询总记录数</h3>
	 *
	 * @param tableName  <span class="en-US">Data table name</span>
	 *                   <span class="zh-CN">数据表名</span>
	 * @param conditions <span class="en-US">Query condition instance list</span>
	 *                   <span class="zh-CN">查询条件实例对象列表</span>
	 * @return <span class="en-US">Total record count</span>
	 * <span class="zh-CN">总记录条数</span>
	 * @throws SQLException <span class="en-US">An error occurred during execution</span>
	 *                      <span class="zh-CN">执行过程中出错</span>
	 */
	@GET
	@Path("/search/{tableName}/count")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@WebMethod
	Long queryTotal(@Nonnull @WebParam @PathParam("tableName") final String tableName,
	                @Nonnull @WebParam @QueryParam("conditions") final String conditions) throws SQLException;

}
