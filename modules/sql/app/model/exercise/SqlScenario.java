package model.exercise;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class SqlScenario extends Exercise {
  
  public static final com.avaje.ebean.Model.Finder<Integer, SqlScenario> finder = new com.avaje.ebean.Model.Finder<>(
      SqlScenario.class);
  
  public static final int STEP = 10;
  
  public String shortName;
  
  public String scriptFile;
  
  @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL)
  @JsonManagedReference
  public List<SqlExercise> exercises;
  
  public SqlScenario(int theId) {
    super(theId);
  }
  
  public int getBorder(SqlExerciseType exType, int start) {
    return (Math.min(getExercisesByType(exType).size(), start + STEP) / STEP) * STEP;
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
  
}
