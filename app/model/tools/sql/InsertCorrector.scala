package model.tools.sql

import model.matching.StringMatcher
import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.expression.operators.relational.{ExpressionList, MultiExpressionList}
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.Statement
import net.sf.jsqlparser.statement.insert.Insert
import net.sf.jsqlparser.statement.select.SubSelect

import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}

object InsertCorrector extends ChangeCorrector("INSERT") {

  override type Q = Insert

  override protected val sqlExecutionDAO: SqlExecutionDAO[Insert] = InsertDAO

  private def expressionLists(query: Q): Seq[ExpressionList] = query.getItemsList match {
    case mel: MultiExpressionList => mel.getExpressionLists.asScala.toSeq
    case el: ExpressionList       => Seq(el)
    case _: SubSelect             => ???
  }

  /* FIXME: correct inserted values! */
  override protected def performAdditionalComparisons(userQuery: Insert, sampleQuery: Insert): AdditionalComparison = AdditionalComparison(
    None,
    Some(
      StringMatcher.doMatch(
        expressionLists(userQuery).map(_.toString),
        expressionLists(sampleQuery).map(_.toString)
      )
    )
  )

  override protected def getTables(query: Q): Seq[Table] = Seq(query.getTable)

  override protected def getWhere(query: Q): Option[Expression] = None

  override protected def checkStatement(statement: Statement): Try[Insert] = statement match {
    case q: Insert => Success(q)
    case other     => Failure(WrongStatementTypeException(queryType, gotten = other.getClass.getSimpleName))
  }

}
