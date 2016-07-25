package model;

import java.util.List;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XSDErrorHandler implements ErrorHandler {
  private List<XMLError> Output;

  public XSDErrorHandler(List<XMLError> output) {
    this.Output = output;
  }

  @Override
  public void warning(SAXParseException exception) throws SAXException {
    if(exception.getLineNumber() > -1) {
      Output.add(new XMLError(exception.getLineNumber()-2, exception.getMessage(), XmlErrorType.WARNING));
    } else {
      Output.add(new XMLError(XmlErrorType.WARNING.toString(), exception.getMessage(), XmlErrorType.WARNING));
    }
  }

  @Override
  public void fatalError(SAXParseException exception) throws SAXException {
    if(exception.getLineNumber() > -1) {
      Output.add(new XMLError(exception.getLineNumber()-2, exception.getMessage(), XmlErrorType.FATALERROR));
    } else {
      Output.add(new XMLError(XmlErrorType.FATALERROR.toString(), exception.getMessage(), XmlErrorType.FATALERROR));
    }
  }

  @Override
  public void error(SAXParseException exception) throws SAXException {
    if(exception.getLineNumber() > -1) {
      Output.add(new XMLError(exception.getLineNumber()-2, exception.getMessage(), XmlErrorType.ERROR));
    } else {
      Output.add(new XMLError(XmlErrorType.ERROR.toString(), exception.getMessage(), XmlErrorType.ERROR));
    }

  }
}
