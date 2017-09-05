package model.exercise;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Splitter;

import io.ebean.Model;
import model.WithId;
import play.twirl.api.Html;

@MappedSuperclass
public abstract class ExerciseCollection<E extends Exercise> extends Model implements WithId, Iterable<E> {
  
  protected static final Splitter SPLITTER = Splitter.fixedLength(100).omitEmptyStrings();
  
  protected static final Splitter NEW_LINE_SPLITTER = Splitter.on("\n");
  
  public static final String SPLIT_CHAR = "#";
  
  @Id
  protected int id;
  
  @JsonProperty(required = true)
  protected String title;
  
  @JsonProperty(required = true)
  protected String author;
  
  @Column(columnDefinition = "text")
  @JsonProperty(required = true)
  protected String text;
  
  @Enumerated(EnumType.STRING)
  protected ExerciseState state;
  
  public ExerciseCollection(int theId) {
    id = theId;
    title = author = text = "";
    state = ExerciseState.RESERVED;
  }
  
  public String getAuthor() {
    return author;
  }
  
  public abstract List<E> getExercises();
  
  @Override
  public int getId() {
    return id;
  }
  
  @JsonIgnore
  public int getMaxPoints() {
    return 0;
    // return exercises.parallelStream().mapToInt(q -> q.maxPoints).sum();
  }
  
  public List<String> getRestHeaders() {
    return Collections.emptyList();
  }
  
  public ExerciseState getState() {
    return state;
  }
  
  @JsonIgnore
  public String getText() {
    return text;
  }
  
  @JsonGetter("text")
  public List<String> getTextForJson() {
    return SPLITTER.splitToList(text);
  }
  
  public String getTitle() {
    return title;
  }
  
  @Override
  public Iterator<E> iterator() {
    return getExercises().iterator();
  }
  
  public Html renderRest() {
    return new Html("");
  }
  
  public void setAuthor(String theAuthor) {
    author = theAuthor;
  }
  
  public void setState(ExerciseState theState) {
    state = theState;
  }
  
  public void setText(String theText) {
    text = theText;
  }
  
  public void setTitle(String theTitle) {
    title = theTitle;
  }
}
