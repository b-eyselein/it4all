package model.javascript;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.avaje.ebean.Model;

import play.data.validation.Constraints.Required;

@Entity
public class JsTest extends Model {
  
  @Id
  public int id;
  
  @Required
  public String awaitedResult;
  
  @OneToMany(mappedBy = "test")
  public List<JsTestvalue> values;
  
  @ManyToOne
  public JsExercise exercise;
  
}
