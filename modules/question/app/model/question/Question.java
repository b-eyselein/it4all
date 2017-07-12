package model.question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.ebean.Finder;
import model.UserAnswer;
import model.exercise.Exercise;

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

  public QType questionType;

  @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
  public List<Answer> answers;

  @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
  public List<UserAnswer> givenAnswers;

  public Question(int theId, String theTitle, String theAuthor, String theText, int theMaxPoints, QType theQuestionType,
      List<Answer> theAnswers) {
    super(theId, theText, theAuthor, theText);
    maxPoints = theMaxPoints;
    questionType = theQuestionType;
    answers = theAnswers;
    givenAnswers = new LinkedList<>();
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

  @JsonIgnore
  public boolean isFreetext() {
    return questionType == QType.FREETEXT;
  }

  public void saveInDb() {
    save();
    answers.forEach(Answer::save);
  }

  public Question updateValues(int theId, String theTitle, String theAuthor, String theText, int theMaxPoints,
      QType theQuestionType, List<Answer> theAnswers) {
    super.updateValues(theId, theTitle, theAuthor, theText);
    maxPoints = theMaxPoints;
    questionType = theQuestionType;
    answers = theAnswers;
    return this;
  }
  
  public boolean userHasAnswered(String username) {
    return false;
  }

}
