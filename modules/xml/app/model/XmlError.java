package model;

import org.xml.sax.SAXParseException;

import model.exercise.FeedbackLevel;
import model.result.EvaluationResult;

public class XmlError extends EvaluationResult {

  protected XmlErrorType errorType;

  protected int line;
  protected String errorMessage;

  public XmlError(SAXParseException exception, XmlErrorType theErrorType) {
    this(exception.getMessage(), exception.getLineNumber(), theErrorType);
  }

  public XmlError(String theMessage, int lineNumber, XmlErrorType theErrorType) {
    super(FeedbackLevel.MINIMAL_FEEDBACK, theErrorType.getSuccess());
    line = lineNumber;

    errorType = theErrorType;
    errorMessage = theMessage;
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
