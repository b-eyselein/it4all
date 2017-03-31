package model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

@Entity
public class ChoiceAnswer extends Model {

  public static final Finder<ChoiceAnswerKey, ChoiceAnswer> finder = new Finder<>(ChoiceAnswer.class);
  
  @EmbeddedId
  public ChoiceAnswerKey key;

  @Enumerated(EnumType.STRING)
  public Correctness correctness;

  @Column(columnDefinition = "text")
  public String text;

  @ManyToOne
  @JoinColumn(name = "choice_question_id", insertable = false, updatable = false)
  public ChoiceQuestion question;

  public ChoiceAnswer(ChoiceAnswerKey theKey) {
    key = theKey;
  }

  public char getIdAsChar() {
    return (char) ('a' + key.id - 1);
  }

  public boolean isCorrect() {
    return correctness != Correctness.WRONG;
  }

}
