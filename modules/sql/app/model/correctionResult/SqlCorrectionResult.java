package model.correctionResult;

import java.util.LinkedList;
import java.util.List;

import model.SqlQueryResult;
import model.exercise.EvaluationResult;
import model.exercise.Success;

public class SqlCorrectionResult extends EvaluationResult {

  private static String toTable(SqlQueryResult queryResult) {
    String table = "";
    table += "<div class=\"table-responsive\">";
    table += "  <table class=\"table table-bordered table-condensed\">";
    table += "    <thead>";
    table += "      <tr>";
    for(String columnName: queryResult.getColumnNames())
      table += "<th>" + columnName + "</th>";
    table += "      </tr>";
    table += "    </thead>";

    table += "    <tbody>";
    for(List<String> row: queryResult.getRows()) {
      table += "      <tr>";
      for(String cell: row)
        table += "<td>" + cell + "</td>";
      table += "      </tr>";
    }
    table += "    </tbody>";
    table += "  </table>";
    table += "</div>";
    return table;
  }

  private List<String> messages = new LinkedList<>();
  private TableComparison tableComparison = null;

  private ColumnComparison columnComparison = null;
  private SqlQueryResult userResult = null;

  private SqlQueryResult sampleResult = null;

  public SqlCorrectionResult(Success theSuccess) {
    super(theSuccess);
  }

  public SqlCorrectionResult(Success theSuccess, ColumnComparison theColumnComparison,
      TableComparison theTableComparison) {
    super(theSuccess);
    columnComparison = theColumnComparison;
    tableComparison = theTableComparison;
  }

  public SqlCorrectionResult(Success theSuccess, List<String> theMessages) {
    super(theSuccess);
    messages.addAll(theMessages);
  }

  public SqlCorrectionResult(Success theSuccess, String aMessage) {
    super(theSuccess);
    messages.add(aMessage);
  }

  @Override
  public String getAsHtml() {
    // FIXME Auto-generated method stub
    String ret = "<div class=\"alert alert-" + getBSClass() + "\">";
    for(String m: messages)
      ret += m;
    ret += "</div>";

    if(tableComparison != null)
      ret += tableComparison.getAsHtml();

    if(columnComparison != null)
      ret += columnComparison.getAsHtml();

    // FIXME: Result of both queries!
    ret += "<div class=\"row\">";

    ret += "<div class=\"col-md-6\">";
    ret += "<p>Ihre L&ouml;sung:</p>";
    if(userResult == null)
      ret += "Es gab einen Fehler bei der Ausf&uuml;hrung ihrer L&ouml;sung!";
    else
      ret += toTable(userResult);
    ret += "</div>";

    ret += "<div class=\"col-md-6\">";
    ret += "<p>Musterl&ouml;sung:</p>";
    if(sampleResult != null)
      ret += toTable(sampleResult);
    ret += "</div>";

    ret += "</div>";

    System.out.println(ret);
    System.out.println();
    return ret;
  }

  public ColumnComparison getColumnComparison() {
    return columnComparison;
  }

  public List<String> getMessages() {
    return messages;
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

}