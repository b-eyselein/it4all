package model.correctionResult;

import java.util.LinkedList;
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
    String ret = "<div class=\"alert alert-" + getBSClass() + "\">";
    ret += "<p><u>Ein Vergleich der verwendeten Spalten brachte folgendes Ergebnis:</u></p>";

    if(success == Success.COMPLETE) {
      ret += "<p><span class=\"glyphicon glyphicon-ok\"></span> Es wurden die richtigen Spalten verwendet.</p>";
    } else {
      if(!missingColumns.isEmpty())
        ret += "<p>Es fehlen folgende Spalten:<ul><li>" + String.join("</li><li>", missingColumns) + "</li></ul></p>";
      if(!wrongColumns.isEmpty())
        ret += "<p>Folgende Spalten wurden f&auml;lschlicherweise benutzt:<ul><li>"
            + String.join("</li><li>", wrongColumns) + "</li></ul></p>";
    }

    ret += "</div>";
    return ret;
  }

  public List<String> getMessages() {
    List<String> ret = new LinkedList<>();
    if(!missingColumns.isEmpty())
      ret.add("Es fehlen folgende Spalten: " + missingColumns);
    if(!wrongColumns.isEmpty())
      ret.add("Folgende Spalten sollen nicht ausgegeben werden: " + wrongColumns);
    return ret;
  }

  public List<String> getMissingColumns() {
    return missingColumns;
  }

  public List<String> getWrongColumns() {
    return wrongColumns;
  }

}
