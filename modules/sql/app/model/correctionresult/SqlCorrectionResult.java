package model.correctionresult;

import java.util.List;

import model.result.CompleteResult;
import model.result.EvaluationResult;

public class SqlCorrectionResult extends CompleteResult {
  
  public SqlCorrectionResult(String theLearnerSolution, List<EvaluationResult> theResults) {
    super(theLearnerSolution, theResults);
  }

}
