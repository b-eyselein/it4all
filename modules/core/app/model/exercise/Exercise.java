package model.exercise;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.avaje.ebean.Model;

@MappedSuperclass
public abstract class Exercise extends Model {

  protected static final String DIV_END = "</div>";
  
  @Id
  public int id;

  public String title; // NOSONAR

  @Column(columnDefinition = "text")
  public String text;

  public Exercise(int theId) {
    id = theId;
  }

  public abstract String renderData();

}
