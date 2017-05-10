package model.exercise;

public enum FeedbackLevel {

  NO_FEEDBACK("Kein Feedback"), MINIMAL_FEEDBACK("Minimales Feedback"), MEDIUM_FEEDBACK(
      "Mittleres Feedback"), FULL_FEEDBACK("Volles Feedback");
  
  private String description;
  
  private FeedbackLevel(String theDescription) {
    description = theDescription;
  }
  
  public String getDescription() {
    return description;
  }
}
