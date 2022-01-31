package model.tools.sql

import net.sf.jsqlparser.statement.Statement
import slick.jdbc.JdbcBackend.Database
import slick.util.AsyncExecutor

import scala.collection.mutable.ListBuffer
import scala.util.{Try, Using}

abstract class SqlExecutionDAO[Q <: Statement](port: Int) {

  protected def db(maybeSchemaName: Option[String]): Database = Database.forURL(
    url = maybeSchemaName match {
      case None             => s"jdbc:mysql://localhost:$port?useSSL=false"
      case Some(schemaName) => s"jdbc:mysql://localhost:$port/$schemaName?useSSL=false"
    },
    user = "it4all",
    password = "sT8aV#k7",
    driver = "com.mysql.cj.jdbc.Driver",
    executor = AsyncExecutor("AsyncExecutor.default", 20, 20, 1000, maxConnections = 20)
  )

  def executeQueries(schemaName: String, userStatement: Q, sampleStatement: Q): SqlExecutionResult = {
    val sampleExecutionResult = executeQuery(schemaName, sampleStatement).toOption

    val userExecutionResult: Option[SqlQueryResult] = executeQuery(schemaName, userStatement).map { userResult =>
      sampleExecutionResult match {
        case None               => userResult
        case Some(sampleResult) => SqlQueryResult.compareAndUpdateUserQueryResult(userResult, sampleResult)
      }
    }.toOption

    SqlExecutionResult(userExecutionResult, sampleExecutionResult)
  }

  protected def executeQuery(schemaName: String, query: Q): Try[SqlQueryResult]

  def tableContents(schemaName: String): Seq[SqlQueryResult] = Using
    .Manager { use =>
      val connection  = use { db(Some(schemaName)).source.createConnection() }
      val tablesQuery = use { connection.prepareStatement("SHOW TABLES;") }
      val resultSet   = use { tablesQuery.executeQuery() }

      val tableNames: ListBuffer[String] = ListBuffer.empty

      while (resultSet.next) tableNames += resultSet.getString(1)

      tableNames.map { tableName =>
        val resultSet = use { connection.prepareStatement(s"SELECT * FROM $tableName;").executeQuery() }

        SqlQueryResult.fromResultSet(resultSet, tableName)
      }.toSeq
    }
    .getOrElse(Seq.empty)

}
