package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;

import play.libs.Json;

public class UmlSolution {

  private static final Path BASE_PATH = Paths.get("modules", "uml", "conf", "resources");
  private static final Path MUSTER_SOLUTION = Paths.get(BASE_PATH.toString(), "mustersolution_classSel.json");

  private List<UmlClass> classes;

  private List<String> otherMethods;
  private List<String> otherAttributes;

  public UmlSolution() {
    String musterSolution = "";
    try {
      musterSolution = String.join("\n", Files.readAllLines(MUSTER_SOLUTION));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    JsonNode solutionJSON = Json.parse(musterSolution);

    classes = JsonWrapper.parseJsonArrayNode(solutionJSON.get("classes")).stream()
        .map(name -> new UmlClass(name, Collections.emptyList(), Collections.emptyList())).collect(Collectors.toList());

    otherMethods = JsonWrapper.parseJsonArrayNode(solutionJSON.get("methods"));
    otherAttributes = JsonWrapper.parseJsonArrayNode(solutionJSON.get("attributes"));
  }

  public List<String> getAttributes() {
    return otherAttributes;
  }

  public List<UmlClass> getClasses() {
    return classes;
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

  public List<String> getMethods() {
    return otherMethods;
  }

}
