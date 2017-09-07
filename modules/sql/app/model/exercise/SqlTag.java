package model.exercise;

public enum SqlTag implements Tag {
  // @formatter:off
  JOIN        ("J",   "Join"),
  DOUBLE_JOIN ("2J",  "Zweifacher Join"),
  TRIPLE_JOIN ("3J",  "Dreifacher Join"),
  ORDER_BY    ("O",   "Reihenfolge"),
  GROUP_BY    ("G",   "Gruppierung"),
  FUNCTION    ("F",   "Funktion"),
  ALIAS       ("A",   "Alias"),
  LIMIT       ("L",   "Limitierung"),
  SUBSELECT   ("S",   "Zweites Select innerhalb");
  // @formatter:on

  private final String buttonContent;
  private final String title;

  private SqlTag(String theButtonContent, String theTitle) {
    buttonContent = theButtonContent;
    title = theTitle;
  }

  @Override
  public String getButtonContent() {
    return buttonContent;
  }

  @Override
  public String getTitle() {
    return title;
  }

}
