package model.programming

import model.Enums.SuccessType
import model.core.{CompleteResult, EvaluationResult}
import play.twirl.api.{Html, HtmlFormat}

case class ProgCompleteResult(learnerSolution: String, results: Seq[ProgEvalResult]) extends CompleteResult[ProgEvalResult] {

  override type SolType = String

  override def renderLearnerSolution: Html = Html(s"<pre>$learnerSolution</pre>")

}


trait ProgEvalResult extends EvaluationResult


case class SyntaxError(reason: String) extends ProgEvalResult {

  override def success: SuccessType = SuccessType.ERROR

}

object AExecutionResult {

  def apply(successType: SuccessType, evaluated: String, awaited: String, result: String, consoleOutput: Option[String]): AExecutionResult = successType match {
    case (SuccessType.COMPLETE | SuccessType.PARTIALLY) => ExecutionResult(evaluated, awaited, result, consoleOutput)
    case SuccessType.NONE                               => ExecutionFailed(evaluated, awaited, result, consoleOutput)
    case SuccessType.ERROR                              => ExecutionException(evaluated, awaited, result, consoleOutput)
  }

}

sealed trait AExecutionResult extends ProgEvalResult {

  val evaluated    : String
  val awaited      : String
  val result       : String
  val consoleOutput: Option[String]

  def printConsoleOut: Html = Html(consoleOutput match {
    case None       => ""
    case Some(cout) => "<p>Konsolenoutput: " + (if (cout.isEmpty) "--</p>" else s"</p><pre>${HtmlFormat.escape(cout)}</pre>")
  })

}

case class ExecutionResult(evaluated: String, awaited: String, result: String, consoleOutput: Option[String]) extends AExecutionResult {

  override def success: SuccessType = SuccessType.COMPLETE

}

case class ExecutionFailed(evaluated: String, awaited: String, result: String, consoleOutput: Option[String]) extends AExecutionResult {

  override def success: SuccessType = SuccessType.NONE

}

case class ExecutionException(evaluated: String, awaited: String, result: String, consoleOutput: Option[String]) extends AExecutionResult {

  override def success: SuccessType = SuccessType.ERROR

}
