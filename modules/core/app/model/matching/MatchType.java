package model.matching;

public enum MatchType {

  // @formatter:off
  FAILURE             ("glyphicon glyphicon-exclamation-sign"),
  SUCCESSFUL_MATCH    ("glyphicon glyphicon-ok"),
  UNSUCCESSFUL_MATCH  ("glyphicon glyphicon-question-sign"),
  ONLY_USER           ("glyphicon glyphicon-remove"),
  ONLY_SAMPLE         ("glyphicon glyphicon-minus");
  // @formatter:on

  private String glyphicon;

  private MatchType(String theGlyphicon) {
    glyphicon = theGlyphicon;
  }

  public String getGlyphicon() {
    return glyphicon;
  }

}
