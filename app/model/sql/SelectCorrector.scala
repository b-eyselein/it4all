package model.sql

import java.sql.Connection

import model.core.CommonUtils.using
import model.sql.ColumnWrapper.wrapColumn
import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.select.{OrderByElement, PlainSelect, Select, SelectItem}

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.language.postfixOps
import scala.util.Try


object SelectCorrector extends QueryCorrector("SELECT") {

  override type Q = net.sf.jsqlparser.statement.select.Select

  def executeStatement(select: String, conn: Connection): SqlQueryResult =
    using(conn.createStatement)(statement => new SqlQueryResult(statement.executeQuery(select)))

  def getColumns(select: Q): Seq[SelectItem] = select.getSelectBody match {
    case ps: PlainSelect => ps.getSelectItems.asScala
    case _               => Seq.empty
  }

  override protected def getColumnWrappers(query: Q): Seq[ColumnWrapper] = query.getSelectBody match {
    case ps: PlainSelect => ps.getSelectItems.asScala map wrapColumn
    case _               => Seq.empty
  }

  override protected def getTables(query: Q): Seq[Table] = query.getSelectBody match {
    case plain: PlainSelect =>

      val tables = plain.getFromItem match {
        case t: Table => Seq(t)
        case _        => Seq.empty
      }

      val joins: Seq[Table] = Option(plain.getJoins) match {
        case None       => Seq.empty
        case Some(join) => join.asScala map (_.getRightItem) flatMap {
          case t: Table => Some(t)
          case _        => None
        }
      }

      tables ++ joins
    case _                  => Seq.empty
  }

  override protected def getWhere(select: Q): Option[Expression] = select.getSelectBody match {
    case ps: PlainSelect => Option(ps.getWhere)
    case _               => None
  }

  override protected def compareGroupByElements(userQ: Q, sampleQ: Q) = Some(GroupByMatcher.doMatch(groupByElements(userQ), groupByElements(sampleQ)))

  override protected def compareOrderByElements(userQ: Q, sampleQ: Q) = Some(OrderByMatcher.doMatch(orderByElements(userQ), orderByElements(sampleQ)))

  def orderByElements(userQ: Q): Seq[OrderByElement] = userQ.getSelectBody match {
    case ps: PlainSelect => Option(ps.getOrderByElements) map (_.asScala) getOrElse Seq.empty
    case _               => Seq.empty
  }

  def groupByElements(query: Q): Seq[Expression] = query.getSelectBody match {
    case ps: PlainSelect => Option(ps.getGroupByColumnReferences) map (_.asScala) getOrElse Seq.empty
    case _               => Seq.empty
  }

  override protected def parseStatement(statement: String): Try[Select] = Try(
    CCJSqlParserUtil.parse(statement) match {
      case q: Select => q
      case _         => null // throw new CorrectionException(statement, s"Das Statement war vom falschen Typ! Erwartet wurde $queryType!")
    })

}
