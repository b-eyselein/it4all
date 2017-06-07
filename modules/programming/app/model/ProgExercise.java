package model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "exercise")
  @JsonManagedReference
  public List<SampleTestData> sampleTestData;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "exercise")
  @JsonManagedReference
  public List<CommitedTestData> commitedTestData;

  public ProgExercise(int theId) {
    super(theId);
  }

  private static String getArguments(int argCount) {
    return Stream.iterate((int) 'a', n -> n + 1).limit(argCount).map(i -> String.valueOf((char) i.intValue()))
        .collect(Collectors.joining(", "));
  }

  public String getDeclaration(ProgLanguage language) {
    // FIXME: move to ProgLanguage as abstract function!?!
    switch(language) {
    case JS:
      return "function " + functionName + "(" + getArguments(inputCount) + ") {\n  return 0;\n}";
    case PYTHON:
      return "def " + functionName + "(" + getArguments(inputCount) + "):\n  return 0";
    default:
      return "not defined...";
    }
  }

  public String getSampleSolution(ProgLanguage language) {
    switch(language) {
    case JS:
      return jsSample;
    case PYTHON:
      return pythonSample;
    default:
      return "not defined...";
    }
  }

}
