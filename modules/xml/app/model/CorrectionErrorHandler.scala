package model

import java.io.IOException

import scala.collection.mutable.ListBuffer

import org.xml.sax.{ ErrorHandler, SAXParseException }

import model.result.{ EvaluationResult, SuccessType }

abstract sealed class XmlError(val title: String, val errorMessage: String, val line: Int, s: SuccessType)
  extends EvaluationResult(s) {
  val lineStr = if (line != -1) s" in Zeile $line" else ""
}

case class FatalXmlError(e: SAXParseException) extends XmlError("Fataler Fehler", e.getMessage, e.getLineNumber, SuccessType.NONE)

case class ErrorXmlError(e: SAXParseException) extends XmlError("Fehler", e.getMessage, e.getLineNumber, SuccessType.NONE)

case class WarningXmlError(e: SAXParseException) extends XmlError("Warnung", e.getMessage, e.getLineNumber, SuccessType.PARTIALLY)

case class FailureXmlError(msg: String, error: IOException = null) extends XmlError("Fehlschlag", msg, -1, SuccessType.NONE)

class CorrectionErrorHandler extends ErrorHandler {

  val errors: ListBuffer[XmlError] = ListBuffer.empty

  override def error(exception: SAXParseException) = errors += ErrorXmlError(exception)

  override def fatalError(exception: SAXParseException) = errors += FatalXmlError(exception)

  override def warning(exception: SAXParseException) = errors += WarningXmlError(exception)

}
