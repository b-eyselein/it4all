package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import model.exercise.Exercise;

@Entity
public class ChoiceQuestion extends Exercise {

  private static final int POINTS_DUMMY = 2;

  public static final Finder<Integer, ChoiceQuestion> finder = new Finder<>(ChoiceQuestion.class);

  @ManyToMany
  public List<ChoiceQuiz> quizzes;

  @Enumerated(EnumType.STRING)
  public QuestionType questionType;

  @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
  public List<ChoiceAnswer> answers;

  public String author; // NOSONAR

  public ChoiceQuestion(int theId) {
    super(theId);
    // if(questionType == QuestionType.SINGLE &&
    // theAnswers.stream().filter(ChoiceAnswer::isCorrect).count() > 1)
    // throw new IllegalArgumentException("Only one answer can be correct!");
  }

  public ChoiceAnswer getAnswer(int id) {
    for(ChoiceAnswer answer: answers)
      if(answer.getId() == id)
        return answer;
    return null;
  }

  public List<ChoiceAnswer> getCorrectAnswers() {
    return answers.stream().filter(ChoiceAnswer::isCorrect).collect(Collectors.toList());
  }

  public int getPoints() {
    // TODO!
    return POINTS_DUMMY;
  }

  public List<ChoiceAnswer> getShuffeledAnswers() {
    List<ChoiceAnswer> shuffeledAnswers = new ArrayList<>(answers);
    Collections.shuffle(shuffeledAnswers);
    return shuffeledAnswers;
  }

}
