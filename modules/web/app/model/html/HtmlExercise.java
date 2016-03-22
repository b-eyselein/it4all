package model.html;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import model.html.result.ElementResult;
import model.html.task.Task;
import play.data.validation.Constraints.Required;

import com.avaje.ebean.Model;

@Entity
@Table(name = "htmlexercise")
public class HtmlExercise extends Model {

  public static Finder<Integer, HtmlExercise> finder = new Finder<Integer, HtmlExercise>(HtmlExercise.class);

  @Id
  public int id;

  @Required
  public String title;

  @Required
  public String text;

  @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
  public List<Task> tasks;
  
  public List<ElementResult<? extends Task>> getElementResults() {
    // FIXME: implement!
    return tasks.parallelStream().map(task -> task.getElementResult()).collect(Collectors.toList());
  }
}
