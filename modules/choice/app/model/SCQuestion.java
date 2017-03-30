package model;

import javax.persistence.Entity;

@Entity
public class SCQuestion extends ChoiceQuestion {

  public static final Finder<Integer, SCQuestion> finder = new Finder<>(SCQuestion.class);

  public SCQuestion(int theId) {
    super(theId);
  }

  @Override
  public boolean isSingleChoice() {
    return true;
  }

}
