package model.choicequestions;

import java.util.List;

public abstract class ChoiceQuestion {

  protected int id;
  protected String question;
  protected List<String> answers;

  public ChoiceQuestion(int theId, String theQuestion, List<String> theAnswers) {
    id = theId;
    question = theQuestion;
    answers = theAnswers;
  }

  public List<String> getAnswers() {
    return answers;
  }

  public abstract List<String> getCorrectAnswers();

  public int getId() {
    return id;
  }

  public String getQuestion() {
    return question;
  }
  
}
