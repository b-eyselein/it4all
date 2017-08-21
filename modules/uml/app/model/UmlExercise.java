package model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;

import io.ebean.Finder;
import model.exercise.Exercise;
import model.exercisereading.ExerciseReader;
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
  @JsonIgnore
  private String solution;

  @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
  public List<Mapping> mappings;

  private String toIgnore;

  public UmlExercise(int theId, String theTitle, String theAuthor, String theText, String theClassSelText,
      String theDiagDrawText, String theSolution, List<Mapping> theMappings, List<String> theToIngore) {
    super(theId, theTitle, theAuthor, theText);
    classSelText = theClassSelText;
    diagDrawText = theDiagDrawText;
    solution = theSolution;
    mappings = theMappings;
    toIgnore = String.join(SPLIT_CHAR, theToIngore);
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

  @JsonGetter("ignore")
  public List<String> getIgnored() {
    return Arrays.asList(toIgnore.split("#"));
  }

  @JsonIgnore
  public UmlSolution getSolution() {
    return UmlSolution.fromJson(solution);
  }

  @JsonGetter("solution")
  public Object getSolutionAsJson() {
    // Setter only for generation of json schema...
    return solution;
  }

  @JsonSetter("solution")
  public void setSolution(Object theSolution) {
    // Getter only for generation of json schema...
    solution = theSolution.toString();
  }

  @Override
  public void updateValues(int theId, String theTitle, String theAuthor, String theText, JsonNode exerciseNode) {
    super.updateValues(theId, theTitle, theAuthor, theText);

    List<Mapping> theMappings = UmlExerciseReader.readArray(exerciseNode.get(StringConsts.MAPPINGS_NAME),
        Mapping::fromJson);
    List<String> ignore = ExerciseReader.parseJsonArrayNode(exerciseNode.get("ignore"));

    UmlExTextParser parser = new UmlExTextParser(theText, theMappings, ignore);
    classSelText = parser.parseTextForClassSel();
    diagDrawText = parser.parseTextForDiagDrawing();

    // Save solution as json in db
    solution = exerciseNode.get(StringConsts.SOLUTION_NAME).toString();
  }

}