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
  @JsonIgnore
  private String solution;
  
  @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
  private List<Mapping> mappings;
  
  private String toIgnore;
  
  public UmlExercise(int id) {
    super(id);
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
  
  public List<Mapping> getMappings() {
    return mappings;
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
  
  @JsonGetter("ignore")
  public List<String> getToIgnore() {
    return Arrays.asList(toIgnore.split("#"));
  }
  
  public void setClassSelText(String theClassSelText) {
    classSelText = theClassSelText;
  }
  
  public void setDiagDrawText(String theDiagDrawText) {
    diagDrawText = theDiagDrawText;
  }
  
  public void setMappings(List<Mapping> theMappings) {
    mappings = theMappings;
  }
  
  @JsonSetter("solution")
  public void setSolution(Object theSolution) {
    // Getter only for generation of json schema...
    solution = theSolution.toString();
  }
  
  public void setSolution(String theSolution) {
    solution = theSolution;
  }
  
  public void setToIgnore(String theToIgnore) {
    toIgnore = theToIgnore;
  }
  
}