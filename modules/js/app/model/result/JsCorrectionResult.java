package model.result;

import java.util.List;

import model.result.CompleteResult;
import model.result.EvaluationResult;

public class JsCorrectionResult extends CompleteResult {
  
  public JsCorrectionResult(String theLearnerSolution, List<EvaluationResult> theResults) {
    super(theLearnerSolution, theResults);
  }
  
}
