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
  
  public static Finder<String, SqlScenario> finder = new Finder<>(SqlScenario.class);
  
  @Id
  public String shortName;

  public String longName;
  
  public String scriptFile;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "scenario")
  public List<SqlExercise> exercises;
  
  public SqlExercise getExercise(String exerciseType, int exerciseId) {
    for(SqlExercise exercise: exercises)
      if(exercise.key.id == exerciseId)
        return exercise;
    return null;
  }

  public List<SqlExercise> getExercisesByType(SqlExType exerciseType) {
    return exercises.stream().filter(exercise -> exercise.exType == exerciseType).collect(Collectors.toList());
  }
  
}
