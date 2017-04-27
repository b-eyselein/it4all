package model.exercise;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.avaje.ebean.Model;
import com.google.common.base.Splitter;

@MappedSuperclass
public abstract class Exercise extends Model {
  
  protected static Splitter splitter = Splitter.fixedLength(100).omitEmptyStrings();
  
  @Id
  public int id;

  public String title; // NOSONAR

  @Column(columnDefinition = "text")
  public String text;

  public Exercise(int theId) {
    id = theId;
  }

  public List<String> getText() {
    return splitter.splitToList(text);
  }
  
}
