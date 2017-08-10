package model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import io.ebean.Finder;
import model.exercise.Exercise;
import model.exercisereading.ExerciseReader;
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

  public WebExercise(int theId, String theTitle, String theAuthor, String theText, String theHtmlText,
      List<HtmlTask> theHtmlTasks, String theJsText, List<JsWebTask> theJsTasks) {
    super(theId, theTitle, theAuthor, theText);
    htmlText = theHtmlText;
    htmlTasks = theHtmlTasks;
    jsText = theJsText;
    jsTasks = theJsTasks;
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

  @Override
  public void updateValues(int theId, String theTitle, String theAuthor, String theText, JsonNode exerciseNode) {
    super.updateValues(theId, theTitle, theAuthor, theText);

    htmlText = ExerciseReader.readTextArray(exerciseNode.get("htmlText"), "");
    htmlTasks = ExerciseReader.readArray(exerciseNode.get("htmlTasks"), WebExerciseReader::readHtmlTask);

    jsText = ExerciseReader.readTextArray(exerciseNode.get("jsText"), "");
    jsTasks = ExerciseReader.readArray(exerciseNode.get("jsTasks"), WebExerciseReader::readJsTask);
  }

}
