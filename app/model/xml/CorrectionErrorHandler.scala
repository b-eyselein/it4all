package model.xml

import model.Enums.SuccessType
import model.core.EvaluationResult
import model.xml.XmlEnums.XmlErrorType
import org.xml.sax.{ErrorHandler, SAXParseException}

import scala.collection.mutable.ListBuffer

abstract sealed class XmlError(val errorType: XmlErrorType, val errorMessage: String, val line: Int, override val success: SuccessType)
  extends EvaluationResult {

  val lineStr: String = if (line != -1) s" in Zeile $line" else ""

  def render: String = s"""<div class="alert alert-$getBSClass"><strong>${errorType.german} $lineStr:</strong> $errorMessage</div>"""

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
