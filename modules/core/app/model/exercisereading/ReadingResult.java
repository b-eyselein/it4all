package model.exercisereading;

import java.util.List;

import model.exercise.Exercise;

public class ReadingResult<T extends Exercise> extends AbstractReadingResult<T> {

  private List<T> read;

  public ReadingResult(String theJson, String theJsonSchema, List<T> theRead) {
    super(theJson, theJsonSchema);
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
