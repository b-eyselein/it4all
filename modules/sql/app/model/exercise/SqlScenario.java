package model.exercise;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.avaje.ebean.Model;

import model.exercise.SqlExercise.SqlExType;

@Entity
public class SqlScenario extends Model {
  
  public static Finder<Integer, SqlScenario> finder = new Finder<>(SqlScenario.class);
  
  @Id
  public String shortName;
  
  public String longName;
  
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "scenario")
  public List<SqlExercise> exercises;
  
  public List<SqlExercise> getExercises(SqlExType exerciseType) {
    return exercises.stream().filter(exercise -> exercise.exType == exerciseType).collect(Collectors.toList());
  }
  
}
