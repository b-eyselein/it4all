package model;

import java.util.List;
import java.util.stream.Collectors;

public class UmlSolution {

  public List<UmlClass> classes; // NOSONAR

  public List<String> otherAttributes; // NOSONAR
  public List<String> otherMethods; // NOSONAR

  public List<UmlConnection> connections; // NOSONAR

  public List<String> getOtherAttributes() {
    return otherAttributes;
  }

  public List<UmlClass> getClasses() {
    return classes;
  }

  public List<String> getClassNames() {
    return classes.stream().map(UmlClass::getName).collect(Collectors.toList());
  }

  public List<UmlConnection> getConnections() {
    return connections;
  }

  public String getEntryAttributes(int index) {
    return otherAttributes.get(index);
  }

  public UmlClass getEntryClasses(int index) {
    return classes.get(index);
  }

  public String getEntryMethods(int index) {
    return otherMethods.get(index);
  }

  public List<String> getOtherMethods() {
    return otherMethods;
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    
    builder.append("Klassen: " + getClassNames());
    builder.append("\n");
    builder.append("Attribute: " + otherAttributes);
    builder.append("\n");
    builder.append("Methoden: " + otherMethods);
    builder.append("\n");
    builder.append("Verbindungen: " + connections);

    return builder.toString();
  }

}
