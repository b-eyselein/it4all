package model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import io.ebean.Finder;
import io.ebean.Model;

@Entity
public class QuestionUser extends Model {

  public static final Finder<String, QuestionUser> finder = new Finder<>(QuestionUser.class);

  @Id
  public String name;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  public List<QuestionRating> ratings;
  
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  public List<UserAnswer> answers;

  public QuestionUser(String theName) {
    name = theName;
  }

}
