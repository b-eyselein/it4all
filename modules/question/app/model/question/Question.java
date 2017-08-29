package model.question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.ebean.Finder;
import model.UserAnswer;
import model.exercise.Exercise;
import model.quiz.Quiz;

@Entity
public class Question extends Exercise {

  public enum QType {
    CHOICE, FILLOUT, FREETEXT;
  }

  public static final int MIN_ANSWERS = 2;
  public static final int STD_ANSWERS = 4;
  public static final int MAX_ANSWERS = 8;

  public static final Finder<Integer, Question> finder = new Finder<>(Question.class);

  private int maxPoints;

  @JsonProperty("exerciseType")
  private QType questionType;

  @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
  private List<Answer> answers;

  @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
  @JsonIgnore
  public List<UserAnswer> givenAnswers;

  @ManyToMany
  @JsonIgnore
  public List<Quiz> quizzes;

  public Question(int id) {
    super(id);
  }

  public List<Answer> getAnswers() {
    return answers;
  }

  @JsonIgnore
  public List<Answer> getAnswersForTemplate() {
    List<Answer> shuffeledAnswers = new ArrayList<>(answers);
    Collections.shuffle(shuffeledAnswers);
    return shuffeledAnswers;
  }

  @JsonIgnore
  public List<Answer> getCorrectAnswers() {
    return answers.parallelStream().filter(Answer::isCorrect).collect(Collectors.toList());
  }

  public int getMaxPoints() {
    return maxPoints;
  }

  public QType getQuestionType() {
    return questionType;
  }

  @JsonIgnore
  public boolean isFreetext() {
    return questionType == QType.FREETEXT;
  }

  public void saveInDb() {
    save();
    answers.forEach(Answer::save);
  }

  public void setAnswers(List<Answer> theAnswers) {
    answers = theAnswers;
  }

  public void setMaxPoints(int theMaxPoints) {
    maxPoints = theMaxPoints;
  }

  public void setQuestionType(QType theQuestionType) {
    questionType = theQuestionType;
  }

  public boolean userHasAnswered(String username) {
    return false;
  }

}
