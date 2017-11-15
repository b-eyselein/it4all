package model.sql

import model.Enums.SuccessType
import model.core.matching.{Match, MatchingResult}
import model.core.{CompleteResult, EvaluationResult}
import net.sf.jsqlparser.expression.{BinaryExpression, Expression}
import net.sf.jsqlparser.statement.select.OrderByElement

case class SqlResult(l: String,
                     columnComparison: MatchingResult[ColumnWrapper, ColumnMatch],
                     tableComparison: MatchingResult[String, Match[String]],
                     whereComparison: MatchingResult[BinaryExpression, BinaryExpressionMatch],

                     executionResult: Option[SqlExecutionResult],

                     groupByComparison: Option[MatchingResult[Expression, GroupByMatch]],
                     orderByComparison: Option[MatchingResult[OrderByElement, OrderByMatch]])
  extends CompleteResult[EvaluationResult](
    l, List(columnComparison, tableComparison, whereComparison) ++ executionResult ++ groupByComparison ++ orderByComparison) {

  def getMatchingResults: List[MatchingResult[_, _ <: Match[_]]] =
    List(columnComparison, tableComparison, whereComparison) ++ groupByComparison ++ orderByComparison

  def notEmptyMatchingResults: List[MatchingResult[_, _ <: Match[_]]] = getMatchingResults.filter(_.allMatches.nonEmpty)

}

case class SqlExecutionResult(userResult: SqlQueryResult, sampleResult: SqlQueryResult) extends EvaluationResult(SqlExecutionResult.analyze(userResult, sampleResult))

object SqlExecutionResult {
  def analyze(userResult: SqlQueryResult, sampleResult: SqlQueryResult): SuccessType =
    if (userResult == null || sampleResult == null)
      SuccessType.FAILURE
    else if (!userResult.isIdentic(sampleResult)) SuccessType.NONE else SuccessType.COMPLETE
}