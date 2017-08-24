package model.exercise;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import io.ebean.Finder;
import model.SqlExerciseReader;
import model.SqlSolution;
import model.StringConsts;
import model.exercisereading.ExerciseReader;

@Entity
public class SqlExercise extends Exercise {
  
  public static final String SAMPLE_JOIN_CHAR = "#";
  
  public static final Finder<Integer, SqlExercise> finder = new Finder<>(SqlExercise.class);
  
  @Enumerated(EnumType.STRING)
  @JsonProperty(required = true)
  public SqlExerciseType exerciseType;
  
  @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
  @JsonManagedReference
  public List<SqlSample> samples;
  
  @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
  @JsonIgnore
  public List<SqlSolution> solutions;
  
  @ManyToOne
  @JsonBackReference
  public SqlScenario scenario;
  
  @JsonProperty(required = true)
  public String tags;
  
  @JsonProperty(required = true)
  public String hint;
  
  public SqlExercise(int theId, String theTitle, String theAuthor, String theText, SqlExerciseType theExerciseType) {
    super(theId, theTitle, theAuthor, theText);
    exerciseType = theExerciseType;
  }
  
  @JsonIgnore
  public String getBadges() {
    return getTags().stream().map(SqlTag::getButtonContent).collect(Collectors.joining());
  }
  
  public List<SqlTag> getTags() {
    if(tags.isEmpty())
      return Collections.emptyList();
    
    return Arrays.stream(tags.split(SAMPLE_JOIN_CHAR)).map(SqlTag::valueOf).collect(Collectors.toList());
  }
  
  @Override
  public void updateValues(int theId, String theTitle, String theAuthor, String theText, JsonNode exerciseNode) {
    super.updateValues(theId, theTitle, theAuthor, theText);
    
    exerciseType = SqlExerciseType.valueOf(exerciseNode.get(StringConsts.EXERCISE_TYPE).asText());
    
    samples = ExerciseReader.readArray(exerciseNode.get(StringConsts.SAMPLES_NAME),
        SqlExerciseReader::readSampleSolution);
    
    hint = exerciseNode.get("hint").asText();
    tags = String.join(SqlExercise.SAMPLE_JOIN_CHAR, ExerciseReader.parseJsonArrayNode(exerciseNode.get("tags")));
  }
  
}
