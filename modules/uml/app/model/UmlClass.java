package model;

import java.util.List;
import java.util.stream.Collectors;

public class UmlClass {
  
  public String name;
  public List<String> methods;
  public List<String> attributes;
  
  public List<String> getAttributes() {
    return attributes;
  }
  
  public String getAttributesAsString() {
    return attributes.stream().sorted().collect(Collectors.joining("\n"));
  }
  
  public List<String> getMethods() {
    return methods;
  }
  
  public String getMethodsAsString() {
    return methods.stream().sorted().collect(Collectors.joining("\n"));
  }
  
  public String getName() {
    return name;
  }
  
}
