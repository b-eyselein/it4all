package model.user;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.Model;

@Entity
@Table(name = "users")
public class User extends Model {
  
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
  
  @Id
  public String name;
  
  @Enumerated(EnumType.STRING)
  public Role role = Role.USER;
  
  @Enumerated(EnumType.STRING)
  public SHOW_HIDE_AGGREGATE todo = SHOW_HIDE_AGGREGATE.SHOW;
  
  public void setTodo(SHOW_HIDE_AGGREGATE newTodo) {
    todo = newTodo;
  }
  
}
