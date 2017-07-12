package model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.ebean.Finder;
import model.exercise.Exercise;
import model.testdata.CommitedTestData;
import model.testdata.SampleTestData;

@Entity
public class ProgExercise extends Exercise {
  
  public static final Finder<Integer, ProgExercise> finder = new Finder<>(ProgExercise.class);
  
  public String functionName;
  
  public int inputCount;
  
  public String pythonSample;
  
  public String jsSample;
  
  public String javaSample;
  
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "exercise")
  @JsonManagedReference
  public List<SampleTestData> sampleTestData;
  
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "exercise")
  @JsonManagedReference
  public List<CommitedTestData> commitedTestData;
  
  public ProgExercise(int theId, String theTitle, String theAuthor, String theText, String theFunctionName,
      int theInputCount, String thePythonSample, String theJsSample, String theJavaSample) {
    super(theId, theTitle, theAuthor, theText);
    functionName = theFunctionName;
    inputCount = theId;
    pythonSample = thePythonSample;
    jsSample = theJsSample;
    javaSample = theJavaSample;
  }
  
  public String getSampleSolution(AvailableLanguages language) {
    switch(language) {
    case JAVA_8:
      return javaSample;
    case PYTHON_3:
      return pythonSample;
    default:
      return "not defined...";
    }
  }
  
  public ProgExercise updateValues(int theId, String theTitle, String theAuthor, String theText, String theFunctionName,
      int theInputCount, String thePythonSample, String theJsSample, String theJavaSample) {
    super.updateValues(theId, theTitle, theAuthor, theText);
    functionName = theFunctionName;
    inputCount = theInputCount;
    pythonSample = thePythonSample;
    jsSample = theJsSample;
    javaSample = theJavaSample;
    return this;
  }
  
}
