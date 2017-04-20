package model.uml;

import java.util.List;

public class UmlClass {

  public String name;

  public List<String> attributes;

  public List<String> methods;

  public List<String> getAttributes() {
    return attributes;
  }

  public List<String> getMethod() {
    return methods;
  }

  public String getName() {
    return name;
  }

}
