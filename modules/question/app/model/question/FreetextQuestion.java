package model.question;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import model.FreetextAnswer;

@Entity
public class FreetextQuestion extends Question {

  // TODO: Exercise extends Model...
  public static final com.avaje.ebean.Model.Finder<Integer, FreetextQuestion> finder = new com.avaje.ebean.Model.Finder<>(
      FreetextQuestion.class);

  @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
  List<FreetextAnswer> givenAnswers;

  public FreetextQuestion(int theId) {
    super(theId);
  }

  @Override
  public boolean isFreetext() {
    return true;
  }

  @Override
  public void saveInDb() {
    save();
  }

  @Override
  public void saveInDB() {
    save();
  }

  @Override
  public String toString() {
    return "Autor: " + author + ", Text: " + text + ", Titel: " + title;
  }

  @Override
  public boolean userHasAnswered(String username) {
    return givenAnswers.stream().anyMatch(ans -> ans.key.username.equals(username));
  }

}
