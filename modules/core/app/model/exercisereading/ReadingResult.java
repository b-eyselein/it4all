package model.exercisereading;

import java.util.List;

import model.exercise.Exercise;

public class ReadingResult<T extends Exercise> extends AbstractReadingResult {

  private List<T> read;

  public ReadingResult(String theJson, List<T> theRead) {
    super(theJson);
    read = theRead;
  }

  public List<T> getRead() {
    return read;
  }

  @Override
  public boolean isSuccess() {
    return true;
  }

}
