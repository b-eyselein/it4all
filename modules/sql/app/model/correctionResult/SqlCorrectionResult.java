package model.correctionResult;

import model.SqlQueryResult;
import model.exercise.EvaluationResult;
import model.exercise.FeedbackLevel;
import model.exercise.Success;

public class SqlCorrectionResult extends EvaluationResult {

  private String message;

  private TableComparison tableComparison = null;
  private ColumnComparison columnComparison = null;

  private SqlQueryResult userResult = null;
  private SqlQueryResult sampleResult = null;

  private FeedbackLevel feedbackLevel;

  public SqlCorrectionResult(Success theSuccess) {
    super(theSuccess);
  }

  public SqlCorrectionResult(Success theSuccess, String theMessage, ColumnComparison theColumnComparison,
      TableComparison theTableComparison, FeedbackLevel theFeedbackLevel) {
    super(theSuccess);
    message = theMessage;
    columnComparison = theColumnComparison;
    tableComparison = theTableComparison;
    feedbackLevel = theFeedbackLevel;
  }

  public SqlCorrectionResult(Success theSuccess, String theMessage, FeedbackLevel theFeedbackLevel) {
    super(theSuccess);
    message = theMessage;
    feedbackLevel = theFeedbackLevel;
  }

  @Override
  public String getAsHtml() {
    String ret = "<div class=\"alert alert-success\">Ihre Query wurde erfolgreich korrigiert.</div>";

    if(feedbackLevel.compareTo(FeedbackLevel.MINIMAL_FEEDBACK) >= 0)
      ret += "<div class=\"alert alert-" + getBSClass() + "\"><p>" + message + "</p></div>";

    if(feedbackLevel.compareTo(FeedbackLevel.FULL_FEEDBACK) >= 0) {
      ret += tableComparison != null ? tableComparison.getAsHtml() : "";
      ret += columnComparison != null ? columnComparison.getAsHtml() : "";
    }

    if(feedbackLevel.compareTo(FeedbackLevel.MEDIUM_FEEDBACK) >= 0)
      ret += resultsAsHtml();

    return ret;
  }

  public ColumnComparison getColumnComparison() {
    return columnComparison;
  }

  public String getMessage() {
    return message;
  }

  public SqlQueryResult getSampleResult() {
    return sampleResult;
  }

  public TableComparison getTableComparison() {
    return tableComparison;
  }

  public SqlQueryResult getUserResult() {
    return userResult;
  }

  public SqlCorrectionResult withColumnsComparisonResult(ColumnComparison theUsedColumnsComparison) {
    columnComparison = theUsedColumnsComparison;
    return this;
  }

  public SqlCorrectionResult withExecutionResult(SqlQueryResult theUserResult, SqlQueryResult theSampleResult) {
    userResult = theUserResult;
    sampleResult = theSampleResult;
    return this;
  }

  public SqlCorrectionResult withTableComparisonResult(TableComparison theTableComparisonResult) {
    tableComparison = theTableComparisonResult;
    return this;
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