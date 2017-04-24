package model.uml;

import java.util.List;

public class UmlClass {

  public enum UmlClassType {
    NORMAL, ABSTRACT, INTERFACE;
  }

  private UmlClassType classType;

  private String name;

  private List<String> attributes;

  private List<String> methods;

  public UmlClass() {
    // Dummy constructor for Json.fromJson(...)
  }

  public UmlClass(UmlClassType theClassType, String theName, List<String> theAttributes, List<String> theMethods) {
    classType = theClassType;
    name = theName;
    attributes = theAttributes;
    methods = theMethods;
  }

  public List<String> getAttributes() {
    return attributes;
  }

  public UmlClassType getClassType() {
    return classType;
  }

  public List<String> getMethods() {
    return methods;
  }

  public String getName() {
    return name;
  }

}
