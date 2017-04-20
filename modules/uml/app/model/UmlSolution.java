package model;

import java.util.List;
import java.util.stream.Collectors;

import model.uml.UmlAssociation;
import model.uml.UmlClass;
import model.uml.UmlImplementation;

public class UmlSolution {
  
  public List<UmlClass> classes; // NOSONAR
  
  public List<UmlAssociation> associations; // NOSONAR
  
  public List<UmlImplementation> implementations; // NOSONAR
  
  public List<String> getAllAttributes() {
    return classes.stream().map(UmlClass::getAttributes).flatMap(List::stream).collect(Collectors.toList());
  }

  public List<String> getAllMethods() {
    return classes.stream().map(UmlClass::getMethod).flatMap(List::stream).collect(Collectors.toList());
  }
  
  public List<UmlAssociation> getAssociations() {
    return associations;
  }
  
  public List<UmlClass> getClasses() {
    return classes;
  }
  
  public List<String> getClassNames() {
    return classes.stream().map(cl -> cl.name).collect(Collectors.toList());
  }
  
  public List<UmlImplementation> getImplementations() {
    return implementations;
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    
    builder.append("Klassen: " + getClassNames());
    builder.append("\n");
    builder.append("Implementationen: " + implementations);
    builder.append("\n");
    builder.append("Verbindungen: " + associations);
    
    return builder.toString();
  }
  
}
