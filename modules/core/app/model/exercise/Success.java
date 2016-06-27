package model.exercise;

public enum Success {

  NONE(0), PARTIALLY(1), COMPLETE(2);

  private int points;

  private Success(int thePoints) {
    points = thePoints;
  }

  public int getPoints() {
    return points;
  }
}