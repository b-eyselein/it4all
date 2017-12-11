package model.sql

import model.Enums.SuccessType
import model.core.matching.{Match, MatchingResult}
import model.core.{CompleteResult, EvaluationResult}

import scala.util.{Failure, Success, Try}

abstract class SqlCorrResult(l: String, results: Seq[EvaluationResult]) extends CompleteResult[EvaluationResult](l, results)

case class SqlResult(l: String, columnComparison: ColumnMatchingResult, tableComparison: TableMatchingResult,
                     whereComparison: BinaryExpressionMatchingResult, executionResult: SqlExecutionResult,
                     groupByComparison: Option[GroupByMatchingResult], orderByComparison: Option[OrderByMatchingResult])
  extends SqlCorrResult(l, List(columnComparison, tableComparison, whereComparison, executionResult) ++ groupByComparison ++ orderByComparison) {

  def getMatchingResults: List[MatchingResult[_, _ <: Match[_]]] =
    List(columnComparison, tableComparison, whereComparison) ++ groupByComparison ++ orderByComparison

  def notEmptyMatchingResults: List[MatchingResult[_, _ <: Match[_]]] = getMatchingResults filter (_.allMatches.nonEmpty)

}

case class SqlFailed(l: String) extends SqlCorrResult(l, Seq.empty)

case class SqlExecutionResult(userResultTry: Try[SqlQueryResult], sampleResultTry: Try[SqlQueryResult]) extends EvaluationResult {

  override val success: SuccessType = (userResultTry, sampleResultTry) match {
    case (Success(userResult), Success(sampleResult)) => SuccessType.ofBool(userResult isIdentic sampleResult)

    case (Failure(userError), Success(sampleResult)) => SuccessType.ERROR

    case (Success(userResult), Failure(sampleError)) => SuccessType.PARTIALLY

    case (Failure(userError), Failure(sampleError)) => SuccessType.ERROR
  }

}
