package model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class WebSolution extends Model {

  public static final Finder<WebSolutionKey, WebSolution> finder = new Finder<>(WebSolution.class);

  @EmbeddedId
  public WebSolutionKey key;

  @Column(columnDefinition = "text")
  public String solution;

  public int points; // NOSONAR

  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "user_name", insertable = false, updatable = false)
  public WebUser user;

  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "exercise_id", insertable = false, updatable = false)
  public WebExercise exercise;

  public WebSolution(WebSolutionKey theKey) {
    key = theKey;
  }

}
