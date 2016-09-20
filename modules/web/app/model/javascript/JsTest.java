package model.javascript;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;

import model.javascript.JsExercise.JsDataType;

@Entity
public class JsTest extends Model implements ITestData {
  
  private static final String VALUES_SPLIT_CHAR = "#";
  
  public static Finder<JsTestKey, JsTest> finder = new Finder<>(JsTest.class);
  
  @EmbeddedId
  public JsTestKey key;
  
  @ManyToOne
  @JoinColumn(name = "exercise_id", updatable = false, insertable = false)
  @JsonBackReference
  public JsExercise exercise;
  
  @Column(columnDefinition = "text")
  public String inputs;
  
  public String output;
  
  public JsTest(JsTestKey theKey) {
    key = theKey;
  }
  
  @Override
  public JsExercise getExercise() {
    return exercise;
  }
  
  @Override
  public int getId() {
    return key.testId;
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
