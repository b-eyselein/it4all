package model;

public class ChoiceAnswer {
  
  public enum Correctness {
    CORRECT, OPTIONAL, WRONG;
  }
  
  private int id;
  private Correctness correctness;
  private String text;
  
  public ChoiceAnswer(int theId, Correctness theCorrectness, String theText) {
    id = theId;
    correctness = theCorrectness;
    text = theText;
  }
  
  public Correctness getCorrectness() {
    return correctness;
  }

  public int getId() {
    return id;
  }

  public char getIdAsChar() {
    return (char) ('a' + id);
  }
  
  public String getText() {
    return text;
  }
  
  public boolean isCorrect() {
    return correctness != Correctness.WRONG;
  }
  
}
