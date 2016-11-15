package model.javascript;

import java.util.List;

import model.result.CompleteResult;
import model.result.EvaluationResult;

public class JsWebCorrectionResult extends CompleteResult {
  
  public JsWebCorrectionResult(String theLearnerSolution, List<EvaluationResult> theResults) {
    super(theLearnerSolution, theResults);
  }

}
