package model.programming;

import java.util.List;

import javax.persistence.MappedSuperclass;

import model.exercise.Exercise;

@MappedSuperclass
public abstract class ProgrammingExercise extends Exercise {

  public String declaration; // NOSONAR
  
  public String functionname; // NOSONAR

  public String sampleSolution; // NOSONAR

  public int inputcount; // NOSONAR

  public ProgrammingExercise(int theId) {
    super(theId);
  }

  public abstract List<ITestData> getFunctionTests();

  public int getInputcount() {
    return inputcount;
  }

  public abstract String getLanguage();

  public abstract String getTestdataValidationUrl();

  public abstract String getTestingUrl();
  
}
