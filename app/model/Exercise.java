package model;

import java.util.Arrays;
import java.util.List;

public abstract class Exercise {
  
  private String exerciseText;
  private String defaultSol;
  private List<Task> tasks;
  
  public Exercise(String text, String defaultSolution, Task... subtasks) {
    exerciseText = text;
    defaultSol = defaultSolution;
    tasks = Arrays.asList(subtasks);
  }
  
  public String getExerciseText() {
    return exerciseText;
  }
  
  public String getDefaultSolution() {
    return defaultSol;
  }
  
  public List<Task> getTasks() {
    return tasks;
  }
  
  public List<String> getExerciseTextInLines() {
    return Arrays.asList(exerciseText.split("\n"));
  }
  
}
