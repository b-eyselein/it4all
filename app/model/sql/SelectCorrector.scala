package model.sql

import model.core.matching.{Match, MatchingResult}
import model.sql.ColumnWrapper.wrapColumn
import model.sql.matcher._
import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.Statement
import net.sf.jsqlparser.statement.select._

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}


object SelectCorrector extends QueryCorrector("SELECT") {

  override type Q = net.sf.jsqlparser.statement.select.Select

  def getColumns(select: Q): Seq[SelectItem] = select.getSelectBody match {
    case ps: PlainSelect => ps.getSelectItems.asScala
    case _               => Seq[SelectItem]()
  }

  override protected def getColumnWrappers(query: Q): Seq[ColumnWrapper] = query.getSelectBody match {
    case ps: PlainSelect => ps.getSelectItems.asScala map wrapColumn
    case _               => Seq[ColumnWrapper]()
  }

  override protected def getTables(query: Q): Seq[Table] = query.getSelectBody match {
    case plain: PlainSelect =>

      val tables: Seq[Table] = plain.getFromItem match {
        case t: Table => Seq(t)
        case _        => Seq[Table]()
      }

      val joinedTables: Seq[Table] = Option(plain.getJoins) match {
        case None       => Seq[Table]()
        case Some(join) => join.asScala map (_.getRightItem) flatMap {
          case t: Table => Some(t)
          case _        => None
        }
      }

      tables ++ joinedTables
    case _                  => Seq[Table]()
  }

  override protected def getWhere(select: Q): Option[Expression] = select.getSelectBody match {
    case ps: PlainSelect => Option(ps.getWhere)
    case _               => None
  }

  override def performAdditionalComparisons(userQuery: Select, sampleQuery: Select): Seq[MatchingResult[_ <: Match]] = Seq(
    compareGroupByElements(userQuery, sampleQuery),
    compareOrderByElements(userQuery, sampleQuery),
    compareLimitElement(userQuery, sampleQuery)
  )

  private def compareGroupByElements(userQ: Q, sampleQ: Q): MatchingResult[GroupByMatch] =
    GroupByMatcher.doMatch(groupByElements(userQ), groupByElements(sampleQ))

  private def compareOrderByElements(userQ: Q, sampleQ: Q): MatchingResult[OrderByMatch] =
    OrderByMatcher.doMatch(orderByElements(userQ), orderByElements(sampleQ))

  private def compareLimitElement(userQ: Q, sampleQ: Q): MatchingResult[LimitMatch] = {
    val maybeUserLimit: Option[Limit] = limitElement(userQ)
    val maybeSampleLimit = limitElement(sampleQ)

    LimitMatcher.doMatch(maybeUserLimit.toSeq, maybeSampleLimit.toSeq)
  }

  private def orderByElements(userQ: Q): Seq[OrderByElement] = userQ.getSelectBody match {
    case ps: PlainSelect => Option(ps.getOrderByElements) map (_.asScala) getOrElse Seq[OrderByElement]()
    case _               => Seq[OrderByElement]()
  }

  private def groupByElements(query: Q): Seq[Expression] = query.getSelectBody match {
    case ps: PlainSelect => Option(ps.getGroupByColumnReferences) map (_.asScala) getOrElse Seq[Expression]()
    case _               => Seq[Expression]()
  }

  private def limitElement(query: Q): Option[Limit] = query.getSelectBody match {
    case ps: PlainSelect => Option(ps.getLimit)
    case _               => None
  }

  override protected def checkStatement(statement: Statement): Try[Select] = statement match {
    case q: Select        => Success(q)
    case other: Statement => Failure(WrongStatementTypeException(queryType, gotten = other.getClass.getSimpleName))
  }

}
