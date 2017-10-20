package model.question;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.ebean.Finder;
import model.UserAnswer;
import model.exercise.Exercise;
import model.quiz.Quiz;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Question extends Exercise {

  public enum QType {
    CHOICE, FILLOUT, FREETEXT;
  }

  public static final int MIN_ANSWERS = 2;
  public static final int STD_ANSWERS = 4;
  public static final int MAX_ANSWERS = 8;

  public static final Finder<Integer, Question> finder = new Finder<>(Question.class);

  public int maxPoints;

  @JsonProperty("exerciseType")
  public QType questionType;

  @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
  public List<Answer> answers;

  @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
  @JsonIgnore
  public List<UserAnswer> givenAnswers;

  @ManyToMany
  @JsonIgnore
  public List<Quiz> quizzes;

  public Question(int id) {
    id_$eq(id);
  }

  @JsonIgnore
  public List<Answer> answersForTemplate() {
    List<Answer> shuffeledAnswers = new ArrayList<>(answers);
    Collections.shuffle(shuffeledAnswers);
    return shuffeledAnswers;
  }

  @JsonIgnore
  public List<Answer> getCorrectAnswers() {
    return answers.parallelStream().filter(Answer::isCorrect).collect(Collectors.toList());
  }

  @JsonIgnore
  public boolean isFreetext() {
    return questionType == QType.FREETEXT;
  }

  public boolean userHasAnswered(String username) {
    return false;
  }

}
