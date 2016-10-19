package model;

import model.exercise.EvaluationResult;
import model.exercise.FeedbackLevel;
import model.exercise.Success;

public class XMLError extends EvaluationResult {

  private static final String CVC = "cvc-elt.1.a: ";

  protected int line = -1;
  protected String title = "";

  protected String errorMessage;
  protected XmlErrorType errorType;

  public XMLError(String theTitle, String theErrorMessage, XmlErrorType theErrorType) {
    super(FeedbackLevel.MINIMAL_FEEDBACK, Success.NONE);
    // TODO: test success!
    if(theErrorType == XmlErrorType.WARNING)
      success = Success.PARTIALLY;
    else if(theErrorType == XmlErrorType.NONE)
      success = Success.COMPLETE;

    errorMessage = theErrorMessage.replace(CVC, "");
    errorType = theErrorType;
    title = theTitle;
  }

  public XMLError(String theTitle, String theErrorMessage, XmlErrorType theErrorType, int theLine) {
    this(theTitle, theErrorMessage, theErrorType);
    line = theLine;
  }

  @Override
  public String getAsHtml() {
    // FIXME: Test getAsHtml() in XMLError
    StringBuilder builder = new StringBuilder();
    builder.append("<div class=\"col-md-12\">");
    builder.append("<div class=\"panel panel-" + getBSClass() + "\">");
    builder.append("<div class=\"panel-heading\">" + title + "</div>");

    builder.append("<div class=\"panel-body\">" + errorMessage + "</div>");
    builder.append("</div></div>");

    return builder.toString();
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

  public String getTitle() {
    return title;
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
