package model.matcher;

import model.UmlClass;
import model.exercise.FeedbackLevel;
import model.matching.Match;

public class UmlClassMatch extends Match<UmlClass> {

  public UmlClassMatch(UmlClass theArg1, UmlClass theArg2) {
    super(FeedbackLevel.MINIMAL_FEEDBACK, theArg1, theArg2);
  }

  @Override
  public void analyze() {
    // ??
  }

  public String getName() {
    return arg1.getName();
  }

}
