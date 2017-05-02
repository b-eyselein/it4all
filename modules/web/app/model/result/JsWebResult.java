package model.result;

import java.util.List;

import model.exercise.Success;
import model.task.JsWebTask;

public class JsWebResult extends WebResult<JsWebResult> {
  
  private List<ConditionResult> postconditionResults;
  private List<ConditionResult> preconditionResults;
  private boolean actionPerformed;
  
  JsWebResult(JsWebTask theTask, List<ConditionResult> thePreResults, boolean theActionPerf,
      List<ConditionResult> thePostResults, List<String> theMessages) {
    super(theTask, analyze(thePostResults, theActionPerf, thePostResults), theMessages);
    
    preconditionResults = thePreResults;
    actionPerformed = theActionPerf;
    postconditionResults = thePostResults;
  }
  
  public static Success analyze(List<ConditionResult> preconds, boolean actionPerf, List<ConditionResult> postconds) {
    if(EvaluationResult.allResultsSuccessful(preconds) && actionPerf
        && EvaluationResult.allResultsSuccessful(postconds))
      return Success.COMPLETE;
    return Success.NONE;
  }
  
  public List<ConditionResult> getPostconditionResults() {
    return postconditionResults;
  }
  
  public List<ConditionResult> getPreconditionResults() {
    return preconditionResults;
  }
  
  public void postCondResult(List<ConditionResult> thePostconditionResults) {
    postconditionResults = thePostconditionResults;
  }
  
  public void preCondResult(List<ConditionResult> thePreconditionResults) {
    preconditionResults = thePreconditionResults;
  }
  
  public boolean wasActionPerformed() {
    return actionPerformed;
  }
  
}
