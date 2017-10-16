package model.feedback;

import model.StringConsts$;

public enum EvaluatedAspect {
  
  // @formatter:off
  USED("Wie oft haben Sie dieses Tool genutzt?", "Oft", "Manchmal", "Selten"),
  
  SENSE("Finden Sie dieses Tool sinnvoll?", "Sinnvoll", StringConsts$.MODULE$.NEUTRAL(), "Sinnlos"),
  
  USABILITY("Wie bewerten Sie die allgemeine Bedienbarkeit dieses Tools?", "Gut", StringConsts$.MODULE$.NEUTRAL(), "Schlecht"),  
  
  STYLE_OF_FEEDBACK("Wie bewerten Sie die Gestaltung des Feedbacks dieses Tools?", "Gut", StringConsts$.MODULE$.NEUTRAL(), "Schlecht"),
  
  FAIRNESS_OF_FEEDBACK("Wie bewerten Sie die Fairness der Evaluation dieses Tools?", "Fair", StringConsts$.MODULE$.NEUTRAL(), "Unfair");
  // @formatter:on
  
  private String question;
  private String positive;
  private String neutral;
  private String negative;
  
  private EvaluatedAspect(String theQuestion, String thePositive, String theNeutral, String theNegative) {
    question = theQuestion;
    positive = thePositive;
    neutral = theNeutral;
    negative = theNegative;
  }
  
  public String getQuestion() {
    return question;
  }
  
  public String getPositive() {
    return positive;
  }
  
  public String getNeutral() {
    return neutral;
  }
  
  public String getNegative() {
    return negative;
  }
  
}