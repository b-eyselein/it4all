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
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Answer extends Model {

  public static final Finder<AnswerKey, Answer> finder = new Finder<>(Answer.class);

  @EmbeddedId
  public AnswerKey key;

  @Column(columnDefinition = "text")
  public String text;
  
  @Enumerated(EnumType.STRING)
  public Correctness correctness;

  @JsonBackReference
  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "question_id", referencedColumnName = "id", insertable = false, updatable = false)
  public Question question;

  public Answer(AnswerKey theKey) {
    key = theKey;
  }

  @JsonIgnore
  public char getIdAsChar() {
    return (char) ('a' + key.id - 1);
  }
  
  @JsonIgnore
  public boolean isCorrect() {
    return correctness != Correctness.WRONG;
  }

  @Override
  public String toString() {
    return text;
  }

}
