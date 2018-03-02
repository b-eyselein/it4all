package model.programming

import model.Enums.SuccessType
import model.Enums.SuccessType._
import model.core.{CompleteResult, EvaluationResult}
import play.twirl.api.{Html, HtmlFormat}

// Types of complete results

sealed abstract class ProgCompleteResult(val learnerSolution: String, results: Seq[ProgEvalResult]) extends CompleteResult[ProgEvalResult] {

  override type SolType = String

  override val renderLearnerSolution: Html = Html(s"<pre>$learnerSolution</pre>")

  def render: Html = Html(results.grouped(2) map { tuple =>
    s"""<div class="row">
       |  ${tuple map (_.render) mkString "\n"}
       |</div>""".stripMargin
  } mkString "\n")

}

case class ProgImplementationCompleteResult(ls: String, results: Seq[ProgEvalResult]) extends ProgCompleteResult(ls, results)

case class ProgValidationCompleteResult(results: Seq[ProgEvalResult]) extends ProgCompleteResult("", results)

// Single results

trait ProgEvalResult extends EvaluationResult {

  def render: String

}

case object ProgEvalFailed extends ProgEvalResult {

  override def success: SuccessType = SuccessType.ERROR

  override def render: String = ???

}

case class SyntaxError(reason: String) extends ProgEvalResult {

  override def success: SuccessType = SuccessType.NONE

  override def render: String =
    s"""<div class="col-md-12">
       |  <div class="alert alert-$getBSClass">
       |    <p>Ihr Code hat einen Syntaxfehler:<p>
       |    <pre>$reason</pre>
       |  </div>
       |</div>""".stripMargin

}

case object TimeOut extends ProgEvalResult {

  override def success: SuccessType = SuccessType.ERROR

  override def render: String =
    s"""<div class="col-md-12">
       |  <div class="alert alert-$getBSClass">
       |    <p>Es gab ein Timeout ihres Codes. Haben Sie eventuell eine Endlosschleife programmiert?</p>
       |  </div>
       |</div>""".stripMargin

}

case class ExecutionResult(success: SuccessType, evaluated: String, completeTestData: CompleteTestData, result: String, consoleOutput: Option[String]) extends ProgEvalResult {

  // FIXME: outputType beachten ?!?

  private def renderResult : String = if(result.isEmpty) "\"\"" else result

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
       |    <p>Test von <code>$evaluated</code> war ${if (isSuccessful) "" else "nicht"} erfolgreich.<p>
       |    <p>Erwartet: <code>${completeTestData.testData.output}</code></p>
       |    <p>Bekommen: <code>$renderResult</code></p>
       |    $printConsoleOut
       |  </div>
       |</div>""".stripMargin
  }

}