package model;

import java.util.List;
import java.util.stream.Collectors;

public class UmlSolution {

  public List<UmlClass> classes; // NOSONAR

  public List<UmlAssociation> associations; // NOSONAR

  public List<UmlImplementation> implementations; // NOSONAR

  public List<UmlClass> getClasses() {
    return classes;
  }

  public List<String> getClassNames() {
    return classes.stream().map(UmlClass::getName).collect(Collectors.toList());
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
