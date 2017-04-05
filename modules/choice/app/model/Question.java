package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.avaje.ebean.Model.Finder;
import com.fasterxml.jackson.annotation.JsonIgnore;

import model.exercise.Exercise;

@Entity
public class Question extends Exercise {
  
  private static final int POINTS_DUMMY = 2;
  
  public static final Finder<Integer, Question> finder = new Finder<>(Question.class);
  
  @JsonIgnore
  @ManyToMany
  public List<Quiz> quizzes;
  
  @Enumerated(EnumType.STRING)
  public QuestionType questionType;
  
  @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
  public List<Answer> answers;
  
  @JsonIgnore
  @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
  public List<QuestionRating> ratings;
  
  public String author; // NOSONAR
  
  public Question(int theId) {
    super(theId);
    // if(questionType == QuestionType.SINGLE &&
    // theAnswers.stream().filter(ChoiceAnswer::isCorrect).count() > 1)
    // throw new IllegalArgumentException("Only one answer can be correct!");
  }
  
  public Answer getAnswer(int id) {
    for(Answer answer: answers)
      if(answer.getId() == id)
        return answer;
    return null;
  }
  
  public OptionalDouble getAverageRating() {
    return ratings.stream().mapToInt(rating -> rating.rating).average();
  }
  
  public List<Answer> getCorrectAnswers() {
    return answers.stream().filter(Answer::isCorrect).collect(Collectors.toList());
  }
  
  public int getPoints() {
    // TODO!
    return POINTS_DUMMY;
  }

  @JsonIgnore
  public List<String> getQuizNames() {
    return quizzes.stream().map(quiz -> quiz.title).collect(Collectors.toList());
  }
  
  @JsonIgnore
  public List<Answer> getShuffeledAnswers() {
    List<Answer> shuffeledAnswers = new ArrayList<>(answers);
    Collections.shuffle(shuffeledAnswers);
    return shuffeledAnswers;
  }
  
}
