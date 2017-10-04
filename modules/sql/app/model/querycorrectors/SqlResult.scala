package model.querycorrectors

import scala.collection.JavaConverters.seqAsJavaListConverter

import model.conditioncorrector.BinaryExpressionMatch
import model.exercise.Success
import model.matching.{ Match, MatchingResult }
import model.result.{ CompleteResult, EvaluationResult }
import model.sql.SqlQueryResult
import net.sf.jsqlparser.expression.{ BinaryExpression, Expression }
import net.sf.jsqlparser.statement.select.OrderByElement

case class SqlResult(
  learnerSolution:  String,
  columnComparison: MatchingResult[ColumnWrapper, ColumnMatch],
  tableComparison:  MatchingResult[String, Match[String]],
  whereComparison:  MatchingResult[BinaryExpression, BinaryExpressionMatch],

  executionResult: SqlExecutionResult,

  groupByComparison: Option[MatchingResult[Expression, GroupByMatch]],
  orderByComparison: Option[MatchingResult[OrderByElement, OrderByMatch]])
  extends CompleteResult[EvaluationResult](
    "Sql",
    (List(columnComparison, tableComparison, whereComparison, executionResult)
      ++ groupByComparison
      ++ orderByComparison).asJava) {

  def getMatchingResults: List[MatchingResult[_, _ <: Match[_]]] =
    List(columnComparison, tableComparison, whereComparison) ++ groupByComparison ++ orderByComparison

  def notEmptyMatchingResults = getMatchingResults.filter(!_.allMatches.isEmpty)

}

case class SqlExecutionResult(userResult: SqlQueryResult, sampleResult: SqlQueryResult) extends EvaluationResult(SqlExecutionResult.analyze(userResult, sampleResult))

object SqlExecutionResult {
  def analyze(userResult: SqlQueryResult, sampleResult: SqlQueryResult) =
    if (userResult == null || sampleResult == null)
      Success.FAILURE
    else if (!userResult.isIdentic(sampleResult)) Success.NONE else Success.COMPLETE
}