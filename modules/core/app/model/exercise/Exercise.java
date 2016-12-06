package model.exercise;

import javax.persistence.MappedSuperclass;

import com.avaje.ebean.Model;

@MappedSuperclass
public abstract class Exercise extends Model {

  protected static final String DIV_END = "</div>";

  public String title; // NOSONAR

  public abstract int getId();

  public abstract int getMaxPoints();

  public abstract String getText();

  public String getTitle() {
    return title;
  }

  public abstract String renderData();

}
