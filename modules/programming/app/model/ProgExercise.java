package model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.JsonNode;

import io.ebean.Finder;
import model.exercise.Exercise;
import model.exercisereading.ExerciseReader;
import model.testdata.CommitedTestData;
import model.testdata.SampleTestData;

@Entity
public class ProgExercise extends Exercise {

  public static final Finder<Integer, ProgExercise> finder = new Finder<>(ProgExercise.class);

  private String functionName;
  private int inputCount;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "exercise")
  @JsonManagedReference
  private List<ProgSample> samples;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "exercise")
  @JsonManagedReference
  public List<SampleTestData> sampleTestData;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "exercise")
  @JsonIgnore
  public List<CommitedTestData> commitedTestData;

  public ProgExercise(int theId, String theTitle, String theAuthor, String theText, String theFunctionName,
      int theInputCount, List<ProgSample> theSamples, List<SampleTestData> theSampleTestData) {
    super(theId, theTitle, theAuthor, theText);
    functionName = theFunctionName;
    inputCount = theInputCount;

    samples = theSamples;
    sampleTestData = theSampleTestData;
  }

  public String getFunctionName() {
    return functionName;
  }

  public int getInputCount() {
    return inputCount;
  }

  public String getSampleSolution(AvailableLanguages language) {
    // FIXME: implement!
    return "not defined...";
  }

  @Override
  public void updateValues(int theId, String theTitle, String theAuthor, String theText, JsonNode exerciseNode) {
    super.updateValues(theId, theTitle, theAuthor, theText);

    functionName = exerciseNode.get("functionName").asText();
    inputCount = exerciseNode.get("inputCount").asInt();

    samples = ExerciseReader.readArray(exerciseNode.get(StringConsts.SAMPLES_NAME), ProgExerciseReader::readSample);
    sampleTestData = ExerciseReader.readArray(exerciseNode.get("sampleTestData"), ProgExerciseReader::readTest);
  }

}
