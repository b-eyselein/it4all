package model.xml

import java.nio.file.Path

import javax.xml.parsers.DocumentBuilderFactory
import model.core.matching.MatchingResult
import model.xml.dtd.{DocTypeDefParser, ElementLine}
import org.xml.sax.{ErrorHandler, SAXParseException}

import scala.collection.mutable.ListBuffer
import scala.language.{implicitConversions, postfixOps}
import scala.util.Try
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

  def correctAgainstMentionedDTD(xml: Path): Seq[XmlError] = {
    val errorHandler = new CorrectionErrorHandler

    try {
      val builder = DocBuilderFactory.newDocumentBuilder
      builder.setErrorHandler(errorHandler)
      builder.parse(xml.toFile)
    } catch {
      case _: SAXException => // Ignore...
    }

    errorHandler.errors
  }

  def correctDTD(dtd: String, exercise: XmlExercise): Try[MatchingResult[ElementLine, ElementLineMatch]] = DocTypeDefParser.parseDTD(dtd) map {
    userGrammar => DocTypeDefMatcher.doMatch(userGrammar.asElementLines, exercise.sampleGrammar.asElementLines)
  }

}
