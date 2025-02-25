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

package org.nervousync.brain.test;

import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.*;
import org.nervousync.brain.enumerations.query.ConditionCode;
import org.nervousync.brain.enumerations.query.JoinType;
import org.nervousync.brain.query.QueryInfo;
import org.nervousync.brain.query.builder.BrainQueryBuilder;
import org.nervousync.brain.query.condition.Condition;
import org.nervousync.brain.query.core.AbstractItem;
import org.nervousync.brain.query.data.QueryData;
import org.nervousync.brain.query.join.JoinInfo;
import org.nervousync.brain.query.param.AbstractParameter;
import org.nervousync.commons.Globals;
import org.nervousync.enumerations.core.ConnectionCode;
import org.nervousync.utils.LoggerUtils;

import java.util.Collections;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public final class QueryBuilderTest {

	private transient final LoggerUtils.Logger logger = LoggerUtils.getLogger(this.getClass());

	static {
		LoggerUtils.initLoggerConfigure(Level.DEBUG);
	}

	@Test
	@Order(10)
	public void simpleAndQuery() throws Exception {
		QueryInfo queryInfo =
				BrainQueryBuilder.newBuilder("tableName")
						.queryColumn("tableName", "columnName1")
						.queryColumn("tableName", "columnName2", "aliasName2")
						.queryColumn("tableName", "columnName3", "aliasName3")
						.greater("tableName", "columnName1", 1)
						.greaterEqual("tableName", "columnName1", 1)
						.less("tableName", "columnName1", 1)
						.lessEqual("tableName", "columnName1", 1)
						.equalTo("tableName", "columnName1", 1)
						.notEqual("tableName", "columnName1", 1)
						.inRanges("tableName", "columnName2", 5, 10)
						.notInRanges("tableName", "columnName2", 5, 10)
						.in("tableName", "columnName3", "a", "b", "c", "d", "e", "f")
						.notIn("tableName", "columnName3", "a", "b", "c", "d", "e", "f")
						.like("tableName", "columnName1", "%a")
						.notLike("tableName", "columnName1", "%a")
						.matchNull("tableName", "columnName1")
						.notNull("tableName", "columnName2")
						.addOrderBy("tableName", "columnName1")
						.addGroupBy("tableName", "columnName2")
						.useCache(Boolean.TRUE)
						.configPager(1, 20)
						.confirm();
		this.logger.info("Generated_Result", queryInfo.toXML(Boolean.TRUE));
	}

	@Test
	@Order(15)
	public void simpleOrQuery() throws Exception {
		QueryInfo queryInfo =
				BrainQueryBuilder.newBuilder("tableName")
						.queryColumn("tableName", "columnName1")
						.queryColumn("tableName", "columnName2", "aliasName2")
						.queryColumn("tableName", "columnName3", "aliasName3")
						.greater(ConnectionCode.OR, "tableName", "columnName1", 1)
						.greaterEqual(ConnectionCode.OR, "tableName", "columnName1", 1)
						.less(ConnectionCode.OR, "tableName", "columnName1", 1)
						.lessEqual(ConnectionCode.OR, "tableName", "columnName1", 1)
						.equalTo(ConnectionCode.OR, "tableName", "columnName1", 1)
						.notEqual(ConnectionCode.OR, "tableName", "columnName1", 1)
						.inRanges(ConnectionCode.OR, "tableName", "columnName2", 5, 10)
						.notInRanges(ConnectionCode.OR, "tableName", "columnName2", 5, 10)
						.in(ConnectionCode.OR, "tableName", "columnName3", "a", "b", "c", "d", "e", "f")
						.notIn(ConnectionCode.OR, "tableName", "columnName3", "a", "b", "c", "d", "e", "f")
						.like(ConnectionCode.OR, "tableName", "columnName1", "%a")
						.notLike(ConnectionCode.OR, "tableName", "columnName1", "%a")
						.matchNull(ConnectionCode.OR, "tableName", "columnName1")
						.notNull(ConnectionCode.OR, "tableName", "columnName2")
						.addOrderBy("tableName", "columnName1")
						.addGroupBy("tableName", "columnName2")
						.useCache(Boolean.TRUE)
						.configPager(1, 20)
						.confirm();
		this.logger.info("Generated_Result", queryInfo.toXML(Boolean.TRUE));
	}

	@Test
	@Order(20)
	public void joinAndQuery() throws Exception {
		QueryInfo queryInfo =
				BrainQueryBuilder.newBuilder("tableName")
						.joinTable(JoinType.LEFT, "joinTable",
								Collections.singletonList(JoinInfo.newInstance("tableName", "currentColumn", "joinColumn")))
						.queryColumn("tableName", "columnName1")
						.queryColumn("tableName", "columnName2", "aliasName2")
						.queryColumn("tableName", "columnName3", "aliasName3")
						.greater("tableName", "columnName1", "matchTable", "matchColumn")
						.greaterEqual("tableName", "columnName1", "matchTable", "matchColumn")
						.less("tableName", "columnName1", "matchTable", "matchColumn")
						.lessEqual("tableName", "columnName1", "matchTable", "matchColumn")
						.equalTo("tableName", "columnName1", "matchTable", "matchColumn")
						.notEqual("tableName", "columnName1", "matchTable", "matchColumn")
						.confirm();
		this.logger.info("Generated_Result", queryInfo.toXML(Boolean.TRUE));
	}

	@Test
	@Order(25)
	public void joinOrQuery() throws Exception {
		QueryInfo queryInfo =
				BrainQueryBuilder.newBuilder("tableName")
						.joinTable(JoinType.LEFT, "joinTable",
								Collections.singletonList(JoinInfo.newInstance("tableName", "currentColumn", "joinColumn")))
						.queryColumn("tableName", "columnName1")
						.queryColumn("tableName", "columnName2", "aliasName2")
						.queryColumn("tableName", "columnName3", "aliasName3")
						.greater(ConnectionCode.OR, "tableName", "columnName1", "matchTable", "matchColumn")
						.greaterEqual(ConnectionCode.OR, "tableName", "columnName1", "matchTable", "matchColumn")
						.less(ConnectionCode.OR, "tableName", "columnName1", "matchTable", "matchColumn")
						.lessEqual(ConnectionCode.OR, "tableName", "columnName1", "matchTable", "matchColumn")
						.equalTo(ConnectionCode.OR, "tableName", "columnName1", "matchTable", "matchColumn")
						.notEqual(ConnectionCode.OR, "tableName", "columnName1", "matchTable", "matchColumn")
						.confirm();
		this.logger.info("Generated_Result", queryInfo.toXML(Boolean.TRUE));
	}

	@Test
	@Order(30)
	public void functionAndQuery() throws Exception {
		QueryInfo queryInfo =
				BrainQueryBuilder.newBuilder("tableName")
						.joinTable(JoinType.LEFT, "joinTable",
								Collections.singletonList(JoinInfo.newInstance("tableName", "currentColumn", "joinColumn")))
						.queryColumn("tableName", "columnName1")
						.queryColumn("tableName", "columnName2", "aliasName2")
						.queryColumn("tableName", "columnName3", "aliasName3")
						.greater("tableName", "columnName1",
								"COUNT", AbstractParameter.column("joinTable", "joinColumn"))
						.greaterEqual("tableName", "columnName1",
								"COUNT", AbstractParameter.column("joinTable", "joinColumn"))
						.less("tableName", "columnName1",
								"COUNT", AbstractParameter.column("joinTable", "joinColumn"))
						.lessEqual("tableName", "columnName1",
								"COUNT", AbstractParameter.column("joinTable", "joinColumn"))
						.equalTo("tableName", "columnName1",
								"COUNT", AbstractParameter.column("joinTable", "joinColumn"))
						.notEqual("tableName", "columnName1",
								"COUNT", AbstractParameter.column("joinTable", "joinColumn"))
						.confirm();
		this.logger.info("Generated_Result", queryInfo.toXML(Boolean.TRUE));
	}

	@Test
	@Order(35)
	public void functionOrQuery() throws Exception {
		QueryInfo queryInfo =
				BrainQueryBuilder.newBuilder("tableName")
						.joinTable(JoinType.LEFT, "joinTable",
								Collections.singletonList(JoinInfo.newInstance("tableName", "currentColumn", "joinColumn")))
						.queryColumn("tableName", "columnName1")
						.queryColumn("tableName", "columnName2", "aliasName2")
						.queryColumn("tableName", "columnName3", "aliasName3")
						.greater(ConnectionCode.OR, "tableName", "columnName1",
								"COUNT", AbstractParameter.column("joinTable", "joinColumn"))
						.greaterEqual(ConnectionCode.OR, "tableName", "columnName1",
								"COUNT", AbstractParameter.column("joinTable", "joinColumn"))
						.less(ConnectionCode.OR, "tableName", "columnName1",
								"COUNT", AbstractParameter.column("joinTable", "joinColumn"))
						.lessEqual(ConnectionCode.OR, "tableName", "columnName1",
								"COUNT", AbstractParameter.column("joinTable", "joinColumn"))
						.equalTo(ConnectionCode.OR, "tableName", "columnName1",
								"COUNT", AbstractParameter.column("joinTable", "joinColumn"))
						.notEqual(ConnectionCode.OR, "tableName", "columnName1",
								"COUNT", AbstractParameter.column("joinTable", "joinColumn"))
						.confirm();
		this.logger.info("Generated_Result", queryInfo.toXML(Boolean.TRUE));
	}

	@Test
	@Order(40)
	public void subQueryAndQuery() throws Exception {
		QueryData subQuery = this.newData();
		QueryInfo queryInfo =
				BrainQueryBuilder.newBuilder("tableName")
						.joinTable(JoinType.LEFT, "joinTable",
								Collections.singletonList(JoinInfo.newInstance("tableName", "currentColumn", "joinColumn")))
						.queryColumn("tableName", "columnName1")
						.queryColumn("tableName", "columnName2", "aliasName2")
						.queryColumn("tableName", "columnName3", "aliasName3")
						.greater("tableName", "columnName1", subQuery)
						.greaterEqual("tableName", "columnName1", subQuery)
						.less("tableName", "columnName1", subQuery)
						.lessEqual("tableName", "columnName1", subQuery)
						.equalTo("tableName", "columnName1", subQuery)
						.notEqual("tableName", "columnName1", subQuery)
						.in("tableName", "columnName2", subQuery)
						.notIn("tableName", "columnName2", subQuery)
						.confirm();
		this.logger.info("Generated_Result", queryInfo.toXML(Boolean.TRUE));
	}

	@Test
	@Order(45)
	public void subQueryOrQuery() throws Exception {
		QueryData subQuery = this.newData();
		QueryInfo queryInfo =
				BrainQueryBuilder.newBuilder("tableName")
						.joinTable(JoinType.LEFT, "joinTable",
								Collections.singletonList(JoinInfo.newInstance("tableName", "currentColumn", "joinColumn")))
						.queryColumn("tableName", "columnName1")
						.queryColumn("tableName", "columnName2", "aliasName2")
						.queryColumn("tableName", "columnName3", "aliasName3")
						.greater(ConnectionCode.OR, "tableName", "columnName1", subQuery)
						.greaterEqual(ConnectionCode.OR, "tableName", "columnName1", subQuery)
						.less(ConnectionCode.OR, "tableName", "columnName1", subQuery)
						.lessEqual(ConnectionCode.OR, "tableName", "columnName1", subQuery)
						.equalTo(ConnectionCode.OR, "tableName", "columnName1", subQuery)
						.notEqual(ConnectionCode.OR, "tableName", "columnName1", subQuery)
						.in(ConnectionCode.OR, "tableName", "columnName2", subQuery)
						.notIn(ConnectionCode.OR, "tableName", "columnName2", subQuery)
						.confirm();
		this.logger.info("Generated_Result", queryInfo.toXML(Boolean.TRUE));
	}

	@Test
	@Order(40)
	public void existQuery() throws Exception {
		QueryData subQuery = this.newData();
		QueryInfo queryInfo =
				BrainQueryBuilder.newBuilder("tableName")
						.joinTable(JoinType.LEFT, "joinTable",
								Collections.singletonList(JoinInfo.newInstance("tableName", "currentColumn", "joinColumn")))
						.queryColumn("tableName", "columnName1")
						.queryColumn("tableName", "columnName2", "aliasName2")
						.queryColumn("tableName", "columnName3", "aliasName3")
						.exists("tableName", subQuery, "FUNCTION")
						.exists(ConnectionCode.OR, "joinTable", subQuery, "FUNCTION")
						.confirm();
		this.logger.info("Generated_Result", queryInfo.toXML(Boolean.TRUE));
	}

	@Test
	@Order(50)
	public void notExistQuery() throws Exception {
		QueryData subQuery = this.newData();
		QueryInfo queryInfo =
				BrainQueryBuilder.newBuilder("tableName")
						.joinTable(JoinType.LEFT, "joinTable",
								Collections.singletonList(JoinInfo.newInstance("tableName", "currentColumn", "joinColumn")))
						.queryColumn("tableName", "columnName1")
						.queryColumn("tableName", "columnName2", "aliasName2")
						.queryColumn("tableName", "columnName3", "aliasName3")
						.notExists("tableName", subQuery, "FUNCTION")
						.notExists(ConnectionCode.OR, "joinTable", subQuery, "FUNCTION")
						.confirm();
		this.logger.info("Generated_Result", queryInfo.toXML(Boolean.TRUE));
	}

	private QueryData newData() {
		QueryData queryData = new QueryData();
		queryData.setTableName("subQueryTable");
		queryData.setQueryItem(AbstractItem.column("subQueryTable", "subColumn"));
		queryData.setConditions(Collections.singletonList(Condition.column(Globals.DEFAULT_VALUE_INT, ConnectionCode.AND, ConditionCode.EQUAL, "subQueryTable", "subColumn", AbstractParameter.constant(2))));
		return queryData;
	}
}
