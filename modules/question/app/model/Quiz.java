package model;

import javax.persistence.Entity;

import io.ebean.Finder;
import model.exercise.Exercise;

@Entity
public class Quiz extends Exercise {

  public static final Finder<Integer, Quiz> finder = new Finder<>(Quiz.class);

  public String theme;

  // @JsonIgnore
  // @ManyToMany(mappedBy = "quizzes", cascade = CascadeType.ALL)
  // public List<Question> questions;

  public Quiz(int theId) {
    super(theId);
  }

  // @JsonIgnore
  // public int getMaxPoints() {
  // return questions.stream().mapToInt(q -> q.maxPoints).sum();
  // }

}
