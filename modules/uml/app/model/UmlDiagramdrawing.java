package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import play.Logger;
import play.libs.Json;

public class UmlDiagramdrawing {

  private String title;

  List<UmlDiagramdrawing_Class> classes_user;
  List<UmlDiagramdrawing_Class> classes_user_c;

  List<UmlDiagramdrawing_Class> classes_solution_c;
  List<UmlDiagramdrawing_Class> classes_solution;

  List<ArrayList<UmlDiagramdrawing_Connection>> connections_user;
  List<ArrayList<UmlDiagramdrawing_Connection>> connections_solution;

  public UmlDiagramdrawing(String input) {
    setTitleExcercise("Foto");

    String musterSolution = "";
    try {
      musterSolution = String.join("\n",
          Files.readAllLines(Paths.get("modules/uml/conf/resources/musterSolution.json")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    JsonNode node = Json.parse(input);
    Logger.debug("input" + Json.prettyPrint(node));
    // Solution
    JsonNode node_c = Json.parse(musterSolution);
    this.classes_user = convertClassesToObject(node);
    this.classes_solution = convertClassesToObject(node_c);
    this.connections_user = convertConnectionsToObject(node);
    this.connections_solution = convertConnectionsToObject(node_c);
    ArrayList<UmlDiagramdrawing_Connection> list_c = new ArrayList<>();
    Logger.debug("" + this.classes_solution.size());
    list_c = getListConnectionsTypeSolution(4);
    for(Iterator<UmlDiagramdrawing_Connection> iterator = list_c.iterator(); iterator.hasNext();) {
      UmlDiagramdrawing_Connection ue = iterator.next();
      Logger.debug(ue.getType() + " " + ue.getStart() + " " + ue.getTarget());
    }
    makeSameClassesList(this.classes_user, this.classes_solution);
  }

  public ArrayList<UmlDiagramdrawing_Class> convertClassesToObject(JsonNode mainNode) {
    ArrayList<UmlDiagramdrawing_Class> classes = new ArrayList<>();
    JsonNode node_classes = mainNode.get("classes");
    if(node_classes.isArray()) {
      for(JsonNode objNode: node_classes) {
        String name = objNode.get("name").toString();
        String[] str_methods = objNode.get("methods").toString()
            .substring(1, objNode.get("methods").toString().length() - 1).split(",");
        ArrayList<String> al_methods = new ArrayList<String>(Arrays.asList(str_methods));

        String[] str_attributes = objNode.get("attributes").toString()
            .substring(1, objNode.get("attributes").toString().length() - 1).split(",");
        ArrayList<String> al_attributes = new ArrayList<String>(Arrays.asList(str_attributes));
        java.util.Collections.sort(al_attributes);
        java.util.Collections.sort(al_methods);
        classes.add(new UmlDiagramdrawing_Class(name, al_methods, al_attributes));
      }
    }
    return classes;
  }

  public ArrayList<ArrayList<UmlDiagramdrawing_Connection>> convertConnectionsToObject(JsonNode mainNode) {
    ArrayList<ArrayList<UmlDiagramdrawing_Connection>> connections = new ArrayList<>();
    JsonNode node_connectionType = mainNode.get("connections");
    String[] types = {"standard", "aggregation", "composition", "implementation", "generalization"};
    for(int i = 0; i < types.length; i++) {
      connections.add(convertConnectionTypeForConnections(node_connectionType.get(types[i]), types[i]));
    }
    return connections;
  }

  public ArrayList<UmlDiagramdrawing_Connection> convertConnectionTypeForConnections(JsonNode type, String str_type) {
    ArrayList<UmlDiagramdrawing_Connection> connections = new ArrayList<>();
    for(JsonNode objNode: type) {
      String kind = str_type;
      String start = objNode.get("start").toString();
      String target = objNode.get("target").toString();
      String mulstart = objNode.get("mulstart").toString();
      String multarget = objNode.get("multarget").toString();
      connections.add(new UmlDiagramdrawing_Connection(kind.substring(0, 1).toUpperCase() + kind.substring(1), start,
          target, mulstart, multarget));
      Logger.debug("connection " + kind.substring(0, 1).toUpperCase() + kind.substring(1) + " start: " + start
          + " target: " + target + " mulstart: " + mulstart + " multarget: " + multarget);
    }
    return connections;
  }

  public List<UmlDiagramdrawing_Class> getClasses_solution() {
    return this.classes_solution;
  }

  public List<UmlDiagramdrawing_Class> getClasses_solution_c() {
    return this.classes_solution_c;
  }

  public List<UmlDiagramdrawing_Class> getClasses_user() {
    return this.classes_user;
  }

  public List<UmlDiagramdrawing_Class> getClasses_user_c() {
    return this.classes_user_c;
  }

  public int getClassesLength(List<UmlDiagramdrawing_Class> input) {
    return input.size();
  }

  public List<ArrayList<UmlDiagramdrawing_Connection>> getConnections_solution() {
    return this.connections_solution;
  }

  public List<ArrayList<UmlDiagramdrawing_Connection>> getConnections_user() {
    return this.connections_user;
  }

  public int getConnectionsLength(ArrayList<UmlDiagramdrawing_Connection> input) {
    return input.size();
  }

  public ArrayList<UmlDiagramdrawing_Connection> getListConnectionsTypeSolution(int number) {
    ArrayList<UmlDiagramdrawing_Connection> ret = new ArrayList<>();
    ret = this.connections_solution.get(number);
    return ret;
  }

  public ArrayList<UmlDiagramdrawing_Connection> getListConnectionsTypeUser(int number) {
    ArrayList<UmlDiagramdrawing_Connection> ret = new ArrayList<>();
    ret = this.connections_user.get(number);
    return ret;
  }

  public int getMaxLength() {
    return Math.max(getClassesLength(getClasses_user()) - 1, getClassesLength(getClasses_solution()) - 1);
  }

  public String getTitleExercise() {
    return title;
  }

  public void makeSameClassesList(List<UmlDiagramdrawing_Class> classes_user,
      List<UmlDiagramdrawing_Class> classes_solution) {
    classes_user_c = new LinkedList<>();
    classes_solution_c = new LinkedList<>();

    for(Iterator<UmlDiagramdrawing_Class> iterator = classes_user.iterator(); iterator.hasNext();) {
      UmlDiagramdrawing_Class ue_user = iterator.next();
      for(Iterator<UmlDiagramdrawing_Class> iterator2 = classes_solution.iterator(); iterator2.hasNext();) {
        UmlDiagramdrawing_Class ue_solution = iterator2.next();
        if(ue_user.getName().equals(ue_solution.getName())) {
          classes_user_c.add(ue_user);
          classes_solution_c.add(ue_solution);
          iterator.remove();
          iterator2.remove();
        }
      }
    }
  }

  public void setTitleExcercise(String title) {
    this.title = title;
  }

  /*
   * public int getIndexInSolutionListforName(String classname_user){ int i =0;
   * ArrayList<UmlDiagramdrawing_Class> list = new ArrayList<>();
   * list=classes_solution; for (Iterator iterator = list.iterator();
   * iterator.hasNext();) { UmlDiagramdrawing_Class ue =
   * (UmlDiagramdrawing_Class) iterator.next();
   * if(ue.getName().equals(classname_user)){ return i; }else{ i++; } } return
   * -i; }
   */

}
