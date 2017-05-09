package model.question;

public enum Correctness {
  
  // @formatter:off
  CORRECT   ("Korrekt",   "glyphicon glyphicon-ok-sign",        "success"),
  OPTIONAL  ("Optional",  "glyphicon glyphicon-question-sign",  "warning"),
  WRONG     ("Falsch",    "glyphicon glyphicon-remove-sign",    "danger");
  // @formatter:on
  
  private String title;
  private String bsGlyphicon;
  private String bsButtonColor;
  
  private Correctness(String theTitle, String theBSGlyphicon, String theBSButtonColor) {
    title = theTitle;
    bsGlyphicon = theBSGlyphicon;
    bsButtonColor = theBSButtonColor;
  }
  
  public String getBSButtonColor() {
    return bsButtonColor;
  }
  
  public String getBSGlyphicon() {
    return bsGlyphicon;
  }
  
  public String getTitle() {
    return title;
  }
  
}