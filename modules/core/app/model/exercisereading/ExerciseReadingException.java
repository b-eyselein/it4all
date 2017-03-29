package model.exercisereading;

public class ExerciseReadingException extends Exception {

  private static final long serialVersionUID = -7009414355643795071L;

  public ExerciseReadingException(String msg) {
    super(msg);
  }

  public ExerciseReadingException(String msg, Throwable cause) {
    super(msg, cause);
  }

}
