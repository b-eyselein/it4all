package model;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class QuestionRatingKey implements Serializable {
  
  private static final long serialVersionUID = -6543546519843584651L;
  
  public int questionId;
  
  public String username;
  
  public QuestionRatingKey(int theQuestionId, String theUsername) {
    questionId = theQuestionId;
    username = theUsername;
  }
  
  @Override
  public boolean equals(Object obj) {
    return obj instanceof QuestionRatingKey && hashCode() == obj.hashCode();
  }
  
  @Override
  public int hashCode() {
    return IntConsts.THOUSAND * username.hashCode() + questionId;
  }
  
}