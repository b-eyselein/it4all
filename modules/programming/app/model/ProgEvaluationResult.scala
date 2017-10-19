package model

import model.ProgEvaluationResult._
import model.result.{EvaluationResult, SuccessType}
import model.testdata.ITestData

abstract sealed class AExecutionResult(val evaluated: String, val result: String, val consoleOutput: String)

case class ExecutionResult(e: String, r: String, c: String) extends AExecutionResult(e, r, c)

case class SyntaxError(e: String, error: String, c: String) extends AExecutionResult(e, error, c)

object ProgEvaluationResult {
  private def analyze(executionResult: AExecutionResult, testData: ITestData): SuccessType = executionResult match {
    case SyntaxError(_, _, _) => SuccessType.NONE
    case ExecutionResult(executed, result, console) => if (validateResult(result, testData.output)) SuccessType.COMPLETE else SuccessType.NONE
  }

  private def validateResult[T](gottenResult: T, awaitedResult: T): Boolean = {
    // FIXME: validation of result is dependent on language! Example: numbers in
    gottenResult == awaitedResult
  }
}

case class ProgEvaluationResult(executionResult: AExecutionResult, testData: ITestData) extends EvaluationResult(analyze(executionResult, testData)) {

  def awaitedResult: String = testData.output

  def id: Int = testData.getId

  def input: java.util.List[String] = testData.getInput

  def realResult: String = executionResult.result

  def getTitleForValidation: String = executionResult match {
    case SyntaxError(_, error, _) => error
    case ExecutionResult(_, _, _) => if (isSuccessful)
      "Dieser Test war erfolgreich."
    else
      s"""Dieser Test war nicht erfolgreich.
         |  Erwartetes Ergebnisses: ${testData.output}
         |  Reales Ergebnis: " + executionResult.result""".stripMargin
  }

  override def toString: String = testData.toString + ", " + executionResult.result

}
