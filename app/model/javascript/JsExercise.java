package model.javascript;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import play.data.validation.Constraints.Required;

import com.avaje.ebean.Model;

@Entity
public class JsExercise extends Model {
  
  public static Finder<Integer, JsExercise> finder = new Finder<Integer, JsExercise>(JsExercise.class);
  
  @Id
  public int id;
  
  @Required
  public String text;
  
  @Required
  public String defaultSolution;
  
  @Required
  public String functionName;
  
  @OneToMany
  public List<JsTest> functionTests;
  
}
