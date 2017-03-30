package model;

import javax.persistence.Entity;

@Entity
public class MCQuestion extends ChoiceQuestion {
  
  public static final Finder<Integer, MCQuestion> finder = new Finder<>(MCQuestion.class);

  public MCQuestion(int theId) {
    super(theId);
  }

  @Override
  public boolean isSingleChoice() {
    return false;
  }

}
