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

package org.nervousync.brain.defines;

import jakarta.annotation.Nonnull;
import org.nervousync.brain.commons.BrainCommons;
import org.nervousync.brain.exceptions.defines.TableDefineException;
import org.nervousync.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <h2 class="en-US">Data table define</h2>
 * <h2 class="zh-CN">数据表定义</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Feb 18, 2019 10:15:08 $
 */
public final class TableDefine {

	/**
	 * <span class="en-US">Data table name</span>
	 * <span class="zh-CN">数据表名称</span>
	 */
	private final String tableName;
	/**
	 * <span class="en-US">Data table name sharding template</span>
	 * <span class="zh-CN">数据表分片模板</span>
	 */
	private final String shardingTemplate;
	/**
	 * <span class="en-US">Data column define list</span>
	 * <span class="zh-CN">数据列定义列表</span>
	 */
	private final List<ColumnDefine> columnDefines;
	/**
	 * <span class="en-US">Index define list</span>
	 * <span class="zh-CN">索引定义列表</span>
	 */
	private final List<IndexDefine> indexDefines;

	/**
	 * <h3 class="en-US">Constructor method for data table define</h3>
	 * <h3 class="zh-CN">数据表定义的构造方法</h3>
	 *
	 * @param tableName        <span class="en-US">Data table name</span>
	 *                         <span class="zh-CN">数据表名称</span>
	 * @param shardingTemplate <span class="en-US">Data table name sharding template</span>
	 *                         <span class="zh-CN">数据表分片模板</span>
	 * @param columnDefines    <span class="en-US">Data column define list</span>
	 *                         <span class="zh-CN">数据列定义列表</span>
	 * @param indexDefines     <span class="en-US">Index define list</span>
	 *                         <span class="zh-CN">索引定义列表</span>
	 */
	public TableDefine(final String tableName, final String shardingTemplate, final List<ColumnDefine> columnDefines,
	                   final List<IndexDefine> indexDefines) {
		this.tableName = tableName;
		this.shardingTemplate = shardingTemplate;
		this.columnDefines = columnDefines;
		this.indexDefines = indexDefines;
	}

	/**
	 * <h3 class="en-US">Getter method for data table name</h3>
	 * <h3 class="zh-CN">数据表名称的Getter方法</h3>
	 *
	 * @return <span class="en-US">Data table name</span>
	 * <span class="zh-CN">数据表名称</span>
	 */
	public String getTableName() {
		return this.tableName;
	}

	/**
	 * <h3 class="en-US">Getter method for data table name sharding template</h3>
	 * <h3 class="zh-CN">数据表分片模板的Getter方法</h3>
	 *
	 * @return <span class="en-US">Data table name sharding template</span>
	 * <span class="zh-CN">数据表分片模板</span>
	 */
	public String getShardingTemplate() {
		return this.shardingTemplate;
	}

	/**
	 * <h3 class="en-US">Getter method for data column define list</h3>
	 * <h3 class="zh-CN">数据列定义列表的Getter方法</h3>
	 *
	 * @return <span class="en-US">Data column define list</span>
	 * <span class="zh-CN">数据列定义列表</span>
	 */
	public List<ColumnDefine> getColumnDefines() {
		return this.columnDefines;
	}

	/**
	 * <h3 class="en-US">Getter method for index define list</h3>
	 * <h3 class="zh-CN">索引定义列表的Getter方法</h3>
	 *
	 * @return <span class="en-US">Index define list</span>
	 * <span class="zh-CN">索引定义列表</span>
	 */
	public List<IndexDefine> getIndexDefines() {
		return this.indexDefines;
	}

	/**
	 * <h3 class="en-US">Get data generation definition</h3>
	 * <h3 class="zh-CN">获取数据生成定义</h3>
	 *
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Define information</span>
	 * <span class="zh-CN">定义信息</span>
	 */
	public GeneratorDefine generatorDefine(final String columnName) {
		if (StringUtils.isEmpty(columnName)) {
			return null;
		}
		return this.columnDefines
				.stream()
				.filter(columnDefine -> columnDefine.getColumnName().equalsIgnoreCase(columnName))
				.findFirst()
				.map(ColumnDefine::getGeneratorDefine)
				.orElse(null);
	}

	/**
	 * <h3 class="en-US">Find data column definition information based on the given data column name</h3>
	 * <h3 class="zh-CN">根据给定的数据列名查找数据列定义信息</h3>
	 *
	 * @param columnName <span class="en-US">Data column name</span>
	 *                   <span class="zh-CN">数据列名</span>
	 * @return <span class="en-US">Data column define</span>
	 * <span class="zh-CN">数据列定义</span>
	 */
	public ColumnDefine column(final String columnName) {
		return this.columnDefines
				.stream()
				.filter(columnDefine -> columnDefine.getColumnName().equalsIgnoreCase(columnName))
				.findFirst()
				.orElse(null);
	}

	/**
	 * <h3 class="en-US">Verify whether the given data column definition information is consistent with the current definition information</h3>
	 * <h3 class="zh-CN">验证给定的数据列定义信息与当前的定义信息是否一致</h3>
	 *
	 * @param existColumns <span class="en-US">Data column define information list</span>
	 *                     <span class="zh-CN">数据列定义信息列表</span>
	 * @throws TableDefineException <span class="en-US">Throws an exception if validation fails</span>
	 *                              <span class="zh-CN">如果验证失败则抛出异常</span>
	 */
	public void validate(@Nonnull final List<ColumnDefine> existColumns) throws TableDefineException {
		if (existColumns.isEmpty()) {
			throw new TableDefineException(0x00DB00000003L, this.tableName);
		}
		List<ColumnDefine> checkedColumns = new ArrayList<>();
		StringBuilder notFoundColumns = new StringBuilder();
		StringBuilder modifiedColumns = new StringBuilder();
		StringBuilder newColumns = new StringBuilder();
		for (ColumnDefine existColumn : existColumns) {
			String columnName = existColumn.getColumnName();
			ColumnDefine columnDefine = this.column(columnName);
			if (columnDefine == null) {
				notFoundColumns.append(BrainCommons.DEFAULT_SPLIT_CHARACTER).append(columnName);
			} else if (existColumn.modified(columnDefine)) {
				modifiedColumns.append(BrainCommons.DEFAULT_SPLIT_CHARACTER).append(columnName);
			}
			checkedColumns.add(columnDefine);
		}

		this.columnDefines.stream()
				.filter(columnDefine -> !checkedColumns.contains(columnDefine))
				.forEach(columnDefine ->
						newColumns.append(BrainCommons.DEFAULT_SPLIT_CHARACTER).append(columnDefine.getColumnName()));
		if (notFoundColumns.length() > 0 || modifiedColumns.length() > 0 || newColumns.length() > 0) {
			int start = BrainCommons.DEFAULT_SPLIT_CHARACTER.length();
			throw new TableDefineException(0x00DB00000004L,
					newColumns.length() > 0 ? newColumns.substring(start) : newColumns.toString(),
					modifiedColumns.length() > 0 ? modifiedColumns.substring(start) : modifiedColumns.toString(),
					notFoundColumns.length() > 0 ? notFoundColumns.substring(start) : notFoundColumns.toString());
		}
	}
}
