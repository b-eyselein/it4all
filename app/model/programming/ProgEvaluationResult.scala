package model.programming

import model.Enums.SuccessType
import model.core.{CompleteResult, EvaluationResult}
import play.twirl.api.{Html, HtmlFormat}

sealed abstract class ProgCompleteResult(val learnerSolution: String, results: Seq[ProgEvalResult]) extends CompleteResult[ProgEvalResult] {

  override type SolType = String

  override val renderLearnerSolution: Html = Html(s"<pre>$learnerSolution</pre>")

}

case class ProgImplementationCompleteResult(ls: String, results: Seq[ProgEvalResult]) extends ProgCompleteResult(ls, results)

case class ProgValidationCompleteResult(results: Seq[ProgEvalResult]) extends ProgCompleteResult("", results)

trait ProgEvalResult extends EvaluationResult


case class SyntaxError(reason: String) extends ProgEvalResult {

  override def success: SuccessType = SuccessType.ERROR

}

object AExecutionResult {

  def apply(successType: SuccessType, evaluated: String, completeTestData: CompleteTestData, result: String, consoleOutput: Option[String]): AExecutionResult = successType match {
    case (SuccessType.COMPLETE | SuccessType.PARTIALLY) => ExecutionResult(successType, evaluated, completeTestData, result, consoleOutput)
    case SuccessType.NONE                               => ExecutionFailed(successType, evaluated, completeTestData, result, consoleOutput)
    case SuccessType.ERROR                              => ExecutionException(successType, evaluated, completeTestData, result, consoleOutput)
  }

}

sealed trait AExecutionResult extends ProgEvalResult {

  val evaluated       : String
  val completeTestData: CompleteTestData
  val result          : String
  val consoleOutput   : Option[String]

  def printConsoleOut: Html = Html(consoleOutput match {
    case None       => ""
    case Some(cout) => "<p>Konsolenoutput: " + (if (cout.isEmpty) "--</p>" else s"</p><pre>${HtmlFormat.escape(cout)}</pre>")
  })

}

case class ExecutionResult(success: SuccessType, evaluated: String, completeTestData: CompleteTestData, result: String, consoleOutput: Option[String]) extends AExecutionResult

case class ExecutionFailed(success: SuccessType, evaluated: String, completeTestData: CompleteTestData, result: String, consoleOutput: Option[String]) extends AExecutionResult

case class ExecutionException(success: SuccessType, evaluated: String, completeTestData: CompleteTestData, result: String, consoleOutput: Option[String]) extends AExecutionResult
