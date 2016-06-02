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
import play.mvc.PathBindable;

@Entity
@Table(name = "users")
public class User extends Model implements PathBindable<User> {
  
  public static Finder<String, User> finder = new Finder<String, User>(User.class);
  
  @OneToMany(mappedBy = "user")
  public List<Grading> gradings;
  
  @Id
  public String name;
  
  @Override
  public User bind(String key, String name) {
    User user = finder.byId(name);
    if(user == null)
      throw new IllegalArgumentException("User with name " + name + " not found!");
    return user;
  }
  
  @Override
  public String javascriptUnbind() {
    return "function(k, v) {\n    return v.name;\n}";
  }
  
  @Override
  public String unbind(String key) {
    return name;
  }
  
}
