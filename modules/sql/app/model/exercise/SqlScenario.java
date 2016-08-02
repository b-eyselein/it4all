package model.exercise;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.avaje.ebean.Model;

@Entity
public class SqlScenario extends Model {
  
  public static Finder<String, SqlScenario> finder = new Finder<>(SqlScenario.class);
  
  @Id
  public String shortName;

  public String longName;
  
  public String scriptFile;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "scenario")
  public List<SelectExercise> selects;
  
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "scenario")
  public List<UpdateExercise> updates;
  
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "scenario")
  public List<UpdateExercise> creates;

  public SqlExercise getExercise(String exerciseType, int exerciseId) {
    List<? extends SqlExercise> list = getExercisesByType(exerciseType);
    for(SqlExercise exercise: list)
      if(exercise.key.id == exerciseId)
        return exercise;
    return null;
  }

  public List<? extends SqlExercise> getExercisesByType(String type) {
    switch(type) {
    case "SELECT":
      return selects;
    case "UPDATE":
      return updates;
    case "CREATE":
      return creates;
    default:
      return Collections.emptyList();
    }
  }

  public List<String> getExerciseTypes() {
    return Arrays.asList("CREATE", "SELECT", "UPDATE");
  }

}
