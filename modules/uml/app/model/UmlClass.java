package model;

import java.util.List;
import java.util.stream.Collectors;

public class UmlClass {

  private String name;
  private List<String> methods;
  private List<String> attributes;

  public UmlClass(String className, List<String> classAttributes, List<String> classMethods) {
    name = className;
    methods = classMethods;
    attributes = classAttributes;
  }

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
