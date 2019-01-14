package model.sql

import java.sql.Connection

import better.files.File
import model.core.CommonUtils.using
import model.sql.SqlConsts._
import net.sf.jsqlparser.statement.Statement
import net.sf.jsqlparser.statement.delete.Delete
import net.sf.jsqlparser.statement.insert.Insert
import net.sf.jsqlparser.statement.select.Select
import net.sf.jsqlparser.statement.update.Update
import slick.jdbc.JdbcBackend.Database

import scala.collection.mutable.ListBuffer
import scala.language.postfixOps
import scala.util.{Failure, Try}

abstract class SqlExecutionDAO(mainDbName: String, port: Int) {

  protected val mainDB = db(None)

  protected def db(maybeSchemaName: Option[String]): Database = {
    val url = maybeSchemaName match {
      case None             => s"jdbc:mysql://localhost:$port?useSSL=false"
      case Some(schemaName) => s"jdbc:mysql://localhost:$port/$schemaName?useSSL=false"
    }

    Database.forURL(url, user = "it4all", password = "sT8aV#k7", driver = "com.mysql.cj.jdbc.Driver")
  }


  def executeQueries(scenario: SqlScenario, exercise: SqlCompleteEx, userStatement: Statement, sampleStatement: Statement): SqlExecutionResult = {
    val userExecutionResult: Try[SqlQueryResult] = executeQuery(scenario.shortName, userStatement)
    val sampleExecutionResult: Try[SqlQueryResult] = executeQuery(scenario.shortName, sampleStatement)
    SqlExecutionResult(userExecutionResult, sampleExecutionResult)
  }

  protected def executeQuery(schemaName: String, query: Statement): Try[SqlQueryResult]

  def executeSetup(schemaName: String, scriptFilePath: File): Try[Unit] = {
    using(mainDB.source.createConnection()) { mainConnection =>
      val res = mainConnection.prepareStatement(s"SHOW DATABASES LIKE '$schemaName';").executeQuery()

      if (!res.next() || res.getString(1) != schemaName) {
        using(mainConnection.prepareStatement(s"CREATE DATABASE IF NOT EXISTS $schemaName;"))(_.execute())
      }
    }

    using(db(Some(schemaName)).source.createConnection()) { connection =>
      readScript(scriptFilePath) foreach { query => using(connection.prepareStatement(query))(_.execute()) }
    }
  }

  private def readScript(filePath: File): Seq[String] = {
    var stringBuilder = StringBuilder.newBuilder

    val queries: ListBuffer[String] = ListBuffer.empty

    for (readLine <- filePath.lines; trimmedLine = readLine.trim if !(trimmedLine startsWith "--")) {
      stringBuilder ++= trimmedLine + "\n"

      if (trimmedLine endsWith sqlDelimiter) {
        queries += stringBuilder.toString
        stringBuilder = StringBuilder.newBuilder
      }
    }

    queries
  }

  private def allTableNames(connection: Connection): Seq[String] = using(connection.prepareStatement("SHOW TABLES;")) { tablesQuery =>
    using(tablesQuery.executeQuery()) { resultSet =>
      val tableNames: ListBuffer[String] = ListBuffer.empty

      while (resultSet.next)
        tableNames += resultSet.getString(1)

      tableNames
    }
  }.flatten getOrElse Seq[String]()

  def tableContents(schemaName: String): Seq[SqlQueryResult] = using(db(Some(schemaName)).source.createConnection()) { connection =>
    allTableNames(connection) map { tableName =>
      val selectStatement = connection.prepareStatement(SELECT_ALL_DUMMY + tableName)
      val resultSet = selectStatement.executeQuery()
      SqlQueryResult(resultSet, tableName)
    }
  } getOrElse Seq[SqlQueryResult]()

}

object SelectDAO extends SqlExecutionDAO("sqlselect", 3107) {

  override protected def executeQuery(schemaName: String, query: Statement): Try[SqlQueryResult] = query match {
    case sel: Select =>
      using(db(Some(schemaName)).source.createConnection()) { connection =>
        using(connection.prepareStatement(sel.toString)) { statement => SqlQueryResult(statement.executeQuery()) }
      } flatten
    case _           => Failure(null)
  }
}

object ChangeDAO extends SqlExecutionDAO("sqlchange", 3108) {

  override protected def executeQuery(schemaName: String, query: Statement): Try[SqlQueryResult] = query match {
    case change@(_: Update | _: Insert | _: Delete) =>
      using(db(Some(schemaName)).source.createConnection()) { connection =>
        connection.setAutoCommit(false)
        using(connection.prepareStatement(change.toString)) { statement => statement.executeUpdate() } flatMap { _ =>

          val validationQuery = "SELECT * FROM " + (change match {
            case upd: Update => upd.getTables.get(0)
            case ins: Insert => ins.getTable
            case del: Delete => del.getTable
          }).getName

          val res = using(connection.prepareStatement(validationQuery)) { statement => SqlQueryResult(statement.executeQuery()) }

          connection.rollback()

          res
        }
      } flatten
    case _                                          => Failure(null)
  }

}

object CreateDAO extends SqlExecutionDAO("sqlcreate", 3109) {

  override protected def executeQuery(schemaName: String, query: Statement): Try[SqlQueryResult] = Try(???)

}