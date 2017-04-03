package model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.avaje.ebean.Model;

@Entity
@Table(name = "choice_answers")
public class ChoiceAnswer extends Model {

  public static final Finder<ChoiceAnswerKey, ChoiceAnswer> finder = new Finder<>(ChoiceAnswer.class);

  @EmbeddedId
  public ChoiceAnswerKey key;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumns({
      @JoinColumn(name = "question_id", referencedColumnName = "question_id", insertable = false, updatable = false),
      @JoinColumn(name = "quiz_id", referencedColumnName = "quiz_id", insertable = false, updatable = false)})
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
