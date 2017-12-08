package model.sql

import java.nio.file.Path
import java.sql.Connection

import model.core.CommonUtils.using
import model.sql.SqlConsts._
import slick.jdbc.JdbcBackend.Database

import scala.collection.mutable.ListBuffer
import scala.io.Source

abstract class SqlExecutionDAO(mainDbName: String, port: Int) {

  protected val mainDB = db(mainDbName)

  protected def db(schemaName: String): Database = Database.forURL(url = s"jdbc:mysql://localhost:$port/$schemaName?useSSL=false",
    user = "it4all", password = "sT8aV#k7", driver = "com.mysql.cj.jdbc.Driver")

  def executeQuery(schemaName: String, userStatement: String, sampleStatement: String): SqlExecutionResult = using(db(schemaName).source.createConnection()) { connection =>
    val userResultSet = connection.prepareStatement(userStatement).executeQuery()
    val sampleResultSet = connection.prepareStatement(sampleStatement).executeQuery()
    SqlExecutionResult(new SqlQueryResult(userResultSet), new SqlQueryResult(sampleResultSet))
  }

  def executeSetup(schemaName: String, scriptFilePath: Path): Unit = {
    using(mainDB.source.createConnection()) { mainConnection =>
      val res = mainConnection.prepareStatement(s"SHOW DATABASES LIKE '$schemaName';").executeQuery()

      if (!res.next() || res.getString(1) != schemaName) {
        var createStatement = mainConnection.prepareStatement(s"CREATE DATABASE IF NOT EXISTS $schemaName")
        createStatement.execute()
      }
    }

    using(db(schemaName).source.createConnection()) { connection =>
      readScript(scriptFilePath) foreach { query => connection.prepareStatement(query).execute() }
    }
  }

  private def readScript(filePath: Path): Seq[String] = {
    var stringBuilder = StringBuilder.newBuilder

    using(Source.fromFile(filePath.toFile)) { source =>
      var queries: ListBuffer[String] = ListBuffer.empty

      for (readLine <- source.getLines; trimmedLine = readLine.trim if !(trimmedLine startsWith "--")) {
        stringBuilder ++= trimmedLine + "\n"

        if (trimmedLine endsWith Delimiter) {
          queries += stringBuilder.toString
          stringBuilder = StringBuilder.newBuilder
        }
      }

      queries
    }
  }

  private def allTableNames(connection: Connection): Seq[String] = using(connection.prepareStatement("SHOW TABLES")) { tablesQuery =>
    val resultSet = tablesQuery.executeQuery()

    val tableNames: ListBuffer[String] = ListBuffer.empty

    while (resultSet.next)
      tableNames += resultSet.getString(1)

    resultSet.close()

    tableNames
  }

  def tableContents(schemaName: String): Seq[SqlQueryResult] = using(db(schemaName).source.createConnection()) { connection =>
    allTableNames(connection) map { tableName =>
      val selectStatement = connection.prepareStatement(SELECT_ALL_DUMMY + tableName)
      val resultSet = selectStatement.executeQuery()
      SqlQueryResult(resultSet, tableName)
    }
  }

}

object SelectDAO extends SqlExecutionDAO("sqlselect", 3107)

object ChangeDAO extends SqlExecutionDAO("sqlchange", 3108)

object CreateDAO extends SqlExecutionDAO("sqlcreate", 3109)