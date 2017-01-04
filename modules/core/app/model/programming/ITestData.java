package model.programming;

import java.util.List;

import model.exercise.Exercise;

public interface ITestData<E extends Exercise> {

  public String buildToEvaluate();

  public E getExercise();

  public int getId();

  public List<String> getInput();

  public String getOutput();
}
