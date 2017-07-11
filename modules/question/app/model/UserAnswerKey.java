package model;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class UserAnswerKey implements Serializable {
  private static final long serialVersionUID = -4383549891626793828L;
  
  public String username;
  
  public int questionId;
  
  public UserAnswerKey(String theUsername, int theQuestionId) {
    username = theUsername;
    questionId = theQuestionId;
  }
  
  @Override
  public boolean equals(Object obj) {
    return obj instanceof UserAnswerKey && hashCode() == obj.hashCode();
  }
  
  @Override
  public int hashCode() {
    return IntConsts.THOUSAND * username.hashCode() + questionId;
  }
  
}
