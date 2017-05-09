package model.question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import model.exercise.Exercise;

@Entity
public class Question extends Exercise {

  public static final int MIN_ANSWERS = 2;
  public static final int STD_ANSWERS = 4;
  public static final int MAX_ANSWERS = 8;

  // TODO: Exercise extends Model...
  public static final com.avaje.ebean.Model.Finder<Integer, Question> finder = new com.avaje.ebean.Model.Finder<>(
      Question.class);

  public String author;

  public int maxPoints;

  @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
  public List<Answer> answers;

  @Enumerated(EnumType.STRING)
  public QuestionType questionType;

  public Question(int theId) {
    super(theId);
  }

  public Answer getAnswer(int id) {
    for(Answer answer: answers)
      if(answer.key.id == id)
        return answer;
    return null;
  }

  @JsonIgnore
  public List<Answer> getAnswersForTemplate() {
    List<Answer> shuffeledAnswers = new ArrayList<>(answers);
    Collections.shuffle(shuffeledAnswers);
    return shuffeledAnswers;
  }

  @JsonIgnore
  public List<Answer> getCorrectAnswers() {
    return answers.stream().filter(Answer::isCorrect).collect(Collectors.toList());
  }

}