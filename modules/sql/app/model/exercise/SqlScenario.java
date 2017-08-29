package model.exercise;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.ebean.Finder;

@Entity
public class SqlScenario extends ExerciseCollection<SqlExercise> {

  public static final Finder<Integer, SqlScenario> finder = new Finder<>(SqlScenario.class);

  public static final int STEP = 10;

  @JsonProperty(required = true)
  private String shortName;

  @JsonProperty(required = true)
  private String scriptFile;

  @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL)
  @JsonManagedReference
  private List<SqlExercise> exercises;

  public SqlScenario(int id) {
    super(id);
  }

  public int getBorder(SqlExerciseType exType, int start) {
    return (Math.min(getExercisesByType(exType).size(), start + STEP) / STEP) * STEP;
  }

  @Override
  public List<SqlExercise> getExercises() {
    return exercises;
  }

  public List<SqlExercise> getExercises(final SqlExerciseType type, final int start) {
    final List<SqlExercise> ex = getExercisesByType(type);
    return ex.subList(Math.max(start, 0), Math.min(start + STEP, ex.size()));
  }

  public List<SqlExercise> getExercisesByType(SqlExerciseType type) {
    return exercises.parallelStream().filter(ex -> ex.exerciseType.equals(type)).collect(Collectors.toList());
  }

  @JsonIgnore
  public String getImageUrl() {
    return shortName + ".png";
  }

  @JsonIgnore
  public int getNumOfSites() {
    return exercises.size() / STEP + 1;
  }

  public String getScriptFile() {
    return scriptFile;
  }

  public String getShortName() {
    return shortName;
  }

  public void setExercises(List<SqlExercise> theExercises) {
    exercises = theExercises;
  }

  public void setScriptFile(String theScriptFile) {
    scriptFile = theScriptFile;
  }

  public void setShortName(String theShortName) {
    shortName = theShortName;
  }

}
