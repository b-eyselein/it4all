package model.programming

import model.Enums.SuccessType
import model.core.{CompleteResult, EvaluationResult}
import play.twirl.api.Html

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


//case class ProgEvaluationResult(executionResult: AExecutionResult, testData: CompleteSampleTestData) extends ProgEvalResult {
//
//  override val success: SuccessType = executionResult match {
//    case SyntaxError(_)                => SuccessType.NONE
//    case ExecutionFailed(_, _, _)      => SuccessType.NONE
//    case ExecutionResult(_, result, _) => SuccessType.COMPLETE
//  }
//
//  def awaitedResult: String = testData.sampleTestData.output
//
//  def id: Int = testData.sampleTestData.id
//
//  def input: Seq[String] = testData.inputs map (_.input)
//
//  def realResult: String = executionResult.result
//
//  def getTitleForValidation: String = executionResult match {
//    case SyntaxError(_, error, _)   => error
//    case ExecutionFailed(_, err, _) => err
//    case ExecutionResult(_, _, _)   => if (isSuccessful)
//      "Dieser Test war erfolgreich."
//    else
//      s"""Dieser Test war nicht erfolgreich.
//         |  Erwartetes Ergebnisses: ${testData.sampleTestData.output}
//         |  Reales Ergebnis: " + executionResult.result""".stripMargin
//  }
//
//  override def toString: String = testData.toString + ", " + executionResult.result
//
//}
