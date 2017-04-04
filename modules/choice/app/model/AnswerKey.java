package model;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class AnswerKey implements Serializable {

  private static final long serialVersionUID = -654684351366843L;

  public int questionId; // NOSONAR
  public int id; // NOSONAR

  public AnswerKey(int theQuestionId, int theId) {
    questionId = theQuestionId;
    id = theId;
  }

  @Override
  public boolean equals(Object obj) {
    if(!(obj instanceof AnswerKey))
      return false;

    AnswerKey other = (AnswerKey) obj;
    return questionId == other.questionId && id == other.id;
  }

  @Override
  public int hashCode() {
    return 1_000 * questionId + id;
  }

}
