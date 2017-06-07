package model.question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.ebean.Finder;

@Entity
public class GivenAnswerQuestion extends Question {

  public static final int MIN_ANSWERS = 2;
  public static final int STD_ANSWERS = 4;
  public static final int MAX_ANSWERS = 8;

  public static final Finder<Integer, GivenAnswerQuestion> finder = new Finder<>(GivenAnswerQuestion.class);

  @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
  public List<Answer> answers;

  public GivenAnswerQuestion(int theId) {
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

  @Override
  public boolean isFreetext() {
    return false;
  }

  @Override
  public void saveInDb() {
    save();
    answers.forEach(Answer::save);
  }

  @Override
  public boolean userHasAnswered(String username) {
    // TODO Auto-generated method stub
    return false;
  }

}
