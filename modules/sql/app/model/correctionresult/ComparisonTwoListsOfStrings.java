package model.correctionresult;

import java.util.List;

import model.exercise.FeedbackLevel;
import model.exercise.Success;
import model.result.EvaluationResult;

public class ComparisonTwoListsOfStrings extends EvaluationResult {

  private String name;

  private List<String> missing;
  private List<String> wrong;

  // FIXME: not using matcher?

  public ComparisonTwoListsOfStrings(String theName, List<String> theMissing, List<String> theWrong) {
    super(FeedbackLevel.MEDIUM_FEEDBACK, analyze(theMissing, theWrong));
    name = theName;
    missing = theMissing;
    wrong = theWrong;
  }

  private static Success analyze(List<String> theMissing, List<String> theWrong) {
    if(theMissing.isEmpty() && theWrong.isEmpty())
      return Success.COMPLETE;
    else if(theMissing.isEmpty())
      return Success.PARTIALLY;
    else
      return Success.NONE;
  }

  public List<String> getMissing() {
    return missing;
  }

  public String getName() {
    return name;
  }

  public List<String> getWrong() {
    return wrong;
  }

}
