package model.question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import io.ebean.Finder;
import model.StringConsts;
import model.UserAnswer;
import model.exercise.Exercise;
import model.exercisereading.ExerciseReader;
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

  public Question(int theId, String theTitle, String theAuthor, String theText, int theMaxPoints, QType theQuestionType,
      List<Answer> theAnswers) {
    super(theId, theTitle, theAuthor, theText);
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
  
  @Override
  public void updateValues(int theId, String theTitle, String theAuthor, String theText, JsonNode exerciseNode) {
    super.updateValues(theId, theTitle, theAuthor, theText);

    maxPoints = exerciseNode.get(StringConsts.MAX_POINTS).asInt();
    questionType = Question.QType.valueOf(exerciseNode.get(StringConsts.EXERCISE_TYPE).asText());
    answers = ExerciseReader.readArray(exerciseNode.get(StringConsts.ANSWER_NAME), QuestionReader::readAnswer);
  }

  public boolean userHasAnswered(String username) {
    return false;
  }

}
