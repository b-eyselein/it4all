package model;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.MappedSuperclass;

import model.exercise.Exercise;

@MappedSuperclass
public abstract class ChoiceQuestion extends Exercise {

  public List<ChoiceAnswer> answers; // NOSONAR

  public ChoiceQuestion(int theId) {
    super(theId);
  }

  public List<ChoiceAnswer> getAnswers() {
    return answers;
  }

  public List<ChoiceAnswer> getCorrectAnswers() {
    return answers.stream().filter(ChoiceAnswer::isCorrect).collect(Collectors.toList());
  }

  public abstract boolean isSingleChoice();

}
