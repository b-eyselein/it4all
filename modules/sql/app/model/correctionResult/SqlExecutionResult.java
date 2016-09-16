package model.correctionResult;

import model.SqlQueryResult;
import model.exercise.EvaluationResult;
import model.exercise.FeedbackLevel;
import model.exercise.Success;

public class SqlExecutionResult extends EvaluationResult {

  private String message;

  private SqlQueryResult userResult;
  private SqlQueryResult sampleResult;

  private FeedbackLevel feedbackLevel;

  public SqlExecutionResult(Success theSuccess, String theMessage, FeedbackLevel theFeedbackLevel,
      SqlQueryResult theUserResult, SqlQueryResult theSampleResult) {
    super(theSuccess);
    message = theMessage;
    feedbackLevel = theFeedbackLevel;
    userResult = theUserResult;
    sampleResult = theSampleResult;
  }

  @Override
  public String getAsHtml() {
    String ret = "<div class=\"alert alert-success\">Ihre Query wurde erfolgreich korrigiert.</div>";

    if(feedbackLevel.compareTo(FeedbackLevel.MINIMAL_FEEDBACK) >= 0)
      ret += "<div class=\"alert alert-" + getBSClass() + "\"><p>" + message + "</p></div>";

    if(feedbackLevel.compareTo(FeedbackLevel.MEDIUM_FEEDBACK) >= 0)
      ret += resultsAsHtml();

    return ret;
  }

  public String getMessage() {
    return message;
  }

  public SqlQueryResult getSampleResult() {
    return sampleResult;
  }

  public SqlQueryResult getUserResult() {
    return userResult;
  }

  private String resultsAsHtml() {
    String ret = "";
    ret += "<div class=\"panel panel-" + getBSClass() + "\">";
    ret += "<div class=\"panel-heading\">Vergleich ihrer L&ouml;sung mit der Musterl&ouml;sung</div>";

    ret += "<div class=\"panel-body\">";
    if(success == Success.COMPLETE) {
      // Results are identical
      ret += "<p>Ihre L&ouml;sung == Musterl&ouml;sung</p>" + userResult.toHtmlTable();
    } else {
      // Results are not identical
      ret += "<div class=\"col-md-6\"><p>Ihre L&ouml;sung:</p>";
      if(userResult == null)
        ret += "Es gab einen Fehler bei der Ausf&uuml;hrung ihrer L&ouml;sung!";
      else
        ret += userResult.toHtmlTable();
      ret += "</div>";

      ret += "<div class=\"col-md-6\"><p>Musterl&ouml;sung:</p>";
      if(sampleResult != null)
        ret += sampleResult.toHtmlTable();
      ret += "</div>";
    }
    ret += "</div>";

    ret += "</div>";
    return ret;
  }

}