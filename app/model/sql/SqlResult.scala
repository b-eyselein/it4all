package model.sql

import model._
import model.core.matching.{Match, MatchingResult}
import model.core.result.{CompleteResult, EvaluationResult, SuccessType}
import model.sql.SqlConsts._
import model.sql.matcher._
import play.api.libs.json._

import scala.language.postfixOps
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

  override type SolType = String

  val successType: SuccessType

  override def toJson(solutionSaved: Boolean): JsValue = Json.obj(
    solutionSavedName -> solutionSaved,
    successName -> successType.entryName,
    resultsName -> jsonRest
  )

  def jsonRest: JsValue

}

final case class SqlQueriesStaticComparison[Q](userQ: Q, sampleQ: Q,
                                               columnComparison: MatchingResult[ColumnMatch],
                                               tableComparison: MatchingResult[TableMatch],
                                               whereComparison: MatchingResult[BinaryExpressionMatch],
                                               additionalComparisons: Seq[MatchingResult[_ <: Match]]) {

  val points: Points = columnComparison.points +
    tableComparison.points +
    whereComparison.points +
    additionalComparisons.map(_.points).fold(0 points)(_ + _)

  val maxPoints: Points = columnComparison.maxPoints +
    tableComparison.maxPoints +
    whereComparison.maxPoints +
    additionalComparisons.map(_.maxPoints).fold(0 points)(_ + _)

}

// FIXME: use builder?
final case class SqlResult(learnerSolution: String,
                           columnComparison: MatchingResult[ColumnMatch],
                           tableComparison: MatchingResult[TableMatch],
                           whereComparison: MatchingResult[BinaryExpressionMatch],
                           additionalComparisons: Seq[MatchingResult[_ <: Match]],
                           executionResult: SqlExecutionResult) extends SqlCorrResult {

  override def results: Seq[EvaluationResult] = Seq(columnComparison, tableComparison, whereComparison, executionResult) ++ additionalComparisons

  override val successType: SuccessType = if (EvaluationResult.allResultsSuccessful(results)) SuccessType.COMPLETE else SuccessType.PARTIALLY

  override val points: Points = columnComparison.points +
    tableComparison.points +
    whereComparison.points +
    additionalComparisons.map(_.points).fold(0 points)(_ + _)

  // FIXME: points for executionResult?

  override val maxPoints: Points = columnComparison.maxPoints +
    tableComparison.maxPoints +
    whereComparison.maxPoints +
    additionalComparisons.map(_.maxPoints).fold(0 points)(_ + _)

  override def jsonRest: JsValue = Json.obj(
    columnComparisonsName -> columnComparison.toJson,
    tableComparisonsName -> tableComparison.toJson,
    whereComparisonsName -> whereComparison.toJson,

    additionalComparisonsName -> additionalComparisons.map(_.toJson),

    executionName -> executionResult.toJson
  )

}

final case class SqlParseFailed(learnerSolution: String, error: Throwable) extends SqlCorrResult {

  override def results: Seq[EvaluationResult] = Seq[EvaluationResult]()

  override val successType: SuccessType = SuccessType.ERROR

  override def jsonRest: JsValue = Json.obj(messageName -> error.getMessage)

}

final case class SqlExecutionResult(userResultTry: Try[SqlQueryResult], sampleResultTry: Try[SqlQueryResult]) extends EvaluationResult {

  override val success: SuccessType = userResultTry match {
    case Failure(_)          => SuccessType.ERROR
    case Success(userResult) => sampleResultTry match {
      case Failure(_)            => SuccessType.PARTIALLY
      case Success(sampleResult) => SuccessType.ofBool(userResult isIdentic sampleResult)
    }
  }


  def toJson: JsObject = Json.obj(
    successName -> success,
    userResultTry match {
      case Success(result) => userResultName -> result.toJson
      case Failure(error)  => userErrorName -> JsString(error.toString)
    },
    sampleResultName -> sampleResultTry.map(_.toJson).toOption
  )

}
