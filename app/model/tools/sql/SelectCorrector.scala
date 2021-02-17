package model.tools.sql

import model.matching.StringMatcher
import model.tools.sql.matcher._
import net.sf.jsqlparser.expression.{BinaryExpression, Expression}
import net.sf.jsqlparser.schema.{Column, Table}
import net.sf.jsqlparser.statement.Statement
import net.sf.jsqlparser.statement.select._

import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}

object SelectCorrector extends QueryCorrector("SELECT") {

  override type Q = net.sf.jsqlparser.statement.select.Select

  def getColumns(select: Q): Seq[SelectItem] =
    select.getSelectBody match {
      case ps: PlainSelect => ps.getSelectItems.asScala.toSeq
      case _               => Seq[SelectItem]()
    }

  override protected def getColumnWrappers(query: Q): Seq[ColumnWrapper] = query.getSelectBody match {
    case ps: PlainSelect =>
      ps.getSelectItems.asScala.map { column =>
        val columnName = column match {
          case _: AllColumns             => "*"
          case at: AllTableColumns       => at.toString
          case set: SelectExpressionItem => set.getExpression.toString
        }

        SelectColumnWrapper(columnName, column)
      }.toSeq
    case _ => Seq[ColumnWrapper]()
  }

  override protected def getTables(query: Q): Seq[Table] = query.getSelectBody match {
    case plain: PlainSelect =>
      val mainTable: Option[Table] = plain.getFromItem match {
        case t: Table => Some(t)
        case _        => None
      }

      val joinedTables: Seq[Table] = Option(plain.getJoins).map(_.asScala) match {
        case None => Seq[Table]()
        case Some(joins) =>
          joins.flatMap { join =>
            join.getRightItem match {
              case t: Table => Some(t)
              case _        => None
            }
          }.toSeq
      }

      mainTable.toSeq ++ joinedTables
    case _ => Seq[Table]()
  }

  override protected def getJoinExpressions(query: Select): Seq[BinaryExpression] = query.getSelectBody match {
    case ps: PlainSelect =>
      Option(ps.getJoins).map(_.asScala) match {
        case None => Seq.empty
        case Some(joins) =>
          joins.flatMap { join =>
            Option(join.getOnExpression).flatMap {
              case be: BinaryExpression => Some(be)
              case _                    => None
            }
          }.toSeq
      }
    case _ => Seq.empty
  }

  override protected def getWhere(select: Q): Option[Expression] = select.getSelectBody match {
    case ps: PlainSelect => Option(ps.getWhere)
    case _               => None
  }

  override def performAdditionalComparisons(userQuery: Select, sampleQuery: Select): AdditionalComparison =
    AdditionalComparison(
      Some(
        SelectAdditionalComparisons(
          StringMatcher.doMatch(groupByElements(userQuery), groupByElements(sampleQuery)),
          StringMatcher.doMatch(orderByElements(userQuery), orderByElements(sampleQuery)),
          LimitMatcher.doMatch(limitElement(userQuery).toSeq, limitElement(sampleQuery).toSeq)
        )
      ),
      None
    )

  private def orderByElements(userQ: Q): Seq[String] = userQ.getSelectBody match {
    case ps: PlainSelect =>
      Option(ps.getOrderByElements)
        .map(_.asScala)
        .getOrElse(Seq.empty)
        .map(_.toString)
        .toSeq
    case _ => Seq.empty
  }

  private def groupByElements(query: Q): Seq[String] = query.getSelectBody match {
    case ps: PlainSelect =>
      Option(ps.getGroupBy)
        .map(_.getGroupByExpressions.asScala)
        .getOrElse(Seq.empty)
        .flatMap {
          case c: Column => Some(c.getColumnName)
          case _         => None
        }
        .toSeq
    case _ => Seq.empty
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
