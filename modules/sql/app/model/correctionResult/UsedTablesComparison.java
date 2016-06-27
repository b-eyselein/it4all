package model.correctionResult;

import java.util.List;

import model.exercise.EvaluationResult;
import model.exercise.Success;

public class UsedTablesComparison extends EvaluationResult {

  private List<String> missingTables;

  private List<String> unneccessaryTables;

  public UsedTablesComparison(Success success, List<String> theMissingTables, List<String> theUnneccessaryTables) {
    super(success);
    missingTables = theMissingTables;
    unneccessaryTables = theUnneccessaryTables;
  }

  public String getMessage() {
    String msg = "";
    if(!missingTables.isEmpty())
      msg += "Es fehlen folgende Tabellen: " + missingTables + "\n";
    if(!unneccessaryTables.isEmpty())
      msg += "Folgende Tabellen werden nicht benötigt: " + unneccessaryTables + "\n";
    return msg;
  }

  public List<String> getMissingTables() {
    return missingTables;
  }

  public List<String> getUnneccessaryTables() {
    return unneccessaryTables;
  }

}
