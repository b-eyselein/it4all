package model;

public class Task {
  
  private String shortDesc;
  private int pts;
  
  public Task(String shortDescription, int points) {
    shortDesc = shortDescription;
    pts = points;
  }
  
  public String getShortDescription() {
    return shortDesc;
  }
  
  public int getPoints() {
    return pts;
  }
  
  public String toString() {
    if(pts == 1)
      return shortDesc + " (1 Punkt)";
    else
      return shortDesc + " (" + pts + " Punkte)";
  }
  
}
