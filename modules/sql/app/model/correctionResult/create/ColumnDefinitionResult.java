package model.correctionResult.create;

import model.exercise.EvaluationResult;
import model.exercise.Success;

public class ColumnDefinitionResult extends EvaluationResult {

  private String datatypeName;
  private String message;

  public ColumnDefinitionResult(Success theSuccess, String theDatatypeName, String theMessage) {
    super(theSuccess);
    datatypeName = theDatatypeName;
    message = theMessage;
  }

  @Override
  public String getAsHtml() {
    String ret = "<div class=\"panel panel-" + getBSClass() + "\">";
    ret += "<div class=\"panel-heading\">Vergleich für Spalte " + datatypeName + "</div>";
    ret += "<div class=\"panel-body\">" + message + "</div>";
    ret += "</div>";
    return ret;
  }

}
