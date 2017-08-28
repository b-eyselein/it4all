package model.exercise;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import io.ebean.Finder;
import model.StringConsts;

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

  public SqlScenario(int theId, String theTitle, String theAuthor, String theText, String theShortName,
      String theScriptFile, List<SqlExercise> theExercises) {
    super(theId, theTitle, theAuthor, theText);
    shortName = theShortName;
    scriptFile = theScriptFile;
    exercises = theExercises;
  }

  @JsonIgnore
  public int getNumOfSites() {
    return exercises.size() / STEP + 1;
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

  public String getScriptFile() {
    return scriptFile;
  }

  public String getShortName() {
    return shortName;
  }

  @Override
  public String toString() {
    return "ID: " + id + "Titel: " + title + ", Name: " + shortName + ", Skriptdatei: " + scriptFile;
  }

  @Override
  public void updateValues(int theId, String theTitle, String theAuthor, String theText, JsonNode exerciseNode) {
    super.updateValues(theId, theTitle, theAuthor, theText);

    shortName = exerciseNode.get(StringConsts.SHORTNAME_NAME).asText();
    scriptFile = exerciseNode.get(StringConsts.SCRIPTFILE_NAME).asText();

    // JsonNode exesNode = exerciseNode.get(StringConsts.EXERCISES_NAME);
    // List<SqlExercise> exercises = readArray(exesNode,
    // delegateReader::readExercise);
  }

}
