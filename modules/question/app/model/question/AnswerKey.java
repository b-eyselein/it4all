package model.question;

import java.io.Serializable;

import javax.persistence.Embeddable;

import model.IntConsts;

@Embeddable
public class AnswerKey implements Serializable {
  
  private static final long serialVersionUID = -654684351366843L;
  
  public int questionId;
  public int id;
  
  public AnswerKey(int theQuestionId, int theId) {
    questionId = theQuestionId;
    id = theId;
  }
  
  @Override
  public boolean equals(Object obj) {
    return obj instanceof AnswerKey && hashCode() == obj.hashCode();
  }
  
  @Override
  public int hashCode() {
    return IntConsts.THOUSAND * questionId + id;
  }
  
}
