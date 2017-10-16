package model

import java.io.StringReader
import java.nio.file.Path
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory

import org.xml.sax.InputSource
import play.Logger

import scala.collection.JavaConverters.seqAsJavaListConverter
import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}

object XmlCorrector {

  implicit def path2InputSource(xml: Path): InputSource = new InputSource(xml.toAbsolutePath.toString)

  implicit def stringReader2InputSource(reader: StringReader): InputSource = new InputSource(reader)

  implicit def file2StreamSource(xml: Path): StreamSource = new StreamSource(xml.toFile)

  implicit def stingReader2StreamSource(reader: StringReader): StreamSource = new StreamSource(reader)

  private val DocBuilderFactory = DocumentBuilderFactory.newInstance
  DocBuilderFactory.setValidating(true)

  private val SchFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)

  def correct(xml: Path, grammar: Path, exercise: XmlExercise): List[XmlError] = exercise.exerciseType match {
    case (XML_XSD | XSD_XML) => correctXsdAndXml(xml, grammar)
    case (XML_DTD | DTD_XML) => correctDtdAndXml(xml)
  }

  def correctJava(xml: Path, grammar: Path, exercise: XmlExercise): java.util.List[XmlError] = correct(xml, grammar, exercise).asJava

  def correct(xml: String, grammar: String, exType: XmlExType): List[XmlError] = {
    // FIXME: for Playground...
    val xmlReader = new StringReader(xml)
    val grammarReader = new StringReader(grammar)

    exType match {
      case (XML_XSD | XSD_XML) => correctXsdAndXml(xmlReader, grammarReader)
      case (XML_DTD | DTD_XML) => correctDtdAndXml(xmlReader)
    }
  }

  def correctJava(xml: String, grammar: String, exType: XmlExType): java.util.List[XmlError] = correct(xml, grammar, exType).asJava

  def recover(e: Throwable): List[XmlError] = List(FailureXmlError(e.getMessage, e))

  def correctDtdAndXml(xml: InputSource): List[XmlError] = Try({
    val errorHandler = new CorrectionErrorHandler

    val builder = DocBuilderFactory.newDocumentBuilder
    builder.setErrorHandler(new CorrectionErrorHandler)
    builder.parse(xml)

    errorHandler.errors.toList
  }) match {
    case Success(errors) => errors
    case Failure(e) => recover(e)
  }

  def correctXsdAndXml(xmlStreamSource: StreamSource, xsdStreamSource: StreamSource): List[XmlError] = Try({
    val errorHandler = new CorrectionErrorHandler
    val schemaOpt = Option(SchFactory.newSchema(xsdStreamSource))

    schemaOpt match {
      case None => List(FailureXmlError("Ihre Eingabedaten konnten nicht geladen werden!"))
      case Some(schema) =>
        val validator = schema.newValidator
        validator.setErrorHandler(new CorrectionErrorHandler)
        validator.validate(xmlStreamSource)
        errorHandler.errors.toList
    }
  }) match {
    case Success(errors) => errors
    case Failure(e) => recover(e)
  }

}
