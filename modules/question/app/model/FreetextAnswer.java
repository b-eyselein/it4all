package model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import io.ebean.Finder;
import io.ebean.Model;
import model.question.FreetextQuestion;

@Entity
public class FreetextAnswer extends Model {

  public static final Finder<FreetextAnswerKey, FreetextAnswer> finder = new Finder<>(FreetextAnswer.class);

  @EmbeddedId
  public FreetextAnswerKey key;

  @ManyToOne
  @JoinColumn(name = "username", insertable = false, updatable = false)
  public QuestionUser user;

  @ManyToOne
  @JoinColumn(name = "question_id", insertable = false, updatable = false)
  public FreetextQuestion question;

  @Column(columnDefinition = "text")
  public String answer;

  public FreetextAnswer(FreetextAnswerKey theKey) {
    key = theKey;
  }

}
