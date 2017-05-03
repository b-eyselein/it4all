package model;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class QuestionRatingKey implements Serializable {

  private static final long serialVersionUID = -6543546519843584651L;

  public int questionId; // NOSONAR

  public String username; // NOSONAR

  public QuestionRatingKey(int theQuestionId, String theUsername) {
    questionId = theQuestionId;
    username = theUsername;
  }

  @Override
  public boolean equals(Object obj) {
    if(!(obj instanceof QuestionRatingKey))
      return false;

    QuestionRatingKey other = (QuestionRatingKey) obj;

    return questionId == other.questionId && username.equals(other.username);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + questionId;
    result = prime * result + ((username == null) ? 0 : username.hashCode());
    return result;
  }

}
