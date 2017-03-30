package model;

import java.util.List;

public class ChoiceQuiz {
  
  private List<ChoiceQuestion> questions;
  
  public ChoiceQuiz(List<ChoiceQuestion> theQuestions) {
    questions = theQuestions;
  }
  
  public List<ChoiceQuestion> getQuestions() {
    return questions;
  }
  
}
