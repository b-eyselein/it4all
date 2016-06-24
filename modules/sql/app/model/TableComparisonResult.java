package model;

import java.util.List;

public class TableComparisonResult {
  
  private List<String> missingTables;

  private List<String> unneccessaryTables;
  
  public List<String> getMissingTables() {
    return missingTables;
  }

  public List<String> getUnneccessaryTables() {
    return unneccessaryTables;
  }
  
  public TableComparisonResult withMissingTables(List<String> theMissingTables) {
    missingTables = theMissingTables;
    return this;
  }

  public TableComparisonResult withUnneccessaryTables(List<String> theUnneccessaryTables) {
    unneccessaryTables = theUnneccessaryTables;
    return this;
  }

}
