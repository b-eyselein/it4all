package model.uml;

public enum UmlClassType {

  CLASS("Klasse"), ABSTRACT("Abstrakte Klasse"), INTERFACE("Interface");

  private String name;

  private UmlClassType(String theName) {
    name = theName;
  }

  public String getName() {
    return name;
  }

}
