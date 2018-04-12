package model.xml

import model.Enums.SuccessType
import model.core.{CompleteResult, EvaluationResult}
import model.xml.XmlEnums.XmlErrorType
import org.xml.sax.SAXParseException
import play.api.libs.json.{JsObject, JsValue, Json}
import play.twirl.api.{Html, HtmlFormat}

case class XmlCompleteResult(learnerSolution: String, solutionSaved: Boolean, results: Seq[XmlError]) extends CompleteResult[XmlError] {

  override type SolType = String

  override def renderLearnerSolution: Html = new Html("<pre>" + HtmlFormat.escape(learnerSolution).toString + "</pre>")

  def render: Html = {
    val solSaved: String = if (solutionSaved)
      s"""<div class="alert alert-success"><span class="glyphicon glyphicon-ok"></span> Ihre Lösung wurde gespeichert.</div>"""
    else
      s"""<div class="alert alert-danger"><span class="glyphicon glyphicon-remove"></span> Ihre Lösung konnte nicht gespeichert werden!</div>"""

    val resultsRender: String = results match {
      case Nil => s"""<div class="alert alert-success"><span class="glyphicon glyphicon-ok"></span> Es wurden keine Fehler gefunden.</div>"""
      case res => res map (_.render) mkString "\n"
    }

    new Html(solSaved + resultsRender)
  }

  def toJson: JsValue = Json.obj(
    "solSaved" -> solutionSaved,
    "success" -> results.isEmpty,
    "results" -> results.map(_.toJson)
  )

}

abstract sealed class XmlError(val errorType: XmlErrorType, val errorMessage: String, val line: Int, override val success: SuccessType)
  extends EvaluationResult {

  val lineStr: String = if (line != -1) s" in Zeile $line" else ""

  def render: String = s"""<div class="alert alert-$getBSClass"><strong>${errorType.german} $lineStr:</strong> $errorMessage</div>"""

  def toJson: JsObject = Json.obj("errorType" -> errorType.name, "errorMessage" -> errorMessage, "line" -> line, "success" -> success.name)

}

case class FatalXmlError(e: SAXParseException) extends XmlError(XmlErrorType.FATAL, e.getMessage, e.getLineNumber, SuccessType.NONE)

case class ErrorXmlError(e: SAXParseException) extends XmlError(XmlErrorType.ERROR, e.getMessage, e.getLineNumber, SuccessType.NONE)

case class WarningXmlError(e: SAXParseException) extends XmlError(XmlErrorType.WARNING, e.getMessage, e.getLineNumber, SuccessType.PARTIALLY)

case class FailureXmlError(msg: String, error: Throwable = null) extends XmlError(XmlErrorType.FAILURE, msg, -1, SuccessType.NONE)

