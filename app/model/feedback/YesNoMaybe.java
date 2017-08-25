package model.feedback;

public enum YesNoMaybe {
  YES(1, "Ja"), NO(0, "Nein"), MAYBE(-1, "Keine Angabe");
  
  private String german;
  private int value;
  
  private YesNoMaybe(int theValue, String theGerman) {
    value = theValue;
    german = theGerman;
  }
  
  public String getGerman() {
    return german;
  }
  
  public int getValue() {
    return value;
  }
  
}
