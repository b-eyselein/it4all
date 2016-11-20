package model.feedback;

public enum YesNoMaybe {
  YES(1, "Ja", false), NO(0, "Nein", false), MAYBE(-1, "Keine Angabe", true);

  private String german;
  private int value;
  private boolean checked;

  private YesNoMaybe(int theValue, String theGerman, boolean isChecked) {
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

  public boolean isChecked() {
    return checked;
  }
}
