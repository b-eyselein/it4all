package model;

import java.util.Collections;
import java.util.List;

import model.exercise.FeedbackLevel;
import model.exercise.Success;
import model.result.EvaluationResult;
import model.tree.Assignment;

public class BooleanQuestionResult extends EvaluationResult {
  
  private CreationQuestion question;
  private String learnerSolution;
  
  public BooleanQuestionResult(Success theSuccess, String theLearnerSolution, CreationQuestion theQuestion,
      String... theMessages) {
    super(FeedbackLevel.MINIMAL_FEEDBACK, theSuccess, theMessages);
    learnerSolution = theLearnerSolution;
    question = theQuestion;
    
  }
  
  @Override
  public String getAsHtml() {
    return String.join(", ", messages);
  }
  
  public String getLearnerSolution() {
    return learnerSolution;
  }

  public CreationQuestion getQuestion() {
    return question;
  }
  
  public List<Assignment> getSolutions() {
    if(question == null)
      // FIXME: Question !== null!
      return Collections.emptyList();
    return question.getSolutions();
  }
  
}
