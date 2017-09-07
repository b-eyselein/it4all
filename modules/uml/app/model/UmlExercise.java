package model;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import io.ebean.Finder;
import io.ebean.annotation.DbJson;
import model.exercise.Exercise;
import model.uml.UmlClass;

@Entity
public class UmlExercise extends Exercise {

  private static final int OFFSET = 50;
  private static final int GAP = 200;

  public static final Finder<Integer, UmlExercise> finder = new Finder<>(UmlExercise.class);

  @Column(columnDefinition = "text")
  @JsonIgnore
  public String classSelText;

  @Column(columnDefinition = "text")
  @JsonIgnore
  public String diagDrawText;

  @Column(columnDefinition = "text")
  @JsonProperty(required = true)
  public String solution;

  @DbJson
  @JsonIgnore
  public Map<String, String> mappings;

  @DbJson
  public List<String> ignoreWords;

  public UmlExercise(int id) {
    super(id);
  }

  @JsonIgnore
  public String getClassesForDiagDrawingHelp() {
    final List<UmlClass> classes = getSolution().getClasses();
    final long sqrt = Math.round(Math.sqrt(classes.size()));

    return IntStream.range(0, classes.size()).mapToObj(i -> {
      final UmlClass clazz = classes.get(i);
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

  @JsonGetter("mappings")
  @JsonProperty(required = true)
  public Set<Map.Entry<String, String>> getMappingsForJson() {
    return mappings.entrySet();
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

}