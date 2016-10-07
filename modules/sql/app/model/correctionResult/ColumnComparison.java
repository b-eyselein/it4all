package model.correctionResult;

import java.util.List;

import model.exercise.EvaluationResult;
import model.exercise.FeedbackLevel;
import model.exercise.Success;

public class ColumnComparison extends EvaluationResult {

  private List<String> missingColumns;

  private List<String> wrongColumns;

  public ColumnComparison(Success success, List<String> theMissingColumns, List<String> theWrongColumns) {
    super(FeedbackLevel.MEDIUM_FEEDBACK, success);
    missingColumns = theMissingColumns;
    wrongColumns = theWrongColumns;
  }

  @Override
  public String getAsHtml() {
    String ret = "<div class=\"panel panel-" + getBSClass() + "\">";
    ret += "<div class=\"panel-heading\">Vergleich der Spalten</div>";

    ret += "<div class=\"panel-body\">";
    if(success == Success.COMPLETE) {
      ret += "<p><span class=\"glyphicon glyphicon-ok\"></span> Es wurden die richtigen Spalten benutzt.</p>";
    } else {
      if(!missingColumns.isEmpty())
        ret += "<p>Es fehlen folgende Spalten: " + concatListElements(missingColumns) + "</p>";
      if(!wrongColumns.isEmpty())
        ret += "<p>Folgende Spalten wurden f&auml;lschlicherweise benutzt:" + concatListElements(wrongColumns) + "</p>";
    }
    ret += "</div></div>";
    return ret;
  }

  public List<String> getMissingColumns() {
    return missingColumns;
  }

  public List<String> getWrongColumns() {
    return wrongColumns;
  }

}
