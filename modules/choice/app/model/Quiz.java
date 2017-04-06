package model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import model.exercise.Exercise;

@Entity
public class Quiz extends Exercise {
  
  // TODO: Exercise extends Model...
  public static final com.avaje.ebean.Model.Finder<Integer, Quiz> finder = new com.avaje.ebean.Model.Finder<>(
      Quiz.class);
  
  @ManyToMany(mappedBy = "quizzes", cascade = CascadeType.ALL)
  public Set<Question> questions;
  
  public Quiz(int theId) {
    super(theId);
  }
  
  public int getPoints() {
    return questions.stream().mapToInt(q -> q.maxPoints).sum();
  }
  
}
