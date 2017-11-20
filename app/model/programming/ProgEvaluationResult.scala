package model.programming

import model.Enums.SuccessType
import model.core.EvaluationResult
import model.programming.ProgEvaluationResult._

abstract sealed class AExecutionResult(val evaluated: String, val result: String, val consoleOutput: String)

case class ExecutionResult(e: String, r: String, c: String) extends AExecutionResult(e, r, c)

case class SyntaxError(e: String, error: String, c: String) extends AExecutionResult(e, error, c)

object ProgEvaluationResult {

  def validateResult[T](gottenResult: T, awaitedResult: T): Boolean = {
    // FIXME: validation of result is dependent on language! Example: numbers in
    gottenResult == awaitedResult
  }

}

case class ProgEvaluationResult(executionResult: AExecutionResult, testData: CompleteSampleTestData)
  extends EvaluationResult {

  override val success: SuccessType = executionResult match {
    case SyntaxError(_, _, _)          => SuccessType.NONE
    case ExecutionResult(_, result, _) => if (validateResult(result, testData.sampleTestData.output)) SuccessType.COMPLETE else SuccessType.NONE
  }

  def awaitedResult: String = testData.sampleTestData.output

  def id: Int = testData.sampleTestData.id

  def input: Seq[String] = testData.inputs map (_.input)

  def realResult: String = executionResult.result

  def getTitleForValidation: String = executionResult match {
    case SyntaxError(_, error, _) => error
    case ExecutionResult(_, _, _) => if (isSuccessful)
      "Dieser Test war erfolgreich."
    else
      s"""Dieser Test war nicht erfolgreich.
         |  Erwartetes Ergebnisses: ${testData.sampleTestData.output}
         |  Reales Ergebnis: " + executionResult.result""".stripMargin
  }

  override def toString: String = testData.toString + ", " + executionResult.result

}
