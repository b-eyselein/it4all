package model.correctionResult;

import model.SqlQueryResult;
import model.exercise.EvaluationResult;
import model.exercise.FeedbackType;
import model.exercise.Success;

public class SqlCorrectionResult extends EvaluationResult {

  private String message;

  private TableComparison tableComparison = null;
  private ColumnComparison columnComparison = null;

  private SqlQueryResult userResult = null;
  private SqlQueryResult sampleResult = null;

  private FeedbackType feedbackType = FeedbackType.FULL_FEEDBACK;

  public SqlCorrectionResult(Success theSuccess) {
    super(theSuccess);
  }

  public SqlCorrectionResult(Success theSuccess, String theMessage) {
    super(theSuccess);
    message = theMessage;
  }

  public SqlCorrectionResult(Success theSuccess, String theMessage, ColumnComparison theColumnComparison,
      TableComparison theTableComparison) {
    super(theSuccess);
    message = theMessage;
    columnComparison = theColumnComparison;
    tableComparison = theTableComparison;
  }

  @Override
  public String getAsHtml() {
    String ret = "<div class=\"alert alert-" + getBSClass() + "\"><p>" + message + "</p></div>";

    if(feedbackType.compareTo(FeedbackType.FULL_FEEDBACK) >= 0) {
      ret += tableComparison != null ? tableComparison.getAsHtml() : "";
      ret += columnComparison != null ? columnComparison.getAsHtml() : "";
    }

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
    // TODO: refactor!
    String ret = "";
    ret += "<div class=\"row\">";

    ret += "<div class=\"col-md-6\">";
    ret += "<p>Ihre L&ouml;sung:</p>";
    if(userResult == null)
      ret += "Es gab einen Fehler bei der Ausf&uuml;hrung ihrer L&ouml;sung!";
    else
      ret += userResult.toHtmlTable();
    ret += "</div>";

    ret += "<div class=\"col-md-6\">";
    ret += "<p>Musterl&ouml;sung:</p>";
    if(sampleResult != null)
      ret += sampleResult.toHtmlTable();
    ret += "</div>";

    ret += "</div>";
    return ret;
  }

}