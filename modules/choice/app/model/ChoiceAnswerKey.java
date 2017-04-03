package model;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class ChoiceAnswerKey implements Serializable {

  private static final long serialVersionUID = -654684351366843L;

  public int quizId; // NOSONAR
  public int questionId; // NOSONAR
  public int id; // NOSONAR

  public ChoiceAnswerKey(int theQuizId, int theQuestionId, int theId) {
    quizId = theQuizId;
    questionId = theQuestionId;
    id = theId;
  }

  @Override
  public boolean equals(Object obj) {
    if(!(obj instanceof ChoiceAnswerKey))
      return false;

    ChoiceAnswerKey other = (ChoiceAnswerKey) obj;
    return quizId == other.quizId && questionId == other.questionId && id == other.id;
  }

  @Override
  public int hashCode() {
    return 1_000_000 * quizId + 1_000 * questionId + id;
  }

}
