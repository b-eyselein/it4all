package model.feedback;

public enum Marks {
  VERY_GOOD(1, "Sehr gut", 2), GOOD(2, "Gut", 1), NEUTRAL(3, "Neutral", 1), BAD(4, "Schlecht", 1), VERY_BAD(5,
      "Sehr schlecht", 2);
  
  private int mark;
  private int width;
  private String german;
  
  private Marks(int theMark, String inGerman, int theWidth) {
    mark = theMark;
    german = inGerman;
    width = theWidth;
  }
  
  public String getGerman() {
    return german;
  }
  
  public int getMark() {
    return mark;
  }
  
  public int getWidth() {
    return width;
  }

}
