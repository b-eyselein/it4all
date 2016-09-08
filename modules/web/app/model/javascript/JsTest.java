package model.javascript;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;

import model.javascript.JsExercise.JsDataType;

@Entity
public class JsTest extends Model implements ITestData {
  
  private static final String VALUES_SPLIT_CHAR = "#";
  
  @Id
  public int id;
  
  @ManyToOne
  @JsonBackReference
  public JsExercise exercise;
  
  @Column(columnDefinition = "text")
  public String inputs;
  
  @Column(columnDefinition = "text")
  public String datatypes;
  
  public String output;
  
  @Override
  public JsExercise getExercise() {
    return exercise;
  }
  
  @Override
  public int getId() {
    return id;
  }
  
  @Override
  public List<String> getInput() {
    String[] inputArray = inputs.split(VALUES_SPLIT_CHAR);
    List<JsDataType> inputTypes = exercise.getInputTypes();
    
    List<String> input = new ArrayList<>(inputArray.length);
    for(int i = 0; i < inputArray.length; i++) {
      String toAdd = inputArray[i];
      if(inputTypes.get(i) == JsDataType.STRING)
        toAdd = "\"" + toAdd + "\"";
      input.add(toAdd);
    }
    
    return input;
  }
  
  @Override
  public String getOutput() {
    return output;
  }
  
}
