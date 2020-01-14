package model.tools.collectionTools.sql

import model.tools.collectionTools.sql.ColumnWrapper.wrapColumn
import model.tools.collectionTools.sql.matcher._
import net.sf.jsqlparser.expression.{BinaryExpression, Expression}
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.Statement
import net.sf.jsqlparser.statement.select._

import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}


object SelectCorrector extends QueryCorrector("SELECT") {

  override type Q = net.sf.jsqlparser.statement.select.Select

  def getColumns(select: Q): Seq[SelectItem] = select.getSelectBody match {
    case ps: PlainSelect => ps.getSelectItems.asScala.toSeq
    case _               => Seq[SelectItem]()
  }

  override protected def getColumnWrappers(query: Q): Seq[ColumnWrapper] = query.getSelectBody match {
    case ps: PlainSelect => ps.getSelectItems.asScala.map(wrapColumn).toSeq
    case _               => Seq[ColumnWrapper]()
  }

  override protected def getTables(query: Q): Seq[Table] = query.getSelectBody match {
    case plain: PlainSelect =>

      val mainTable: Option[Table] = plain.getFromItem match {
        case t: Table => Some(t)
        case _        => None
      }

      val joinedTables: Seq[Table] = Option(plain.getJoins).map(_.asScala) match {
        case None                   => Seq[Table]()
        case Some(joins: Seq[Join]) =>
          joins.flatMap { join =>
            join.getRightItem match {
              case t: Table => Some(t)
              case _        => None
            }
          }.toSeq
      }

      mainTable.toSeq ++ joinedTables
    case _                  => Seq[Table]()
  }

  override protected def getJoinExpressions(query: Select): Seq[BinaryExpression] = query.getSelectBody match {
    case ps: PlainSelect =>
      Option(ps.getJoins).map(_.asScala) match {
        case None                   => Seq.empty
        case Some(joins: Seq[Join]) =>
          joins.flatMap { join =>
            Option(join.getOnExpression) flatMap {
              case be: BinaryExpression => Some(be)
              case _                    => None
            }
          }.toSeq
      }
    case _               => Seq.empty
  }

  override protected def getWhere(select: Q): Option[Expression] = select.getSelectBody match {
    case ps: PlainSelect => Option(ps.getWhere)
    case _               => None
  }

  override def performAdditionalComparisons(userQuery: Select, sampleQuery: Select): AdditionalComparison = AdditionalComparison(
    Some(
      SelectAdditionalComparisons(
        GroupByMatcher.doMatch(groupByElements(userQuery), groupByElements(sampleQuery)),
        OrderByMatcher.doMatch(orderByElements(userQuery), orderByElements(sampleQuery)),
        compareLimitElement(userQuery, sampleQuery),
      )
    ), None
  )


  private def compareLimitElement(userQ: Q, sampleQ: Q) = {
    val maybeUserLimit: Option[Limit] = limitElement(userQ)
    val maybeSampleLimit              = limitElement(sampleQ)

    LimitMatcher.doMatch(maybeUserLimit.toSeq, maybeSampleLimit.toSeq)
  }

  private def orderByElements(userQ: Q): Seq[OrderByElement] = userQ.getSelectBody match {
    case ps: PlainSelect => Option(ps.getOrderByElements).map(_.asScala).getOrElse(Seq[OrderByElement]()).toSeq
    case _               => Seq[OrderByElement]()
  }

  private def groupByElements(query: Q): Seq[Expression] = query.getSelectBody match {
    case ps: PlainSelect => Option(ps.getGroupBy).map(_.getGroupByExpressions.asScala).getOrElse(Seq[Expression]()).toSeq
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
