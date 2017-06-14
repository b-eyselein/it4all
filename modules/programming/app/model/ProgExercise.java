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

  public ProgExercise(int theId) {
    super(theId);
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

}
