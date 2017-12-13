package model.sql

import model.Enums.SuccessType
import model.core.matching.{Match, MatchingResult}
import model.core.{CompleteResult, EvaluationResult}
import play.twirl.api.Html

import scala.util.{Failure, Success, Try}

abstract class SqlCorrResult extends CompleteResult[EvaluationResult] {

  override type SolType = String

}

case class SqlResult(learnerSolution: String, columnComparison: ColumnMatchingResult, tableComparison: TableMatchingResult,
                     whereComparison: BinaryExpressionMatchingResult, executionResult: SqlExecutionResult,
                     groupByComparison: Option[GroupByMatchingResult], orderByComparison: Option[OrderByMatchingResult])
  extends SqlCorrResult {

  override def results: Seq[EvaluationResult] = Seq(columnComparison, tableComparison, whereComparison, executionResult) ++ groupByComparison ++ orderByComparison

  private def matchingResults: Seq[MatchingResult[_, _ <: Match[_]]] =
    Seq(columnComparison, tableComparison, whereComparison) ++ groupByComparison ++ orderByComparison

  def notEmptyMatchingResults: Seq[MatchingResult[_, _ <: Match[_]]] = matchingResults filter (_.allMatches.nonEmpty)

  override def renderLearnerSolution: Html = new Html(s"<pre>$learnerSolution</pre>")

}

case class SqlFailed(learnerSolution: String) extends SqlCorrResult {

  override val results: Seq[EvaluationResult] = Seq.empty

  override def renderLearnerSolution: Html = new Html(s"<pre>$learnerSolution</pre>")

}

case class SqlExecutionResult(userResultTry: Try[SqlQueryResult], sampleResultTry: Try[SqlQueryResult]) extends EvaluationResult {

  override val success: SuccessType = (userResultTry, sampleResultTry) match {
    case (Success(userResult), Success(sampleResult)) => SuccessType.ofBool(userResult isIdentic sampleResult)

    case (Failure(_), Success(_)) => SuccessType.ERROR

    case (Success(_), Failure(_)) => SuccessType.PARTIALLY

    case (Failure(_), Failure(_)) => SuccessType.ERROR
  }


}
