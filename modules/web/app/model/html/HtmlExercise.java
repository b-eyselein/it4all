package model.html;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import model.html.task.Task;

@Entity
@Table(name = "htmlexercise")
public class HtmlExercise extends Model {
  
  public static Finder<Integer, HtmlExercise> finder = new Finder<Integer, HtmlExercise>(HtmlExercise.class);

  @Id
  public int id;

  public String title;

  @Column(name = "exerciseText", length = 1000)
  public String exerciseText;

  @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
  @JsonManagedReference
  public List<Task> tasks;

}
