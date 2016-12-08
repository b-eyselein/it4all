package model.exercise;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.avaje.ebean.Model;

@Entity
public class SqlScenario extends Model {

  public static final Finder<String, SqlScenario> finder = new Finder<>(SqlScenario.class);

  public static final int STEP = 10;

  @Id
  public String shortName;

  public String longName; // NOSONAR

  public String scriptFile; // NOSONAR

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "scenario")
  public List<SqlExercise> exercises;

  public SqlScenario(String theShortName) {
    shortName = theShortName;
  }

  public int getBorder(SqlExerciseType exType, int start) {
    return (Math.min(getExercisesByType(exType).size(), start + STEP) / STEP) * STEP;
  }

  public List<SqlExercise> getExercises(SqlExerciseType type, int start) {
    List<SqlExercise> ex = getExercisesByType(type);
    return ex.subList(Math.max(start, 0), Math.min(start + STEP, ex.size()));
  }

  public List<SqlExercise> getExercisesByType(SqlExerciseType type) {
    return exercises.parallelStream().filter(ex -> ex.key.exercisetype.equals(type)).collect(Collectors.toList());
  }

}
