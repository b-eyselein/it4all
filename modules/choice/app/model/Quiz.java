package model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import model.exercise.Exercise;

@Entity
public class Quiz extends Exercise {
  
  // TODO: Exercise extends Model...
  public static final com.avaje.ebean.Model.Finder<Integer, Quiz> finder = new com.avaje.ebean.Model.Finder<>(
      Quiz.class);
  
  public String theme; // NOSONAR
  
  @JsonIgnore
  @ManyToMany(mappedBy = "quizzes", cascade = CascadeType.ALL)
  public List<Question> questions;
  
  public Quiz(int theId) {
    super(theId);
  }
  
  @JsonIgnore
  public int getMaxPoints() {
    return questions.stream().mapToInt(q -> q.maxPoints).sum();
  }
  
}
