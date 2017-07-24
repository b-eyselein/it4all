package model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.ebean.Finder;
import model.exercise.Exercise;
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
  
  public ProgExercise updateValues(int theId, String theTitle, String theAuthor, String theText, String theFunctionName,
      int theInputCount, List<ProgSample> theSamples, List<SampleTestData> theSampleTestData) {
    super.updateValues(theId, theTitle, theAuthor, theText);
    functionName = theFunctionName;
    inputCount = theInputCount;
    
    samples = theSamples;
    sampleTestData = theSampleTestData;
    return this;
  }
  
}
