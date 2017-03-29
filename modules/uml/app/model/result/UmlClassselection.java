package model.result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;

import model.UmlExercise;
import play.libs.Json;

public class UmlClassselection extends UmlResult {

  private static final Path BASE_PATH = Paths.get("modules", "uml", "conf", "resources");
  private static final Path MUSTER_SOLUTION = Paths.get(BASE_PATH.toString(), "mustersolution_classSel.json");
  
  // c == Correct, f == False, m == Missing

  private List<String> correctClasses;
  private List<String> falseClasses;
  private List<String> missingClasses;

  private List<String> correctMethods;
  private List<String> wrongMethods;
  private List<String> missingMethods;

  private List<String> correctAttributes;
  private List<String> wrongAttributes;
  private List<String> missingAttributes;

  public UmlClassselection(UmlExercise exercise, JsonNode userJSON) {
    super(exercise, "Krankenhaus");
    String musterSolution = "";
    try {
      musterSolution = String.join("\n", Files.readAllLines(MUSTER_SOLUTION));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // Init
    List<String> userClasses = parseJSONArray(userJSON.get("classes"));
    List<String> userMethods = parseJSONArray(userJSON.get("methods"));
    List<String> userAttributes = parseJSONArray(userJSON.get("attributes"));

    JsonNode solutionJSON = Json.parse(musterSolution);
    List<String> solutionClasses = parseJSONArray(solutionJSON.get("classes"));
    List<String> solutionMethods = parseJSONArray(solutionJSON.get("methods"));
    List<String> solutionAttributes = parseJSONArray(solutionJSON.get("attributes"));

    // correct ==> Unification of two lists...
    correctClasses = unifyLists(userClasses, solutionClasses);
    correctMethods = unifyLists(userMethods, solutionMethods);
    correctAttributes = unifyLists(userAttributes, solutionAttributes);

    falseClasses = new LinkedList<>();
    for(String object: userClasses) {
      if(!solutionClasses.contains(object)) {
        falseClasses.add(object);
      }
    }

    wrongMethods = new LinkedList<>();
    for(String object: userMethods) {
      if(!solutionMethods.contains(object)) {
        wrongMethods.add(object);
      }
    }

    wrongAttributes = new LinkedList<>();
    for(String object: userAttributes) {
      if(!solutionAttributes.contains(object)) {
        wrongAttributes.add(object);
      }
    }

    // missing
    missingClasses = new LinkedList<>();
    for(String object: solutionClasses) {
      if(!userClasses.contains(object)) {
        missingClasses.add(object);
      }
    }

    missingMethods = new LinkedList<>();
    for(String object: solutionMethods) {
      if(!userMethods.contains(object)) {
        missingMethods.add(object);
      }
    }

    missingAttributes = new LinkedList<>();
    for(String object: solutionAttributes) {
      if(!userAttributes.contains(object)) {
        missingAttributes.add(object);
      }
    }
  }

  public static String toHtmlList(List<String> contents) {
    if(contents.isEmpty() || contents.contains("undefined"))
      return "--";

    return contents.stream().map(c -> "<li>" + c + "</li>").collect(Collectors.joining("", "<ul>", "</ul>"));
  }

  private static List<String> unifyLists(List<String> listOne, List<String> listTwo) {
    List<String> ret = new LinkedList<>();

    for(String contentOne: listOne)
      if(listTwo.contains(contentOne))
        ret.add(contentOne);

    return ret;
  }

  public String getCorrectAttributes() {
    return toHtmlList(correctAttributes);
  }

  public String getCorrectClasses() {
    return toHtmlList(correctClasses);
  }

  public String getCorrectMethods() {
    return toHtmlList(correctMethods);
  }

  public String getMissingAttributes() {
    return toHtmlList(missingAttributes);
  }

  public String getMissingClasses() {
    return toHtmlList(missingClasses);
  }

  public String getMissingMethods() {
    return toHtmlList(missingMethods);
  }

  public String getWrongAttributes() {
    return toHtmlList(wrongAttributes);
  }

  public String getWrongClasses() {
    return toHtmlList(falseClasses);
  }

  public String getWrongMethods() {
    return toHtmlList(wrongMethods);
  }

}
