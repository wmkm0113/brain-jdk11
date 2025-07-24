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
import org.nervousync.brain.enumerations.ddl.GenerationType;

import java.io.Serializable;
import java.util.*;

/**
 * <h2 class="en-US">Column data generator configure information</h2>
 * <h2 class="zh-CN">列数据生成器配置信息</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Nov 4, 2020 15:49:52 $
 */
public final class InitOption implements Serializable {

	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = 1000740115064213568L;

	/**
	 * <span class="en-US">Generation type</span>
	 * <span class="zh-CN">生成器类型</span>
	 */
	private GenerationType generationType;
	/**
	 * <span class="en-US">Generator name</span>
	 * <span class="zh-CN">生成器名称</span>
	 */
	private String generatorName;
	/**
	 * <span class="en-US">Column histories names list</span>
	 * <span class="zh-CN">历史列名列表</span>
	 */
	private List<String> historiesNames = new ArrayList<>();

	/**
	 * <h3 class="en-US">Constructor method for column data generator configure information</h3>
	 * <h3 class="zh-CN">列数据生成器配置信息的构造方法</h3>
	 */
	public InitOption() {
	}

	/**
	 * <h3 class="en-US">Getter method for generation type</h3>
	 * <h3 class="zh-CN">生成器类型的Getter方法</h3>
	 *
	 * @return <span class="en-US">Generation type</span>
	 * <span class="zh-CN">生成器类型</span>
	 */
	public GenerationType getGenerationType() {
		return generationType;
	}

	/**
	 * <h3 class="en-US">Setter method for generation type</h3>
	 * <h3 class="zh-CN">生成器类型的Setter方法</h3>
	 *
	 * @param generationType <span class="en-US">Generation type</span>
	 *                       <span class="zh-CN">生成器类型</span>
	 */
	public void setGenerationType(final GenerationType generationType) {
		this.generationType = generationType;
	}

	/**
	 * <h3 class="en-US">Getter method for generator name</h3>
	 * <h3 class="zh-CN">生成器名称的Getter方法</h3>
	 *
	 * @return <span class="en-US">Generator name</span>
	 * <span class="zh-CN">生成器名称</span>
	 */
	public String getGeneratorName() {
		return generatorName;
	}

	/**
	 * <h3 class="en-US">Setter method for generator name</h3>
	 * <h3 class="zh-CN">生成器名称的Setter方法</h3>
	 *
	 * @param generatorName <span class="en-US">Generator name</span>
	 *                      <span class="zh-CN">生成器名称</span>
	 */
	public void setGeneratorName(final String generatorName) {
		this.generatorName = generatorName;
	}

	/**
	 * <h3 class="en-US">Getter method for the column histories names list</h3>
	 * <h3 class="zh-CN">历史列名列表的Getter方法</h3>
	 *
	 * @return <span class="en-US">Column histories names list</span>
	 * <span class="zh-CN">历史列名列表</span>
	 */
	@Nonnull
	public List<String> getHistoriesNames() {
		return (this.historiesNames == null) ? Collections.emptyList() : this.historiesNames;
	}

	/**
	 * <h3 class="en-US">Setter method for the column histories names list</h3>
	 * <h3 class="zh-CN">历史列名列表的Setter方法</h3>
	 *
	 * @param historiesNames <span class="en-US">Column histories names list</span>
	 *                       <span class="zh-CN">历史列名列表</span>
	 */
	public void setHistoriesNames(@Nonnull final List<String> historiesNames) {
		this.historiesNames = historiesNames;
	}
}
