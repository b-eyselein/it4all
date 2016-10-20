package model.errorhandlers;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import model.XMLError;
import model.XmlErrorType;

public class XmlXsdErrorHandler extends CorrectionErrorHandler {
  
  @Override
  public void error(SAXParseException exception) throws SAXException {
    if(exception.getLineNumber() > -1) {
      output.add(new XMLError(FAILURE, exception.getMessage(), XmlErrorType.ERROR, exception.getLineNumber()));
    } else {
      output.add(new XMLError(XmlErrorType.ERROR.toString(), exception.getMessage(), XmlErrorType.ERROR));
    }
  }
  
  @Override
  public void fatalError(SAXParseException exception) throws SAXException {
    if(exception.getLineNumber() > -1) {
      output.add(new XMLError(FAILURE, exception.getMessage(), XmlErrorType.FATALERROR, exception.getLineNumber()));
    } else {
      output.add(new XMLError(XmlErrorType.FATALERROR.toString(), exception.getMessage(), XmlErrorType.FATALERROR));
    }
  }
  
  @Override
  public void warning(SAXParseException exception) throws SAXException {
    if(exception.getLineNumber() > -1) {
      output.add(new XMLError(FAILURE, exception.getMessage(), XmlErrorType.WARNING, exception.getLineNumber()));
    } else {
      output.add(new XMLError(XmlErrorType.WARNING.toString(), exception.getMessage(), XmlErrorType.WARNING));
    }
  }
  
}
