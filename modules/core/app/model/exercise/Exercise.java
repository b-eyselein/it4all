package model.exercise;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.avaje.ebean.Model;
import com.google.common.base.Splitter;

import model.WithId;

@MappedSuperclass
public abstract class Exercise extends Model implements WithId {

  protected static final Splitter SPLITTER = Splitter.fixedLength(100).omitEmptyStrings();
  protected static final Splitter NEW_LINE_SPLITTER = Splitter.on("\n");

  @Id
  public int id;

  public String title;

  public String author;

  @Column(columnDefinition = "text")
  public String text;

  public Exercise(int theId) {
    id = theId;
  }

  @Override
  public int getId() {
    return id;
  }
  
  public List<String> getText() {
    return SPLITTER.splitToList(text);
  }

}
