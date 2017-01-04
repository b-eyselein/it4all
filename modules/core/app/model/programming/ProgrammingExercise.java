package model.programming;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import model.IntExerciseIdentifier;
import model.exercise.Exercise;

@MappedSuperclass
public abstract class ProgrammingExercise<I extends ITestData> extends Exercise {
  
  @Id
  public int id;
  
  @Column(columnDefinition = "text")
  public String text;
  
  public String declaration; // NOSONAR
  
  public String functionname; // NOSONAR
  
  public String sampleSolution; // NOSONAR
  
  public int inputcount; // NOSONAR
  
  public abstract IntExerciseIdentifier getExerciseIdentifier();
  
  public abstract List<I> getFunctionTests();
  
  @Override
  public int getId() {
    return id;
  }
  
  public int getInputcount() {
    return inputcount;
  }
  
  public abstract String getLanguage();
  
  public abstract String getTestdataValidationUrl();
  
  public abstract String getTestingUrl();
  
  @Override
  public String getText() {
    return text;
  }
  
}
