package model.javascript;

import java.util.Arrays;
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
  
  public static final Finder<Integer, JsExercise> finder = new Finder<>(JsExercise.class);
  
  @Id
  public int id;
  
  public String title;// NOSONAR
  
  @Column(columnDefinition = "text")
  public String text;
  
  public String declaration; // NOSONAR
  
  public String functionname; // NOSONAR
  
  public String sampleSolution; // NOSONAR
  
  public String inputtypes; // NOSONAR
  
  public int inputcount; // NOSONAR
  
  @Enumerated(EnumType.STRING)
  public JsDataType returntype;
  
  @OneToMany(mappedBy = "exercise")
  @JsonManagedReference
  public List<JsTest> functionTests;
  
  public JsExercise(int theId) {
    id = theId;
  }
  
  public List<JsDataType> getInputTypes() {
    return Arrays.stream(inputtypes.split("#")).map(JsDataType::valueOf).collect(Collectors.toList());
  }
  
}
