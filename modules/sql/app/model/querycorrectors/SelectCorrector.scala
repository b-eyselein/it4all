package model.querycorrectors

import java.sql.Connection

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.util.{ Failure, Success, Try }

import model.CommonUtils.cleanly
import model.CorrectionException
import model.exercise.SqlExercise
import model.matching.{ Match, MatchType, Matcher }
import model.sql.SqlQueryResult
import net.sf.jsqlparser.JSQLParserException
import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.schema.{ Column, Table }
import net.sf.jsqlparser.statement.select.{ OrderByElement, PlainSelect }
import play.db.Database

case class GroupByMatch(ua: Option[Expression], sa: Option[Expression], s: Int)
  extends Match[Expression](ua, sa, s) {
  override def analyze(ua: Expression, sa: Expression) = MatchType.SUCCESSFUL_MATCH
}

case class OrderByMatch(ua: Option[OrderByElement], sa: Option[OrderByElement], s: Int)
  extends Match[OrderByElement](ua, sa, s) {
  override def analyze(ua: OrderByElement, sa: OrderByElement) = MatchType.SUCCESSFUL_MATCH
}

object GROUP_BY_MATCHER extends Matcher[Expression, GroupByMatch](
  "Group By Elemente",
  List("Group By Statement"),
  _.asInstanceOf[Column].getColumnName == _.asInstanceOf[Column].getColumnName,
  new GroupByMatch(_, _, _)
)

object ORDER_BY_MATCHER extends Matcher[OrderByElement, OrderByMatch](
  "Order By Elemente",
  List("Order By Statement"),
  _.getExpression.toString == _.getExpression.toString,
  new OrderByMatch(_, _, _)
)

object SelectCorrector extends QueryCorrector("SELECT") {

  type Q = net.sf.jsqlparser.statement.select.Select

  def executeStatement(select: String, conn: Connection) =
    cleanly(conn.createStatement)(_.close)(q => new SqlQueryResult(q.executeQuery(select))) match {
      case Success(queryResult) => queryResult
      case Failure(e)           => throw new CorrectionException(select, s"Es gab einen Fehler bei der Ausfuehrung des Statements '$select'", e)
    }

  override def executeQuery(db: Database, userQ: Q, sampleQ: Q, exercise: SqlExercise): Try[SqlExecutionResult] =
    cleanly(db.getConnection)(_.close)(conn => {
      conn.setCatalog(exercise.scenario.shortName)
      new SqlExecutionResult(executeStatement(userQ.toString, conn), executeStatement(sampleQ.toString, conn))
    })

  def getColumns(select: Q) = select.getSelectBody.asInstanceOf[PlainSelect].getSelectItems.asScala.toList

  override def getColumnWrappers(query: Q): List[ColumnWrapper] =
    query.getSelectBody.asInstanceOf[PlainSelect].getSelectItems.asScala.map(ColumnWrapper.wrap(_)).toList

  override def getTableNames(select: Q) = getTables(select).map(_.getName)

  override def getTables(query: Q) = {
    val plain = query.getSelectBody.asInstanceOf[PlainSelect]

    val tables = if (plain.getFromItem.isInstanceOf[Table]) List(plain.getFromItem.asInstanceOf[Table]) else List.empty

    val joins = if (plain.getJoins == null) List.empty else
      plain.getJoins.asScala.filter(_.getRightItem.isInstanceOf[Table]).map(_.getRightItem.asInstanceOf[Table])

    (tables ++ joins).toList
  }

  override def getWhere(select: Q) = select.getSelectBody.asInstanceOf[PlainSelect].getWhere

  override def compareGroupByElements(userQ: Q, sampleQ: Q) = Some(GROUP_BY_MATCHER.doMatch(groupByElements(userQ), groupByElements(sampleQ)))

  override def compareOrderByElements(userQ: Q, sampleQ: Q) = Some(ORDER_BY_MATCHER.doMatch(orderByElements(userQ), orderByElements(sampleQ)))

  def orderByElements(userQ: Q) = {
    val javaOrderBys = userQ.getSelectBody.asInstanceOf[PlainSelect].getOrderByElements
    if (javaOrderBys == null) List.empty else javaOrderBys.asScala.toList
  }

  def groupByElements(query: Q) = {
    val javaGroupBys = query.getSelectBody.asInstanceOf[PlainSelect].getGroupByColumnReferences
    if (javaGroupBys == null) List.empty else javaGroupBys.asScala.toList
  }

  def parseStatement(statement: String) = try {
    CCJSqlParserUtil.parse(statement) match {
      case q: Q => q
      case o    => throw new CorrectionException(statement, s"Das Statement war vom falschen Typ ${o.getClass}! Erwartet wurde ein $queryType - Statement!")
    }
  } catch {
    case e: JSQLParserException => throw new CorrectionException(statement, "Es gab einen Fehler beim Parsen des Statements: " + statement, e)
  }

}
