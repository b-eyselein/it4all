package model.result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import model.UmlClass;
import model.UmlConnection;
import model.UmlConnectionType;
import model.UmlExercise;
import play.Logger;
import play.libs.Json;

public class UmlDiagramdrawing extends UmlResult {

  private static final String[] CONNECTION_TYPES = {"standard", "aggregation", "composition", "implementation",
      "generalization"};

  private static final Path MUSTER_SOLUTION_PATH = Paths.get("modules/uml/conf/resources/mustersolution.json");

  List<UmlClass> userClasses;
  List<UmlClass> classes_user_c;

  List<UmlClass> classes_solution_c;
  List<UmlClass> solutionClasses;

  List<List<UmlConnection>> userConnections;
  List<List<UmlConnection>> solutionConnections;

  public UmlDiagramdrawing(UmlExercise exercise, JsonNode node) {
    super(exercise, "Krankenhaus");

    String musterSolution = "";
    try {
      musterSolution = String.join("\n", Files.readAllLines(MUSTER_SOLUTION_PATH));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // Solution
    Logger.debug("input" + Json.prettyPrint(node));
    userClasses = convertClassesToObject(node.get("classes"));
    userConnections = convertConnectionsToObject(node);

    JsonNode node_c = Json.parse(musterSolution);
    solutionClasses = convertClassesToObject(node_c.get("classes"));
    solutionConnections = convertConnectionsToObject(node_c);

    List<UmlConnection> list_c = new ArrayList<>();

    list_c = getListConnectionsTypeSolution(4);
    for(Iterator<UmlConnection> iterator = list_c.iterator(); iterator.hasNext();) {
      UmlConnection ue = iterator.next();
      Logger.debug(ue.getType() + " " + ue.getStart() + " " + ue.getTarget());
    }
    makeSameClassesList(userClasses, solutionClasses);
  }

  public List<UmlClass> convertClassesToObject(JsonNode classesNode) {
    List<UmlClass> classes = new ArrayList<>();

    for(JsonNode objNode: classesNode) {
      String name = objNode.get("name").asText();

      List<String> attributes = parseJSONArray(objNode.get("attributes"));
      Collections.sort(attributes);

      List<String> methods = parseJSONArray(objNode.get("methods"));
      Collections.sort(methods);

      classes.add(new UmlClass(name, attributes, methods));
    }

    return classes;
  }

  public List<List<UmlConnection>> convertConnectionsToObject(JsonNode mainNode) {
    List<List<UmlConnection>> connections = new ArrayList<>();
    JsonNode node_connectionType = mainNode.get("connections");

    for(String type: CONNECTION_TYPES)
      connections.add(convertConnectionTypeForConnections(node_connectionType.get(type),
          UmlConnectionType.valueOf(type.toUpperCase())));

    return connections;
  }

  public List<UmlConnection> convertConnectionTypeForConnections(JsonNode node, UmlConnectionType type) {
    ArrayList<UmlConnection> connections = new ArrayList<>();
    for(JsonNode objNode: node) {
      String start = objNode.get("start").asText();
      String target = objNode.get("target").asText();
      String mulstart = objNode.get("mulstart").asText();
      String multarget = objNode.get("multarget").asText();

      connections.add(new UmlConnection(type, start, target, mulstart, multarget));
      Logger.debug("connection " + type + " start: " + start + " target: " + target + " mulstart: " + mulstart
          + " multarget: " + multarget);
    }
    return connections;
  }

  public List<UmlClass> getClasses_solution() {
    return this.solutionClasses;
  }

  public List<UmlClass> getClasses_solution_c() {
    return this.classes_solution_c;
  }

  public List<UmlClass> getClasses_user() {
    return this.userClasses;
  }

  public List<UmlClass> getClasses_user_c() {
    return this.classes_user_c;
  }

  public int getClassesLength(List<UmlClass> input) {
    return input.size();
  }

  public List<List<UmlConnection>> getConnections_solution() {
    return solutionConnections;
  }

  public List<List<UmlConnection>> getConnections_user() {
    return userConnections;
  }

  public int getConnectionsLength(List<UmlConnection> input) {
    return input.size();
  }

  public List<UmlConnection> getListConnectionsTypeSolution(int number) {
    return solutionConnections.get(number);
  }

  public List<UmlConnection> getListConnectionsTypeUser(int number) {
    return userConnections.get(number);
  }

  public int getMaxLength() {
    return Math.max(getClassesLength(getClasses_user()) - 1, getClassesLength(getClasses_solution()) - 1);
  }

  public void makeSameClassesList(List<UmlClass> classes_user, List<UmlClass> classes_solution) {
    classes_user_c = new LinkedList<>();
    classes_solution_c = new LinkedList<>();

    for(Iterator<UmlClass> iterator = classes_user.iterator(); iterator.hasNext();) {
      UmlClass ue_user = iterator.next();
      for(Iterator<UmlClass> iterator2 = classes_solution.iterator(); iterator2.hasNext();) {
        UmlClass ue_solution = iterator2.next();
        if(ue_user.getName().equals(ue_solution.getName())) {
          classes_user_c.add(ue_user);
          classes_solution_c.add(ue_solution);
          iterator.remove();
          iterator2.remove();
        }
      }
    }
  }
}
