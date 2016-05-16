package model.user;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
//import javax.persistence.OneToMany;
import javax.persistence.Table;

//import model.html.Grading;

import com.avaje.ebean.Model;

import model.exercise.Grading;

@Entity
@Table(name = "users")
public class User extends Model {
  
  public static Finder<String, User> finder = new Finder<String, User>(User.class);

  @OneToMany(mappedBy = "user")
  public List<Grading> gradings;

  @Id
  public String name;

}
