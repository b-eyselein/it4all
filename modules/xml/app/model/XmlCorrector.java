package model;

import java.io.File;
import java.io.IOException;
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

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import model.user.User;

public class XmlCorrector {
  
  private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
  private static DocumentBuilder builder = null;
  private static SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
  
  
  public static List<XMLError> correctXMLAgainstDTD(File studentSolutionXML) {
    
    List<XMLError> output = new LinkedList<>();
    
    factory.setValidating(true);
    try {
      builder = factory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
    }
    
    builder.setErrorHandler(new SimpleXMLErrorHandler(output));
    
    try {
      Document doc = builder.parse(studentSolutionXML);
    } catch (SAXException | IOException e) {
    } catch (NullPointerException e){
      output.add(new XMLError("leere XML", XmlErrorType.FATALERROR));
    }
    
    return output;
    
  }
  
  public static List<XMLError> correctXMLAgainstXSD(File studentSolutionXML, File xsd) throws IOException {
    
    List<XMLError> output = new LinkedList<>();
    Source xmlFile = new StreamSource(studentSolutionXML);
    Source xsdFile = new StreamSource(xsd);
    
    Schema schema = null;
    try {
      schema = schemaFactory.newSchema(xmlFile);
    } catch (SAXException e) {
    }
    
    Validator validator = schema.newValidator();
    validator.setErrorHandler(new SimpleXMLErrorHandler(output));
    try {
      validator.validate(xsdFile);
    } catch (SAXException e) {
    }
    
    return output;
  }
  
  public static List<XMLError> correctDTDAgainstXML(File studentenSolutionDTD) {
    
    List<XMLError> output = new LinkedList<>();
    
    factory.setValidating(true);
    try {
      builder = factory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
    }
    
    builder.setErrorHandler(new DTDXMLErrorHandler(output));
    
    try {
      Document doc = builder.parse(studentenSolutionDTD); // studentenSolutionDTD);
    } catch (SAXException | IOException e) {
    }
    
    for(XMLError item: output) {
      System.out.println(item);
    }
    
    return output;
    
  }
  
  public static List<XMLError> correct(File solutionFile, File referenceFile, XmlExercise exercise, User user)
      throws IOException {
    switch(exercise.exerciseType) {
    case XMLAgainstXSD:
      return correctXMLAgainstXSD(solutionFile, referenceFile);
    case XMLAgainstDTD:
      return correctXMLAgainstDTD(solutionFile);
    case DTDAgainstXML:
      return correctDTDAgainstXML(solutionFile);
    // case XSDAgainstXML:
    // return correctXSDAgainstXML(solutionFile, referenceFile);
    default:
      return null;
    }
  }
}
