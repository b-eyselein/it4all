package model;

import java.util.List;
import java.util.stream.Collectors;

import model.uml.UmlAssociation;
import model.uml.UmlClass;
import model.uml.UmlImplementation;
import play.libs.Json;

public class UmlSolution {
  
  private List<UmlClass> classes;
  
  private List<UmlAssociation> associations;
  
  private List<UmlImplementation> implementations;
  
  public static UmlSolution fromJson(String json) {
    return Json.fromJson(Json.parse(json), UmlSolution.class);
  }
  
  public List<String> getAllAttributes() {
    return classes.stream().map(UmlClass::getAttributes).flatMap(List::stream).collect(Collectors.toList());
  }
  
  public List<String> getAllMethods() {
    return classes.stream().map(UmlClass::getMethods).flatMap(List::stream).collect(Collectors.toList());
  }
  
  public List<UmlAssociation> getAssociations() {
    return associations;
  }
  
  public List<UmlClass> getClasses() {
    return classes;
  }
  
  public List<UmlImplementation> getImplementations() {
    return implementations;
  }
  
  public void setAssociations(List<UmlAssociation> theAssociations) {
    associations = theAssociations;
  }
  
  public void setClasses(List<UmlClass> theClasses) {
    classes = theClasses;
  }
  
  public void setImplementations(List<UmlImplementation> theImplementations) {
    implementations = theImplementations;
  }
  
}
