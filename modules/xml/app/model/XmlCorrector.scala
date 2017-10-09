package model

import java.io.{ IOException, StringReader }
import java.nio.file.Path

import scala.collection.JavaConverters.seqAsJavaListConverter
import scala.language.implicitConversions

import org.xml.sax.InputSource

import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory
import play.Logger

object XmlCorrector {

  implicit def path2InputSource(xml: Path) = new InputSource(xml.toAbsolutePath.toString)
  implicit def stringReader2InputSource(reader: StringReader) = new InputSource(reader)

  implicit def file2StreamSource(xml: Path) = new StreamSource(xml.toFile)
  implicit def stingReader2StreamSource(reader: StringReader) = new StreamSource(reader)

  private val GenericFailureMsg = "Es gab einen Fehler bei der Korrektur!"
  private val ParserCreationError = "Es gab einen Fehler bei der Erstellung eines Parsers!"

  private val DocBuilderFactory = DocumentBuilderFactory.newInstance
  DocBuilderFactory.setValidating(true)

  private val SchFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)

  def correct(xml: Path, grammar: Path, exercise: XmlExercise) = exercise.exerciseType match {
    case (XmlExType.XML_XSD | XmlExType.XSD_XML) => correctXsdAndXml(xml, grammar)
    case (XmlExType.XML_DTD | XmlExType.DTD_XML) => correctDtdAndXml(xml)
  }

  def correctJava(xml: Path, grammar: Path, exercise: XmlExercise) = correct(xml, grammar, exercise).asJava

  def correct(xml: String, grammar: String, exType: XmlExType) = {
    // FIXME: for Playground...
    val xmlReader = new StringReader(xml)
    val grammarReader = new StringReader(grammar)

    exType match {
      case (XmlExType.XML_XSD | XmlExType.XSD_XML) => correctXsdAndXml(xmlReader, grammarReader)
      case (XmlExType.XML_DTD | XmlExType.DTD_XML) => correctDtdAndXml(xmlReader)
    }
  }

  def correctJava(xml: String, grammar: String, exType: XmlExType) = correct(xml, grammar, exType).asJava

  def recover(e: IOException) = {
    Logger.error("There has been an error correcting", e)
    List(FailureXmlError(GenericFailureMsg, e))
  }

  def correctDtdAndXml(xml: InputSource) = try {
    val errorHandler = new CorrectionErrorHandler

    val builder = DocBuilderFactory.newDocumentBuilder
    builder.setErrorHandler(new CorrectionErrorHandler)
    builder.parse(xml)

    errorHandler.errors.toList
  } catch {
    case e: IOException => recover(e)
  }

  def correctXsdAndXml(xmlStreamSource: StreamSource, xsdStreamSource: StreamSource) = try {
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
  } catch {
    case e: IOException => recover(e)
  }

}
