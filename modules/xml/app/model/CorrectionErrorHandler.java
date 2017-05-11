package model;

import java.util.LinkedList;
import java.util.List;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class CorrectionErrorHandler implements ErrorHandler {

  private List<XmlError> output = new LinkedList<>();

  @Override
  public void error(SAXParseException exception) throws SAXException {
    output.add(new XmlError(exception, XmlErrorType.ERROR));
  }

  @Override
  public void fatalError(SAXParseException exception) throws SAXException {
    output.add(new XmlError(exception, XmlErrorType.FATALERROR));
  }

  public List<XmlError> getErrors() {
    return output;
  }

  @Override
  public void warning(SAXParseException exception) throws SAXException {
    output.add(new XmlError(exception, XmlErrorType.WARNING));
  }
}
