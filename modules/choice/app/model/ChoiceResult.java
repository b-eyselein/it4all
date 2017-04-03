package model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import model.exercise.FeedbackLevel;
import model.exercise.Success;
import model.result.EvaluationResult;

public class ChoiceResult extends EvaluationResult {

  private ChoiceQuestion question;

  private List<ChoiceAnswer> correct = new LinkedList<>();
  private List<ChoiceAnswer> missing;
  private List<ChoiceAnswer> wrong;

  public ChoiceResult(List<ChoiceAnswer> theSelAns, ChoiceQuestion theQuestion) {
    super(FeedbackLevel.MINIMAL_FEEDBACK, Success.NONE);
    question = theQuestion;
    success = analyze(new LinkedList<>(theSelAns), new LinkedList<>(theQuestion.getCorrectAnswers()));
  }

  public List<ChoiceAnswer> getCorrect() {
    return correct;
  }

  public List<ChoiceAnswer> getMissing() {
    return missing;
  }

  public ChoiceQuestion getQuestion() {
    return question;
  }

  public List<ChoiceAnswer> getWrong() {
    return wrong;
  }

  private Success analyze(List<ChoiceAnswer> selAns, List<ChoiceAnswer> corrAns) {
    for(Iterator<ChoiceAnswer> sel = selAns.iterator(); sel.hasNext();) {
      ChoiceAnswer selectedAns = sel.next();
      for(Iterator<ChoiceAnswer> corr = corrAns.iterator(); corr.hasNext();) {
        ChoiceAnswer correctAns = corr.next();
        if(selectedAns.getId() == correctAns.getId()) {
          correct.add(selectedAns);
          sel.remove();
          corr.remove();
        }
      }
    }
    missing = corrAns;
    wrong = selAns;
    return (missing.isEmpty() && wrong.isEmpty()) ? Success.COMPLETE : Success.NONE;
  }

}
