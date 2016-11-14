package model.exercise;

public interface Exercise {

  public ExerciseIdentifier getExerciseIdentifier();

  public int getId();

  public abstract int getMaxPoints();

  public String getText();

  public String getTitle();

}
