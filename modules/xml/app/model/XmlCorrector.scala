package model

import java.io.StringReader
import java.nio.file.Path

import org.xml.sax.InputSource

import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory

object XmlCorrector {

  private val GenericFailureMsg = "Es gab einen Fehler bei der Korrektur!"
  private val ParserCreationError = "Es gab einen Fehler bei der Erstellung eines Parsers!"

  private val DocBuilderFactory = DocumentBuilderFactory.newInstance
  DocBuilderFactory.setValidating(true)

  private val SchFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)

  def correct(xml: Path, grammar: Path, exercise: XmlExercise) = exercise.exerciseType match {
    case XmlExType.XML_XSD ⇒ correctXMLAgainstXSD(xml, grammar)
    case XmlExType.XML_DTD ⇒ correctXMLAgainstDTD(xml)
    case XmlExType.DTD_XML ⇒ correctDTDAgainstXML(xml)
    case XmlExType.XSD_XML ⇒ ???
    case _                 ⇒ ???
  }

  def correct(xml: String, grammar: String, exType: XmlExType) = {
    // FIXME: for Playground...
    val xmlReader = new StringReader(xml)
    val grammarReader = new StringReader(grammar)

    exType match {
      case XmlExType.XML_XSD ⇒ correctXMLAgainstXSD(xmlReader, grammarReader, new CorrectionErrorHandler)
      case XmlExType.XML_DTD ⇒ correctWithDTD(xmlReader, new CorrectionErrorHandler)
      case XmlExType.DTD_XML ⇒ correctWithDTD(xmlReader, new CorrectionErrorHandler)
      case XmlExType.XSD_XML ⇒ ???
    }
  }

  def correctDTDAgainstXML(xml: Path) = try {
    val errorHandler = new CorrectionErrorHandler
    val builder = DocBuilderFactory.newDocumentBuilder
    builder.setErrorHandler(new CorrectionErrorHandler)
    builder.parse(xml.toFile)
    errorHandler.errors.toList
  } catch {
    case e: Throwable ⇒ List(FailureXmlError(ParserCreationError, e))
  }

  def correctXMLAgainstDTD(xml: Path) = try {
    val errorHandler = new CorrectionErrorHandler
    val builder = DocBuilderFactory.newDocumentBuilder
    builder.setErrorHandler(errorHandler)
    builder.parse(xml.toFile)
    errorHandler.errors.toList
  } catch {
    case e: Throwable ⇒ List(FailureXmlError(ParserCreationError, e))
  }

  def correctXMLAgainstXSD(xml: Path, grammar: Path) = try {
    val xmlFile = new StreamSource(xml.toFile)
    val xsdFile = new StreamSource(grammar.toFile)
    val errorHandler = new CorrectionErrorHandler

    val schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
    val schemaOpt = Option(schemaFactory.newSchema(xsdFile))

    schemaOpt match {
      case None ⇒ List(FailureXmlError("Ihre Eingabedaten konnten nicht geladen werden!"))
      case Some(schema) ⇒
        val validator = schema.newValidator
        validator.setErrorHandler(errorHandler)
        validator.validate(xmlFile)
        errorHandler.errors.toList
    }
  } catch {
    case e: Throwable ⇒ List(FailureXmlError(GenericFailureMsg, e))
  }

  def correctWithDTD(xml: StringReader, errorHandler: CorrectionErrorHandler) = try {
    val builder = DocBuilderFactory.newDocumentBuilder
    builder.setErrorHandler(errorHandler)
    builder.parse(new InputSource(xml))
    errorHandler.errors.toList
  } catch {
    case e: Throwable ⇒ List(FailureXmlError(GenericFailureMsg, e))
  }

  def correctXMLAgainstXSD(xml: StringReader, grammar: StringReader, errorHandler: CorrectionErrorHandler) = try {
    val xmlFile = new StreamSource(xml)
    val xsdFile = new StreamSource(grammar)
    val schemaOpt = Option(SchFactory.newSchema(xsdFile))

    schemaOpt match {
      case None ⇒ List(FailureXmlError("Ihre Eingabedaten konnten nicht geladen werden!"))
      case Some(schema) ⇒
        val validator = schema.newValidator
        validator.setErrorHandler(errorHandler)
        validator.validate(xmlFile)
        errorHandler.errors.toList
    }
  } catch {
    case e: Throwable ⇒ List(FailureXmlError(GenericFailureMsg, e))
  }

}
