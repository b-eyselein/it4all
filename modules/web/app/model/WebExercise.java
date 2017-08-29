package model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.ebean.Finder;
import model.exercise.Exercise;
import model.task.HtmlTask;
import model.task.JsWebTask;

@Entity
public class WebExercise extends Exercise {
  
  public static final Finder<Integer, WebExercise> finder = new Finder<>(WebExercise.class);
  
  @Column(columnDefinition = "text")
  private String htmlText;
  
  @Column(columnDefinition = "text")
  private String jsText;
  
  @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
  @JsonManagedReference
  private List<HtmlTask> htmlTasks;
  
  @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
  @JsonManagedReference
  private List<JsWebTask> jsTasks;
  
  @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
  @JsonIgnore
  public List<WebSolution> solutions;
  
  public WebExercise(int id) {
    super(id);
  }
  
  @JsonIgnore
  public static long withHtmlPart() {
    return finder.all().stream().filter(ex -> !ex.htmlTasks.isEmpty()).count();
  }
  
  @JsonIgnore
  public static long withJsPart() {
    return finder.all().stream().filter(ex -> !ex.jsTasks.isEmpty()).count();
  }
  
  public String getHtmlText() {
    return htmlText;
  }
  
  @JsonProperty("htmlText")
  public List<String> getHtmlTextForJson() {
    return SPLITTER.splitToList(htmlText);
  }
  
  public String getJsText() {
    return jsText;
  }
  
  @JsonProperty("jsText")
  public List<String> getJsTextForJson() {
    return SPLITTER.splitToList(jsText);
  }
  
  public void setHtmlText(String theHtmlText) {
    htmlText = theHtmlText;
  }
  
  public void setHtmlTasks(List<HtmlTask> theHtmlTasks) {
    htmlTasks = theHtmlTasks;
  }
  
  public void setJsText(String theJsText) {
    jsText = theJsText;
  }
  
  public void setJsTasks(List<JsWebTask> theJsTasks) {
    jsTasks = theJsTasks;
  }
  
  public List<HtmlTask> getHtmlTasks() {
    return htmlTasks;
  }
  
  public List<JsWebTask> getJsTasks() {
    return jsTasks;
  }
  
}
