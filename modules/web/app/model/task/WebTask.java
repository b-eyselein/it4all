package model.task;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.google.common.base.Splitter;

import io.ebean.Model;
import model.WebExercise;

@MappedSuperclass
public abstract class WebTask extends Model {

  protected static final Splitter SPLITTER = Splitter.fixedLength(100).omitEmptyStrings();

  @EmbeddedId
  public WebTaskKey key;

  @ManyToOne
  @JoinColumn(name = "exercise_id", insertable = false, updatable = false)
  @JsonBackReference
  public WebExercise exercise;

  @Column(columnDefinition = "text")
  public String text;

  @Column
  public String xpathQuery;

  public WebTask(WebTaskKey theKey) {
    key = theKey;
  }

  public List<String> getText() {
    return SPLITTER.splitToList(text);
  }

}
