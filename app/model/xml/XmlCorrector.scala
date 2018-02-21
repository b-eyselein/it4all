package model.xml

import java.io.StringReader
import java.nio.file.Path
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory

import dtdparser.{DTDLine, DTDParser}
import org.xml.sax.InputSource

import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}
import scala.xml.SAXException

object XmlCorrector {

  implicit def stringReader2InputSource(reader: StringReader): InputSource = new InputSource(reader)

  implicit def file2StreamSource(xml: Path): StreamSource = new StreamSource(xml.toFile)

  implicit def stingReader2StreamSource(reader: StringReader): StreamSource = new StreamSource(reader)

  private val DocBuilderFactory = DocumentBuilderFactory.newInstance
  DocBuilderFactory.setValidating(true)

  private val XsdSchemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)

  def correct(xml: Path, grammar: Path): Seq[XmlError] = {
    //    exerciseType match {
    //      case (XmlExType.XML_XSD | XmlExType.XSD_XML) => correctXmlAndXsd(xml, grammar)
    //      case (XmlExType.XML_DTD | XmlExType.DTD_XML) => correctDtdAndXml(xml)
    //    }
    correctDtdAndXml(xml)
  }

  def correct(xml: String, grammar: String): Seq[XmlError] = {
    // FIXME: for Playground...
    val xmlReader = new StringReader(xml)
    //    val grammarReader = new StringReader(grammar)

    //    exType match {
    //      case (XmlExType.XML_XSD | XmlExType.XSD_XML) => correctXsdAndXmlStreamSource(xmlReader, grammarReader)
    //      case (XmlExType.XML_DTD | XmlExType.DTD_XML) =>
    //    }

    correctDtdAndXmlInputSource(xmlReader)
    ???
  }

  def recover(e: Throwable): Seq[XmlError] = Seq(FailureXmlError(e.getMessage, e))

  def correctDTD(dtd: String, exercise: XmlExercise): Seq[Any] = {
    // FIXME: use...
    val parsedLines: Seq[Try[DTDLine]] = DTDParser.parseDTD(dtd)

    println("Grammar file content: " + exercise.sampleGrammar)

    println(parsedLines)

    Seq.empty
  }

  def correctDtdAndXml(xml: Path): Seq[XmlError] = {
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

  def correctDtdAndXmlInputSource(xml: InputSource): Seq[XmlError] = Try {
    val errorHandler = new CorrectionErrorHandler

    val builder = DocBuilderFactory.newDocumentBuilder
    builder.setErrorHandler(new CorrectionErrorHandler)
    builder.parse(xml)

    errorHandler.errors
  } match {
    case Success(errors) => errors
    case Failure(e)      => recover(e)
  }


  def correctXmlAndXsd(xml: Path, grammar: Path): Seq[XmlError] = {
    val errorHandler = new CorrectionErrorHandler

    val schema = XsdSchemaFactory.newSchema(new StreamSource(grammar.toFile))

    if (schema == null) List(FailureXmlError("Ihre Eingabedaten konnten nicht geladen werden!"))

    try {
      val validator = schema.newValidator
      validator.setErrorHandler(errorHandler)
      validator.validate(new StreamSource(xml.toFile))
    } catch {
      case e: SAXException => // Ignore...
    }

    errorHandler.errors
  }

  def correctXsdAndXmlStreamSource(xmlStreamSource: StreamSource, xsdStreamSource: StreamSource): Seq[XmlError] = Try({
    val errorHandler = new CorrectionErrorHandler
    val schemaOpt = Option(XsdSchemaFactory.newSchema(xsdStreamSource))

    schemaOpt match {
      case None         => Seq(FailureXmlError("Ihre Eingabedaten konnten nicht geladen werden!"))
      case Some(schema) =>
        val validator = schema.newValidator
        validator.setErrorHandler(new CorrectionErrorHandler)
        validator.validate(xmlStreamSource)
        errorHandler.errors
    }
  }) match {
    case Success(errors) => errors
    case Failure(e)      => recover(e)
  }

}
