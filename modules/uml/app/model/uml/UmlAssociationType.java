package model.uml;

public enum UmlAssociationType {

  ASSOCIATION("Assoziation"), AGGREGATION("Aggregation"), COMPOSITION("Komposition");

  private String name;

  private UmlAssociationType(String theName) {
    name = theName;
  }

  public String getName() {
    return name;
  }

}
