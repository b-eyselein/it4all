package model.quiz;

import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.ebean.Finder;
import model.exercise.ExerciseCollection;
import model.question.Question;

@Entity
public class Quiz extends ExerciseCollection<Question> {
  
  public static final Finder<Integer, Quiz> finder = new Finder<>(Quiz.class);
  
  private String theme;
  
  @JsonIgnore
  @ManyToMany(mappedBy = "quizzes", cascade = CascadeType.ALL)
  public List<Question> questions;
  
  public Quiz(int theId, String theTitle, String theAuthor, String theText, String theTheme) {
    super(theId, theTitle, theAuthor, theText);
    theme = theTheme;
  }
  
  @Override
  public List<Question> getExercises() {
    return Collections.emptyList();
    // questions;
  }
  
  public String getTheme() {
    return theme;
  }
  
}
