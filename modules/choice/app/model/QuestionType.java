package model;

public enum QuestionType {

  // @formatter:off
  MULTIPLE              ("checkbox",  "checkbox",   "Multiple Choice",                true,   "Auswahl von mehreren (richtigen) Antworten"),
  SINGLE                ("radio",     "radio",      "Single Choice",                  true,   "Auswahl von einer einzigen (richtigen) Antworten"),
  FILLOUT_WITH_ORDER    ("text",      "form-group", "Ausfüllfragen mit Reihenfolge",  false,  "Ausfüllen von Textfelder in (richtiger) Reihenfolge"),
  FILLOUT_WITHOUT_ORDER ("text",      "form-group", "Ausfüllfragen ohne Reihenfolge", true,   "Ausfüllen von Textfelder ohne Beachtung jeglicher Reihenfolge");
  // @formatter:on

  private String inputType;
  private String divClass;
  private String name;
  private boolean shuffleAnswers;
  private String description;

  private QuestionType(String theInputType, String theDivClass, String theName, boolean theShuffleAnswers,
      String theDescription) {
    inputType = theInputType;
    divClass = theDivClass;
    name = theName;
    shuffleAnswers = theShuffleAnswers;
    description = theDescription;
  }

  public String getDescription() {
    return description;
  }

  public String getDivClass() {
    return divClass;
  }

  public String getInputType() {
    return inputType;
  }

  public String getName() {
    return name;
  }

  public boolean shuffleAnswers() {
    return shuffleAnswers;
  }

}