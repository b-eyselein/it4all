package model.correctionResult;

import java.util.LinkedList;
import java.util.List;

import model.exercise.EvaluationResult;
import model.exercise.Success;

public class TableComparison extends EvaluationResult {

  private List<String> missingTables;

  private List<String> unneccessaryTables;

  public TableComparison(Success success, List<String> theMissingTables, List<String> theUnneccessaryTables) {
    super(success);
    missingTables = theMissingTables;
    unneccessaryTables = theUnneccessaryTables;
  }

  @Override
  public String getAsHtml() {
    // FIXME Auto-generated method stub
    return null;
  }

  public List<String> getMessages() {
    List<String> ret = new LinkedList<>();
    if(!missingTables.isEmpty())
      ret.add("Es fehlen folgende Tabellen: " + missingTables);
    if(!unneccessaryTables.isEmpty())
      ret.add("Folgende Tabellen werden nicht benötigt: " + unneccessaryTables);
    return ret;
  }

  public List<String> getMissingTables() {
    return missingTables;
  }

  public List<String> getUnneccessaryTables() {
    return unneccessaryTables;
  }

}
