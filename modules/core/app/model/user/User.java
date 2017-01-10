package model.user;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.avaje.ebean.Model;

import model.exercise.Grading;
import play.mvc.PathBindable;

@Entity
@Table(name = "users")
public class User extends Model implements PathBindable<User> {

  public enum SHOW_HIDE_AGGREGATE {
    SHOW("Einblenden"), AGGREGATE("Zusammenfassen"), HIDE("Ausblenden");
    
    private String german;
    
    private SHOW_HIDE_AGGREGATE(String theGerman) {
      german = theGerman;
    }
    
    public String getGerman() {
      return german;
    }
  }

  public static final Finder<String, User> finder = new Finder<>(User.class);

  @OneToMany(mappedBy = "user")
  public List<Grading> gradings;
  
  @Id
  public String name;

  @Enumerated(EnumType.STRING)
  public Role role = Role.USER;
  
  @Enumerated(EnumType.STRING)
  public SHOW_HIDE_AGGREGATE todo = SHOW_HIDE_AGGREGATE.SHOW;
  
  @OneToMany(mappedBy = "user")
  public List<ExerciseResult<?>> results;
  
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

  public void setTodo(SHOW_HIDE_AGGREGATE newTodo) {
    todo = newTodo;
  }

  @Override
  public String unbind(String key) {
    return name;
  }

}
