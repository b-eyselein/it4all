package model.question;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Splitter;

import io.ebean.Finder;
import io.ebean.Model;

@Entity
public class Answer extends Model {

  private static final Splitter SPLITTER = Splitter.fixedLength(100).omitEmptyStrings();
  
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

  public String asText() {
    return text;
  }
  
  @JsonIgnore
  public char getIdAsChar() {
    return (char) ('a' + key.id - 1);
  }

  public List<String> getText() {
    return SPLITTER.splitToList(text);
  }

  @JsonIgnore
  public boolean isCorrect() {
    return correctness != Correctness.WRONG;
  }

  @Override
  public String toString() {
    return "(" + key.id + ")\t[" + correctness + "]\t" + text;
  }

}
