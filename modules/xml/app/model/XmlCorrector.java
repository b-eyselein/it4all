package model;

import java.io.File;
import java.io.IOException;
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
import model.errorhandlers.XmlXsdErrorHandler;
import model.errorhandlers.XmlDtdErrorHandler;
import model.exercise.EvaluationFailed;
import model.exercise.EvaluationResult;
import play.Logger;

public class XmlCorrector {

  private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

  private XmlCorrector() {

  }

  public static List<EvaluationResult> correct(File solutionFile, File referenceFile, XmlExercise exercise) {
    switch(exercise.exerciseType) {
    case XML_XSD:
      return correctXMLAgainstXSD(solutionFile, referenceFile);
    case XML_DTD:
      return correctXMLAgainstDTD(solutionFile);
    case DTD_XML:
      return correctDTDAgainstXML(referenceFile);
    case XSD_XML:
    default:
      return Collections.emptyList();
    }
  }

  public static List<EvaluationResult> correctDTDAgainstXML(File referenceXML) {
    CorrectionErrorHandler errorHandler = new DtdXmlErrorHandler();
    factory.setValidating(true);
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      builder.setErrorHandler(errorHandler);
      builder.parse(referenceXML);
    } catch (ParserConfigurationException e) {
      Logger.error("There was an error creating the Parser: ", e);
      return Arrays.asList(new EvaluationFailed("Es gab einen Fehler beim Korrigieren ihrer Lösung."));
    } catch (SAXException | IOException e) {
      // Errors are getting caught in error handler
    }
    return errorHandler.getErrors();
  }

  public static List<EvaluationResult> correctXMLAgainstDTD(File studentSolutionXML) {
    CorrectionErrorHandler errorHandler = new XmlDtdErrorHandler();
    factory.setValidating(true);
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      builder.setErrorHandler(errorHandler);
      builder.parse(studentSolutionXML);
    } catch (ParserConfigurationException e) {
      Logger.error("There was an error creating the Parser: ", e);
      return Arrays.asList(new EvaluationFailed("Es gab einen Fehler beim Korrigieren ihrer Lösung."));
    } catch (SAXException | IOException e) {
      // Errors are getting caught in error handler
    }
    return errorHandler.getErrors();
  }

  public static List<EvaluationResult> correctXMLAgainstXSD(File studentSolutionXML, File xsd) {
    Source xmlFile = new StreamSource(studentSolutionXML);
    Source xsdFile = new StreamSource(xsd);

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

    } catch (SAXException | IOException e) {
    } catch (NullPointerException e) {
      Logger.error("Could not validate XSD-File", e);
      return Arrays.asList(
          new XMLError("Fehler bei Validierung ihrer XSD", "Konnte XSD nicht validieren.", XmlErrorType.FATALERROR));
    }

    return errorHandler.getErrors();
  }
  
}
