package model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.ebean.Finder;
import model.exercise.Exercise;
import model.uml.UmlClass;

@Entity
public class UmlExercise extends Exercise {

  private static final int OFFSET = 50;
  private static final int GAP = 200;

  public static final Finder<Integer, UmlExercise> finder = new Finder<>(UmlExercise.class);

  @Column(columnDefinition = "text")
  private String classSelText;

  @Column(columnDefinition = "text")
  private String diagDrawText;

  @Column(columnDefinition = "text")
  private String solution;

  // @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
  // public List<Mapping> mappings;

  public UmlExercise(int theId, String theTitle, String theAuthor, String theText, String theClassSelText,
      String theDiagDrawText, String theSolution) {
    super(theId, theTitle, theAuthor, theText);
    classSelText = theClassSelText;
    diagDrawText = theDiagDrawText;
    solution = theSolution;
  }

  @JsonIgnore
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

  @JsonIgnore
  public String getClassSelText() {
    return classSelText;
  }

  @JsonIgnore
  public String getDiagDrawText() {
    return diagDrawText;
  }

  @JsonIgnore
  public UmlSolution getSolution() {
    return UmlSolution.fromJson(solution);
  }

  public UmlExercise updateValues(int theId, String theTitle, String theAuthor, String theText, String theClassSelText,
      String theDiagDrawText, String theSolution) {
    super.updateValues(theId, theTitle, theAuthor, theText);
    classSelText = theClassSelText;
    diagDrawText = theDiagDrawText;
    solution = theSolution;
    return this;
  }

}