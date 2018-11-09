package model.xml

import better.files.File
import javax.xml.parsers.DocumentBuilderFactory
import model.core.matching.MatchingResult
import model.xml.dtd.{DocTypeDef, ElementLine}
import org.xml.sax.{ErrorHandler, SAXParseException}

import scala.collection.mutable.ListBuffer
import scala.language.{implicitConversions, postfixOps}
import scala.xml.SAXException


class CorrectionErrorHandler extends ErrorHandler {

  val errors: ListBuffer[XmlError] = ListBuffer.empty

  override def error(exception: SAXParseException): Unit = errors += new XmlError(XmlErrorType.ERROR, exception)

  override def fatalError(exception: SAXParseException): Unit = errors += new XmlError(XmlErrorType.FATAL, exception)

  override def warning(exception: SAXParseException): Unit = errors += new XmlError(XmlErrorType.WARNING, exception)

}

object XmlCorrector {

  private val DocBuilderFactory = DocumentBuilderFactory.newInstance
  DocBuilderFactory.setValidating(true)

  def correctAgainstMentionedDTD(xml: File): Seq[XmlError] = {
    val errorHandler = new CorrectionErrorHandler

    try {
      val builder = DocBuilderFactory.newDocumentBuilder
      builder.setErrorHandler(errorHandler)
      builder.parse(xml.toJava)
      ()
    } catch {
      case _: SAXException => // Ignore...
    }

    errorHandler.errors
  }

  def correctDTD(userGrammar: DocTypeDef, sampleGrammar: DocTypeDef): MatchingResult[ElementLine, ElementLineMatch] =
    DocTypeDefMatcher.doMatch(userGrammar.asElementLines, sampleGrammar.asElementLines)


}
