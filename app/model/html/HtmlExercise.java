package model.html;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import model.Exercise;
import model.Task;

import com.avaje.ebean.Model;

@Entity
public class HtmlExercise extends Model implements Exercise {
  
  @Id
  public int id;
  
  public String exerciseText;
  public String defaultSolution;
  
  @OneToMany
  public List<Task> tasks;
  
  public static Finder<Integer, HtmlExercise> exerciseFinder = new Finder<Integer, HtmlExercise>(HtmlExercise.class);
  
  public List<Task> getAllTasks() {
    Finder<Integer, Task> taskFinder = new Finder<Integer, Task>(Task.class);
    return taskFinder.all();
  }
  
  
  @Override
  public List<String> getExerciseTextInLines() {
    return Arrays.asList(exerciseText.split("\n"));
  }
  
  public List<String> getDefaultInLines() {
    return Arrays.asList(defaultSolution.split("\\\\n"));
  }
  
}
