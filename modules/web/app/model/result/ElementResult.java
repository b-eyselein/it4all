package model.result;

import java.util.List;

import model.exercise.Success;
import model.task.WebTask;

public class ElementResult extends WebResult {
  
  protected List<AttributeResult> attributeResults;
  
  protected TextContentResult textContentResult;
  
  ElementResult(WebTask theTask, Success theSuccess, List<AttributeResult> theAttributeResults,
      TextContentResult theTextContentResult, List<String> theMessages) {
    super(theTask, theSuccess, theMessages);
    
    attributeResults = theAttributeResults;
    textContentResult = theTextContentResult;
  }
  
  public List<AttributeResult> getAttributeResults() {
    return attributeResults;
  }
  
  public TextContentResult getTextContentResult() {
    return textContentResult;
  }
  
}
