package model.matching;

import model.exercise.FeedbackLevel;

public class StringMatch extends Match<String> {

  public StringMatch(String theArg1, String theArg2) {
    super(FeedbackLevel.MINIMAL_FEEDBACK, theArg1, theArg2);
  }

  @Override
  public void analyze() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public String toString() {
    return arg1;
  }

}
