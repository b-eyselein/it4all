package model;

public enum QuestionType {
  
  // @formatter:off
  MULTIPLE                ("Multiple Choice",                 "Auswahl von mehreren (richtigen) Antworten"),
  SINGLE                  ("Single Choice",                   "Auswahl von einer einzigen (richtigen) Antworten"),
  FILLOUT_WITH_ORDER      ("Ausfüllfragen mit Reihenfolge",   "Ausfüllen von Textfelder in (richtiger) Reihenfolge"),
  FILLOUT_WITHOUT_ORDER   ("Ausfüllfragen ohne Reihenfolge",  "Ausfüllen von Textfelder ohne Beachtung jeglicher Reihenfolge");
  // @formatter:on

  private String name;
  private String description;
  
  private QuestionType(String theName, String theDescription) {
    name = theName;
    description = theDescription;
  }
  
  public String getDescription() {
    return description;
  }
  
  public String getName() {
    return name;
  }
  
}