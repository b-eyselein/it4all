package model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import io.ebean.Finder;
import io.ebean.annotation.DbJson;
import model.exercise.Exercise;

@Entity
public class UmlExercise extends Exercise {
  
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
    return UmlSolution$.MODULE$.getClassesForDiagDrawingHelp(getSolution().classes());
  }
  
  @JsonGetter("mappings")
  @JsonProperty(required = true)
  public Set<Map.Entry<String, String>> getMappingsForJson() {
    return mappings.entrySet();
  }
  
  @JsonIgnore
  public UmlSolution getSolution() {
    return UmlSolution.fromJson(solution).get();
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