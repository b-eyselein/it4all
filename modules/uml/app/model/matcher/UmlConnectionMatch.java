package model.matcher;

import model.UmlConnection;
import model.exercise.FeedbackLevel;
import model.matching.Match;

public class UmlConnectionMatch extends Match<UmlConnection> {

  public UmlConnectionMatch(UmlConnection theArg1, UmlConnection theArg2) {
    super(FeedbackLevel.MINIMAL_FEEDBACK, theArg1, theArg2);
  }

  @Override
  public void analyze() {
    // ??
  }

}
