package model.errorhandlers;

import java.util.LinkedList;
import java.util.List;

import org.xml.sax.ErrorHandler;

import model.exercise.EvaluationResult;

public abstract class CorrectionErrorHandler implements ErrorHandler {

  protected static final String TITEL_TODO = "TITEL!";

  protected List<EvaluationResult> output;

  public CorrectionErrorHandler() {
    output = new LinkedList<>();
  }

  public List<EvaluationResult> getErrors() {
    return output;
  }

}
