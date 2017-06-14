package model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

import io.ebean.Model;

@Entity
public class ProgSolution extends Model {
  
  @EmbeddedId
  public ProgSolutionKey key;
  
  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "user_name", insertable = false, updatable = false)
  public ProgUser user;

  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "exercise_id", insertable = false, updatable = false)
  public ProgExercise exercise;

  @Column(columnDefinition = "text")
  public String sol;

  public ProgSolution(ProgSolutionKey theKey) {
    key = theKey;
  }

}
