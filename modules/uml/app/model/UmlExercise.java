package model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.Column;
import javax.persistence.Entity;

import model.exercise.Exercise;
import model.uml.UmlClass;

@Entity
public class UmlExercise extends Exercise {

  private static final int OFFSET = 50;
  private static final int GAP = 200;

  public static final com.avaje.ebean.Model.Finder<Integer, UmlExercise> finder = new com.avaje.ebean.Model.Finder<>(
      UmlExercise.class);

  @Column(columnDefinition = "text")
  public String classSelText;

  @Column(columnDefinition = "text")
  public String diagDrawText;

  @Column(columnDefinition = "text")
  public String solution;

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
        "name: \"" + clazz.getName() + "\"," +
        "\nclassType: \"" + clazz.getClassType() + "\",\n" +
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

  public UmlSolution getSolution() {
    return UmlSolution.fromJson(solution);
  }

  @Override
  public void saveInDB() {
    save();
  }

}