package model.xml

import model.Enums.SuccessType
import model.core.{CompleteResult, EvaluationResult}
import model.xml.XmlEnums.XmlErrorType
import org.xml.sax.{ErrorHandler, SAXParseException}
import play.twirl.api.{Html, HtmlFormat}

import scala.collection.mutable.ListBuffer
import scalatags.Text.all._

case class XmlCompleteResult(learnerSolution: String, solutionSaved: Boolean, results: Seq[XmlError]) extends CompleteResult[XmlError] {

  override type SolType = String

  override def renderLearnerSolution: Html = new Html(pre(HtmlFormat.escape(learnerSolution).toString).toString)

  def render: Html = {
    val solSaved: String = if (solutionSaved)
      div(cls := "alert alert-success")(span(cls := "glyphicon glyphicon-ok"), " Ihre Lösung wurde gespeichert.").toString
    else
      div(cls := "alert alert-danger")(span(cls := "glyphicon glyphicon-remove"), " Ihre L&ouml;sung konnte nicht gespeichert werden!").toString

    val resultsRender: String = results match {
      case Nil => div(cls := "alert alert-success")(span(cls := "glyphicon glyphicon-ok"), "Es wurden keine Fehler gefunden.").toString
      case res => res map (_.render) mkString "\n"
    }

    new Html(solSaved + resultsRender)
  }

}

abstract sealed class XmlError(val errorType: XmlErrorType, val errorMessage: String, val line: Int, override val success: SuccessType)
  extends EvaluationResult {

  val lineStr: String = if (line != -1) s" in Zeile $line" else ""

  def render: String = div(cls := s"alert alert-$getBSClass")(strong(errorType.german + lineStr + ":"), errorMessage).toString

}

case class FatalXmlError(e: SAXParseException) extends XmlError(XmlErrorType.FATAL, e.getMessage, e.getLineNumber, SuccessType.NONE)

case class ErrorXmlError(e: SAXParseException) extends XmlError(XmlErrorType.ERROR, e.getMessage, e.getLineNumber, SuccessType.NONE)

case class WarningXmlError(e: SAXParseException) extends XmlError(XmlErrorType.WARNING, e.getMessage, e.getLineNumber, SuccessType.PARTIALLY)

case class FailureXmlError(msg: String, error: Throwable = null) extends XmlError(XmlErrorType.FAILURE, msg, -1, SuccessType.NONE)


class CorrectionErrorHandler extends ErrorHandler {

  val errors: ListBuffer[XmlError] = ListBuffer.empty

  override def error(exception: SAXParseException): Unit = errors += ErrorXmlError(exception)

  override def fatalError(exception: SAXParseException): Unit = errors += FatalXmlError(exception)

  override def warning(exception: SAXParseException): Unit = errors += WarningXmlError(exception)

}
