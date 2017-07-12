package model.exercise;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.ebean.Finder;

@Entity
public class SqlScenario extends Exercise {
  
  public static final Finder<Integer, SqlScenario> finder = new Finder<>(SqlScenario.class);
  
  public static final int STEP = 10;
  
  public String shortName;
  
  public String scriptFile;
  
  @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL)
  @JsonManagedReference
  public List<SqlExercise> exercises;
  
  public SqlScenario(int theId, String theTitle, String theAuthor, String theText, String theShortName,
      String theScriptFile, List<SqlExercise> theExercises) {
    super(theId, theTitle, theAuthor, theText);
    shortName = theShortName;
    scriptFile = theScriptFile;
    exercises = theExercises;
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
  
  @Override
  public String toString() {
    return "ID: " + id + "Titel: " + title + ", Name: " + shortName + ", Skriptdatei: " + scriptFile;
  }

  public SqlScenario updateValues(int id, String title, String author, String text, String theShortName,
      String theScriptfile, List<SqlExercise> theExercises) {
    super.updateValues(id, title, author, text);
    shortName = theShortName;
    scriptFile = theScriptfile;
    exercises = theExercises;
    return this;
  }
  
}
