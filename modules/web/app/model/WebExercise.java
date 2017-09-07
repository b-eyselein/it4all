package model;

import java.util.Arrays;
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
import model.exercise.Tag;
import model.task.HtmlTask;
import model.task.JsWebTask;
import play.twirl.api.Html;

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

  @JsonProperty("htmlText")
  public List<String> getHtmlTextForJson() {
    return SPLITTER.splitToList(htmlText);
  }
  
  @JsonProperty("jsText")
  public List<String> getJsTextForJson() {
    return SPLITTER.splitToList(jsText);
  }
  
  @Override
  public List<? extends Tag> getTags() {
    return Arrays.asList(WebTag.W_HTML, WebTag.W_JS);
  }

  @Override
  @JsonIgnore
  public Html renderRest() {
    return views.html.webAdmin.webExTableRest.render(this);
  }

}
