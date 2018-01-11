package model.programming

import model.Enums.SuccessType
import model.JsonFormat
import model.core.EvaluationResult
import model.programming.ProgEvaluationResult._
import play.api.libs.json.JsValue

sealed trait AExecutionResult {
  val evaluated    : String
  val result       : String
  val consoleOutput: Option[String]
}

object ExecutionResult extends JsonFormat {

  def fromJson(jsValue: JsValue, consoleOutput: Option[String]): Option[ExecutionResult] = jsValue.asObj flatMap { jsObj =>

    // FIXME: refactor ProgEvaluationResult.fromJson(...)!

    val maybeVarAndValue = jsObj.arrayField("inputs", _.asObj flatMap { jsObj =>
      val variable = jsObj.stringField("variable")
      val value = jsObj.forgivingStringField("value")
      (variable zip value).headOption
    })

    val maybeInputs = maybeVarAndValue map (varAndValue => varAndValue.sortBy(_._1)) map (_ map (_._2))

    val maybeFunctionName = jsObj.stringField("functionName")

    val maybeResult = jsObj.forgivingStringField("result")

    (maybeInputs zip maybeFunctionName zip maybeResult).headOption map {
      case ((inputs, functionName), result) =>
        val evaluated = ProgLanguage.buildToEvaluate(functionName, inputs)
        ExecutionResult(evaluated, result, consoleOutput)
    }
  }

}

case class ExecutionResult(evaluated: String, result: String, consoleOutput: Option[String]) extends AExecutionResult

case class SyntaxError(evaluated: String, result: String, consoleOutput: Option[String]) extends AExecutionResult

case class EvaluationFailed(error: Throwable) extends AExecutionResult {

  override val evaluated: String = "ERROR!"

  override val result: String = error.getMessage

  override val consoleOutput: Option[String] = None

}

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
    case EvaluationFailed(_)           => SuccessType.NONE
    case ExecutionResult(_, result, _) => if (validateResult(result, testData.sampleTestData.output)) SuccessType.COMPLETE else SuccessType.NONE
  }

  def awaitedResult: String = testData.sampleTestData.output

  def id: Int = testData.sampleTestData.id

  def input: Seq[String] = testData.inputs map (_.input)

  def realResult: String = executionResult.result

  def getTitleForValidation: String = executionResult match {
    case SyntaxError(_, error, _) => error
    case EvaluationFailed(err)    => err.getMessage
    case ExecutionResult(_, _, _) => if (isSuccessful)
      "Dieser Test war erfolgreich."
    else
      s"""Dieser Test war nicht erfolgreich.
         |  Erwartetes Ergebnisses: ${testData.sampleTestData.output}
         |  Reales Ergebnis: " + executionResult.result""".stripMargin
  }

  override def toString: String = testData.toString + ", " + executionResult.result

}
