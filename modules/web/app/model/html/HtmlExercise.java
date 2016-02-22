package model.html;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import play.data.validation.Constraints.Required;

import com.avaje.ebean.Model;

@Entity
public class HtmlExercise extends Model {
  
  public static Finder<Integer, HtmlExercise> finder = new Finder<Integer, HtmlExercise>(HtmlExercise.class);
  
  @Id
  public int id;
  
  @Required
  public String title;
  
  @Required
  public String text;
  
  @OneToMany(mappedBy = "exercise")
  public List<Task> tasks;
  
  public List<ElementResult> getElementResults() {
    return tasks.parallelStream().map(task -> task.getElementResult()).collect(Collectors.toList());
  }
  
}
