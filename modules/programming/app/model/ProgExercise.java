package model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonManagedReference;

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

  public static String getDeclaration(ProgLanguage language, String function, int inputcount) {
    // FIXME: move to ProgLanguage as abstract function!?!
    switch(language) {
    case JS:
      return "function " + function + "(" + getArguments(inputcount) + ") {\n  return 0;\n}";
    case PYTHON:
      return "def " + function + "(" + getArguments(inputcount) + "):\n  return 0";
    default:
      return "not defined...";
    }
  }

  private static String getArguments(int argCount) {
    // @formatter:off
    return Stream.iterate((int) 'a', n -> n + 1)
        .map(i -> (char) i.intValue())
        .map(String::valueOf)
        .limit(argCount)
        .collect(Collectors.joining(", "));
    // @formatter:on
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
