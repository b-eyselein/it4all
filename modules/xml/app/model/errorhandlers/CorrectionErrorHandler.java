package model.errorhandlers;

import java.util.LinkedList;
import java.util.List;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import model.XmlError;
import model.XmlErrorType;

public class CorrectionErrorHandler implements ErrorHandler {
  
  protected static final String FAILURE = "Fehler";
  
  protected List<XmlError> output;
  
  public CorrectionErrorHandler() {
    output = new LinkedList<>();
  }
  
  @Override
  public void error(SAXParseException exception) throws SAXException {
    output.add(new XmlError(exception.getMessage(), XmlErrorType.ERROR, exception.getLineNumber()));
  }
  
  @Override
  public void fatalError(SAXParseException exception) throws SAXException {
    output.add(new XmlError(exception.getMessage(), XmlErrorType.FATALERROR, exception.getLineNumber()));
  }
  
  public List<XmlError> getErrors() {
    return output;
  }
  
  @Override
  public void warning(SAXParseException exception) throws SAXException {
    output.add(new XmlError(exception.getMessage(), XmlErrorType.WARNING, exception.getLineNumber()));
  }
}
