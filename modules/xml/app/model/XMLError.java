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

  @Override
  public String getAsHtml() {
    throw new IllegalArgumentException("Cannot be used anymore!");
    // StringBuilder builder = new StringBuilder();
    // builder.append("<div class=\"col-md-12\">");
    // builder.append("<div class=\"panel panel-" + getBSClass() + "\">");
    // builder.append(
    // "<div class=\"panel-heading\">" + errorType.getTitle() + (line != -1 ? "
    // in Zeile " + line : "") + "</div>");
    //
    // builder.append("<div class=\"panel-body\">" + errorMessage + "</div>");
    // builder.append("</div></div>");
    //
    // return builder.toString();
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
