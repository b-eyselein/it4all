package model;

import java.util.List;
import java.util.stream.Collectors;

public class UmlDiagramdrawing_Class {

  private String name;
  private List<String> methods;
  private List<String> attributes;

  public UmlDiagramdrawing_Class(String name, List<String> methods, List<String> attributes) {
    this.name = name;
    this.methods = methods;
    this.attributes = attributes;
  }

  public List<String> getAttributes() {
    return this.attributes;
  }

  public String getAttributesAsString() {
    return attributes.stream().sorted().collect(Collectors.joining("\n"));
    // String ret = "";
    // List<String> s = this.attributes;
    // Collections.sort(s);
    // for(Iterator iterator = s.iterator(); iterator.hasNext();) {
    // ret += (String) iterator.next() + "\n";
    // }
    // return ret;
  }

  public List<String> getMethods() {
    return this.methods;
  }

  public String getMethodsAsString() {
    return methods.stream().sorted().collect(Collectors.joining("\n"));
    // String ret = "";
    // List<String> s = this.methods;
    // Collections.sort(s);
    // for(Iterator<String> iterator = s.iterator(); iterator.hasNext();) {
    // ret += iterator.next() + "\n";
    // }
    // return ret;
  }

  public String getName() {
    return this.name;
  }

  public void setAttributes(List<String> attributes) {
    this.attributes = attributes;
  }

  public void setMethods(List<String> methods) {
    this.methods = methods;
  }

  public void setName(String name) {
    this.name = name;
  }

}
