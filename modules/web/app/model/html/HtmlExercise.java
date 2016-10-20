package model.html;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import model.exercise.Exercise;
import model.html.task.CssTask;
import model.html.task.HtmlTask;

@Entity
@DiscriminatorValue(value = "html")
public class HtmlExercise extends Exercise {
  
  public static final Finder<Integer, HtmlExercise> finder = new Finder<>(HtmlExercise.class);

  @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
  @JsonManagedReference
  public List<HtmlTask> tasks;

  @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
  @JsonManagedReference
  public List<CssTask> cssTasks;

  @Override
  public int getMaxPoints() {
    return 2 * tasks.size();
  }

}
