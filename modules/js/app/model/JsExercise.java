package model;

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

import model.exercise.Exercise;

@Entity
public class JsExercise extends Model implements Exercise {
  
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
  
  @Override
  public String getExerciseIdentifier() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getId() {
    return id;
  }

  public List<JsDataType> getInputTypes() {
    return Arrays.stream(inputtypes.split("#")).map(JsDataType::valueOf).collect(Collectors.toList());
  }

  @Override
  public int getMaxPoints() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public String getText() {
    return text;
  }

  @Override
  public String getTitle() {
    return title;
  }
  
}
