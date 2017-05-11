package model.question;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.MappedSuperclass;

import model.exercise.Exercise;

@MappedSuperclass
public abstract class Question extends Exercise {
  
  public int maxPoints;
  
  public Question(int theId) {
    super(theId);
  }
  
  public static List<Question> all() {
    return Stream.concat(FreetextQuestion.finder.all().stream(), GivenAnswerQuestion.finder.all().stream())
        .collect(Collectors.toList());
  }
  
  public abstract boolean isFreetext();
  
  public abstract void saveInDb();
  
  public abstract boolean userHasAnswered(String username);

}
