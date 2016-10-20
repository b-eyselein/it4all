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
      break;
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
    
    ret += "<div class=\"form-group\">";
    ret += "<label for=\"shortname\" class=\"control-label col-sm-2\">Identifizierung des Szenarios:</label>";
    ret += "<div class=\"col-sm-10\">";
    ret += "<input class=\"form-control\" name=\"shortname\" id=\"shortname\" value=\"" + created.shortName + "\">";
    ret += "</div></div>";
    
    ret += "<div class=\"form-group\">";
    ret += "<label for=\"longname\" class=\"control-label col-sm-2\">Vollständiger Name des Szenarios:</label>";
    ret += "<div class=\"col-sm-10\">";
    ret += "<input class=\"form-control\" name=\"longname\" id=\"longname\" value=\"" + created.longName + "\">";
    ret += "</div></div>";
    
    ret += "<div class=\"form-group\">";
    ret += "<label for=\"scriptFile\" class=\"control-label col-sm-2\">SQL-Skriptdatei des Szenarios:</label>";
    ret += "<div class=\"col-sm-10\">";
    ret += "<input class=\"form-control\" name=\"scriptfile\" id=\"scriptfile\" value=\"" + created.scriptFile + "\">";
    ret += "</div></div>";
    
    ret += "</div>";
    ret += "</div>";
    return ret;
  }
}
