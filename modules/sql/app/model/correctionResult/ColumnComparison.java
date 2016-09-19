package model.correctionResult;

import java.util.List;

import model.exercise.EvaluationResult;
import model.exercise.Success;

public class ColumnComparison extends EvaluationResult {

  private List<String> missingColumns;

  private List<String> wrongColumns;

  public ColumnComparison(Success success, List<String> theMissingColumns, List<String> theWrongColumns) {
    super(success);
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
        ret += "<p>Es fehlen folgende Spalten:<ul><li>" + String.join("</li><li>", missingColumns) + "</li></ul></p>";
      if(!wrongColumns.isEmpty())
        ret += "<p>Folgende Spalten wurden f&auml;lschlicherweise benutzt:<ul><li>"
            + String.join("</li><li>", wrongColumns) + "</li></ul></p>";
    }
    ret += "</div>";

    ret += "</div>";
    return ret;
  }

  public List<String> getMissingColumns() {
    return missingColumns;
  }

  public List<String> getWrongColumns() {
    return wrongColumns;
  }

}
