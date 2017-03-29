package model.choicequestions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MCQuestion extends ChoiceQuestion {

  private int[] correctAnswers;
  
  public MCQuestion(int theId, String theQuestion, List<String> theAnswers, int[] theCorrectAnswers) {
    super(theId, theQuestion, theAnswers);
  }
  
  @Override
  public List<String> getCorrectAnswers() {
    return Arrays.stream(correctAnswers).mapToObj(answers::get).collect(Collectors.toList());
  }

}
