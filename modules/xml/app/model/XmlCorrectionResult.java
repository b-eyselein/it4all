package model;

import java.util.List;

import model.result.CompleteResult;
import model.result.EvaluationResult;

public class XmlCorrectionResult extends CompleteResult {

  public XmlCorrectionResult(String theLearnerSolution, List<EvaluationResult> theResults) {
    super(theLearnerSolution, theResults);
  }

}
