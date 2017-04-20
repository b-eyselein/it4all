package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.Column;
import javax.persistence.Entity;

import model.exercise.Exercise;
import model.uml.UmlClass;
import play.libs.Json;

@Entity
public class UmlExercise extends Exercise {
  
  private static final int OFFSET = 50;
  private static final int GAP = 200;
  
  private static final Path BASE_PATH = Paths.get("modules", "uml", "conf", "resources");
  private static final Path MUSTER_SOLUTION = Paths.get(BASE_PATH.toString(), "musterSolution.json");
  
  public static final com.avaje.ebean.Model.Finder<Integer, UmlExercise> finder = new com.avaje.ebean.Model.Finder<>(
      UmlExercise.class);
  
  @Column(columnDefinition = "text")
  public String classSelText;
  
  @Column(columnDefinition = "text")
  public String diagDrawText;
  
  @Column(columnDefinition = "text")
  public String diagDrawHelpText;
  
  public UmlExercise(int theId) {
    super(theId);
  }
  
  public String getClassesForDiagDrawingHelp() {
    List<UmlClass> classes = getSolution().getClasses();
    long sqrt = Math.round(Math.sqrt(classes.size()));

    return IntStream.range(0, classes.size()).mapToObj(i -> {
      UmlClass clazz = classes.get(i);
    // @formatter:off
      return "{\n" +
        "name: \"" + clazz.name + "\"," +
        "\ntype: \"" + /* TODO: type...*/ "CLASS" + "\",\n" +
        "attributes: [],\nmethods: [],\n" +
        "position: {x: " + ((i / sqrt) * GAP + OFFSET) + ", y: " + ((i % sqrt) * GAP + OFFSET) + "}\n" +
      "}";
    }
    // @formatter:on
    ).collect(Collectors.joining(",\n"));
  }
  
  public String getExTextForClassSel() {
    return classSelText;
  }
  
  public String getExTextForDiagDraw() {
    return diagDrawText;
  }
  
  public String getExTextForDiagDrawHelp() {
    return diagDrawHelpText;
  }
  
  public UmlSolution getSolution() {
    // FIXME: member of class!
    try {
      return Json.fromJson(Json.parse(String.join("\n", Files.readAllLines(MUSTER_SOLUTION))), UmlSolution.class);
    } catch (IOException e) {
      return null;
    }
  }
  
}