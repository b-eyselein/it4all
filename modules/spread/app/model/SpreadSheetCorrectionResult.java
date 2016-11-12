package model;

import java.util.LinkedList;
import java.util.List;

public class SpreadSheetCorrectionResult {

  private boolean wasSuccessful;

  private List<String> messages = new LinkedList<>();

  public SpreadSheetCorrectionResult(boolean success, List<String> notices) {
    wasSuccessful = success;
    messages.addAll(notices);
  }

  public SpreadSheetCorrectionResult(boolean success, String notice) {
    wasSuccessful = success;
    messages.add(notice);
  }

  public List<String> getNotices() {
    return messages;
  }

  public boolean isSuccess() {
    return wasSuccessful;
  }

}
