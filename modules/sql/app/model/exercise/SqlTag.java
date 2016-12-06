package model.exercise;

public enum SqlTag {
  JOIN("J", "Join"), DOUBLE_JOIN("2J", "Zweifacher Join"), TRIPLE_JOIN("3J", "Dreifacher Join"), ORDER_BY("O",
      "Reihenfolge"), GROUP_BY("G", "Gruppierung"), FUNCTION("F",
          "Funktion"), ALIAS("A", "Alias"), LIMIT("L", "Limitierung"), SUBSELECT("S", "Zweites Select innerhalb");
  
  private String buttonContent;
  private String title;
  
  private SqlTag(String theButtonContent, String theTitle) {
    buttonContent = theButtonContent;
    title = theTitle;
  }
  
  public String getButtonContent() {
    return "<span class=\"label label-default\" title=\"" + title + "\">" + buttonContent + "</span>";
  }
  
}
