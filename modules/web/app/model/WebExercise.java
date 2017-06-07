package model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.ebean.Finder;
import model.exercise.Exercise;
import model.task.HtmlTask;
import model.task.JsWebTask;

@Entity
public class WebExercise extends Exercise {

  public static final Finder<Integer, WebExercise> finder = new Finder<>(WebExercise.class);

  @Column(columnDefinition = "text")
  public String htmlText;

  @Column(columnDefinition = "text")
  public String jsText;

  @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
  @JsonManagedReference
  public List<HtmlTask> htmlTasks;

  @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
  @JsonManagedReference
  public List<JsWebTask> jsTasks;

  @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
  @JsonIgnore
  public List<WebSolution> solutions;

  public WebExercise(int theId) {
    super(theId);
  }

  @JsonIgnore
  public static long withHtmlPart() {
    return finder.all().stream().filter(ex -> !ex.htmlTasks.isEmpty()).count();
  }

  @JsonIgnore
  public static long withJsPart() {
    return finder.all().stream().filter(ex -> !ex.jsTasks.isEmpty()).count();
  }

  public List<String> getHtmlText() {
    return SPLITTER.splitToList(htmlText);
  }

  public List<String> getJsText() {
    return SPLITTER.splitToList(jsText);
  }

}
