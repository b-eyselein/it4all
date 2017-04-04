package model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import model.exercise.Exercise;

@Entity
public class ChoiceQuiz extends Exercise {
  
  public static final Finder<Integer, ChoiceQuiz> finder = new Finder<>(ChoiceQuiz.class);
  
  @ManyToMany(mappedBy = "quizzes", cascade = CascadeType.ALL)
  public List<ChoiceQuestion> questions;
  
  public ChoiceQuiz(int theId) {
    super(theId);
  }

  public int getPoints() {
    return questions.stream().mapToInt(ChoiceQuestion::getPoints).sum();
  }
  
}
