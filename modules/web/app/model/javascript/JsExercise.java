package model.javascript;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class JsExercise extends Model {

  public enum JsDataType {
    BOOLEAN, NUMBER, STRING, SYMBOL, UNDEFINED, NULL, OBJECT;
  }

  public static Finder<Integer, JsExercise> finder = new Finder<>(JsExercise.class);
  
  @Id
  public int id;

  public String title;

  @Column(columnDefinition = "text")
  public String text;

  public String declaration;

  public String functionName;

  public String sampleSolution;

  // FIXME: inputTypes!
  public int inputCount;
  
  @Enumerated(EnumType.STRING)
  public JsDataType returntype;

  @OneToMany(mappedBy = "exercise")
  @JsonManagedReference
  public List<JsTest> functionTests;

  public String buildToEvaluate(List<String> input) {
    return functionName + "(" + String.join(", ", input.stream().map(i -> "\"" + i + "\"").collect(Collectors.toList()))
        + ")";
  }
  
}
