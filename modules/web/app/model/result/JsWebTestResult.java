package model.result;

import java.util.List;

import model.exercise.Success;
import model.task.JsWebTask;

public class JsWebTestResult extends ElementResult {

  public List<ConditionResult> postconditionResults;
  public List<ConditionResult> preconditionResults;
  public boolean actionPerformed;

  public JsWebTestResult(JsWebTask theTask, List<ConditionResult> thePreResults, boolean theActionPerf,
      List<ConditionResult> thePostResults) {
    super(theTask, analyze(thePostResults, theActionPerf, thePostResults), /* attributeResults */null,
        null/* textContentResult */);
    preconditionResults = thePreResults;
    actionPerformed = theActionPerf;
    postconditionResults = thePostResults;
  }

  public static <T extends EvaluationResult> boolean allResultsSuccessful(List<T> results) {
    for(EvaluationResult res: results)
      if(res.getSuccess() != Success.COMPLETE)
        return false;
    return true;
  }

  public static Success analyze(List<ConditionResult> preconds, boolean actionPerf, List<ConditionResult> postconds) {
    if(allResultsSuccessful(preconds) && actionPerf && allResultsSuccessful(postconds))
      return Success.COMPLETE;
    return Success.NONE;
  }

  public void postCondResult(List<ConditionResult> thePostconditionResults) {
    postconditionResults = thePostconditionResults;
  }

  public void preCondResult(List<ConditionResult> thePreconditionResults) {
    preconditionResults = thePreconditionResults;
  }

}
