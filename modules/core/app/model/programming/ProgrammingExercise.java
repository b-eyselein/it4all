package model.programming;

import model.IntExerciseIdentifier;

public interface ProgrammingExercise {
  
  public String getDeclaration();
  
  public IntExerciseIdentifier getExerciseIdentifier();
  
  public int getInputcount();
  
  public String getLanguage();

  public String getSampleSolution();
  
  public String getTestdataValidationUrl();

  public String getTestingUrl();
  
  public String getText();
  
}
