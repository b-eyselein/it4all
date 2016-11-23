package model.feedback;

public enum Mark {
  VERY_GOOD(1, "Sehr gut", false), GOOD(2, "Gut", false), NEUTRAL(3, "Neutral", false), BAD(4, "Schlecht",
      false), VERY_BAD(5, "Sehr schlecht", false), NO_MARK(-1, "Keine Angabe", true);
  
  private int myMark;
  private String german;
  private boolean checked;
  
  private Mark(int theMark, String inGerman, boolean isChecked) {
    myMark = theMark;
    german = inGerman;
    checked = isChecked;
  }
  
  public String getGerman() {
    return german;
  }
  
  public int getMark() {
    return myMark;
  }
  
  public boolean isChecked() {
    return checked;
  }
  
}
