package model;

import javax.persistence.CascadeType;
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

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "question_id", referencedColumnName = "id", insertable = false, updatable = false)
  public ChoiceQuestion question;

  @Enumerated(EnumType.STRING)
  public Correctness correctness;

  @Column(columnDefinition = "text")
  public String text;

  public ChoiceAnswer(ChoiceAnswerKey theKey) {
    key = theKey;
  }

  public int getId() {
    return key.id;
  }

  public char getIdAsChar() {
    return (char) ('a' + key.id - 1);
  }

  public boolean isCorrect() {
    return correctness != Correctness.WRONG;
  }

  @Override
  public String toString() {
    return text;
  }

}
