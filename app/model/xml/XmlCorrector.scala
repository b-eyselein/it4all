package model.xml

import java.nio.file.Path

import dtdparser.{DTDLine, DTDParser}
import javax.xml.parsers.DocumentBuilderFactory
import org.xml.sax.{ErrorHandler, SAXParseException}

import scala.collection.mutable.ListBuffer
import scala.language.implicitConversions
import scala.util.Try
import scala.xml.SAXException


class CorrectionErrorHandler extends ErrorHandler {

  val errors: ListBuffer[XmlError] = ListBuffer.empty

  override def error(exception: SAXParseException): Unit = errors += ErrorXmlError(exception)

  override def fatalError(exception: SAXParseException): Unit = errors += FatalXmlError(exception)

  override def warning(exception: SAXParseException): Unit = errors += WarningXmlError(exception)

}

object XmlCorrector {

  private val DocBuilderFactory = DocumentBuilderFactory.newInstance
  DocBuilderFactory.setValidating(true)

  def correct(xml: Path, grammar: Path): Seq[XmlError] = {
    val errorHandler = new CorrectionErrorHandler

    try {
      val builder = DocBuilderFactory.newDocumentBuilder
      builder.setErrorHandler(errorHandler)
      builder.parse(xml.toFile)
    } catch {
      case e: SAXException => // Ignore...
    }

    errorHandler.errors
  }

  def correctDTD(dtd: String, exercise: XmlExercise): Seq[Any] = {
    // FIXME: implement!
    val parsedLines: Seq[Try[DTDLine]] = DTDParser.parseDTD(dtd)

    println("Grammar file content: " + exercise.sampleGrammar)

    println(parsedLines)

    Seq.empty
  }

}
