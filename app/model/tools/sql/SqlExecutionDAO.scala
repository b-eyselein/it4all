package model.tools.sql

import java.sql.Connection

import net.sf.jsqlparser.statement.Statement
import net.sf.jsqlparser.statement.delete.Delete
import net.sf.jsqlparser.statement.insert.Insert
import net.sf.jsqlparser.statement.select.Select
import net.sf.jsqlparser.statement.update.Update
import play.api.Logger
import slick.jdbc.JdbcBackend.Database

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

abstract class SqlExecutionDAO(port: Int) {

  private val SHOW_TABLES_DUMMY = "SHOW TABLES;"

  private val SELECT_ALL_DUMMY = "SELECT * FROM "

  private val logger = Logger(classOf[SqlExecutionDAO])

  protected def using[A <: AutoCloseable, B](resource: A)(f: A => B): Try[B] =
    try {
      Success(f(resource))
    } catch {
      case e: Exception => Failure(e)
    } finally {
      try {
        if (resource != null) resource.close()
      } catch {
        case e: Exception => logger.error("There has been an error: ", e)
      }
    }

  protected def db(maybeSchemaName: Option[String]): Database = Database.forURL(
    url = maybeSchemaName match {
      case None             => s"jdbc:mysql://localhost:$port?useSSL=false"
      case Some(schemaName) => s"jdbc:mysql://localhost:$port/$schemaName?useSSL=false"
    },
    user = "it4all",
    password = "sT8aV#k7",
    driver = "com.mysql.cj.jdbc.Driver"
  )

  def executeQueries(
    schemaName: String,
    userStatement: Statement,
    sampleStatement: Statement
  ): SqlExecutionResult = {
    val sampleExecutionResult = executeQuery(schemaName, sampleStatement).toOption

    val userExecutionResult: Option[SqlQueryResult] = executeQuery(schemaName, userStatement).map { userResult =>
      sampleExecutionResult match {
        case None               => userResult
        case Some(sampleResult) => SqlQueryResult.compareAndUpdateUserQueryResult(userResult, sampleResult)
      }
    }.toOption

    SqlExecutionResult(userExecutionResult, sampleExecutionResult)
  }

  protected def executeQuery(schemaName: String, query: Statement): Try[SqlQueryResult]

  private def allTableNames(connection: Connection): Seq[String] =
    using(connection.prepareStatement(SHOW_TABLES_DUMMY)) { tablesQuery =>
      using(tablesQuery.executeQuery()) { resultSet =>
        val tableNames: ListBuffer[String] = ListBuffer.empty

        while (resultSet.next) tableNames += resultSet.getString(1)

        tableNames
      }
    }.flatten.map(_.toSeq).getOrElse(Seq[String]())

  def tableContents(schemaName: String): Seq[SqlQueryResult] =
    using(db(Some(schemaName)).source.createConnection()) { connection =>
      allTableNames(connection).map { tableName =>
        val selectStatement = connection.prepareStatement(SELECT_ALL_DUMMY + tableName)
        val resultSet       = selectStatement.executeQuery()
        SqlQueryResult.fromResultSet(resultSet, tableName)
      }
    }.getOrElse(Seq.empty)

}

object SelectDAO extends SqlExecutionDAO(3107) {

  override protected def executeQuery(schemaName: String, query: Statement): Try[SqlQueryResult] = query match {
    case sel: Select =>
      using(db(Some(schemaName)).source.createConnection()) { connection =>
        using(connection.prepareStatement(sel.toString)) { statement =>
          SqlQueryResult.fromResultSet(statement.executeQuery())
        }
      }.flatten
    case _ => Failure(null)
  }
}

object ChangeDAO extends SqlExecutionDAO(3108) {

  override protected def executeQuery(schemaName: String, query: Statement): Try[SqlQueryResult] = query match {
    case change @ (_: Update | _: Insert | _: Delete) =>
      using(db(Some(schemaName)).source.createConnection()) { connection =>
        connection.setAutoCommit(false)
        using(connection.prepareStatement(change.toString)) { statement =>
          statement.executeUpdate()
        } flatMap { _ =>
          val validationQuery = "SELECT * FROM " + (change match {
            case upd: Update => upd.getTable
            case ins: Insert => ins.getTable
            case del: Delete => del.getTable
          }).getName

          val res = using(connection.prepareStatement(validationQuery)) { statement =>
            SqlQueryResult.fromResultSet(statement.executeQuery())
          }

          connection.rollback()

          res
        }
      }.flatten
    case _ => Failure(null)
  }

}

object CreateDAO extends SqlExecutionDAO(3109) {

  override protected def executeQuery(schemaName: String, query: Statement): Try[SqlQueryResult] = Try(???)

}
