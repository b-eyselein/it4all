package model.creation;

import java.util.LinkedList;
import java.util.List;

import model.exercise.CreationResult;
import model.exercise.CreationResultType;
import model.exercise.SqlScenario;

public class ScenarioCreationResult extends CreationResult<SqlScenario> {
  
  private List<ExerciseCreationResult> exerciseResults = new LinkedList<>();
  
  public ScenarioCreationResult(CreationResultType theResultType, String theMessage, SqlScenario theScenario) {
    super(theResultType, theScenario, theMessage);
  }
  
  public void addExerciseResult(ExerciseCreationResult exResult) {
    exerciseResults.add(exResult);
  }
  
  public void addExerciseResults(List<ExerciseCreationResult> exResults) {
    exerciseResults.addAll(exResults);
  }
  
  @Override
  public String getAsHtml() {
    String ret = "";
    switch(resultType) {
    case FAILURE:
      ret = "<div class=\"panel panel-danger\">";
      ret += "<div class=\"panel-heading\">Fehler beim Einlesen des Szenarios:</div>";
      ret += "<div class=\"panel-body\">" + message + "</div>";
      ret += "</div>";
    case NOT_UPDATED:
      ret = getScenarioDescriptionAsHtml();
      ret += "<div class=\"alert alert-" + getBSClass()
          + "\">Das Szenario existiert bereits und muss nicht aktualisiert werden.</div>";
      break;
    case TO_UPDATE:
      ret = getScenarioDescriptionAsHtml();
      ret += "<div class=\"alert alert-" + getBSClass()
          + "\">Das Szenario existiert bereits und muss aktualisiert werden!</div>";
      break;
    case NEW:
      ret = getScenarioDescriptionAsHtml();
      ret += "<div class=\"alert alert-" + getBSClass()
          + "\">Das Szenario existiert noch nicht und muss erstellt werden!</div>";
      break;
    default:
      break;
    }
    return ret;
    
  }
  
  public List<ExerciseCreationResult> getExerciseResults() {
    return exerciseResults;
  }
  
  private String getScenarioDescriptionAsHtml() {
    String ret = "<div class=\"panel panel-default\">";
    ret += "<div class=\"panel-heading\">Beschreibung des Szenarios</div>";
    ret += "<div class=\"panel-body\">";
    ret += "<p>Kurzer Name des Szenarios: \"" + created.shortName + "\"</p>";
    ret += "<p>Vollständiger Name des Szenarios: \"" + created.longName + "\"</p>";
    ret += "<p>SQL-Skriptdatei des Szenarios: \"" + created.scriptFile + "\"</p>";
    ret += "</div>";
    ret += "</div>";
    return ret;
  }
}
