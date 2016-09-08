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
    // FIXME Auto-generated method stub
    return null;
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
