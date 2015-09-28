package model.html;

import model.Exercise;
import model.Task;

public class HtmlExercise extends Exercise {

  public HtmlExercise(String text, String defaultSolution, Task... subtasks) {
    super(text, defaultSolution, subtasks);
  }
  
}
