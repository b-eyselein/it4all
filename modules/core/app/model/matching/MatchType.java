package model.matching;

public enum MatchType {
  
  SUCCESSFUL_MATCH("glyphicon glyphicon-ok"), UNSUCCESSFUL_MATCH("glyphicon glyphicon-question-sign"), ONLY_USER(
      "glyphicon glyphicon-remove"), ONLY_SAMPLE("glyphicon glyphicon-minus");
  
  private String glyphicon;
  
  private MatchType(String theGlyphicon) {
    glyphicon = theGlyphicon;
  }
  
  @Override
  public String toString() {
    return glyphicon;
  }
  
}
