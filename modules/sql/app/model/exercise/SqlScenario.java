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

  @Id
  public String shortName;

  public String longName;

  public String scriptFile;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "scenario")
  public List<SqlExercise> exercises;

  public SqlScenario(String theShortName) {
    shortName = theShortName;
  }

  public List<SqlExercise> getExercisesByType(SqlExerciseType type) {
    return exercises.parallelStream().filter(ex -> ex.key.exercisetype.equals(type)).collect(Collectors.toList());
  }

}
