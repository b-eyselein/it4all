package model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

import model.JsExercise.JsDataType;
import model.programming.TestData;
import model.programming.TestDataKey;

@Entity
public class JsTestData extends TestData {

  public static final Finder<TestDataKey, JsTestData> finder = new Finder<>(JsTestData.class);

  @ManyToOne
  @JoinColumn(name = "exercise_id", updatable = false, insertable = false)
  @JsonBackReference
  public JsExercise exercise;

  public JsTestData(TestDataKey theKey) {
    super(theKey);
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

}
