package model.exercise;

public enum Success {
  
  COMPLETE(2), PARTIALLY(1), NONE(0);

  private int points;

  private Success(int thePoints) {
    points = thePoints;
  }

  public int getPoints() {
    return points;
  }
}