package model.tools.sql

import model.matching.StringMatcher
import net.sf.jsqlparser.expression.{BinaryExpression, Expression}
import net.sf.jsqlparser.schema.{Column, Table}
import net.sf.jsqlparser.statement.Statement
import net.sf.jsqlparser.statement.select._

import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}

object SelectCorrector extends QueryCorrector("SELECT") {

  override type Q = Select

  def getColumns(select: Q): Seq[SelectItem] = select.getSelectBody match {
    case ps: PlainSelect => ps.getSelectItems.asScala.toSeq
    case _               => Seq.empty
  }

  private def columnWrappersFromPlainSelect(ps: PlainSelect): Seq[ColumnWrapper] = ps.getSelectItems.asScala.toSeq.map { column =>
    SelectColumnWrapper(
      columnName = column match {
        case _: AllColumns             => "*"
        case at: AllTableColumns       => at.toString
        case set: SelectExpressionItem => set.getExpression.toString
      },
      col = column
    )
  }

  override protected def getColumnWrappers(query: Q): Seq[ColumnWrapper] = query.getSelectBody match {
    case ps: PlainSelect => columnWrappersFromPlainSelect(ps)
    case _               => Seq.empty
  }

  private def asTable(fromItem: FromItem): Option[Table] = fromItem match {
    case t: Table => Some(t)
    case _        => None
  }

  override protected def getTables(query: Q): Seq[Table] = query.getSelectBody match {
    case plain: PlainSelect =>
      val mainTable = asTable(plain.getFromItem)

      val joinedTables: Seq[Table] = Option(plain.getJoins)
        .map { _.asScala.toSeq.flatMap { join => asTable(join.getRightItem) } }
        .getOrElse(Seq.empty)

      mainTable.toSeq ++ joinedTables
    case _ => Seq.empty
  }

  private def binaryExpressionFromExpression(expression: Expression): Option[BinaryExpression] = expression match {
    case be: BinaryExpression => Some(be)
    case _                    => None
  }

  private def joinExpressionsFromPlainSelect(ps: PlainSelect): Seq[BinaryExpression] = Option(ps.getJoins)
    .map { joins => joins.asScala.toSeq.flatMap { join => Option(join.getOnExpression).flatMap(binaryExpressionFromExpression) } }
    .getOrElse(Seq.empty)

  override protected def getJoinExpressions(query: Select): Seq[BinaryExpression] = query.getSelectBody match {
    case ps: PlainSelect => joinExpressionsFromPlainSelect(ps)
    case _               => Seq.empty
  }

  override protected def getWhere(select: Q): Option[Expression] = select.getSelectBody match {
    case ps: PlainSelect => Option(ps.getWhere)
    case _               => None
  }

  override def performAdditionalComparisons(userQuery: Select, sampleQuery: Select): AdditionalComparison = AdditionalComparison(
    Some(
      SelectAdditionalComparisons(
        StringMatcher.doMatch(groupByElements(userQuery), groupByElements(sampleQuery)),
        StringMatcher.doMatch(orderByElements(userQuery), orderByElements(sampleQuery)),
        StringMatcher.doMatch(limitElement(userQuery).map(_.toString).toSeq, limitElement(sampleQuery).map(_.toString).toSeq)
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
        .map(_.getGroupByExpressionList.getExpressions.asScala)
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
