package model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import model.exercise.Success;
import model.question.Answer;
import model.question.GivenAnswerQuestion;
import model.question.Question;
import model.result.EvaluationResult;

public class QuestionResult extends EvaluationResult {
  
  private Question question;
  
  private List<Answer> correct = new LinkedList<>();
  private List<Answer> missing;
  private List<Answer> wrong;
  
  public QuestionResult(List<Answer> theSelAns, GivenAnswerQuestion theQuestion) {
    super(Success.NONE);
    question = theQuestion;
    success = analyze(new LinkedList<>(theSelAns), new LinkedList<>(theQuestion.getCorrectAnswers()));
  }
  
  public List<Answer> getCorrect() {
    return correct;
  }
  
  public List<Answer> getMissing() {
    return missing;
  }
  
  public Question getQuestion() {
    return question;
  }
  
  public List<Answer> getWrong() {
    return wrong;
  }
  
  private Success analyze(List<Answer> selAns, List<Answer> corrAns) {
    for(Iterator<Answer> sel = selAns.iterator(); sel.hasNext();) {
      Answer selectedAns = sel.next();
      for(Iterator<Answer> corr = corrAns.iterator(); corr.hasNext();) {
        Answer correctAns = corr.next();
        if(selectedAns.key.id == correctAns.key.id) {
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
