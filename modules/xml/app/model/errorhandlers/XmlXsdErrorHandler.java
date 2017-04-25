package model.errorhandlers;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import model.XmlError;
import model.XmlErrorType;

public class XmlXsdErrorHandler extends CorrectionErrorHandler {

  @Override
  public void error(SAXParseException exception) throws SAXException {
    output.add(new XmlError(exception.getMessage(), XmlErrorType.ERROR, exception.getLineNumber()));
  }

  @Override
  public void fatalError(SAXParseException exception) throws SAXException {
    output.add(new XmlError(exception.getMessage(), XmlErrorType.FATALERROR, exception.getLineNumber()));
  }

  @Override
  public void warning(SAXParseException exception) throws SAXException {
    output.add(new XmlError(exception.getMessage(), XmlErrorType.WARNING, exception.getLineNumber()));
  }

}
