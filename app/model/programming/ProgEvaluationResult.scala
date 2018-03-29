package model.programming

import model.Enums.SuccessType
import model.Enums.SuccessType._
import model.core.{CompleteResult, EvaluationResult}
import model.programming.ProgConsts._
import play.api.libs.json.{JsBoolean, JsValue, Json}
import play.twirl.api.{Html, HtmlFormat}

// Types of complete results

case class ProgCompleteResult(implementation: String, solutionSaved: Boolean, results: Seq[ProgEvalResult]) extends CompleteResult[ProgEvalResult] {

  override type SolType = String

  override val renderLearnerSolution: Html = Html(s"<pre>$learnerSolution</pre>")

  def render: Html = Html(results.grouped(2) map { tuple =>
    s"""<div class="row">
       |  ${tuple map (_.render) mkString "\n"}
       |</div>""".stripMargin
  } mkString "\n")

  def toJson: JsValue = Json.obj(
    "solutionSaved" -> solutionSaved,
    "results" -> results.map(_.toJson)
  )

  override def learnerSolution: String = implementation

}

// Single results

trait ProgEvalResult extends EvaluationResult {

  def render: String

  def toJson: JsValue

}

case object ProgEvalFailed extends ProgEvalResult {

  override def success: SuccessType = SuccessType.ERROR

  override def render: String = ???

  override def toJson: JsValue = Json.obj("msg" -> "Die Evaluation schlug fehl!") //???

}

case class ExecutionResult(success: SuccessType, completeTestData: CompleteTestData, result: String, consoleOutput: Option[String]) extends ProgEvalResult {

  // FIXME: outputType beachten ?!?

  private def renderResult: String = if (result.isEmpty) "\"\"" else result

  private def printConsoleOut: String = consoleOutput map (cout => "<p>Konsolenoutput: " + (if (cout.isEmpty) "--</p>" else s"</p><pre>${HtmlFormat.escape(cout)}</pre>")) getOrElse ""

  override def render: String = if (success == ERROR) {
    s"""<div class="col-md-6">
       |  <div class="alert alert-$getBSClass">
       |    <p>Es gab einen Fehler bei der Ausführung ihres Codes:</p>
       |    <pre>$renderResult</pre>
       |    $printConsoleOut
       |  </div>
       |</div>""".stripMargin
  } else {
    s"""<div class="col-md-6">
       |  <div class="alert alert-$getBSClass">
       |    <p>Test ${completeTestData.testData.id} war ${if (isSuccessful) "" else "nicht"} erfolgreich.<p>
       |    <p>Erwartet: <code>${completeTestData.testData.output}</code></p>
       |    <p>Bekommen: <code>$renderResult</code></p>
       |    $printConsoleOut
       |  </div>
       |</div>""".stripMargin
  }

  override def toJson: JsValue = Json.obj(
    idName -> completeTestData.testData.id,
    correct -> JsBoolean(success == SuccessType.COMPLETE),
    awaitedName -> completeTestData.testData.output,
    gottenName -> result
  )

}