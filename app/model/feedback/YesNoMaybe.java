package model.feedback;

public enum YesNoMaybe {
  YES(3, 1, "Ja", false), MAYBE(2, 0, "Keine Angabe", true), NO(3, -1, "Nein", false);

  private String german;
  private int width;
  private int value;
  private boolean checked;

  private YesNoMaybe(int theWidth, int theValue, String theGerman, boolean isChecked) {
    width = theWidth;
    value = theValue;
    german = theGerman;
    checked = isChecked;
  }

  public String getGerman() {
    return german;
  }
  
  public int getValue() {
    return value;
  }

  public int getWidth() {
    return width;
  }

  public boolean isChecked() {
    return checked;
  }
}
