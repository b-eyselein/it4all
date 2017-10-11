package model.querycorrectors

import java.sql.Connection

import model.CommonUtils.cleanly
import model.exercise.SqlExercise
import model.matching.MatchingResult
import model.sql.SqlQueryResult
import model.{CorrectionException, StringConsts}
import net.sf.jsqlparser.JSQLParserException
import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.select.OrderByElement
import play.db.Database

import scala.collection.JavaConverters._
import scala.util.Try

abstract class ChangeCorrector(queryType: String) extends QueryCorrector(queryType) {

  def runUpdate(conn: Connection, query: String): Try[Int] = cleanly(conn.createStatement)(_.close)(_.executeUpdate(query))

  def runValidationQuery(conn: Connection, query: String): Try[SqlQueryResult] =
    cleanly(conn.createStatement)(_.close)(s => new SqlQueryResult(s.executeQuery(query)))

  def getResultSet(statement: Q, connection: Connection, validation: String): Try[SqlQueryResult] = {
    runUpdate(connection, statement.toString)
    val result = runValidationQuery(connection, validation)
    connection.rollback()
    result
  }

  override def executeQuery(db: Database, userQ: Q, sampleQ: Q, exercise: SqlExercise): Try[SqlExecutionResult] =
    cleanly(db.getConnection)(_.close)(connection => {
      connection.setCatalog(exercise.scenario.shortName)
      connection.setAutoCommit(false)

      val validation = StringConsts.SELECT_ALL_DUMMY + getTableNames(sampleQ).head

      new SqlExecutionResult(getResultSet(userQ, connection, validation).get, getResultSet(sampleQ, connection, validation).get)
    })

  override def compareGroupByElements(plainUserQuery: Q, plainSampleQuery: Q): Option[MatchingResult[Expression, GroupByMatch]] = None

  override def compareOrderByElements(plainUserQuery: Q, plainSampleQuery: Q): Option[MatchingResult[OrderByElement, OrderByMatch]] = None
}

object InsertCorrector extends ChangeCorrector("INSERT") {

  import net.sf.jsqlparser.statement.insert.Insert

  type Q = Insert

  override def getColumnWrappers(query: Q): List[ColumnWrapper] = List.empty

  override def getTableNames(query: Q) = List(query.getTable.getName)

  override def getTables(query: Q) = List(query.getTable)

  override def getWhere(query: Q): Expression = null

  def parseStatement(statement: String): Insert = try {
    val parsed = CCJSqlParserUtil.parse(statement)
    parsed match {
      case q: Q => q
      case _ => throw new CorrectionException(statement, "Das Statement war vom falschen Typ! Erwartet wurde " + queryType + "!")
    }
  } catch {
    case e: JSQLParserException => throw new CorrectionException(statement, "Es gab einen Fehler beim Parsen des Statements: " + statement, e)
  }

}

object DeleteCorrector extends ChangeCorrector("DELETE") {

  import net.sf.jsqlparser.statement.delete.Delete

  type Q = Delete

  override def getColumnWrappers(query: Q): List[ColumnWrapper] = List.empty

  override def getTableNames(query: Q) = List(query.getTable.getName)

  override def getTables(query: Q): List[Table] = query.getTables.asScala.toList

  override def getWhere(query: Q): Expression = query.getWhere

  def parseStatement(statement: String): Delete = try {
    val parsed = CCJSqlParserUtil.parse(statement)
    parsed match {
      case q: Q => q
      case _ => throw new CorrectionException(statement, "Das Statement war vom falschen Typ! Erwartet wurde " + queryType + "!")
    }
  } catch {
    case e: JSQLParserException => throw new CorrectionException(statement, "Es gab einen Fehler beim Parsen des Statements: " + statement, e)
  }

}

object UpdateCorrector extends ChangeCorrector("UPDATE") {

  import net.sf.jsqlparser.statement.update.Update

  type Q = Update

  override def getColumnWrappers(query: Q): List[ColumnWrapper] = query.getColumns.asScala.map(ColumnWrapper.wrap).toList

  override def getTableNames(query: Q): List[String] = query.getTables.asScala.map(_.getName).toList

  override def getTables(query: Q): List[Table] = query.getTables.asScala.toList

  override def getWhere(query: Q): Expression = query.getWhere

  def parseStatement(statement: String): Update = try {
    val parsed = CCJSqlParserUtil.parse(statement)
    parsed match {
      case q: Q => q
      case _ => throw new CorrectionException(statement, "Das Statement war vom falschen Typ! Erwartet wurde " + queryType + "!")
    }
  } catch {
    case e: JSQLParserException => throw new CorrectionException(statement, "Es gab einen Fehler beim Parsen des Statements: " + statement, e)
  }

}
