package model.correctionResult;

import java.util.List;

import model.exercise.EvaluationResult;
import model.exercise.Success;

public class UsedColumnsComparison extends EvaluationResult {

  private List<String> missingColumns;

  private List<String> wrongColumns;

  public UsedColumnsComparison(Success success, List<String> theMissingColumns, List<String> theWrongColumns) {
    super(success);
    missingColumns = theMissingColumns;
    wrongColumns = theWrongColumns;
  }

  public String getMessage() {
    String msg = "";
    if(!missingColumns.isEmpty())
      msg += "Es fehlen folgende Spalten: " + missingColumns + "\n";
    if(!wrongColumns.isEmpty())
      msg += "Folgende Spalten sollen nicht ausgegeben werden: " + wrongColumns + "\n";
    return msg;
  }

  public List<String> getMissingColumns() {
    return missingColumns;
  }

  public List<String> getWrongColumns() {
    return wrongColumns;
  }

}
