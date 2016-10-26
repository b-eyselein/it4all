package model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
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
import model.exercise.EvaluationFailed;
import model.exercise.EvaluationResult;
import play.Logger;

public class XmlCorrector {

  private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

  private XmlCorrector() {

  }

  public static List<EvaluationResult> correct(Path xml, Path grammar, XmlExercise exercise) {
    switch(exercise.exerciseType) {
    case XML_XSD:
      return correctXMLAgainstXSD(xml, grammar);
    case XML_DTD:
      return correctXMLAgainstDTD(xml);
    case DTD_XML:
      return correctDTDAgainstXML(xml);
    case XSD_XML:
    default:
      return Arrays.asList(new EvaluationFailed("Dieser Aufgabentyp kann nicht korrigiert werden!"));
    }

  }

  public static List<EvaluationResult> correctDTDAgainstXML(Path xml) {
    CorrectionErrorHandler errorHandler = new DtdXmlErrorHandler();
    factory.setValidating(true);
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      builder.setErrorHandler(errorHandler);
      builder.parse(xml.toFile());
    } catch (ParserConfigurationException e) {
      Logger.error("There was an error creating the Parser: ", e);
      return Arrays.asList(new EvaluationFailed("Es gab einen Fehler beim Korrigieren ihrer Lösung."));
    } catch (SAXException | IOException e) { // NOSONAR
      Logger.debug("Error:", e);
      // Errors are getting caught in error handler since they are made by
      // learners
    }
    return errorHandler.getErrors();
  }

  public static List<EvaluationResult> correctXMLAgainstDTD(Path xml) {
    CorrectionErrorHandler errorHandler = new XmlDtdErrorHandler();
    factory.setValidating(true);
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      builder.setErrorHandler(errorHandler);
      builder.parse(xml.toFile());
    } catch (ParserConfigurationException e) {
      Logger.error("There was an error creating the Parser: ", e);
      return Arrays.asList(new EvaluationFailed("Es gab einen Fehler beim Korrigieren ihrer Lösung."));
    } catch (SAXException | IOException e) { // NOSONAR
      // Errors are getting caught in error handler since they are made by
      // learners
    }
    return errorHandler.getErrors();
  }

  public static List<EvaluationResult> correctXMLAgainstXSD(Path xml, Path grammar) {
    Source xmlFile = new StreamSource(xml.toFile());
    Source xsdFile = new StreamSource(grammar.toFile());

    CorrectionErrorHandler errorHandler = new XmlXsdErrorHandler();

    try {
      SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      Schema schema = schemaFactory.newSchema(xsdFile);

      if(schema == null)
        return Arrays.asList(new XMLError("Fehler beim Laden ihrer Lösung!",
            "Ihre Eingabedaten konnten nicht geladen werden!", XmlErrorType.FATALERROR));

      Validator validator = schema.newValidator();
      validator.setErrorHandler(errorHandler);
      validator.validate(xmlFile);
    } catch (SAXException | IOException e) { // NOSONAR
      // Errors are getting caught in error handler since they are made by
      // learners
    } catch (NullPointerException e) {
      Logger.error("Could not validate XSD-File", e);
      return Arrays.asList(
          new XMLError("Fehler bei Validierung ihrer XSD", "Konnte XSD nicht validieren.", XmlErrorType.FATALERROR));
    }

    return errorHandler.getErrors();
  }

}
