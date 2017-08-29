package model.exercise;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Splitter;

import io.ebean.Model;
import model.WithId;

@MappedSuperclass
public abstract class Exercise extends Model implements WithId {
  
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
  
  public void setTitle(String theTitle) {
    title = theTitle;
  }
  
  public void setAuthor(String theAuthor) {
    author = theAuthor;
  }
  
  public void setText(String theText) {
    text = theText;
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
  
}
