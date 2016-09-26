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

  public static Finder<String, SqlScenario> finder = new Finder<>(SqlScenario.class);

  @Id
  public String shortName;

  public String longName;

  public String scriptFile;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "scenario")
  public List<SqlExercise> exercises;

  public SqlScenario(String theShortName) {
    shortName = theShortName;
  }

  public List<? extends SqlExercise> getExercisesByType(SqlExerciseType type) {
    return exercises.parallelStream().filter(ex -> ex.querytype.equals(type)).collect(Collectors.toList());
  }

}
