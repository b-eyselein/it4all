package model.result;

import model.Attribute;
import model.exercise.FeedbackLevel;
import model.exercise.Success;

public class AttributeResult extends EvaluationResult {

  private Attribute attribute;
  
  private String foundValue;

  public AttributeResult(Attribute theAttribute, String theFoundValue) {
    super(FeedbackLevel.MEDIUM_FEEDBACK, analyze(theFoundValue, theAttribute.value));
    attribute = theAttribute;
    foundValue = theFoundValue;
  }

  private static Success analyze(String foundValue, String awaitedValue) {
    if(foundValue == null)
      return Success.NONE;

    if(!foundValue.contains(awaitedValue))
      return Success.PARTIALLY;

    return Success.COMPLETE;
  }

  public String getFoundValue() {
    return foundValue;
  }

  public String getKey() {
    return attribute.key;
  }

  public String getValue() {
    return attribute.value;
  }
}
