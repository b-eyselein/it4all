package model;

import model.exercise.FeedbackLevel;
import model.result.EvaluationResult;

public class XMLError extends EvaluationResult {

  protected int line = -1;
  protected String errorMessage;
  protected XmlErrorType errorType;

  public XMLError(String theErrorMessage, XmlErrorType theErrorType, int theLine) {
    super(FeedbackLevel.MINIMAL_FEEDBACK, theErrorType.getSuccess());
    errorMessage = theErrorMessage;
    errorType = theErrorType;
    line = theLine;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public XmlErrorType getErrorType() {
    return errorType;
  }

  public int getLine() {
    return line;
  }

  public String getLineStr() {
    return line != -1 ? " in Zeile " + line : "";
  }

  @Override
  public String toString() {
    if(errorType == XmlErrorType.NONE)
      return errorType.toString();
    else
      return (line != -1) ? errorType + ":\nZeile: " + line + "\nFehler: " + errorMessage + "\n"
          : errorType + ":\nFehler: " + errorMessage + "\n";
  }
}
