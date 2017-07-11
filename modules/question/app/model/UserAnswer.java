package model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import io.ebean.Finder;
import io.ebean.Model;
import model.question.Question;

@Entity
public class UserAnswer extends Model {

  public static final Finder<UserAnswerKey, UserAnswer> finder = new Finder<>(UserAnswer.class);

  @EmbeddedId
  public UserAnswerKey key;

  @ManyToOne
  @JoinColumn(name = "username", insertable = false, updatable = false)
  public QuestionUser user;

  @ManyToOne
  @JoinColumn(name = "question_id", insertable = false, updatable = false)
  public Question question;

  @Column(columnDefinition = "text")
  public String text;

  public UserAnswer(UserAnswerKey theKey) {
    key = theKey;
  }

}
