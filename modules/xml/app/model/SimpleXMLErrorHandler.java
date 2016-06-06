package model;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;

import java.util.List;


public class SimpleXMLErrorHandler implements ErrorHandler {
  private List<XMLError> Output;

  public SimpleXMLErrorHandler(List<XMLError> output) {
    this.Output = output;
  }
	
  @Override
  public void warning(SAXParseException exception) throws SAXException {
    Output.add(new XMLError(exception.getLineNumber(), exception.getMessage(), XmlErrorType.WARNING));
  }
  
  @Override
  public void fatalError(SAXParseException exception) throws SAXException {
    Output.add(new XMLError(exception.getLineNumber(), exception.getMessage(), XmlErrorType.FATALERROR));
  }
  
  @Override
  public void error(SAXParseException exception) throws SAXException {
    Output.add(new XMLError(exception.getLineNumber(), exception.getMessage(), XmlErrorType.ERROR));
    
  }
}
