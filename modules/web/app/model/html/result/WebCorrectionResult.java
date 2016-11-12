package model.html.result;

import java.util.List;

import model.result.CompleteResult;
import model.result.EvaluationResult;

public class WebCorrectionResult extends CompleteResult {
  
  public WebCorrectionResult(String theLearnerSolution, List<EvaluationResult> theResults) {
    super(theLearnerSolution, theResults);
  }
  
}
