package model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import com.avaje.ebean.Model.Finder;

import model.exercise.Exercise;

@Entity
public class Quiz extends Exercise {
  
  public static final Finder<Integer, Quiz> finder = new Finder<>(Quiz.class);
  
  @ManyToMany(mappedBy = "quizzes", cascade = CascadeType.ALL)
  public Set<Question> questions;
  
  public Quiz(int theId) {
    super(theId);
  }
  
  public int getPoints() {
    return questions.stream().mapToInt(Question::getPoints).sum();
  }
  
}
