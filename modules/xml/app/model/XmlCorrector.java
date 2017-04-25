package model;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import model.errorhandlers.CorrectionErrorHandler;
import model.errorhandlers.DtdXmlErrorHandler;
import model.errorhandlers.XmlDtdErrorHandler;
import model.errorhandlers.XmlXsdErrorHandler;
import play.Logger;

public class XmlCorrector {

  private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

  private XmlCorrector() {

  }

  public static List<XmlError> correct(String xml, String grammar, XmlExercise exercise) {
    switch(exercise.exerciseType) {
    case XML_XSD:
      return correctXMLAgainstXSD(xml, grammar, new XmlXsdErrorHandler());
    case XML_DTD:
      return correctWithDTD(xml, new XmlDtdErrorHandler());
    case DTD_XML:
      return correctWithDTD(xml, new DtdXmlErrorHandler());
    case XSD_XML:
    default:
      return Collections.emptyList();
    // return Arrays.asList(new EvaluationFailed("Dieser Aufgabentyp kann nicht
    // korrigiert werden!"));
    }

  }

  private static List<XmlError> correctWithDTD(String xml, CorrectionErrorHandler errorHandler) {
    factory.setValidating(true);
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      builder.setErrorHandler(errorHandler);
      builder.parse(xml);
    } catch (ParserConfigurationException e) {
      Logger.error("There was an error creating the Parser: ", e);
      return Collections.emptyList();
    } catch (SAXException | IOException e) {
      Logger.error("Error: SAXException while correcting XML");
    }
    return errorHandler.getErrors();
  }

  public static List<XmlError> correctXMLAgainstXSD(String xml, String grammar, CorrectionErrorHandler errorHandler) {
    Source xmlFile = new StreamSource(new StringReader(xml));
    Source xsdFile = new StreamSource(new StringReader(grammar));

    try {
      SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      Schema schema = schemaFactory.newSchema(xsdFile);

      if(schema == null)
        return Arrays
            .asList(new XmlError("Ihre Eingabedaten konnten nicht geladen werden!", XmlErrorType.FATALERROR, -1));

      Validator validator = schema.newValidator();
      validator.setErrorHandler(errorHandler);
      validator.validate(xmlFile);
    } catch (SAXException | IOException e) { // NOSONAR
      // Errors are getting caught in error handler since they are made by
      // learners
    } catch (NullPointerException e) {
      Logger.error("Could not validate XSD-File", e);
      return Arrays.asList(new XmlError("Konnte XSD nicht validieren.", XmlErrorType.FATALERROR, -1));
    }

    return errorHandler.getErrors();
  }

}
