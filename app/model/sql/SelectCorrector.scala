package model.sql

import java.sql.Connection

import model.core.CommonUtils.cleanly
import model.core.matching.MatchType.MatchType
import model.core.matching.{Match, MatchType, Matcher}
import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.schema.{Column, Table}
import net.sf.jsqlparser.statement.select.{OrderByElement, PlainSelect, Select, SelectItem}
import play.db.Database

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.util.{Failure, Success, Try}

case class GroupByMatch(ua: Option[Expression], sa: Option[Expression], s: Int)
  extends Match[Expression](ua, sa, s) {
  override def analyze(ua: Expression, sa: Expression): MatchType = MatchType.SUCCESSFUL_MATCH
}

case class OrderByMatch(ua: Option[OrderByElement], sa: Option[OrderByElement], s: Int)
  extends Match[OrderByElement](ua, sa, s) {
  override def analyze(ua: OrderByElement, sa: OrderByElement): MatchType = MatchType.SUCCESSFUL_MATCH
}

object GroupByMatcher extends Matcher[Expression, GroupByMatch](
  "Group By Elemente", List("Group By Statement"), _.asInstanceOf[Column].getColumnName == _.asInstanceOf[Column].getColumnName, GroupByMatch
)

object OrderByMatcher extends Matcher[OrderByElement, OrderByMatch](
  "Order By Elemente", List("Order By Statement"), _.getExpression.toString == _.getExpression.toString, OrderByMatch
)

object SelectCorrector extends QueryCorrector("SELECT") {

  override type Q = net.sf.jsqlparser.statement.select.Select

  def executeStatement(select: String, conn: Connection): SqlQueryResult =
    cleanly(conn.createStatement)(_.close)(q => new SqlQueryResult(q.executeQuery(select))) match {
      case Success(queryResult) => queryResult
      case Failure(_)           => null // throw new CorrectionException(select, s"Es gab einen Fehler bei der Ausfuehrung des Statements '$select'", e)
    }

  override protected def executeQuery(db: Database, userQ: Q, sampleQ: Q, exercise: SqlExercise): Try[SqlExecutionResult] =
    cleanly(db.getConnection)(_.close)(conn => {
      conn.setCatalog(exercise.scenario.shortName)
      val userResult = executeStatement(userQ.toString, conn)
      val sampleResult = executeStatement(sampleQ.toString, conn)
      new SqlExecutionResult(userResult, sampleResult)
    })

  def getColumns(select: Q): List[SelectItem] = select.getSelectBody.asInstanceOf[PlainSelect].getSelectItems.asScala.toList

  override protected def getColumnWrappers(query: Q): List[ColumnWrapper] =
    query.getSelectBody.asInstanceOf[PlainSelect].getSelectItems.asScala.map(ColumnWrapper.wrap).toList

  override protected def getTableNames(select: Q): List[String] = getTables(select).map(_.getName)

  override protected def getTables(query: Q): List[Table] = {
    val plain = query.getSelectBody.asInstanceOf[PlainSelect]

    val tables = plain.getFromItem match {
      case t: Table => List(t)
      case _        => List.empty
    }

    val joins = Option(plain.getJoins) match {
      case None       => List.empty
      case Some(join) => join.asScala.filter(_.getRightItem.isInstanceOf[Table]).map(_.getRightItem.asInstanceOf[Table])
    }

    tables ++ joins
  }

  override protected def getWhere(select: Q): Option[Expression] = Option(select.getSelectBody.asInstanceOf[PlainSelect].getWhere)

  override protected def compareGroupByElements(userQ: Q, sampleQ: Q) = Some(GroupByMatcher.doMatch(groupByElements(userQ), groupByElements(sampleQ)))

  override protected def compareOrderByElements(userQ: Q, sampleQ: Q) = Some(OrderByMatcher.doMatch(orderByElements(userQ), orderByElements(sampleQ)))

  def orderByElements(userQ: Q): List[OrderByElement] = {
    val javaOrderBys = userQ.getSelectBody.asInstanceOf[PlainSelect].getOrderByElements
    if (javaOrderBys == null) List.empty else javaOrderBys.asScala.toList
  }

  def groupByElements(query: Q): List[Expression] = Option(query.getSelectBody.asInstanceOf[PlainSelect].getGroupByColumnReferences) match {
    case None      => List.empty
    case Some(gbs) => gbs.asScala.toList
  }

  override protected def parseStatement(statement: String): Try[Select] = Try(
    CCJSqlParserUtil.parse(statement) match {
      case q: Select => q
      case _         => null // throw new CorrectionException(statement, s"Das Statement war vom falschen Typ! Erwartet wurde $queryType!")
    })

}
