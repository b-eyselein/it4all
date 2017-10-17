package model.exercise;

import java.util.Collections;
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
import model.JsonReadable;
import play.twirl.api.Html;

@MappedSuperclass
public abstract class Exercise extends Model implements JsonReadable {

  protected static final Splitter SPLITTER = Splitter.fixedLength(100).omitEmptyStrings();

  protected static final Splitter NEW_LINE_SPLITTER = Splitter.on("\n");

  public static final String SPLIT_CHAR = "#";

  @Id
  public int id;

  @Column
  @JsonProperty(required = true)
  public String title;

  @Column
  @JsonProperty(required = true)
  public String author;

  @Column(columnDefinition = "text")
  @JsonProperty(required = true)
  public String text;

  @Column
  @Enumerated(EnumType.STRING)
  public ExerciseState state;

  public Exercise(int theId) {
    id = theId;
  }

  public String getAuthor() {
    return author;
  }

  @Override
  public int getId() {
    return id;
  }

  @JsonIgnore
  public List<? extends Tag> getTags() {
    return Collections.emptyList();
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

  public Html renderRest() {
    return new Html("");
  }

  public Html renderEditRest(boolean isCreation) {
    return new Html("");
  }

  @Override
  public void saveInDB() {
    save();
  }

  public void setAuthor(String theAuthor) {
    author = theAuthor;
  }

  public void setText(String theText) {
    text = theText;
  }

  public void setTitle(String theTitle) {
    title = theTitle;
  }

}
