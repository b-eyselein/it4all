package model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import model.exercise.FeedbackLevel;
import model.exercise.Success;
import model.result.EvaluationResult;

public class ChoiceResult extends EvaluationResult {
  
  private List<Integer> correct = new LinkedList<>();
  private List<Integer> missing;
  private List<Integer> wrong;
  
  public ChoiceResult(List<Integer> theSelAns, ChoiceQuestion question) {
    super(FeedbackLevel.MINIMAL_FEEDBACK, Success.NONE);
    success = analyze(new LinkedList<>(theSelAns), new LinkedList<>(question.getCorrectAnswers()));
  }
  
  public List<Integer> getCorrect() {
    return correct;
  }
  
  public List<Integer> getMissing() {
    return missing;
  }
  
  public List<Integer> getWrong() {
    return wrong;
  }
  
  private Success analyze(List<Integer> selAns, List<ChoiceAnswer> corrAns) {
    for(Iterator<Integer> sel = selAns.iterator(); sel.hasNext();) {
      Integer selectedAns = sel.next();
      for(Iterator<ChoiceAnswer> corr = corrAns.iterator(); corr.hasNext();) {
        ChoiceAnswer correctAns = corr.next();
        if(selectedAns.intValue() == correctAns.getId()) {
          correct.add(selectedAns);
          sel.remove();
          corr.remove();
        }
      }
    }
    missing = corrAns.stream().map(ChoiceAnswer::getId).collect(Collectors.toList());
    wrong = selAns;
    return (missing.isEmpty() && wrong.isEmpty()) ? Success.COMPLETE : Success.NONE;
  }
  
}
