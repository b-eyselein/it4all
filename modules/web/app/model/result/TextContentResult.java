package model.result;

import model.exercise.FeedbackLevel;
import model.exercise.Success;

public class TextContentResult extends EvaluationResult {

  private String awaitedContent;
  private String foundContent;

  public TextContentResult(String theFoundContent, String theAwaitedContent) {
    super(FeedbackLevel.MINIMAL_FEEDBACK, analyze(theFoundContent, theAwaitedContent));
    awaitedContent = theAwaitedContent;
    foundContent = theFoundContent;
  }

  private static Success analyze(String foundValue, String awaitedValue) {
    if(foundValue == null)
      return Success.NONE;

    if(!foundValue.contains(awaitedValue))
      return Success.PARTIALLY;

    return Success.COMPLETE;
  }

  public String getAwaitedContent() {
    return awaitedContent;
  }

  public String getFoundContent() {
    return foundContent;
  }

}
