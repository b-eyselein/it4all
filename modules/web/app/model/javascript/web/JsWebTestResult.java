package model.javascript.web;

import java.util.List;

import model.exercise.EvaluationResult;
import model.exercise.Success;

public class JsWebTestResult extends EvaluationResult {
  
  private JsWebTest test;
  private List<ConditionResult> postconditionResults;
  private List<ConditionResult> preconditionResults;
  
  public JsWebTestResult(JsWebTest theTest, Success theSuccess, List<ConditionResult> thePreconditionResults,
      List<ConditionResult> thePostconditionResults) {
    super(theSuccess);
    test = theTest;
    preconditionResults = thePreconditionResults;
    postconditionResults = thePostconditionResults;
  }
  
  @Override
  public String getAsHtml() {
    // FIXME: implement getAsHtml for JsWebTestReault
    String toAdd = "<div class=\"panel panel-" + getBSClass() + "\">\n";
    toAdd += "  <div class=\"panel-heading\">\n";
    toAdd += "    <h4 class=\"panel-title\"><a data-toggle=\"collapse\" href=\"#col" + test.id + "\">Test " + (test.id)
        + "</a></h4>\n";
    toAdd += "  </div>\n";
    
    toAdd += "  <div id=\"col" + test.id + "\" class=\"panel-collapse collapse"
        + (success == Success.COMPLETE ? "" : " in") + "\">\n";
    toAdd += "    <div class=\"panel-body\">\n";
    
    for(ConditionResult result: preconditionResults)
      toAdd += result.getAsHtml();
    
    for(ConditionResult result: postconditionResults)
      toAdd += result.getAsHtml();
    
    toAdd += "    </div>\n";
    toAdd += "  </div>\n";
    toAdd += "</div>";
    return toAdd;
  }
  
  public void postCondResult(List<ConditionResult> thePostconditionResults) {
    postconditionResults = thePostconditionResults;
  }
  
  public void preCondResult(List<ConditionResult> thePreconditionResults) {
    preconditionResults = thePreconditionResults;
  }
  
}
