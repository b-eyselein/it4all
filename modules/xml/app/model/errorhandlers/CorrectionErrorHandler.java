package model.errorhandlers;

import java.util.LinkedList;
import java.util.List;

import org.xml.sax.ErrorHandler;

import model.XMLError;

public abstract class CorrectionErrorHandler implements ErrorHandler {
  
  protected static final String FAILURE = "Fehler";
  
  protected List<XMLError> output;
  
  public CorrectionErrorHandler() {
    output = new LinkedList<>();
  }
  
  public List<XMLError> getErrors() {
    return output;
  }
  
}
