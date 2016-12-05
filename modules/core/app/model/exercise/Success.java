package model.exercise;

public enum Success {
  
  NONE(0, "danger"), PARTIALLY(1, "warning"), COMPLETE(2, "success");
  
  private int points;
  private String color;
  
  private Success(int thePoints, String theColor) {
    points = thePoints;
    color = theColor;
  }
  
  public String getColor() {
    return color;
  }
  
  public int getPoints() {
    return points;
  }
  
}