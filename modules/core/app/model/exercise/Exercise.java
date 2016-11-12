package model.exercise;

public interface Exercise {
  
  public String getExerciseIdentifier();
  
  public int getId();
  
  public abstract int getMaxPoints();
  
  public String getText();
  
  public String getTitle();
  
}
