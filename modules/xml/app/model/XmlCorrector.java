package model;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
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

import model.user.User;

public class XmlCorrector {
  
  private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
  private static DocumentBuilder builder = null;
  private static SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
  
  public static List<XMLError> correct(File solutionFile, File referenceFile, XmlExercise exercise, User user)
      throws IOException {
    switch(exercise.exerciseType) {
    case XMLAgainstXSD:
      return correctXMLAgainstXSD(solutionFile, referenceFile);
    case XMLAgainstDTD:
      return correctXMLAgainstDTD(solutionFile);
    case DTDAgainstXML:
      return correctDTDAgainstXML(solutionFile, referenceFile);
    // case XSDAgainstXML:
    // return correctXSDAgainstXML(solutionFile, referenceFile);
    default:
      return null;
    }
  }
  
  public static List<XMLError> correctDTDAgainstXML(File studentenSolutionDTD, File referenceXML) {
    
    List<XMLError> output = new LinkedList<>();
    
    factory.setValidating(true);
    try {
      builder = factory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
    }
    
    builder.setErrorHandler(new DTDXMLErrorHandler(output));
    
    try {
      builder.parse(referenceXML);
    } catch (SAXException | IOException e) {
    }
    
    return output;
    
  }
  
  public static List<XMLError> correctXMLAgainstDTD(File studentSolutionXML) {
    
    List<XMLError> output = new LinkedList<>();
    
    factory.setValidating(true);
    try {
      builder = factory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
    }
    
    builder.setErrorHandler(new XSDErrorHandler(output));
    
    try {
      builder.parse(studentSolutionXML);
    } catch (SAXException | IOException e) {
    } catch (NullPointerException e) {
      output.add(new XMLError("leere XML", XmlErrorType.FATALERROR));
    }
    
    return output;
    
  }
  
  public static List<XMLError> correctXMLAgainstXSD(File studentSolutionXML, File xsd) {
    
    List<XMLError> output = new LinkedList<>();
    Source xmlFile = new StreamSource(studentSolutionXML);
    Source xsdFile = new StreamSource(xsd);
    
    Schema schema = null;
    try {
      schema = schemaFactory.newSchema(xsdFile);
    } catch (SAXException e) {
      // output.add(new XMLError("Beim parsen der XML ist ein Fehler
      // aufgetreten.", XmlErrorType.FATALERROR));
      // return output;
    }
    
    if(schema == null)
      return Arrays.asList(new XMLError("Ihre Eingabedaten konnten nicht geladen werden!", XmlErrorType.FATALERROR));
    
    Validator validator = schema.newValidator();
    validator.setErrorHandler(new SimpleXMLErrorHandler(output));
    try {
      validator.validate(xmlFile);
    } catch (SAXException | IOException e) {
    } catch (NullPointerException e) {
      output.add(new XMLError("konnte XSD nicht validieren.", XmlErrorType.FATALERROR));
    }
    
    return output;
  }
}
