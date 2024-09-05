# 大脑数据源

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nervousync/brain-jdk11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.nervousync/brain-jdk11/)
[![License](https://img.shields.io/github/license/wmkm0113/brain-jdk11.svg)](https://github.com/wmkm0113/brain-jdk11/blob/master/LICENSE)
![Language](https://img.shields.io/badge/language-Java-green)
[![Twitter:wmkm0113](https://img.shields.io/twitter/follow/wmkm0113?label=Follow)](https://twitter.com/wmkm0113)

[English](README.md)
简体中文
[繁體中文](README_zh_TW.md)

为数据处理平台打造的通用数据源，提供了统一的JDBC数据库连接池、支持使用SSL安全连接数据库服务器、支持分库分表、
支持关系型数据库、分布式数据库以及通过SOAP/Restful方式提供服务的远程数据库、支持多种身份认证方式，
同时提供了统一的事务配置，方便使用者进行跨数据库的事务操作。

## 目录

* [JDK版本](#JDK版本)
* [生命周期](#生命周期)
* [使用方法](#使用方法)
    + [在项目中添加支持](#1在项目中添加支持)
    + [初始化数据源](#2初始化数据源)
    + [注册数据源](#3注册数据源)
    + [操作数据源](#4操作数据源)
    + [高级配置](#5高级配置)
* [贡献与反馈](#贡献与反馈)
* [赞助与鸣谢](#赞助与鸣谢)

## JDK版本：

编译：OpenJDK 11   
运行：OpenJDK 11+ 或兼容版本

## 生命周期：

**功能冻结：** 2026年12月31日   
**安全更新：** 2029年12月31日

## 使用方法：

### 1、在项目中添加支持

**Maven:**

```
<dependency>
    <groupId>org.nervousync</groupId>
	<artifactId>brain-jdk11</artifactId>
    <version>${version}</version>
</dependency>
```

**Gradle:**

```
Manual: compileOnly group: 'org.nervousync', name: 'brain-jdk11', version: '${version}'
Short: compileOnly 'org.nervousync:brain-jdk11:${version}'
```

**SBT:**

```
libraryDependencies += "org.nervousync" % "brain-jdk11" % "${version}" % "provided"
```

**Ivy:**

```
<dependency org="org.nervousync" name="brain-jdk11" rev="${version}"/>
```

### 2、初始化数据源

调用org.nervousync.brain.source.BrainDataSource中名为 “initialize” 的静态方法来初始化单例模式的数据源。

参数信息:

|    Name    | Data type  |    Notes    |
|:----------:|:----------:|:-----------:|
| jmxEnabled |    布尔值     |   开启JMX监控   |
|  lazyInit  |    布尔值     | 使用时再执行初始化操作 |
|  ddlType   | DDLType枚举值 |   操作类型枚举值   |

Enumeration value of DDLType:

```
NONE: 无操作
VALIDATE: 仅验证，实体类定义与数据表结构不一致时抛出异常
CREATE: 仅创建不存在的数据表
CREATE_TRUNCATE: 启动时创建数据表，结束时清空所有数据表
CREATE_DROP: 启动时创建数据表，结束时删除所有数据表
SYNCHRONIZE: 同步实体类定义与数据表结构。如果数据表不存在，则创建数据表；如果结构不一致，则进行更新操作；如果未找到实体类与数据表的映射，则删除数据表
```

### 3、注册数据源

调用org.nervousync.brain.source.BrainDataSource单例对象的名为 “register” 的方法来注册数据源，参数为数据源的配置信息。

**数据源配置信息范例：**

分布式数据库：

```
{
  "schemaName" : "SampleSchema",
  "defaultSchema" : false,
  "dialectName" : "MongoDB",
  "trustStore" : {
    "trustStorePath" : "pathToStoreFile",
    "trustStorePassword" : "password"
  },
  "authentication" : {
    "authType" : "BASIC",
    "userName" : "username",
    "passWord" : "password"
  },
  "lowQueryTimeout" : 500,
  "validateTimeout" : 1,
  "connectTimeout" : 5,
  "sharding" : false,
  "shardingDefault" : "",
  "pooled" : true,
  "minConnections" : 2,
  "maxConnections" : 10,
  "serverList" : [ {
    "serverAddress" : "localhost",
    "serverPort" : 8080,
    "serverLevel" : 0
  }, {
    "serverAddress" : "localhost",
    "serverPort" : 8081,
    "serverLevel" : 1
  }, {
    "serverAddress" : "localhost",
    "serverPort" : 8082,
    "serverLevel" : 2
  } ],
  "databaseName" : "SampleDatabase",
  "useSsl" : true
}
```

关系型数据库：

```
{
  "schemaName" : "SampleSchema",
  "defaultSchema" : false,
  "dialectName" : "PostgreSQL",
  "trustStore" : {
    "trustStorePath" : "pathToStoreFile",
    "trustStorePassword" : "password"
  },
  "authentication" : {
    "authType" : "BASIC",
    "userName" : "username",
    "passWord" : "password"
  },
  "lowQueryTimeout" : 500,
  "validateTimeout" : 1,
  "connectTimeout" : 5,
  "sharding" : false,
  "shardingDefault" : "",
  "pooled" : true,
  "minConnections" : 2,
  "maxConnections" : 10,
  "serverArray" : true,
  "serverList" : [ {
    "serverAddress" : "localhost",
    "serverPort" : 8080,
    "serverLevel" : 0
  }, {
    "serverAddress" : "localhost",
    "serverPort" : 8081,
    "serverLevel" : 1
  }, {
    "serverAddress" : "localhost",
    "serverPort" : 8082,
    "serverLevel" : 2
  } ],
  "jdbcUrl" : "jdbc:url",
  "retryCount" : 3,
  "retryPeriod" : 1000,
  "cachedLimitSize" : 5,
  "testOnBorrow" : true,
  "testOnReturn" : true
}
```

远程数据库：

```
{
  "schemaName" : "SampleSchema",
  "defaultSchema" : false,
  "dialectName" : "PostgreSQL",
  "trustStore" : {
    "trustStorePath" : "pathToStoreFile",
    "trustStorePassword" : "password"
  },
  "authentication" : {
    "authType" : "BASIC",
    "userName" : "username",
    "passWord" : "password"
  },
  "lowQueryTimeout" : 500,
  "validateTimeout" : 1,
  "connectTimeout" : 5,
  "sharding" : false,
  "shardingDefault" : "",
  "pooled" : true,
  "minConnections" : 2,
  "maxConnections" : 10,
  "remoteType" : "Restful",
  "remoteAddress" : "https://localhost/service/path",
  "proxyConfig" : null,
  "keepAlive" : 1200
}
```

### 4、操作数据源

**初始化数据表：**

调用org.nervousync.brain.source.BrainDataSource单例对象的名为 “initTable” 的方法来初始化数据表。

参数信息：

|     参数名     |                    数据类型                     |    备注     |
|:-----------:|:-------------------------------------------:|:---------:|
| tableDefine |  org.nervousync.brain.defines.TableDefine   |  数据表定义信息  |
|  database   | org.nervousync.brain.defines.ShardingDefine | 数据库分片配置信息 |
|    table    | org.nervousync.brain.defines.ShardingDefine | 数据表分片配置信息 |
| schemaNames |           java.lang.String array            |  数据源名称数组  |

抛出异常：如果在执行过程中出错

**清空所有数据表：**

调用org.nervousync.brain.source.BrainDataSource单例对象的名为 “truncateTables” 的方法来清空所有数据源中所有数据表的数据。

抛出异常：如果在执行过程中出错

**清空数据表：**

调用org.nervousync.brain.source.BrainDataSource单例对象的名为 “truncateTable” 的方法来清空指定数据源中指定数据表的数据。

参数信息：

|     参数名     |                   数据类型                   |  Notes  |
|:-----------:|:----------------------------------------:|:-------:|
| tableDefine | org.nervousync.brain.defines.TableDefine | 数据表定义信息 |
| schemaNames |          java.lang.String array          | 数据源名称数组 |

Throws: Exception if an error occurred during execution

**删除所有数据表**

调用org.nervousync.brain.source.BrainDataSource单例对象的名为 “dropTables” 的方法来删除所有数据源中的所有数据表。

参数信息：

|    参数名     |                       数据类型                       |   备注   |
|:----------:|:------------------------------------------------:|:------:|
| dropOption | org.nervousync.brain.enumerations.ddl.DropOption | 级联删除选项 |

抛出异常：如果在执行过程中出错

**删除数据表**

调用org.nervousync.brain.source.BrainDataSource单例对象的名为 “dropTable” 的方法来删除指定数据源中的指定数据表。

参数信息：

|     参数名     |                       数据类型                       |   备注    |
|:-----------:|:------------------------------------------------:|:-------:|
| tableDefine |     org.nervousync.brain.defines.TableDefine     | 数据表定义信息 |
| dropOption  | org.nervousync.brain.enumerations.ddl.DropOption | 级联删除选项  |
| schemaNames |              java.lang.String array              | 数据源名称数组 |

抛出异常：如果在执行过程中出错

**插入记录：**

调用org.nervousync.brain.source.BrainDataSource单例对象的名为 “insert” 的方法来新增数据到指定数据源的指定数据表。

参数信息：

|     参数名     |                   数据类型                   |   备注    |
|:-----------:|:----------------------------------------:|:-------:|
| schemaName  |             java.lang.String             |  数据源名称  |
| tableDefine | org.nervousync.brain.defines.TableDefine | 数据表定义信息 |
|   dataMap   |   java.util.Map<String, Serializable>    | 写入数据映射表 |

返回值：数据库生成的主键值映射表

抛出异常：如果在执行过程中出错

**检索记录：**

调用org.nervousync.brain.source.BrainDataSource单例对象的名为 “retrieve” 的方法来从指定数据源的指定数据表检索数据。

参数信息：

|     参数名     |                        数据类型                        |     备注     |
|:-----------:|:--------------------------------------------------:|:----------:|
| schemaName  |                  java.lang.String                  |   数据源名称    |
| tableDefine |      org.nervousync.brain.defines.TableDefine      |  数据表定义信息   |
|   columns   |                  java.lang.String                  |   查询数据列名   |
|  filterMap  |        java.util.Map<String, Serializable>         |  查询条件映射表   |
|  forUpdate  |                      boolean                       | 检索结果用于更新记录 |
| lockOption  | org.nervousync.brain.enumerations.query.LockOption |  查询记录锁定选项  |

返回值：检索到记录的数据映射表

抛出异常：如果在执行过程中出错

**更新记录：**

调用org.nervousync.brain.source.BrainDataSource单例对象的名为 “update” 的方法来更新指定数据源中指定数据表的数据。

参数信息：

|     参数名     |                   数据类型                   |   备注    |
|:-----------:|:----------------------------------------:|:-------:|
| schemaName  |             java.lang.String             |  数据源名称  |
| tableDefine | org.nervousync.brain.defines.TableDefine | 数据表定义信息 |
|   dataMap   |   java.util.Map<String, Serializable>    | 更新数据映射表 |
|  filterMap  |   java.util.Map<String, Serializable>    | 更新条件映射表 |

返回值：更新记录条数

抛出异常：如果在执行过程中出错

**删除记录：**

调用org.nervousync.brain.source.BrainDataSource单例对象的名为 “delete” 的方法来删除指定数据源中指定数据表的数据。

参数信息：

|     参数名     |                   数据类型                   |   备注    |
|:-----------:|:----------------------------------------:|:-------:|
| schemaName  |             java.lang.String             |  数据源名称  |
| tableDefine | org.nervousync.brain.defines.TableDefine | 数据表定义信息 |
|  filterMap  |   java.util.Map<String, Serializable>    | 删除条件映射表 |

返回值：删除记录条数

抛出异常：如果在执行过程中出错

**查询数据：**

调用org.nervousync.brain.source.BrainDataSource单例对象的名为 “query” 的方法在指定数据源中进行数据查询。

参数信息：

|    参数名    |                 数据类型                 |   备注   |
|:---------:|:------------------------------------:|:------:|
| queryInfo | org.nervousync.brain.query.QueryInfo | 数据检索信息 |

返回值：List of data mapping tables for queried records

抛出异常：如果在执行过程中出错

**查询数据用于更新操作：**

调用org.nervousync.brain.source.BrainDataSource单例对象的名为 “queryForUpdate” 的方法在指定数据源中进行数据查询，查询结果用于批量更新数据操作。

参数信息：

|      参数名      |                             数据类型                              |     备注     |
|:-------------:|:-------------------------------------------------------------:|:----------:|
|  schemaName   |                       java.lang.String                        |   数据源名称    |
|  tableDefine  |           org.nervousync.brain.defines.TableDefine            |  数据表定义信息   |
| conditionList | jva.util.List<org.nervousync.brain.query.condition.Condition> | 查询条件实例对象列表 |
|  lockOption   |      org.nervousync.brain.enumerations.query.LockOption       |  查询记录锁定选项  |

返回值：List of data mapping tables for queried records

抛出异常：如果在执行过程中出错

**开启事务：**

调用org.nervousync.brain.source.BrainDataSource单例对象的名为 “initTransactional” 的方法来配置当前线程的事务配置信息。

参数信息：

|         参数名         |                                数据类型                                |   备注   |
|:-------------------:|:------------------------------------------------------------------:|:------:|
| transactionalConfig | org. nervousync. brain. configs. transactional.TransactionalConfig | 事务配置信息 |

抛出异常：如果在执行过程中出错

**结束事务：**

调用org.nervousync.brain.source.BrainDataSource单例对象的名为 “endTransactional” 的方法来结束当前线程的事务。

抛出异常：如果在执行过程中出错

**回滚事务：**

调用org.nervousync.brain.source.BrainDataSource单例对象的名为 “rollback” 的方法来回滚当前线程的事务（如果当前线程配置了事务信息）。

抛出异常：如果在执行过程中出错

**提交事务：**

调用org.nervousync.brain.source.BrainDataSource单例对象的名为 “commit” 的方法来提交当前线程的事务（如果当前线程配置了事务信息）。

抛出异常：如果在执行过程中出错

### 5、高级配置

**使用证书库中的证书进行身份认证：**

替换 “authentication” 的配置信息为：

```
{
  "authType" : "CERTIFICATE",
  "trustStorePath" : "trustStorePath",
  "trustStorePassword" : "password",
  "certificateName" : "willUsedCertificateName"
}
```

**使用X.509证书进行身份认证：**

替换 “authentication” 的配置信息为：

```
{
  "authType" : "CERTIFICATE",
  "certData" : "base64EncodedCertificateContent"
}
```

## 贡献与反馈

欢迎各位朋友将此文档及项目中的提示信息、错误信息等翻译为更多语言，以帮助更多的使用者更好地了解与使用此工具包。   
如果在使用过程中发现问题或需要改进、添加相关功能，请提交issue到本项目或发送电子邮件到[wmkm0113\@gmail.com](mailto:wmkm0113@gmail.com?subject=bugs_and_features)   
为了更好地沟通，请在提交issue或发送电子邮件时，写明如下信息：   
1、目的是：发现Bug/功能改进/添加新功能   
2、请粘贴以下信息（如果存在）：传入数据，预期结果，错误堆栈信息   
3、您认为可能是哪里的代码出现问题（如提供可以帮助我们尽快地找到并解决问题）   
如果您提交的是添加新功能的相关信息，请确保需要添加的功能是一般性的通用需求，即添加的新功能可以帮助到大多数使用者。

如果您需要添加的是定制化的特殊需求，我将收取一定的定制开发费用，具体费用金额根据定制化的特殊需求的工作量进行评估。   
定制化特殊需求请直接发送电子邮件到[wmkm0113\@gmail.com](mailto:wmkm0113@gmail.com?subject=payment_features)
，同时请尽量在邮件中写明您可以负担的开发费用预算金额。

## 赞助与鸣谢

<span id="JetBrains">
    <img src="https://resources.jetbrains.com/storage/products/company/brand/logos/jetbrains.svg" width="100px" alt="JetBrains Logo (Main) logo.">
    <span>非常感谢 <a href="https://www.jetbrains.com/">JetBrains</a> 通过许可证赞助我们的开源项目。</span>
</span>