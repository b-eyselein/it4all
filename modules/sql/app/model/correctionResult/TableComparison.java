package model.correctionResult;

import java.util.List;

import model.exercise.EvaluationResult;
import model.exercise.FeedbackLevel;
import model.exercise.Success;

public class TableComparison extends EvaluationResult {
  
  // FIXME: implement feedbackLevel!
  private List<String> missingTables;
  
  private List<String> unneccessaryTables;
  
  public TableComparison(Success success, List<String> theMissingTables, List<String> theUnneccessaryTables) {
    super(FeedbackLevel.MINIMAL_FEEDBACK, success);
    missingTables = theMissingTables;
    unneccessaryTables = theUnneccessaryTables;
  }
  
  @Override
  public String getAsHtml() {
    String ret = "<div class=\"panel panel-" + getBSClass() + "\">";
    ret += "<div class=\"panel-heading\">Vergleich der verwendeten Tabellen</div>";
    
    ret += "<div class=\"panel-body\">";
    if(success == Success.COMPLETE)
      ret += "<p><span class=\"glyphicon glyphicon-ok\"></span> Es wurden die richtigen Tabellen verwendet.</p>";
    else {
      if(!missingTables.isEmpty())
        ret += "<p>Es fehlen folgende Tabellen:" + concatListElements(missingTables) + "</p>";
      if(!unneccessaryTables.isEmpty())
        ret += "<p>Folgende Tabellen wurden f&auml;lschlicherweise benutzt: " + concatListElements(unneccessaryTables)
            + "</p>";
    }
    ret += "</div>";
    
    ret += "</div>";
    return ret;
  }
  
  public List<String> getMissingTables() {
    return missingTables;
  }
  
  public List<String> getUnneccessaryTables() {
    return unneccessaryTables;
  }
  
}
