package model;

import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

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

  public WebExercise(int theId) {
    super(theId);
  }
  
  public List<? extends Task> getTasks(String exType) {
    switch(exType) {
    case "html":
      return htmlTasks;
    case "css":
      return cssTasks;
    case "js":
      return jsTasks;
    default:
      return Collections.emptyList();
    }
  }

  @Override
  public String renderData() {
    StringBuilder builder = new StringBuilder();
    builder.append("<div class=\"col-md-6\">");
    builder.append("<div class=\"panel panel-default\">");
    builder.append("<div class=\"panel-heading\">Aufgabe " + id + ": " + title + "</div>");
    builder.append("<div class=\"panel-body\">");
    builder.append("<p>Text: " + text + "</p>");

    builder.append("<h2>HTML-Tasks</h2>");
    for(Task task: htmlTasks)
      builder.append("<p>" + task.getId() + ": " + task.getDescription() + "</p>");

    builder.append("<h2>CSS-Tasks</h2>");
    for(Task task: cssTasks)
      builder.append("<p>" + task.getId() + ": " + task.getDescription() + "</p>");

    builder.append(DIV_END + DIV_END + DIV_END);

    return builder.toString();
  }

}
