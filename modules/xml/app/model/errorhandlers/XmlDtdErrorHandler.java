package model.errorhandlers;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import model.XmlError;
import model.XmlErrorType;

public class XmlDtdErrorHandler extends CorrectionErrorHandler {

  @Override
  public void error(SAXParseException exception) throws SAXException {
    int lineNumber = -1;
    if(exception.getLineNumber() > -1)
      lineNumber = exception.getLineNumber() - 2;

    output.add(new XmlError(exception.getMessage(), XmlErrorType.ERROR, lineNumber));
  }

  @Override
  public void fatalError(SAXParseException exception) throws SAXException {
    int lineNumber = -1;
    if(exception.getLineNumber() > -1)
      lineNumber = exception.getLineNumber() - 2;

    output.add(new XmlError(exception.getMessage(), XmlErrorType.FATALERROR, lineNumber));
  }

  @Override
  public void warning(SAXParseException exception) throws SAXException {
    int lineNumber = -1;
    if(exception.getLineNumber() > -1)
      lineNumber = exception.getLineNumber() - 2;

    output.add(new XmlError(exception.getMessage(), XmlErrorType.WARNING, lineNumber));
  }

}
