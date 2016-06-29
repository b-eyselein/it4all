package model;

import java.util.List;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class SimpleXMLErrorHandler implements ErrorHandler {
  private List<XMLError> output;

  public SimpleXMLErrorHandler(List<XMLError> output) {
    this.output = output;
  }

  @Override
  public void error(SAXParseException exception) throws SAXException {
    if(exception.getLineNumber() > -1) {
      output.add(new XMLError(exception.getLineNumber(), exception.getMessage(), XmlErrorType.ERROR));
    } else {
      output.add(new XMLError(XmlErrorType.ERROR.toString(), exception.getMessage(), XmlErrorType.ERROR));
    }

  }

  @Override
  public void fatalError(SAXParseException exception) throws SAXException {
    if(exception.getLineNumber() > -1) {
      output.add(new XMLError(exception.getLineNumber(), exception.getMessage(), XmlErrorType.FATALERROR));
    } else {
      output.add(new XMLError(XmlErrorType.FATALERROR.toString(), exception.getMessage(), XmlErrorType.FATALERROR));
    }
  }

  @Override
  public void warning(SAXParseException exception) throws SAXException {
    if(exception.getLineNumber() > -1) {
      output.add(new XMLError(exception.getLineNumber(), exception.getMessage(), XmlErrorType.WARNING));
    } else {
      output.add(new XMLError(XmlErrorType.WARNING.toString(), exception.getMessage(), XmlErrorType.WARNING));
    }

  }
}
