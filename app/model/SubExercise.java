package model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Constraints.Required;

import com.avaje.ebean.Model;

@Entity
public class SubExercise extends Model {
  
  @Id
  public int id;
  
  @Required
  public String title;
  
  @Required
  public String text;
  
  @ManyToOne
  public Exercise exercise;
  
  public String defaultSolution;
  
  @OneToMany(mappedBy = "exercise")
  public List<Task> tasks;
  
  public int getPoints() {
    return tasks.stream().mapToInt(task -> task.pts).sum();
  }
  
}
