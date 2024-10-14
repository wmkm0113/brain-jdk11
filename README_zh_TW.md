# 資料庫通用介面

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nervousync/brain-jdk11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.nervousync/brain-jdk11/)
[![License](https://img.shields.io/github/license/wmkm0113/brain-jdk11.svg)](https://github.com/wmkm0113/brain-jdk11/blob/master/LICENSE)
![Language](https://img.shields.io/badge/language-Java-green)
[![Twitter:wmkm0113](https://img.shields.io/twitter/follow/wmkm0113?label=Follow)](https://twitter.com/wmkm0113)

[English](README.md)
[简体中文](README_zh_CN.md)
繁體中文

為資料處理平台打造的通用資料來源，提供了統一的JDBC資料庫連線池、支援使用SSL安全連線資料庫伺服器、支援分庫分錶、
支援關係型資料庫、分散式資料庫以及透過SOAP/Restful方式提供服務的遠端資料庫、支援多種身分認證方式，
同時提供了統一的事務配置，方便使用者進行跨資料庫的事務操作。

## 目錄

* [JDK版本](#JDK版本)
* [生命週期](#生命週期)
* [使用方法](#使用方法)
    + [在專案中添加支持](#1在專案中添加支持)
    + [初始化資料庫管理器](#2初始化資料庫管理器)
    + [註冊資料來源](#3註冊資料來源)
    + [操作資料來源](#4操作資料來源)
    + [進階配置](#5進階配置)
* [貢獻與回饋](#貢獻與回饋)
* [贊助與鳴謝](#贊助與鳴謝)

## JDK版本：

編譯：OpenJDK 11   
運行：OpenJDK 11+ 或相容版本

## 生命週期：

**功能凍結：** 2026年12月31日   
**安全更新：** 2029年12月31日

## 使用方法：

### 1、在專案中添加支持

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

### 2、初始化資料庫管理器

呼叫org.nervousync.brain.source.BrainDataSource中名為 “initialize” 的靜態方法來初始化單例模式的資料來源。

參數資訊:

|    Name    | Data type  |    Notes    |
|:----------:|:----------:|:-----------:|
| jmxEnabled |    布林值     |   開啟JMX監控   |
|  lazyInit  |    布林值     | 使用時再執行初始化操作 |
|  ddlType   | DDLType枚舉值 |   操作類型枚舉值   |

Enumeration value of DDLType:

```
NONE: 無操作
VALIDATE: 僅驗證，實體類別定義與資料表結構不一致時拋出例外
CREATE: 僅建立不存在的資料表
CREATE_TRUNCATE: 啟動時建立資料表，結束時清空所有資料表
CREATE_DROP: 啟動時建立資料表，結束時刪除所有資料表
SYNCHRONIZE: 同步實體類別定義與資料表結構。如果資料表不存在，則建立資料表；如果結構不一致，則進行更新操作；如果未找到實體類別與資料表的映射，則刪除資料表
```

### 3、註冊資料來源

呼叫org.nervousync.brain.source.BrainDataSource單例物件的名為 “register” 的方法來註冊資料來源，參數為資料來源的設定資訊。

**資料來源配置資訊範例：**

分散式資料庫：

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

關係型資料庫：

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

遠端資料庫：

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

### 4、操作資料來源

**初始化資料表：**

呼叫org.nervousync.brain.source.BrainDataSource單例物件的名為 “initTable” 的方法來初始化資料表。

參數資訊：

|    參數名稱     |                    資料型別                     |    備註     |
|:-----------:|:-------------------------------------------:|:---------:|
| tableDefine |  org.nervousync.brain.defines.TableDefine   |  資料表定義資訊  |
|  database   | org.nervousync.brain.defines.ShardingDefine | 資料庫分片設定資訊 |
|    table    | org.nervousync.brain.defines.ShardingDefine | 資料表分片設定資訊 |
| schemaNames |           java.lang.String array            | 資料來源名稱陣列  |

拋出例外：如果在執行過程中出錯

**清空所有資料表：**

呼叫org.nervousync.brain.source.BrainDataSource單例物件的名為 “truncateTables” 的方法來清空所有資料來源中所有資料表的資料。

拋出例外：如果在執行過程中出錯

**清空資料表：**

呼叫org.nervousync.brain.source.BrainDataSource單例物件的名為 “truncateTable” 的方法來清空指定資料來源中指定資料表的資料。

參數資訊：

|     參數名     |                   資料型別                   |  Notes   |
|:-----------:|:----------------------------------------:|:--------:|
| tableDefine | org.nervousync.brain.defines.TableDefine | 資料表定義資訊  |
| schemaNames |          java.lang.String array          | 資料來源名稱陣列 |

Throws: Exception if an error occurred during execution

**刪除所有資料表**

呼叫org.nervousync.brain.source.BrainDataSource單例物件的名為 “dropTables” 的方法來刪除所有資料來源中的所有資料表。

參數資訊：

|    參數名稱    |                       資料型別                       |   備註   |
|:----------:|:------------------------------------------------:|:------:|
| dropOption | org.nervousync.brain.enumerations.ddl.DropOption | 級聯刪除選項 |

拋出例外：如果在執行過程中出錯

**刪除資料表**

呼叫org.nervousync.brain.source.BrainDataSource單例物件的名為 “dropTable” 的方法來刪除指定資料來源中的指定資料表。

參數資訊：

|    參數名稱     |                       資料型別                       |    備註    |
|:-----------:|:------------------------------------------------:|:--------:|
| tableDefine |     org.nervousync.brain.defines.TableDefine     | 資料表定義資訊  |
| dropOption  | org.nervousync.brain.enumerations.ddl.DropOption |  級聯刪除選項  |
| schemaNames |              java.lang.String array              | 資料來源名稱陣列 |

拋出例外：如果在執行過程中出錯

**插入記錄：**

呼叫org.nervousync.brain.source.BrainDataSource單例物件的名為 “insert” 的方法來新增資料到指定資料來源的指定資料表。

參數資訊：

|    參數名稱     |                   資料型別                   |   備註    |
|:-----------:|:----------------------------------------:|:-------:|
| schemaName  |             java.lang.String             | 資料來源名稱  |
| tableDefine | org.nervousync.brain.defines.TableDefine | 資料表定義資訊 |
|   dataMap   |   java.util.Map<String, Serializable>    | 寫入資料映射表 |

傳回值：資料庫產生的主鍵值映射表

拋出例外：如果在執行過程中出錯

**檢索記錄：**

呼叫org.nervousync.brain.source.BrainDataSource單例物件的名為 “retrieve” 的方法來從指定資料來源的指定資料表檢索資料。

參數資訊：

|    參數名稱     |                   資料型別                   |     備註     |
|:-----------:|:----------------------------------------:|:----------:|
| schemaName  |             java.lang.String             |   資料來源名稱   |
| tableDefine | org.nervousync.brain.defines.TableDefine |  資料表定義資訊   |
|  filterMap  |   java.util.Map<String, Serializable>    |  查詢條件映射表   |
|  forUpdate  |                 boolean                  | 檢索結果用於更新記錄 |
| lockOption  |     jakarta.persistence.LockModeType     |  查詢記錄鎖定選項  |

傳回值：檢索到記錄的資料映射表

拋出例外：如果在執行過程中出錯

**更新記錄：**

呼叫org.nervousync.brain.source.BrainDataSource單例物件的名為 “update” 的方法來更新指定資料來源中指定資料表的資料。

參數資訊：

|    參數名稱     |                   資料型別                   |   備註    |
|:-----------:|:----------------------------------------:|:-------:|
| schemaName  |             java.lang.String             | 資料來源名稱  |
| tableDefine | org.nervousync.brain.defines.TableDefine | 資料表定義資訊 |
|   dataMap   |   java.util.Map<String, Serializable>    | 更新資料映射表 |
|  filterMap  |   java.util.Map<String, Serializable>    | 更新條件映射表 |

傳回值：更新記錄條數

拋出例外：如果在執行過程中出錯

**刪除記錄：**

呼叫org.nervousync.brain.source.BrainDataSource單例物件的名為 “delete” 的方法來刪除指定資料來源中指定資料表的資料。

參數資訊：

|    參數名稱     |                   資料型別                   |   備註    |
|:-----------:|:----------------------------------------:|:-------:|
| schemaName  |             java.lang.String             | 資料來源名稱  |
| tableDefine | org.nervousync.brain.defines.TableDefine | 資料表定義資訊 |
|  filterMap  |   java.util.Map<String, Serializable>    | 刪除條件映射表 |

傳回值：刪除記錄條數

拋出例外：如果在執行過程中出錯

**查詢資料：**

呼叫org.nervousync.brain.source.BrainDataSource單例物件的名為 “query” 的方法在指定資料來源中進行資料查詢。

參數資訊：

|   參數名稱    |                 資料型別                 |   備註   |
|:---------:|:------------------------------------:|:------:|
| queryInfo | org.nervousync.brain.query.QueryInfo | 資料檢索資訊 |

傳回值：List of data mapping tables for queried records

拋出例外：如果在執行過程中出錯

**查詢資料用於更新操作：**

呼叫org.nervousync.brain.source.BrainDataSource單例物件的名為 “queryForUpdate” 的方法在指定資料來源中進行資料查詢，查詢結果用於批次更新資料操作。

參數資訊：

|     參數名稱      |                             資料型別                              |     備註     |
|:-------------:|:-------------------------------------------------------------:|:----------:|
|  schemaName   |                       java.lang.String                        |   資料來源名稱   |
|  tableDefine  |           org.nervousync.brain.defines.TableDefine            |  資料表定義資訊   |
| conditionList | jva.util.List<org.nervousync.brain.query.condition.Condition> | 查詢條件實例物件清單 |
|  lockOption   |               jakarta.persistence.LockModeType                |  查詢記錄鎖定選項  |

傳回值：List of data mapping tables for queried records

拋出例外：如果在執行過程中出錯

**開啟事務：**

呼叫org.nervousync.brain.source.BrainDataSource單例物件的名為 “initTransactional” 的方法來設定目前執行緒的事務配置資訊。

參數資訊：

|        參數名稱         |                                資料型別                                |   備註   |
|:-------------------:|:------------------------------------------------------------------:|:------:|
| transactionalConfig | org. nervousync. brain. configs. transactional.TransactionalConfig | 事務配置資訊 |

拋出例外：如果在執行過程中出錯

**結束事務：**

呼叫org.nervousync.brain.source.BrainDataSource單例物件的名為 “endTransactional” 的方法來結束目前執行緒的事務。

拋出例外：如果在執行過程中出錯

**回滾事務：**

呼叫org.nervousync.brain.source.BrainDataSource單例物件的名為 “rollback” 的方法來回滾目前執行緒的事務（如果目前執行緒配置了事務資訊）。

拋出例外：如果在執行過程中出錯

**提交交易：**

呼叫org.nervousync.brain.source.BrainDataSource單例物件的名為 “commit” 的方法來提交目前執行緒的事務（如果當前執行緒配置了事務資訊）。

拋出例外：如果在執行過程中出錯

### 5、進階配置

**使用憑證庫中的憑證進行身分認證：**

替換 “authentication” 的設定資訊為：

```
{
  "authType" : "CERTIFICATE",
  "trustStorePath" : "trustStorePath",
  "trustStorePassword" : "password",
  "certificateName" : "willUsedCertificateName"
}
```

**使用X.509憑證進行身份認證：**

替換 “authentication” 的設定資訊為：

```
{
  "authType" : "CERTIFICATE",
  "certData" : "base64EncodedCertificateContent"
}
```

## 貢獻與回饋

歡迎各位朋友將此文檔及專案中的提示資訊、錯誤資訊等翻譯為更多語言，以説明更多的使用者更好地瞭解與使用此工具包。   
如果在使用過程中發現問題或需要改進、添加相關功能，請提交issue到本專案或發送電子郵件到[wmkm0113\@gmail.com](mailto:wmkm0113@gmail.com?subject=bugs_and_features)   
為了更好地溝通，請在提交issue或發送電子郵件時，寫明如下資訊：   
1、目的是：發現Bug/功能改進/添加新功能   
2、請粘貼以下資訊（如果存在）：傳入資料，預期結果，錯誤堆疊資訊   
3、您認為可能是哪裡的代碼出現問題（如提供可以幫助我們儘快地找到並解決問題）   
如果您提交的是添加新功能的相關資訊，請確保需要添加的功能是一般性的通用需求，即添加的新功能可以幫助到大多數使用者。

如果您需要添加的是定制化的特殊需求，我將收取一定的定制開發費用，具體費用金額根據定制化的特殊需求的工作量進行評估。   
定制化特殊需求請直接發送電子郵件到[wmkm0113\@gmail.com](mailto:wmkm0113@gmail.com?subject=payment_features)
，同時請儘量在郵件中寫明您可以負擔的開發費用預算金額。

## 贊助與鳴謝

<span id="JetBrains">
    <img src="https://resources.jetbrains.com/storage/products/company/brand/logos/jetbrains.svg" width="100px" alt="JetBrains Logo (Main) logo.">
    <span>非常感謝 <a href="https://www.jetbrains.com/">JetBrains</a> 通過許可證贊助我們的開源項目。</span>
</span>

