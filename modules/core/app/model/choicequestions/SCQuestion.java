package model.choicequestions;

import java.util.Arrays;
import java.util.List;

public class SCQuestion extends ChoiceQuestion {
  
  private int correctAnswer;
  
  public SCQuestion(int theId, String theQuestion, List<String> theAnswers, int theCorrectAnswer) {
    super(theId, theQuestion, theAnswers);
    correctAnswer = theCorrectAnswer;
  }

  @Override
  public List<String> getCorrectAnswers() {
    return Arrays.asList(answers.get(correctAnswer));
  }

}
