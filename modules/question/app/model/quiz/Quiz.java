package model.quiz;

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
  private List<Question> questions;

  public Quiz(int id) {
    super(id);
  }

  @Override
  public List<Question> getExercises() {
    return questions;
  }

  public List<Question> getQuestions() {
    return questions;
  }

  public String getTheme() {
    return theme;
  }

  public void setQuestions(List<Question> theQuestions) {
    questions = theQuestions;
  }

  public void setTheme(String theTheme) {
    theme = theTheme;
  }

}
