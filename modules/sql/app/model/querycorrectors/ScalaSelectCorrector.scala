package model.querycorrectors

import java.sql.Connection
import scala.util.Failure
import scala.util.Success
import scala.util.Try
import model.CorrectionException
import model.ScalaUtils.cleanly
import model.exercise.SqlExercise
import model.matching.ScalaMatch
import model.matching.ScalaMatcher
import model.querycorrectors.ColumnWrapper
import model.querycorrectors.ScalaQueryCorrector
import model.querycorrectors.SqlExecutionResult
import model.sql.SqlQueryResult
import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.schema.Column
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.select.OrderByElement
import net.sf.jsqlparser.statement.select.PlainSelect
import net.sf.jsqlparser.statement.select.SelectItem
import play.db.Database
import model.matching.ScalaMatchingResult

object ScalaSelectCorrector extends ScalaQueryCorrector("SELECT") {

  override type Q = net.sf.jsqlparser.statement.select.Select

  case class ScalaGroupByMatch(userArg: Option[Expression], sampleArg: Option[Expression]) extends ScalaMatch[Expression](userArg, sampleArg)
  case class ScalaOrderByMatch(userArg: Option[OrderByElement], sampleArg: Option[OrderByElement]) extends ScalaMatch[OrderByElement](userArg, sampleArg)

  val GROUP_BY_MATCHER = new ScalaMatcher[Expression, ScalaGroupByMatch]("Group By Elemente",
    _.asInstanceOf[Column].getColumnName == _.asInstanceOf[Column].getColumnName,
    new ScalaGroupByMatch(_, _))

  val ORDER_BY_MATCHER = new ScalaMatcher[OrderByElement, ScalaOrderByMatch]("Order By Elemente",
    _.getExpression.toString == _.getExpression.toString,
    new ScalaOrderByMatch(_, _))

  def executeStatement(select: String, conn: Connection) =
    cleanly(conn.createStatement)(_.close)(q => new SqlQueryResult(q.executeQuery(select))) match {
      case Success(queryResult) => queryResult
      case Failure(e) => throw new CorrectionException(select, s"Es gab einen Fehler bei der Ausführung des Statements '$select'", e)
    }

  override def executeQuery(db: Database, userQ: Q, sampleQ: Q, exercise: SqlExercise): Try[SqlExecutionResult] =
    cleanly(db.getConnection)(_.close)(conn => {
      conn.setCatalog(exercise.scenario.getShortName)
      val userResult = executeStatement(userQ.toString, conn)
      val sampleResult = executeStatement(sampleQ.toString, conn)
      new SqlExecutionResult(userResult, sampleResult)
    })

  def getColumns(select: Q): List[SelectItem] = select.getSelectBody.asInstanceOf[PlainSelect].getSelectItems.asScala.toList

  override def getColumnWrappers(query: Q): List[ColumnWrapper] =
    query.getSelectBody.asInstanceOf[PlainSelect].getSelectItems.asScala.map(ColumnWrapper.wrap).toList

  override def getTableNames(select: Q): List[String] = getTables(select).map(_.getName)

  override def getTables(query: Q): List[Table] = {
    val plain = query.getSelectBody.asInstanceOf[PlainSelect]

    val tables = if (plain.getFromItem.isInstanceOf[Table]) List(plain.getFromItem.asInstanceOf[Table]) else List.empty

    val joins = if (plain.getJoins == null) List.empty else
      plain.getJoins.asScala.filter(_.getRightItem.isInstanceOf[Table]).map(_.getRightItem.asInstanceOf[Table])

    (tables ++ joins).toList
  }

  override def getWhere(select: Q) = select.getSelectBody.asInstanceOf[PlainSelect].getWhere

  override def makeOtherComparisons(userQ: Q, sampleQ: Q): List[ScalaMatchingResult[_, _ <: ScalaMatch[_]]] =
    List(compareGroupByElements(userQ, sampleQ), compareOrderByElements(userQ, sampleQ))

  def compareGroupByElements(plainUserQuery: Q, plainSampleQuery: Q): ScalaMatchingResult[Expression, ScalaGroupByMatch] = {
    val group1 = plainUserQuery.getSelectBody.asInstanceOf[PlainSelect].getGroupByColumnReferences.asScala.toList
    val group2 = plainSampleQuery.getSelectBody.asInstanceOf[PlainSelect].getGroupByColumnReferences.asScala.toList

    GROUP_BY_MATCHER.doMatch(if (group1 == null) List.empty else group1, if (group2 == null) List.empty else group2)
  }

  def compareOrderByElements(plainUserQuery: Q, plainSampleQuery: Q): ScalaMatchingResult[OrderByElement, ScalaOrderByMatch] = {
    val order1 = plainUserQuery.getSelectBody.asInstanceOf[PlainSelect].getOrderByElements.asScala.toList
    val order2 = plainSampleQuery.getSelectBody.asInstanceOf[PlainSelect].getOrderByElements.asScala.toList

    ORDER_BY_MATCHER.doMatch(if (order1 == null) List.empty else order1, if (order2 == null) List.empty else order2)
  }
}
