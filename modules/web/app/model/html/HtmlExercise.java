package model.html;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

//import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import model.exercise.Exercise;
import model.html.task.CSSTask;
import model.html.task.Task;

@Entity
@DiscriminatorValue(value = "html")
public class HtmlExercise extends Exercise {
  
  public static final Finder<Integer, HtmlExercise> finder = new Finder<Integer, HtmlExercise>(HtmlExercise.class);

  @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
  @JsonManagedReference
  public List<Task> tasks;

  @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
  @JsonManagedReference
  public List<CSSTask> cssTasks;

  @Override
  public int getMaxPoints() {
    return 2 * tasks.size();
  }

}
