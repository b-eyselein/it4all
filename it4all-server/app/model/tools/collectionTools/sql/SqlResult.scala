package model.tools.collectionTools.sql

import model.core.matching.{GenericAnalysisResult, Match, MatchingResult}
import model.core.result.{CompleteResult, EvaluationResult, SuccessType}
import model.points._
import model.tools.collectionTools.sql.matcher._
import net.sf.jsqlparser.expression.BinaryExpression
import net.sf.jsqlparser.schema.Table

import scala.util.{Failure, Success, Try}

final case class WrongStatementTypeException(awaited: String, gotten: String) extends Exception(s"Wrong type of statement! Expected '$awaited', bot got '$gotten'")

class SqlStatementException(cause: Throwable) extends Exception(cause) {

  override def getMessage: String = {

    @annotation.tailrec
    def go(cause: Throwable): String = {
      if (Option(cause.getMessage).isDefined) cause.getMessage
      else if (Option(cause.getCause).isEmpty) ""
      else go(cause.getCause)
    }

    go(cause)
  }

}

abstract class SqlCorrResult extends CompleteResult[EvaluationResult] {

  val successType: SuccessType

}

@deprecated
final case class SqlQueriesStaticComparison[Q](
  userQ: Q, sampleQ: Q,
  columnComparison: MatchingResult[ColumnWrapper, GenericAnalysisResult, ColumnMatch],
  tableComparison: MatchingResult[Table, GenericAnalysisResult, TableMatch],
  joinExpressionComparison: MatchingResult[BinaryExpression, GenericAnalysisResult, BinaryExpressionMatch],
  whereComparison: MatchingResult[BinaryExpression, GenericAnalysisResult, BinaryExpressionMatch],
  additionalComparisons: Seq[MatchingResult[_, _, _ <: Match[_, _]]],
) {

  val points: Points = columnComparison.points +
    tableComparison.points +
    joinExpressionComparison.points +
    whereComparison.points +
    additionalComparisons.map(_.points).fold(zeroPoints)(_ + _)

  val maxPoints: Points = columnComparison.maxPoints +
    tableComparison.maxPoints +
    joinExpressionComparison.maxPoints +
    whereComparison.maxPoints +
    additionalComparisons.map(_.maxPoints).fold(zeroPoints)(_ + _)

}

// FIXME: use builder?
final case class SqlResult(
  columnComparison: MatchingResult[ColumnWrapper, GenericAnalysisResult, ColumnMatch],
  tableComparison: MatchingResult[Table, GenericAnalysisResult, TableMatch],
  joinExpressionComparison: MatchingResult[BinaryExpression, GenericAnalysisResult, BinaryExpressionMatch],
  whereComparison: MatchingResult[BinaryExpression, GenericAnalysisResult, BinaryExpressionMatch],
  additionalComparisons: Seq[MatchingResult[_, _, _ <: Match[_, _]]],
  executionResult: SqlExecutionResult,
  solutionSaved: Boolean
) extends SqlCorrResult {

  override def results: Seq[EvaluationResult] = Seq(columnComparison, tableComparison, whereComparison, executionResult) ++ additionalComparisons

  override val successType: SuccessType = if (results.nonEmpty && results.forall(_.success == SuccessType.COMPLETE)) SuccessType.COMPLETE else SuccessType.PARTIALLY

  override val points: Points = columnComparison.points +
    tableComparison.points +
    joinExpressionComparison.points +
    whereComparison.points +
    additionalComparisons.map(_.points).fold(zeroPoints)(_ + _)

  // FIXME: points for executionResult?

  override val maxPoints: Points = columnComparison.maxPoints +
    tableComparison.maxPoints +
    joinExpressionComparison.maxPoints +
    whereComparison.maxPoints +
    additionalComparisons.map(_.maxPoints).fold(zeroPoints)(_ + _)

}

final case class SqlParseFailed(error: Throwable, maxPoints: Points, solutionSaved: Boolean) extends SqlCorrResult {

  override def results: Seq[EvaluationResult] = Seq[EvaluationResult]()

  override val successType: SuccessType = SuccessType.ERROR

  override val points: Points = zeroPoints

}

final case class SqlExecutionResult(userResultTry: Try[SqlQueryResult], sampleResultTry: Try[SqlQueryResult]) extends EvaluationResult {

  override val success: SuccessType = userResultTry match {
    case Failure(_)          => SuccessType.ERROR
    case Success(userResult) => sampleResultTry match {
      case Failure(_)            => SuccessType.PARTIALLY
      case Success(sampleResult) => SuccessType.ofBool(userResult isIdentic sampleResult)
    }
  }

}
