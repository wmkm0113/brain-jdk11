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
import org.nervousync.brain.defines.ColumnDefine;
import org.nervousync.brain.defines.TableDefine;
import org.nervousync.brain.enumerations.query.CalculateCode;
import org.nervousync.brain.enumerations.query.ConnectionCode;
import org.nervousync.brain.enumerations.query.JoinType;
import org.nervousync.brain.manager.TableManager;
import org.nervousync.brain.query.QueryInfo;
import org.nervousync.brain.query.builder.BrainQueryBuilder;
import org.nervousync.utils.LoggerUtils;
import org.nervousync.utils.StringUtils;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public final class QueryBuilderTest {

	private transient final LoggerUtils.Logger logger = LoggerUtils.getLogger(this.getClass());

	static {
		LoggerUtils.initLoggerConfigure(Level.DEBUG);
		registerTable();
		registerJoin();
		registerSubQuery();
	}

	private static void registerTable() {
		TableDefine tableDefine = new TableDefine();
		tableDefine.setTableName("tableName");

		List<ColumnDefine> columnDefines = new ArrayList<>();
		for (int i = 1 ; i < 4 ; i++) {
			ColumnDefine columnDefine = new ColumnDefine();
			columnDefine.setColumnName("columnName" + i);
			columnDefine.setJdbcType(Types.VARCHAR);
			columnDefines.add(columnDefine);
		}
		tableDefine.setColumnDefines(columnDefines);
		TableManager.getInstance().register(tableDefine);
	}

	private static void registerJoin() {
		TableDefine tableDefine = new TableDefine();
		tableDefine.setTableName("joinTable");

		ColumnDefine columnDefine = new ColumnDefine();
		columnDefine.setColumnName("joinColumn");
		columnDefine.setJdbcType(Types.VARCHAR);
		tableDefine.setColumnDefines(List.of(columnDefine));
		TableManager.getInstance().register(tableDefine);
	}

	private static void registerSubQuery() {
		TableDefine tableDefine = new TableDefine();
		tableDefine.setTableName("subQueryTable");

		ColumnDefine columnDefine = new ColumnDefine();
		columnDefine.setColumnName("subColumn");
		columnDefine.setJdbcType(Types.VARCHAR);
		tableDefine.setColumnDefines(List.of(columnDefine));
		TableManager.getInstance().register(tableDefine);
	}

	@Test
	@Order(10)
	public void simpleAndQuery() throws Exception {
		QueryInfo queryInfo =
				new BrainQueryBuilder()
						.fromTable("tableName").confirm()
						//	Configure query items
						.items()
						.constant(1, "CONV", Types.INTEGER).sortCode(1).confirm()
						.column("tableName", "columnName1").confirm()	//	Confirm query column
						.column("tableName", "columnName2").aliasName("aliasName2").sortCode(10).confirm()	//	Confirm query column
						.column("tableName", "columnName3").distinct().aliasName("aliasName3").confirm()	//	Confirm query column
						.function("MAX", Types.INTEGER).aliasName("FUNC").sortCode(1)
						.parameters()
						.column()
						.value("tableName", "columnName2", Boolean.FALSE).sortCode(1).confirm()	//	Confirm function parameter
						.confirm()	//	Confirm function item
						.confirm()
						.calculate(CalculateCode.ADD, Types.INTEGER).aliasName("TOTAL").sortCode(1)
						.parameters()
						.column("tableName", "columnName1").confirm()	//	Confirm calculate parameter
						.column("tableName", "columnName2").confirm()	//	Confirm calculate parameter
						.confirm()	//	Confirm calculate parameters
						.confirm()	//	Confirm calculate item
						.confirm()
						//	Configure where clause
						.where()
						.greater("tableName", "columnName1")
						.matchValue(1)
						.confirm()	//	Confirm condition
						.greaterEqual("tableName", "columnName1")
						.matchValue(1)
						.confirm()	//	Confirm condition
						.less("tableName", "columnName1")
						.matchValue(1)
						.confirm()	//	Confirm condition
						.lessEqual("tableName", "columnName1")
						.matchValue(1)
						.sortCode(1)
						.confirm()	//	Confirm condition
						.equalTo("tableName", "columnName1")
						.matchValue(1)
						.confirm()	//	Confirm condition
						.notEqual("tableName", "columnName1")
						.matchValue(1)
						.confirm()	//	Confirm condition
						.betweenAnd("tableName", "columnName2")
						.inRanges(5, 10)
						.confirm()	//	Confirm condition
						.notBetweenAnd("tableName", "columnName2")
						.inRanges(5, 10)
						.confirm()	//	Confirm condition
						.in("tableName", "columnName3")
						.inArray("a", "b", "c", "d", "e", "f")
						.confirm()	//	Confirm condition
						.notIn("tableName", "columnName3")
						.inArray("a", "b", "c", "d", "e", "f")
						.confirm()	//	Confirm condition
						.like("tableName", "columnName1")
						.matchValue("%a")
						.confirm()	//	Confirm condition
						.notLike("tableName", "columnName1")
						.matchValue("%a")
						.confirm()	//	Confirm condition
						.isNull("tableName", "columnName1")
						.notNull("tableName", "columnName2")
						.confirm()	//	Confirm where clause
						.orders()
						.orderBy("tableName", "columnName1")
						.asc()
						.desc()
						.confirm()
						.confirm()
						.groups()
						.groupBy("tableName", "columnName2")
						.confirm()
						.confirm()
						.useCache()
						.pager(1, 20)
						.build();
		this.logger.info("Generated_Result", queryInfo.toString(StringUtils.StringType.XML));
	}

	@Test
	@Order(15)
	public void simpleOrQuery() throws Exception {
		QueryInfo queryInfo =
				new BrainQueryBuilder()
						.fromTable("tableName").confirm()
						.items()
						.column("tableName", "columnName1")
						.confirm()
						.column("tableName", "columnName2")
						.aliasName("aliasName2")
						.confirm()
						.column("tableName", "columnName3")
						.aliasName("aliasName3")
						.confirm()
						.confirm()
						.where()
						.greater(ConnectionCode.OR, "tableName", "columnName1")
						.matchValue(1)
						.confirm()
						.greaterEqual(ConnectionCode.OR, "tableName", "columnName1")
						.matchValue(1)
						.confirm()
						.less(ConnectionCode.OR, "tableName", "columnName1")
						.matchValue(1)
						.confirm()
						.lessEqual(ConnectionCode.OR, "tableName", "columnName1")
						.matchValue(1)
						.confirm()
						.equalTo(ConnectionCode.OR, "tableName", "columnName1")
						.matchValue(1)
						.confirm()
						.notEqual(ConnectionCode.OR, "tableName", "columnName1")
						.matchValue(1)
						.confirm()
						.betweenAnd(ConnectionCode.OR, "tableName", "columnName2")
						.inRanges(5, 10)
						.confirm()
						.notBetweenAnd(ConnectionCode.OR, "tableName", "columnName2")
						.inRanges(5, 10)
						.confirm()
						.in(ConnectionCode.OR, "tableName", "columnName3")
						.inArray("a", "b", "c", "d", "e", "f")
						.confirm()
						.notIn(ConnectionCode.OR, "tableName", "columnName3")
						.inArray("a", "b", "c", "d", "e", "f")
						.confirm()
						.like(ConnectionCode.OR, "tableName", "columnName1")
						.matchValue("%a")
						.confirm()
						.notLike(ConnectionCode.OR, "tableName", "columnName1")
						.matchValue("%a")
						.confirm()
						.isNull(ConnectionCode.OR, "tableName", "columnName1")
						.notNull(ConnectionCode.OR, "tableName", "columnName2")
						.confirm()
						.orders()
						.orderBy("tableName", "columnName1")
						.sortCode(1)
						.asc()
						.desc()
						.confirm()
						.confirm()
						.groups()
						.groupBy("tableName", "columnName2")
						.sortCode(1)
						.confirm()
						.confirm()
						.useCache()
						.pager(1, 20)
						.build();
		this.logger.info("Generated_Result", queryInfo.toString(StringUtils.StringType.XML));
	}

	@Test
	@Order(18)
	public void simpleGroupQuery() throws Exception {
		QueryInfo queryInfo =
				new BrainQueryBuilder()
						.fromTable("tableName").confirm()
						.items()
						.column("tableName", "columnName1").confirm()
						.column("tableName", "columnName2").aliasName("aliasName2").confirm()
						.column("tableName", "columnName3").aliasName("aliasName3").confirm()
						.confirm()
						.where()
						.group()
						.sortCode(2)
						.greater("tableName", "columnName1").matchValue(1).confirm()
						.greaterEqual("tableName", "columnName1").matchValue(1).confirm()
						.less("tableName", "columnName1").matchValue(1).confirm()
						.lessEqual("tableName", "columnName1").matchValue(1).confirm()
						.equalTo("tableName", "columnName1").matchValue(1).confirm()
						.notEqual("tableName", "columnName1").matchValue(1).confirm()
						.betweenAnd("tableName", "columnName2").inRanges(5, 10).confirm()
						.notBetweenAnd("tableName", "columnName2").inRanges(5, 10).confirm()
						.in("tableName", "columnName3").inArray("a", "b", "c", "d", "e", "f").confirm()
						.notIn("tableName", "columnName3").inArray("a", "b", "c", "d", "e", "f").confirm()
						.like("tableName", "columnName1").matchValue("%a").confirm()
						.notLike("tableName", "columnName1").matchValue("%a").confirm()
						.isNull("tableName", "columnName1")
						.notNull("tableName", "columnName2")
						.exists("tableName", "columnName")
						.matchQuery()
						.fromTable("subQueryTable").confirm()
						.column("subQueryTable", "subColumn").confirm()
						.where()
						.equalTo("subQueryTable", "subColumn").matchValue(2).confirm()
						.confirm()
						.confirm()
						.confirm()
						.notExists("tableName", "columnName")
						.matchQuery()
						.fromSubQuery("sub1")
						.builder()
						.fromTable("subQueryTable").confirm()
						.items()
						.column("subQueryTable", "subColumn").confirm()
						.confirm()
						.where()
						.equalTo("subQueryTable", "subColumn").matchValue(2).confirm()
						.confirm()
						.confirm()
						.confirm()
						.confirm()
						.confirm()
						.confirm()
						.confirm()
						.orders()
						.orderBy("tableName", "columnName1")
						.confirm()
						.confirm()
						.groups()
						.groupBy("tableName", "columnName2")
						.confirm()
						.confirm()
						.useCache()
						.pager(1, 20)
						.build();
		this.logger.info("Generated_Result", queryInfo.toString(StringUtils.StringType.XML));
	}

	@Test
	@Order(20)
	public void joinAndQuery() throws Exception {
		QueryInfo queryInfo =
				new BrainQueryBuilder()
						.fromTable("tableName").confirm()
						.joins()
						.joinTable(JoinType.LEFT, "tableName", "joinTable")
						.on("currentColumn", "joinColumn")
						.confirm()
						.confirm()
						.items()
						.column("tableName", "columnName1")
						.confirm()
						.column("tableName", "columnName2")
						.aliasName("aliasName2")
						.confirm()
						.column("tableName", "columnName3")
						.aliasName("aliasName3")
						.confirm()
						.confirm()
						.where()
						.greater("tableName", "columnName1")
						.matchFunction()
						.functionName("MAX", Types.INTEGER)
						.parameters()
						.constant()
						.value("1")
						.sortCode(1)
						.confirm()
						.function()
						.sortCode(2)
						.functionName("MIN", Types.INTEGER)
						.parameters()
						.confirm()
						.confirm()
						.confirm()
						.confirm()
						.confirm()
						.confirm()
						.confirm()
						.greaterEqual("tableName", "columnName1")
						.matchColumn("matchTable", "matchColumn")
						.confirm()
						.less("tableName", "columnName1")
						.matchColumn("matchTable", "matchColumn")
						.confirm()
						.lessEqual("tableName", "columnName1")
						.matchColumn("matchTable", "matchColumn")
						.confirm()
						.equalTo("tableName", "columnName1")
						.matchColumn("matchTable", "matchColumn")
						.confirm()
						.notEqual("tableName", "columnName1")
						.matchColumn("matchTable", "matchColumn")
						.confirm()
						.confirm()
						.build();
		this.logger.info("Generated_Result", queryInfo.toString(StringUtils.StringType.XML));
	}

	@Test
	@Order(25)
	public void joinOrQuery() throws Exception {
		QueryInfo queryInfo =
				new BrainQueryBuilder()
						.fromTable("tableName").confirm()
						.joins()
						.joinTable(JoinType.LEFT, "tableName", "joinTable")
						.aliasName("aliasName")
						.on("currentColumn", "joinColumn")
						.confirm()
						.confirm()
						.items()
						.column("tableName", "columnName1")
						.confirm()
						.column("tableName", "columnName2")
						.aliasName("aliasName2")
						.confirm()
						.column("tableName", "columnName3")
						.aliasName("aliasName3")
						.confirm()
						.confirm()
						.where()
						.greater(ConnectionCode.OR, "tableName", "columnName1")
						.matchColumn("matchTable", "matchColumn")
						.confirm()
						.greaterEqual(ConnectionCode.OR, "tableName", "columnName1")
						.matchColumn("matchTable", "matchColumn")
						.confirm()
						.less(ConnectionCode.OR, "tableName", "columnName1")
						.matchColumn("matchTable", "matchColumn")
						.confirm()
						.lessEqual(ConnectionCode.OR, "tableName", "columnName1")
						.matchColumn("matchTable", "matchColumn")
						.confirm()
						.equalTo(ConnectionCode.OR, "tableName", "columnName1")
						.matchColumn("matchTable", "matchColumn")
						.confirm()
						.notEqual(ConnectionCode.OR, "tableName", "columnName1")
						.matchColumn("matchTable", "matchColumn")
						.confirm()
						.confirm()
						.build();
		this.logger.info("Generated_Result", queryInfo.toString(StringUtils.StringType.XML));
	}

	@Test
	@Order(28)
	public void joinSubQuery() throws Exception {
		QueryInfo queryInfo =
				new BrainQueryBuilder()
						.fromTable("tableName").confirm()
						.joins()
						.joinQuery(JoinType.LEFT, "tableName")
						.subQueryBuilder()
						.fromTable("joinTable").confirm()
						.items()
						.column("joinTable", "joinColumn")
						.confirm()
						.confirm()
						.where()
						.equalTo("joinTable", "joinColumn")
						.matchValue(1)
						.confirm()
						.confirm()
						.confirm()
						.aliasName("aliasName")
						.on("currentColumn", "joinColumn")
						.confirm()
						.confirm()
						.items()
						.column("tableName", "columnName1")
						.confirm()
						.column("tableName", "columnName2")
						.aliasName("aliasName2")
						.confirm()
						.column("tableName", "columnName3")
						.aliasName("aliasName3")
						.confirm()
						.confirm()
						.where()
						.greater(ConnectionCode.OR, "tableName", "columnName1")
						.matchColumn("matchTable", "matchColumn")
						.confirm()
						.greaterEqual(ConnectionCode.OR, "tableName", "columnName1")
						.matchColumn("matchTable", "matchColumn")
						.confirm()
						.less(ConnectionCode.OR, "tableName", "columnName1")
						.matchColumn("matchTable", "matchColumn")
						.confirm()
						.lessEqual(ConnectionCode.OR, "tableName", "columnName1")
						.matchColumn("matchTable", "matchColumn")
						.confirm()
						.equalTo(ConnectionCode.OR, "tableName", "columnName1")
						.matchColumn("matchTable", "matchColumn")
						.confirm()
						.notEqual(ConnectionCode.OR, "tableName", "columnName1")
						.matchColumn("matchTable", "matchColumn")
						.confirm()
						.confirm()
						.build();
		this.logger.info("Generated_Result", queryInfo.toString(StringUtils.StringType.XML));
	}

	@Test
	@Order(30)
	public void functionAndQuery() throws Exception {
		QueryInfo queryInfo =
				new BrainQueryBuilder()
						.fromTable("tableName").confirm()
						.joins()
						.joinTable(JoinType.LEFT, "tableName", "joinTable")
						.on("currentColumn", "joinColumn")
						.confirm()
						.confirm()
						.items()
						.column("tableName", "columnName1")
						.confirm()
						.column("tableName", "columnName2")
						.aliasName("aliasName2")
						.confirm()
						.column("tableName", "columnName3")
						.aliasName("aliasName3")
						.confirm()
						.confirm()
						.where()
						.greater("tableName", "columnName1")
						.function("COUNT")
						.matchColumn("joinTable", "joinColumn")
						.confirm()
						.greaterEqual("tableName", "columnName1")
						.function("COUNT")
						.matchColumn("joinTable", "joinColumn")
						.confirm()
						.less("tableName", "columnName1")
						.function("COUNT")
						.matchColumn("joinTable", "joinColumn")
						.confirm()
						.lessEqual("tableName", "columnName1")
						.function("COUNT")
						.matchColumn("joinTable", "joinColumn")
						.confirm()
						.equalTo("tableName", "columnName1")
						.function("COUNT")
						.matchColumn("joinTable", "joinColumn")
						.confirm()
						.notEqual("tableName", "columnName1")
						.function("COUNT")
						.matchColumn("joinTable", "joinColumn")
						.confirm()
						.confirm()
						.build();
		this.logger.info("Generated_Result", queryInfo.toString(StringUtils.StringType.XML));
	}

	@Test
	@Order(35)
	public void functionOrQuery() throws Exception {
		QueryInfo queryInfo =
				new BrainQueryBuilder()
						.fromTable("tableName").confirm()
						.joins()
						.joinTable(JoinType.LEFT, "tableName", "joinTable")
						.on("currentColumn", "joinColumn")
						.confirm()
						.confirm()
						.items()
						.column("tableName", "columnName1")
						.confirm()
						.column("tableName", "columnName2")
						.aliasName("aliasName2")
						.confirm()
						.column("tableName", "columnName3")
						.aliasName("aliasName3")
						.confirm()
						.confirm()
						.where()
						.greater(ConnectionCode.OR, "tableName", "columnName1")
						.function("COUNT")
						.matchColumn("joinTable", "joinColumn")
						.confirm()
						.greaterEqual(ConnectionCode.OR, "tableName", "columnName1")
						.function("COUNT")
						.matchColumn("joinTable", "joinColumn")
						.confirm()
						.less(ConnectionCode.OR, "tableName", "columnName1")
						.function("COUNT")
						.matchColumn("joinTable", "joinColumn")
						.confirm()
						.lessEqual(ConnectionCode.OR, "tableName", "columnName1")
						.function("COUNT")
						.matchColumn("joinTable", "joinColumn")
						.confirm()
						.equalTo(ConnectionCode.OR, "tableName", "columnName1")
						.function("COUNT")
						.matchColumn("joinTable", "joinColumn")
						.confirm()
						.notEqual(ConnectionCode.OR, "tableName", "columnName1")
						.function("COUNT")
						.matchColumn("joinTable", "joinColumn")
						.confirm()
						.confirm()
						.build();
		this.logger.info("Generated_Result", queryInfo.toString(StringUtils.StringType.XML));
	}

	@Test
	@Order(40)
	public void subQueryAndQuery() throws Exception {
		QueryInfo queryInfo =
				new BrainQueryBuilder()
						.fromTable("tableName").confirm()
						.joins()
						.joinTable(JoinType.LEFT, "tableName", "joinTable")
						.on("currentColumn", "joinColumn")
						.confirm()
						.confirm()
						.items()
						.column("tableName", "columnName1")
						.confirm()
						.column("tableName", "columnName2")
						.aliasName("aliasName2")
						.confirm()
						.column("tableName", "columnName3")
						.aliasName("aliasName3")
						.confirm()
						.confirm()
						.where()
						.greater("tableName", "columnName1")
						.matchQuery()
						.fromTable("subQueryTable").confirm()
						.column("subQueryTable", "subColumn").confirm()
						.where()
						.equalTo("subQueryTable", "subColumn")
						.matchValue(2)
						.confirm()
						.confirm()
						.confirm()
						.confirm()
						.confirm()
						.build();
		this.logger.info("Generated_Result", queryInfo.toString(StringUtils.StringType.XML));
	}

	@Test
	@Order(45)
	public void subQueryOrQuery() throws Exception {
		QueryInfo queryInfo =
				new BrainQueryBuilder()
						.fromTable("tableName").confirm()
						.joins()
						.joinTable(JoinType.LEFT, "tableName", "joinTable")
						.on("currentColumn", "joinColumn")
						.confirm()
						.confirm()
						.items()
						.column("tableName", "columnName1")
						.confirm()
						.column("tableName", "columnName2")
						.aliasName("aliasName2")
						.confirm()
						.column("tableName", "columnName3")
						.aliasName("aliasName3")
						.confirm()
						.confirm()
						.where()
						.greaterEqual(ConnectionCode.OR, "tableName", "columnName1")
						.matchQuery()
						.fromTable("subQueryTable").confirm()
						.column("subQueryTable", "subColumn").confirm()
						.where()
						.equalTo("subQueryTable", "subColumn")
						.matchValue(2)
						.confirm()
						.confirm()
						.confirm()
						.confirm()
						.confirm()
						.build();
		this.logger.info("Generated_Result", queryInfo.toString(StringUtils.StringType.XML));
	}

	@Test
	@Order(50)
	public void existQuery() throws Exception {
		QueryInfo queryInfo =
				new BrainQueryBuilder()
						.fromTable("tableName").confirm()
						.joins()
						.joinTable(JoinType.LEFT, "tableName", "joinTable")
						.on("currentColumn", "joinColumn")
						.confirm()
						.confirm()
						.items()
						.column("tableName", "columnName1")
						.confirm()
						.column("tableName", "columnName2")
						.aliasName("aliasName2")
						.confirm()
						.column("tableName", "columnName3")
						.aliasName("aliasName3")
						.confirm()
						.confirm()
						.where()
						.exists("tableName", "columnName")
						.matchQuery()
						.fromTable("subQueryTable").confirm()
						.column("subQueryTable", "subColumn").confirm()
						.where()
						.equalTo("subQueryTable", "subColumn")
						.matchValue(2)
						.confirm()
						.confirm()
						.confirm()
						.confirm()
						.confirm()
						.build();
		this.logger.info("Generated_Result", queryInfo.toString(StringUtils.StringType.XML));
	}

	@Test
	@Order(55)
	public void notExistQuery() throws Exception {
		QueryInfo queryInfo =
				new BrainQueryBuilder()
						.fromTable("tableName").confirm()
						.joins()
						.joinTable(JoinType.LEFT, "tableName", "joinTable")
						.on("currentColumn", "joinColumn")
						.confirm()
						.confirm()
						.items()
						.column("tableName", "columnName1")
						.confirm()
						.column("tableName", "columnName2")
						.aliasName("aliasName2")
						.confirm()
						.column("tableName", "columnName3")
						.aliasName("aliasName3")
						.confirm()
						.confirm()
						.where()
						.notExists("tableName", "columnName")
						.matchQuery()
						.fromTable("subQueryTable").confirm()
						.column("subQueryTable", "subColumn").confirm()
						.where()
						.equalTo("subQueryTable", "subColumn").matchValue(2).confirm()
						.confirm()
						.groups()
						.groupBy("column1", "column2").confirm()
						.confirm()
						.having()
						.less(ConnectionCode.OR, "tableName", "columnName1").matchValue(1).confirm()
						.confirm()
						.confirm()
						.confirm()
						.confirm()
						.build();
		this.logger.info("Generated_Result", queryInfo.toString(StringUtils.StringType.XML));
	}

	@Test
	@Order(60)
	public void havingQuery() throws Exception {
		QueryInfo queryInfo =
				new BrainQueryBuilder()
						.fromTable("tableName").confirm()
						.items()
						.column("tableName", "columnName1").confirm()
						.column("tableName", "columnName2").aliasName("aliasName2").confirm()
						.column("tableName", "columnName3").aliasName("aliasName3").confirm()
						.confirm()
						.groups()
						.groupBy("tableName", "groupByColumn").confirm()
						.confirm()
						.having()
						.greater("tableName", "columnName1").matchValue(1).confirm()
						.greaterEqual("tableName", "columnName1").matchValue(1).confirm()
						.less("tableName", "columnName1").matchValue(1).confirm()
						.lessEqual("tableName", "columnName1").matchValue(1).confirm()
						.equalTo("tableName", "columnName1").matchValue(1).confirm()
						.notEqual("tableName", "columnName1").matchValue(1).confirm()
						.betweenAnd("tableName", "columnName2").inRanges(5, 10).confirm()
						.notBetweenAnd("tableName", "columnName2").inRanges(5, 10).confirm()
						.in("tableName", "columnName3").inArray("a", "b", "c", "d", "e", "f").confirm()
						.notIn("tableName", "columnName3").inArray("a", "b", "c", "d", "e", "f").confirm()
						.like("tableName", "columnName1").matchValue("%a").confirm()
						.notLike("tableName", "columnName1").matchValue("%a").confirm()
						.isNull("tableName", "columnName1")
						.notNull("tableName", "columnName2").confirm()
						.orders()
						.orderBy("tableName", "columnName1").confirm()
						.confirm()
						.groups()
						.groupBy("tableName", "columnName2").confirm()
						.confirm()
						.useCache()
						.pager(1, 20)
						.build();
		String xmlData = queryInfo.toString(StringUtils.StringType.XML);
		this.logger.info("Generated_Result", xmlData);
		QueryInfo parsedQuery = StringUtils.stringToObject(xmlData, QueryInfo.class, "https://nervousync.org/schemas/brain");
		this.logger.info("Generated_Result", parsedQuery.toString(StringUtils.StringType.JSON));
	}
}
