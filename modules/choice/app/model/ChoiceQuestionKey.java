package model;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class ChoiceQuestionKey implements Serializable {

  private static final long serialVersionUID = -3435843516843514L;

  public int questionId; // NOSONAR

  public int quizId; // NOSONAR

  public ChoiceQuestionKey(int theQuizId, int theQuestionId) {
    quizId = theQuizId;
    questionId = theQuestionId;
  }

  @Override
  public boolean equals(Object obj) {
    if(!(obj instanceof ChoiceQuestionKey))
      return false;
    ChoiceQuestionKey other = (ChoiceQuestionKey) obj;
    return questionId == other.questionId && quizId == other.quizId;
  }

  @Override
  public int hashCode() {
    return 1000 * quizId + questionId;
  }

}
