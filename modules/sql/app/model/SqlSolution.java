package model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

import io.ebean.Finder;
import io.ebean.Model;
import model.exercise.SqlExercise;

@Entity
public class SqlSolution extends Model {

  public static final Finder<SqlSolutionKey, SqlSolution> finder = new Finder<>(SqlSolution.class);

  @Column(columnDefinition = "text")
  public String sol;

  public int points;

  @EmbeddedId
  public SqlSolutionKey key;

  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "user_name", insertable = false, updatable = false)
  public SqlUser user;

  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "scenario_id", referencedColumnName = "scenario_id", insertable = false, updatable = false)
  @JoinColumn(name = "exercise_id", referencedColumnName = "exercise_id", insertable = false, updatable = false)
  public SqlExercise exercise;

  public SqlSolution(SqlSolutionKey theKey) {
    key = theKey;
  }

}
