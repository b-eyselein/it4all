package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.avaje.ebean.Model;

@Entity
public class ChoiceQuestion extends Model {

  public static final Finder<ChoiceQuestionKey, ChoiceQuestion> finder = new Finder<>(ChoiceQuestion.class);

  @EmbeddedId
  public ChoiceQuestionKey key;

  @ManyToOne
  @JoinColumn(name = "quiz_id", insertable = false, updatable = false)
  public ChoiceQuiz quiz;

  @Column(columnDefinition = "text")
  public String text;

  @Enumerated(EnumType.STRING)
  public QuestionType questionType;

  @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
  public List<ChoiceAnswer> answers;

  public ChoiceQuestion(ChoiceQuestionKey theKey) {
    key = theKey;

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

  public List<ChoiceAnswer> getShuffeledAnswers() {
    List<ChoiceAnswer> shuffeledAnswers = new ArrayList<>(answers);
    Collections.shuffle(shuffeledAnswers);
    return shuffeledAnswers;
  }

}
