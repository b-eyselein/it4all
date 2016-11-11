package model.feedback;

public enum Evaluated {
  USABILITY("Allgemeine Bedienbarkeit"), FEEDBACK("Gestaltung des Feedbacks"), FAIRNESS("Fairness der Evaluation");
  
  private String question;
  
  private Evaluated(String theQuestion) {
    question = theQuestion;
  }

  public String getQuestion() {
    return question;
  }
}
