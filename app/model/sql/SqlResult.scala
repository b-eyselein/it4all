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

  private def matchingResults: List[MatchingResult[_, _ <: Match[_]]] =
    List(columnComparison, tableComparison, whereComparison) ++ groupByComparison ++ orderByComparison

  def notEmptyMatchingResults: List[MatchingResult[_, _ <: Match[_]]] = matchingResults filter (_.allMatches.nonEmpty)

}

case class SqlFailed(l: String) extends SqlCorrResult(l, Seq.empty)

case class SqlExecutionResult(userResultTry: Try[SqlQueryResult], sampleResultTry: Try[SqlQueryResult]) extends EvaluationResult {

  override val success: SuccessType = (userResultTry, sampleResultTry) match {
    case (Success(userResult), Success(sampleResult)) => SuccessType.ofBool(userResult isIdentic sampleResult)

    case (Failure(_), Success(_)) => SuccessType.ERROR

    case (Success(_), Failure(_)) => SuccessType.PARTIALLY

    case (Failure(_), Failure(_)) => SuccessType.ERROR
  }


}
