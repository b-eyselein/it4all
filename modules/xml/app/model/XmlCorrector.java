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
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import model.user.User;

public class XmlCorrector {
  
  public static List<XMLError> correctXMLAgainstDTD(File studentSolutionXML) {
    List<XMLError> output = new LinkedList<>();
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(true);
    DocumentBuilder builder = null;
    try {
      builder = factory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
    }
    builder.setErrorHandler(new ErrorHandler() {
      
      @Override
      public void warning(SAXParseException exception) throws SAXException {
        output.add(new XMLError(exception.getLineNumber(), exception.getMessage(), XmlErrorType.WARNING));
      }
      
      @Override
      public void fatalError(SAXParseException exception) throws SAXException {
        output.add(new XMLError(exception.getLineNumber(), exception.getMessage(), XmlErrorType.FATALERROR));
      }
      
      @Override
      public void error(SAXParseException exception) throws SAXException {
        output.add(new XMLError(exception.getLineNumber(), exception.getMessage(), XmlErrorType.ERROR));
        
      }
    });
    
    try {
      Document doc = builder.parse(studentSolutionXML);
      
    } catch (SAXException e) {
    } catch (IOException e) {
    }
    
    return output;
  }
  
  public static List<XMLError> correctXMLAgainstXSD(File studentSolutionXML, File xsd) throws IOException {
    List<XMLError> output = new LinkedList<>();
    Source xmlFile = new StreamSource(studentSolutionXML);
    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema = null;
    try {
      schema = schemaFactory.newSchema(xsd);
    } catch (SAXException e) {
    }
    
    Validator validator = schema.newValidator();
    validator.setErrorHandler(new ErrorHandler() {
      
      @Override
      public void warning(SAXParseException exception) throws SAXException {
        output.add(new XMLError(exception.getLineNumber(), exception.getMessage(), XmlErrorType.WARNING));
      }
      
      @Override
      public void fatalError(SAXParseException exception) throws SAXException {
        output.add(new XMLError(exception.getLineNumber(), exception.getMessage(), XmlErrorType.FATALERROR));
      }
      
      @Override
      public void error(SAXParseException exception) throws SAXException {
        output.add(new XMLError(exception.getLineNumber(), exception.getMessage(), XmlErrorType.ERROR));
        
      }
    });
    try {
      validator.validate(xmlFile);
    } catch (SAXException e) {
    }
    return output;
  }
  
  public static List<XMLError> correctDTDAgainstXML(File studentenSolutionDTD) {
    
    List<XMLError> output = new LinkedList<>();
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(true);
    DocumentBuilder builder = null;
    try {
      builder = factory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
    }
    builder.setErrorHandler(new ErrorHandler() {
      
      @Override
      public void warning(SAXParseException exception) throws SAXException {
        // String string = null;
        if(exception.getSystemId().indexOf("xml") >= 0) {
          
          output.add(new XMLError(exception.getMessage(), XmlErrorType.WARNING));
          
        } else {
          output.add(new XMLError(exception.getLineNumber(), exception.getMessage(), XmlErrorType.WARNING));
        }
      }
      
      @Override
      public void fatalError(SAXParseException exception) throws SAXException {
        // String string = null;
        if(exception.getSystemId().indexOf("xml") >= 0) {
          
          output.add(new XMLError(exception.getMessage(), XmlErrorType.WARNING));
          
        } else {
          output.add(new XMLError(exception.getLineNumber(), exception.getMessage(), XmlErrorType.FATALERROR));
        }
      }
      
      @Override
      public void error(SAXParseException exception) throws SAXException {
        // String string = null;
        if(exception.getSystemId().indexOf("xml") >= 0) {
          
          output.add(new XMLError(exception.getMessage(), XmlErrorType.WARNING));
          
        } else {
          output.add(new XMLError(exception.getLineNumber(), exception.getMessage(), XmlErrorType.ERROR));
        }
      }
    });
    
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
