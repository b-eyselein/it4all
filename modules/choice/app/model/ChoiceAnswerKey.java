package model;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class ChoiceAnswerKey implements Serializable {
  
  private static final long serialVersionUID = -654684351366843L;
  
  public int id; // NOSONAR

  public int choiceQuestionId; // NOSONAR
  
  public ChoiceAnswerKey(int theId, int theQuestionId) {
    id = theId;
    choiceQuestionId = theQuestionId;
  }
  
  @Override
  public boolean equals(Object obj) {
    if(!(obj instanceof ChoiceAnswerKey))
      return false;

    ChoiceAnswerKey other = (ChoiceAnswerKey) obj;
    return id == other.id && choiceQuestionId == other.choiceQuestionId;
  }
  
  @Override
  public int hashCode() {
    return 1000 * choiceQuestionId + id;
  }

}
