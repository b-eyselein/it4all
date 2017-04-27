package model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import model.exercise.Exercise;
import model.task.CssTask;
import model.task.HtmlTask;
import model.task.JsWebTask;
import model.task.Task;

@Entity
public class WebExercise extends Exercise {

  public static final Finder<Integer, WebExercise> finder = new Finder<>(WebExercise.class);

  @Column(columnDefinition = "text")
  public String htmlText;

  @Column(columnDefinition = "text")
  public String cssText;

  @Column(columnDefinition = "text")
  public String jsText;

  @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
  @JsonManagedReference
  public List<HtmlTask> htmlTasks;

  @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
  @JsonManagedReference
  public List<CssTask> cssTasks;

  @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
  @JsonManagedReference
  public List<JsWebTask> jsTasks;

  @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
  @JsonIgnore
  public List<WebSolution> solutions;

  public WebExercise(int theId) {
    super(theId);
  }

  public List<String> getCssText() {
    return splitter.splitToList(cssText);
  }

  public List<String> getHtmlText() {
    return splitter.splitToList(htmlText);
  }

  public List<String> getJsText() {
    return splitter.splitToList(jsText);
  }

  public List<Task> getTasks(String exType) {
    switch(exType) {
    case "html":
      return new LinkedList<>(htmlTasks);
    case "css":
      return new LinkedList<>(cssTasks);
    case "js":
      return new LinkedList<>(jsTasks);
    default:
      return Collections.emptyList();
    }
  }

}
