package model;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;

import model.exercise.Exercise;

@Entity
public class ChoiceQuestion extends Exercise {
  
  public static final Finder<Integer, ChoiceQuestion> finder = new Finder<>(ChoiceQuestion.class);
  
  @Enumerated(EnumType.STRING)
  public QuestionType questionType;
  
  @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
  public List<ChoiceAnswer> answers;
  
  public ChoiceQuestion(int theId) {
    super(theId);
    
    // if(questionType == QuestionType.SINGLE &&
    // theAnswers.stream().filter(ChoiceAnswer::isCorrect).count() > 1)
    // throw new IllegalArgumentException("Only one answer can be correct!");
  }

  public List<ChoiceAnswer> getCorrectAnswers() {
    return answers.stream().filter(ChoiceAnswer::isCorrect).collect(Collectors.toList());
  }
  
}
