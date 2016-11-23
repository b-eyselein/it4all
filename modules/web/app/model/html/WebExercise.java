package model.html;

import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import model.exercise.Exercise;
import model.html.task.CssTask;
import model.html.task.HtmlTask;
import model.html.task.Task;

@Entity
public class WebExercise extends Model implements Exercise {

  public static final Finder<Integer, WebExercise> finder = new Finder<>(WebExercise.class);

  @Id
  public int id;

  @Column(columnDefinition = "text")
  public String text;

  public String title; // NOSONAR

  @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
  @JsonManagedReference
  public List<HtmlTask> htmlTasks;

  @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
  @JsonManagedReference
  public List<CssTask> cssTasks;

  public WebExercise(int exerciseId) {
    id = exerciseId;
  }
  
  @Override
  public int getId() {
    return id;
  }

  @Override
  public int getMaxPoints() {
    return 2 * htmlTasks.size();
  }

  public List<? extends Task> getTasks(String exType) {
    switch(exType) {
    case "html":
      return htmlTasks;
    case "css":
      return cssTasks;
    default:
      return Collections.emptyList();
    }
  }

  @Override
  public String getText() {
    return text;
  }

  @Override
  public String getTitle() {
    return title;
  }

}
