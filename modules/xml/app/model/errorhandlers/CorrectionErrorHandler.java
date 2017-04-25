package model.errorhandlers;

import java.util.LinkedList;
import java.util.List;

import org.xml.sax.ErrorHandler;

import model.XmlError;

public abstract class CorrectionErrorHandler implements ErrorHandler {
  
  protected static final String FAILURE = "Fehler";
  
  protected List<XmlError> output;
  
  public CorrectionErrorHandler() {
    output = new LinkedList<>();
  }
  
  public List<XmlError> getErrors() {
    return output;
  }
  
}
