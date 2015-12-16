package model.spreadsheet;

import java.util.List;

public class SpreadSheetCorrectionResult {
  
  private boolean wasSuccessful;
  
  private List<String> messages;
  
  public SpreadSheetCorrectionResult(boolean success, List<String> notices) {
    wasSuccessful = success;
    messages = notices;
  }
  
  public boolean isSuccess() {
    return wasSuccessful;
  }
  
  public List<String> getNotices() {
    return messages;
  }
  
}
