package model.html;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import model.html.task.Task;
import play.data.validation.Constraints.Required;

import com.avaje.ebean.Model;

@Entity
@Table(name = "XmlExercise")
public class XmlExercise extends Model {
  
  public static Finder<Integer, XmlExercise> finder = new Finder<Integer, XmlExercise>(XmlExercise.class);
  
  @Id
  public int id;
  
  @Required
  public String title;
  
  @Required
  @Column(name = "exerciseText", length = 1000)
  public String exerciseText;
  
  @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
  public List<Task> tasks;
  
}
