package model.multchoice;

import java.util.List;

public class MCQuestion {

  private int id;
  private String question;
  private List<String> answers;

  public MCQuestion(int theId, String theQuestion, List<String> theAnswers) {
    id = theId;
    question = theQuestion;
    answers = theAnswers;
  }

  public List<String> getAnswers() {
    return answers;
  }

  public int getId() {
    return id;
  }

  public String getQuestion() {
    return question;
  }

}
