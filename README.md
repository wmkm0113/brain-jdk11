# Brain DataSource

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nervousync/brain-jdk11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.nervousync/brain-jdk11/)
[![License](https://img.shields.io/github/license/wmkm0113/brain-jdk11.svg)](https://github.com/wmkm0113/brain-jdk11/blob/master/LICENSE)
![Language](https://img.shields.io/badge/language-Java-green)
[![Twitter:wmkm0113](https://img.shields.io/twitter/follow/wmkm0113?label=Follow)](https://twitter.com/wmkm0113)

English
[简体中文](README_zh_CN.md)
[繁體中文](README_zh_TW.md)

A universal data source created for the data processing platform, it provides a unified JDBC database connection pool,
supports the use of SSL to securely connect to the database server, and supports sub-databases and tables,
supports relational databases, distributed databases and remote databases that provide services through SOAP/Restful,
and supports multiple identity authentication methods.
At the same time, it provides unified transaction configuration to facilitate users to conduct cross-database
transaction operations.

## Table of contents

* [JDK Version](#JDK-Version)
* [End of Life](#End-of-Life)
* [Usage](#Usage)
    + [Add support to the project](#1-add-support-to-the-project)
    + [Initialize data source](#2-initialize-data-source)
    + [Register database schema](#3-register-database-schema)
    + [Process schema](#4-process-schema)
    + [Advanced configuration](#5-advanced-configuration)
* [Contributions and feedback](#contributions-and-feedback)
* [Sponsorship and Thanks To](#sponsorship-and-thanks-to)

## JDK Version

Compile：OpenJDK 11   
Runtime: OpenJDK 11+ or compatible version

## End of Life

**Features Freeze:** 31, Dec, 2026   
**Secure Patch:** 31, Dec, 2029

## Usage

### 1. Add support to the project

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

### 2. Initialize data source

Invoke static method named "initialize" at org.nervousync.brain.source.BrainDataSource to initialize singleton instance
of BrainDataSource.

Parameters:

|    Name    | Data type |               Notes               |
|:----------:|:---------:|:---------------------------------:|
| jmxEnabled |  boolean  |      Enable the JMX monitor       |
|  lazyInit  |  boolean  | Delay initialize connection pools |
|  ddlType   |  DDLType  |   Enumeration value of DDLType    |

Enumeration value of DDLType:

```
NONE: Nothing to do.
VALIDATE: Only verification, an exception is thrown when the entity class definition is inconsistent with the data table structure
CREATE: Only create data tables that do not exist
CREATE_TRUNCATE: Create data tables at startup and truncate all data tables at end
CREATE_DROP: Create data tables at startup and delete all data tables at end
SYNCHRONIZE: Synchronize entity class definition and data table structure. 
             If the data table does not exist, create the data table; 
             if the structure is inconsistent, perform an update operation; 
             if the mapping between the entity class and the data table is not found, delete the data table
```

### 3. Register database schema:

Invoke method named "register" at singleton instance of org.nervousync.brain.source.BrainDataSource to register database
schema, parameter is database schema configure information.

**Example of database schema configure information:**

Distribute Database:

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

Relational Database:

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

Remote Database:

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

### 4. Process schema

**Initialize table:**

Invoke method named "initTable" at singleton instance of BrainDataSource.

Parameters:

|    Name     |                  Data type                  |                     Notes                     |
|:-----------:|:-------------------------------------------:|:---------------------------------------------:|
| tableDefine |  org.nervousync.brain.defines.TableDefine   |           Table define information            |
|  database   | org.nervousync.brain.defines.ShardingDefine |  Database sharding configuration information  |
|    table    | org.nervousync.brain.defines.ShardingDefine | Data table sharding configuration information |
| schemaNames |           java.lang.String array            |            Data schema name array             |

Throws: Exception if an error occurred during execution

**Truncate Tables:**

Invoke method named "truncateTables" at singleton instance of BrainDataSource will truncate all data tables by all
schemas.

Throws: Exception if an error occurred during execution

**Truncate Table:**

Invoke method named "truncateTable" at singleton instance of BrainDataSource will truncate data table.

Parameters:

|    Name     |                Data type                 |          Notes           |
|:-----------:|:----------------------------------------:|:------------------------:|
| tableDefine | org.nervousync.brain.defines.TableDefine | Table define information |
| schemaNames |          java.lang.String array          |  Data schema name array  |

Throws: Exception if an error occurred during execution

**Drop Tables:**

Invoke method named "dropTables" at singleton instance of BrainDataSource will drop all data tables by all schemas.

Parameters:

|    Name    |                    Data type                     |          Notes           |
|:----------:|:------------------------------------------------:|:------------------------:|
| dropOption | org.nervousync.brain.enumerations.ddl.DropOption | Cascading delete options |

Throws: Exception if an error occurred during execution

**Drop Table:**

Invoke method named "dropTable" at singleton instance of BrainDataSource will drop data table.

Parameters:

|    Name     |                    Data type                     |          Notes           |
|:-----------:|:------------------------------------------------:|:------------------------:|
| tableDefine |     org.nervousync.brain.defines.TableDefine     | Table define information |
| dropOption  | org.nervousync.brain.enumerations.ddl.DropOption | Cascading delete options |
| schemaNames |              java.lang.String array              |  Data schema name array  |

Throws: Exception if an error occurred during execution

**Insert record:**

Invoke method named "insert" at singleton instance of BrainDataSource will insert record to the target data table.

Parameters:

|    Name     |                Data type                 |          Notes           |
|:-----------:|:----------------------------------------:|:------------------------:|
| schemaName  |             java.lang.String             |     Data schema name     |
| tableDefine | org.nervousync.brain.defines.TableDefine | Table define information |
|   dataMap   |   java.util.Map<String, Serializable>    |   Insert data mapping    |

Returns: Primary key value mapping table generated by database

Throws: Exception if an error occurred during execution

**Retrieve record:**

Invoke method named "retrieve" at singleton instance of BrainDataSource will retrieve record from the target data table.

Parameters:

|    Name     |                Data type                 |                  Notes                  |
|:-----------:|:----------------------------------------:|:---------------------------------------:|
| schemaName  |             java.lang.String             |            Data schema name             |
| tableDefine | org.nervousync.brain.defines.TableDefine |        Table define information         |
|  filterMap  |   java.util.Map<String, Serializable>    |         Retrieve filter mapping         |
|  forUpdate  |                 boolean                  | Retrieve result using for update record |
| lockOption  |     jakarta.persistence.LockModeType     |        Query record lock option         |

Returns: Data mapping table of retrieved record

Throws: Exception if an error occurred during execution

**Update record:**

Invoke method named "update" at singleton instance of BrainDataSource will update record at the target data table.

Parameters:

|    Name     |                Data type                 |          Notes           |
|:-----------:|:----------------------------------------:|:------------------------:|
| schemaName  |             java.lang.String             |     Data schema name     |
| tableDefine | org.nervousync.brain.defines.TableDefine | Table define information |
|   dataMap   |   java.util.Map<String, Serializable>    |   Update data mapping    |
|  filterMap  |   java.util.Map<String, Serializable>    | Retrieve filter mapping  |

Returns: Updated records count

Throws: Exception if an error occurred during execution

**Delete record:**

Invoke method named "delete" at singleton instance of BrainDataSource will delete record from the target data table.

Parameters:

|    Name     |                Data type                 |          Notes           |
|:-----------:|:----------------------------------------:|:------------------------:|
| schemaName  |             java.lang.String             |     Data schema name     |
| tableDefine | org.nervousync.brain.defines.TableDefine | Table define information |
|  filterMap  |   java.util.Map<String, Serializable>    |  Delete filter mapping   |

Returns: Deleted records count

Throws: Exception if an error occurred during execution

**Query records:**

Invoke method named "query" at singleton instance of BrainDataSource will query records from the target data tables.

Parameters:

|   Name    |              Data type               |          Notes           |
|:---------:|:------------------------------------:|:------------------------:|
| queryInfo | org.nervousync.brain.query.QueryInfo | Query record information |

Returns: List of data mapping tables for queried records

Throws: Exception if an error occurred during execution

**Query records for update:**

Invoke method named "queryForUpdate" at singleton instance of BrainDataSource will query records from the target data
table.

Parameters:

|     Name      |                           Data type                           |             Notes             |
|:-------------:|:-------------------------------------------------------------:|:-----------------------------:|
|  schemaName   |                       java.lang.String                        |       Data schema name        |
|  tableDefine  |           org.nervousync.brain.defines.TableDefine            |   Table define information    |
| conditionList | jva.util.List<org.nervousync.brain.query.condition.Condition> | Query condition instance list |
|  lockOption   |               jakarta.persistence.LockModeType                |   Query record lock option    |

Returns: List of data mapping tables for queried records

Throws: Exception if an error occurred during execution

**Begin transactional:**

Invoke method named "initTransactional" at singleton instance of BrainDataSource will configure transactional at current
thread.

Parameters:

|        Name         |                             Data type                              |                Notes                |
|:-------------------:|:------------------------------------------------------------------:|:-----------------------------------:|
| transactionalConfig | org. nervousync. brain. configs. transactional.TransactionalConfig | Transactional configure information |

Throws: Exception if an error occurred during execution

**End transactional:**

Invoke method named "endTransactional" at singleton instance of BrainDataSource will finish the transactional at current
thread.

Throws: Exception if an error occurred during execution

**Rollback transactional:**

Invoke method named "rollback" at singleton instance of BrainDataSource will roll back the transactional if the current
thread was configured transactional.

Throws: Exception if an error occurred during execution

**Commit transactional:**

Invoke method named "commit" at singleton instance of BrainDataSource will commit the transactional if the current
thread was configured transactional.

Throws: Exception if an error occurred during execution

### 5. Advanced configuration

**Using trust store for authentication:**

Replace the "authentication" data as:

```
{
  "authType" : "CERTIFICATE",
  "trustStorePath" : "trustStorePath",
  "trustStorePassword" : "password",
  "certificateName" : "willUsedCertificateName"
}
```

**Using X.509 certificate for authentication:**

Replace the "authentication" data as:

```
{
  "authType" : "CERTIFICATE",
  "certData" : "base64EncodedCertificateContent"
}
```

## Contributions and feedback

Friends are welcome to translate the prompt information, error messages,
etc. in this document and project into more languages to help more users better understand and use this toolkit.   
If you find problems during use or need to improve or add related functions, please submit an issue to this project
or send email to [wmkm0113\@gmail.com](mailto:wmkm0113@gmail.com?subject=bugs_and_features)   
For better communication, please include the following information when submitting an issue or sending an email:

1. The purpose is: discover bugs/function improvements/add new features
2. Please paste the following information (if it exists): incoming data, expected results, error stack information
3. Where do you think there may be a problem with the code (if provided, it can help us find and solve the problem as
   soon as possible)

If you are submitting information about adding new features, please ensure that the features to be added are general
needs, that is, the new features can help most users.

If you need to add customized special requirements, I will charge a certain custom development fee.
The specific fee amount will be assessed based on the workload of the customized special requirements.   
For customized special features, please send an email directly
to [wmkm0113\@gmail.com](mailto:wmkm0113@gmail.com?subject=payment_features). At the same time, please try to indicate
the budget amount of development cost you can afford in the email.

## Sponsorship and Thanks To

<span id="JetBrains">
    <img src="https://resources.jetbrains.com/storage/products/company/brand/logos/jetbrains.svg" width="100px" alt="JetBrains Logo (Main) logo.">
    <span>Many thanks to <a href="https://www.jetbrains.com/">JetBrains</a> for sponsoring our Open Source projects with a license.</span>
</span>