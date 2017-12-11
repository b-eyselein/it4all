package model.sql

import model.Enums.SuccessType
import model.core.matching.{Match, MatchingResult}
import model.core.{CompleteResult, EvaluationResult}

abstract class SqlCorrResult(l: String, results: Seq[EvaluationResult]) extends CompleteResult[EvaluationResult](l, results)

case class SqlResult(l: String,
                     columnComparison: ColumnMatchingResult, tableComparison: TableMatchingResult,
                     whereComparison: BinaryExpressionMatchingResult, executionResult: Option[SqlExecutionResult],
                     groupByComparison: Option[GroupByMatchingResult], orderByComparison: Option[OrderByMatchingResult])

  extends SqlCorrResult(l, List(columnComparison, tableComparison, whereComparison) ++ executionResult ++ groupByComparison ++ orderByComparison) {

  def getMatchingResults: List[MatchingResult[_, _ <: Match[_]]] =
    List(columnComparison, tableComparison, whereComparison) ++ groupByComparison ++ orderByComparison

  def notEmptyMatchingResults: List[MatchingResult[_, _ <: Match[_]]] = getMatchingResults.filter(_.allMatches.nonEmpty)

}

case class SqlFailed(l: String) extends SqlCorrResult(l, Seq.empty)

case class SqlExecutionResult(userResult: SqlQueryResult, sampleResult: SqlQueryResult) extends EvaluationResult {

  override val success: SuccessType =
    if (userResult == null || sampleResult == null) SuccessType.ERROR
    else SuccessType.ofBool(userResult isIdentic sampleResult)

}
