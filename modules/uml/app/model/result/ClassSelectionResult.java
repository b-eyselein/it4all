package model.result;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;

import model.JsonWrapper;
import model.UmlClass;
import model.UmlExercise;
import model.UmlSolution;

public class ClassSelectionResult extends UmlResult {

  private List<UmlClass> correctClasses;
  private List<UmlClass> wrongClasses;
  private List<UmlClass> missingClasses;

  private List<String> correctMethods;
  private List<String> wrongMethods;
  private List<String> missingMethods;

  private List<String> correctAttributes;
  private List<String> wrongAttributes;
  private List<String> missingAttributes;

  public ClassSelectionResult(UmlExercise exercise, JsonNode userJSON) {
    super(exercise);

    UmlSolution musterSolution = exercise.getSolution();

    List<UmlClass> userClasses = JsonWrapper.parseJsonArrayNode(userJSON.get("classes")).stream()
        .map(name -> new UmlClass(name, Collections.emptyList(), Collections.emptyList())).collect(Collectors.toList());
    List<String> userMethods = JsonWrapper.parseJsonArrayNode(userJSON.get("otherMethods"));
    List<String> userAttributes = JsonWrapper.parseJsonArrayNode(userJSON.get("otherAttributes"));

    // correct ==> Unification of two lists...
    correctClasses = unifyLists(userClasses, musterSolution.getClasses());
    correctMethods = unifyLists(userMethods, musterSolution.getMethods());
    correctAttributes = unifyLists(userAttributes, musterSolution.getAttributes());

    wrongClasses = new LinkedList<>();
    for(UmlClass object: userClasses) {
      if(!musterSolution.getClasses().contains(object)) {
        wrongClasses.add(object);
      }
    }

    wrongMethods = new LinkedList<>();
    for(String object: userMethods) {
      if(!musterSolution.getMethods().contains(object)) {
        wrongMethods.add(object);
      }
    }

    wrongAttributes = new LinkedList<>();
    for(String object: userAttributes) {
      if(!musterSolution.getAttributes().contains(object)) {
        wrongAttributes.add(object);
      }
    }

    // missing
    missingClasses = new LinkedList<>();
    for(UmlClass object: musterSolution.getClasses()) {
      if(!userClasses.contains(object)) {
        missingClasses.add(object);
      }
    }

    missingMethods = new LinkedList<>();
    for(String object: musterSolution.getMethods()) {
      if(!userMethods.contains(object)) {
        missingMethods.add(object);
      }
    }

    missingAttributes = new LinkedList<>();
    for(String object: musterSolution.getAttributes()) {
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

  private static <T> List<T> unifyLists(List<T> listOne, List<T> listTwo) {
    List<T> ret = new LinkedList<>();

    for(T contentOne: listOne)
      if(listTwo.contains(contentOne))
        ret.add(contentOne);

    return ret;
  }

  public String getCorrectAttributes() {
    return toHtmlList(correctAttributes);
  }

  public String getCorrectClasses() {
    return toHtmlList(correctClasses.stream().map(UmlClass::getName).collect(Collectors.toList()));
  }

  public String getCorrectMethods() {
    return toHtmlList(correctMethods);
  }

  public String getMissingAttributes() {
    return toHtmlList(missingAttributes);
  }

  public String getMissingClasses() {
    return toHtmlList(missingClasses.stream().map(UmlClass::getName).collect(Collectors.toList()));
  }

  public String getMissingMethods() {
    return toHtmlList(missingMethods);
  }

  public String getWrongAttributes() {
    return toHtmlList(wrongAttributes);
  }

  public String getWrongClasses() {
    return toHtmlList(wrongClasses.stream().map(UmlClass::getName).collect(Collectors.toList()));
  }

  public String getWrongMethods() {
    return toHtmlList(wrongMethods);
  }

}
